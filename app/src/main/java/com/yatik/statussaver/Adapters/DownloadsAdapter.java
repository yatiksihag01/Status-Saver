package com.yatik.statussaver.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yatik.statussaver.ZoomView;
import com.yatik.statussaver.Others.Data;
import com.yatik.statussaver.R;

import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder>{

    private final List<Data> filesList;
    private Context context;

    public DownloadsAdapter(List<Data> filesData){
        this.filesList = filesData;
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

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull DownloadsAdapter.ViewHolder holder, int position) {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //TODO: add error image here
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        Data singleFileData = filesList.get(position);
        Glide
                .with(context)
                .load(singleFileData.getFileUri())
                .centerCrop()
                .apply(requestOptions)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            String filePath = singleFileData.getPath();
            Intent intent = new Intent(context, ZoomView.class);
            intent.putExtra("filePath", filePath);
            intent.putExtra("sentFrom", "downloadsAdapter");
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }
}
