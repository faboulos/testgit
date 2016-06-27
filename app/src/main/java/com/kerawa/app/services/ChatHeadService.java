package com.kerawa.app.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kerawa.app.R;
import com.kerawa.app.utilities.Krw_functions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatHeadService extends Service {

    private WindowManager windowManager;
    private List<View> chatHeads;
    private LayoutInflater inflater;
    SharedPreferences prefs;
    private String nom;
    ListView liste;
    private ImageView Thumbnail;
    private int closed;
    private String keyword;
    private String image;
    private String lien;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        nom = prefs.getString("Kerawa-count", null);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = LayoutInflater.from(this);
        chatHeads = new ArrayList<View>();


    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        closed=0;
        final View chatHead = inflater.inflate(R.layout.service_chat_head, null);
        final TextView txt_title = (TextView) chatHead.findViewById(R.id.txt_title);
        final TextView txt_text = (TextView) chatHead.findViewById(R.id.txt_text);
        ImageView icone= (ImageView) chatHead.findViewById(R.id.icon_img);
        final RelativeLayout list_loader= (RelativeLayout) chatHead.findViewById(R.id.list_loader);
        Thumbnail= (ImageView) chatHead.findViewById(R.id.photo);
         keyword=intent.getStringExtra("title");
         image=intent.getStringExtra("image");
         lien=intent.getStringExtra("lien");
        txt_text.setText("Chargement de la pub...");
        //txt_text.setText("Resultats");


        /*icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShowHide(list_loader).Show();
                //new ShowHide(txt_title).Hide();
                Picasso.with(getApplicationContext())
                        .load(image)
                        .placeholder(R.drawable.logo2)
                        .into(Thumbnail);
            }
        });*/

        Picasso.with(getApplicationContext())
                .load(image)
                .placeholder(R.drawable.logo2)
                .into(Thumbnail, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        txt_text.setText(keyword);
                        chatHead.findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                windowManager.removeView(chatHead);
                                closed=1;
                                //stopSelf();
                            }
                        });


                        Thumbnail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Krw_functions._Openurl(lien, getApplicationContext());
                                Krw_functions.pushParseAdclickedEvent(getApplicationContext(), lien);
                                removeChatHead(chatHead);
                                closed=1;
                            }
                        });

                    }

                    @Override
                    public void onError() {
                        txt_text.setText("Erreur de chargement");
                        chatHead.findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                windowManager.removeView(chatHead);
                                //stopSelf();
                            }
                        });

                        chatHead.findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                windowManager.removeView(chatHead);
                                closed=1;
                                //stopSelf();
                            }
                        });
                    }
                });





        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, 0, PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;

        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, params);
                        return true;
                }
                return false;
            }
        });

        addChatHead(chatHead, params);

        return START_NOT_STICKY;
    }

    public void addChatHead(View chatHead, WindowManager.LayoutParams params) {
        chatHeads.add(chatHead);
        windowManager.addView(chatHead, params);
    }

    public void removeChatHead(View chatHead) {
        chatHeads.remove(chatHead);
        windowManager.removeView(chatHead);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (View chatHead : chatHeads) {
            removeChatHead(chatHead);
        }
     //   if (closed==0)   getApplicationContext().startService(new Intent(getApplication(), ChatHeadService.class).putExtra("title",keyword).putExtra("image",image).putExtra("lien",lien));

    }

}