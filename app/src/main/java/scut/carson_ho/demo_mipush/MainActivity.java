package scut.carson_ho.demo_mipush;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //1 = 注册失败(没网);1 = 广播还没注册
        if (BaseActivity.pushState == 1 && BaseActivity.broadcastNet_State == 1) {

            System.out.println("在首页注册广播");
            Message msg = BaseActivity.AppHandler.obtainMessage();
            msg.what = 2;
            BaseActivity.AppHandler.sendMessage(msg);


        }else{
            System.out.println("不用在首页注册广播");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //2 = 广播已注册
        if (BaseActivity.broadcastNet_State == 2) {

            System.out.println("在首页注销广播");
            Message msg = BaseActivity.AppHandler.obtainMessage();
            msg.what = 3;
            BaseActivity.AppHandler.sendMessage(msg);

        }else{
            System.out.println("不用在首页注销广播");
        }
    }
}
