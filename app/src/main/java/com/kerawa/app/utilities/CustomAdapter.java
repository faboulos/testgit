package com.kerawa.app.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerawa.app.R;

/**
 * Created by mabian on 2/17/16.
 */
public class CustomAdapter extends BaseAdapter{
    String[] tel;
    Context context;
    Integer[] imageId;
    String what;
    private static LayoutInflater inflater = null;
    public  CustomAdapter(Context con, String[] telNo, Integer[] images, String st){
        tel = telNo;
        context = con;
        imageId = images;
        what = st;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return tel.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.smsorcallview, null);
        holder.tv = (TextView)rowView.findViewById(R.id.number);
        holder.imageView = (ImageView)rowView.findViewById(R.id.icon);
        holder.des = (TextView)rowView.findViewById(R.id.desc);
        holder.tv.setText(tel[position]);
        holder.imageView.setImageResource(imageId[position]);
        holder.des.setText(what);

        return rowView;
    }
    public class Holder{
        TextView tv;
        ImageView imageView;
        TextView des;
    }
}
