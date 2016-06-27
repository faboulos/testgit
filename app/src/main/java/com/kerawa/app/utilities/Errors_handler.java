package com.kerawa.app.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kerawa.app.R;

/**
 Created by martin on 11/13/15.
 */
public class Errors_handler {
public Activity ctx;
private Context context;
public String  error_text;
public boolean autoHide;
public TextView statusText;
public int imgResource;
private int DisplayHeight;
    DisplayMetrics display;
//    CountryTitle.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
final Animation slideUp;
final Animation  slideDown;
public Errors_handler(Activity ct,Context ctx2,String erT,boolean ah,TextView st,int icon){
    this.ctx= ct;
    this.autoHide=ah;
    this.error_text=erT;
    this.statusText=st;
    this.imgResource=icon;
    this.context=ctx2;
    slideUp= AnimationUtils.loadAnimation(context, R.anim.slide_up);
    slideDown=  AnimationUtils.loadAnimation(context, R.anim.slide_down);
    display = this.context.getResources().getDisplayMetrics();
    DisplayHeight = display.heightPixels;
}

public void execute(long sd,long hd,long wd){
    //sd=show duration,hd=hide duration,wd=wait duration
    statusText.setText(error_text);
    slideDown.setDuration(sd);
    slideUp.setDuration(hd);
    statusText.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
    slideDown.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation arg0) {
            (new ShowHide(statusText)).Show();
            RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lparam.setMargins(15, 0, 15, 15);
            statusText.setLayoutParams(lparam);
            statusText.bringToFront();
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lparam.setMargins(15,(40*DisplayHeight/100), 15, 15);
            statusText.setLayoutParams(lparam);
        }
    });

    slideUp.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation arg0) {
            RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lparam.setMargins(15, 0, 15, 15);
            statusText.setLayoutParams(lparam);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            (new ShowHide(statusText)).Hide();
            RelativeLayout.LayoutParams lparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lparam.setMargins(15, 0, 15, 15);
            statusText.setLayoutParams(lparam);
        }
    });

    statusText.startAnimation(slideDown);
 if(autoHide){
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            statusText.startAnimation(slideUp);
        }
    }, wd);
  }else{
     String closeText="\r\n--Cliquer pour fermer--";
     String currentText=statusText.getText().toString();
     statusText.setText(currentText+closeText);
     statusText.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             statusText.startAnimation(slideUp);
         }
     });

 }

}

}
