package com.kerawa.app.utilities;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerawa.app.R;
import com.kerawa.app.helper.ImageStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


/**
 Created by kwemart on 06/05/2015.
 */
public class SImilarAdapter extends ArrayAdapter<Annonce> {
    Context activity;
    int layoutResourceId;
    Annonce produit;
    List<Annonce> data;
    private AdsHolder holder=null;
    public SImilarAdapter(Context act, int layoutResourceId, List<Annonce> data) {
        super(act, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.activity = act;
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new AdsHolder();
            holder.title = (TextView) row.findViewById(R.id.title_similar);
            holder.Thumbnail = (ImageView)row.findViewById(R.id.thumbnail_similar);

            row.setTag(holder);
        } else {
            holder = (AdsHolder) row.getTag();
        }
        Log.d("total_similar",data.size()+"");
        produit = data.get(position);

        holder.title.setText(produit.getTitle());

       if (holder.Thumbnail != null) {
                if (!produit.getImage_thumbnail_url().trim().equals(""))
                    Picasso.with(getContext())
                            .load(produit.getImage_thumbnail_url())
                            .placeholder(R.drawable.default8)
                            .into(holder.Thumbnail);
                else

                    Picasso.with(getContext())
                            .load(R.drawable.default8)
                            .into(holder.Thumbnail);

                //new Image_downloader(holder.Thumbnail).execute(produit.getImage_thumbnail_url(), produit.getAdd_id());
            }


        return row;

    }
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

  @Override
  public boolean isEnabled(int position)
  {
      return true;
  }
    class AdsHolder {
        TextView title;
        ImageView Thumbnail;
        }

}
