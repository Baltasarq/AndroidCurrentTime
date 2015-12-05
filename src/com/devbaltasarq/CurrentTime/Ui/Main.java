package com.devbaltasarq.CurrentTime.Ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import com.devbaltasarq.CurrentTime.Core.TimeFetcher;
import com.devbaltasarq.CurrentTime.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Activity {
    private Handler handler;
    private Timer timer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.main );

        ImageButton btQuit = (ImageButton) this.findViewById( R.id.btQuit );
        btQuit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.this.finish();
            }
        } );

        this.handler = new Handler();
    }

    @Override
    public void onPause() {
        super.onPause();

        this.timer.cancel();
        this.timer.purge();
        this.timer = null;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        this.setStatus( R.string.status_init );
        this.timer = new Timer();

        TimerTask taskFetchTime = new TimerTask() {
            @Override
            public void run() {
                Main.this.handler.post( new Runnable() {
                    public void run() {
                        try {
                            new TimeFetcher( Main.this ).execute( new URL( TimeFetcher.TIME_URL ) );
                        } catch(MalformedURLException e)
                        {
                            Main.this.setStatus( R.string.status_incorrect_url );
                        }
                    }
                });
            }
        };

        // Program the task for every ten seconds from nows
        timer.schedule( taskFetchTime, 0, 10000 );
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

    public void setStatus(int msgId) {
        final TextView lblStatus = (TextView) this.findViewById( R.id.lblStatus );

        lblStatus.setText( msgId );
    }
}
