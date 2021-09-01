package com.car.navigation.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.car.navigation.entity.CityModel;

import java.util.ArrayList;
import java.util.List;

/**
 * selector Dialog选择Utils
 *
 * @author CJZ
 * @Time 2018/11/20
 */
public class DialogUtils {

    private static List<String> options1Items;
    private static ArrayList<ArrayList<String>> options2Items;
    private static ArrayList<ArrayList<ArrayList<String>>> options3Items;

    /**
     * Dialog本单位用户选择
     *
     * @param context 上下文
     * @param list    源数据
     * @param request 回调
     */
    public static void selectActivityType(Context context, final List<String> list, DialogRequest request) {
        ArrayList<String> nameList = new ArrayList<>();
        for (String model : list) {
            nameList.add(model);
        }
        selectorCondition(context, nameList, request);
    }

    public interface DialogRequest {
        void selected(int index);
    }

    /**
     * 省市区三级联动
     */
    public static void selectPCD(Context context, DialogPCDRequest request) {
        getPCDData(context);
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1) : "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

//                String opt3tx = options2Items.size() > 0
//                        && options3Items.get(options1).size() > 0
//                        && options3Items.get(options1).get(options2).size() > 0 ?
//                        options3Items.get(options1).get(options2).get(options3) : "";

                request.selected(opt1tx, opt2tx);
            }

        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        //pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器
//        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    public interface DialogPCDRequest {
        void selected(String options1Item, String options2Item);
    }

    /**
     * 条件选择器
     */
    public static void selectorCondition(Context context, List<String> listData, DialogRequest request) {
        /**
         * 注意 ：如果是三级联动的数据(省市区等)，请参照 JsonDataActivity 类里面的写法。
         */
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                request.selected(options1);
            }
        })
                .setContentTextSize(20)//设置滚轮文字大小
                .setSelectOptions(0, 1)//默认选中项
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
                    }
                })
                .build();

        pvOptions.setPicker(listData);//一级选择器*/
        pvOptions.show();
    }


    //省市区数据初始化
    public static void getPCDData(Context context) {
        options1Items = new ArrayList<>();
        options2Items = new ArrayList<>();
        options3Items = new ArrayList<>();
        String jsonAssets = GsonUtils.getJsonAssets(context, "province.json");
        //获取assets目录下的json文件数据
        ArrayList<CityModel> cityModelList = GsonUtils.parseAssetsData(jsonAssets);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        for (int i = 0; i < cityModelList.size(); i++) {//遍历省份
            options1Items.add(cityModelList.get(i).getName());
        }
//        options1Items = cityModelList;

        for (int i = 0; i < cityModelList.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < cityModelList.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = cityModelList.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(cityModelList.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }
    }
}
