package com.yatik.statussaver.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yatik.statussaver.R;
import com.yatik.statussaver.models.Status;
import com.yatik.statussaver.ui.ZoomView;

import java.util.Objects;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {
    private Context context;

    @NonNull
    @Override
    public StatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_view, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.ViewHolder holder, int position) {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);

        Status status = differ.getCurrentList().get(position);
        if (status.getFileName().endsWith(".mp4")) {
            holder.videoPlayIcon.setVisibility(View.VISIBLE);
        }
        Glide
                .with(context)
                .load(status.getContentUri())
                .centerCrop()
                .apply(requestOptions)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            String filePath = status.getContentUri().toString();
            Intent intent = new Intent(context, ZoomView.class);
            intent.putExtra("filePath", filePath);
            context.startActivity(intent);
        });
        holder.imageView.setOnLongClickListener(v -> {
            Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private final DiffUtil.ItemCallback<Status> differCallbacks = new DiffUtil.ItemCallback<Status>() {
        @Override
        public boolean areItemsTheSame(@NonNull Status oldItem, @NonNull Status newItem) {
            return Objects.equals(oldItem.getContentUri(), newItem.getContentUri());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Status oldItem, @NonNull Status newItem) {
            return oldItem.equals(newItem);
        }
    };

    public AsyncListDiffer<Status> differ = new AsyncListDiffer<>(this, differCallbacks);

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final ImageView videoPlayIcon;

        public ViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.fileView);
            videoPlayIcon = view.findViewById(R.id.play_icon);
        }

    }

}
