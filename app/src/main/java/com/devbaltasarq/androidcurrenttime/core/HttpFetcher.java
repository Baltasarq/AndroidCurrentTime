package com.devbaltasarq.androidcurrenttime.core;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.devbaltasarq.androidcurrenttime.R;
import com.devbaltasarq.androidcurrenttime.view.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Fetches the time from GeoNames
 * Created by baltasarq on 4/12/2015.
 */
public class HttpFetcher {
    public static final String LOG_TAG = HttpFetcher.class.getSimpleName();
    public static final String TIME_URL = "http://api.geonames.org/timezoneJSON?lat=42.34&lng=-7.86&username=dispositivos_moviles";

    public HttpFetcher(Observer activity)
    {
        this.activity = activity;
    }

    public void execute()
    {
        final Executor EXECUTOR = Executors.newSingleThreadExecutor();
        final Handler HANDLER = new Handler( Looper.getMainLooper() );

        EXECUTOR.execute( () -> {
            final HttpFetcher SELF = HttpFetcher.this;
            boolean result = false;

            try {
                result = SELF.doInBackground( new URL( TIME_URL ) );
            } catch (MalformedURLException e) {
                Log.e( LOG_TAG, e.getMessage() );
                SELF.activity.setStatus( R.string.status_incorrect_url );
            }

            if ( result ) {
                HANDLER.post( () -> {
                    SELF.activity.setTimeInfo(this.responseParser.getDataDateTime());
                    SELF.activity.setStatus(R.string.status_ok);
                });
            } else {
                HANDLER.post( () -> {
                    SELF.activity.setDefaultValues();
                });
            }
        });
    }

    private boolean doInBackground(URL url)
    {
        InputStream is = null;
        boolean toret = false;

        try {
            // Connection
            Log.d( LOG_TAG, " in doInBackground(): connecting" );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout( 1000 /* milliseconds */ );
            conn.setConnectTimeout( 5000 /* milliseconds */ );
            conn.setRequestMethod( "GET" );
            conn.setDoInput( true );

            // Obtain the answer
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d( LOG_TAG, String.format( " in doInBackground(): server response is: %s(%d)",
                    conn.getResponseMessage(),
                    responseCode ) );

            if ( responseCode == 200 ) {
                this.responseParser = new ResponseParser( conn.getInputStream() );
                toret = true;
                Log.d( LOG_TAG, " in doInBackground(): finished" );
                Log.i( LOG_TAG, " in doInBackground(): fetching ok" );
            } else {
                Log.i( LOG_TAG, " in doInBackground(): fetching failed" );
            }
        }
        catch(IOException exc) {
            Log.e( LOG_TAG, " in doInBackground(), connecting: " + exc.getMessage() );
        } finally {
            Util.close( is, LOG_TAG, "doInBackground" );
        }

        return toret;
    }

    private ResponseParser responseParser;
    private Observer activity;
}
