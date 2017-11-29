package com.devbaltasarq.androidcurrenttime;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** ResponseParser interprets the answer obtained from the REST API */
public class ResponseParser {
    public static final String LOG_TAG = "ResponseParser";
    public static final String COUNTRY_NAME_TAG = "countryName";
    public static final String TIMEZONE_TAG = "timezoneId";
    public static final String GMT_OFFSET_TAG = "gmtOffset";
    public static final String TIME_TAG = "time";

    /** Creates a new ResponseParser, given the InputStream from the connection */
    public ResponseParser(InputStream is)
    {
        this.query( is );
    }

    public String getTime()
    {
        return time;
    }

    public String getTimeInfo()
    {
        return timeInfo;
    }

    public String getGmtInfo()
    {
        return gmtInfo;
    }

    private void query(InputStream is)
    {
        try {
            Log.d(LOG_TAG, " in doInBackground(): querying");
            JSONObject json = new JSONObject( getStringFromStream( is ) );
            Log.d(LOG_TAG, " in doInBackground(): content fetched: " + json.toString( 4 ));
            this.time = json.getString( TIME_TAG );
            this.timeInfo = json.getString( TIMEZONE_TAG ) + " (" + json.getString( COUNTRY_NAME_TAG ) + ")";

            // GMT info
            int gmtOffset = json.getInt( GMT_OFFSET_TAG );
            this.gmtInfo = "GMT";

            if ( gmtOffset != 0 ) {
                this.gmtInfo = this.getGmtInfo() + " ";

                if ( gmtOffset > 0 ) {
                    this.gmtInfo = this.getGmtInfo() + "+";
                }

                this.gmtInfo = this.getGmtInfo() + Integer.toString( gmtOffset );
            }
        } catch(JSONException exc) {
            Log.e( LOG_TAG, " in query(): " + exc.getMessage() );
        }
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

    private String time;
    private String timeInfo;
    private String gmtInfo;
}
