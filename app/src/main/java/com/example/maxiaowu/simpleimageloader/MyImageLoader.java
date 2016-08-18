package com.example.maxiaowu.simpleimageloader;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.os.SystemClock;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by maxiaowu on 16/8/7.
 */
public class MyImageLoader{
    private static final int DISK_CACHE_INDEX = 0;
    private static final int IO_BUFFER_SIZE = 1024;
    private static final int BIND_BITMAP_COMPLETE = 0x001;
    private static final int  TAG_URL =R.id.Iv_image ;
    private Context mContext;
    private LruCache<String,Bitmap> lruCache;
    private DiskLruCache diskLruCache;
    private boolean mIsDiskLruCacheCreated;
    private ImageResizer mImageResizer;
    private Executor excutor;

    public boolean ismIsDiskLruCacheCreated() {
        return mIsDiskLruCacheCreated;
    }

    private static final long DISK_CACHE_SIZE=50*1024*1024;
    private static final int  THREAD_COUNT=4;

    private MyHandler mHandler;

    public static class MyHandler extends android.os.Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BIND_BITMAP_COMPLETE:
                    //FIXME:解决图片重复加载的问题
                    LoaderResult result= (LoaderResult) msg.obj;
                    if (result!=null && result.imageView!=null &&result.imageView.getTag(TAG_URL)!=null&& result.imageView.getTag(TAG_URL).equals(result.url)){
                        result.imageView.setImageBitmap(result.bitmap);
                    }
                    break;
            }
        }
    }

    private static class HolderClass{
        public static MyImageLoader imageLoader=new MyImageLoader();
    }

    public static MyImageLoader getInstance(){
        return HolderClass.imageLoader;
    }
    private MyImageLoader() {
        mContext=ImageLoderApplication.context;
        mImageResizer=new ImageResizer();
        excutor= Executors.newFixedThreadPool(THREAD_COUNT);
        mHandler=new MyHandler();
        initLruCache();
        initDiskLruCache();

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
    public Bitmap getBitmapFromDiskMemory(String url,int reqWidth,int reqHeight) {
        //把url转化成key,因为图片的url里可能有特殊字符,这将影响url在安卓中的使用
        String key = hashKeyFromUrl(url);
        Bitmap bitmap = null;
        try {
          if (diskLruCache!=null){
              DiskLruCache.Snapshot snapShot = diskLruCache.get(key);
            if (snapShot!=null){

                FileInputStream inputStream = (FileInputStream) snapShot.getInputStream(DISK_CACHE_INDEX);
                bitmap = mImageResizer.decodeSampleBitmapFromFileDescriptor(inputStream.getFD(), reqWidth, reqHeight);
            }
              //因为FileInputStream是有序的文件流,两次decodeStream会影响文件流的位置属性,会导致第二次得到的是null,所以通过文件流对应的文件描述符解决
          }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }
    public void putBitmapToDiskMemory(String url,InputStream inputStream){
        String hashKey=hashKeyFromUrl(url);
        try {
            DiskLruCache.Editor editor=diskLruCache.edit(hashKey);
            if (editor!=null){
                OutputStream outputStream=editor.newOutputStream(DISK_CACHE_INDEX);
                BufferedOutputStream buffer=new BufferedOutputStream(outputStream,IO_BUFFER_SIZE);
                int b;
               if (inputStream!=null) {
                   while ((b = inputStream.read()) != -1) {
                       buffer.write(b);
                   }
                   editor.commit();
               }else {
                   editor.abort();
               }

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


    public Bitmap loadImage(String url,int reqWidth,int reqHeight){
        Bitmap bitmap=null;
        bitmap=getBitmapFromMemory(url);
        if (bitmap!=null){
            return bitmap;
        }
       if (mIsDiskLruCacheCreated) {
           bitmap = getBitmapFromDiskMemory(url, reqWidth, reqHeight);
           if (bitmap != null) {
               putBitmapToMemory(url, bitmap);
               return bitmap;
           }
           bitmap=downloadBitmapToStream(url, reqWidth, reqHeight);
           return bitmap;
       }
        bitmap=downloadBitmapFromUrl(url);
        return bitmap;

    }

    public void bindBitmap(final String url, final ImageView imageview){
        imageview.setTag(TAG_URL,url);
        Bitmap bitmap=getBitmapFromMemory(url);
        if (bitmap!=null){
            imageview.setImageBitmap(bitmap);
            return;
        }
        Runnable runnableTask=new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap=loadImage(url,imageview.getWidth(),imageview.getHeight());
                if (bitmap!=null){
                    LoaderResult result=new LoaderResult(imageview,url,bitmap);
                    mHandler.obtainMessage(BIND_BITMAP_COMPLETE,result).sendToTarget();


                }
            }
        };
        excutor.execute(runnableTask);
    }

    private Bitmap downloadBitmapFromUrl(String urlstr){
        URL url= null;
        try {
            url = new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            InputStream input= conn.getInputStream();
            return BitmapFactory.decodeStream(new BufferedInputStream(input,IO_BUFFER_SIZE));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Bitmap downloadBitmapToStream(String urlstr,int reqWidth,int reqHeight) {
        try {
            URL url=new URL(urlstr);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            InputStream input= conn.getInputStream();
            putBitmapToDiskMemory(urlstr,input);
            SystemClock.sleep(500);
            return getBitmapFromDiskMemory(urlstr,reqWidth,reqHeight);
        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public class LoaderResult{
        public ImageView imageView;
        public String url;
        public Bitmap bitmap;

        public LoaderResult(ImageView imageView, String url, Bitmap bitmap) {
            this.imageView = imageView;
            this.url = url;
            this.bitmap = bitmap;
        }
    }

}
