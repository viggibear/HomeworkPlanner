package com.viggi.homeworkplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.melnykov.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;
import java.util.List;


public class AddHomeworkActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String DUE_DATEPICKER_TAG = "duedatepicker";
    public static final String REMINDER_DATEPICKER_TAG = "reminderdatepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    final Calendar calendar = Calendar.getInstance();

    final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
    final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

    MaterialEditText mAssignmentName;
    MaterialEditText mSubjectName;
    MaterialEditText mNotes;
    EditText mDueDate;
    EditText mReminderDate;
    EditText mReminderTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_homework);

        mAssignmentName = (MaterialEditText) findViewById(R.id.add_assmname_edittext);
        mSubjectName = (MaterialEditText) findViewById(R.id.add_subjname_edittext);
        mNotes = (MaterialEditText) findViewById(R.id.add_notes_edittext);
        mDueDate = (EditText) findViewById(R.id.add_duedate_edittext);
        mReminderDate = (EditText) findViewById(R.id.add_reminderdate_edittext);
        mReminderTime = (EditText) findViewById(R.id.add_remindertime_edittext);

        mDueDate.setFocusable(false);
        mReminderDate.setFocusable(false);
        mReminderTime.setFocusable(false);

        mDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(false);
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), DUE_DATEPICKER_TAG);
            }
        });

        mReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(false);
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), REMINDER_DATEPICKER_TAG);
            }
        });

        mReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.setVibrate(false);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
            }
        });

        FloatingActionButton mConfirmButton = (FloatingActionButton) findViewById(R.id.add_confirm_fab);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mAssignmentNameString = mAssignmentName.getText().toString();
                final String mSubjectNameString = mSubjectName.getText().toString();
                final String mNotesString = mNotes.getText().toString();
                final String mDueDateString = mDueDate.getText().toString();
                final String mReminderDateString = mReminderDate.getText().toString();
                final String mReminderTimeString = mReminderTime.getText().toString();

                if (mAssignmentNameString.isEmpty()) {
                    mAssignmentName.setError(getString(R.string.empty_error_string));
                }

                if (mSubjectNameString.isEmpty()) {
                    mSubjectName.setError(getString(R.string.empty_error_string));
                    return;
                }

                if (mDueDateString.isEmpty()) {
                    errorToast(getString(R.string.empty_due_date));
                    return;
                }

                if ((mReminderDateString.isEmpty() && !mReminderTimeString.isEmpty()) || (!mReminderDateString.isEmpty() && mReminderTimeString.isEmpty())) {
                    errorToast(getString(R.string.incomplete_reminder_fields));
                    return;
                }

                for(Homework homeworkObject:Homework.listAll(Homework.class)){
                    if (homeworkObject.getSubjName().equals(mSubjectNameString) && homeworkObject.getHwName().equals(mAssignmentNameString)){
                        errorToast(getString(R.string.homework_exists_string));
                        break;
                    }
                    return;
                }

                final String datePattern = "dd-MM-yy";
                final String dateTimePattern = "dd-MM-yy HH:mm";
                final LocalDate dueDate = LocalDate.parse(mDueDateString, DateTimeFormat.forPattern(datePattern));

                LocalDateTime reminderDateTime = new LocalDateTime();
                if (!mReminderDateString.isEmpty() && !mReminderTimeString.isEmpty()) {
                    reminderDateTime = LocalDateTime.parse(mReminderDateString + " " + mReminderTimeString, DateTimeFormat.forPattern(dateTimePattern));
                    if (reminderDateTime.isAfter(LocalDateTime.parse(mDueDateString + " 00:00", DateTimeFormat.forPattern(dateTimePattern)))) {
                        errorToast(getString(R.string.faulty_reminder_chronlogy));
                        return;
                    }
                }

                Homework homework = new Homework(mAssignmentNameString, mSubjectNameString, mNotesString, dueDate.toDate(), reminderDateTime.toDate(), 0, 0);
                homework.save();

                AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(AddHomeworkActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra("HOMEWORK_NAME", homework.getHwName());
                alarmIntent.putExtra("DUE_DATE_STRING", mDueDateString);
                List<Homework> homeworkList = Homework.listAll(Homework.class);
                for (int i = 0; i < homeworkList.size(); i++){
                    Homework homeworkObject = homeworkList.get(i);
                    if (homeworkObject.getSubjName().equals(homework.getSubjName()) && homeworkObject.getHwName().equals(homework.getHwName())){
                        alarmIntent.putExtra("HOMEWORK_INDEX", i);
                    }
                }
                Calendar reminderCalendarDate = Calendar.getInstance();
                reminderCalendarDate.setTime(reminderDateTime.toDate());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AddHomeworkActivity.this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, reminderCalendarDate.getTimeInMillis(), pendingIntent);

                Intent intent = new Intent(AddHomeworkActivity.this, ViewHomeworkActivity.class);
                startActivity(intent);

                /*Log.d("checkHwSize", String.valueOf(Homework.listAll(Homework.class).size()));
                Homework homework1 = Homework.listAll(Homework.class).get(0);
                Log.d("checkHw", homework1.toString());
                homework1.delete();
                Log.d("checkHwSize", String.valueOf(Homework.listAll(Homework.class).size()));*/

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_homework, menu);
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

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        if (datePickerDialog.getTag().equals(DUE_DATEPICKER_TAG)) {
            String dueDateString = String.format("%02d", day) + "-" + String.format("%02d", month + 1) + "-" + String.valueOf(year);
            mDueDate.setText(dueDateString);
        }
        if (datePickerDialog.getTag().equals(REMINDER_DATEPICKER_TAG)) {
            String reminderDateString = String.format("%02d", day) + "-" + String.format("%02d", month + 1) + "-" + String.valueOf(year);
            mReminderDate.setText(reminderDateString);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String reminderTimeString = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
        mReminderTime.setText(reminderTimeString);
    }

    public void errorToast(String errorText) {
        SuperActivityToast superToast = new SuperActivityToast(AddHomeworkActivity.this);
        superToast.setDuration(SuperToast.Duration.SHORT);
        superToast.setText(errorText);
        superToast.setIcon(R.drawable.ic_error_white_18dp, SuperToast.IconPosition.LEFT);
        superToast.show();
    }
}
