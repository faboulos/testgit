package com.kerawa.app.utilities;

/**
 * Created by martin on 11/17/15.
 */


import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kerawa.app.R;

import java.util.ArrayList;

public class Listadapter extends BaseAdapter{

    ArrayList<Krw_functions.PhoneContactInfo> packageList;
    Activity context;
    PackageManager packageManager;
    boolean[] itemChecked;

    public Listadapter(Activity context, ArrayList<Krw_functions.PhoneContactInfo> packageList) {
        super();
        this.context = context;
        this.packageList = packageList;
        itemChecked = new boolean[packageList.size()];
    }

    private class ViewHolder {
        TextView userName;
        TextView userNumber;
        CheckBox ck1;
    }

    public int getCount() {
        return packageList.size();
    }

    public Object getItem(int position) {
        return packageList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contacts_item, null);
            holder = new ViewHolder();

            holder.userName = (TextView) convertView.findViewById(R.id.textView1);
            holder.userNumber=(TextView) convertView.findViewById(R.id.textView2);
            holder.ck1 = (CheckBox) convertView.findViewById(R.id.checkBox1);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        Krw_functions.PhoneContactInfo userInfo = (Krw_functions.PhoneContactInfo) getItem(position);

      /*
      Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
      String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
        appIcon.setBounds(0, 0, 40, 40);

        */
        holder.userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mon_compte,0,0,0);
        holder.userNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_phone,0,0,0);
        holder.userName.setCompoundDrawablePadding(15);
        holder.userName.setText(userInfo.getContactName());
        holder.userNumber.setCompoundDrawablePadding(15);
        holder.userNumber.setText(userInfo.getContactNumber());
        holder.ck1.setChecked(false);

        if (itemChecked[position])
            holder.ck1.setChecked(true);
        else
            holder.ck1.setChecked(false);

        holder.ck1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (holder.ck1.isChecked())
                    itemChecked[position] = true;
                else
                    itemChecked[position] = false;
            }
        });

        return convertView;

    }



}
