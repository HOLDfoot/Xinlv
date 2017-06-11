package com.example.xinlv;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.xinlv.Bean.ChartBean;
import com.example.xinlv.net.Connect;
import com.example.xinlv.tools.DateTool;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends Activity {

    private LineChart chart1;
    private LineChart chart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initView();
    }

    private void initView() {
        chart1 = (LineChart) findViewById(R.id.chart1);
        chart2 = (LineChart) findViewById(R.id.chart2);
        chart1.setNoDataText("正在请求网络");
        chart2.setNoDataText("正在请求网络");
        setChartList();//->Connect.getBeatsHistory->GetHistoryTask->initChart1/2
    }


    private void setChartList() {
       /* Connect.getBeatsHistory(MenuActivity.mac,""+1,this);
        Connect.getBeatsHistory(MenuActivity.mac,""+2,this);*/
        //测试使用
        Connect.getBeatsHistory(MenuActivity.mac, "" + 2, this);
        Connect.getBeatsHistory(MenuActivity.mac, "" + 1, this);
       // List<ChartBean> chartList;

    }

    public void initChart(List<ChartBean> chartList, String num) {
        ;

        LineChart chart = null;
        if (num.equals("1")) {
            chart = chart1;
        } else if (num.equals("2")) {
            chart = chart2;
        }
        XAxis xAxis = chart.getXAxis();

        //设置X轴的文字在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //设置描述文字
        if (num.equals("1")) {
            chart.setDescription("最近7天走势图");
        } else if (num.equals("2")) {
            chart.setDescription("最近30天走势图");
        }
        //模拟一个x轴的数据  12/1 12/2 ... 12/7
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < chartList.size(); i++) {
            //横坐标从左到右的标识
            xValues.add(DateTool.getMonthDateHourMinuteFromString(chartList.get(i).getTime()));
        }

        Log.e("wing", xValues.size() + "");


        //模拟第二组组y轴数据(存放y轴数据的是一个Entry的ArrayList) 他是构建LineDataSet的参数之一

        ArrayList<Entry> yValue = new ArrayList<Entry>();
        for (int i = 0; i < chartList.size(); i++) {
            yValue.add(new Entry((chartList.get(i).getBeats()), i));
        }

        //构建一个LineDataSet 代表一组Y轴数据 （比如不同的彩票： 七星彩  双色球）

        LineDataSet dataSet1 = new LineDataSet(yValue, "心率变化");
        if (num.equals("1")) {
            dataSet1.setColor(Color.BLUE);
        } else if (num.equals("2")) {
            dataSet1.setColor(Color.RED);
        }

        //构建一个类型为LineDataSet的ArrayList 用来存放所有 y的LineDataSet   他是构建最终加入LineChart数据集所需要的参数
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();

        //将数据加入dataSets
        dataSets.add(dataSet1);

        //构建一个LineData  将dataSets放入
        LineData lineData = new LineData(xValues, dataSets);

        //将数据插入
        chart.setData(lineData);

    }

}
