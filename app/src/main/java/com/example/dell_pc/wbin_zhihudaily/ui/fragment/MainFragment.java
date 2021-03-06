package com.example.dell_pc.wbin_zhihudaily.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell_pc.wbin_zhihudaily.R;
import com.example.dell_pc.wbin_zhihudaily.adapter.MainAdapter;
import com.example.dell_pc.wbin_zhihudaily.bean.NewsEntity;
import com.example.dell_pc.wbin_zhihudaily.bean.StoryEntity;
import com.example.dell_pc.wbin_zhihudaily.bean.TopStoryEntity;
import com.example.dell_pc.wbin_zhihudaily.network.Network;
import com.example.dell_pc.wbin_zhihudaily.ui.activity.StoryActivity;
import com.example.dell_pc.wbin_zhihudaily.util.DateUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wbin on 2016/8/22.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    MainAdapter adapter = new MainAdapter();
    public static ArrayList<TopStoryEntity> topStoryEntities;   //热门新闻
    boolean state = true;     //状态，为true表示获取今日要闻或刷新，为false表示获取往期消息
    boolean isLoading = false;      //是否在加载新的数据[下拉加载更多]

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //下拉加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                if (lastVisibleItemPosition >= totalItemCount - 2 && dy > 0) {      //底部剩余两个未显示时加载新的数据
                    if (!isLoading) {
                        isLoading = true;
                        loadHistoryData();
                    }
                }
            }
        });

        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, StoryEntity storyEntity) {
                Intent intent = new Intent(getActivity(), StoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(StoryEntity.class.toString(), storyEntity);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
        state = true;
        loadData();
        return view;
    }

    /**
     * 加载最新信息
     */
    private void loadData() {
        state = true;
        Network.getZhihuApi()
                .getLatestNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    /**
     * 加载往期信息
     */
    private void loadHistoryData() {
        Log.e(MainFragment.class.toString(), "loadHistoryData!!!");
        state = false;
        DateUtil.subDate();
        Network.getZhihuApi2()
                .getBeforeNews(DateUtil.getDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    @Override
    public void onRefresh() {
        Log.e(MainFragment.class.toString(), "刷新！！！！！！！");
        state = true;
        loadData();
    }

    //subscriber不能重用[onCompleted后会取消订阅],故每次都要重新new一个
    private Subscriber<NewsEntity> getSubscriber() {
        return new Subscriber<NewsEntity>() {
            @Override
            public void onCompleted() {
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
            }

            @Override
            public void onError(Throwable e) {
                Log.e(MainFragment.class.toString(), e.toString());
                Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
            }

            @Override
            public void onNext(NewsEntity newsEntity) {
                Log.e(MainFragment.class.toString(), "on next!!");
                String date = newsEntity.getDate();
                for (StoryEntity storyEntity : newsEntity.getStories()) {
                    storyEntity.setDate(date);
                }
                if (state) {
                    DateUtil.setCalendar(date);
                    topStoryEntities = newsEntity.getTop_stories();
                    adapter.setList(newsEntity.getStories());
                } else {
                    adapter.appendList(newsEntity.getStories());
                }
            }
        };
    }
}
