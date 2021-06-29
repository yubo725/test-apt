package com.example.testapt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.annotation.MyAnnotation;

public class MainActivity extends AppCompatActivity {

    @MyAnnotation("hello")
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}