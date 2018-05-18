package com.gulshan.hasanli.easysoundrecorder.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gulshan.hasanli.easysoundrecorder.R;

public class RecordingsViewHolder extends RecyclerView.ViewHolder {

    protected TextView fileNameTextView;
    protected TextView fileLengthTextView;
    protected TextView fileDateTextView;
    protected LinearLayout linearLayout;

    public RecordingsViewHolder(View itemView) {
        super(itemView);

        fileNameTextView = (TextView)itemView.findViewById(R.id.file_name_text);
        fileLengthTextView = (TextView)itemView.findViewById(R.id.file_length_text);
        fileDateTextView = (TextView)itemView.findViewById(R.id.file_date_text);
        linearLayout = itemView.findViewById(R.id.linearLayout);

    }
}