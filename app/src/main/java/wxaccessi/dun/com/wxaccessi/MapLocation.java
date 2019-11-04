package wxaccessi.dun.com.wxaccessi;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/4/15.
 */

public class MapLocation {
    private String mMockProviderName = LocationManager.GPS_PROVIDER;
    private LocationManager locationManager;
    private Thread thread;
    private Boolean RUN = true;
    private double latitude = 31.3029742, longitude = 120.6097126;// 默认常州


    public void setxy(double lat,double log){
        latitude=lat;
        longitude=log;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void inilocation(Context context) {
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        locationManager.addTestProvider(mMockProviderName, false, true, false, false, true, true,
                true, 0, 5);
        locationManager.setTestProviderEnabled(mMockProviderName, true);
        locationManager.requestLocationUpdates(mMockProviderName, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
            }
        });
    }

    /**
     * setLocation 设置GPS的位置
     *
     */
    public void setLocation(double longitude, double latitude) {
        Location location = new Location(mMockProviderName);
        location.setTime(System.currentTimeMillis());
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(2.0f);
        location.setAccuracy(3.0f);
        if (Build.VERSION.SDK_INT > 16) {
            //api 16以上的需要加上这一句才能模拟定位 , 也就是targetSdkVersion > 16
            location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        try
        {
            Method method = Location.class.getMethod("makeComplete");
            if (method != null)
            {
                method.invoke(location);
            }
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        locationManager.setTestProviderLocation(mMockProviderName, location);
    }

    public void iniMap() {
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (RUN) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setLocation(longitude, latitude);
                }
            }
        });
        thread.start();
    }
}
