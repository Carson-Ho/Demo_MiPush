package scut.carson_ho.demo_mipush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Message;

/**
 * Created by Carson_Ho on 16/11/2.
 */
public class Network_Receiver extends BroadcastReceiver {

    private boolean isConn;

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("网络状态发生变化");

        //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {


            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() | dataNetworkInfo.isConnected()) {
                System.out.println("有网了");

                //立即回调注册
                System.out.println("回调立即注册推送服务");

                Message msg = BaseActivity.AppHandler.obtainMessage();
                msg.what = 0;
                BaseActivity.AppHandler.sendMessage(msg);

            } else {
                System.out.println("没网了");
                BaseActivity.pushState = 1;
            }

            //API大于23时使用下面的方式进行网络监听
        } else {

            System.out.println("API level 大于23");
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                if (networkInfo.isConnected()) {
                    //立即回调注册
                    System.out.println("回调立即注册推送服务");

                    Message msg = BaseActivity.AppHandler.obtainMessage();
                    msg.what = 0;
                    BaseActivity.AppHandler.sendMessage(msg);

                    break;
                } else {
                    System.out.println("没网了");
                    //没网时设置推送服务状态量
                    BaseActivity.pushState = 1;

                }

            }
        }
    }
}


