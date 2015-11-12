package com.elrain.bashim.activity.helper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.elrain.bashim.R;
import com.elrain.bashim.activity.MainActivity;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.CounterOfNewItems;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class NotificationHelper {

    public static void showNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setAutoCancel(true);
        builder.setContentText(String.format(context.getString(R.string.notification_text_all),
                CounterOfNewItems.getInstance().getQuotesCounter(), CounterOfNewItems.getInstance().getComicsCounter()));
        Intent intent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constants.ID_NOTIFICATION, builder.build());
        CounterOfNewItems.getInstance().setCounterTooZero();
    }
}
