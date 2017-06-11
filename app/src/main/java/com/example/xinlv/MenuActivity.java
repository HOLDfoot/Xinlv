package com.example.xinlv;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.example.xinlv.fragment.CheckFragment;
import com.example.xinlv.fragment.HealthFragment;
import com.example.xinlv.fragment.MineFragment;

public class MenuActivity extends FragmentActivity implements View.OnClickListener {

    private Fragment mContent, checkFragment, healthFragment, mineFragment;
    private FragmentManager fragmentManager;
    private ImageButton ib_check, ib_health, ib_mine;
    public static String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        initFragment();
        fragmentManager = getSupportFragmentManager();
        mContent = checkFragment;//initial the mContent
        fragmentManager.beginTransaction().add(R.id.framelayout, mContent).commit();

        initView();
        initMac();
        Intent in = new Intent(this, MainActivity.class);
        //startActivity(in);
    }
    private void initMac(){
        WifiManager manager= (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mac=manager.getConnectionInfo().getMacAddress();
    }

    private void initFragment() {
        checkFragment = new CheckFragment();
        healthFragment = new HealthFragment();
        mineFragment = new MineFragment();
    }

    private void initView() {
        ib_check = (ImageButton) findViewById(R.id.ib_check);
        ib_health = (ImageButton) findViewById(R.id.ib_health);
        ib_mine = (ImageButton) findViewById(R.id.ib_mine);

        ib_mine.setOnClickListener(this);
        ib_health.setOnClickListener(this);
        ib_check.setOnClickListener(this);
    }

    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            //TODO
            if (to.isAdded()) {
                transaction.hide(from).show(to).commit();
            } else {
                transaction.hide(from).add(R.id.framelayout, to).commit();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ib_check:
                if(curIb==R.id.ib_check) return;
                ib_check.setImageResource(R.drawable.check_select);
                setButtonNormal();
                curIb = R.id.ib_check;
                switchContent(mContent, checkFragment);
                break;
            case R.id.ib_health:
                if(curIb==R.id.ib_health) return;
                ib_health.setImageResource(R.drawable.health_select);
                setButtonNormal();
                curIb = R.id.ib_health;
                switchContent(mContent, healthFragment);
                break;
            case R.id.ib_mine:
                if(curIb==R.id.ib_mine) return;
                ib_mine.setImageResource(R.drawable.mine_select);
                setButtonNormal();
                curIb = R.id.ib_mine;
                switchContent(mContent, mineFragment);
                break;

        }
    }

    private static int curIb = R.id.ib_check;

    private void setButtonNormal() {
        switch (curIb) {
            case R.id.ib_check:
                ib_check.setImageResource(R.drawable.check_normal);
                break;
            case R.id.ib_health:
                ib_health.setImageResource(R.drawable.health_normal);
                break;
            case R.id.ib_mine:
                ib_mine.setImageResource(R.drawable.mine_normal);
                break;
        }
    }
}
