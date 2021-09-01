package com.car.navigation.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.car.navigation.R;
import com.car.navigation.fragment.HomeFragment;
import com.car.navigation.fragment.SettingFragment;
import com.car.navigation.tcp.SocketClientService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Fragment> fragments = new ArrayList<>();

    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ignoreBatteryOptimization();
        ButterKnife.bind(this);
        startService(new Intent(MainActivity.this, SocketClientService.class));

        String[] mTitles = getResources().getStringArray(R.array.main_tab);

        for (int i = 0; i < mTitles.length; i++) {
            tab.addTab(tab.newTab().setText(mTitles[i]));
        }

        fragments.add(new HomeFragment());
        fragments.add(new SettingFragment());

        viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });

        //设置TabLayout和ViewPager联动
        tab.setupWithViewPager(viewpager, false);
    }


    /**
     * 忽略电池优化
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ignoreBatteryOptimization() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(this.getPackageName());
            if (!hasIgnored) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        } catch (Exception e) {
            //TODO :handle exception
            Log.e("ex", e.getMessage());
        }
    }

}