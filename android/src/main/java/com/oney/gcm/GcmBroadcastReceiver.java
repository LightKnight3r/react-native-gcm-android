package com.oney.gcm;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Set;

public class GcmBroadcastReceiver extends BroadcastReceiver {
    private final static String TAG = GcmModule.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        Bundle bundle = intent.getBundleExtra("bundle");
        // JSONObject notifyInfo = new JSONObject();
        // Set<String> keys = bundle.keySet();
        // String link = "";
        // for (String key : keys) {
        //    try {
        //      notifyInfo.put(key, JSONObject.wrap(bundle.get(key)));
        //      link = (String)notifyInfo.get("link");
        // 
        //    } catch(JSONException e) {
        //        //Handle exception here
        //    }
        // }
        //if(!link.equals("WebRTC")) {
         Intent newIntent = new Intent(context, BackgroundService.class);
         newIntent.putExtra("bundle", bundle);
         context.startService(newIntent);
         abortBroadcast();
        //}
    }
}
