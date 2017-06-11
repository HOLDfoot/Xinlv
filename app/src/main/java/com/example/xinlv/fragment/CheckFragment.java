package com.example.xinlv.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xinlv.EyeActivity;
import com.example.xinlv.LocationActivity;
import com.example.xinlv.MainActivity;
import com.example.xinlv.R;
import com.example.xinlv.WebActivity;

public class CheckFragment extends Fragment {


    //private TextView tv_what, tv_inflact, tv_principle, tv_check;
    private GridView gridView;

    public CheckFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(new MyGridViewAdapter());
    }


    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("测量原理");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setMessage(R.string.principle);
        builder.setPositiveButton("了解", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //可以为null
            }
        });
        //TODO
        builder.create().show();
    }

    private class MyGridViewAdapter extends BaseAdapter {

        private int[] drawable = new int[]{R.drawable.what1,
                R.drawable.influence2, R.drawable.reason3,
                R.drawable.watch4, R.drawable.eye5, R.drawable.running6};
        private String[] name = new String[]{"什么是心率", "对健康的影响", "测量原理", "心率监测",
                "视力护航", "RunningMan"};

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.main_gridview_item, null);
                holder.iv = (ImageView) convertView
                        .findViewById(R.id.gridview_item_iv);
                holder.nameTv = (TextView) convertView
                        .findViewById(R.id.gridview_item_name);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    switch (position) {
                        case 0:
                            intent.putExtra("url", "http://baike.baidu.com/link?url=BfXsXKlCOz-WtqTkqTN2XSLSAbjDV31oCHrn8Qln6n4mlYLIlRiPurrb_rp30PGA");
                            startActivity(intent);
                            break;
                        case 1:
                            intent.putExtra("url", "http://www.jingyanbus.com/health/jiankang/189693.html");
                            startActivity(intent);
                            break;
                        case 2:
                            showDialog();
                            break;
                        case 3:
                            Intent in = new Intent(getActivity(), MainActivity.class);
                            startActivity(in);
                            break;
                        case 4:
                            //Toast.makeText(getActivity(), "5", Toast.LENGTH_SHORT).show();
                            Intent eye = new Intent(getActivity(), EyeActivity.class);
                            startActivity(eye);
                            break;
                        case 5:
                            //Toast.makeText(getActivity(), "6", Toast.LENGTH_SHORT).show();
                            Intent run = new Intent(getActivity(), LocationActivity.class);
                            startActivity(run);
                            break;
                    }

                }
            });

            holder.iv.setImageResource(drawable[position]);
            holder.nameTv.setText(name[position]);

            return convertView;
        }

        private class ViewHolder {
            ImageView iv;
            TextView nameTv, numTv;
        }
    }
}
