package com.parmjeet.gurujisong;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

class CacheClass {
    public static final  String TAG=" cache";
    public static void writeObject(Context context, String key, Object object,int val) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
  /*      ArrayList<songList> sList = new ArrayList<songList>();
        Log.d(TAG, "writeObject: " +val);
        if(val>1) {
          try {
              sList = (ArrayList<songList>) CacheClass.readObject(context, "OFLINE");
          } catch (ClassNotFoundException e) {
              e.printStackTrace();
          }
      }
        Log.d(TAG, "writeObject: " +sList);
        Toast.makeText(context," try to witte     "+sList,Toast.LENGTH_SHORT).show();

        if(!(sList.contains(object))){
            Log.d(TAG, "writeObject: is writte");
            Toast.makeText(context," writte successful     "+object,Toast.LENGTH_SHORT).show();*/
             ObjectOutputStream oos = new ObjectOutputStream(fos);
             oos.writeObject(object);
             oos.close();
     //   }

        fos.close();
    }
    public static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        Log.d(TAG, "readObject: ");
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        fis.close();
        ois.close();
        return object;
    }
}
