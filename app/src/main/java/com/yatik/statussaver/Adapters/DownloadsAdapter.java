package com.yatik.statussaver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yatik.statussaver.Others.Data;
import com.yatik.statussaver.ImageDetails;
import com.yatik.statussaver.R;

import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder>{

    private final List<Data> filesList;
    private final List<Object> statusList;
    private Context context;

    public DownloadsAdapter(List<Data> filesData, List<Object> statusList){
        this.filesList = filesData;
        this.statusList =  statusList;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        public ViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.fileView);

        }
    }

    @NonNull
    @Override
    public DownloadsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadsAdapter.ViewHolder holder, int position) {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //TODO: add error image here
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            Data singleFileData = filesList.get(position);
            Glide
                    .with(context)
                    .load(singleFileData.getFileUri())
                    .centerCrop()
                    .apply(requestOptions)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(v -> {
                String filePath = singleFileData.getPath();
                Intent intent = new Intent(context, ImageDetails.class);
                intent.putExtra("filePath", filePath);
                context.startActivity(intent);
            });

        } else{
            Uri uriToLoad = Uri.parse(String.valueOf(statusList.get(position)));

            Log.v("uriToLoad", String.valueOf(statusList.get(position)));
            Glide
                    .with(context)
                    .load(uriToLoad)
                    .centerCrop()
                    .apply(requestOptions)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(v -> {
                String filePath = String.valueOf(statusList.get(position));
                Intent intent = new Intent(context, ImageDetails.class);
                intent.putExtra("filePath", filePath);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return statusList.size();
        }
        return filesList.size();
    }
}
