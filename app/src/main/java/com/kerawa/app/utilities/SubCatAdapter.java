package com.kerawa.app.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerawa.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 3/16/16.
 */
public class SubCatAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Model> data;
    private static LayoutInflater inflater = null;
    Integer icon;
    public SubCatAdapter(Context c, ArrayList<Model> d,String categoryTitle){
        this.context = c;
        this.data = d;
        switch (categoryTitle){
            case "A vendre":
                this.icon = R.drawable.sale;
                break;
            case "Automobile":
                this.icon  =R.drawable.auto;
                break;
            case "Emplois":
                this.icon = R.drawable.job;
                break;
            case "High Tech":
                this.icon = R.drawable.hight_tech;
                break;
            case "Immobilier":
                this.icon = R.drawable.immo;
                break;
            case "Mode":
                this.icon = R.drawable.style;
                break;
            case "Services":
                this.icon = R.drawable.services;
                break;
            default:
        }


        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();

        if(convertView == null) {

            convertView = inflater.inflate(R.layout.subcatadapterlayout, null);
            holder.tv = (TextView) convertView.findViewById(R.id.Scatname);
            holder.imageView = (ImageView) convertView.findViewById(R.id.arrow);
            holder.iconsub = (ImageView)convertView.findViewById(R.id.iconsub);
            convertView.setTag(holder);

        } else {
            holder =(Holder) convertView.getTag();
        }

            holder.iconsub.setImageResource(icon);
            holder.tv.setText(data.get(position).getTitle());
            holder.imageView.setImageResource(R.mipmap.forarrow);

            return convertView;



    }
    static class Holder{
        TextView tv;
        ImageView imageView;
        ImageView iconsub;
    }
}
