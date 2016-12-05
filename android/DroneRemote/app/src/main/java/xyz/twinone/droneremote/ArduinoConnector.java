package xyz.twinone.droneremote;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;

/**
 * @author Luuk W. (Twinone).
 */
public class ArduinoConnector {

    private static final String TAG = "ArduinoConnector";
    private final Handler mHandler;
    private Socket sock;
    private InputStream is;
    private OutputStream os;

    private String mHost;
    private int mPort;

    private HandlerThread mThread = new HandlerThread("");

    private Listener mListener;
    private Runnable mPending;

    public ArduinoConnector(String host, int port, Listener l) {
        mHost = host;
        mPort = port;

        mThread.start();
        mHandler = new Handler(mThread.getLooper());


        mListener = l;
        connect();
    }

    private void connect() {
        Log.d(TAG, "Connecting to " + mHost + ":" + mPort);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    sock = new Socket(mHost, mPort);
                    is = sock.getInputStream();
                    os = sock.getOutputStream();
                    Log.d(TAG, "Connected");

                } catch (IOException e) {
                    Log.e(TAG, "Error connecting to host: ", e);
                }

            }
        });
    }

    public void send(final String msg) {
        if (sock == null || sock.isClosed() || !sock.isConnected()) {
            Log.d(TAG, "Reconnecting");
            connect();
        }

        mHandler.removeCallbacks(mPending);
        mPending = new Runnable() {
            @Override
            public void run() {
                try {
                    os.write(msg.getBytes());
                    os.flush();
                    Log.d(TAG, "Written " + msg);
                } catch (IOException e) {
                    Log.d(TAG, "Error writing to socket: ", e);
                    connect();
                }
            }
        };
        mHandler.post(mPending);
    }

    public interface Listener {
        void onMessage(String msg);
    }


}
