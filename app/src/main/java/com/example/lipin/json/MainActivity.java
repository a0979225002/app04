package com.example.lipin.json;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager cmgr;
    private MyReceiver myReceiver;//創立廣播器

    @BindView(R.id.mesg) TextView mesg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //實作廣播器
        myReceiver = new MyReceiver();
        //給予單一監聽,網路相關全部監聽
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //多了監聽寫法filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //讓他接收的事件 Receiver:接收者
        filter.addAction("brad");
        registerReceiver(myReceiver,filter);

        //監聽網路狀態
        cmgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    @Override
    public void finish() {
       unregisterReceiver(myReceiver);
        super.finish();
    }

    //查看是否有連線
    private boolean isConnectNetWork(){
     NetworkInfo networkInfo =  cmgr.getActiveNetworkInfo();
     return networkInfo!=null && networkInfo.isConnectedOrConnecting();
    }
    //查看是否有wifi
    private boolean iswifiConnected(){
       NetworkInfo networkInfo = cmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
       return networkInfo.isConnected();
    }
    //BroadcastReceiver:廣播接收器
    // 任何訊息都會接收監聽,你只需判斷要給誰去工作
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("brad")) {
                String data = intent.getStringExtra("data");
                mesg.setText(data);
            }
        }
    }
    //檢查網路
    public void test1(View view) {
        Log.v("brad","isNetwork:"+isConnectNetWork());
    }
//    檢查wifi
    public void test2(View view) {
        Log.v("brad","wifi:"+iswifiConnected());
    }
    public void test3(View view) {
        //有關底層的網路寫法,是需要thread的,不然不能夠去使用
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://www.jianshu.com/p/628832ee4ae5");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuffer sb = new StringBuffer();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    reader.close();
                    Intent intent = new Intent("brad");
                    intent.putExtra("data", sb.toString());
                    sendBroadcast(intent);

                } catch (Exception e) {
                    Log.v("brad", e.toString());
                }
            }
        }.start();

    }
    public void test4(View view) {
        Intent intent = new Intent(this,Main2Activity.class);
        startActivity(intent);
    }
    public void test5(View view) {
    }
    public void test6(View view) {
    }

}
