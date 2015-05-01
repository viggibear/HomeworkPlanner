package com.viggi.homeworkplanner;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

/**
 * Created by Vignesh Ravi on 26/4/2015.
 */
public class HomeworkViewAdapter extends RecyclerView.Adapter<HomeworkViewAdapter.ViewHolder> {
    private List<Homework> homeworkList;
    private Context context;

    public HomeworkViewAdapter(List<Homework> homeworkList, Context context){
        this.homeworkList = homeworkList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.homework_card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final Homework homework = homeworkList.get(i);
        LocalDate jodadueDate = new LocalDate(homework.getDueDate());
        String dueDateString = DateTimeFormat.forPattern("dd MMM yy").print(jodadueDate);

        boolean doneChecked = false;
        switch (homework.getmDone()) {
            case 0: doneChecked = false;
                    break;
            case 1: doneChecked = true;
                    break;
        }

        viewHolder.mHomeworkName.setText(homework.getHwName());
        viewHolder.mSubjectName.setText(homework.getSubjName());
        viewHolder.mDueDate.setText("Due: "+dueDateString);
        viewHolder.mCheckBox.setChecked(doneChecked);


        final boolean checked = doneChecked;
        final int position = i;
        viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                Activity activity = (Activity) context;
                if (homework.getmDone()==1){
                    homework.setmDone(0);
                    homework.save();
                    removeAt(position);
                    SuperActivityToast superActivityToast = new SuperActivityToast(activity, SuperToast.Type.BUTTON);
                    superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
                    superActivityToast.setText("Homework Incomplete");
                    superActivityToast.setButtonIcon(SuperToast.Icon.Dark.UNDO, "UNDO");
                    OnClickWrapper onClickWrapper = new OnClickWrapper("superactivitytoast", new SuperToast.OnClickListener() {
                        @Override
                        public void onClick(View view, Parcelable token) {
                            homework.setmDone(1);
                            insert(homework, position);
                        }

                    });
                    superActivityToast.setOnClickWrapper(onClickWrapper);
                    superActivityToast.show();
                }
                else if (homework.getmDone()==0){
                    homework.setmDone(1);
                    homework.save();
                    removeAt(position);
                    SuperActivityToast superActivityToast = new SuperActivityToast(activity, SuperToast.Type.BUTTON);
                    superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
                    superActivityToast.setText("Homework Done");
                    superActivityToast.setButtonIcon(SuperToast.Icon.Dark.UNDO, "UNDO");
                    OnClickWrapper onClickWrapper = new OnClickWrapper("superactivitytoast", new SuperToast.OnClickListener() {
                        @Override
                        public void onClick(View view, Parcelable token) {
                            homework.setmDone(0);
                            homework.save();
                            insert(homework, position);
                        }

                    });
                    superActivityToast.setOnClickWrapper(onClickWrapper);
                    superActivityToast.show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return homeworkList == null ? 0 : homeworkList.size();
    }

    public void insert (Homework homework, int position) {
        homeworkList.add(position, homework);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, homeworkList.size());
    }

    public void removeAt(int position) {
        homeworkList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, homeworkList.size());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mHomeworkName;
        public TextView mSubjectName;
        public TextView mDueDate;
        public CheckBox mCheckBox;

        public ViewHolder(View v) {
            super(v);
            mHomeworkName = (TextView) v.findViewById(R.id.cardview_hwname_textview);
            mSubjectName = (TextView) v.findViewById(R.id.cardview_subjectname_textview);
            mDueDate = (TextView) v.findViewById(R.id.cardview_duedate_textview);
            mCheckBox = (CheckBox) v.findViewById(R.id.cardview_done_checkbox);
        }
    }
}


