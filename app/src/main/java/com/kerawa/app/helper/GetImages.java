package com.kerawa.app.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;

import com.kerawa.app.utilities.Krw_functions;
import com.kerawa.app.utilities.ShowHide;

import java.net.URL;
import java.net.URLConnection;


/**
 Created by kwemart on 09/09/2015.
 */

public class GetImages extends AsyncTask<Object, Object, Object> {
    public String requestUrl, imagename_;
    private Bitmap bitmap ;
    public Context ctx;
    private ShowHide vx;
    public View v;
    public String adsID;
    public Boolean bol = true ;
    public GetImages(String requestUrl,  String _imagename_,String adsid,Boolean bol) {
        this.requestUrl = requestUrl;
        this.imagename_ = _imagename_ ;
        this.adsID=adsid;
        this.bol = bol ;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       if (v!=null){
           vx=new ShowHide(v);
           vx.Show();
       }

    }

    @Override
    protected Object doInBackground(Object... objects) {
        try {
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (v!=null){
            vx.Hide();
        }
        if (!ImageStorage.checkifImageExists(imagename_, adsID)) {
            ImageStorage.saveToSdCard(bitmap, imagename_, adsID);
        }
        if (bol) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(ImageStorage.getImage(imagename_, adsID));
                String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                i.setDataAndType(uri, mimetype);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Krw_functions.Show_Toast(ctx, uri.toString(), true);
                ctx.startActivity(i);
            } catch (Exception e) {
                Krw_functions.Show_Toast(ctx, e.toString(), true);
            }
        }
    }
  }
