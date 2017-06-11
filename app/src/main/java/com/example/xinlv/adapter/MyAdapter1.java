package com.example.xinlv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xinlv.R;

/**
 * Created by user on 16-3-21.
 */
public class MyAdapter1 extends BaseAdapter {
    private Context context;
    private int[] array;
    private int small=0;
    private int big=1;
    private int small_position,big_position;
    private int length;
    /**
     * get the position of the small and the position of big data
     */
    void getSBPosion(){
        if(length<2){
            return;
        }
        small=array[0];
        for(int i=0;i<length;i++){
            if(array[i]>big){
                big=array[i];
                big_position=i;
            }
            if(array[i]<small){
                small=array[i];
                small_position=i;
            }
        }
        for(int i=0;i<10;i++) {
            Log.d("Tag", array[i]+"  i="+i);
        }
        Log.d("Tag","small="+small+"big="+big);
        Log.d("Tag","small="+small_position+"bigposition="+big_position);
    }
    public MyAdapter1(Context context,int[] array,int length){
        this.context=context;
        this.array=array;
        this.length=length;
        getSBPosion();
    }
    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public Object getItem(int position) {
        return array[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        class ViewHolder{
                TextView tv;
        }
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.list1_item,null);
            holder=new ViewHolder();
            holder.tv= (TextView) convertView.findViewById(R.id.lv1_tv);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.tv.setText(""+array[position]);
        if(position==small_position){
            holder.tv.setTextColor(Color.YELLOW);
        }else if(position==big_position){
            holder.tv.setTextColor(Color.RED);
        }else {
            holder.tv.setTextColor(Color.BLACK);
            holder.tv.setTextSize(12);
        }

        return convertView;
    }
}
