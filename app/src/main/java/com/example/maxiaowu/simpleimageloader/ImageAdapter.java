package com.example.maxiaowu.simpleimageloader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by xiaowu on 2016-8-17.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private ArrayList<String> dataset;

    public ImageAdapter(Context context, ArrayList<String> dataset) {
        this.mContext=context;
        this.dataset=dataset;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(mContext).inflate(R.layout.item,parent,false);
        ImageViewHolder imageViewHolder=new ImageViewHolder(itemView);
//        itemView.setTag(imageViewHolder);
        return imageViewHolder;
    }


    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.imageView.setTag(dataset.get(position));
        MyImageLoader.getInstance().bindBitmap(dataset.get(position),holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.Iv_image);
        }
    }
}
