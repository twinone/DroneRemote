package xyz.twinone.droneremote;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements JoyStickView.Listener {

    private static final String TAG = "Main";

    private static final int INTERVAL = 170;
    private static final String HOST = "192.168.1.101";
    private static final int PORT = 80;

    private String mLastString;

    private ArduinoConnector mConnector;

    private JoyStickView mJS1;
    private JoyStickView mJS2;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        fullscreen();

        mConnector = new ArduinoConnector(HOST, PORT, null);

        mJS1 = (JoyStickView) findViewById(R.id.js1);
        mJS1.setListener(this);
        mJS2 = (JoyStickView) findViewById(R.id.js2);
        mJS2.setListener(this);

        mJS1.setResetY(false);
        mJS1.setInvertY(true);
        mJS1.setInvertX(true);

        mHandler.post(mRunner);

    }


    private Runnable mRunner = new Runnable() {
        @Override
        public void run() {
            String msg =
                    (int) (js1x * 1023) + ":" +
                            (int) (js1y * 1023) + ":" +
                            (int) (js2x * 1023) + ":" +
                            (int) (js2y * 1023);
            if (!msg.equals(mLastString)) {
                mConnector.send(msg);
            }
            mLastString = msg;
            mHandler.postDelayed(mRunner, INTERVAL);
        }
    };


    private void fullscreen() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private float js1x = 0.5f;
    private float js1y = 0.0f;
    private float js2x = 0.5f;
    private float js2y = 0.5f;

    @Override
    public void onUpdate(JoyStickView v, float x, float y) {
        if (R.id.js1 == v.getId()) {
            //js1x = x;
            js1y = y;
        } else {
            js2x = x;
            js2y = y;
        }

    }
}
