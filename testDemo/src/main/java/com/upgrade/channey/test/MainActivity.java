package com.upgrade.channey.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.upgrade.channey.common_upgrade.UpgradeDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //7.0版本 authority不能为空，7.0以下版本authority可为空
        String authority = "com.upgrade.channey.test.fileProvider"; //和manifest中provider的authority一致
        UpgradeDialog.getInstance(this)
                .focusUpdate(false)
                .gotoMarket(true)
                .setOnNegativeButtonClickListener(new UpgradeDialog.OnNegativeButtonClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(MainActivity.this,"onClick",Toast.LENGTH_SHORT).show();
                // TODO: 2017/3/2 这里处理 "下次再说" 按钮的点击事件
            }
        })
                .show("有新版本","http://118.178.248.230:8888/app-dxd-debug.apk",authority);
    }
}
