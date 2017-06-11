package com.example.xinlv.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.xinlv.Bean.BeatsInfo;
import com.example.xinlv.Bean.ChartBean;
import com.example.xinlv.ChartActivity;
import com.example.xinlv.R;
import com.example.xinlv.db.BeatsInfoDao;
import com.example.xinlv.tools.DateTool;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16-3-22.
 */
public class HealthFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    private LineChart mLineChart;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private TextView tv_result,tv_chart;
    private int currentTab=0;
    private static BeatsInfoDao dao;
    private Button  btn_chart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_health, container, false);
        initView(view);
        return view;
    }
    private void initView(View view){
        mLineChart = (LineChart) view.findViewById(R.id.lineChart);
        linearLayout=(LinearLayout)view.findViewById(R.id.linearlayout);
        scrollView= (ScrollView) view.findViewById(R.id.scrollview);
        tv_result= (TextView) view.findViewById(R.id.tv_result);
        tv_chart= (TextView) view.findViewById(R.id.tv_chart);
        btn_chart=(Button)view.findViewById(R.id.btn_chart);
        initLinearLayout();

        tv_result.setOnClickListener(this);
        tv_chart.setOnClickListener(this);

        btn_chart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_chart:
                if(currentTab==1) return;
                linearLayout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                currentTab=1;
                break;
            case R.id.tv_result:
                if(currentTab==0) return;
                linearLayout.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                currentTab=0;
                break;
            case R.id.btn_chart:
                Intent intent=new Intent(getActivity(),ChartActivity.class);
                startActivity(intent);

        }
    }

    private List<ChartBean> getChartList(){
        if(dao==null){
            dao=new BeatsInfoDao(getActivity());
        }
        ArrayList<BeatsInfo> infoList=dao.getBeats();
        ArrayList<ChartBean> chartList=new ArrayList<ChartBean>();
        BeatsInfo info;
        for(int i=0;i<infoList.size();i++){
            info=infoList.get(i);
            chartList.add(new ChartBean(DateTool.getMonthDateHourMinuteFromInt(info.getTime()),info.getBeats()));
        }
        // Collections.reverse(chartList);
        return  chartList;
    }
    private void initLinearLayout(){

        List<ChartBean> chartList=  getChartList();

        XAxis xAxis = mLineChart.getXAxis();

        //设置X轴的文字在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //设置描述文字
        mLineChart.setDescription("本地心率走势图");

        //模拟一个x轴的数据  12/1 12/2 ... 12/7
        ArrayList<String> xValues = new ArrayList<String>();
        //由于取出的数据是desc的所以翻转一下
        for (int i =0;i<chartList.size(); i++) {
            //横坐标从左到右的标识
            xValues.add(chartList.get(i).getTime());
        }

        Log.e("wing", xValues.size() + "");



        //模拟第二组组y轴数据(存放y轴数据的是一个Entry的ArrayList) 他是构建LineDataSet的参数之一

        ArrayList<Entry> yValue = new ArrayList<Entry>();

        for (int i =chartList.size()-1;i>=0; i--) {
            yValue.add(new Entry((chartList.get(i).getBeats()),i));
        }

        //构建一个LineDataSet 代表一组Y轴数据 （比如不同的彩票： 七星彩  双色球）

        LineDataSet dataSet1 = new LineDataSet(yValue, "心率变化");
        dataSet1.setColor(Color.GREEN);
        //构建一个类型为LineDataSet的ArrayList 用来存放所有 y的LineDataSet   他是构建最终加入LineChart数据集所需要的参数
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();

        //将数据加入dataSets
        dataSets.add(dataSet1);

        //构建一个LineData  将dataSets放入
        LineData lineData = new LineData(xValues, dataSets);

        //将数据插入
        mLineChart.setData(lineData);

    }
}
