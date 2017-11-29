package com.devbaltasarq.androidcurrenttime;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.activity_main );

        ImageButton btQuit = (ImageButton) this.findViewById( R.id.btQuit );
        btQuit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        } );

        this.setDefaultValues();
    }

    @Override
    public void onPause() {
        super.onPause();

        this.timer.cancel();
        this.timer.purge();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        this.setStatus( R.string.status_init );

        TimerTask taskFetchTime = new TimerTask() {
            @Override
            public void run() {
                try {
                    new HttpFetcher(MainActivity.this ).execute( new URL( HttpFetcher.TIME_URL ) );
                } catch(MalformedURLException e)
                {
                    Log.e( "Timer.run", e.getMessage() );
                    MainActivity.this.setStatus( R.string.status_incorrect_url );
                }
            }
        };

        // Program the task for every 20 seconds from now on.
        this.timer = new Timer();
        timer.schedule( taskFetchTime, 0, 20000 );
    }

    public void setTimeInfo(String time, String timeZoneInfo, String gmtInfo)
    {
        final TextView lblTime = (TextView) this.findViewById( R.id.lblTime );
        final TextView lblGmtInfo = (TextView) this.findViewById( R.id.lblGmtInfo );
        final TextView lblTimeZoneInfo = (TextView) this.findViewById( R.id.lblTimeZoneInfo );

        lblTime.setText( time );
        lblTimeZoneInfo.setText( timeZoneInfo );
        lblGmtInfo.setText( gmtInfo );
    }

    public void setStatus(int msgId)
    {
        final TextView lblStatus = (TextView) this.findViewById( R.id.lblStatus );

        lblStatus.setText( msgId );
    }

    public void setDefaultValues()
    {
        final String notAvailable = this.getString( R.string.status_not_available );

        this.setTimeInfo( "00:00", notAvailable, notAvailable );
        this.setStatus( R.string.status_error );
    }

    private Timer timer;
}
