package com.parmjeet.gurujisong;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadService extends Service {
    String file_name;
    String uri;
    Context context=this;
    Bundle bundle;
    int diff;
    int cl;
    private static final String TAG = "MyTag";

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: in download service    "+diff);
        if(intent!=null) {

            bundle = intent.getExtras();
            if (bundle != null && bundle.containsKey("filenmane") && bundle.containsKey("link")) {
                file_name = (String) bundle.get("filenmane");
                uri = bundle.getString("link");
                new DownloadBack().execute(uri);
            }
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }

        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: downloding complete ################################");
        super.onDestroy();
    }

   class DownloadBack extends AsyncTask<String, Void, Boolean>{

       @Override
       protected Boolean doInBackground(String... ur) {
           Log.d(TAG, "doInBackground: in downlad service");
           try {
               String edit="save_"+file_name;
               File file = context.getFileStreamPath(file_name);
               File file2 = context.getFileStreamPath(edit);
             /*  if (( file2.exists() || file.exists()) && diff==100)   {
                   diff=100;
                   onDestroy();
                  return true;
               }*/
               try {
//                File mydir = context.getDir("downloaded song", Context.MODE_PRIVATE); //Creating an internal dir;
//               File fileWithinMyDir = new File(mydir,file_name ); //Getting a file within the dir.
//               FileOutputStream fOut =new FileOutputStream(fileWithinMyDir);
                   FileOutputStream fOut;
                   fOut = context.openFileOutput(edit, context.MODE_PRIVATE);

                   String imageURL =ur[0];
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
                   Log.d(TAG, "downloadservice: called  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                   while ((count = inputStream.read(data)) != -1) {
                       total += count;
                        diff=(int) ((total*100)/connLength);
                       Log.d(TAG, "onStartCommand of download: is %"+diff);
                       fOut.write(data, 0, count);
                   }
                   fOut.flush();
                   fOut.close();
                   inputStream.close();



               } catch (FileNotFoundException | MalformedURLException e) {
                   e.printStackTrace();

               } catch (IOException e) {
                   e.printStackTrace();

               }
           } catch (Exception e) {
               e.printStackTrace();

           }
           return null;
       }

       @Override
       protected void onPostExecute(Boolean aBoolean) {
           super.onPostExecute(aBoolean);
           Log.d(TAG, "onPostExecute:  in download service");
         /* if(diff<100){
               String edit1="save_"+file_name;
               File file = new File(context.getFilesDir(), edit1);
               if(file.exists()){
                   file.delete();
               }
         //           onDestroy();
           }*/
       }
   }
}
