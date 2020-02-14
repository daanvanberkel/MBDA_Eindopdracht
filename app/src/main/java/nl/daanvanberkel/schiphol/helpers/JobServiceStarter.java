package nl.daanvanberkel.schiphol.helpers;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import nl.daanvanberkel.schiphol.services.FavoriteFlightService;

public class JobServiceStarter {
    public static final int JOB_ID = 1;

    private JobServiceStarter() {}

    public static void startFavoriteFlightJobService(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

        if (jobScheduler.getPendingJob(JOB_ID) != null) {
            return;
        }

        ComponentName serviceComponent = new ComponentName(context, FavoriteFlightService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent);
        builder.setMinimumLatency(1000 * 60); // 1 minute
        builder.setOverrideDeadline(1000 * 60 * 5); // 5 minutes
        jobScheduler.schedule(builder.build());
    }

    public static void stopFavoriteFlightJobService(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

        JobInfo jobInfo = jobScheduler.getPendingJob(JOB_ID);

        if (jobInfo != null) {
            jobScheduler.cancel(JOB_ID);
        }
    }

    public static void restartFavoriteFlightJobService(Context context) {
        stopFavoriteFlightJobService(context);
        startFavoriteFlightJobService(context);
    }
}
