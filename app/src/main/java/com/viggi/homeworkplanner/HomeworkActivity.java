package com.viggi.homeworkplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

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


public class HomeworkActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Toolbar mToolbar;
    private Homework homework;

    public static final String DUE_DATEPICKER_TAG = "duedatepicker";
    public static final String REMINDER_DATEPICKER_TAG = "reminderdatepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    final Calendar calendar = Calendar.getInstance();

    final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
    final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

    MaterialEditText mDueDate;
    MaterialEditText mReminderDate;
    MaterialEditText mReminderTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        final Intent intent = getIntent();
        final int hwIndex = intent.getIntExtra("HOMEWORK_INDEX", 0);
        homework = Homework.listAll(Homework.class).get(hwIndex);

        setTitle("View Homework");

        final MaterialEditText mAssignmentName = (MaterialEditText) findViewById(R.id.add_assmname_edittext);
        final MaterialEditText mSubjectName = (MaterialEditText) findViewById(R.id.add_subjname_edittext);
        final MaterialEditText mNotes = (MaterialEditText) findViewById(R.id.add_notes_edittext);

        mDueDate = (MaterialEditText) findViewById(R.id.add_duedate_edittext);
        mReminderDate = (MaterialEditText) findViewById(R.id.add_reminderdate_edittext);
        mReminderTime = (MaterialEditText) findViewById(R.id.add_remindertime_edittext);

        final CheckBox doneCheckBox = (CheckBox) findViewById(R.id.homework_done_check_box);
        final FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.homework_edit_fab);

        mAssignmentName.setText(homework.getHwName());
        mSubjectName.setText(homework.getSubjName());
        mNotes.setText(homework.getNotes());

        switch (homework.getmDone()){
            case (0): doneCheckBox.setChecked(false);
                        break;
            case (1): doneCheckBox.setChecked(true);
                        break;
        }

        final LocalDateTime dueDate = new LocalDateTime(homework.getDueDate());
        final LocalDateTime reminderDateTime = new LocalDateTime(homework.getReminderDate());
        final String datePattern = "dd-MM-yy";
        final String timePatten = "HH:mm";
        final String dueDateString = dueDate.toString(DateTimeFormat.forPattern(datePattern));
        final String reminderDateString = reminderDateTime.toString(DateTimeFormat.forPattern(datePattern));
        final String reminderTimeString = reminderDateTime.toString(DateTimeFormat.forPattern(timePatten));

        mDueDate.setText(dueDateString);
        mReminderDate.setText(reminderDateString);
        mReminderTime.setText(reminderTimeString);

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

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mAssignmentNameString = mAssignmentName.getText().toString();
                final String mSubjectNameString = mSubjectName.getText().toString();
                final String mNotesString = mNotes.getText().toString();
                final String mDueDateString = mDueDate.getText().toString();
                final String mReminderDateString = mReminderDate.getText().toString();
                final String mReminderTimeString = mReminderTime.getText().toString();
                final boolean mChecked = doneCheckBox.isChecked();

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

                if(mReminderDateString.isEmpty() || mReminderTimeString.isEmpty()){
                    errorToast(getString(R.string.incomplete_reminder_fields));
                    return;
                }

                if ((mReminderDateString.isEmpty() && !mReminderTimeString.isEmpty()) || (!mReminderDateString.isEmpty() && mReminderTimeString.isEmpty())) {
                    errorToast(getString(R.string.incomplete_reminder_fields));
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
                    if(!reminderDateTime.isAfter(new LocalDateTime())){
                        errorToast(getString(R.string.reminder_before_now));
                        return;
                    }
                }

                homework.setHwName(mAssignmentNameString);
                homework.setSubjName(mSubjectNameString);
                homework.setNotes(mNotesString);
                homework.setDueDate(dueDate.toDate());
                homework.setReminderDate(reminderDateTime.toDate());

                if (mChecked){
                    homework.setmDone(1);
                }
                else if (!mChecked){
                    homework.setmDone(0);
                }

                homework.save();

                AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(HomeworkActivity.this, AlarmReceiver.class);
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
                PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeworkActivity.this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, reminderCalendarDate.getTimeInMillis(), pendingIntent);

                Intent intent = new Intent(HomeworkActivity.this, ViewHomeworkActivity.class);
                startActivity(intent);

            }
        });





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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homework, menu);
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
        if (id == R.id.action_export){
            Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
            calendarIntent.setType("vnd.android.cursor.item/event");
            Calendar dueDate = Calendar.getInstance();
            dueDate.setTime(homework.getDueDate());
            calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dueDate.getTimeInMillis()+60*60*1000*6);
            calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dueDate.getTimeInMillis()+60*60*1000*8);
            calendarIntent.putExtra(CalendarContract.Events.TITLE, "HW Due: "+homework.getHwName());
            startActivity(calendarIntent);
        }
        return super.onOptionsItemSelected(item);

    }

    public void errorToast(String errorText) {
        SuperActivityToast superToast = new SuperActivityToast(HomeworkActivity.this);
        superToast.setDuration(SuperToast.Duration.SHORT);
        superToast.setText(errorText);
        superToast.setIcon(R.drawable.ic_error_white_18dp, SuperToast.IconPosition.LEFT);
        superToast.show();
    }
}
