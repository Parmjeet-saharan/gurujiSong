package com.parmjeet.gurujisong;

import com.chartboost.sdk.Chartboost;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.app.Service;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import android.app.Notification;
import android.app.NotificationManager;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ExoPlayer extends AppCompatActivity {
    int j;
    PlayerControlView playerView;
    private SimpleExoPlayer player;
    private AdView mAdView;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private static final String TAG = "MyTag";
    private boolean mBound = false;
    private MyService myService;
    ArrayList<songList> solist=new ArrayList<songList>() ;
    ArrayList oflineList=new ArrayList();
    private Intent intent;
    private ServiceConnection mConection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyServiceBinder binder= (MyService.MyServiceBinder) iBinder;
            myService=binder.getService();
            mBound=true;
            initializePlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exo_player);
        playerView=(PlayerControlView) findViewById(R.id.video_view);
        mAdView = findViewById(R.id.adView);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Intent i=getIntent();
        Bundle b=i.getExtras();
        intent = new Intent(this, MyService.class);
        if(b!=null && b.containsKey("link"))
        {
         //   String songLists= (String) b.get("link");
             j = (int) b.get("link");
            solist=(ArrayList) b.getSerializable("arrray");

            if(isMyServiceRunning(MyService.class)){
                stopService(new Intent(ExoPlayer.this,MyService.class));
            }

            intent.putExtra("link", j);
            intent.putExtra("array",solist);
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            }else {*/
                startService(intent);

           /* playerView.setUseController(true);
            playerView.showController();
            playerView.setControllerAutoShow(true);
            playerView.setControllerHideOnTouch(false);*/
        //    Toast.makeText(getApplicationContext(),"!!!!!!!!!!!!!!"+j,Toast.LENGTH_SHORT).show();
        }else if(b!=null && b.containsKey("posstion")){
            j = (int) b.get("posstion");
            oflineList=b.getStringArrayList("arrray");
            Log.d(TAG, "onCreate: exoplayer @@@@@@@@@@@@@@@@@@    "+oflineList.size());
            Log.d(TAG, "onCreate: exoplayer "+oflineList.get(0));
          //  Toast.makeText(getApplicationContext(),"send @@@@@@@@!!!!1!"+oflineList.get(0),Toast.LENGTH_SHORT).show();
            if(isMyServiceRunning(MyService.class)){
                stopService(new Intent(ExoPlayer.this,MyService.class));
            }
            intent.putExtra("posstion", j);
            intent.putStringArrayListExtra("array", oflineList);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(intent);
//            }

                startService(intent);
           /* playerView.setUseController(true);
            playerView.showController();
            playerView.setControllerAutoShow(true);
            playerView.setControllerHideOnTouch(false);*/

        }
;

      //  Util.startForegroundService(this, intent);


    }
    @Override
    public void onStart() {
        super.onStart();
        bindService(intent, mConection, Context.BIND_AUTO_CREATE);
        initializePlayer();
        if (Util.SDK_INT >= 24) {
           initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
           initializePlayer();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
    //        releasePlayer();
        }
    }

    @Override
    public void onStop() {
        unbindService(mConection);
        mBound = false;
        super.onStop();
        if (Util.SDK_INT >= 24) {
        //    releasePlayer();
        }
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
    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
//    private void releasePlayer() {
//        if (player != null) {
//            playWhenReady = player.getPlayWhenReady();
//            playbackPosition = player.getCurrentPosition();
//            currentWindow = player.getCurrentWindowIndex();
//            player.release();
//            player = null;
//        }
//    }
//    private void initializePlayer() {
//        player = ExoPlayerFactory.newSimpleInstance(this);
//        playerView.setPlayer(player);
//        if(j!=null) {
//            Uri uri = Uri.parse(j);
//            MediaSource mediaSource = buildMediaSource(uri);
//            player.setPlayWhenReady(playWhenReady);
//            player.seekTo(currentWindow, playbackPosition);
//            player.prepare(mediaSource, false, false);
//        }
//
//    }
//    private MediaSource buildMediaSource(Uri uri) {
//        DataSource.Factory dataSourceFactory =
//                new DefaultDataSourceFactory(this, "exoplayer-codelab");
//        return new ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(uri);
//    }
private void initializePlayer() {
    if (mBound) {
        SimpleExoPlayer player = myService.getplayerInstance();
        playerView.setPlayer(player);
    }

}
@Override
public void onDestroy() {

  //  stopForeground(true);
 //   stopService(intent);
    Log.d(TAG, "onDestroy: exoplayer !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    super.onDestroy();
}
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
