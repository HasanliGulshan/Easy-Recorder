package com.gulshan.hasanli.easysoundrecorder.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.gulshan.hasanli.easysoundrecorder.R;
import com.gulshan.hasanli.easysoundrecorder.activities.MainActivity;
import com.gulshan.hasanli.easysoundrecorder.data.DatabaseHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RecordingService extends Service{

    private static final String LOG_TAG = "RecordingService";

    private String fileName = null;
    private String filePath = null;

    private MediaRecorder mediaRecorder = null;

    private DatabaseHelper databaseHelper;

    private long startingTimeMill = 0;
    private long elapsedMillis = 0;
    private int elapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat timerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer timer = null;
    private TimerTask incrementTimerTask = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.i(LOG_TAG, "recordingservice");
        databaseHelper = new DatabaseHelper(getApplicationContext());

    }

    public interface OnTimerChangedListener {

        void onTimerChanged(int seconds);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("start","recordingservice");
        startRecording();
        return START_STICKY;

    }

    public void startRecording() {

        setFileNameAndPath();
        setUpMediaRecorder();

        try{

            mediaRecorder.prepare();
            mediaRecorder.start(); //started recording

            startingTimeMill = System.currentTimeMillis();

        }catch (Exception e) {

            Log.e(LOG_TAG, "prepare failed");
            e.printStackTrace();

        }
    }

    public void setFileNameAndPath() {

        int count = 0;
        File file = null;

        do{
            count++;

            fileName = getString(R.string.default_file_name) + "_" + (databaseHelper.getCount() + count) + ".mp4";
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            filePath += "/SoundRecorder/" + fileName;

            file = new File(filePath);

        }while (file.exists() && !file.isDirectory());

    }

    public void setUpMediaRecorder() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setAudioChannels(1);

    }

    public void stopRecording() {

        mediaRecorder.stop(); //recording stopped
        Toast.makeText(this, R.string.toast_recording_finished + filePath, Toast.LENGTH_SHORT).show();
        mediaRecorder.release();//can not reused
        elapsedMillis = System.currentTimeMillis() - startingTimeMill;

        if(incrementTimerTask != null) {

            incrementTimerTask.cancel();
            incrementTimerTask = null;

        }

        mediaRecorder = null;

        try{

           databaseHelper.addRecording(fileName, filePath, elapsedMillis);
           Log.i(LOG_TAG, fileName + filePath);

        }catch (Exception e) {

            Log.e(LOG_TAG, "exception", e);

        }

    }

    private void startTimer() {

        timer = new Timer();
        incrementTimerTask = new TimerTask() {
            @Override
            public void run() {

                elapsedSeconds++;

                if(onTimerChangedListener != null) {

                    onTimerChangedListener.onTimerChanged(elapsedSeconds);

                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, createNotification());

                }

            }
        };

        timer.scheduleAtFixedRate(incrementTimerTask, 1000, 1000);

    }

    @Override
    public void onDestroy() {

        if(mediaRecorder != null) {

            stopRecording();

        }

        super.onDestroy();
    }

    public Notification createNotification() {

        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                                       .setSmallIcon(R.drawable.ic_mic_white_36dp)
                                       .setContentTitle(getString(R.string.notification))
                                       .setContentText(timerFormat.format(elapsedSeconds * 1000))
                                       .setOngoing(true);

        builder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
                new Intent[]{new Intent(getApplicationContext(), MainActivity.class)}, 0));

        return builder.build();

    }

}
