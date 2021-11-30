package fr.univangers.master.devmobile;

import android.provider.BaseColumns;

public class TaskContract {
    public TaskContract(){}

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TaskEntry.TABLE_NAME + "(" + TaskEntry._ID + " INTEGER PRIMARY KEY, " + TaskEntry.COLUMN_WEIGHT + " INTEGER, " + TaskEntry.COLUMN_LABEL + " TEXT)";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

    public static class TaskEntry implements BaseColumns{
        public static final String TABLE_NAME= "tasks";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_LABEL = "label";
    }
}
