package com.viggi.homeworkplanner;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Vignesh Ravi on 2/5/2015.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String mHomeworkName = intent.getStringExtra("HOMEWORK_NAME");
        String mDueDateString = intent.getStringExtra("DUE_DATE_STRING");

        Intent oneHourRemindIntent = new Intent(context, PingService.class);
        oneHourRemindIntent.setAction(CommonConstants.ACTION_REMIND_ONE_HOUR);
        oneHourRemindIntent.putExtra("HOMEWORK_NAME", mHomeworkName);
        oneHourRemindIntent.putExtra("DUE_DATE_STRING", mDueDateString);
        oneHourRemindIntent.putExtra("HOMEWORK_INDEX",intent.getIntExtra("HOMEWORK_INDEX", 0));
        PendingIntent piRemind = PendingIntent.getService(context, 0, oneHourRemindIntent, 0);

        Intent doneIntent = new Intent(context, PingService.class);
        doneIntent.setAction(CommonConstants.ACTION_DONE);
        doneIntent.putExtra("HOMEWORK_NAME", mHomeworkName);
        doneIntent.putExtra("DUE_DATE_STRING", mDueDateString);
        doneIntent.putExtra("HOMEWORK_INDEX",intent.getIntExtra("HOMEWORK_INDEX", 0));
        PendingIntent piDone = PendingIntent.getService(context, 0, doneIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(mHomeworkName)
                        .setContentText("Due: " + mDueDateString)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(mHomeworkName + " is Due on " + mDueDateString))
                        .addAction(R.drawable.ic_access_alarm_black_18dp, "Snooze", piRemind)
                        .addAction (R.drawable.ic_done_black_18dp, "Done", piDone);

        Intent resultIntent = new Intent(context, HomeworkActivity.class);
        resultIntent.putExtra("HOMEWORK_INDEX",intent.getIntExtra("HOMEWORK_INDEX", 0));
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}