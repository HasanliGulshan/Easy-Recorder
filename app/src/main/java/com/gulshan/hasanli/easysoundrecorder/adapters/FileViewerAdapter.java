package com.gulshan.hasanli.easysoundrecorder.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gulshan.hasanli.easysoundrecorder.R;
import com.gulshan.hasanli.easysoundrecorder.data.DatabaseHelper;
import com.gulshan.hasanli.easysoundrecorder.fragments.PlayBackFragment;
import com.gulshan.hasanli.easysoundrecorder.interfaces.OnDatabaseChangedListener;
import com.gulshan.hasanli.easysoundrecorder.models.RecordingItems;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FileViewerAdapter extends RecyclerView.Adapter<RecordingsViewHolder> implements OnDatabaseChangedListener {

    private static final String LOG_TAG = "FileViewerAdapter";

    private DatabaseHelper databaseHelper;
    private RecordingItems recordingItems;
    private Context mcontext;
    private LinearLayoutManager mLinearLayoutManager;

    public FileViewerAdapter(Context context, LinearLayoutManager linearLayoutManager) {
       super();

       mcontext = context;
       databaseHelper = new DatabaseHelper(mcontext);
       databaseHelper.setOnDatabaseChangedListener(this);
       mLinearLayoutManager = linearLayoutManager;

    }

    @NonNull
    @Override
    public RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_viewer_items, parent,false);

            mcontext = parent.getContext();

            return new RecordingsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecordingsViewHolder holder, int position) {

        recordingItems = getItem(position);
        long itemDuration = recordingItems.getLength();

        final long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MILLISECONDS.toSeconds(minutes);

        Log.i(LOG_TAG, recordingItems.getFileName());

        holder.fileNameTextView.setText(recordingItems.getFileName());
        holder.fileLengthTextView.setText(String.format("%02d:%02d", minutes, seconds));

        holder.fileDateTextView.setText(
                DateUtils.formatDateTime(
                        mcontext,
                        recordingItems.getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        );

        //set OnClick to open PlayBackFragment
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    PlayBackFragment playBackFragment = new PlayBackFragment().newInstance(getItem(holder.getLayoutPosition()));

                    FragmentTransaction fragmentTransaction =
                                  ((FragmentActivity) mcontext)
                                        .getSupportFragmentManager()
                                        .beginTransaction();

                    playBackFragment.show(fragmentTransaction, "dialog_playback");

                }catch (Exception e) {

                    Log.i(LOG_TAG, "failed opening PlayBackFragment");
                    e.printStackTrace();

                }
            }
        });


        //set OnLongClickListener
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ArrayList<String> options = new ArrayList<>();
                options.add(mcontext.getString(R.string.dialog_file_share));
                options.add(mcontext.getString(R.string.dialog_file_rename));
                options.add(mcontext.getString(R.string.dialog_file_delete));

                final CharSequence[] functions = options.toArray(new CharSequence[options.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                builder.setTitle(mcontext.getString(R.string.dialog_title_options));
                builder.setItems(functions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0) {

                            shareFileDialog(holder.getLayoutPosition());

                        } else if(which == 1) {

                            renameFileDialog(holder.getLayoutPosition());

                        } else if (which == 2) {

                            deleteFileDialog(holder.getLayoutPosition());

                        }

                    }
                });

                builder.setCancelable(true);

                builder.setNegativeButton(R.string.dialog_action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

                    }
                });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();

                return false;
            }
        });

    }

    public RecordingItems getItem(int position) {

        return databaseHelper.getItemAt(position);

    }

    @Override
    public int getItemCount() {
        return databaseHelper.getCount();
    }



    @Override
    public void onNewDatabaseEntryAdded() {

        //item add to the top
        notifyItemInserted(getItemCount() - 1);
        mLinearLayoutManager.scrollToPosition(getItemCount() - 1);

    }

    @Override
    public void onDatabaseEntryRenamed() {

    }

    public void remove(int position) {
        //this will remove item from recyclerView, storage and database

        File file = new File(getItem(position).getFilePath());

        file.delete();

        Toast.makeText(
                mcontext,
                String.format(
                        mcontext.getString(R.string.toast_file_delete),
                        getItem(position).getFileName()
                ),
                Toast.LENGTH_SHORT
        ).show();

        databaseHelper.removeItemWithId(getItem(position).getId());
        notifyItemRemoved(position);

    }

    public void rename(int position, String name) {

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        filePath += "/SoundRecorder" + name;
        File file = new File(filePath);

        if (file.exists() && !file.isDirectory()) {

            //fie name is not unique

            Toast.makeText(mcontext,
                    mcontext.getString(R.string.toast_file_exists),
                    Toast.LENGTH_SHORT).show();

        } else {

            //file is unique
            File oldFilePath = new File(getItem(position).getFilePath());
            oldFilePath.renameTo(file);
            databaseHelper.renameItem(getItem(position), name, filePath);
            notifyItemChanged(position);
        }
    }

    public void shareFileDialog(int position) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(getItem(position).getFilePath())));
        shareIntent.setType("audio/mp4");
        mcontext.startActivity(Intent.createChooser(shareIntent, mcontext.getText(R.string.send_to)));
    }

    public void renameFileDialog(final int position) {

        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(mcontext);

        LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
        View view = layoutInflater.inflate(R.layout.rename, null);

        final EditText renameEditText = (EditText)view.findViewById(R.id.new_name_edit_text);

        renameFileBuilder.setTitle(mcontext.getString(R.string.dialog_file_rename));
        renameFileBuilder.setCancelable(true);

        renameFileBuilder.setPositiveButton(mcontext.getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String value = renameEditText.getText().toString().trim() + ".mp4";
                            rename(position, value);

                        } catch (Exception e) {
                            Log.e(LOG_TAG, "exception", e);
                        }

                        dialog.cancel();
                    }
                });

        renameFileBuilder.setNegativeButton(mcontext.getString(R.string.dialog_action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        renameFileBuilder.setView(view);
        AlertDialog alertDialog = renameFileBuilder.create();

        alertDialog.show();
    }

    public void deleteFileDialog(final int position) {

      AlertDialog.Builder deleteFileBuilder = new AlertDialog.Builder(mcontext);
      deleteFileBuilder.setTitle(mcontext.getString(R.string.dialog_title_delete));
      deleteFileBuilder.setMessage(mcontext.getString(R.string.dialog_text_delete));
      deleteFileBuilder.setCancelable(true);

      deleteFileBuilder.setPositiveButton(mcontext.getString(R.string.dialog_action_yes), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

            try {

                //try to delete
                remove(position);

            } catch (Exception e) {

                Log.i(LOG_TAG, "exception" , e);

            }

            dialog.cancel();

          }
      });

      deleteFileBuilder.setNegativeButton(mcontext.getString(R.string.dialog_action_no), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

              dialog.cancel();

          }
      });

      AlertDialog alertDialog = deleteFileBuilder.create();
      alertDialog.show();

    }

    public void removeOutOfApp(String filePath) {
        //user deletes a saved recording out of the application through another application
    }

}
