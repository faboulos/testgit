package com.kerawa.app.utilities;

import android.view.View;

/**
  Created by kwemart on 27/08/2015.
 */
public class ShowHide {
    View obj;
    public ShowHide(View _obj){
        this.obj=_obj;
    }

    public void Hide(){
        this.obj.setVisibility(View.GONE);
    }
    public void Show(){
        this.obj.setVisibility(View.VISIBLE);

    }

}
