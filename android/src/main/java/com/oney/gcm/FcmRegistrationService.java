package com.oney.gcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * @author phuongtq
 * @file ....java
 * @brief ....java source file.
 * <p/>
 * File/module comments.
 * @mobile 01684499886
 * @note No Note at the moment
 * @bug No known bugs.
 * <p/>
 * <pre>
 * MODIFICATION HISTORY:
 *
 * Ver   Who  	  Date       Changes
 * ----- --------- ---------- -----------------------------------------------
 * 1.00  phuongtq   12/1/2016 First release
 *
 *
 * </pre>
 */

public class FcmRegistrationService extends FirebaseInstanceIdService {
    private static final String TAG = "FcmRegistrationService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
