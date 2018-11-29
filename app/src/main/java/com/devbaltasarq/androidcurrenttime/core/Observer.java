package com.devbaltasarq.androidcurrenttime.core;

/** Avoids direct dependency between model and view. */
public interface Observer {
    void setStatus(int strId);
    void setTimeInfo(DataDateTime data);
    void setDefaultValues();
}
