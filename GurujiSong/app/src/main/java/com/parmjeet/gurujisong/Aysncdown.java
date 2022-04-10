package com.parmjeet.gurujisong;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

class Aysncdown extends AsyncTask<String, Void, Boolean> {
       public interface Adinterface{
           void onItemsClick();
       }
    private Adinterface onItemsClickListener;
    Context context;
    String file_name;
    int cl;
    Aysncdown(Context context,String file_name ,Adinterface listener,int cl ){
    this.context=context;
    this.file_name=file_name;
   this.onItemsClickListener=listener;
   this.cl=cl;
    }
    ProgressDialog mProgressDialog;
    private static final String TAG = "MyTag";
    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (cl==1) {
            File file = context.getFileStreamPath(file_name);
            if (file.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("This song already exist")
                        .setTitle("DUPLICATE COPY");
                builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
            mProgressDialog = new ProgressDialog(context);
            // Set your progress dialog Title
            mProgressDialog.setTitle("Downloading");
            // Set your progress dialog Message
            mProgressDialog.setMessage("Downloading, Please Wait!");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // Show progress dialog
            mProgressDialog.show();
        }

    }
    @Override
    protected Boolean doInBackground(String... ur) {
        Log.d(TAG, "doInBackground: called");
        try {
            String edit="save_"+file_name;
            File file = context.getFileStreamPath(file_name);
            File file2 = context.getFileStreamPath(edit);

            if ( file2.exists())  {
                if (cl==1) {
                    Boolean success = file2.renameTo(file);
                }
                return true;
            }
            if ( file.exists())  {
                return true;
            }

            try {

//                File mydir = context.getDir("downloaded song", Context.MODE_PRIVATE); //Creating an internal dir;
//               File fileWithinMyDir = new File(mydir,file_name ); //Getting a file within the dir.
//               FileOutputStream fOut =new FileOutputStream(fileWithinMyDir);
                FileOutputStream fOut;
                if (cl==2) {
                     fOut = context.openFileOutput(edit, context.MODE_PRIVATE);
                }else {
                     fOut = context.openFileOutput(file_name, context.MODE_PRIVATE);
                }
                String imageURL = ur[0];
                URL url = new URL(imageURL);
                URLConnection conn = url.openConnection();
             //   HttpURLConnection conn = (HttpURLConnection) url.openConnection();
           //     conn.setRequestMethod("GET");
        //        conn.setDoOutput(true);
                conn.connect();

      //          int responseCode = conn.getResponseCode();

                int connLength = conn.getContentLength();
                InputStream inputStream = conn.getInputStream();
                byte[] data = new byte[connLength];
                long total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    int diff=(int) ((total*100)/connLength);
                    if (cl==1) {
                        mProgressDialog.setProgress(diff);
                    }
                    fOut.write(data, 0, count);
                }
                fOut.flush();
                fOut.close();
                inputStream.close();
                return true;
            } catch (FileNotFoundException | MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        //   Toast.makeText(context,imageURL+"async!!!!!!!!!!!!!",Toast.LENGTH_SHORT).show();
      /*  Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;*/
    }

    @Override
    protected void onPostExecute(Boolean bitmap) {
       Log.d(TAG, "onPostExecute: called"+bitmap);
        if (cl==1) {
            mProgressDialog.dismiss();
        }
        onItemsClickListener.onItemsClick();


    }


}
