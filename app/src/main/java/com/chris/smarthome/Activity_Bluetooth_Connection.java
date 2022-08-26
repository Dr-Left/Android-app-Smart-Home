package com.chris.smarthome;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


public class Activity_Bluetooth_Connection extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final int MESSAGE_READ = 0;
    public static final int DEBUG = 1;
    private static final String TAG = "Activity_Bluetooth_Connection";
    private final int CONNECTED = 1000;
    private final int DISCONNECTED = 1001;
    public BluetoothSocket mSocket;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver receiver;
    private ListView listView;
    private ArrayAdapter<String> deviceAdapter;
    private List<String> listDevices;
    private TextView textData;
    private Button btnOpen;
    private Button btnSearch;
    //private ConnectThread mThread = null;
    private EditText editSend;
    private Button btnSend;
    private TextView textStatus;
    private ComThread mThread = null;
    private int state = DISCONNECTED;
    private Handler mHandler = new Handler() {
        public void handleMessage(@NonNull android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    //textData.append("Receive: "+(String)msg.obj+"\r\n");
                    textData.setText("Receive: " + (String) msg.obj + "\r\n");
                    break;
                case DEBUG:
                    textStatus.setText((String) msg.obj);
                    break;
                default:
                    break;
            }
        }

    };

    // 判断位置信息是否开启
    private static boolean isLocationOpen(final Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //gps定位
        boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //网络定位
        boolean isNetWorkProvider = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsProvider || isNetWorkProvider;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);

        /*
         The block below is to ensure every permission is granted,
         which is necessary in high APIs.
        */
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.INTERNET,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 1);
                Log.d(TAG, "onCreate: PERMISSION FAILED");
            }
        }


        listView = (ListView) this.findViewById(R.id.listView);
        listDevices = new ArrayList<String>();
        deviceAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listDevices);
        listView.setAdapter(deviceAdapter);
        listView.setOnItemClickListener(this);//添加监听

        textData = (TextView) this.findViewById(R.id.text);
        //textData.setText(textData.getText(), TextView.BufferType.EDITABLE);//这行可实现TextView尾部追加

        editSend = (EditText) findViewById(R.id.editSend);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        btnOpen = (Button) findViewById(R.id.btnOpen);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnOpen.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        textStatus = (TextView) findViewById(R.id.textStatus);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            btnOpen.setText("关闭蓝牙");
        }

        if (Build.VERSION.SDK_INT >= 23) {
            boolean isLocat = isLocationOpen(getApplicationContext());
            Toast.makeText(getApplicationContext(), "isLo:" + isLocat, Toast.LENGTH_SHORT).show();
            //开启位置服务，支持获取ble蓝牙扫描结果
            if (!isLocat) {
                Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(enableLocate, 1);
            }
        }

        //蓝牙广播处理
        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //搜索设备时，取得设备的MAC地址
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String str = device.getName() + "|" + device.getAddress();
                    if (listDevices.indexOf(str) == -1)// 防止重复添加
                        listDevices.add(str); // 获取设备名称和mac地址
                    if (deviceAdapter != null) {
                        deviceAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(receiver);
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOpen:
                if (!bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.enable();//开启蓝牙
                    Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); //300秒为蓝牙设备可见时间
                    startActivity(enable);
                    btnOpen.setText("关闭蓝牙");
                } else {
                    bluetoothAdapter.disable();//关闭蓝牙
                    btnOpen.setText("开启蓝牙");
                    if (mSocket != null) {
                        try {
                            mSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.btnSearch:
                if (!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "请先开启蓝牙", Toast.LENGTH_SHORT).show();
                } else {
                    //btContent.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    if (listDevices != null) {
                        listDevices.clear();
                        if (deviceAdapter != null) {
                            deviceAdapter.notifyDataSetChanged();
                        }
                    }

                    bluetoothAdapter.startDiscovery();
                    Toast.makeText(getApplicationContext(), "开始搜索", Toast.LENGTH_SHORT).show();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver, filter); //注册广播接收器
                }
                break;
            case R.id.btnSend:
                if (state == CONNECTED && mThread != null) {
                    String info = editSend.getText().toString();

                    textStatus.setText("Send:" + info);
                    textData.setText("Send Data:" + info + "\r\n");
                    //textData.append("Send Data:"+info+"\r\n");
                    mThread.write(info.getBytes());
                }
                break;

        }

    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "请先开启蓝牙", Toast.LENGTH_SHORT).show();
        } else {
            bluetoothAdapter.cancelDiscovery();//停止搜索

            String str = listDevices.get(position);
            String macAdress = str.split("\\|")[1];

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAdress);
            try {

            	/*
                final String SPP_UUID="";
            	UUID uuid = UUID.fromString(SPP_UUID); //Standard SerialPortService ID
            	mSocket = device.createRfcommSocketToServiceRecord(uuid);
            	*/

                Method clientMethod = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                mSocket = (BluetoothSocket) clientMethod.invoke(device, 1);
                // Even the Harmony(TM) system should use 1!
                // 1 连接单片机 ,  2和3 连接手机

                try {
                    mSocket.connect();//连接
                    textStatus.setText("尝试连接..........");
                    if (mSocket.isConnected()) {
                        textStatus.setText("连接成功");
                        Toast.makeText(getApplicationContext(), "蓝牙连接成功", Toast.LENGTH_SHORT).show();
                        listView.setVisibility(View.GONE);
                        textData.setVisibility(View.VISIBLE);
                        mThread = new ComThread(mSocket);
                        mThread.start();//另开一个线程，与蓝牙设备进行通信
                        state = CONNECTED;
                        // return to the former page
                        Intent intent = new Intent();
//                        intent.putExtra("connection_thread", mThread);
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        textStatus.setText("连接失败");
                        Toast.makeText(getApplicationContext(), "蓝牙连接失败", Toast.LENGTH_SHORT).show();
                        mSocket.close();
                        listView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    class ComThread extends Thread {

        private BluetoothSocket s;
        private boolean exitflag = false;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ComThread(BluetoothSocket s) {
            this.s = s;
        }

        public synchronized boolean getFlag() {
            return exitflag;
        }

        public synchronized void setFlag(boolean v) {
            exitflag = true;
        }

        public void write(byte[] bytes) {
            try {
                outputStream = mSocket.getOutputStream();
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                inputStream = s.getInputStream();
                int len = 0;
                String result = "";
                exitflag = false;
                while (len != -1) {
                    if (inputStream.available() <= 0) {
                        Thread.sleep(1000);//等待0.5秒，让数据接收完整
                        continue;
                    } else {
                        try {
                            Thread.sleep(500);//等待0.5秒，让数据接收完整
                            byte[] data = new byte[1024];
                            len = inputStream.read(data);
                            result = URLDecoder.decode(new String(data, "utf-8"));

	                         /*String debuginfo="len = "+len+" result = "+result;
	                         Message msg1 = new Message();
                             msg1.what = DEBUG;
                             msg1.obj = debuginfo;
                             mHandler.sendMessage(msg1);


	                         Message msg = new Message();
                             msg.what = MESSAGE_READ;
                             msg.obj = result;
                             mHandler.sendMessage(msg);
	                         */

                            final String mid = result;
                            textData.post(new Runnable() {
                                public void run() {
                                    //textData.append("Receiv: "+mid);
                                    textData.setText("Receiv: " + mid);
                                    //String oldstr = textData.getText().toString();
                                    //textData.setText(oldstr+"\r\nReceiv: "+mid);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                inputStream.close();
                outputStream.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
