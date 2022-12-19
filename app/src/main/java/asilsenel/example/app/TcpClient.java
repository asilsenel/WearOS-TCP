package asilsenel.example.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;



public class TcpClient {
    public Activity activity;
    public static final String TAG = TcpClient.class.getSimpleName();
    public String SERVER_IP; //server IP address
    public static final int SERVER_PORT = 5555; //default
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;


    TextView text_View;
    RelativeLayout mainLayout;
    final MediaPlayer mp;
    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(TextView _text, RelativeLayout mainLayout, MediaPlayer mp, Activity _activity) {
         this.text_View=_text;
         this.mainLayout=mainLayout;
         this.mp = mp;
         this.activity = _activity;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String tmp_IP, final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);
                    mBufferOut.println(tmp_IP + message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }
    Socket socket;
    Runnable runnable;
    public void run() {



        mRun = true;
        EditText txt_ip = (EditText)this.activity.findViewById(R.id.editTextIp);
        String etIpAddress = txt_ip.getText().toString();
        Log.d("et",etIpAddress);
        Button button_ip=(Button)this.activity.findViewById(R.id.button_ip);

        button_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SERVER_IP = txt_ip.getText().toString();
                Log.d("ip",SERVER_IP);
                try {

                    //here you must put your computer's IP address.
                    InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                    Log.d("TCP Client", serverAddr.toString());
                    Log.d("TCP Client", "C: Connecting...");

                    //create a socket to make the connection with the server
                    socket = new Socket(serverAddr, SERVER_PORT);

                    Log.d("TSC_App:","Connection OK");
                    SERVER_IP = "";


                    //sends the message to the server
                    mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


                    runnable = new Runnable() {
                        @Override
                        public void run() {

                            Handler handler = new Handler(Looper.getMainLooper());

                            // while(true) {

                            try {

                                //receives the message which the server sends back
                                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));



                                if(socket.isConnected()){
                                    mainLayout.setBackgroundColor(Color.parseColor("#007a00")); //green
                                    try{
                                        mServerMessage = mBufferIn.readLine();
                                    }
                                    catch (Exception ex){
                                        mainLayout.setBackgroundColor(Color.parseColor("#8b0000")); //red
                                        try {
                                            socket = new Socket(serverAddr, SERVER_PORT);
                                            Thread thread = new Thread(runnable);
                                            thread.start();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        Log.d("TSC_App:","Connection OK");
                                    }

                                    if (mServerMessage != null ) {
                                        Log.d("ReceivingData",mServerMessage);
                                        //call the method messageReceived from MyActivity class
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                text_View.setTextColor(Color.parseColor("#ffffff"));
                                                text_View.setText(mServerMessage);
                                                mp.start();
                                            }
                                        });
                                    }
                                }
                                else{
                                    mainLayout.setBackgroundColor(Color.parseColor("#8b0000"));
                                    try {
                                        socket = new Socket(serverAddr, SERVER_PORT);
                                        Thread thread = new Thread(runnable);
                                        thread.start();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Log.d("TSC_App:","Connection OK");
                                }

                                Log.d("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

                            } catch (Exception e) {
                                Log.e("TCP", "S: Error", e);
                            } finally {
                                //the socket must be closed. It is not possible to reconnect to this socket
                                // after it is closed, which means a new socket instance has to be created.
                                try {
                                    socket = new Socket(serverAddr, SERVER_PORT);
                                    Thread thread = new Thread(runnable);
                                    thread.start();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Log.d("TSC_App:","Connection OK");



                            }
                        }

                        //}
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();



                } catch (Exception e) {
                    Log.e("TCP", "C: Error", e);
                }
            }
        });

    }
}