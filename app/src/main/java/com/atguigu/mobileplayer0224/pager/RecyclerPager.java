package com.atguigu.mobileplayer0224.pager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.adapter.RecyclerAdapter;
import com.atguigu.mobileplayer0224.domain.NetAudioBean;
import com.atguigu.mobileplayer0224.fragment.BaseFragment;
import com.atguigu.mobileplayer0224.utils.Constants;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;


/**
 * Created by shkstart on 2017/6/1.
 */

public class RecyclerPager extends BaseFragment{
    private RecyclerView recyclerview;
    private ProgressBar progressbar;
    private TextView tv_nomedia;
    private RecyclerAdapter adapter;
    private List<NetAudioBean.ListBean> datas;

    @Override
    public View initView() {
        View view=View.inflate(context, R.layout.recycler_audio,null);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        getDateFromNet();
    }

    private void getDateFromNet() {
        RequestParams reques = new RequestParams(Constants.NET_AUDIO_URL);
        x.http().get(reques, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("onSuccess==" + result);
                processData(result);
               // refresh.finishRefresh();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("onError==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    private void processData(String json) {
        NetAudioBean netAudioBean = new Gson().fromJson(json, NetAudioBean.class);
        datas = netAudioBean.getList();
        if(datas!=null&&datas.size()>0) {
            //有视频
            tv_nomedia.setVisibility(View.GONE);
            //设置适配器
            adapter = new RecyclerAdapter(context,datas);
            recyclerview.setAdapter(adapter);
            //RecyclerView必须设置LayoutManager
            recyclerview.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        }else {
            tv_nomedia.setVisibility(View.VISIBLE);
        }
        progressbar.setVisibility(View.GONE);
    }
}
