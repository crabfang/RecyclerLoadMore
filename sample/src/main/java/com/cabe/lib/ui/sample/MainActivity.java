package com.cabe.lib.ui.sample;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cabe.lib.ui.widget.LoadMoreRecyclerView;
import com.cabe.lib.ui.widget.OnEndViewListener;
import com.cabe.lib.ui.widget.OnLoadViewListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：沈建芳 on 2019-05-16 16:01
 */
public class MainActivity extends AppCompatActivity {
    private LoadMoreRecyclerView recyclerView;
    private MyAdapter mMyAdapter = new MyAdapter();
    private int curPageIndex = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.activity_main_recycler);
        recyclerView.setLoadTips("请稍后，还有更多内容哦");
        recyclerView.setEndTips("很遗憾，没有更多内容了");
        recyclerView.setAdapter(mMyAdapter);
        recyclerView.setScrollCallback(() -> recyclerView.postDelayed(() -> appendData(++ curPageIndex), 1000));
        recyclerView.postDelayed(this::loadData, 1000);

        recyclerView.setOnLoadViewListener(new OnLoadViewListener() {
            @Override
            public View onCreateLoadView(ViewGroup parent) {
                return null;
            }
            @Override
            public void onLoadViewBind(View loadView) {
                loadView.setBackgroundColor(0xFFFF0000);
                TextView label = (TextView) ((ViewGroup) loadView).getChildAt(1);
                label.setTextColor(0xFFFFFFFF);
            }
        });
        recyclerView.setOnEndViewListener(new OnEndViewListener() {
            @Override
            public View onCreateEndView(ViewGroup parent) {
                return null;
            }
            @Override
            public void onEndViewBind(View loadView) {
                loadView.setBackgroundColor(0xFF0000FF);
                TextView label = (TextView) ((ViewGroup) loadView).getChildAt(0);
                label.setTextColor(0xFF00FFFF);
            }
        });
        recyclerView.setOnChildrenCallback(recyclerView -> {
            boolean isEnough = true;
            int parentHeight = ((ViewGroup)recyclerView.getParent()).getHeight();
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int canShowHeight = Math.max(parentHeight, layoutManager.getHeight());

            Rect rect = new Rect();
            int childPosition = layoutManager.getItemCount() > 1 ? layoutManager.getItemCount() - 2 : 0;
            View lastView = layoutManager.findViewByPosition(childPosition);
            if(lastView != null) {
                //lastView为空，表示没滚动到底
                lastView.getGlobalVisibleRect(rect);
                if(rect.bottom <= canShowHeight) {
                    isEnough = false;
                }
            }
            return isEnough;
        });

        SwipeRefreshLayout swipeLayout = findViewById(R.id.activity_main_swipe);
        swipeLayout.setOnRefreshListener(() -> recyclerView.postDelayed(() -> {
            swipeLayout.setRefreshing(false);
            loadData();
        }, 1000));
    }

    private void loadData() {
        curPageIndex = 0;
        List<String> dataList = new ArrayList<>();
        for(int i=0;i<4;i++) {
            dataList.add("loadData_" + i);
        }
        mMyAdapter.setDataList(dataList);
    }

    private void appendData(int pageIndex) {
        List<String> dataList = new ArrayList<>();
        for(int i=0;i<10;i++) {
            dataList.add("appendData#" + pageIndex + "_" + i);
        }
        mMyAdapter.appendDataList(dataList);
        if(pageIndex > 1) {
            recyclerView.setScrollEnd(true);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<String> dataList = new ArrayList<>();
        private void setDataList(List<String> list) {
            dataList = list;
            notifyDataSetChanged();
        }
        private void appendDataList(List<String> list) {
            int count = getItemCount();
            if(list != null) {
                dataList.addAll(list);
            }
            notifyItemInserted(count);
        }
        private String getItemData(int position) {
            if(position < 0 || position >= getItemCount()) return null;

            return dataList.get(position);
        }
        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String data = getItemData(position);
            holder.rowLabel.setText(data);
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView rowLabel;
        MyViewHolder(View itemView) {
            super(itemView);
            rowLabel = itemView.findViewById(R.id.item_layout_view);
        }
    }
}
