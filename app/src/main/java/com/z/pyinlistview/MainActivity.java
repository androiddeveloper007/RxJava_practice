package com.z.pyinlistview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.lv_city_selection)
    XListView lvCitySelection;
    @BindView(R.id.sidrbar)
    SideBar sideBar;
    @BindView(R.id.dialog)
    TextView dialog;
    private SortCityAdapter adapter;
    private PinyinComparatorCity pinyinComparator;
    private Thread thread;
    List<addressBean.city> cities;
    public static final int MSG_LOAD_DATA = 0x0001;
    public static final int MSG_LOAD_SUCCESS = 0x0002;
    public static final int MSG_LOAD_FAILED = 0x0003;
    private TimeCountUtil countUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handler.sendEmptyMessage(MSG_LOAD_DATA);//初始化地址数据
        pinyinComparator = new PinyinComparatorCity();
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    lvCitySelection.setSelection(position);
                }
            }
        });
    }

    private void initJsonData() {
        countUtil = new TimeCountUtil();
        countUtil.startCalculate();
        String JsonData = new GetJsonDataUtil().getJson(MainActivity.this, "address.json");
        ArrayList<addressBean> jsonBean = parseData(JsonData);
        cities = new ArrayList<>();
        for (addressBean a : jsonBean) {
            List<addressBean.city> b = a.getCity();
            cities.addAll(b);
        }
        handler.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }

    public ArrayList<addressBean> parseData(String result) {
        ArrayList<addressBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                addressBean entity = gson.fromJson(data.optJSONObject(i).toString(), addressBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    Observable.create(new ObservableOnSubscribe() {
                        @Override
                        public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                            countUtil.endCalculate();
                            Collections.sort(cities, pinyinComparator);
                            e.onComplete();
                        }
                    }).subscribeOn(Schedulers.io()).
                            observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {
                                }
                                @Override
                                public void onNext(@NonNull Object o) {
                                }
                                @Override
                                public void onError(@NonNull Throwable e) {
                                    Toast.makeText(MainActivity.this, "解析数据失败", Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onComplete() {
                                    countUtil.endCalculate();
                                    adapter = new SortCityAdapter(MainActivity.this, cities);
                                    lvCitySelection.setAdapter(adapter);
                                }
                            });//把这段代码连起来写就成了RxJava引以为傲的链式操作
                    break;
                case MSG_LOAD_FAILED:
                    Toast.makeText(MainActivity.this, "解析数据失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
}
/*//Observable  对应的上游类型是ObservableOnSubscribe 我们需要发射数据，所以实现了ObservableEmitter方法
                    Observable download = Observable.create(new ObservableOnSubscribe() {
                        @Override
                        public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                            countUtil.endCalculate();
                            Collections.sort(cities, pinyinComparator);
                            e.onComplete();
                        }
                    });
                    //Observable 对应的下游类型是Observer
                    Observer observer = new Observer() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }
                        @Override
                        public void onNext(@NonNull Object o) {
                        }
                        @Override
                        public void onError(@NonNull Throwable e) {
                            Toast.makeText(MainActivity.this, "解析数据失败", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onComplete() {
                            countUtil.endCalculate();
                            adapter = new SortCityAdapter(MainActivity.this, cities);
                            lvCitySelection.setAdapter(adapter);
                        }
                    };
                    //建立订阅关系AndroidSchedulers.mainThread()
                    download.subscribeOn(Schedulers.io()).
                            observeOn(AndroidSchedulers.mainThread())
                            .subscribe(observer);//把这段代码连起来写就成了RxJava引以为傲的链式操作*/