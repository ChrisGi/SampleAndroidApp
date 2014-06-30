package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import model.User;

/**
 * Created by root on 29.06.14.
 */
public class DataBaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "testdb.db";
    private static final int DATABASE_VERSION = 1;

    private static DataBaseHelper instance;

    public static synchronized DataBaseHelper getInstance(Context context) {

        if (instance == null) {
            instance = new DataBaseHelper(context);
        }

        return instance;

    }

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public SQLiteDatabase getDataBase(boolean writable) throws SQLiteException {
        return writable ? getWritableDatabase() : getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            String userTable = "CREATE TABLE "
                    + User.TABLE_NAME + "( "
                    + User.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + User.COLUMN_NAME + " TEXT );";

            db.execSQL("PRAGMA foreign_keys=ON;");

            db.execSQL(userTable);


        }catch (SQLiteException ex){
            throw new SQLiteException(ex.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion > oldVersion) {

            db.execSQL("PRAGMA foreign_keys=ON;");

            for (int i = oldVersion; i < newVersion; i++) {

                int nextVersion = i + 1;

                switch (nextVersion) {

                    case 2:


                        break;

                }

            }

        }

    }
}
