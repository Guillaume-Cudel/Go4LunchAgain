package com.guillaume.myapplication.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.concurrent.futures.ResolvableFuture;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.android.volley.Response;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guillaume.myapplication.NavigationActivity;
import com.guillaume.myapplication.R;
import com.guillaume.myapplication.api.RestaurantHelper;
import com.guillaume.myapplication.api.UserHelper;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class NotificationWorker extends ListenableWorker {

    private final CharSequence name = "Channel 1";
    private Restaurant mRestaurant;
    private UserFirebase currentUser;
    private List<UserFirebase> workmates = new ArrayList<>();
    private List<String> workmatesName = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser authUser = mAuth.getCurrentUser();
    private String userID = authUser.getUid();
    private final String TAG = "NotificationWorker";
    private ResolvableFuture<Result> mFuture;


    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        //todo finish to set it
        Context applicationContext = getApplicationContext();

        return CallbackToFutureAdapter.getFuture(completer -> {
            Callback callback = new Callback() {
                int successes = 0;


                public void onFailure(Call call, IOException e) {
                    completer.setException(e);
                }


                public void onResponse(Call call, Response response) {
                    successes++;
                    if (successes == 100) {
                        completer.set(Result.success());
                    }
                }
            };

            recoveData(userID, applicationContext);

            return callback;
        });
    }


    private void recoveData(String userID, Context context) {
        UserHelper.getUser(userID, new UserHelper.GetUserCallback() {
            @Override
            public void onSuccess(UserFirebase user) {
                currentUser = user;
                if (currentUser.getRestaurantChoosed() != null) {
                    String restaurantID = currentUser.getRestaurantChoosed();
                    recoveRestaurantData(restaurantID);
                    recoveAllWorkmates(restaurantID, context);
                }
            }

            @Override
            public void onError(Exception exception) {
                currentUser = null;
            }
        });
    }


    private void recoveRestaurantData(String restaurantID) {
        RestaurantHelper.getTargetedRestaurant(restaurantID, new RestaurantHelper.GetRestaurantsTargetedCallback() {
            @Override
            public void onSuccess(Restaurant restaurant) {
                mRestaurant = restaurant;
            }

            @Override
            public void onError(Exception exception) {
                mRestaurant = null;
            }
        });
    }

    private void recoveAllWorkmates(String restaurantID, Context context) {
        RestaurantHelper.getAllUsers(restaurantID, new RestaurantHelper.GetAllUsersCallback() {
            @Override
            public void onSuccess(List<UserFirebase> list) {
                workmates = list;
                if(workmates.size() > 0 && mRestaurant != null) {
                    sendNotification(context);
                }
            }

            @Override
            public void onError(Exception exception) {
                workmates = null;
            }
        });
    }

    private void convertWorkmatesToString() {
        workmatesName.clear();
        for (int i = 0; i < workmates.size(); i++) {
            workmatesName.add(workmates.get(i).getUsername());
        }
    }


    private void sendNotification(Context context) {

        convertWorkmatesToString();
        //todo fix bug with name send 2 times
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

        String content1 = "Don't forgot !";
        String content2 = "You eat at the restaurant: ";
        String restaurantName = currentUser.getRestaurantName();
        String restaurantAddress = mRestaurant.getVicinity();

        String message = content2 + restaurantName.toUpperCase() + " at " + restaurantAddress + " with " + wNames;
        Log.e(TAG, wNames.toString());


        String CHANNEL_ID = "CHANNEL_ID";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            String description = "This is channel 1";
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notifIntent = new Intent(context, NavigationActivity.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifIntent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        String title = "Go4Lunch";
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

        int notificationID = 1;
        notificationManager.notify(notificationID, builder.build());

    }

}



