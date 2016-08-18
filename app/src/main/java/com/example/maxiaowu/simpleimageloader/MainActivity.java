package com.example.maxiaowu.simpleimageloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
//    ArrayList<String> dataset=new ArrayList<>(Arrays.asList("http://192.168.1.106:8080/dessert.jpeg",
//            "http://192.168.1.106:8080/tecent.jpeg",
//            "http://192.168.1.106:8080/channel.jpeg",
//            "http://192.168.1.106:8080/Hydrangeas.jpeg",
//            "http://192.168.1.106:8080/kaola.jpeg",
//            "http://192.168.1.106:8080/flower.jpeg",
//            "http://192.168.1.106:8080/tomcat.jpeg",
//            "http://192.168.1.106:8080/1.jpeg",
//            "http://192.168.1.106:8080/dessert.jpeg","http://192.168.1.106:8080/dessert.jpeg","http://192.168.1.106:8080/dessert.jpeg",
//            "http://192.168.1.106:8080/dessert.jpeg","http://192.168.1.106:8080/dessert.jpeg","http://192.168.1.106:8080/dessert.jpeg",
//            "http://192.168.1.106:8080/dessert.jpeg",
//            "http://192.168.1.106:8080/dessert.jpeg","http://192.168.1.106:8080/dessert.jpeg","http://192.168.1.106:8080/dessert.jpeg",
//            "http://192.168.1.106:8080/dessert.jpeg","http://192.168.1.106:8080/dessert.jpeg"));
    ArrayList<String> dataset=new ArrayList<>(Arrays.asList("http://192.168.1.106:8080/dessert.jpeg",
            "http://192.168.1.106:8080/tecent.jpeg",
            "http://192.168.1.106:8080/channel.jpeg",
            "http://192.168.1.106:8080/Hydrangeas.jpeg",
            "http://192.168.1.106:8080/kaola.jpeg",
            "http://192.168.1.106:8080/flower.jpeg",
            "http://192.168.1.106:8080/tomcat.jpeg",
            "http://192.168.1.106:8080/1.jpeg",
            "http://192.168.1.106:8080/dessert.jpeg",
        "http://192.168.1.106:8080/tecent.jpeg",
        "http://192.168.1.106:8080/dessert.jpeg",
        "http://192.168.1.106:8080/tecent.jpeg",
        "http://192.168.1.106:8080/dessert.jpeg",
        "http://192.168.1.106:8080/tecent.jpeg",
            "http://192.168.1.106:8080/dessert.jpeg",
        "http://192.168.1.106:8080/tecent.jpeg",
        "http://192.168.1.106:8080/dessert.jpeg",
        "http://192.168.1.106:8080/tecent.jpeg",
            "http://192.168.1.106:8080/dessert.jpeg",
        "http://192.168.1.106:8080/dessert.jpeg"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView= (RecyclerView) findViewById(R.id.rv_image_wall);
//        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setLayoutManager(linearLayoutManager=new LinearLayoutManager(this));
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setAdapter(new ImageAdapter(this,dataset));
    }
}
