package com.kerawa.app.utilities;

/**
 Created by Fifty on 2015-07-12.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerawa.app.R;
import com.kerawa.app.helper.Image_downloader;

import java.util.List;



public class CustomListAdapter extends BaseAdapter {
    private Context activity;
    private LayoutInflater inflater;
    private List<Annonce> movieItems;



    public CustomListAdapter(Context activity, List<Annonce> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;

    }
    public void updateReceiptsList(List<Annonce> newlist) {
        movieItems.clear();
        movieItems.addAll(newlist);
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, parent,false);
        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
               //thumbNail.set(R.drawable.default8);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView city = (TextView) convertView.findViewById(R.id.city1);
        TextView date = (TextView) convertView.findViewById(R.id.date1);
        TextView price = (TextView) convertView.findViewById(R.id.price2);


        // getting movie data for the row
        Annonce m = movieItems.get(position);
        new Image_downloader(thumbNail).execute(m.getImage_thumbnail_url(),m.getAdd_id());

        // title
        title.setText(m.getTitle());
        city.setText(m.getCity_name());
        date.setText(m.getDate());
        price.setText("Prix : "+m.getPrice());
        //price.setText(m.getPrice());
        return convertView;
    }

}