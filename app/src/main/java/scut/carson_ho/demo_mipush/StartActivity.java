package scut.carson_ho.demo_mipush;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Carson_Ho on 16/11/2.
 */
public class StartActivity extends AppCompatActivity {

    private Handler mainHandler, workHandler;
    private HandlerThread mHandlerThread;

    private int time = 4;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        mainHandler = new Handler();
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (StartActivity.this,
                        MainActivity.class);
                //跳转主页
                startActivity(intent);
                StartActivity.this.finish();
            }
        });

        //创建后台计时线程
        initBackground();

        workHandler.sendEmptyMessage(0x121);

    }

    private void initBackground() {

        mHandlerThread = new HandlerThread("countTime");

        //启动新线程
        mHandlerThread.start();

        workHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            //消息处理的操作
            public void handleMessage(Message msg) {
                //设置了两种消息处理操作,通过msg来进行识别
                switch (msg.what) {
                    //标识1:0x121
                    case 0x121:

                        if (time > 0) {

                            //通过主线程Handler.post方法进行在主线程的UI更新操作
                            //可能有人看到new了一个Runnable就以为是又开了一个新线程
                            //事实上并没有开启任何新线程，只是使run()方法体的代码抛到与mHandler相关联的线程中执行，我们知道mainHandler是与主线程关联的，所以更新TextView组件依然发生在主线程
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    button.setText(time + "s后跳过");
                                    time--;
                                }
                            });
                            Message msg2 = workHandler.obtainMessage();// obtain=get 是一种优化写�?
                            // 内部查找可重用的Message 如果有就重用 没有�? 才创建新的�??
                            msg2.what = 0x121;

                            workHandler.sendMessageDelayed(msg2, 1000);


                        }

                        if (time == 0) {
                            Intent localIntent = new Intent(StartActivity.this,
                                    MainActivity.class);
                            //跳转主页
                            startActivity(localIntent);

                            StartActivity.this.finish();
                            return;
                        }

                        break;
                }


            }
        };


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        workHandler.removeCallbacksAndMessages(null);
        mainHandler.removeCallbacksAndMessages(null);
    }



}
