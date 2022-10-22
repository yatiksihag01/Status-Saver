package com.yatik.statussaver.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final List<Data> imageList;
    private final List<Object> statusList;
    private Context context;

    public ImageAdapter(List<Data> imageData, List<Object> statusList) {
        this.imageList = imageData;
        this.statusList = statusList;

    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_view, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        //TODO: add error image here
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Data imageData = imageList.get(position);
            Glide
                    .with(context)
                    .load(imageData.getFileUri())
                    .centerCrop()
                    .apply(requestOptions)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(v -> {
                String filePath = imageData.getPath();
                Intent intent = new Intent(context, ZoomView.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("sentFrom", "imageAdapter");
                context.startActivity(intent);
            });

            holder.imageView.setOnLongClickListener(v -> {
                Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
                return true;
            });

        } else {
            Uri uriToLoad = Uri.parse(String.valueOf(statusList.get(position)));
            Glide
                    .with(context)
                    .load(uriToLoad)
                    .centerCrop()
                    .apply(requestOptions)
                    .into(holder.imageView);

            holder.imageView.setOnClickListener(v -> {
                String filePath = String.valueOf(statusList.get(position));
                Intent intent = new Intent(context, ZoomView.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("sentFrom", "imageAdapter");
                context.startActivity(intent);
            });

            holder.imageView.setOnLongClickListener(v -> {
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
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.fileView);

        }
    }
}
