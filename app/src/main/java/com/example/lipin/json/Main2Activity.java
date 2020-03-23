package com.example.lipin.json;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends AppCompatActivity {
    private ConnectivityManager cmgr;
    private MyReceiver myReceiver;//創立廣播器
    private boolean isAllowSDCard;//允許外部儲存
    private File downloaDir;//外部資源存取位置
    private ProgressDialog progressDialog;//給予進度對話框


    @BindView(R.id.mesg)
    TextView mesg;
    @BindView(R.id.img)
    ImageView img;

    public Main2Activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        //網路搜尋android request permission官方白皮書
        //開啟外部儲存權限
        //查看有無這個權限
        if (ContextCompat.checkSelfPermission(this,
                //加入外部儲存媒體權限
                //無權限
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        } else {
            //有權限
            isAllowSDCard = true;
            init();
        }
    }

    private void init() {
        //有權限才能下載
        if (isAllowSDCard){
            //將下載下來的檔案放在外存的DOWNLOADS資料夾內
            downloaDir = Environment.getExternalStoragePublicDirectory(
                                 Environment.DIRECTORY_DOWNLOADS);
        }
        //給予進度條
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Downloading.......");


        //實作廣播器
        myReceiver = new MyReceiver();
        //給予單一監聽,網路相關全部監聽
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //多了監聽寫法filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        //讓他接收的事件 Receiver:接收者
        filter.addAction("brad");//多聽一個intent

        registerReceiver(myReceiver, filter);

        //監聽網路狀態
        cmgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //有取得權限
            isAllowSDCard = true;
        } else {
            //沒有取得權限
            isAllowSDCard = false;
        }
        init();
    }

    //關閉廣播器
    @Override
    public void finish() {
        unregisterReceiver(myReceiver);
        super.finish();
    }

    //查看是否有連線
    private boolean isConnectNetWork() {
        NetworkInfo networkInfo = cmgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
    //BroadcastReceiver:廣播接收器
    // 任何訊息都會接收監聽,你只需判斷要給誰去工作
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //將接收intent內自設的名稱給予監聽事件IntentFilter
            if (intent.getAction().equals("brad")) {
                //呼叫intent的key,他會傳給我當初設置的那個key的value
                String data = intent.getStringExtra("data");
                //將他輸出到TextView內
                mesg.setText(data);
            } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                //是否有網路監聽
                Log.v("brad", "isNetwork:" + isConnectNetWork());
            }
        }
    }
    //檢查網路

    //產生圖片
    public void test4(View view) {
        new Thread() {
            @Override
            public void run() {
                fetchImage();
            }
        }.start();
    }

    private Bitmap bmp;

    //抓取圖片01方法
    private void fetchImage() {
        try {
            URL url = new URL("https://scontent.ftpe7-1.fna.fbcdn.net/v/t45.1600-4/fr/cp0/q90/spS444/90183260_23844401339030594_1167503635836305408_n.png.jpg?_nc_cat=1&_nc_sid=67cdda&_nc_ohc=zHeV4ZWyy9sAX-q4CnV&_nc_ht=scontent.ftpe7-1.fna&oh=a40c2a2afd0eb9bbf27f8992c27d0919&oe=5E9E829F");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            //將圖片轉成BitMap
            bmp = BitmapFactory.decodeStream(connection.getInputStream());
            uIhandler.sendEmptyMessage(0);

        } catch (Exception e) {

        }
    }

    //抓取圖片02
    private void fetchImage02() {
        try {
            URL url = new URL("https://p2.bahamut.com.tw/B/2KU/78/3fd6cba69a49f81c785000c001183em5.JPG?w=1000");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            //將圖片轉成BitMap
            bmp = BitmapFactory.decodeStream(connection.getInputStream());
            uIhandler.sendEmptyMessage(1);

        } catch (Exception e) {

        }
    }

    //使用UIhandler去更新ui介面
    private UIhandler uIhandler = new UIhandler();

    private class UIhandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    img.setImageBitmap(bmp);
                    break;
                case 1:
                    img.setImageBitmap(bmp);
                    break;
                case 2:
                    progressDialog.dismiss();//讓進度條消失掉,並沒有刪除該物件
                    break;
                default:
                    Log.v("brad", "有出問題");
            }

        }
    }

    //第二張圖片
    public void test5(View view) {
        new Thread() {
            @Override
            public void run() {
                fetchImage02();
            }
        }.start();

    }

    //使用pdfmyurl網站的(網站轉成pdf)功能方法
    private void fetchPDF() {
        try {
            URL url = new URL("https://pdfmyurl.com/?url=https://www.gamer.com.tw/");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            HttpURLConnection connection1 =(HttpsURLConnection) url.openConnection();
//            因為跳出error:1000008b:SSL 無法解析加密https
//            因此加入這串讓他為true
            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            connection.connect();

            File downloadFile = new File(downloaDir,"gamer.pdf");
            FileOutputStream fout = new FileOutputStream(downloadFile);

            byte[] buf = new byte[1024*4096];//一次4mb
            BufferedInputStream bin =
                    new BufferedInputStream(connection.getInputStream());

            int len = -1;
            while ((len = bin.read(buf))!=-1){
                fout.write(buf,0,len);
            }

            bin.close();
            fout.flush();
            fout.close();

            Log.v("brad","save ok");

        } catch (Exception e) {
            Log.v("brad", e.toString());
        }finally {
            //防止ui在上面一直跑進度條,事情做完讓他關掉
            uIhandler.sendEmptyMessage(2);

        }
    }

    public void test6(View view) {
        //沒有允許權限不給做動作
        if (!isAllowSDCard) return;
        progressDialog.show();//show出進度條
        new Thread(){
            @Override
            public void run() {
                fetchPDF();
            }
        }.start();
    }

}
