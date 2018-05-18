package com.gulshan.hasanli.easysoundrecorder.fragments;

import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.gulshan.hasanli.easysoundrecorder.R;
import com.gulshan.hasanli.easysoundrecorder.service.RecordingService;

import java.io.File;

public class RecordFragment extends Fragment {

    //Recording controls;
    private FloatingActionButton btnRecord = null;
    private Button btnPause = null;

    private TextView recordingText;
    private int recordCount = 0;

    View recordView;

    //fragment parameters
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();

    private int position;

    private boolean startRecording = true;
    private boolean pauseRecording = true;

    private Chronometer chronometer = null;
    private long timeWhenPaused = 0; //store time when paused

    public RecordFragment() {
    }

    public static RecordFragment newInstance(int position) {
        RecordFragment recordFragment = new RecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        recordFragment.setArguments(bundle);

        return recordFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();

        if(bundle != null) {

            position = bundle.getInt(ARG_POSITION);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        recordView = inflater.inflate(R.layout.fragment_record, container, false);

        btnRecord = (FloatingActionButton)recordView.findViewById(R.id.btn_record);
        btnPause = (Button)recordView.findViewById(R.id.btn_pause);

        recordingText = (TextView)recordView.findViewById(R.id.recording_status_text);

        chronometer = (Chronometer)recordView.findViewById(R.id.chronometer);


        btnPause.setVisibility(View.GONE);//hide button before recording

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("I am here", "recordBtn");
                startRecording(startRecording);
                startRecording =! startRecording;
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pauseRecording(pauseRecording);
                pauseRecording =! pauseRecording;

            }
        });

        return recordView;
    }

    //start/stop Recording

    public void startRecording(boolean start) {

        Intent intent = new Intent(getActivity(), RecordingService.class);

        if(start) {
            //start record
            btnRecord.setImageResource(R.drawable.ic_media_stop);
            Toast.makeText(getActivity(), R.string.toast_recording_started, Toast.LENGTH_SHORT).show();

            File folder = new File(Environment.getExternalStorageDirectory() +"/SoundRecorder");
            if(!folder.exists()) {
                //if folder is not exists then create one
                folder.mkdir();

            }

            //start chronometer
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if(recordCount == 0) {

                        recordingText.setText(getString(R.string.record_in_progress) + ".");

                    } else if(recordCount == 1) {

                        recordingText.setText(getString(R.string.record_in_progress) + "..");

                    } else if(recordCount == 2) {

                        recordingText.setText(getString(R.string.record_in_progress) + "...");
                        recordCount = -1;

                    }

                    recordCount++;
                }

            });

            //start RecordingService
            getActivity().startService(intent);
            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            recordCount++;

            Log.i("start", "first");
        } else {

            Log.i("start", "second");
            //stop service
            getActivity().stopService(intent);
            btnRecord.setImageResource(R.drawable.ic_mic_white_36dp);

            timeWhenPaused = 0;
            //allow screen off
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());

            recordingText.setText(getString(R.string.record_prompt));
        }

    }

    public void pauseRecording(boolean pause) {
        if(pause) {

           //pause recording
           btnPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_play,0,0,0);
           recordingText.setText(getString(R.string.resume_recording_button).toUpperCase());
           timeWhenPaused = chronometer.getBase() - SystemClock.elapsedRealtime();
           chronometer.stop();

        } else {

           //resume recording
            btnPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_media_pause,0,0,0);
            recordingText.setText(getString(R.string.pause_recording_button).toUpperCase());
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            chronometer.start();

        }

    }
}
