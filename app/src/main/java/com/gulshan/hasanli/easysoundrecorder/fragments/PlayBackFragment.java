package com.gulshan.hasanli.easysoundrecorder.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.support.v4.app.DialogFragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gulshan.hasanli.easysoundrecorder.R;
import com.gulshan.hasanli.easysoundrecorder.models.RecordingItems;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayBackFragment extends DialogFragment {

   private static final String LOG_TAG = "PlayBackFragment";
   private static final String ARG_ITEM = "recording_item";

   private FloatingActionButton fab_play = null;

   private Handler handler = new Handler();
   private MediaPlayer mediaPlayer =null;
   private RecordingItems recordingItems = null;

   private TextView fileNameTextView = null;
   private TextView currentProgressTextVIew = null;
   private TextView fullLengthTextView = null;

   private  SeekBar seekBar = null;

   //store whether or not mediaPlayer playing or not
    private boolean isPlaying = false;

    //stores minutes and second of record
    long minutes = 0;
    long seconds = 0;

    public PlayBackFragment newInstance(RecordingItems items) {
        PlayBackFragment playBackFragment = new PlayBackFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_ITEM, items);
        playBackFragment.setArguments(bundle);

        return playBackFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recordingItems = getArguments().getParcelable(ARG_ITEM);

        Log.i(LOG_TAG, String.valueOf(recordingItems.getLength()));

        long itemDuration = recordingItems.getLength();

        minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(minutes);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View  view = getActivity().getLayoutInflater().inflate(R.layout.fragment_play_back, null);

        fileNameTextView = (TextView)view.findViewById(R.id.file_name_text);
        currentProgressTextVIew = (TextView)view.findViewById(R.id.current_progress_text);
        fullLengthTextView = (TextView)view.findViewById(R.id.full_length_text);
        fab_play = (FloatingActionButton)view.findViewById(R.id.fab_play);

        ColorFilter colorFilter = new LightingColorFilter(getResources().getColor(R.color.colorPrimary),
                                  getResources().getColor(R.color.colorPrimary));


        seekBar = (SeekBar)view.findViewById(R.id.seekbar);
        seekBar.getProgressDrawable().setColorFilter(colorFilter);
        seekBar.getThumb().setColorFilter(colorFilter);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser) {

                    Log.i(LOG_TAG, "onProgressChanged");
                    mediaPlayer.seekTo(progress);
                    handler.removeCallbacks(runnable);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(progress);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(progress)  - TimeUnit.MILLISECONDS.toSeconds(minutes);

                    currentProgressTextVIew.setText(String.format("%02d:%02d", minutes, seconds));

                    updateSeekBar();

                } else if (mediaPlayer == null && fromUser) {

                    prepareMediaPlayer(progress);
                    updateSeekBar();

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if(mediaPlayer != null) {

                    Log.i(LOG_TAG, "onStartTrackingTouch");
                    handler.removeCallbacks(runnable);

                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer != null) {
                    Log.i(LOG_TAG, "onStopTrackingTouch");
                    handler.removeCallbacks(runnable);
                    mediaPlayer.seekTo(seekBar.getProgress());

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())  - TimeUnit.MILLISECONDS.toSeconds(minutes);

                    currentProgressTextVIew.setText(String.format("%02d:%02d", minutes, seconds));
                    updateSeekBar();

                }
            }
        });


        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onPlay(isPlaying);
                isPlaying = !isPlaying;

            }
        });

        fileNameTextView.setText(recordingItems.getFileName());
        fullLengthTextView.setText(String.format("%02d:%02d", minutes, seconds));

        builder.setView(view);

        //request dialog without title

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return builder.create();
    }

    //updating SeekBar
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            int currentPosition  = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(currentPosition);

            long minutes = TimeUnit.MILLISECONDS.toMinutes(currentPosition);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(minutes);

            currentProgressTextVIew.setText(String.format("%02d:%02d", minutes, seconds));

            updateSeekBar();

        }
    };

    public void updateSeekBar() {

        handler.postDelayed(runnable, 1000);

    }


    private void prepareMediaPlayer(int progress) {

        //set mediaPlayer to start from middle

        mediaPlayer = new MediaPlayer();

        try {

            mediaPlayer.setDataSource(recordingItems.getFilePath());
            mediaPlayer.prepare();
            mediaPlayer.seekTo(progress);
            seekBar.setMax(mediaPlayer.getDuration());

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i(LOG_TAG, "setOnCompletionListener");
                    stopPlaying();
                }
            });

        } catch (Exception e) {

            Log.i(LOG_TAG, "prepareMediaPlayer failed");
            e.printStackTrace();

        }

        //keep screen on while playing
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);

        //disable buttons;
        AlertDialog alertDialog = (AlertDialog)getDialog();
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEUTRAL).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);

    }

    @Override
    public void onPause() {
        super.onPause();

        if(mediaPlayer != null) {

            stopPlaying();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mediaPlayer != null) {

            stopPlaying();

        }

    }

    //Play start/stop

    private void onPlay(boolean isPlaying) {

        if(!isPlaying) {

            if(mediaPlayer == null) {

                //if mediaPlayer does not start at all
                startPlaying();

            } else {

                //play where you left it
                resumePlaying();

            }

        } else  {

            pausePlaying();

        }

    }

    private void startPlaying() {
        //start playing
        fab_play.setImageResource(R.drawable.ic_media_pause);
        mediaPlayer = new MediaPlayer();

        try {

            mediaPlayer.setDataSource(recordingItems.getFilePath());
            mediaPlayer.prepare();
            seekBar.setMax(mediaPlayer.getDuration());

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

        } catch (Exception e) {

            Log.i(LOG_TAG, "start failed");
            e.printStackTrace();

        }


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });

        updateSeekBar();

        //keep screen on while playing
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }


    private void stopPlaying() {

            Log.i(LOG_TAG, "stopPlaying");
            fab_play.setImageResource(R.drawable.ic_media_play);
            handler.removeCallbacks(runnable);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            //allow screen to turn off
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            isPlaying =! isPlaying;

            currentProgressTextVIew.setText(fileNameTextView.getText());
            seekBar.setProgress(seekBar.getMax());

    }

    private void resumePlaying() {

        fab_play.setImageResource(R.drawable.ic_media_pause);
        handler.removeCallbacks(runnable);
        mediaPlayer.start();
        updateSeekBar();

    }

    private void pausePlaying() {

        fab_play.setImageResource(R.drawable.ic_media_play);
        handler.removeCallbacks(runnable);
        mediaPlayer.pause();

    }

}
