package com.viggi.homeworkplanner;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.github.johnpersano.supertoasts.SuperToast;

import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.Date;


public class NotificationButtonHandler extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_button_handler);

        final Intent previousIntent = getIntent();
        final int homeworkIndex = previousIntent.getIntExtra("HOMEWORK_INDEX", 0);
        Homework homework = Homework.listAll(Homework.class).get(homeworkIndex);

        cancelNotification(this, 001);

        /*if (previousIntent.getStringExtra("IS_DONE").equals("true")){
            homework.setmDone(1);
            SuperToast.create(this, "Homework Marked as Done", SuperToast.Duration.LONG).show();
            Intent intent = new Intent(NotificationButtonHandler.this, HomeworkActivity.class);
            intent.putExtra("HOMEWORK_INDEX", homeworkIndex);
            startActivity(intent);
        }*/

         if (previousIntent.getStringExtra("IS_DONE").equals("false")){
            Calendar newReminderDate = Calendar.getInstance(); // creates calendar
            newReminderDate.setTime(new Date()); // sets calendar time/date
            newReminderDate.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
            homework.setReminderDate(newReminderDate.getTime()); // returns new date object, one hour in the future
            homework.save();

            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(NotificationButtonHandler.this, AlarmReceiver.class);
            alarmIntent.putExtra("HOMEWORK_NAME", homework.getHwName());
            alarmIntent.putExtra("DUE_DATE_STRING", new LocalDate(homework.getDueDate()).toString());
            alarmIntent.putExtra("HOMEWORK_INDEX", homeworkIndex);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(NotificationButtonHandler.this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, newReminderDate.getTimeInMillis(), pendingIntent);

            SuperToast.create(this, "Reminder Postponed by 1 Hour", SuperToast.Duration.LONG).show();
            Intent intent = new Intent(NotificationButtonHandler.this, HomeworkActivity.class);
            intent.putExtra("HOMEWORK_INDEX", homeworkIndex);
            startActivity(intent);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_button_handler, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }
}
