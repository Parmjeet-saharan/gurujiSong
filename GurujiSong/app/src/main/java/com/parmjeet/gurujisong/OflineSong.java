package com.parmjeet.gurujisong;
import com.chartboost.sdk.Chartboost;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OflineSong extends AppCompatActivity {
Button button;
RecyclerView recyclerView;
   String[] files;
   ArrayList list=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofline_song);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerViewOfline);
        button=(Button) findViewById(R.id.online);
        files=this.fileList();
  //   list.addAll(Arrays.asList(files));
        for(int i=0;i<files.length;i++){
            if((files[i].endsWith("mp3")  || files[i].endsWith("MP3"))  &&  !(files[i].startsWith("save_"))){
                list.add(files[i]);
            }
        }
        if(list.size()==0){
             list.add("YOU DID NOT DOWNLOAD ANY SHABAD STILL :FOR DOWNLOAD SHABAD CLICK ON CIRCLE ICON RIGHT SIDE OF EACH SHABAD IN ONLINE ACTIVITY");
        }
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
             AdView mAdView;
          MobileAds.initialize(this, new OnInitializationCompleteListener() {
              @Override
              public void onInitializationComplete(InitializationStatus initializationStatus) {

              }
          });
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
//        TODO: Add adView to your view hierarchy.
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
   //                   Toast.makeText(getApplicationContext(),"!!!!!!!!!!!!!!"+files[0],Toast.LENGTH_SHORT).show();
        OflineAdapter customAdapter = new OflineAdapter(OflineSong.this, list );
        recyclerView.setAdapter(customAdapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(OflineSong.this,MainActivity.class);
                startActivity(in);

            }
        });
    }
    @Override
    public void onBackPressed() {
        // If an interstitial is on screen, close it.
        if (Chartboost.onBackPressed()) {
            return;
        } else {
            super.onBackPressed();
        }
    }

}