package com.atguigu.mobileplayer0224.pager;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.activity.ShowImageAndGifActivity;
import com.atguigu.mobileplayer0224.adapter.NetAudioFragmentAdapter;
import com.atguigu.mobileplayer0224.domain.NetAudioBean;
import com.atguigu.mobileplayer0224.fragment.BaseFragment;
import com.atguigu.mobileplayer0224.utils.CacheUtils;
import com.atguigu.mobileplayer0224.utils.Constants;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;


public class NetAudioPager extends BaseFragment {
    //private TextView textView;

   // private Context context;
    private ListView listview;
    private ProgressBar progressbar;
    private TextView tvNomedia;
    //String url="http://s.budejie.com/topic/list/jingxuan/1/budejie-android-6.2.8/0-20.json?market=baidu&udid=863425026599592&appname=baisibudejie&os=4.2.2&client=android&visiting=&mac=98%3A6c%3Af5%3A4b%3A72%3A6d&ver=6.2.8";
    private List<NetAudioBean.ListBean>  datas;
    private NetAudioFragmentAdapter myAdapter;
    private MaterialRefreshLayout refresh;


    @Override
    public View initView() {
        View view=View.inflate(context, R.layout.fragment_net_audio,null);
        listview = (ListView) view.findViewById( R.id.listview );
        progressbar = (ProgressBar) view.findViewById( R.id.progressbar );
        tvNomedia = (TextView) view.findViewById( R.id.tv_nomedia );
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh_audio);
        refresh.setMaterialRefreshListener(new MyMaterialRefreshListener());
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NetAudioBean.ListBean listBean = datas.get(i);
                if(listBean!=null) {

                    Intent intent = new Intent(context, ShowImageAndGifActivity.class);
                    if(listBean.getType().equals("gif")) {
                        String url2 = listBean.getGif().getImages().get(0);
                        intent.putExtra("url2",url2);
                        context.startActivity(intent);
                    }else if(listBean.getType().equals("image")) {
                        String url2 = listBean.getImage().getBig().get(0);
                        intent.putExtra("url2",url2);
                        context.startActivity(intent);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        String url= CacheUtils.getString(context, Constants.NET_AUDIO_URL);
        if(!TextUtils.isEmpty(url)) {
            processData(url);
        }
        getDateFromNet();
    }

    private void getDateFromNet() {
        RequestParams reques = new RequestParams(Constants.NET_AUDIO_URL);
        x.http().get(reques, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("onSuccess==" + result);
                processData(result);
                refresh.finishRefresh();
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
            tvNomedia.setVisibility(View.GONE);
            //设置适配器
            myAdapter = new NetAudioFragmentAdapter(context,datas);
            listview.setAdapter(myAdapter);
        }else {
            tvNomedia.setVisibility(View.VISIBLE);
        }
        progressbar.setVisibility(View.GONE);

    }
    class MyMaterialRefreshListener extends MaterialRefreshListener {


        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            getDateFromNet();
        }
    }

}
