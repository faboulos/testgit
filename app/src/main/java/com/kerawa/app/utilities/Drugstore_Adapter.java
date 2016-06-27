package com.kerawa.app.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerawa.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 Created by kwemart on 06/05/2015.
 */
public class Drugstore_Adapter extends ArrayAdapter<Drugstore> {
    Context activity;
    int layoutResourceId;
    Drugstore drugstore;
    ArrayList<Drugstore> data;
    public Drugstore_Adapter(Context act, int layoutResourceId, ArrayList<Drugstore> data) {
        super(act, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.activity = act;
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StoreHolder holder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            row = inflater.inflate(this.layoutResourceId , parent, false);
            holder = new StoreHolder();
            holder.Name = (TextView) row.findViewById(R.id.name);
            holder.Region = (TextView) row.findViewById(R.id.region);
            holder.city = (TextView) row.findViewById(R.id.city);
            holder.phinelist = (TextView) row.findViewById(R.id.phonelist);
            holder.location = (TextView) row.findViewById(R.id.location);
            holder.phones = (Button) row.findViewById(R.id.phones_button);
            holder.phonesList= (GridView) row.findViewById(R.id.phonegrid);
            holder.Thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
            row.setTag(holder);
        } else {
            holder = (StoreHolder) row.getTag();
        }

        drugstore = data.get(position);
        holder.Name.setText(drugstore.getName());
        holder.city.setText(drugstore.getCity_name());
        holder.Region.setText("("+drugstore.getRegion()+")");
        try {
            if(drugstore.getLocation().split(":")[1].replace(" ","").trim().equals("")||drugstore.getLocation().split(":")[1].length()==1){
                holder.location.setText(getContext().getString(R.string.undefined));
            }else {
                holder.location.setText(drugstore.getLocation().split(":")[1]);
            }
        }catch (Exception ignored){}


        holder.phones.setTag(drugstore.getPhones());
        holder.phinelist.setText(Krw_functions.join(drugstore.getPhones(),","));
        String[] web=new String[drugstore.getPhones().size()],tags=new String[drugstore.getPhones().size()];
        int[] Imageids=new int[drugstore.getPhones().size()];
        for (int i=0;i<drugstore.getPhones().size();i++){
            drugstore.getPhones().get(i);web[i]=drugstore.getPhones().get(i);
            Imageids[i]=R.drawable.ic_pharmacy_call;
            tags[i]=web[i];
        }
        CustomGrid adapter = new CustomGrid(activity,web, Imageids, tags,R.layout.phone_grid);
        holder.phonesList.setAdapter(adapter);
        holder.phonesList.setBackgroundColor(Color.WHITE);

        holder.phonesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                TextView Tv = (TextView) v.findViewById(R.id.grid_text);
                String T = (String) v.getTag();
                String Title = Tv.getText().toString();

                String telURI = "tel:" + T;
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telURI));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               try{
                   activity.startActivity(intent);
               }catch (Exception e) {
                       Krw_functions.Show_Toast(getContext(), e.toString()+"\n"+e.getLocalizedMessage(), true);
               }
            }
        });

        Picasso.with(getContext())
                .load(R.drawable.icon_pharmacy)
                .into(holder.Thumbnail);
        holder.phones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         Krw_functions.Show_Toast(getContext(),v.getTag().toString(),true);
            }
        });
        return row;

    }

   private class StoreHolder {
        TextView Name;
        TextView city;
        TextView Region;
        TextView location;
        Button phones;
        GridView phonesList;
        TextView phinelist;
        ImageView Thumbnail;
    }

}
