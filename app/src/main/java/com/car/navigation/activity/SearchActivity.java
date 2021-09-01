package com.car.navigation.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.car.navigation.R;
import com.car.navigation.adapter.SearchAdapter;
import com.car.navigation.util.DialogUtils;
import com.car.navigation.util.KeyboardUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener, INaviInfoCallback {

    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.rvResult)
    RecyclerView rvResult;
    @BindView(R.id.status)
    ProgressBar status;

    @SuppressLint("InvalidWakeLockTag")
    private PowerManager.WakeLock mWakeLock;
    private AMapNavi mAMapNavi;
    private String keyword;

    private SearchAdapter searchAdapter;
    private String city;


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        KeyboardUtils.showKeyboard(edtContent);//自动弹出软键盘
        mAMapNavi = AMapNavi.getInstance(this);
        mAMapNavi.setIsUseExtraGPSData(true);//开启使用外部定位点数据
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
        }
        city = getIntent().getStringExtra("city");
        tvCity.setText(city);

        searchAdapter = new SearchAdapter(this);
        rvResult.setLayoutManager(new LinearLayoutManager(this));
        rvResult.setAdapter(searchAdapter);
        setLinistener();


    }

    @OnClick({R.id.iv_back, R.id.tv_city})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_city:
                KeyboardUtils.hideKeyboard(edtContent);//自动隐藏软键盘
                DialogUtils.selectPCD(this, new DialogUtils.DialogPCDRequest() {
                    @Override
                    public void selected(String options1Item, String options2Item) {
                        tvCity.setText(options2Item);
                        city = options2Item;
                    }
                });
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnEventThread(AMapLocation amapLocation) {
        if (mAMapNavi != null) {
            mAMapNavi.setExtraGPSData(2, amapLocation);
        }
    }

    private void setLinistener() {
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PoiItem item) {
                LatLonPoint latLonPoint = item.getLatLonPoint();
                AmapNaviParams amapNaviParams = new AmapNaviParams(null, null, new Poi(item.getTitle(), new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()), ""), AmapNaviType.DRIVER);
                amapNaviParams.setUseInnerVoice(true);
                amapNaviParams.setShowCrossImage(true);
                AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), amapNaviParams, SearchActivity.this);
            }
        });

        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
                if (!TextUtils.isEmpty(keyword)) {
                    status.setVisibility(View.VISIBLE);
                    searchAdapter.setKeyword(keyword);
                    doSearchQuery(keyword, city);
                } else {
                    Log.e("afterTextChanged", "结果:key为空");
                    searchAdapter.clearData();
                }
            }
        });

        mAMapNavi.addAMapNaviListener(new AMapNaviListener() {
            @Override
            public void onInitNaviFailure() {

            }

            @Override
            public void onInitNaviSuccess() {

            }

            @Override
            public void onStartNavi(int i) {

            }

            @Override
            public void onTrafficStatusUpdate() {

            }

            @Override
            public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

            }

            @Override
            public void onGetNavigationText(int i, String s) {

            }

            @Override
            public void onGetNavigationText(String s) {

            }

            @Override
            public void onEndEmulatorNavi() {

            }

            @Override
            public void onArriveDestination() {

            }

            @Override
            public void onCalculateRouteFailure(int i) {

            }

            @Override
            public void onReCalculateRouteForYaw() {

            }

            @Override
            public void onReCalculateRouteForTrafficJam() {

            }

            @Override
            public void onArrivedWayPoint(int i) {

            }

            @Override
            public void onGpsOpenStatus(boolean b) {

            }

            @Override
            public void onNaviInfoUpdate(NaviInfo naviInfo) {

            }

            @Override
            public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

            }

            @Override
            public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

            }

            @Override
            public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

            }

            @Override
            public void showCross(AMapNaviCross aMapNaviCross) {

            }

            @Override
            public void hideCross() {

            }

            @Override
            public void showModeCross(AMapModelCross aMapModelCross) {

            }

            @Override
            public void hideModeCross() {

            }

            @Override
            public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

            }

            @Override
            public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

            }

            @Override
            public void hideLaneInfo() {

            }

            @Override
            public void onCalculateRouteSuccess(int[] ints) {

            }

            @Override
            public void notifyParallelRoad(int i) {

            }

            @Override
            public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

            }

            @Override
            public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

            }

            @Override
            public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

            }

            @Override
            public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

            }

            @Override
            public void onPlayRing(int i) {

            }

            @Override
            public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

            }

            @Override
            public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

            }

            @Override
            public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

            }

            @Override
            public void onGpsSignalWeak(boolean b) {

            }
        });
    }


    /**
     * 开始进行poi搜索 按城市 关键字查询
     */
    protected void doSearchQuery(String key, String city) {
        //不输入城市名称有些地方搜索不到
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        PoiSearch.Query query = new PoiSearch.Query(key, "", city);
        // 设置每页最多返回多少条poiitem
        query.setPageSize(50);
        // 设置查询页码
        query.setPageNum(0);

        //构造 PoiSearch 对象，并设置监听
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        //调用 PoiSearch 的 searchPOIAsyn() 方法发送请求。
        poiSearch.searchPOIAsyn();
    }


    @Override
    public void onPoiSearched(PoiResult poiResult, int errcode) {
        if (errcode == 1000) {
            ArrayList<PoiItem> pois = poiResult.getPois();
            if (pois.size() == 0) {//无搜索结果
                Toast.makeText(this, "未搜索到结果!", Toast.LENGTH_SHORT).show();
                status.setVisibility(View.GONE);
            } else {//有搜索结果
                status.setVisibility(View.GONE);
                searchAdapter.notifyDataSetChanged(pois);
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviDirectionChanged(int i) {

    }

    @Override
    public void onDayAndNightModeChanged(int i) {

    }

    @Override
    public void onBroadcastModeChanged(int i) {

    }

    @Override
    public void onScaleAutoChanged(boolean b) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

}