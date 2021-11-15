package com.example.jjinmak;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageButton qrscan_btn;
    private RecyclerAdapter adapter;
    private IntentIntegrator qrScan;
    private ItemTouchHelper helper;
    private Handler mHandler;
    Socket socket;
    PrintWriter sendWriter;
    private String ip = "192.168.1.141";
    private int port = 12351;

    ProgressDialog customProgressDialog;


    HashMap<String, String> positionList;

    TextView textView;
    Button chatbutton;
    TextView chatView;
    EditText message;
    String sendmsg;
    String displayString;
    String happy;
    ToggleButton Togglelove;
    String command;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //actionBar 숨김
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        positionList = new HashMap<String, String>();

        //Socket
        mHandler = new Handler();
        textView = (TextView) findViewById(R.id.textView);
        message = (EditText) findViewById(R.id.message);
        Togglelove = (ToggleButton) findViewById(R.id.toggleBtn);

        //Loading
        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //recyclerview
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
        // swiper 구현
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        helper.attachToRecyclerView(recyclerView);

        new Thread() {
            public void run() {
                try {
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while (true) {
                        String read = input.readLine();
                        if (read != null && !read.isEmpty() && !read.equals("Readnull")) {
                            Log.i("Activity", "Read" + read);
                            mHandler.post(new msgUpdate(read));

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addItem(read, happy);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        //QR Scanning Button 이벤트
        qrScan = new IntentIntegrator(this);

        qrscan_btn = (ImageButton)findViewById(R.id.qrscan_btn);
        qrscan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.setOrientationLocked(false);
                qrScan.setPrompt("네모칸에 QR코드를 맞춰주세요");
                qrScan.initiateScan();
            }
        });

        adapter.setOnCheckedChangeListener(new RecyclerAdapter.OnCheckedChangeListener() {
            @Override
            public void OnCheckedChange(TextView v, boolean b) {

                command = "1"; // Data "1""
                command += positionList.get(v.getText()); // ID

                String t = b ? "1" : "2";   // ON / OFF

                command += t;

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sendWriter.println(command);
                            sendWriter.flush();
                            message.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                Log.d("fuck", "Send Command" + command);
            }
        });
    }

    class msgUpdate implements Runnable {
        private String tmsg;

        public msgUpdate(String str) {
            this.tmsg = str;
        }

        @Override
        public void run() {
//            chatView.setText(chatView.getText().toString() + tmsg + "\n");
        }
    }


    protected void addItem(String _in, String _exin) {
        int tempLen = _in.length() - _exin.length();
        String tempString = _in.substring(0, tempLen);
        try {
            JSONObject obj = new JSONObject(tempString);
            positionList.put(obj.getString("addr"), obj.getString("id"));
//            recyclerview_data.setID(obj.getString());

            Data recyclerview_data = new Data();
            recyclerview_data.setAddr(obj.getString("addr"));
            recyclerview_data.setName(obj.getString("name"));
            recyclerview_data.setNetRole(obj.getString("netRole"));
            recyclerview_data.setStatus("good");
            if (obj.getString("netRole").equals("ap")) {

                recyclerview_data.setNetRoleImage(R.drawable.apicon);

            } else {
                recyclerview_data.setNetRoleImage(R.drawable.iotimage);
            }
            adapter.addItem(recyclerview_data);
            customProgressDialog.cancel();

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //result -> qr임
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(MainActivity.this, "취소!", Toast.LENGTH_SHORT).show();
            } else {

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressOFF();
//                    }
//                }, 3500);
                //qrcode 결과가 있으면
                Toast.makeText(MainActivity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                happy = 0x00 + result.getContents();

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sendWriter.println(happy);
                            sendWriter.flush();
                            message.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                customProgressDialog.show();
                Log.d("Fuck", "QR: " + happy);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }






    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
}

