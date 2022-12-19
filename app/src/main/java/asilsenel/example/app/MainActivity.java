package asilsenel.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.google.android.gms.common.util.ArrayUtils;

import asilsenel.example.app.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
    private TextView textView;
    private Button button;
    private ActivityMainBinding binding;
    RelativeLayout mainLayout;

    @SuppressLint({"WrongViewCast", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        textView=findViewById(R.id.textView);
        EditText txt_ip = findViewById(R.id.editTextIp);
        mainLayout= findViewById(R.id.mainLayout);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.samsung);
        final MediaPlayer ding = MediaPlayer.create(this, R.raw.ding);
        TcpClient tcpClient = new TcpClient(textView,mainLayout, mp,this);


        tcpClient.run();
        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                String response = " is OK";
                tcpClient.sendMessage(ipAddress,response);
                textView.setTextColor(Color.parseColor("#000000"));
                textView.setText("OK");
                ding.start();
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}