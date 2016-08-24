package com.example.dell_pc.wbin_zhihudaily.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by wbin on 2016/8/22.
 */
public class StoryEntity implements Parcelable {
    private int type;
    private int id;
    private String ga_prefix;
    private String title;
    private List<String> images;
    private boolean isRead = false;
    private String date;
    private boolean isTop=false;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.id);
        dest.writeString(this.ga_prefix);
        dest.writeString(this.title);
        dest.writeStringList(this.images);
        dest.writeByte(this.isRead ? (byte) 1 : (byte) 0);
        dest.writeString(this.date);
        dest.writeByte(this.isTop ? (byte) 1 : (byte) 0);
    }

    public StoryEntity() {
    }

    protected StoryEntity(Parcel in) {
        this.type = in.readInt();
        this.id = in.readInt();
        this.ga_prefix = in.readString();
        this.title = in.readString();
        this.images = in.createStringArrayList();
        this.isRead = in.readByte() != 0;
        this.date = in.readString();
        this.isTop = in.readByte() != 0;
    }

    public static final Parcelable.Creator<StoryEntity> CREATOR = new Parcelable.Creator<StoryEntity>() {
        @Override
        public StoryEntity createFromParcel(Parcel source) {
            return new StoryEntity(source);
        }

        @Override
        public StoryEntity[] newArray(int size) {
            return new StoryEntity[size];
        }
    };
}
