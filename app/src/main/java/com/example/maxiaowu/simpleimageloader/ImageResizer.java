package com.example.maxiaowu.simpleimageloader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/**
 * Created by maxiaowu on 16/8/16.
 */
public class ImageResizer {

    public Bitmap decodeSampleBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFileDescriptor(fd,null,options);
    }


    public Bitmap decodeSampleBitmapFromResources(Resources res,int resId,int reqWidth,int reqHeight){
        BitmapFactory.Options option=new BitmapFactory.Options();
        option.inJustDecodeBounds=true;
        option.inSampleSize=calculateInSampleSize(option,reqWidth,reqHeight);
        option.inJustDecodeBounds=false;
        return BitmapFactory.decodeResource(res,resId,option);
    }
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            while (halfWidth / inSampleSize > reqWidth && halfHeight / inSampleSize > reqHeight) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}