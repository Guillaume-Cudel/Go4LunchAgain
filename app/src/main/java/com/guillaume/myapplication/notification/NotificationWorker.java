package com.guillaume.myapplication.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.guillaume.myapplication.NavigationActivity;
import com.guillaume.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationWorker extends Worker {

    private String TAG = "NotificationWorker";
    private String CHANNEL_ID = "CHANNEL_ID";
    private String title = "Go4Lunch";
    private CharSequence name = "Channel 1";
    private String description = "This is channel 1";
    private final int notificationID = 1;


    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();

        try {
            sendNotification(applicationContext);
            return Result.success();
        } catch (Throwable throwable) {
            Log.e(TAG, "Error applying blur", throwable);
            return Result.failure();
        }
    }

    private void sendNotification(Context context) {

        List<String> workmatesName = new ArrayList<String>();
        workmatesName.add("Emma");
        workmatesName.add("Bob");

        StringBuilder wNames = new StringBuilder();

        String prefix = ", ";
        for (String str : workmatesName) {
            wNames.append(str);
            wNames.append(prefix);
        }
        if (wNames.length() > 0) {
            wNames.deleteCharAt(wNames.length() - 2);
            wNames.append(".");
        }

        //todo change the content with server data
        String content1 = "Don't forgot !";
        String content2 = "You eat at the restaurant: ";
        String restaurant = "Emile zola";
        String address = "address";

        String message = content2 + restaurant.toUpperCase() + " at " + address + " with " + wNames;
        Log.e(TAG, wNames.toString());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notifIntent = new Intent(context, NavigationActivity.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifIntent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_rice_bowl)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(content1)
                // add this for more informations
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // remove the notification when tap it
                .setAutoCancel(true);

        notificationManager.notify(notificationID, builder.build());
    }
}
