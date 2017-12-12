// package com.oney.gcm;
//
// import android.support.v4.content.WakefulBroadcastReceiver;
// import android.content.Intent;
// import android.content.Context;
// import android.os.Bundle;
// import org.json.JSONObject;
// import org.json.JSONException;
// import java.util.Set;
// import android.util.Log;
// import android.app.ActivityManager;
// import java.util.List;
// import android.app.ActivityManager.RunningAppProcessInfo;
// import com.oney.gcm.GcmApplication;
//
// public class PusherReceiver extends WakefulBroadcastReceiver {
//   private final static String TAG = GcmModule.class.getCanonicalName();
//     public void onReceive(Context context, Intent intent) {
//         Bundle bundle = intent.getBundleExtra("bundle");
//         JSONObject notifyInfo = new JSONObject();
//         String link = "";
//         Set<String> keys = bundle.keySet();
//         for (String key : keys) {
//             try {
//                 notifyInfo.put(key, JSONObject.wrap(bundle.get(key)));
//                 link = (String)notifyInfo.get("link");
//             } catch(JSONException e) {
//                 //Handle exception here
//             }
//         }
//         if(link.equals("WebRTC")){
//           Intent newIntent = new Intent(context, PhoneCallBackgroundService.class);
//           newIntent.putExtra("bundle", bundle);
//           startWakefulService(context, newIntent);
//         }
//     }
//
// }
