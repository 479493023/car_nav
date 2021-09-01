package com.car.navigation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.help.Tip;
import com.car.navigation.R;

import java.util.ArrayList;
import java.util.List;

public class SearchKeyAdapter extends RecyclerView.Adapter<SearchKeyAdapter.ViewHolder> {

    private Context context;

    private List<Tip> mDataList = new ArrayList<>();
    private OnItemClickListener mClickListener = null;
    private String keyword;

    public SearchKeyAdapter(Context context) {
        this.context = context;
    }

    public void notifyDataSetChanged(List<Tip> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_search_key, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SearchKeyAdapter.ViewHolder holder, int position) {
        holder.tvKey.setText(mDataList.get(position).getName());
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
        void onItemClick(Tip key);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvKey;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKey = itemView.findViewById(R.id.tvKey);
        }
    }
}
