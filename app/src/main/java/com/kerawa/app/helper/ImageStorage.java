package com.kerawa.app.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class ImageStorage {


    public static String saveToSdCard(Bitmap bitmap, String filename,String adsID) {

        String stored = null;
        if(bitmap!=null) {


            File sdcard = Environment.getExternalStorageDirectory();

            File folder = new File(sdcard.getAbsoluteFile(), ".kerawa");//the dot makes this directory hidden to the user
            folder.mkdir();
            File adsDir = new File(folder.getAbsoluteFile(), adsID);
            adsDir.mkdir();
            File file = new File(adsDir.getAbsoluteFile(), filename);
            if (file.exists())
                return stored;

            try {
                FileOutputStream out = new FileOutputStream(file);


                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                out.flush();
                out.close();
                stored = "success";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stored;
    }


    public static File getImage(String imagename,String adsID) {

        File mediaImage = null;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root);
            if (!myDir.exists())
                return null;

            mediaImage = new File(myDir.getPath() + "/.kerawa/"+adsID+"/"+imagename);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mediaImage;
    }
    public static boolean checkifImageExists(String imagename,String adsID){
        Bitmap b = null ;
        File file = ImageStorage.getImage("/"+imagename,adsID);
        String path = null;
        if (file != null) {
            path = file.getAbsolutePath();
        }

        if (path != null)
            b = BitmapFactory.decodeFile(path);


        return b!=null ;
    }

    public static void createStorage()
    {
        File sdcard = Environment.getExternalStorageDirectory() ;

        //delete the existing .kerawa file if it exists
        try{
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root+"/.kerawa");
            if (myDir.exists())
                myDir.delete();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //create a new .kerawa file inside
        File folder = new File(sdcard.getAbsoluteFile(), ".kerawa");
        folder.mkdir();
    }
    public static void createStorageTemp()
    {
        File sdcard = Environment.getExternalStorageDirectory() ;

        //delete the existing .kerawa file if it exists
        try{
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File myDir = new File(sdcard.getAbsoluteFile(), ".kerawa_temp");
            if (myDir.exists())
                myDir.delete();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //create a new .kerawa file inside SD card
        File folder = new File(sdcard.getAbsoluteFile(), ".kerawa_temp");
        folder.mkdir();
    }

    public static void DeleteFile(String filename)
    {
        try{
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File myDir = new File(root+"/"+filename);
            if (myDir.exists())
                myDir.delete();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void RenameFile(String oldname,String newname)
    {
        //delete the existing .kerawa file if it exists
        try{
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root+"/"+oldname);
            File newDir = new File(root+"/"+newname);
            if (myDir.exists())//a chercher
                myDir.renameTo(newDir);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}