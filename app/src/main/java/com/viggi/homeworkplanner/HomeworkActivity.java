package com.viggi.homeworkplanner;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.melnykov.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;


public class HomeworkActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private Homework homework;

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

        final MaterialEditText assignmentNameEditText = (MaterialEditText) findViewById(R.id.add_assmname_edittext);
        final MaterialEditText subjectNameEditText = (MaterialEditText) findViewById(R.id.add_subjname_edittext);
        final MaterialEditText notesEditText = (MaterialEditText) findViewById(R.id.add_notes_edittext);
        final MaterialEditText dueDateEditText = (MaterialEditText) findViewById(R.id.add_duedate_edittext);
        final MaterialEditText reminderDateEditText = (MaterialEditText) findViewById(R.id.add_reminderdate_edittext);
        final MaterialEditText reminderTimeEditText = (MaterialEditText) findViewById(R.id.add_remindertime_edittext);
        final FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.homework_edit_fab);

        assignmentNameEditText.setKeyListener(null); assignmentNameEditText.setText(homework.getHwName());
        subjectNameEditText.setKeyListener(null); subjectNameEditText.setText(homework.getSubjName());
        notesEditText.setKeyListener(null); notesEditText.setText(homework.getNotes());

        final LocalDateTime dueDate = new LocalDateTime(homework.getDueDate());
        final LocalDateTime reminderDateTime = new LocalDateTime(homework.getReminderDate());
        final String datePattern = "dd-MM-yy";
        final String timePatten = "HH:mm";
        final String dueDateString = dueDate.toString(DateTimeFormat.forPattern(datePattern));
        final String reminderDateString = reminderDateTime.toString(DateTimeFormat.forPattern(datePattern));
        final String reminderTimeString = reminderDateTime.toString(DateTimeFormat.forPattern(timePatten));

        dueDateEditText.setKeyListener(null); dueDateEditText.setText(dueDateString);
        reminderDateEditText.setKeyListener(null); reminderDateEditText.setText(reminderDateString);
        reminderTimeEditText.setKeyListener(null); reminderTimeEditText.setText(reminderTimeString);



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
}
