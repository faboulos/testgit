package com.kerawa.app.utilities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kerawa.app.R;
import com.kerawa.app.helper.ImageStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


/**
 Created by kwemart on 06/05/2015.
 */
public class Ads_Adapter extends ArrayAdapter<Annonce> {
    Context activity;
    int layoutResourceId;
    Annonce produit;
    List<Annonce> data;
    private ScaleGestureDetector scaleGDetector;
    private float scale = 1f;
    private AdsHolder holder = null;
    private static final String TAG = "Touch";

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    public Ads_Adapter(Context act, int layoutResourceId, List<Annonce> data) {
        super(act, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.activity = act;
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        scaleGDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new AdsHolder();
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.city = (TextView) row.findViewById(R.id.city1);
            holder.date = (TextView) row.findViewById(R.id.date1);
            holder.price = (TextView) row.findViewById(R.id.price2);
            holder.Thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
            holder.AdsID = (TextView) row.findViewById(R.id.ads_id);
            holder.vipthumb = (ImageView) row.findViewById(R.id.img_vip);

            row.setTag(holder);
        } else {
            holder = (AdsHolder) row.getTag();
                                                                                                                                                    }

        produit = data.get(position);
        String enh = produit.getEhanced();
        holder.title.setText(produit.getTitle());
        holder.city.setText(produit.getCity_name());
        holder.date.setText(produit.getDate());
        if (!produit.getPrice().trim().equalsIgnoreCase("") && !produit.getPrice().trim().equalsIgnoreCase("null")) {
            holder.price.setText("Prix: " + produit.getPrice());
        } else {
            holder.price.setText("Prix: " + "N/A");
        }


        if (enh != null && enh.equals("vip")) {
            holder.vipthumb.setId(Integer.parseInt(produit.getAdd_id()));
            ImageView new_vip = (ImageView) row.findViewById(Integer.parseInt(produit.getAdd_id()));
            Picasso.with(getContext())
                    .load(R.drawable.vip)
                    .into(new_vip);
            new ShowHide(new_vip).Show();
            row.setBackgroundColor(activity.getResources().getColor(R.color.grey_200));
        } else if (enh == null) {
            new ShowHide(holder.vipthumb).Hide();
            row.setBackgroundColor(activity.getResources().getColor(R.color.textPrimary));
        }
        if (Krw_functions.isConnected(getContext())) {
            if (holder.Thumbnail != null) {
                if (!produit.getImage_thumbnail_url().trim().equals(""))
                    Picasso.with(getContext())
                            .load(produit.getImage_thumbnail_url())
                            .placeholder(R.drawable.default8)
                            .into(holder.Thumbnail);
                else

                    Picasso.with(getContext())
                            .load(R.drawable.default8)
                            .into(holder.Thumbnail);

                //new Image_downloader(holder.Thumbnail).execute(produit.getImage_thumbnail_url(), produit.getAdd_id());
            }
        } else {
            String path = "";

            if (ImageStorage.checkifImageExists(produit.getImage_thumbnail_url(), produit.getAdd_id())) {
                try {


                    path = ImageStorage.getImage(produit.getImage_thumbnail_url(), produit.getAdd_id()).getAbsolutePath();
                    Log.d("thumbnail_path", path);
                    File f = new File(path);
                    //holder.Thumbnail.setImageBitmap(BitmapFactory.decodeFile(path));  //ImageStorage.getImage("thumbnail",produit.getAdd_id());
                    if (!path.trim().equalsIgnoreCase(""))
                        Picasso.with(getContext())
                                .load(f)
                                .placeholder(R.drawable.default8)
                                .into(holder.Thumbnail);
                    else
                        Picasso.with(getContext())
                                .load(R.drawable.default8)
                                .into(holder.Thumbnail);


                } catch (Exception ex) {
                    Picasso.with(getContext())
                            .load(R.drawable.default8)
                            .into(holder.Thumbnail);
                }
            } else {
                //load default path

                holder.Thumbnail.setImageResource(R.drawable.default8);  //ImageStorage.getImage("thumbnail",produit.getAdd_id());

            }
        }
/*        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent i = new Intent(getContext().getApplicationContext(), DetailsActivity.class);
                i.putExtra("title", produit.getTitle());
                i.putExtra("price", produit.getPrice());
                i.putExtra("cat", produit.getCategory_name());
                i.putExtra("date",produit.getDate());
                i.putExtra("ville",produit.getCity_name());
                i.putExtra("ad_url",produit.getAd_url());
                i.putExtra("phone1", produit.getPhone1().replace("\\D+", ""));
                i.putExtra("phone2", produit.getPhone2().replace("\\D+", ""));
                activity.startActivity(i);
            }
     });*/
//        holder.Thumbnail.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                ImageView view = (ImageView) v;
//                // make the image scalable as a matrix
//                view.setScaleType(ImageView.ScaleType.MATRIX);
//                float scale;
//
//                // Handle touch events here...
//                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//
//                    case MotionEvent.ACTION_UP: //first finger down only
//                        savedMatrix.set(matrix);
//                        start.set(event.getX(), event.getY());
//                        Log.d(TAG, "mode=DRAG");
//                        mode = DRAG;
//                        final Dialog dialog = new Dialog(activity);
//                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                       // int resid = v.getId();
//                       // Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), resid);
//                       // Drawable draw = new BitmapDrawable(activity.getResources(),bmp);
//                        ImageView img = new ImageView(activity);
//                        img.setImageDrawable(((ImageView) v).getDrawable());
//                        LinearLayout.LayoutParams vp =
//                                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                        GridLayout.LayoutParams.MATCH_PARENT);
//                        img.setLayoutParams(vp);
//
//                        DisplayMetrics display = activity.getResources().getDisplayMetrics();
//                        int DisplayHeight=display.heightPixels;
//                        int DisplayWidth=display.widthPixels;
//
//                        img.getLayoutParams().height = DisplayHeight/3;
//                        img.getLayoutParams().width  = DisplayWidth ;
//                        dialog.setContentView(img);
//                        //dialog.r
//                        dialog.show();
//                        break;
//
//
//                    case MotionEvent.ACTION_POINTER_DOWN: //second finger down
//                        /*oldDist = spacing(event); // calculates the distance between two points where user touched.
//                        Log.d(TAG, "oldDist=" + oldDist);
//                        // minimal distance between both the fingers
//                        if (oldDist > 5f) {
//                            savedMatrix.set(matrix);
//                            midPoint(mid, event); // sets the mid-point of the straight line between two points where user touched.
//                            mode = ZOOM;
//                            Log.d(TAG, "mode=ZOOM" );
//                        }*/
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//                        /*if (mode == DRAG)
//                        { //movement of first finger
//                            matrix.set(savedMatrix);
//                            if (view.getLeft() >= -392)
//                            {
//                                matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
//                            }
//                        }
//                        else if (mode == ZOOM) { //pinch zooming
//                            float newDist = spacing(event);
//                            Log.d(TAG, "newDist=" + newDist);
//                            if (newDist > 5f) {
//                                matrix.set(savedMatrix);
//                                scale = newDist/oldDist; //thinking I need to play around with this value to limit it**
//                                matrix.postScale(scale, scale, mid.x, mid.y);
//                            }
//                        }*/
//                        break;
//                }
//
//                // Perform the transformation
//                view.setImageMatrix(matrix);
//
//                return true; // indicate event was handled
//
//            }
//        });
//
        return row;


    }



    class AdsHolder {
        TextView title;
        TextView city;
        TextView date;
        TextView price;
        ImageView Thumbnail;
        TextView AdsID;
        ImageView vipthumb;
    }


    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener{

        public boolean onScaleBegin(ScaleGestureDetector sgd){
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector sgd){

        }

        public boolean onScale(ScaleGestureDetector sgd){

            // Multiply scale factor
            scale*= sgd.getScaleFactor();
            // Scale or zoom the imageview


            Log.i("Main",String.valueOf(scale));
            return true;
        }

    }
}
