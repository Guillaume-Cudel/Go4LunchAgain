package com.guillaume.myapplication.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class AlarmReceiver extends BroadcastReceiver {

    private static String TAG = "Alarm";
    private final String workID = "notificationWorkID";
    private WorkManager mWorkManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        mWorkManager = WorkManager.getInstance(context);

        Toast.makeText(context, "Alarm's running", Toast.LENGTH_LONG).show();
        Log.e(TAG, "Alarm's running");
        //todo put the good work request after the test
        applyNotification();
        //applyNotificationPeriodically();
    }

    private void applyNotification() {
        mWorkManager.enqueue(OneTimeWorkRequest.from(NotificationWorker.class));

        /*mWorkManager.enqueue(new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build());*/
    }

    private void applyNotificationPeriodically() {
        mWorkManager.enqueue(new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(1, TimeUnit.MINUTES)
                .addTag(workID)
                .build());
        Log.e(TAG, "Send notification all days");
    }
}
