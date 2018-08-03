package com.oney.gcm;

import com.facebook.react.ReactActivity;
import android.os.Bundle;
import android.view.WindowManager;
import com.oney.gcm.R;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import android.view.View;
import com.facebook.react.bridge.Arguments;
import android.util.Log;
import android.content.Intent;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Set;
import de.hdodenhof.circleimageview.CircleImageView;
import android.os.CountDownTimer;
import com.oney.gcm.GcmApplication;
import android.view.KeyEvent;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.KeyguardManager.OnKeyguardExitResult;


public class UnlockScreenActivity extends ReactActivity {
    private static final String TAG = "UnlockScreenActivity";
    KeyguardManager.KeyguardLock keylock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

      setContentView(R.layout.activity_call_coming);
      new CountDownTimer(30000, 1000) {

         public void onTick(long millisUntilFinished) {
            // do smth
         }

         public void onFinish() {
             finish();
         }
      }.start();

      Intent intent = getIntent();
      Bundle bundleBack = intent.getBundleExtra("bundle");

      JSONObject userInfo = new JSONObject();
      String userName = "";
      String phoneNumber = "";
      Set<String> keys = bundleBack.keySet();
      for (String key : keys) {
          try {
              // json.put(key, bundle.get(key)); see edit below
              userInfo.put(key, JSONObject.wrap(bundleBack.get(key)));
              userName = (String)userInfo.get("title");
              phoneNumber = (String)userInfo.get("phone");

          } catch(JSONException e) {
              //Handle exception here
          }
      }
      TextView textViewName = (TextView)findViewById(R.id.textViewName);
      textViewName.setText(userName);
      TextView textViewPhone = (TextView)findViewById(R.id.textViewPhone);
      textViewPhone.setText(phoneNumber);

      //onclicklistener
      Button acceptCallBtn = (Button) findViewById(R.id.buttonCall);
      acceptCallBtn.setOnClickListener(new Button.OnClickListener() {
        public void onClick(View view) {
            WritableMap params = Arguments.createMap();
            sendEvent(getReactInstanceManager().getCurrentReactContext(), "accept-call", params);
            finish();
        }
      });
      Button declineCall = (Button) findViewById(R.id.buttonEndCall);
      declineCall.setOnClickListener(new Button.OnClickListener() {
          public void onClick(View view) {
            WritableMap params = Arguments.createMap();
            sendEvent(getReactInstanceManager().getCurrentReactContext(), "decline-call", params);
            finish();
          }
      });
      KeyguardManager kgm = (KeyguardManager)getSystemService(this.KEYGUARD_SERVICE);

      if(kgm.isKeyguardSecure()) {
          kgm.exitKeyguardSecurely(new OnKeyguardExitResult() {

    				public void onKeyguardExitResult(boolean success) {
    					  keylock.disableKeyguard();
    				}

    			});
      }
    }
    private void sendEvent(ReactContext reactContext, String eventName, WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}
