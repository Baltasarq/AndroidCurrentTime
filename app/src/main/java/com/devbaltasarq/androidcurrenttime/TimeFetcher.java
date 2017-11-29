package com.devbaltasarq.androidcurrenttime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fetches the time from GeoNames
 * Created by baltasarq on 4/12/15.
 */
public class TimeFetcher extends AsyncTask<URL, Void, Boolean> {
    public static final String LOG_TAG = "TimeFetcher";
    public static final String COUNTRY_NAME_TAG = "countryName";
    public static final String TIMEZONE_TAG = "timezoneId";
    public static final String GMT_OFFSET_TAG = "gmtOffset";
    public static final String TIME_TAG = "time";
    public static final String TIME_URL = "http://api.geonames.org/timezoneJSON?lat=42.34&lng=-7.86&username=dispositivos_moviles";

    private MainActivity activity;
    private String time;
    private String timeInfo;
    private String gmtInfo;

    public TimeFetcher(MainActivity activity) {
        this.activity = activity;
    }

    private void setDefaultValues() {
        this.time = "00:00";
        this.timeInfo = "N/A";
        this.gmtInfo = "N/A";
    }

    @Override
    protected void onPreExecute() {
        final TextView lblStatus = (TextView) this.activity.findViewById( R.id.lblStatus );

        this.activity.setStatus( R.string.status_connecting );
        this.setDefaultValues();
    }

    @Override
    protected Boolean doInBackground(URL... urls) {
        InputStream is = null;
        boolean toret = false;

        try {
            // Check connectivty
            Log.d( LOG_TAG, " in doInBackground(): checking connectivity" );
            ConnectivityManager connMgr = (ConnectivityManager)  this.activity.getSystemService( Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            boolean connected = ( networkInfo != null && networkInfo.isConnected() );
            Log.d( LOG_TAG, " in doInBackground(), connected: " + connected );

            if ( !connected ) {
                this.activity.setStatus( R.string.status_not_connected );
            } else {
                // Connection
                Log.d( LOG_TAG, " in doInBackground(): connecting" );
                HttpURLConnection conn = (HttpURLConnection) urls[ 0 ].openConnection();
                conn.setReadTimeout( 1000 /* milliseconds */ );
                conn.setConnectTimeout( 1000 /* milliseconds */ );
                conn.setRequestMethod( "GET" );
                conn.setDoInput( true );

                // Obtain the answer
                conn.connect();
                int responseCode = conn.getResponseCode();
                Log.d( LOG_TAG, String.format( " in doInBackground(): server response is: %s(%d)",
                        conn.getResponseMessage(),
                        responseCode ) );
                is = conn.getInputStream();

                // Starts the query
                Log.d( LOG_TAG, " in doInBackground(): querying" );
                JSONObject json = new JSONObject( getStringFromStream( is ) );
                Log.d( LOG_TAG, " in doInBackground(): content fetched: " + json.toString( 4 ) );
                this.time = json.getString( TIME_TAG );
                this.timeInfo = json.getString( TIMEZONE_TAG ) + " (" + json.getString( COUNTRY_NAME_TAG ) + ")";

                // GMT info
                int gmtOffset = json.getInt( GMT_OFFSET_TAG );
                this.gmtInfo = "GMT";

                if ( gmtOffset != 0 ) {
                    this.gmtInfo += " ";

                    if ( gmtOffset > 0 ) {
                        this.gmtInfo += "+";
                    }

                    this.gmtInfo += Integer.toString( gmtOffset );
                }

                toret = true;
                Log.d( LOG_TAG, " in doInBackground(): finished" );
            }
        }
        catch(JSONException exc) {
            Log.e( LOG_TAG, " in doInBackground(), parsing JSON: " + exc.getMessage() );
        } catch(IOException exc) {
            Log.e( LOG_TAG, " in doInBackground(), connecting: " + exc.getMessage() );
        } finally {
            if ( is != null ) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e( LOG_TAG, " in doInBackGround(): error closing input stream" );
                }
            }
        }

        return toret;
    }

    private String getStringFromStream(InputStream is)
    {
        BufferedReader reader = null;
        StringBuilder toret = new StringBuilder();
        String line;

        try {
            reader = new BufferedReader( new InputStreamReader( is ) );
            while( ( line = reader.readLine() ) != null ) {
                toret.append( line );
            }
        } catch (IOException e) {
            Log.e( LOG_TAG, " in getStringFromString(): error converting net input to string"  );
        }

        return toret.toString();
    }

    @Override
    public void onPostExecute(Boolean result)
    {
        final TextView lblStatus = (TextView) this.activity.findViewById( R.id.lblStatus );
        int idFinalStatus = R.string.status_ok;

        if ( !result ) {
            idFinalStatus = R.string.status_error;
            Log.i( LOG_TAG, " in onPostExecute(): time fetched incorrectly" );
        } else {
            Log.i( LOG_TAG, " in onPostExecute(): time fetching ok" );
        }

        lblStatus.setText( idFinalStatus );
        this.activity.setTimeInfo( time, timeInfo, gmtInfo );
    }
}
