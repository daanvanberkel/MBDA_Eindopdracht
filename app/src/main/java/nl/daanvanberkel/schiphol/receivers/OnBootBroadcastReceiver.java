package nl.daanvanberkel.schiphol.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import nl.daanvanberkel.schiphol.helpers.JobServiceStarter;

public class OnBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            if (sharedPreferences.getBoolean("notifications", true)) {
                JobServiceStarter.startFavoriteFlightJobService(context);
            } else {
                JobServiceStarter.stopFavoriteFlightJobService(context);
            }
        }
    }
}
