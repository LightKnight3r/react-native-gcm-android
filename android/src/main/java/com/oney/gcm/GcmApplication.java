package com.oney.gcm;
import android.app.Application;

public class GcmApplication extends Application {

  public static boolean isActivityVisible() {
    return activityVisible;
  }

  public static void activityResumed() {
    activityVisible = true;
  }

  public static void activityStarted() {
    activityVisible = true;
  }

  private static boolean activityVisible;
}
