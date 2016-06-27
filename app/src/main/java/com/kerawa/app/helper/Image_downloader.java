package com.kerawa.app.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.kerawa.app.R;

import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 Created by martin on 9/21/15.
 */
public class Image_downloader extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private DatabaseHelper myDB ;
    public Image_downloader(ImageView imageView) {
        imageViewReference = new WeakReference<ImageView>(imageView);
    }
    public Image_downloader() {
        imageViewReference = null ;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        return downloadBitmap(params[0],params[1]);
    }


    @Override
    protected void onPreExecute() {
        if(imageViewReference !=null) {
            ImageView imageView = imageViewReference.get();
            Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.default8);
            imageView.setImageDrawable(placeholder);
        }
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(imageViewReference !=null) {
            if (isCancelled()) {
                bitmap = null;
            }

            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);

                }
            }
        }
    }


    private Bitmap downloadBitmap(String url,String ad_id) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap;
                bitmap = BitmapFactory.decodeStream(inputStream);
                //save it into the DataBase
                //enregistrement du fichier
                String path = ".kerawa/"+ad_id+"/thumbnail";
                //save the file to this path
                ImageStorage.saveToSdCard(bitmap,"thumbnail",ad_id);
                return bitmap;
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }




}
