package com.aeragi.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import model.User;


public class MyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if (savedInstanceState == null) {



            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        //private static final String TAG = PlaceholderFragment.class.getSimpleName();
        private static final int LOADER_ID = 1;

        UserListAdapter listAdapter;

        ListView list;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            listAdapter = new UserListAdapter(getActivity(), R.layout.user_info_row, null, new String[]{}, new int[]{}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


//            ContentValues values = new ContentValues();
//            values.put(User.COLUMN_NAME, "admin");
//
//            Uri u = getActivity().getContentResolver().insert(User.CONTENT_URI, values);
//
//            Log.i(TAG, u.toString());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);

            list = (ListView) rootView.findViewById(R.id.list_fr_my);
            list.setAdapter(listAdapter);

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();

            load(false);
        }

        private void load(boolean forceLoad) {

            if (forceLoad) {

                getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, usersLoader);

            } else {
                getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, usersLoader);
            }

        }

        private LoaderManager.LoaderCallbacks<Cursor> usersLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(getActivity(), User.CONTENT_URI, null, null, null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

                if (data != null && data.moveToFirst()){

                    listAdapter.swapCursor(data);

                }

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

                if (listAdapter != null) listAdapter.swapCursor(null);

            }
        };

        private class UserListAdapter extends SimpleCursorAdapter {

            private final Context context;
            private ViewHolder viewHolder;
            private final int layout;

            public UserListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
                super(context, layout, c, from, to, flags);

                this.context = context;
                this.layout = layout;

            }

            class ViewHolder{

                private TextView userName;

                public ViewHolder(View view) {

                    userName = (TextView) view.findViewById(R.id.user_name);

                }

                private void populateUserInfo(Cursor cursor){

                    userName.setText(cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME)));

                }
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                Cursor cursor = getCursor();

                if (!cursor.moveToPosition(position)) {
                    throw new IllegalStateException(
                            "cursor can't move to this position");
                }

                if (convertView == null) {

                    convertView = newView(context, cursor, parent);

                    viewHolder = new ViewHolder(convertView);

                    convertView.setTag(viewHolder);

                } else {

                    viewHolder = (ViewHolder) convertView.getTag();

                }

                bindView(convertView, context, cursor);

                return convertView;

            }

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                return inflater.inflate(layout, null);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);

                viewHolder = (ViewHolder) view.getTag();
                viewHolder.populateUserInfo(cursor);

            }
        }
    }

}
