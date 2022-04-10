package com.parmjeet.gurujisong;
import android.app.Notification;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Binder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class MyService extends Service implements Aysncdown.Adinterface, RewardedVideoAdListener {
    private SimpleExoPlayer player;
    private RewardedVideoAd mRewardedVideoAd;
    private static final String CHANNEL_ID="playback_chanal";
    private static final int CHANNEL_NAME=R.string.chanal_id;
    private static final int channelDescription=1000;
    private static final int NOTIFICATION_ID=100;
    private static final int stop=1;

    int j;
    private InterstitialAd mInterstitialAd;
    int count=0;
    int lastWindowIndex = 0;

    Bundle b=null;
    int size;
    ArrayList<songList> solist =new ArrayList<songList>();
    ArrayList oflinelist=new ArrayList<String>();
    private static final String TAG = "MyTag";
    private boolean playWhenReady = true;
    int repeat;
    private int currentWindow = 0;
   private Notification mNotification;
    private int mNotificationId;
    private long playbackPosition = 0;
    private PlayerNotificationManager playerNotificationManager;
    PendingIntent notifyPendingIntent;
    private final IBinder mBinder = new MyServiceBinder();
 //    MostPlay mostPlay=new MostPlay(this);
    MainActivity mainActivity=new MainActivity();
    
    @Override
    public void onCreate() {
  //      Toast.makeText(getApplicationContext(),"oncreat !!!!1!",Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onCreate: start");
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
  //     Toast.makeText(getApplicationContext(),"onStartCommand !!!!1!",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStartCommand: start  "+solist.size());
        if(intent!=null) {
            b = intent.getExtras();
            if (b != null && b.containsKey("link")) {
                // item = b.getParcelable(AppConstants.ITEM_KEY);
                j = (int) b.get("link");
                solist = (ArrayList) b.get("array");
                size = solist.size();
                //       Toast.makeText(getApplicationContext(),"onStartCommand !!!!1!"+j,Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onStartCommand: link is " + j);
            } else if (b != null && b.containsKey("posstion")) {
                j = (int) b.get("posstion");
                oflinelist = b.getStringArrayList("array");
                Log.d(TAG, "onStartCommand: service ##############################################################      " + oflinelist.size());
                //        Toast.makeText(getApplicationContext(),"onStartCommand !!!!1!"+j,Toast.LENGTH_SHORT).show();
                size = oflinelist.size();
            } else {
                //      Toast.makeText(getApplicationContext(),"j is null !!!!1!",Toast.LENGTH_SHORT).show();

            }
            if (player == null) {
                startPlayer();
            } else {
                releasePlayer();
                startPlayer();
            }
            Intent notifyIntent = new Intent(this, MainActivity.class);
// Set the Activity to start in a new, empty task
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Create the PendingIntent
            notifyPendingIntent = PendingIntent.getActivity(
                    this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );
            if (solist.size() > 2) {
                repeat = solist.get(j).getNo_of_time() + 1;
                solist.get(j).setNo_of_time(repeat);
                update();
                if (repeat > 5) {
         //           Toast.makeText(getApplicationContext(), "song no is  " + j + "  " + repeat, Toast.LENGTH_SHORT).show();
                    servceStart(j);
//                    Dwnld dwnld = new Dwnld(j);
//                    Thread thread = new Thread(dwnld);
//                    thread.start();
                }
            }
        }
            return START_STICKY;

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ok !!!!!!!!!!!!!!!!!   "+solist.size());
        if(playerNotificationManager!=null) {
            playerNotificationManager.setPlayer(null);
        }
        stopForeground(true);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
   //    update();
        releasePlayer();

        super.onDestroy();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: start");
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
    private void startPlayer() {
 //       Toast.makeText(getApplicationContext(),"onStartCommand startplayer",Toast.LENGTH_SHORT).show();

        final Context context = this;
      //  Uri uri = Uri.parse(item.getUrl());

        player = ExoPlayerFactory.newSimpleInstance(this);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build();
        player.setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true);


        if(b!=null){

            MediaSource[] mediaSource =new MediaSource[size];
            for(  int i=0;i<size;i++){
                int k=i+j;
                if(k<size){
                    if(b.containsKey("link")) {
                        songList s = (songList) solist.get(k);
                        String names=s.getSonglastsegmant();
                        String edit="save_"+names;
                        File file = context.getFileStreamPath(names);
                        File file2 = context.getFileStreamPath(edit);
                        File filePlay;
                        String l;
                        if ( file2.exists())  {
                            filePlay = new File(context.getFilesDir(), edit);
                            l=filePlay.getPath();
          //                  Toast.makeText(getApplicationContext(),"song no is saved ",Toast.LENGTH_SHORT).show();
                        }else if ( file.exists())  {
                            filePlay = new File(context.getFilesDir(), names);
                            l=filePlay.getPath();
                //            Toast.makeText(getApplicationContext(),"song no is saved ",Toast.LENGTH_SHORT).show();
                        }else {
                            l=s.getUri();
           //                 Toast.makeText(getApplicationContext(),"song no is not saved ",Toast.LENGTH_SHORT).show();
                        }
                   //     String l = s.getUri();
                        Uri uri = Uri.parse(l);
                        mediaSource[i] = buildMediaSource(uri);
                    }else if(b.containsKey("posstion")){
                        File file = new File(context.getFilesDir(), (String) oflinelist.get(k));
                        String l =file.getPath();
                        Uri uri = Uri.parse(l);
                        DataSource.Factory dataSourceFactory =
                                new DefaultDataSourceFactory(
                                        this, "exoplayer-codelab");
                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                        // The MediaSource represents the media to be played.
                        mediaSource[i]  =
                                new ExtractorMediaSource(
                                        uri, dataSourceFactory, extractorsFactory, null, null);
                    }
                }else {
                    int t=k-size;
                    if(b.containsKey("link")) {
                        songList s = (songList) solist.get(t);
                        String names=s.getSonglastsegmant();
                        String edit="save_"+names;
                        File file = context.getFileStreamPath(names);
                        File file2 = context.getFileStreamPath(edit);
                        File filePlay;
                        String l;
                        if ( file2.exists())  {
                             filePlay = new File(context.getFilesDir(), edit);
                             l=filePlay.getPath();
           //                 Toast.makeText(getApplicationContext(),"song no is saved ",Toast.LENGTH_SHORT).show();
                        }else if ( file.exists())  {
                             filePlay = new File(context.getFilesDir(), names);
                            l=filePlay.getPath();
            //                Toast.makeText(getApplicationContext(),"song no is saved ",Toast.LENGTH_SHORT).show();
                        }else {
                            l=s.getUri();
                   //         Toast.makeText(getApplicationContext(),"song no is not saved ",Toast.LENGTH_SHORT).show();
                        }

                        Uri uri = Uri.parse(l);
                        mediaSource[i] = buildMediaSource(uri);
                    }else if(b.containsKey("posstion")){
                        File file = new File(context.getFilesDir(), (String) oflinelist.get(t));
                        String l =file.getPath();
                        Uri uri = Uri.parse(l);
                        DataSource.Factory dataSourceFactory =
                                new DefaultDataSourceFactory(
                                        this, "exoplayer-codelab");
                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                        // The MediaSource represents the media to be played.
                        mediaSource[i]  =
                                new ExtractorMediaSource(
                                        uri, dataSourceFactory, extractorsFactory, null, null);
                    }

                }

            }
            MediaSource mediaSourc = mediaSource.length == 1 ? mediaSource[0]
                    : new ConcatenatingMediaSource(mediaSource);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
            player.prepare(mediaSourc, false, false);
    playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                    context,
                    "guru",
                 Integer.valueOf(CHANNEL_NAME)   ,
                    NOTIFICATION_ID,
                    new PlayerNotificationManager.MediaDescriptionAdapter() {
                        @Override
                        public String getCurrentContentTitle(Player player) {
                            String songName="";
                            int songNo=player.getCurrentWindowIndex()+j;
                            if(solist.size()>0) {
                                if(songNo>=solist.size()){
                                    songNo=songNo-solist.size();
                                }
                                 songName = (solist.get(songNo)).getSonglastsegmant();
                            }else if(oflinelist.size()>0){
                                if(songNo>=oflinelist.size()){
                                    songNo=songNo-oflinelist.size();
                                }
                                 songName = (String) oflinelist.get(songNo);
                            }
                            return songName;
                        }

                        @Nullable
                        @Override
                        public PendingIntent createCurrentContentIntent(Player player) {
                            return notifyPendingIntent;
                        }

                        @Nullable
                        @Override
                        public String getCurrentContentText(Player player) {
                            return "context";
                        }

                        @Nullable
                        @Override
                        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.logo);
                            return icon;
                        }
                    },
                   new PlayerNotificationManager.NotificationListener() {

                        @Override
                        public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                            Log.d(TAG, "onNotificationCancelled: notification cancelcalled");
                            onDestroy();
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.cancel(NOTIFICATION_ID);
                    //        stopForeground(true);
                            stopSelf();
                        }

                        @Override
                        public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                            Log.d(TAG, "onNotificationPosted: notification caled");
                            mNotification=notification;
                            mNotificationId=notificationId;
                            //  startForeground(101, notification);
                        }
                    }

            );
            playerNotificationManager.setPlayer(player);
            Player.EventListener eventListener=new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playWhenReady && playbackState == Player.STATE_READY) {
                        // media actually playing
                    } else if (playWhenReady) {
                        // might be idle (plays after prepare()),
                        // buffering (plays when data available)
                        // or ended (plays when seek away from end)
                    } else {
                        // player paused in any state
                        stopForeground(false);
                    }
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) {
                        startForeground(mNotificationId,mNotification);
                        // Active playback.
                    } else {
                         //      stopForeground(false);
                         //    stopSelf();
                        // Not playing because playback is paused, ended, suppressed, or the player
                        // is buffering, stopped or failed. Check player.getPlaybackState,
                        // player.getPlayWhenReady, player.getPlaybackError and
                        // player.getPlaybackSuppressionReason for details.
                    }

                }

                @Override
                public void onPositionDiscontinuity(int reason) {
                    if(count>4){
                        count=0;
                    }

                    int latestWindowIndex = player.getCurrentWindowIndex();
                    int realIndex=latestWindowIndex+j;
                    if(realIndex>=solist.size()){
                        realIndex=realIndex-solist.size();
                    }
                        Log.d(TAG, "onPositionDiscontinuity: latestestwindowindex is   " +latestWindowIndex+"  ofset is  "+j);
                    if (latestWindowIndex != lastWindowIndex) {
                        count++;
                        // item selected in playlist has changed, handle here
               //         Toast.makeText(getApplicationContext(),"song no is  "+realIndex+"  "+solist.size(),Toast.LENGTH_SHORT).show();
                        lastWindowIndex = latestWindowIndex;
                        if(solist.size()>2 ){
                            repeat=solist.get(realIndex).getNo_of_time()+1;
                            solist.get(realIndex).setNo_of_time(repeat);
                            update();
                             boolean isStart=true;
                             String file_name=solist.get(realIndex).getSonglastsegmant();
                            String edit="save_"+file_name;
                            File file = context.getFileStreamPath(file_name);
                            File file2 = context.getFileStreamPath(edit);
                            if ( file2.exists() || file.exists())   {
                              isStart=false;
                            }
                          if(repeat>5 && isStart){
           //                   Toast.makeText(getApplicationContext(),"song no is  "+realIndex+"  "+repeat,Toast.LENGTH_SHORT).show();
                              servceStart(realIndex);
                         //     Dwnld dwnld=new Dwnld(realIndex);
                          //    Thread thread=new Thread(dwnld);
                            //      thread.start();
                      }
                        }
                    }
            //        Toast.makeText(getApplicationContext(),"song no is  "+realIndex+"  "+repeat,Toast.LENGTH_SHORT).show();

                }
                @Override
                public void onLoadingChanged(boolean isLoading) {
                         if(count>=4){
                             count=0;
                             videoAd();
                           /*  mInterstitialAd.loadAd(new AdRequest.Builder().build());
                             if (mInterstitialAd.isLoaded()) {
                                 mInterstitialAd.show();
                             } else {
                                 Log.d("TAG", "The interstitial wasn't loaded yet.");
                             }*/
                         }
                }
            };
            player.addListener(eventListener);
        }

    }


    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }
    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    @Override
    public void onItemsClick() {

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mRewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
    public void videoAd(){
        mRewardedVideoAd.loadAd("ca-app-pub-4379271297318191/7261448707",
                new AdRequest.Builder().build());
    }


    public class MyServiceBinder extends Binder{
        public MyService getService(){
            return  MyService.this;
        }
    }

    public SimpleExoPlayer getplayerInstance() {
        if (player == null) {
            startPlayer();
        }
        return player;
    }
    public void update(){
        Log.d(TAG, "onDestroy: ok bit66666666666666666666666666    "+solist.size());
        if(solist.size()>2){
            try {
                //            mostPlay.updateList(solist);
                CacheClass.writeObject(this,"OFLINE",solist,solist.size());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void servceStart(int index){
        songList s = solist.get(index);
        String filename = s.getSonglastsegmant();
        String u = s.getUri();
        Intent intent1=new Intent(this,DownloadService.class);
        intent1.putExtra("filenmane",filename);
        intent1.putExtra("link",u);
      startService(intent1);
    }
  class   Dwnld implements Runnable{
        int index;
        public Dwnld(int  index){
            this.index=index;
        }
      @Override
      public void run() {
          if (solist.size()>2) {
              songList s = solist.get(index);
              String filename = s.getSonglastsegmant();
              String u = s.getUri();
      //        Toast.makeText(getApplicationContext(),"song no is  ",Toast.LENGTH_SHORT).show();
              new Aysncdown(MyService.this, filename, MyService.this,2).execute(u);
          }
      }
  }
}
