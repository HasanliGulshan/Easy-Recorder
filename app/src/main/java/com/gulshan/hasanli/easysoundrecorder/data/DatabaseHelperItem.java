package com.gulshan.hasanli.easysoundrecorder.data;

import android.provider.BaseColumns;

public abstract class DatabaseHelperItem implements BaseColumns {

   public static final String TABLE_NAME = "saved_recordings";

   public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
   public static final String COLUMN_NAME_FILE_PATH = "file_path";
   public static final String COLUMN_NAME_LENGTH = "length";
   public static final String COLUMN_NAME_TIME = "time";
}
