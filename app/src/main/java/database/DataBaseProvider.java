package database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;

import model.User;

/**
 * Created by root on 29.06.14.
 */
public class DataBaseProvider extends ContentProvider {

    public static final String AUTHORITY = "com.database.DataBaseProvider";

    private DataBaseHelper dataBaseHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    private static final int USER = 1010;
    private static final int USER_ID = 1020;

    static {

        uriMatcher.addURI(AUTHORITY, "user", USER);
        uriMatcher.addURI(AUTHORITY, "user" + "/#", USER_ID);

    }

    @Override
    public boolean onCreate() {

        dataBaseHelper = DataBaseHelper.getInstance(getContext());

        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getDataBase(false);
        SQLiteQueryBuilder queryBuilder;

        switch (uriMatcher.match(uri)) {

            case USER:

                queryBuilder = new SQLiteQueryBuilder();

                queryBuilder.setTables(User.TABLE_NAME);

                cursor = queryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);


                break;

            case USER_ID:

                queryBuilder = new SQLiteQueryBuilder();

                queryBuilder.setTables(User.TABLE_NAME);
                queryBuilder.appendWhere(User.COLUMN_ID + " = " + uri.getLastPathSegment());

                cursor = queryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);

                break;

            default:

                throw new IllegalArgumentException("Error: Unknown Uri " + uri);
        }

        if (cursor != null) {

            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            return cursor;

        } else {

            return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getDataBase(true);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        long id;

        switch (uriMatcher.match(uri)){

            case USER:

                id = sqLiteDatabase.insert(User.TABLE_NAME, null, values);

                break;

            default:

                throw new IllegalArgumentException("Error: Unknown Uri " + uri);

        }
        if (id > 0) {

            getContext().getContentResolver().notifyChange(uri, null);

            return Uri.parse(uri + "/" + id);

        } else {
            return null;
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getDataBase(true);
        int rowsCount;
        String id;
        String where;
        int returnId = 0;

        switch (uriMatcher.match(uri)){

            case USER:

                rowsCount = sqLiteDatabase.delete(User.TABLE_NAME, selection, selectionArgs);

                break;

            case USER_ID:

                id = uri.getLastPathSegment();

                where = User.COLUMN_ID + " = " + id;

                if (!TextUtils.isEmpty(selection)) {

                    where += " AND " + selection;
                }

                rowsCount = sqLiteDatabase.delete(User.TABLE_NAME, where, selectionArgs);

                returnId = Integer.parseInt(id);

                break;

            default:

                throw new IllegalArgumentException("Error: Unknown Uri " + uri);

        }

        if (rowsCount > 0) {

            getContext().getContentResolver().notifyChange(uri, null);

            return returnId;

        } else {
            return 0;
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getDataBase(true);
        int rowsCount;
        String id = uri.getLastPathSegment();
        String where;

        switch (uriMatcher.match(uri)){

            case USER_ID:

                where = User.COLUMN_ID + " = " + id;

                if (!TextUtils.isEmpty(selection)) {

                    where += " AND " + selection;
                }

                rowsCount = sqLiteDatabase.update(User.TABLE_NAME, values, where, selectionArgs);

                break;

            default:

                throw new IllegalArgumentException("Error: Unknown Uri " + uri);

        }

        if (rowsCount > 0) {

            getContext().getContentResolver().notifyChange(uri, null);

            return Integer.parseInt(uri.getLastPathSegment());

        } else {
            return 0;
        }

    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {

        ContentProviderResult[] result = new ContentProviderResult[operations.size()];
        int i = 0;
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getDataBase(true);
        sqLiteDatabase.beginTransaction();

        for (ContentProviderOperation operation : operations) {
            // Chain the result for back references
            result[i++] = operation.apply(this, result, i);
        }

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();

        return result;
    }
}
