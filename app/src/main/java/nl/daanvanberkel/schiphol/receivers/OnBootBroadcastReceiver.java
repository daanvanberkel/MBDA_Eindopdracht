package nl.daanvanberkel.schiphol.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nl.daanvanberkel.schiphol.helpers.JobServiceStarter;

public class OnBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            JobServiceStarter.startFavoriteFlightJobService(context);
        }
    }
}
