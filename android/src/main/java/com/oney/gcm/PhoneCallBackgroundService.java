// package com.oney.gcm;
//
// import android.app.IntentService;
// import android.content.Intent;
// import android.os.IBinder;
// import android.util.Log;
//
// import com.oney.gcm.PusherReceiver;
//
// public class PhoneCallBackgroundService extends IntentService {
//     private static final String TAG = "PhoneCallBackgroundService";
//     public PhoneCallBackgroundService() {
//         super("PhoneCallBackgroundService");
//     }
//     public IBinder onBind(Intent intent) {
//         return null;
//     }
//     protected void onHandleIntent(Intent i) {
//         Log.d(TAG, "onHandleIntent");
//         i.setClass(this, UnlockScreenActivity.class);
//         i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//         startActivity(i);
//         PusherReceiver.completeWakefulIntent(i);
//     }
// }
