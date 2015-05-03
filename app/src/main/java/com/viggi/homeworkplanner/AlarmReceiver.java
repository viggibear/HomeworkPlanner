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
        final int homeworkIndex = intent.getIntExtra("HOMEWORK_INDEX", 0);

        Intent doneIntent = new Intent(context, NotificationButtonHandler.class);
        doneIntent.putExtra("HOMEWORK_INDEX", homeworkIndex);
        doneIntent.putExtra("IS_DONE", "true");
        PendingIntent donePendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        doneIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

        Intent postponeIntent = new Intent(context, NotificationButtonHandler.class);
        postponeIntent.putExtra("HOMEWORK_INDEX", homeworkIndex);
        postponeIntent.putExtra("IS_DONE", "false");
        PendingIntent postponePendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        postponeIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(mHomeworkName)
                        .setContentText("Due: " + mDueDateString)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(mHomeworkName + " is Due on " + mDueDateString))
                        /*.addAction(R.drawable.ic_done_black_18dp,
                                context.getString(R.string.done), donePendingIntent)*/
                        .addAction(R.drawable.ic_access_alarm_black_18dp,
                                context.getString(R.string.one_hour_remind), postponePendingIntent)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, HomeworkActivity.class);
        resultIntent.putExtra("HOMEWORK_INDEX", homeworkIndex);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 001;
        Homework homeworkObject = Homework.listAll(Homework.class).get(homeworkIndex);

        if (homeworkObject.getmArchived() == 0){
            NotificationManager mNotifyMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }

    }
}