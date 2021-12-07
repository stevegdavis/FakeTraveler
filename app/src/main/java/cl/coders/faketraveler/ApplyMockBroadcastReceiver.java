package cl.coders.faketraveler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class ApplyMockBroadcastReceiver extends BroadcastReceiver {

    Intent serviceIntent;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public ApplyMockBroadcastReceiver() {
        alarmManager = MainActivity.alarmManager;
        serviceIntent = MainActivity.serviceIntent;
        pendingIntent = MainActivity.pendingIntent;
        sharedPref = MainActivity.sharedPref;
        editor = MainActivity.editor;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        double lat = 0;
        double lng = 0;
        try {
            if(MainActivity.list != null && MainActivity.list.size() > 0){
                lat = Double.parseDouble(MainActivity.list.get(MainActivity.GPXCoordinatesIdx).Latitude);
                lng = Double.parseDouble(MainActivity.list.get(MainActivity.GPXCoordinatesIdx++).Longitude);
                if(MainActivity.GPXCoordinatesIdx >= MainActivity.list.size())
                    MainActivity.GPXCoordinatesIdx = 0;
            }
            else {
                lat = Double.parseDouble(sharedPref.getString("lat", "0"));
                lng = Double.parseDouble(sharedPref.getString("lng", "0"));
            }

            MainActivity.exec(lat, lng);

            if (!MainActivity.hasEnded()) {
                MainActivity.setAlarm(MainActivity.timeInterval);
            } else {
                MainActivity.stopMockingLocation();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
