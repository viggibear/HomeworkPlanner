package com.viggi.homeworkplanner;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Vignesh Ravi on 19/4/2015.
 */
public class Homework extends SugarRecord<Homework> {
    private String hwName;
    private String subjName;
    private String notes;
    private Date dueDate;
    private Date reminderDate;
    private int mDone;
    private int mArchived;

    public Homework(){}

    public Homework(String hwName, String subjName, String notes, Date dueDate, Date reminderDate, int mDone, int mArchived){
        super();
        this.hwName = hwName;
        this.subjName = subjName;
        this.notes = notes;
        this.dueDate = dueDate;
        this.reminderDate = reminderDate;
        this.mDone = mDone;
        this.mArchived = mArchived;
    }

    public String getHwName() {
        return hwName;
    }

    public void setHwName(String hwName) {
        this.hwName = hwName;
    }

    public String getSubjName() {
        return subjName;
    }

    public void setSubjName(String subjName) {
        this.subjName = subjName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(Date reminderDate) {
        this.reminderDate = reminderDate;
    }

    public int getmDone() {
        return mDone;
    }

    public void setmDone(int mDone) {
        this.mDone = mDone;
    }

    public int getmArchived() {
        return mArchived;
    }

    public void setmArchived(int mArchived) {
        this.mArchived = mArchived;
    }

    @Override
    public String toString() {
        return "Homework{" +
                "hwName='" + hwName + '\'' +
                ", subjName='" + subjName + '\'' +
                ", notes='" + notes + '\'' +
                ", dueDate=" + dueDate.toString() +
                ", reminderDate=" + reminderDate.toString() +
                ", mDone=" + mDone +
                ", mArchived=" + mArchived +
                '}';
    }
}
