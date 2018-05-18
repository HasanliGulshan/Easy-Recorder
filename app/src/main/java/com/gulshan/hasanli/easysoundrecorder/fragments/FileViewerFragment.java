package com.gulshan.hasanli.easysoundrecorder.fragments;

import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gulshan.hasanli.easysoundrecorder.R;
import com.gulshan.hasanli.easysoundrecorder.adapters.FileViewerAdapter;

public class FileViewerFragment extends Fragment{

    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = "FileViewerFragment";

    private int position = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {

            position = getArguments().getInt(ARG_POSITION);

        }

    }

    private FileViewerAdapter fileViewerAdapter;

    public static FileViewerFragment newInstance(int position) {

        FileViewerFragment fileViewerFragment = new FileViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        fileViewerFragment.setArguments(bundle);

        return fileViewerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_viewer,container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //new to oldest order
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration((new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL)));

        fileViewerAdapter = new FileViewerAdapter(getActivity(), linearLayoutManager);
        recyclerView.setAdapter(fileViewerAdapter);

        return view;
    }

    FileObserver fileObserver = new FileObserver(android.os.Environment.getExternalStorageState().toString()
                                + "/SoundRecorder") {

        //set up a file observer to watch over sd card

        @Override
        public void onEvent(int event, @Nullable String path) {

            if(event == FileObserver.DELETE) {

                //user delete recording out of app

                String filePath = android.os.Environment.getExternalStorageDirectory().toString() + "/SoundRecorder" + path + "]";

                Log.d(LOG_TAG, "File deleted" + filePath);

                fileViewerAdapter.removeOutOfApp(filePath);

            }

        }
    };

}
