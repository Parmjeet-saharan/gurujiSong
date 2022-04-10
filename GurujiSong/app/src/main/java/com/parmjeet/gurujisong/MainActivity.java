package com.parmjeet.gurujisong;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.chartboost.sdk.Chartboost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class MainActivity extends AppCompatActivity implements CustomAdapter.OnItemClickListener ,Aysncdown.Adinterface , RewardedVideoAdListener {
    private static final int MY_REQUEST_CODE =101 ;
    private RewardedVideoAd mRewardedVideoAd;
    private boolean newData=false;
    AppUpdateManager mAppUpdateManager;
    InstallStateUpdatedListener installStateUpdatedListener;
    FakeAppUpdateManager fakeAppUpdateManager;


    private static final String TAG = "MyTag mainactivity";
    int noOfSong=0;
    RecyclerView recyclerView;
    ChildEventListener childEventListener;
    FirebaseDatabase database;
     FirebaseRemoteConfig mFirebaseRemoteConfig;
    DatabaseReference ref;
    ArrayList<songList> sList = new ArrayList<songList>();
    ArrayList<songList> sList1 = new ArrayList<songList>();
    DownloadManager dm;
    private String node;
    public static final String preKey = "preference";
    public static final String isdataChange="Refresh";
    public static final String key = "length";
    private String str;
    HashMap<String, Object> inc = new HashMap<>();
    boolean isRefresh = false;

    private boolean confirm = true;
    Button button;
    Context context = this;
    private AdView mAdView;
    private int no_of_song = 0;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //   songlistView=(ListView) findViewById(R.id.listviewsong);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        button = (Button) findViewById(R.id.ofline);
        //     Toast.makeText(getApplicationContext(),"###############",Toast.LENGTH_SHORT).show();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("audiosong");
           mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                   .setMinimumFetchIntervalInSeconds(2)
                     .build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        inc.put("isrefresh", 1);
            mFirebaseRemoteConfig.setDefaultsAsync(inc);
          configList();
      boolean isnewData=   new SizeOfMsg(context,"dolater").getRefresh("check");


        noOfSong = new SizeOfMsg(this, preKey).getSharedPerference(key);
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else {
            connected = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("IF YOU WANT TO LISTEN ONLINE SHABAD TURN ON NETWORK ELSE LISTEN DOWNLOADED SHABAD")
                    .setTitle("YOU ARE OFLINE");
            builder.setPositiveButton("GO ONLINE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                    startActivity(intent);
                    //       startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));


                }
            });
            builder.setNegativeButton("OFLINE SHABAD", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    dialog.dismiss();
                    Intent in = new Intent(MainActivity.this, OflineSong.class);
                    startActivity(in);
                }
            });
            builder.show();

        }
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }

        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener()
                              {
                                  @Override
                                  public void onAdClosed() {
                                      super.onAdClosed();
                                      Log.d(TAG, "onAdClosed: add is closed@@@@@@@@@");
                                  }

                                  @Override
                                  public void onAdFailedToLoad(LoadAdError loadAdError) {
                                      super.onAdFailedToLoad(loadAdError);
                                      Log.d(TAG, " @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2 onAdFailedToLoad: error is"+loadAdError);
                                  }

                                  @Override
                                  public void onAdLeftApplication() {
                                      super.onAdLeftApplication();
                                  }

                                  @Override
                                  public void onAdOpened() {
                                      super.onAdOpened();
                                  }

                                  @Override
                                  public void onAdClicked() {
                                      super.onAdClicked();
                                      Log.d(TAG, "onAdClicked: add is get clicked @@@@@@@@@@@@@@@@@@@@@@@2");
                                  }

                                  @Override
                                  public void onAdImpression() {
                                      super.onAdImpression();
                                  }

                                  public void onAdLoaded( )
                                  {
                                      // ads:adUnitId="ca-app-pub-4379271297318191/8698349220">

                                      Log.d(TAG, "onAdLoaded: add load @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                                  };
                                  public void onAdFailedToLoad( )
                                  {
                                      Log.d(TAG, "onAdFailedToLoad: re$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$############################################$");
                                  }
                              }
        );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        try {
            sList1 = (ArrayList<songList>) CacheClass.readObject(this, "OFLINE");

            Collections.sort(sList1, Comparator.comparingInt(songList::getNo_of_time).reversed());
            delete m1 = new delete();
            Thread t1 = new Thread(m1);
            t1.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
   if(isCallDatabase() || isnewData){
          Date date=new Date();
          long timeMilli=date.getTime();
         new SizeOfMsg(this,"Date").setDate("callDate",timeMilli);
           attachDatabaseReadListener(node);
       }else if(((sList1.size())==noOfSong) && connected && noOfSong>0) {
    //      Toast.makeText(getApplicationContext()," ofline   "+sList1.size(),Toast.LENGTH_SHORT).show();
          CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, (ArrayList) sList1, MainActivity.this);
        recyclerView.setAdapter(customAdapter);

        }else {
           Date date=new Date();
           long timeMilli=date.getTime();
          new SizeOfMsg(this,"Date").setDate("callDate",timeMilli);
            attachDatabaseReadListener(node);
  //          Toast.makeText(getApplicationContext(),"no of song   "+noOfSong+"  ="+sList.size(),Toast.LENGTH_SHORT).show();

        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, OflineSong.class);
                startActivity(in);
            }
        });
        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {

            @Override
            public void onSdkInitialized(AppLovinSdkConfiguration config) {

            }
        } );
  // bbreuh0e9 ojewfjw
        //bufbniweuk ewhwei iwbeiub weoweffwk ojwefn
       /* FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "let see";
                        if (!task.isSuccessful()) {
                            msg ="sucess";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
*/
      /*  FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = "tokin is "  + token;
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });*/
  //      MediationTestSuite.launch(MainActivity.this);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fakeTest();
        updateApp();
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

    private void attachDatabaseReadListener(String nodeId) {
        //    no_of_song=0;
   //     Toast.makeText(getApplicationContext(), " database called   ", Toast.LENGTH_SHORT).show();
        Query myTopPostsQuery;
        if (nodeId == null) {
            myTopPostsQuery = ref
                    .orderByChild("songlastsegmant");
        } else {
            myTopPostsQuery = ref
                    .orderByChild("songlastsegmant");

        }

        myTopPostsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                songList sname = (songList) snapshot.getValue(songList.class);

                sList.add(sname);
       //         Toast.makeText(getApplicationContext(), " song name is   " + sname + "    ", Toast.LENGTH_SHORT).show();
                no_of_song++;
                CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, (ArrayList) sList, MainActivity.this);
                recyclerView.setAdapter(customAdapter);
                node = sname.getSonglastsegmant();
                try {
                    //   Toast.makeText(getApplicationContext(),"   "+deleted+"    "+filePath,Toast.LENGTH_SHORT).show();
                    CacheClass.writeObject(MainActivity.this, "OFLINE", sList, no_of_song);
                    new SizeOfMsg(MainActivity.this, preKey).setSharedpreferences(no_of_song, key);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (node.equals(str)) {
                    confirm = false;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void detachDatabaseReadListener() {
        if (childEventListener != null) {
            ref.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    @Override
    public void onItemClick(int position) throws IOException {
        if (sList.size() > (noOfSong-1)) {
            songList s = sList.get(position);
            String filename = s.getSonglastsegmant();
            String u = s.getUri();
            new Aysncdown(MainActivity.this, filename, MainActivity.this, 1).execute(u);
        } else if (sList1.size() > (noOfSong=1)) {
            songList s = sList1.get(position);
            String filename = s.getSonglastsegmant();
            String u = s.getUri();
            new Aysncdown(MainActivity.this, filename, MainActivity.this, 1).execute(u);
        }
    }

    @Override
    public void onItemsClick() {
      //     Toast.makeText(getApplicationContext()," call back get call !!!!!!!!!!!!!!!!",Toast.LENGTH_SHORT).show();
        MobileAds.initialize(this);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);

        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-4379271297318191/7261448707",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
       //   Toast.makeText(getApplicationContext(),"video is loaded !!!!!!!!!!!!!!!!!!!!!!!!11",Toast.LENGTH_SHORT).show();
        mRewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        //      Toast.makeText(getApplicationContext(),"video is opened !!!!!!!!!!!!!!!!!!!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
       //     Toast.makeText(getApplicationContext(),"video is started !!!!!!!!!!!!!!!!!!!",Toast.LENGTH_SHORT).show();
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
        //     Toast.makeText(getApplicationContext(),"video load is failed !!!!!!!!!!!!!!!!!!!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    private boolean isCallDatabase() {
        long d1 = new SizeOfMsg(context, "Date").getDate("callDate");
        Date d2 = new Date();
        long timeSec = d2.getTime();
        long diff = timeSec - d1;
        int days = (int) diff / (1000 * 60 * 60 * 24);
        if (days >= 7) {
            return true;
        } else {
            return false;
            // guey88wy2 uugewhiihewe jewwiehihiew  wjqejuugkqwjew
            // uguhsdds yyewgdsnihdsvm dsinoiisd

        }
    }

      public boolean configList(){
          mFirebaseRemoteConfig.fetchAndActivate()
                  .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                      @Override
                      public void onComplete(@NonNull Task<Boolean> task) {
                          if (task.isSuccessful()) {
                              boolean updated = task.getResult();
                   //           Log.d(TAG, "Config params updated: " + updated);
                              int curent=(int) mFirebaseRemoteConfig.getLong("isrefresh");
                          int last=  new SizeOfMsg(context,isdataChange).getinc("isrefresh");
                          if(last!=curent){
                                  newData = true;
                              new SizeOfMsg(context,isdataChange).setinc("isrefresh",curent);
                              new SizeOfMsg(context,"dolater").setRefresh("check",true);
                          }else {
                              new SizeOfMsg(context,"dolater").setRefresh("check",false);
                          }
                          } else {
                           Toast.makeText(MainActivity.this, "Fetch failed  ",
                                     Toast.LENGTH_SHORT).show();
                              new SizeOfMsg(context,"dolater").setRefresh("check",false);
                          }
                      }
                  });
          return newData;
      }
    class delete implements Runnable {
        @Override
        public void run() {
//            String[] files = context.fileList();
//            int size = files.length;
//            for (int i = 0; i < size; i++) {
//                File file = new File(context.getFilesDir(), files[i]);
//                if (file.exists()) {
//                    Calendar time = Calendar.getInstance();
//                    time.add(Calendar.DAY_OF_YEAR, -15);
//                    //I store the required attributes here and delete them
//                    Date lastModified = new Date(file.lastModified());
//                    if (lastModified.before(time.getTime())) {
//                        //file is older than a week
//                        file.delete();
//                    }
//                }
//
//            }
            MostPlay mostPlay = new MostPlay(context, sList1);
            mostPlay.savedKept();
        }
    }
    public void updateApp(){
         installStateUpdatedListener = new
                InstallStateUpdatedListener() {
                    @Override
                    public void onStateUpdate(@NonNull InstallState state) {
                        if (state.installStatus() == InstallStatus.DOWNLOADED){
                            //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                            Log.d(TAG, "onStateUpdate: popup come");
                            popupSnackbarForCompleteUpdate();
                        } else {
                            Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
                        }
                    }
                };
      /*   appUpdateManager = AppUpdateManagerFactory.create(context);
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        Log.d(TAG, "updateApp: update get call @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ");
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            Log.d(TAG, "updateApp: @@@@@@@@@@@@@@@@@@@@@@ "+ appUpdateInfo.updateAvailability() );
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                Log.d(TAG, "updateApp: update is availabe 2222222222222222222222222222222222222222222");
                Toast.makeText(context, "what happen", Toast.LENGTH_LONG).show();

                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            this,
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });*/
        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
           Log.d(TAG, "updateApp: "+String.valueOf(appUpdateInfo.updateAvailability()));
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/)){
                Log.d(TAG, "updateApp: update avalaible");
                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/, MainActivity.this, MY_REQUEST_CODE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                popupSnackbarForCompleteUpdate();
            } else {
                Log.e(TAG, "checkForAppUpdateAvailability: something else");
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                //     log("Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.liner),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> mAppUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate();
                    }
                });
    }
 public void fakeTest(){
     fakeAppUpdateManager= new FakeAppUpdateManager(this);
     fakeAppUpdateManager.setUpdateNotAvailable();
     if(fakeAppUpdateManager.isConfirmationDialogVisible()){
         fakeAppUpdateManager.userAcceptsUpdate();
         fakeAppUpdateManager.downloadStarts();
         fakeAppUpdateManager.downloadCompletes();
         fakeAppUpdateManager.completeUpdate();
         fakeAppUpdateManager.installCompletes();
     }

 }

}