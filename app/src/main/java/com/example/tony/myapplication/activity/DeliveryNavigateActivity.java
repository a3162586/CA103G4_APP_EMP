package com.example.tony.myapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.tony.myapplication.R;

public class DeliveryNavigateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_navigate);
        Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
    }
}
