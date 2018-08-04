package com.oney.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.app.NotificationChannel;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.leolin.shortcutbadger.ShortcutBadger;

public class GcmModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private final static String TAG = GcmModule.class.getCanonicalName();
    private Intent mIntent;
    private boolean mIsInForeground;
    private static final String CHANNEL_ID = "channel_sysnotify";

    public GcmModule(ReactApplicationContext reactContext, Intent intent) {
        super(reactContext);
        mIntent = intent;

        Log.d(TAG, "mIntent is null: " + (mIntent == null));
        if (getReactApplicationContext().hasCurrentActivity()) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(reactContext);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("GcmMainActivity", getCurrentActivity().getClass().getSimpleName());
            editor.apply();
        }

        if (mIntent == null) {
            listenGcmRegistration();
            listenGcmReceiveNotification();
            getReactApplicationContext().addLifecycleEventListener(this);
        }
    }

    @Override
    public String getName() {
        return "GcmModule";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        if (mIntent != null) {
            Bundle bundle = mIntent.getBundleExtra("bundle");
            String bundleString = convertJSON(bundle);
            Log.d(TAG, "bundleString: " + bundleString);
            constants.put("launchNotification", bundleString);
        }
        constants.put("fcmToken", FirebaseInstanceId.getInstance().getToken());
        return constants;
    }

    private void sendEvent(String eventName, Object params) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

    private void listenGcmRegistration() {
        IntentFilter intentFilter = new IntentFilter("RNGcmRegistrationServiceResult");

        getReactApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                boolean success = bundle.getBoolean("success");
                if (success) {
                    String token = bundle.getString("token");
                    WritableMap params = Arguments.createMap();
                    params.putString("deviceToken", token);

                    sendEvent("remoteNotificationsRegistered", params);
                } else {
                    String message = bundle.getString("message");
                    WritableMap params = Arguments.createMap();
                    params.putString("message", message);

                    sendEvent("remoteNotificationsRegisteredError", params);

                }
            }
        }, intentFilter);
    }

    private String convertJSON(Bundle bundle) {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    json.put(key, JSONObject.wrap(bundle.get(key)));
                } else {
                    json.put(key, bundle.get(key));
                }
            } catch(JSONException e) {
                return null;
            }
        }
        return json.toString();
    }

    private void listenGcmReceiveNotification() {
        IntentFilter intentFilter = new IntentFilter("com.oney.gcm.GCMReceiveNotification");

        getReactApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "GCMReceiveNotification BroadcastReceiver");

                if (getReactApplicationContext().hasActiveCatalystInstance()) {
                    Bundle bundle = intent.getBundleExtra("bundle");

                    String bundleString = convertJSON(bundle);

                    WritableMap params = Arguments.createMap();
                    params.putString("dataJSON", bundleString);
                    params.putBoolean("isInForeground", mIsInForeground);

                    sendEvent("remoteNotificationReceived", params);
                    abortBroadcast();
                } else {
                }
            }
        }, intentFilter);
    }

    @ReactMethod
    public void requestPermissions() {
        getReactApplicationContext().startService(new Intent(getReactApplicationContext(), GcmRegistrationService.class));
    }
    @ReactMethod
    public void joinTopic(String token , String topic) {
        GcmPubSub pubSub = GcmPubSub.getInstance(getReactApplicationContext());
        try {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    @ReactMethod
    public void joinTopicFcm(String token , String topic) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    @ReactMethod
    public void stopService() {
        if (mIntent != null) {
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    getReactApplicationContext().stopService(mIntent);
                }
            }, 1000);
        }
    }
    private Class getMainActivityClass() {
        try {
            String packageName = getReactApplicationContext().getPackageName();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getReactApplicationContext());
            String activityString = preferences.getString("GcmMainActivity", null);
            if (activityString == null) {
                Log.d(TAG, "GcmMainActivity is null");
                return null;
            } else {
                return Class.forName(packageName + "." + activityString);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
          return;
        NotificationManager mNotificationManager = (NotificationManager) getReactApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = CHANNEL_ID;
        // The user-visible name of the channel.
        CharSequence name = "rn-gcm-android";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // Configure the notification channel.
        //mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mChannel.enableLights(true);
        mChannel.enableVibration(true);
        mNotificationManager.createNotificationChannel(mChannel);
    }
    @ReactMethod
    public void createNotification(ReadableMap infos) {
        createChannel();

        Resources resources = getReactApplicationContext().getResources();

        String packageName = getReactApplicationContext().getPackageName();

        Class intentClass = getMainActivityClass();

        Log.d(TAG, "packageName: " + packageName);

        if (intentClass == null) {
            Log.d(TAG, "intentClass is null");
            return;
        }

        int largeIconResourceId = resources.getIdentifier(infos.getString("largeIcon"), "mipmap", packageName);
        int smallIconResourceId = android.R.drawable.ic_dialog_info;
        if(infos.hasKey("smallIcon")){
            smallIconResourceId = resources.getIdentifier(infos.getString("smallIcon"), "mipmap", packageName);
        }
        Intent intent = new Intent(getReactApplicationContext(), intentClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getReactApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(resources, largeIconResourceId);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getReactApplicationContext(),CHANNEL_ID)
                .setLargeIcon(largeIcon)
                .setSmallIcon(smallIconResourceId)
                .setContentTitle(infos.getString("subject"))
                .setContentText(infos.getString("message"))
                .setAutoCancel(infos.getBoolean("autoCancel"))
                .setSound(defaultSoundUri)
                .setTicker(infos.getString("ticker"))
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        notificationBuilder.setChannelId(CHANNEL_ID);
        NotificationManager notificationManager =
                (NotificationManager) getReactApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = notificationBuilder.build();
        notif.defaults |= Notification.DEFAULT_VIBRATE;
        notif.defaults |= Notification.DEFAULT_SOUND;
        notif.defaults |= Notification.DEFAULT_LIGHTS;

        notificationManager.notify(0, notif);
    }

    @ReactMethod
    public void applyBadgeCount (int badgeCount){
        ShortcutBadger.applyCount(getReactApplicationContext(), badgeCount);
    }

    @ReactMethod
    public void removeBadgeCount(){
        ShortcutBadger.removeCount(getReactApplicationContext());
    }

    @Override
    public void onHostResume() {
        mIsInForeground = true;
    }

    @Override
    public void onHostPause() {
        mIsInForeground = false;
    }

    @Override
    public void onHostDestroy() {

    }
}
