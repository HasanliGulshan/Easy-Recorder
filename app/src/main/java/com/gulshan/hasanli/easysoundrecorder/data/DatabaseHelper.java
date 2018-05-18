package com.gulshan.hasanli.easysoundrecorder.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gulshan.hasanli.easysoundrecorder.interfaces.OnDatabaseChangedListener;
import com.gulshan.hasanli.easysoundrecorder.models.RecordingItems;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String LOG_TAG = "DBHelper";

    private static OnDatabaseChangedListener onDatabaseChangedListener;

    private static final String DATABASE_NAME = "saved_recordings.db";

    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + DatabaseHelperItem.TABLE_NAME + "(" +
                   DatabaseHelperItem._ID + " INTEGER PRIMARY KEY" + " , " + DatabaseHelperItem.COLUMN_NAME_RECORDING_NAME + TEXT_TYPE + " , " +
                   DatabaseHelperItem.COLUMN_NAME_FILE_PATH + TEXT_TYPE + " , " + DatabaseHelperItem.COLUMN_NAME_LENGTH + " INTEGER " + " , " +
                   DatabaseHelperItem.COLUMN_NAME_TIME + " INTEGER " + ")";

    @Override

    public void onCreate(SQLiteDatabase db) {
         db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHelperItem.TABLE_NAME);

    }

    public DatabaseHelper(Context context) {
        super(context, DatabaseHelperItem.TABLE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static void setOnDatabaseChangedListener(OnDatabaseChangedListener listener) {

        onDatabaseChangedListener = listener;

    }

    public RecordingItems getItemAt(int position) {

        SQLiteDatabase database = getReadableDatabase();
        String[] recordArray = {
                DatabaseHelperItem._ID,
                DatabaseHelperItem.COLUMN_NAME_RECORDING_NAME,
                DatabaseHelperItem.COLUMN_NAME_FILE_PATH,
                DatabaseHelperItem.COLUMN_NAME_LENGTH,
                DatabaseHelperItem.COLUMN_NAME_TIME };

        Cursor cursor = database.query(DatabaseHelperItem.TABLE_NAME, recordArray, null, null, null, null, null, null);

        if(cursor.moveToPosition(position)) {

            RecordingItems recordingItems = new RecordingItems();

            recordingItems.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelperItem._ID)));
            recordingItems.setFileName(cursor.getString(cursor.getColumnIndex(DatabaseHelperItem.COLUMN_NAME_RECORDING_NAME)));
            recordingItems.setFilePath(cursor.getString(cursor.getColumnIndex(DatabaseHelperItem.COLUMN_NAME_FILE_PATH)));
            recordingItems.setLength(cursor.getInt(cursor.getColumnIndex(DatabaseHelperItem.COLUMN_NAME_LENGTH)));
            recordingItems.setTime(cursor.getLong(cursor.getColumnIndex(DatabaseHelperItem.COLUMN_NAME_TIME)));

            return recordingItems;

        }

      return null;

    }

    public void removeItemWithId(int position) {

        SQLiteDatabase database = getWritableDatabase();

        String[] whereArgs = {String.valueOf(position)};

        database.delete(DatabaseHelperItem.TABLE_NAME, "_ID=?", whereArgs);

    }

    public Context getContext() {

        return context;

    }

    public long addRecording(String recordingName, String filePath, long length) {

        Log.i(LOG_TAG, recordingName + filePath + length);

        SQLiteDatabase database = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelperItem.COLUMN_NAME_RECORDING_NAME, recordingName);
        contentValues.put(DatabaseHelperItem.COLUMN_NAME_FILE_PATH, filePath);
        contentValues.put(DatabaseHelperItem.COLUMN_NAME_LENGTH, length);
        contentValues.put(DatabaseHelperItem.COLUMN_NAME_TIME, System.currentTimeMillis());

        long rowId = database.insert(DatabaseHelperItem.TABLE_NAME, null, contentValues);

        if(onDatabaseChangedListener != null) {

            onDatabaseChangedListener.onNewDatabaseEntryAdded();

        }

        return rowId;

    }

    public int getCount(){

        SQLiteDatabase database = getReadableDatabase();
        String[] items = {DatabaseHelperItem._ID};

        Cursor cursor = database.query(DatabaseHelperItem.TABLE_NAME, items, null, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }


    public void renameItem(RecordingItems recordingItems, String recordingName, String filePath) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelperItem.COLUMN_NAME_RECORDING_NAME, recordingName);
        contentValues.put(DatabaseHelperItem.COLUMN_NAME_FILE_PATH, filePath);

        long rowId = database.update(DatabaseHelperItem.TABLE_NAME, contentValues,
                DatabaseHelperItem._ID + "=" + recordingItems.getId(), null);

        if (onDatabaseChangedListener != null) {

            onDatabaseChangedListener.onDatabaseEntryRenamed();

        }

    }

}
