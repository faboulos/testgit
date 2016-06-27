package com.kerawa.app.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerawa.app.R;

import java.util.ArrayList;

/**
 Created by martin on 10/14/15.
 */
public class Spinner_adapter extends ArrayAdapter<Model> {
Context ctx;
ArrayList<Model> spinnerValues;
int resourceId;
    public Spinner_adapter(Context ctx, int layoutresourceID, ArrayList<Model> objects) {
        super(ctx, layoutresourceID, objects);
        this.ctx=ctx;
        this.spinnerValues=objects;
        this.resourceId=layoutresourceID;
        notifyDataSetChanged();
    }

    @Override public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View mySpinner = inflater.inflate(R.layout.single_spinner, parent, false);
        TextView main_text = (TextView) mySpinner.findViewById(R.id.text_main_seen);
        main_text.setText(spinnerValues.get(position).getTitle());
        ImageView left_icon = (ImageView) mySpinner.findViewById(R.id.left_pic);
        if(spinnerValues.get(position).getIcon() > 0) {
            left_icon.setImageResource(spinnerValues.get(position).getIcon());
            new ShowHide(left_icon).Show();
        }else{
            new ShowHide(left_icon).Hide();
        }
        mySpinner.setTag(spinnerValues.get(position).getTag());
        return mySpinner;
    }
}
