package com.example.xinlv.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xinlv.MenuActivity;
import com.example.xinlv.R;
import com.example.xinlv.Bean.BeatsInfo;
import com.example.xinlv.db.BeatsInfoDao;
import com.example.xinlv.net.Connect;

import java.util.ArrayList;

/**
 * Created by user on 16-3-22.
 */
public class MineFragment extends Fragment implements View.OnClickListener{

    private Activity activity;
    private BeatsInfoDao dao;

    private TextView tv_mac, tv_sb, tv_cause, tv_plan;
    private EditText et_hb;
    //下面的textview是作为button使用的
    private TextView tv_eat,tv_sport,tv_medicine;

    private int currentBeats=0;
    private String mac=MenuActivity.mac;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        dao = new BeatsInfoDao(activity);
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initView(view);
        initWatcher(view);
        return view;
    }
    private void initWatcher(View view){
        et_hb= (EditText) view.findViewById(R.id.et_hb);
        et_hb.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputBeats=s.toString().trim();
                if(inputBeats.length()==0){
                    return;
                }
                int ib=Integer.valueOf(inputBeats);
                currentBeats=ib;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initView(View view) {
        tv_mac = (TextView) view.findViewById(R.id.tv_mac);
        et_hb = (EditText) view.findViewById(R.id.et_hb);
        tv_sb = (TextView) view.findViewById(R.id.tv_sb);
        tv_cause = (TextView) view.findViewById(R.id.tv_cause);
        tv_plan = (TextView) view.findViewById(R.id.tv_plan);
        //3个按钮TextView
        tv_eat= (TextView) view.findViewById(R.id.tv_eat);
        tv_sport= (TextView) view.findViewById(R.id.tv_sport);
        tv_medicine= (TextView) view.findViewById(R.id.tv_medicine);
        tv_eat.setOnClickListener(this);
        tv_sport.setOnClickListener(this);
        tv_medicine.setOnClickListener(this);

        tv_mac.setText(MenuActivity.mac);
        et_hb.setText(getCurrentBeats());
        tv_sb.setText(getSmallNormalBig());
        tv_cause.setText(getCause());
    }

    @Override
    public void onClick(View v) {
        if(currentBeats>=60&&currentBeats<=100){
        tv_plan.setText(R.string.normal);
        return;
        }
        switch (v.getId()){
            case R.id.tv_eat:
                setPlan(1,currentBeats);
                break;
            case R.id.tv_sport:
                setPlan(2,currentBeats);
                break;
            case R.id.tv_medicine:
                setPlan(3,currentBeats);
                break;
        }
    }
    private void setPlan(int num,int beats){
        if(beats==0){
            Toast.makeText(activity,"请先测试",Toast.LENGTH_SHORT).show();
            return  ;
        }
        Connect.setPlan(mac,num+"",beats+"",tv_plan);
    }

    private String getCause(){
        String[] array=getResources().getStringArray(R.array.snb);
        String retn;
        if(currentBeats>100){
            retn=array[2];
        }else if(currentBeats<60){
            retn=array[0];
        }else {
            retn=array[1];
        }
        return retn;
    }
    private String getSmallNormalBig(){
        String retn;
        if(currentBeats>100){
            retn="大";
        }else if(currentBeats<60){
            retn="小";
        }else {
            retn="适中";
        }
        return  retn;
    }

    private String getCurrentBeats() {
        ArrayList<BeatsInfo> infoList = dao.getBeats();
        if(infoList.isEmpty()){
            //Toast.makeText(activity,"您还未测过心律",Toast.LENGTH_SHORT).show();
            return "没有测试过心律";
        }else {
            currentBeats= infoList.get(0).getBeats();
            return currentBeats+"";
        }
    }
}
