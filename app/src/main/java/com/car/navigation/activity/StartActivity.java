package com.car.navigation.activity;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.car.navigation.R;

public class StartActivity extends CheckPermissionsActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
}