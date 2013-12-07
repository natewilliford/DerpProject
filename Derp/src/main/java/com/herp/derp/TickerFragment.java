package com.herp.derp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathan on 12/4/13.
 */
public class TickerFragment extends Fragment {

    public TickerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_ticker, container, false);

        final TextView tickerLast = (TextView) rootView.findViewById(R.id.ticker_last);

        BitstampData bitstamp = App.getBitstamp();

        bitstamp.ticker(new BitstampData.Callback() {
            @Override
            public void resolve(Object response) {
                JSONObject ticker = (JSONObject) response;
                try {
                    tickerLast.setText(ticker.getString("last"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(String message, Exception e) {
                Log.e(MainActivity.LOG_TAG, "couldn't get ticker");
            }
        });

        return rootView;
    }
}