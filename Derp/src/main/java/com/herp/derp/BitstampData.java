package com.herp.derp;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.List;

public class BitstampData {

//    private String clientId;
//    private String apiKey;
//    private String apiSecret;

    private List<NameValuePair> authParams;

    private Boolean isAuthenticated = false;
    private JSONObject balance;
    private JSONObject ticker;

    private BitstampData(List<NameValuePair> authParams) {
        this.authParams = authParams;
    }

    public static BitstampData factory(String clientId, String apiKey, String apiSecret) throws Exception {
        List<NameValuePair> authParams = BitstampApi.buildDefaultAuthParmas(clientId, apiKey, apiSecret);
        return new BitstampData(authParams);
    }

    public void auth(final Callback callback) {
        auth(callback, false);
    }

    public void auth(final Callback callback, final boolean forceReload) {
        Thread t = new Thread(){
            @Override
            public void run() {
                if (!isAuthenticated || forceReload) {
                    try {
                        balance = BitstampApi.balance(authParams);
                        callback.resolve(true);
                    } catch (Exception e) {
                        // Make fer damn sure these aren't in a weird state.
                        balance = null;
                        isAuthenticated = false;
                        callback.error("Failed to get balance", e);
                    }
                } else {
                    callback.resolve(true);
                }
            }
        };
        t.start();
    }

    public void balance(final Callback callback) {
        balance(callback, false);
    }

    public void balance(final Callback callback, final boolean forceReload) {
        Thread t = new Thread(){
            @Override
            public void run() {
                if (balance == null || forceReload) {
                    try {
                        balance = BitstampApi.balance(authParams);
                        callback.resolve(balance);
                    } catch (Exception e) {
                        balance = null;
                        callback.error("Failed to get balance", e);
                    }
                } else {
                    callback.resolve(balance);
                }
            }
        };
        t.start();
    }

    public void ticker(final Callback callback) {
        ticker(callback, true);
    }

    public void ticker(final Callback callback, final boolean forceReload) {
        Thread t = new Thread(){

            @Override
            public void run() {
                if (ticker == null || forceReload) {
                    try {
                        ticker = BitstampApi.ticker();
                        callback.resolve(ticker);
                    } catch (Exception e) {
                        ticker = null;
                        callback.error("Failed to get ticker", e);
                    }
                } else {
                    callback.resolve(ticker);
                }
            }
        };
        t.start();
    }

    public interface Callback {
        public void resolve(Object response);
        public void error(String message, Exception e);
    }
}
