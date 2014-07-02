package services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TestIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_TEST = "services.action.TEST";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM_URL = "services.extra.PARAM_URL";
    private static final String EXTRA_PARAM_RECEIVER = "services.extra.PARAM_RECEIVER";
    private static final String TAG = TestIntentService.class.getSimpleName();

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionTest(Context context, String param1, String param2) {
        Intent intent = new Intent(context, TestIntentService.class);
        intent.setAction(ACTION_TEST);
        intent.putExtra(EXTRA_PARAM_URL, param1);
        intent.putExtra(EXTRA_PARAM_RECEIVER, param2);
        context.startService(intent);
    }


    public TestIntentService() {
        super("TestIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_TEST.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM_URL);
                final String param2 = intent.getStringExtra(EXTRA_PARAM_RECEIVER);
                handleActionTest(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionTest(String paramUrl, String paramReceiver) {

        final Intent receiverIntent = new Intent(paramReceiver);

        if (!paramUrl.equals("")) {

            RequestQueue mRequestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, paramUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {

                            //do something with JSONObject
                            Log.i(TAG, jsonObject.toString());

                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(receiverIntent);
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                            receiverIntent.putExtra("ERROR", volleyError.getMessage());
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(receiverIntent);

                        }
                    }
            );

            mRequestQueue.add(request);

        }


    }


}
