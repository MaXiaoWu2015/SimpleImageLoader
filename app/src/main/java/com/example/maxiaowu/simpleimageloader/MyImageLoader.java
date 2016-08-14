package com.example.maxiaowu.simpleimageloader;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityManagerCompat;
import android.util.LruCache;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by maxiaowu on 16/8/7.
 */
public class MyImageLoader {
    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFER_SIZE = 1024;
    private Context mContext;
    private LruCache<String,Bitmap> lruCache;
    private DiskLruCache diskLruCache;
    private boolean mIsDiskLruCacheCreated;

    public boolean ismIsDiskLruCacheCreated() {
        return mIsDiskLruCacheCreated;
    }

    private static final long DISK_CACHE_SIZE=50*1024*1024;
    private MyImageLoader(Context context) {
        mContext=context;
        initLruCache();
        initDiskLruCache();

    }
    public static MyImageLoader build(Context context){
        return new MyImageLoader(context);
    }

    private void initDiskLruCache() {

        File diskCacheDir=getDiskCacheDir(mContext,"bitmap");
        if (diskCacheDir.exists()){
            diskCacheDir.mkdirs();
        }
        try {
            if(getUsableSpace()>DISK_CACHE_SIZE){

                diskLruCache=DiskLruCache.open(diskCacheDir,1,1,DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated=true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private long getUsableSpace() {
        ActivityManager am= (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;

    }

    private File getDiskCacheDir(Context mContext, String bitmap) {
        return new File(mContext.getCacheDir()+"/"+bitmap);
    }

    private void initLruCache() {
        int maxMemory= (int) (Runtime.getRuntime().maxMemory()/1024);
        int cacheSize=maxMemory/8;
        lruCache=new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight()/1024;
            }
        };
    }

    public Bitmap getBitmapFromMemory(String url){
        Bitmap bitmap=lruCache.get(url);
        return bitmap;
    }
    public void putBitmapToMemory(String url,Bitmap bitmap){
       if (getBitmapFromMemory(url)==null){
           lruCache.put(url,bitmap);
       }
    }
    public Bitmap getBitmapFromDiskMemory(String url){
        //把url转化成key,因为图片的url里可能有特殊字符,这将影响url在安卓中的使用
        String key=hashKeyFromUrl(url);
        try {
            DiskLruCache.Snapshot snapShot=diskLruCache.get(key);
            FileInputStream inputStream= (FileInputStream) snapShot.getInputStream(DISK_CACHE_INDEX);
            //FIXME 因为FileInputStream是有序的文件流,两次decodeStream会影响文件流的位置属性,会导致第二次得到的是null,所以通过文件流对应的文件描述符解决
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void putBitmapToDiskMemory(String url,InputStream inputStream){
        String hashKey=hashKeyFromUrl(url);
        try {
            DiskLruCache.Editor editor=diskLruCache.edit(hashKey);
            if (editor!=null){
                OutputStream outputStream=editor.newOutputStream(DISK_CACHE_INDEX);
                BufferedOutputStream buffer=new BufferedOutputStream(outputStream,IO_BUFFER_SIZE);
                int b;
                while ((b=inputStream.read())!=-1){
                    buffer.write(b);
                }
                editor.commit();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String hashKeyFromUrl(String url){
        String cacheKey;
        try {
            MessageDigest digest=MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            cacheKey=bytesToHexString(digest.digest());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            cacheKey=String.valueOf(url.hashCode());
        }
        return cacheKey;

    }

    public String bytesToHexString(byte[] bytes){
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<bytes.length;i++){
            String hex=Integer.toHexString(0xFF & bytes[i]);
            if (hex.length()==1){
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    public void loadImage(String url, ImageView imageView){

    }


}
