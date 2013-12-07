package com.herp.derp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.NumberFormat;

/**
 * Created by nathan on 12/5/13.
 */
public class TradeFragment extends Fragment{

    BitstampData bitstamp;

    TextView btcBalanceTextView;
    TextView usdBalanceTextView;
    TextView tickerLastTextView;
    TextView tickerAskTextView;
    TextView tickerBidTextView;
    TextView commissionTextView;

    Switch sellBuySwitch;

    EditText tradeAmountEditText;
    EditText tradePriceEditText;

    TextView totalTextView;

    String currentTradeAmount = "";
    String currentTradePrice = "";

    Button placeOrderButton;

    Double fee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fratment_trade, container, false);

        bitstamp = App.getBitstamp();

        btcBalanceTextView = (TextView) rootView.findViewById(R.id.btc_balance);
        usdBalanceTextView = (TextView) rootView.findViewById(R.id.usd_balance);
        tickerLastTextView = (TextView) rootView.findViewById(R.id.ticker_last);
        tickerAskTextView = (TextView) rootView.findViewById(R.id.ticker_ask);
        tickerBidTextView = (TextView) rootView.findViewById(R.id.ticker_bid);
        commissionTextView = (TextView) rootView.findViewById(R.id.commission);

        totalTextView = (TextView) rootView.findViewById(R.id.total);

        sellBuySwitch = (Switch) rootView.findViewById(R.id.sell_buy_switch);

        tradeAmountEditText = (EditText) rootView.findViewById(R.id.trade_amount);
        tradePriceEditText = (EditText) rootView.findViewById(R.id.trade_price);

        placeOrderButton = (Button) rootView.findViewById(R.id.place_order);

//        tradePriceEditText.setRawInputType(Configuration.KEYBOARD_12KEY);




        tradeAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {
                if(!s.toString().equals("") && !s.toString().equals(".") && !s.toString().equals("0") && !s.toString().equals(currentTradeAmount)){
                    tradeAmountEditText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d\\.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formated = NumberFormat.getNumberInstance().format(parsed);

                    currentTradeAmount = formated;
                    tradeAmountEditText.setText(cleanString);
                    tradeAmountEditText.setSelection(cleanString.length());

                    tradeAmountEditText.addTextChangedListener(this);
                }
                updateTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        tradePriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(currentTradePrice)){
                    tradePriceEditText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formated = NumberFormat.getCurrencyInstance().format(parsed / 100);

                    currentTradePrice = formated;
                    tradePriceEditText.setText(formated);
                    tradePriceEditText.setSelection(formated.length());

                    tradePriceEditText.addTextChangedListener(this);
                }
                updateTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


//        placeOrderButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String tradePriceString = ((EditText) rootView.findViewById(R.id.trade_price)).getText();
//                double tradePrice =
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setMessage("Are you sure you want to place a trade?")
//                builder.setPositiveButton("Trade", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
////                        bitstamp.buy()
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                    }
//                });
//
//                AlertDialog dialog = builder.create();
//            }
//        });

        sellBuySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateTotal();
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
                            tickerAskTextView.setText(ticker.getString("ask"));
                            tickerBidTextView.setText(ticker.getString("bid"));
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
                            fee = Double.parseDouble(balance.getString("fee")) / 100.0;
                            NumberFormat percentInstance = NumberFormat.getPercentInstance();
                            percentInstance.setMaximumFractionDigits(6);
                            commissionTextView.setText(percentInstance.format(fee));
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


    private void updateTotal() {
        if (fee == null) {
            return;
        }

        try {
            Double tradeAmount = Double.parseDouble(currentTradeAmount.replaceAll("[^\\d\\.]", ""));
            Double tradePrice = Double.parseDouble(currentTradePrice.replaceAll("[$,.]", "")) / 100.0;
            Double subTotal = tradeAmount * tradePrice;

            Double commision = subTotal * fee;

            double total = 0;

            // Checked means buy.
            if (sellBuySwitch.isChecked()) {
                total = subTotal + commision;
            } else {
                total = subTotal - commision;
            }

            totalTextView.setText(String.valueOf(total));
        } catch(Exception e) {
            totalTextView.setText("");
        }
    }
}
