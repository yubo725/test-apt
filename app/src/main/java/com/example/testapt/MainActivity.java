package com.example.testapt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.annotation.MyAnnotation;
import com.example.annotation.NeedLogin;

public class MainActivity extends AppCompatActivity {

    @MyAnnotation(R.id.text_view)
    TextView textView;

    @MyAnnotation(R.id.image_view)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityViewInjector.inject(this);
        textView.setText("Hello Injector!");
        imageView.setImageResource(R.drawable.ic_launcher_background);

        textView.setOnClickListener(v -> toOtherActivity());
    }

    @NeedLogin
    public void toOtherActivity() {
        startActivity(new Intent(MainActivity.this, OtherActivity.class));
    }
}