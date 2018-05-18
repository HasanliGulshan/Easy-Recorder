package com.gulshan.hasanli.easysoundrecorder.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordingItems implements Parcelable{

    private String fileName;
    private String filePath;
    private int id;
    private int length;
    private long time; //data and time

    public RecordingItems() {

    }

    public RecordingItems(Parcel in) {

        fileName = in.readString();
        filePath = in.readString();
        id = in.readInt();
        length = in.readInt();
        time = in.readLong();

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

         dest.writeString(fileName);
         dest.writeString(filePath);
         dest.writeInt(id);
         dest.writeInt(length);
         dest.writeLong(time);

    }

    public static final Parcelable.Creator<RecordingItems> CREATOR = new Creator<RecordingItems>() {
        @Override
        public RecordingItems createFromParcel(Parcel source) {
            return new RecordingItems(source);
        }

        @Override
        public RecordingItems[] newArray(int size) {
            return new RecordingItems[size];
        }
    };

}
