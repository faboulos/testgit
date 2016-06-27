package com.kerawa.app.utilities;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kerawa.app.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by mabian on 2/17/16.
 */
public class ShareAppsAdapter extends BaseAdapter{
    List <PackageInfo> packageList;
    Activity context;
    PackageManager packageManager;


    public ShareAppsAdapter(Activity context, List<PackageInfo> packageList,
                       PackageManager packageManager) {
        super();
        this.context = context;
        this.packageList = packageList;
        this.packageManager = packageManager;

    }

    private class ViewHolder {
        TextView apkName;
        TextView desc;
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
            convertView = inflater.inflate(R.layout.sharedapps, null);
            holder = new ViewHolder();

            holder.apkName = (TextView) convertView
                    .findViewById(R.id.appName);
            holder.desc = (TextView)convertView.findViewById(R.id.description);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        PackageInfo packageInfo = (PackageInfo) getItem(position);

        Drawable appIcon = packageManager
                .getApplicationIcon(packageInfo.applicationInfo);
        String appName = packageManager.getApplicationLabel(
                packageInfo.applicationInfo).toString();
        appIcon.setBounds(0, 0, 50, 50);
        holder.apkName.setCompoundDrawables(appIcon, null, null, null);
        holder.apkName.setCompoundDrawablePadding(15);
        holder.apkName.setText(appName);
            //message to be placed under each application to help user know what to do, can be changed

        String[] des = {"Partager l'annonce à vos amis sur " +appName+". Ils recevront directement le lien",
                "Partager l'annonce à vos amis et/ou vos groupes sur "+appName,
                "Partager l'annonce à vos groupes, fanpages, amis sur "+appName};
        holder.desc.setText(des[position]);
        return convertView;

    }
}
