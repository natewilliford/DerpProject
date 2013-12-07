package com.herp.derp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {


    public static final String LOG_TAG = "derp";

    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void login(final String clientId, final String apiKey, final String apiSecret) {
        if (loading) {
            Log.w(LOG_TAG, "already loading");
            return;
        }

        setLoading(true);

        if (clientId.length() == 0) {
            Toast toast = Toast.makeText(this, "Please enter a User ID", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (apiKey.length() == 0) {
            Toast toast = Toast.makeText(this, "Please enter an API Key", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (apiSecret.length() == 0) {
            Toast toast = Toast.makeText(this, "Please enter an API Secret", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        try {
            Log.i(LOG_TAG, "Creating BitstampData object");
            final BitstampData bitstamp = BitstampData.factory(clientId, apiKey, apiSecret);

            Log.i(LOG_TAG, "Authing");
            bitstamp.auth(new BitstampData.Callback() {
                @Override
                public void resolve(Object response) {
                    if ((Boolean) response == true) {
                        SharedPreferences prefs = MainActivity.this.getSharedPreferences(
                                App.PACKAGE_NAME, Context.MODE_PRIVATE);

                        App.setBitstamp(bitstamp);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("clientId", clientId);
                        editor.putString("apiKey", apiKey);
                        editor.putString("apiSecret", apiSecret);
                        editor.commit();

                        Log.i(LOG_TAG, "Saved prefs");

                        Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                        startActivity(intent);
                    } else {
                        Log.w(LOG_TAG, "Could not auth");
                        setLoading(false);
                    }
                }

                @Override
                public void error(String message, Exception e) {
                    Log.w(LOG_TAG, "Could not auth");
                    setLoading(false);
                }
            });
        } catch (Exception e) {
            // TODO toast
            Log.e(LOG_TAG, "Could not build BitstampData.");
            setLoading(false);
        }
    }

    public void setLoading(boolean l) {
        this.loading = l;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            TextView helpTextView = (TextView) rootView.findViewById(R.id.help_text);
            Linkify.addLinks(helpTextView, Linkify.ALL);

            Button b = (Button) rootView.findViewById(R.id.submit_button);

            MainActivity activity = (MainActivity) getActivity();

            SharedPreferences prefs = activity.getSharedPreferences(
                    App.PACKAGE_NAME, Context.MODE_PRIVATE);

            ((EditText) rootView.findViewById(R.id.client_id)).setText(prefs.getString("clientId", ""));
            ((EditText) rootView.findViewById(R.id.api_key)).setText(prefs.getString("apiKey", ""));
            ((EditText) rootView.findViewById(R.id.api_secret)).setText(prefs.getString("apiSecret", ""));

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button b = (Button) view;
                    b.setEnabled(false);
                    final String clientId = ((EditText) rootView.findViewById(R.id.client_id)).getText().toString();
                    final String apiKey = ((EditText) rootView.findViewById(R.id.api_key)).getText().toString();
                    final String apiSecret = ((EditText) rootView.findViewById(R.id.api_secret)).getText().toString();

                    ((MainActivity) getActivity()).login(clientId, apiKey, apiSecret);

                    b.setEnabled(true);
                }
            });

            return rootView;
        }
    }
}
