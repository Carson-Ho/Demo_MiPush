package scut.carson_ho.demo_mipush;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

/**
 * Created by Carson_Ho on 16/10/26.
 */

    //主要要继承Application
public class BaseActivity extends Application {
    // 使用自己APP的ID（官网注册的）
    private static final String APP_ID = "2882303761517520369";
    // 使用自己APP的Key（官网注册的）
    private static final String APP_KEY = "5401752085369";

    //推送服务状态量
    public static int pushState = 1;

    //网络检测注册广播状态量
    public static int broadcastNet_State = 1;

    //网络监听变量
    Network_Receiver netWorkStateReceiver;

    //回调线程
    HandlerThread mHandlerThread;
    public static Handler AppHandler;


    //为了提高推送服务的注册率，我建议在Application的onCreate中初始化推送服务
    //你也可以根据需要，在其他地方初始化推送服务
    @Override
    public void onCreate() {
        super.onCreate();

        //创建回调线程
        initBackground();

        if (shouldInit()) {
            //注册推送服务
            //注册成功后会向DemoMessageReceiver发送广播
            // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
    }


    //创建回调线程
    private void initBackground(){
        //通过实例化mHandlerThread从而创建新线程
        mHandlerThread = new HandlerThread("handlerThread");
        //启动新线程
        mHandlerThread.start();
        //消息处理的操作
        AppHandler = new Handler(mHandlerThread.getLooper()){
            
            @Override

            public void handleMessage(Message msg)
            {

                //0=立即注册推送服务,1=延时注册推送服务,2=注册广播,3=注销广播
                switch(msg.what){

                    //0=立即注册推送服务
                    case 0:
                        System.out.println("立即注册推送服务");
                        startPush();
                        break;

                    //1=延时注册推送服务
                    case 1:
                        System.out.println("正在回调延时(1分钟)注册推送服务....");
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println("延时完毕,开始重新注册推送服务");
                        startPush();
                        break;

                    //2=注册广播
                    case 2:
                        System.out.println("正在回调注册广播...");
                        regBroadcastReceiver();
                        break;

                    //3=注销广播
                    case 3:
                        System.out.println("正在回调注销广播...");
                        unRegBroadcastReceiver();
                        break;

                    default:
                        break;
                }
            }
        };
    }

    //通过判断手机里的所有进程是否有这个App的进程
    //从而判断该App是否有打开
    private boolean shouldInit() {

    //通过ActivityManager我们可以获得系统里正在运行的activities
    //包括进程(Process)等、应用程序/包、服务(Service)、任务(Task)信息。
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();

        //获取本App的唯一标识
        int myPid = Process.myPid();
        //利用一个增强for循环取出手机里的所有进程
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            //通过比较进程的唯一标识和包名判断进程里是否存在该App
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }


    //注册推送服务
    public void startPush(){
        if (shouldInit() && !(pushState ==3) ) {
            System.out.println("注册推送服务");
            MiPushClient.registerPush(this, APP_ID, APP_KEY);

        }
    }

    //注册广播
    public void regBroadcastReceiver(){
            netWorkStateReceiver = new Network_Receiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        System.out.println("注册广播成功");
        broadcastNet_State = 2;

    }

    //注销广播
    public void unRegBroadcastReceiver(){
                unregisterReceiver(netWorkStateReceiver);
        System.out.println("注销广播成功");
        broadcastNet_State = 1;

        }

    // 程序终止的时候执行
    @Override
    public void onTerminate() {
        System.out.println("程序终止的时候执行");
        releaseHandler();
        super.onTerminate();
    }

    //释放线程,防止内存泄露
    private void releaseHandler(){
        AppHandler.removeCallbacksAndMessages(null);
        }

}
