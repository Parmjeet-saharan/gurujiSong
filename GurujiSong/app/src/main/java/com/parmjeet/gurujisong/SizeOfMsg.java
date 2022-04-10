package com.parmjeet.gurujisong;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.concurrent.TimeUnit;

class SizeOfMsg {
   Context mContext;
   String key;
   SizeOfMsg(Context mContext,String key){
       this.mContext=mContext;
       this.key=key;
   }

  public void setSharedpreferences(int val,String key1){
      SharedPreferences sharedpreferences =mContext.getSharedPreferences(key,mContext.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedpreferences.edit();
      editor.putInt(key1, val);
      editor.commit();
  }
  public int getSharedPerference(String key1){
      SharedPreferences sharedpreferences =mContext.getSharedPreferences(key,mContext.MODE_PRIVATE);
      int val=sharedpreferences.getInt(key1,101);
      return val;
  }
  public void setDate(String key1, long d1){
      SharedPreferences sharedpreferences =mContext.getSharedPreferences(key,mContext.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedpreferences.edit();
      editor.putLong(key1,d1);
      editor.commit();
  }
  public long getDate(String key1){
      SharedPreferences sharedpreferences =mContext.getSharedPreferences(key,mContext.MODE_PRIVATE);
      Date date = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(8));
      long timeMilli = date.getTime();
      long d1=sharedpreferences.getLong(key1,timeMilli);
      return d1;
  }
  public void setinc(String key1,Integer val){
      SharedPreferences sharedpreferences =mContext.getSharedPreferences(key,mContext.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedpreferences.edit();
      editor.putInt(key1,val);
      editor.commit();
  }
  public int getinc(String key1){
      SharedPreferences sharedpreferences =mContext.getSharedPreferences(key,mContext.MODE_PRIVATE);
      int val=sharedpreferences.getInt(key1,1);
      return val;
  }
  public void setRefresh(String key1, boolean val){
      SharedPreferences sharedpreferences =mContext.getSharedPreferences(key,mContext.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedpreferences.edit();
      editor.putBoolean(key1,val);
      editor.commit();
  }
  public boolean getRefresh(String key1){
      SharedPreferences sharedpreferences =mContext.getSharedPreferences(key,mContext.MODE_PRIVATE);
     boolean val=sharedpreferences.getBoolean(key1,false);
     return  val;
  }
}
