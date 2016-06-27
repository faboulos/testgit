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

/**
 * Created by etinge mabian on 3/16/16.
 */
public class SubCatSAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Model> data;
    private static LayoutInflater inflater = null;
    Integer icon;
    public SubCatSAdapter(Context c, ArrayList<Model> d, String categoryTitle){
        this.context = c;
        this.data = d;
        switch (categoryTitle){
            case "A vendre":
                this.icon = R.drawable.sale;
                break;
            case "Automobile":
                this.icon  = R.drawable.auto;
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
            case "--- Sélectionner ---":
                this.icon = R.mipmap.ic_down;
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
            convertView = inflater.inflate(R.layout.single_spinner, parent, false);
            TextView tv = (TextView) convertView.findViewById(R.id.text_main_seen);
            tv.setText(data.get(position).getTitle());
            ImageView iconsub = (ImageView)convertView.findViewById(R.id.left_pic);
            iconsub.setImageResource(icon);
            return convertView;



    }

}
