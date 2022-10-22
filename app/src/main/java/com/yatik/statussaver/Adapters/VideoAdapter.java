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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yatik.statussaver.Others.Data;
import com.yatik.statussaver.R;
import com.yatik.statussaver.ZoomView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final List<Data> videoList;
    private final List<Object> statusList;
    private Context context;

    public VideoAdapter(List<Data> imageData, List<Object> statusList) {
        this.videoList = imageData;
        this.statusList = statusList;

    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //TODO: add error image here
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        holder.videoPlayIcon.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Data videoData = videoList.get(position);
            Glide
                    .with(context)
                    .load(videoData.getFileUri())
                    .centerCrop()
                    .apply(requestOptions)
                    .into(holder.thumbnailView);

            holder.thumbnailView.setOnClickListener(v -> {
                String filePath = videoData.getPath();
                Intent intent = new Intent(context, ZoomView.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("sentFrom", "videoAdapter");
                context.startActivity(intent);
            });


            holder.thumbnailView.setOnLongClickListener(v -> {
                Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
                return true;
            });

        } else {
            Uri uriToLoad = Uri.parse(String.valueOf(statusList.get(position)));

            Log.v("uriToLoad", String.valueOf(statusList.get(position)));
            Glide
                    .with(context)
                    .load(uriToLoad)
                    .centerCrop()
                    .apply(requestOptions)
                    .into(holder.thumbnailView);

            holder.thumbnailView.setOnClickListener(v -> {
                String filePath = String.valueOf(statusList.get(position));
                Intent intent = new Intent(context, ZoomView.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("sentFrom", "videoAdapter");
                context.startActivity(intent);
            });


            holder.thumbnailView.setOnLongClickListener(v -> {
                Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return statusList.size();
        }
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnailView;
        private final ImageView videoPlayIcon;

        public ViewHolder(View view) {
            super(view);

            thumbnailView = view.findViewById(R.id.fileView);
            videoPlayIcon = view.findViewById(R.id.play_icon);

        }
    }

}
