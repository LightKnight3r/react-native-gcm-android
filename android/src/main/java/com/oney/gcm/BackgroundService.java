package com.oney.gcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;

import io.neson.react.notification.NotificationPackage;
import android.app.Notification;
import android.os.Build;
import com.oney.gcm.GcmApplication;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected ReactRootView createRootView() {
        return new ReactRootView(this);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

//        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                .addPackage(new GcmPackage(intent))
                .addPackage(new NotificationPackage())
               .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
               //~ .setUseOldBridge(true)
                .build();

//        mReactRootView.startReactApplication(mReactInstanceManager, "GCMHandle", null);
        mReactInstanceManager.createReactContextInBackground();
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !GcmApplication.isActivityVisible()) {
        //
        //   Notification.Builder builder = new Notification.Builder(this, "channel_sysnotify")
        //           .setContentTitle("Săn Ship")
        //           .setContentText("ahihi")
        //           .setAutoCancel(true);
        //   Notification notification = builder.build();
        //   startForeground(1, notification);
        //
        // }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mReactInstanceManager.destroy();
        mReactInstanceManager = null;
    }

//    private Class getBuildConfigClass() {
//        try {
//            String packageName = getPackageName();
//
//            return Class.forName(packageName + ".BuildConfig");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    private boolean getBuildConfigDEBUG() {
//        Class klass = getBuildConfigClass();
//        for (Field f : klass.getDeclaredFields()) {
//            if (f.getName().equals("DEBUG")) {
//                try {
//                    return f.getBoolean(this);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return false;
//    }
}
