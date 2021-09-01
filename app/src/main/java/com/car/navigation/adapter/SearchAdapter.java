package com.car.navigation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.core.PoiItem;
import com.car.navigation.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {


    private Context context;
    private List<PoiItem> mDataList = new ArrayList<>();
    private OnItemClickListener mClickListener = null;
    private String keyword;

    public SearchAdapter(Context context) {
        this.context = context;
    }

    public void notifyDataSetChanged(List<PoiItem> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void clearData() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        PoiItem poiItem = mDataList.get(position);
        if (TextUtils.isEmpty(keyword)) {
            holder.tvAddress.setText(poiItem.getTitle());
        } else {
            holder.tvAddress.setText(setKeyHeightLight(poiItem.getTitle()));
        }
        holder.tvDetail.setText(poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(mDataList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(PoiItem item);
    }

    public SpannableString setKeyHeightLight(String content) {
        SpannableString s = new SpannableString(content);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAddress;
        private final TextView tvDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvDetail = itemView.findViewById(R.id.tv_detail);
        }
    }
}
