package com.yatik.statussaver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{

    private ArrayList<ImageData> imageData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView leftImageView;
        private final ImageView rightImageView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            leftImageView = view.findViewById(R.id.imageView);
            rightImageView = view.findViewById(R.id.imageView2);
        }

        public ImageView getLeftImageView() {
            return leftImageView;
        }

        public ImageView getRightImageView(){
            return rightImageView;
        }
    }

    public CustomAdapter(ArrayList<ImageData> imageData){
        this.imageData = imageData;
    }


    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        holder.getLeftImageView().setImageResource(imageData.get(position).getLeftImageId());
        holder.getRightImageView().setImageResource(imageData.get(position).getRightImageId());
    }

    @Override
    public int getItemCount() {
        return imageData.size();
    }
}
