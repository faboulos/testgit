package com.kerawa.app.utilities;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kerawa.app.R;

import java.util.List;

public class QrDetailAdapter extends RecyclerView.Adapter<QrDetailAdapter.ContactViewHolder> {

    private List<CardDetails> contactList;

    public QrDetailAdapter(List<CardDetails> contactList) {
        this.contactList = contactList;

    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        CardDetails ci = contactList.get(i);
        contactViewHolder.vName.setText(ci.getDetails());
        contactViewHolder.vTitle.setText(ci.getTitle());
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView vName;
        protected TextView vTitle;

        public ContactViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.txtName);
            vTitle = (TextView) v.findViewById(R.id.title);
        }
    }
}