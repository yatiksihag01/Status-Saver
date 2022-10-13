package com.yatik.statussaver.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yatik.statussaver.Adapters.ImageAdapter;
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
public class ImageFragment extends Fragment {

    View noFilesFound;
    private ImageAdapter imageAdapter;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<Object> statusList;
    private final List<Data> imageList = new ArrayList<>();
    private final  List<Object> imageStatusList = new ArrayList<>();
    private final Handler handler = new Handler();

    public ImageFragment(){
        //Required empty constructor
    }


    public ImageFragment(List<Object> statusList) {
        this.statusList = statusList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                new Thread(() -> {
                    getListAboveQ(CommonClass.wa_status_uri);
                    handler.post(this::getImageData);
                }).start();
            } else {
                getImageData();
            }
        });

        noFilesFound = view.findViewById(R.id.no_files_found_img);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), CommonClass.SPAN_COUNT);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        getImageData();
    }


    private void getImageData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && CommonClass.FOLDER_BELOW_Q.exists()){
             extractImagesBelowQ();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && CommonClass.FOLDER_ABOVE_Q.exists()){
            extractImagesAboveQ();
        } else {
            Toast.makeText(getContext(), "WhatsApp Is Not Installed On Your Device", Toast.LENGTH_LONG).show();
            noFilesView();
            swipeRefreshLayout.setRefreshing(false);

        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void extractImagesAboveQ() {
        imageStatusList.clear();
        new Thread(() -> {
            // a potentially time consuming task
            //TODO: Sort
            if (statusList.size() > 0){
                for (int i = 0; i < statusList.size(); i++){
                    String docId = DocumentsContract.getDocumentId(Uri.parse(String.valueOf(statusList.get(i))));
                    File file = new File(docId);
                    new Data(file, file.getName(), file.getAbsolutePath());
                    if (Data.isImage){
                        imageStatusList.add(statusList.get(i));
                    }
                }
                handler.post(() -> {
                    if (imageStatusList.size() <= 0) {
                        noFilesView();
                    } else {
                        noFilesFound.setVisibility(View.GONE);
                    }
                    imageAdapter = new ImageAdapter(imageList, imageStatusList);
                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                });
            } else {
                handler.post(() -> {
                    if (statusList.size() <= 0) {
                        noFilesView();
                    } else {
                        noFilesFound.setVisibility(View.GONE);
                    }
                    imageAdapter = new ImageAdapter(imageList, imageStatusList);
                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                });
            }
            swipeRefreshLayout.setRefreshing(false);
        }).start();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void extractImagesBelowQ(){
        new Thread(() -> {
            // a potentially time consuming task
            File[] allFiles = CommonClass.FOLDER_BELOW_Q.listFiles();
            assert allFiles != null;
            Arrays.sort(allFiles, (o1, o2) -> {
                if (o1.lastModified() > o2.lastModified()){
                    return -1;
                } else if (o2.lastModified() > o1.lastModified()){
                    return 1;
                }
                return 0;
            });
            imageList.clear();

            if (allFiles.length > 0) {
                for (File file : allFiles) {
                    Log.v("file", String.valueOf(file));
                    Data imageData = new Data(file, file.getName(), file.getAbsolutePath());
                    if (Data.isImage) {
                        imageList.add(imageData);
                    }
                }
                handler.post(() -> {
                    if (imageList.size() <= 0) {
                        noFilesView();
                    } else {
                        noFilesFound.setVisibility(View.GONE);
                    }
                    // TODO: progress bar and dataChanged
                    imageAdapter = new ImageAdapter(imageList, statusList);
                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                });
            } else {
                handler.post(() -> {
                    if (imageList.size() <= 0) {
                        noFilesView();
                    } else {
                        noFilesFound.setVisibility(View.GONE);
                    }
                    imageAdapter = new ImageAdapter(imageList, imageStatusList);
                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                });
            }
            swipeRefreshLayout.setRefreshing(false);
        }).start();

    }


    public void getListAboveQ(Uri uriMain) {

        statusList.clear();

        ContentResolver contentResolver = requireContext().getContentResolver();
        Uri buildChildDocumentsUriUsingTree = DocumentsContract.buildChildDocumentsUriUsingTree(uriMain, DocumentsContract.getDocumentId(uriMain));

        try (Cursor cursor = contentResolver.query(buildChildDocumentsUriUsingTree, new String[]{"document_id"}, null, null, null)) {
            while (cursor.moveToNext()) {
                if (!DocumentsContract.buildDocumentUriUsingTree(uriMain, cursor.getString(0)).toString().endsWith(".nomedia")) {

                    statusList.add(DocumentsContract.buildDocumentUriUsingTree(uriMain, cursor.getString(0)));

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void noFilesView(){
        View noFilesFound = requireView().findViewById(R.id.no_files_found_img);
        noFilesFound.setVisibility(View.VISIBLE);

        Button openWA = noFilesFound.findViewById(R.id.no_files_button);
        openWA.setOnClickListener(v -> {

            Intent launchIntent = null;
            try{
                launchIntent = requireActivity().getPackageManager().getLaunchIntentForPackage(CommonClass.WA_PACKAGE_NAME);
            } catch (Exception ignored) {}

            if(launchIntent == null){
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=" + CommonClass.WA_PACKAGE_NAME)));
            } else {
                startActivity(launchIntent);
            }

        });
    }

}