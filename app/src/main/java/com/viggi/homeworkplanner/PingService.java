package com.viggi.homeworkplanner;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Vignesh Ravi on 2/5/2015.
 */
public class PingService extends IntentService {

    public PingService() {

        // The super call is required. The background thread that IntentService
        // starts is labeled with the string argument you pass.
        super("com.viggi.homeworkplanner");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        final String mHomeworkName = intent.getStringExtra("HOMEWORK_NAME");
        final String mDueDateString = intent.getStringExtra("DUE_DATE_STRING");
        final int mHomeworkIndex = intent.getIntExtra("HOMEWORK_INDEX", 0);
        final List<Homework> homeworkList = Homework.listAll(Homework.class);
        final Homework homework = homeworkList.get(mHomeworkIndex);

        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        String action = intent.getAction();
        if(action.equals(CommonConstants.ACTION_DONE)) {
            homework.setmDone(1);
            homework.save();
            nm.cancel(001);
        }
        else if (action.equals(CommonConstants.ACTION_REMIND_ONE_HOUR)){
            Date reminderDate = homework.getReminderDate();
            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(reminderDate); // sets calendar time/date
            cal.add(Calendar.MINUTE, 1); // adds one hour
            reminderDate = cal.getTime(); // returns new date object, one hour in the future

            homework.setReminderDate(reminderDate);
            homework.save();

            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            alarmIntent.putExtra("HOMEWORK_NAME", homework.getHwName());
            alarmIntent.putExtra("DUE_DATE_STRING", mDueDateString);
            alarmIntent.putExtra("HOMEWORK_INDEX", Homework.listAll(Homework.class).indexOf(homework));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

            nm.cancel(001);
        }
        Log.d(CommonConstants.DEBUG_TAG, CommonConstants.ACTION_REMIND_ONE_HOUR);


    }
}
