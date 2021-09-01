package com.car.navigation.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.car.navigation.Constants;
import com.car.navigation.R;
import com.car.navigation.activity.SearchActivity;
import com.car.navigation.util.DataKeeper;

/**
 * 首页
 */
public class HomeFragment extends Fragment implements AMap.OnMyLocationChangeListener {


    private MapView mMapView;
    //初始化地图控制器对象
    private AMap aMap;

    MyLocationStyle myLocationStyle;
    private String city;
    private String citycode;
    private double latitude;
    private double longitude;
    private LatLng latLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_home, container, false);

        String latitude_str = DataKeeper.get(getActivity(), Constants.HIS_LATITUDE, "");
        String longitude_str = DataKeeper.get(getActivity(), Constants.HIS_LONGITUDE, "");
        if (!TextUtils.isEmpty(latitude_str) && !TextUtils.isEmpty(longitude_str)) {
            latitude = Double.parseDouble(latitude_str);
            longitude = Double.parseDouble(longitude_str);
            latLng = new LatLng(latitude, longitude);
        }

        //获取地图控件引用
        mMapView = (MapView) inflate.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(10000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
        myLocationStyle.radiusFillColor(getResources().getColor(R.color.transparent));
        myLocationStyle.strokeColor(getResources().getColor(R.color.transparent));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setLogoBottomMargin(-150);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMyLocationChangeListener(this);
        if (latLng != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        inflate.findViewById(R.id.tvSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });
        return inflate;

    }

    @Override
    public void onMyLocationChange(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        city = location.getExtras().getString("City");
        citycode = location.getExtras().getString("citycode");

        DataKeeper.put(getActivity(), Constants.HIS_LATITUDE, String.valueOf(latitude));
        DataKeeper.put(getActivity(), Constants.HIS_LONGITUDE, String.valueOf(longitude));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}