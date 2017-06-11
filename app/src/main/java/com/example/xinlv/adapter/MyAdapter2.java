package com.example.xinlv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xinlv.Bean.BeatsInfo;
import com.example.xinlv.R;

import java.util.ArrayList;

/**
 * Created by user on 16-3-25.
 */
public class MyAdapter2 extends BaseAdapter {

    private Context context;
    private ArrayList<BeatsInfo> infoList;
    private int beats;

    public MyAdapter2(Context context, ArrayList<BeatsInfo> infoList) {
        this.context = context;
        this.infoList = infoList;

    }

    @Override
    public int getCount() {
        int n=0;
        for(BeatsInfo beatsInfo:infoList){
            if(beatsInfo.getBeats()>0)
                n++;
        }
        return n;
    }

    @Override
    public Object getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        class ViewHolder {
            TextView tv_lv2;
        }

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list2_item, null);
            holder = new ViewHolder();
            holder.tv_lv2 = (TextView) convertView.findViewById(R.id.lv2_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        beats = infoList.get(position).getBeats();
        holder.tv_lv2.setText(beats + "");
        if (beats > 100 || beats < 60) {
            holder.tv_lv2.setTextColor(Color.WHITE);
            holder.tv_lv2.setBackgroundColor(Color.RED);
        }
        return convertView;
    }
}
