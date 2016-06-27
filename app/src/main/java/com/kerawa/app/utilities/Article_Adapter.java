package com.kerawa.app.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerawa.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 Created by kwemart on 06/05/2015.
 */
public class Article_Adapter extends ArrayAdapter<Article> {
    Context activity;
    int layoutResourceId;
    Article article;
    List<Article> data = new ArrayList<>();


    public Article_Adapter(Context act, int layoutResourceId, List<Article> data) {
        super(act, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.activity = act;
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ArticleHolder holder = null;
        article=new Article();
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ArticleHolder();
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.description=(TextView) row.findViewById(R.id.price2);
            new ShowHide(holder.description).Hide();
            holder.date = (TextView) row.findViewById(R.id.date1);
            holder.Thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
            holder.Art_ID = (TextView) row.findViewById(R.id.ads_id);
            row.setTag(holder);
        } else {
            holder = (ArticleHolder) row.getTag();
        }
        article = data.get(position);
        holder.title.setText(article.getTitle());
        // holder.Author.setText(article.getAuthor_name());
        holder.date.setText(article.getDate());
        holder.description.setText(article.getArt_plain_description());
        // if(article.getImage_thumbnail_url()!=null)  holder.Thumbnail.setImageURI(Uri.parse(article.getImage_thumbnail_url()));
        if (article.getImage_thumbnail_url()!= null&&holder.Thumbnail!= null) {
            Picasso.with(getContext())
                    .load(article.getImage_thumbnail_url())
                    .placeholder(R.drawable.default8)
                    .into(holder.Thumbnail);
            //new Image_downloader(holder.Thumbnail).execute(article.getImage_thumbnail_url(),null);
        }



        return row;

    }

    class ArticleHolder {
        TextView title;
        TextView description;
        TextView date;
        TextView Author;
        ImageView Thumbnail;
        TextView Art_ID;
    }

}
