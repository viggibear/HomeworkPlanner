package com.viggi.homeworkplanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;


public class ViewHomeworkActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    String pickString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_homework);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        final FloatingActionButton mAddHwButton = (FloatingActionButton) findViewById(R.id.view_add_fab);
        mAddHwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewHomeworkActivity.this, AddHomeworkActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.view_homework_recyclerview);
        mAddHwButton.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        List<Homework> homeworkList = getHomeworkList(pickString);
        setmRecyclerView(homeworkList);
        setListeners(mRecyclerView);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (position == 0) {
            pickString = "Due";
            setTitle("Due");
        } else if (position == 1) {
            pickString = "Done";
            setTitle("Done");
        } else if (position == 2) {
            pickString = "Archived";
            setTitle("Archived");

        }
        try {
            List<Homework> homeworkList = getHomeworkList(pickString);
            setmRecyclerView(homeworkList);
        } catch (NullPointerException e) {
            return;
        }

    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_view_homework, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    private void setmRecyclerView(List<Homework> homeworkList) {
        mAdapter = new HomeworkViewAdapter(homeworkList, ViewHomeworkActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public List<Homework> getHomeworkList(String pickString) {
        List<Homework> homeworkList = Homework.listAll(Homework.class);
        if (pickString.equals("Due")) {
            for (Homework homework : homeworkList) {
                if (homework.getmDone() == 1 || homework.getmArchived() == 1) {
                    homeworkList.remove(homework);
                }
            }
        } else if (pickString.equals("Done")) {
            for (Homework homework : homeworkList) {
                if (homework.getmDone() == 0 || homework.getmArchived() == 1) {
                    homeworkList.remove(homework);
                }
            }
        } else if (pickString.equals("Archived")) {
            for (Homework homework : homeworkList) {
                if (homework.getmArchived() == 0) {
                    homeworkList.remove(homework);
                }
            }
        }
        return homeworkList;
    }

    public void setListeners(RecyclerView mRecyclerView) {
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemSingleClickListener(this, new RecyclerItemSingleClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(ViewHomeworkActivity.this, HomeworkActivity.class);
                        intent.putExtra("HOMEWORK_INDEX", position);
                        startActivity(intent);
                    }
                }
        ));

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(mRecyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    List<Homework> homeworkList = getHomeworkList(pickString);
                                                    Homework homework = homeworkList.get(position);
                                                    homeworkList.remove(homework);
                                                    homework.delete();
                                                    setmRecyclerView(getHomeworkList(pickString));
                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    dialog.cancel();
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewHomeworkActivity.this);
                                    builder.setMessage("Are you sure you want to delete this?").setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();

                                }


                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    final List<Homework> homeworkList = getHomeworkList(pickString);
                                    final Homework homework = homeworkList.get(position);
                                    if (homework.getmArchived() == 0) {
                                        homework.setmArchived(1);
                                        homework.save();
                                        homeworkList.remove(position);
                                        setmRecyclerView(homeworkList);
                                        SuperActivityToast superActivityToast = new SuperActivityToast(ViewHomeworkActivity.this, SuperToast.Type.BUTTON);
                                        superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
                                        superActivityToast.setText("Homework Archived");
                                        superActivityToast.setButtonIcon(SuperToast.Icon.Dark.UNDO, "UNDO");
                                        OnClickWrapper onClickWrapper = new OnClickWrapper("superactivitytoast", new SuperToast.OnClickListener() {
                                            @Override
                                            public void onClick(View view, Parcelable token) {
                                                homework.setmArchived(0);
                                                homework.save();
                                                List<Homework> homeworkList = getHomeworkList(pickString);
                                                setmRecyclerView(homeworkList);
                                            }

                                        });
                                        superActivityToast.setOnClickWrapper(onClickWrapper);
                                        superActivityToast.show();
                                    } else if (homework.getmArchived() == 1) {
                                        homework.setmArchived(0);
                                        homework.save();
                                        homeworkList.remove(position);
                                        setmRecyclerView(homeworkList);
                                        SuperActivityToast superActivityToast = new SuperActivityToast(ViewHomeworkActivity.this, SuperToast.Type.BUTTON);
                                        superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
                                        superActivityToast.setText("Homework Unarchived");
                                        superActivityToast.setButtonIcon(SuperToast.Icon.Dark.UNDO, "UNDO");
                                        OnClickWrapper onClickWrapper = new OnClickWrapper("superactivitytoast", new SuperToast.OnClickListener() {
                                            @Override
                                            public void onClick(View view, Parcelable token) {
                                                homework.setmArchived(1);
                                                homework.save();
                                                List<Homework> homeworkList = getHomeworkList(pickString);
                                                setmRecyclerView(homeworkList);
                                            }

                                        });
                                        superActivityToast.setOnClickWrapper(onClickWrapper);
                                        superActivityToast.show();
                                    }

                                }


                            }
                        });
        mRecyclerView.addOnItemTouchListener(swipeTouchListener);
    }
}