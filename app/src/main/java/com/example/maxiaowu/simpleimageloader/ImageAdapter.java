package com.example.maxiaowu.simpleimageloader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by xiaowu on 2016-8-17.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<String> dataset;
    private IOnItemClickListener itemClickListener;
//    private boolean isRecyclerViewIdle;
//
//    public void setRecyclerViewIdle(boolean recyclerViewIdle) {
//        isRecyclerViewIdle = recyclerViewIdle;
//    }

    public void setItemClickListener(IOnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ImageAdapter(Context context, List<String> dataset) {
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

        String url=dataset.get(position);
        String tag= (String) holder.imageView.getTag();
        //为了防止出现图片错乱的情况
        if (!url.equals(tag)){
//           holder.imageView.setImageResource(R.mipmap.ic_launcher);
            holder.imageView.setImageResource(R.drawable.failed);
        }
            holder.imageView.setTag(dataset.get(position));
            MyImageLoader.getInstance().bindBitmap(dataset.get(position),holder.imageView);

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public  class ImageViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;

        public ImageViewHolder(final View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.Iv_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(getAdapterPosition(),getLayoutPosition());
                }
            });
        }
    }

    public  interface IOnItemClickListener{
        void onClick(int adapterPosition,int layoutPosition);
    }
}
