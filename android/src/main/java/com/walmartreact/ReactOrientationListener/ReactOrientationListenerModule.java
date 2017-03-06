package com.walmartreact.ReactOrientationListener;

import javax.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;
import android.os.Build;
import android.content.Context;
import android.view.Surface;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

public class ReactOrientationListenerModule extends ReactContextBaseJavaModule {

  ReactApplicationContext reactContext;
  OrientationEventListener mOrientationListener;
  String lastOrientation = null;

  public ReactOrientationListenerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    final ReactApplicationContext thisContext = reactContext;

    mOrientationListener = new OrientationEventListener(reactContext,
      SensorManager.SENSOR_DELAY_NORMAL) {
      @Override
      public void onOrientationChanged(int orientation) {
        String currentOrientation = getRotation();
        if (lastOrientation != null && currentOrientation == lastOrientation) {
          return;
        }
        lastOrientation = currentOrientation;

        WritableNativeMap params = new WritableNativeMap();
        params.putString("orientation", currentOrientation);
        params.putString("device", getDeviceName());
        if (thisContext.hasActiveCatalystInstance()) {
          thisContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                  .emit("orientationDidChange", params);
        }
      }
    };

    if (mOrientationListener.canDetectOrientation() == true) {
      mOrientationListener.enable();
    } else {
      mOrientationListener.disable();
    }
  }

  public String getDeviceName() {
    String manufacturer = Build.MANUFACTURER;
    String model = Build.MODEL;
    if (model.startsWith(manufacturer)) {
      return capitalize(model);
    } else {
      return capitalize(manufacturer) + " " + model;
    }
  }

  private String capitalize(String s) {
    if (s == null || s.length() == 0) {
      return "";
    }
    char first = s.charAt(0);
    if (Character.isUpperCase(first)) {
      return s;
    } else {
      return Character.toUpperCase(first) + s.substring(1);
    }
  }

  private String getRotation() {
    WindowManager windowManager = (WindowManager) reactContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    final int rotation = windowManager.getDefaultDisplay().getOrientation();
    switch(rotation) {
      case Surface.ROTATION_0:
      case Surface.ROTATION_180:
        return "PORTRAIT";
      default:
        return "LANDCAPE";
    }
  }

  @Override
  public String getName() {
    return "OrientationListener";
  }

  @Override
  public @Nullable Map<String, Object> getConstants() {
    HashMap<String, Object> constants = new HashMap<String, Object>();
    PackageManager packageManager = this.reactContext.getPackageManager();
    String packageName = this.reactContext.getPackageName();
    return constants;
  }

  @ReactMethod
  public void getOrientation(Callback success) {
    WritableNativeMap data = new WritableNativeMap();
    DisplayMetrics metrics = this.reactContext.getResources().getDisplayMetrics();
    String orientation = "";
    if(metrics.widthPixels < metrics.heightPixels){
      orientation = "PORTRAIT";
    }else {
      orientation = "LANDSCAPE";
    }
    data.putString("orientation", orientation);
    data.putString("device", getDeviceName());
    success.invoke(data);
  }

}
