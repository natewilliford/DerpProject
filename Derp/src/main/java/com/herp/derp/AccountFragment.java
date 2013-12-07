package com.herp.derp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathan on 12/4/13.
 */
public class AccountFragment extends Fragment {

    TextView tickerLastTextView;
     BitstampData bitstamp;

    TextView btcBalanceTextView;
    TextView usdBalanceTextView;
    Button updateTickerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bitstamp = App.getBitstamp();
        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        btcBalanceTextView = (TextView) rootView.findViewById(R.id.btc_balance);
        usdBalanceTextView = (TextView) rootView.findViewById(R.id.usd_balance);
        tickerLastTextView = (TextView) rootView.findViewById(R.id.ticker_last);
        updateTickerButton = (Button) rootView.findViewById(R.id.update_ticker);

        updateTickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh(true);
            }
        });

        refresh(false);

        return rootView;
    }

    private void refresh(boolean forceRefresh) {
        bitstamp.ticker(new BitstampData.Callback() {
            @Override
            public void resolve(Object response) {
                final JSONObject ticker = (JSONObject) response;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String last = ticker.getString("last");
                            Log.e(MainActivity.LOG_TAG, "Got ticker: " + last);
                            tickerLastTextView.setText(last);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void error(String message, Exception e) {
                Log.e(MainActivity.LOG_TAG, "couldn't get ticker");
                e.printStackTrace();
            }
        }, forceRefresh);

        bitstamp.balance(new BitstampData.Callback() {
            @Override
            public void resolve(Object response) {
                final JSONObject balance = (JSONObject) response;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            btcBalanceTextView.setText(balance.getString("btc_balance"));
                            usdBalanceTextView.setText(balance.getString("usd_balance"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void error(String message, Exception e) {
                Log.e(MainActivity.LOG_TAG, "couldn't get balance");
            }
        }, forceRefresh);
    }
}