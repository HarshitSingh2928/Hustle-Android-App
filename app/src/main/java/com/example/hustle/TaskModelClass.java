package com.example.hustle;

import com.firebase.ui.database.FirebaseRecyclerOptions;

public class TaskModelClass {

    String task,nTime,tTime;
    long hourMilli;

    public String getnTime() {
        return nTime;
    }

    public long getHourMilli() {
        return hourMilli;
    }

    public void setHourMilli(long hourMilli) {
        this.hourMilli = hourMilli;
    }

    public void setnTime(String nTime) {
        this.nTime = nTime;
    }

    public String gettTime() {
        return tTime;
    }

    public void settTime(String tTime) {
        this.tTime = tTime;
    }

    public String getTask() {
        return task;
    }
    public void setTask(String task) {
        this.task = task;
    }
}
