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
  Created by martin on 9/24/15.
 */
public class Ads_Form_Grid_adapter extends ArrayAdapter<ads_Model> {
    private int resourceId;
    private Context mContext;
    ArrayList<ads_Model> objects;
    public Ads_Form_Grid_adapter(Context c, int resourceId, ArrayList<ads_Model> _objects) {
        super(c,resourceId,_objects);
        this.resourceId=resourceId;
        this.mContext = c;
        this.objects=_objects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return objects.size();
    }

    @Override
    public ads_Model getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = inflater.inflate(resourceId, null);

        } else {
            grid = convertView;
        }

        ads_Model item=objects.get(position);
        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
        textView.setText(item.getTitle());
        imageView.setImageBitmap(item.getIcon());
        grid.setTag(item.getTag());
        return grid;
    }
}

