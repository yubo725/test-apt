package com.example.testapt;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.annotation.MyAnnotation;

public class OtherActivity extends AppCompatActivity {

    @MyAnnotation(R.id.text_view)
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        OtherActivityViewInjector.inject(this);
    }
}
