package com.example.maxiaowu.simpleimageloader;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static int LOCAL_IMAGE_COMPLETE=0x001;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageAdapter imageAdapter;
    private ImageView bigImageView;
    private int   mImagesCount;
    private String mDirPath;
    private List<String> localDataSet;
    ArrayList<String> dataset=new ArrayList<>(Arrays.asList("http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/1.jpg",
            "http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/Hydrangeas.jpg",
            "http://10.117.4.49:8080/kaola.png",
            "http://10.117.4.49:8080/flower.png",
            "http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/1.jpg",
            "http://10.117.4.49:8080/dessert.png","http://10.117.4.49:8080/1.jpg","http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/dessert.png","http://10.117.4.49:8080/1.jpg","http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/kaola.png",
            "http://10.117.4.49:8080/dessert.png","http://10.117.4.49:8080/1.jpg","http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/1.jpg","http://10.117.4.49:8080/dessert.png"));

    //    ArrayList<String> dataset=new ArrayList<>(Arrays.asList("http://192.168.1.106:8080/dessert.png",
//            "http://192.168.1.106:8080/tecent.png",
//            "http://192.168.1.106:8080/channel.png",
//            "http://192.168.1.106:8080/Hydrangeas.png",
//            "http://192.168.1.106:8080/kaola.png",
//            "http://192.168.1.106:8080/flower.png",
//            "http://192.168.1.106:8080/tomcat.png",
//            "http://192.168.1.106:8080/1.png",
//            "http://192.168.1.106:8080/dessert.png",
//            "http://192.168.1.106:8080/tecent.png",
//            "http://192.168.1.106:8080/dessert.png",
//            "http://192.168.1.106:8080/tecent.png",
//            "http://192.168.1.106:8080/dessert.png",
//            "http://192.168.1.106:8080/tecent.png",
//            "http://192.168.1.106:8080/dessert.png",
//            "http://192.168.1.106:8080/tecent.png",
//            "http://192.168.1.106:8080/dessert.png",
//            "http://192.168.1.106:8080/tecent.png",
//            "http://192.168.1.106:8080/dessert.png",
//            "http://192.168.1.106:8080/dessert.png"));

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOCAL_IMAGE_COMPLETE:
                    progressBar.dismiss();
                    localDataSet= Arrays.asList(new File(mDirPath).list());
                    recyclerView= (RecyclerView) findViewById(R.id.rv_image_wall);
                    bigImageView= (ImageView) findViewById(R.id.iv_content);
//        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
                    recyclerView.setLayoutManager(linearLayoutManager=new LinearLayoutManager(MainActivity.this));
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setAdapter(imageAdapter=new ImageAdapter(MainActivity.this,localDataSet));
                    imageAdapter.setItemClickListener(new ImageAdapter.IOnItemClickListener() {
                        @Override
                        public void onClick(int adapterPosition, int layoutPosition) {
//                Toast.makeText(getApplicationContext(),"adapterPosition:"+adapterPosition+",layoutPosition:"+layoutPosition,Toast.LENGTH_SHORT).show();
                            bigImageView.setImageResource(R.drawable.failed);
                            MyImageLoader.getInstance().bindBitmap(localDataSet.get(layoutPosition),bigImageView);

                        }
                    });
                    recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
//                    imageAdapter.setRecyclerViewIdle(true);
//                    imageAdapter.notifyDataSetChanged();
//                }
//            }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            int position = linearLayoutManager.findFirstVisibleItemPosition();
                            bigImageView.setImageResource(R.drawable.failed);
                            //FIXME：如果直接使用bindBitmap，会出现上面的大图比下面列表里的图片先加载出来
                            MyImageLoader.getInstance().bindBitmap(localDataSet.get(position),bigImageView);
                        }
                    });

                    break;
            }
        }
    };
    private ProgressDialog progressBar;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocalImages();


    }


    public void   getLocalImages(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(MainActivity.this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar=ProgressDialog.show(MainActivity.this,null,"正在加载。。");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int imageCount=0;
                HashSet<String> dirPaths=new HashSet<String>();
                ContentResolver contentResolver=getContentResolver();
                Uri imageUri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor=contentResolver.query(imageUri,null, MediaStore.Images.Media.MIME_TYPE+"=? or "
                + MediaStore.Images.Media.MIME_TYPE+"=?",new String[]{"image/jpeg","image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

                if (cursor!=null){
                    while (cursor.moveToNext()){
                        String path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        if (path!=null){
                            File parentFile=new File(path).getParentFile();
                            if (parentFile!=null ){
                                if (dirPaths.contains(parentFile.getAbsolutePath())){
                                    continue;
                                }
                                dirPaths.add(parentFile.getAbsolutePath());
                                imageCount=parentFile.list(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File file, String s) {
                                        if (s.endsWith(".jpg")||s.endsWith(".png")||s.endsWith("jpeg")){
                                            return true;
                                        }
                                        return false;
                                    }
                                }).length;
                            }

                            if (imageCount>=mImagesCount){
                                mImagesCount=imageCount;
                                mDirPath=parentFile.getAbsolutePath();
                            }
                        }
                    }
                    cursor.close();
                    dirPaths=null;
                    handler.sendEmptyMessage(LOCAL_IMAGE_COMPLETE);
                }
            }
        }).start();

    }


}
