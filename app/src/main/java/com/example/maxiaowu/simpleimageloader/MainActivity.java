package com.example.maxiaowu.simpleimageloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    ArrayList<String> dataset=new ArrayList<>(Arrays.asList("http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/tecent.png",
            "http://10.117.4.49:8080/channel.png",
            "http://10.117.4.49:8080/Hydrangeas.png",
            "http://10.117.4.49:8080/kaola.png",
            "http://10.117.4.49:8080/flower.png",
            "http://10.117.4.49:8080/tomcat.png",
            "http://10.117.4.49:8080/1.png",
            "http://10.117.4.49:8080/dessert.png","http://10.117.4.49:8080/dessert.png","http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/dessert.png","http://10.117.4.49:8080/dessert.png","http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/dessert.png","http://10.117.4.49:8080/dessert.png","http://10.117.4.49:8080/dessert.png",
            "http://10.117.4.49:8080/dessert.png","http://10.117.4.49:8080/dessert.png"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridLayoutManager=new ImageGridLayoutManager(this,3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView= (RecyclerView) findViewById(R.id.rv_image_wall);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new ImageAdapter(this,dataset));
    }
}
