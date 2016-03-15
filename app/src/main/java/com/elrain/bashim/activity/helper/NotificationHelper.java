package com.elrain.bashim.activity.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.elrain.bashim.R;
import com.elrain.bashim.activity.MainActivity;
import com.elrain.bashim.receiver.BashBroadcastReceiver;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;

public class NotificationHelper {

    public static void showNotification(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean(context.getString(R.string.preferences_key_notifications), false)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle(context.getString(R.string.app_name));
            builder.setAutoCancel(true);
            builder.setContentText(context.getResources().getQuantityString(R.plurals.notification_text,
                    BashPreferences.getInstance(context).getQuotesCounter(),
                    BashPreferences.getInstance(context).getQuotesCounter()));
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constants.KEY_OPEN_MAIN_ACTIVITY, true);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = builder.build();
            notification.deleteIntent = PendingIntent.getBroadcast(context, 0, getCancelIntent(context), 0);
            mNotificationManager.notify(Constants.ID_NOTIFICATION, notification);
        }
    }

    @NonNull
    private static Intent getCancelIntent(Context context) {
        Intent cancelIntent = new Intent(context, BashBroadcastReceiver.class);
        cancelIntent.setAction(Constants.INTENT_CANCEL);
        return cancelIntent;
    }
}
