package com.yatik.statussaver.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yatik.statussaver.Adapters.DownloadsAdapter;
import com.yatik.statussaver.Others.CommonClass;
import com.yatik.statussaver.Others.Data;
import com.yatik.statussaver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DownloadsFragment extends Fragment {

    private final Handler handler = new Handler();
    private final List<Data> filesList = new ArrayList<>();
    TextView noFilesText;
    SwipeRefreshLayout swipeRefreshLayout;
    private DownloadsAdapter downloadsAdapter;
    private RecyclerView downloadsRecycler;

    public DownloadsFragment() {
        //Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_downloads, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this::extractFiles);

        noFilesText = view.findViewById(R.id.no_saved_files_text);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), CommonClass.SPAN_COUNT);
        downloadsRecycler = view.findViewById(R.id.downloadsRecyclerView);
        downloadsRecycler.setHasFixedSize(true);
        downloadsRecycler.setLayoutManager(layoutManager);

        extractFiles();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void extractFiles() {
        new Thread(() -> {
            // a potentially time consuming task
            if (!CommonClass.SAVED_FOLDER_FILE.exists()) {
                CommonClass.SAVED_FOLDER_FILE.mkdir();
            }
            File[] allFiles = CommonClass.SAVED_FOLDER_FILE.listFiles();
            assert allFiles != null;
            Arrays.sort(allFiles, (o1, o2) -> {
                if (o1.lastModified() > o2.lastModified()) {
                    return -1;
                } else if (o2.lastModified() > o1.lastModified()) {
                    return 1;
                }
                return 0;
            });
            filesList.clear();

            if (allFiles.length > 0) {
                for (File file : allFiles) {
                    Data filesData = new Data(file, file.getName(), file.getAbsolutePath());
                    filesList.add(filesData);
                }
                handler.post(() -> {
                    downloadsAdapter = new DownloadsAdapter(filesList);
                    downloadsRecycler.setAdapter(downloadsAdapter);
                    downloadsAdapter.notifyDataSetChanged();
                });
            } else {
                handler.post(() -> {
                    if (filesList.size() <= 0) {
                        noFilesText.setVisibility(View.VISIBLE);
                    } else {
                        noFilesText.setVisibility(View.GONE);
                    }
                    downloadsAdapter = new DownloadsAdapter(filesList);
                    downloadsRecycler.setAdapter(downloadsAdapter);
                    downloadsAdapter.notifyDataSetChanged();
                });
            }
            swipeRefreshLayout.setRefreshing(false);
        }).start();

    }


}