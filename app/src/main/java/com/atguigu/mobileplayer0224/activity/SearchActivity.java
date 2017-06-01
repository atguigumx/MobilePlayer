package com.atguigu.mobileplayer0224.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileplayer0224.R;
import com.atguigu.mobileplayer0224.adapter.SearchAdapter;
import com.atguigu.mobileplayer0224.domain.SearchBean;
import com.atguigu.mobileplayer0224.utils.JsonParser;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private List<SearchBean.ItemsBean> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViews();
    }
    private EditText etSousuo;
    private TextView tvGo;
    private ImageView ivVoice;
    private ListView lv;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    public static final String NET_SEARCH_URL = "http://hot.news.cntv.cn/index.php?controller=list&action=searchList&sort=date&n=20&wd=";
    private String url;
    private SearchAdapter adapter;
    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-27 11:54:25 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        etSousuo = (EditText)findViewById( R.id.et_sousuo );
        tvGo = (TextView)findViewById( R.id.tv_go );
        lv = (ListView)findViewById( R.id.lv );
        ivVoice = (ImageView)findViewById(R.id.iv_voice);

        ivVoice.setOnClickListener(this);
        tvGo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_voice:
                //语言输入
                showVoiceDialog();
                break;
            case R.id.tv_go:
                toSearch();
                break;
        }
    }

    private void toSearch() {
        String trim = etSousuo.getText().toString().trim();
        if(!TextUtils.isEmpty(trim)) {
            url=NET_SEARCH_URL+trim;
            getDateFromNet(url);
        }else {
            Toast.makeText(SearchActivity.this, "无数据", Toast.LENGTH_SHORT).show();
        }

        
    }

    private void getDateFromNet(String Url) {
        RequestParams request=new RequestParams(Url);
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
               processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processData(String result) {
        SearchBean searchBean = new Gson().fromJson(result, SearchBean.class);
        items = searchBean.getItems();
               if(items != null && items.size() >0){
            adapter = new SearchAdapter(this,items);
            lv.setAdapter(adapter);
                   }
    }

    private void showVoiceDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //默认设置普通话
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
         // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {
            if (i == ErrorCode.SUCCESS) {
               Toast.makeText(SearchActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
            }
        }
    }
    class MyRecognizerDialogListener implements RecognizerDialogListener {

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String resultString = recognizerResult.getResultString();
            Log.e("TAG","onResult=="+resultString);
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.e("TAG","onError=="+speechError.getMessage());
        }
    }
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        etSousuo.setText(resultBuffer.toString());
        etSousuo.setSelection(etSousuo.length());

    }
}
