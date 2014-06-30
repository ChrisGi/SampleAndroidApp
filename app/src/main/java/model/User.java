package model;

import android.net.Uri;

import database.DataBaseProvider;

/**
 * Created by root on 29.06.14.
 */
public class User {

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + DataBaseProvider.AUTHORITY + "/user");

    public static final String TABLE_NAME = "user";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";

    public long id;
    public String name;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
