package com.chris.smarthome;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chris.smarthome.utils.QueryWeatherUtil;
import com.chris.smarthome.utils.TodayWeather;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static Handler mHandler;
    public static boolean blue_tooth_connected;
    public static Activity mainActivity;
    private final List<Appliance> applianceList = new ArrayList<Appliance>();
    private ImageView imageView_chooseCity;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String remember_city_code;
    private String remember_city_name;
    private TextView textView_city_name;
    private TextView textView_current_wendu;
    private TextView textView_shidu;
    private TextView textView_quality;
    private TextView textView_type;
    private ImageView imageView_weather;
    private Button button_connect_bluetooth;
    private FloatingActionButton button_add;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: locating service
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = (Activity) this;

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

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        remember_city_code = pref.getString("city_code", "101010100");
        remember_city_name = pref.getString("city_name", "北京");
//        Log.d(TAG, remember_city_code);
        blue_tooth_connected = false;

        QueryWeatherUtil.queryWeatherCode(remember_city_code);

        textView_city_name = (TextView) findViewById(R.id.textView5);
        textView_quality = (TextView) findViewById(R.id.textView7);
        textView_shidu = (TextView) findViewById(R.id.textView6);
//        textView_fengxiang = (TextView) findViewById(R.id.);
        textView_type = (TextView) findViewById(R.id.textView2);
        textView_current_wendu = (TextView) findViewById(R.id.textView1);
        imageView_chooseCity = (ImageView) findViewById(R.id.imageView2);
        imageView_weather = (ImageView) findViewById(R.id.imageView);
        button_connect_bluetooth = (Button) findViewById(R.id.button2);
        button_add = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        initAppliances(); // init info of appliances
        recyclerView = findViewById(R.id.recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ApplianceAdaptor adaptor = new ApplianceAdaptor(applianceList);
        recyclerView.setAdapter(adaptor);

//        recyclerView.setEnabled(false);
//        if (!blue_tooth_connected) {
//            recyclerView.setVisibility(View.INVISIBLE);
//            button_connect_bluetooth.setVisibility(View.VISIBLE);
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//            button_connect_bluetooth.setVisibility(View.GONE);
//        }

        button_connect_bluetooth.setOnClickListener((View view) -> {
            Intent intent = new Intent(MainActivity.this, Activity_Bluetooth_Connection.class);
            startActivityForResult(intent, 2);
        });

        imageView_chooseCity.setOnClickListener((View view) -> {
//            Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, seekForCity.class);
            startActivityForResult(intent, 1);
        });

        button_add.setOnClickListener((View v) -> {
            Intent intent = new Intent(MainActivity.this, Add_appliance.class);
            startActivityForResult(intent, 4);
        });

        // get message
        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case QueryWeatherUtil.UPDATE_TODAY_WEATHER:
                        updateTodayWeather((TodayWeather) msg.obj);
                        break;
                    case Activity_Bluetooth_Connection.MESSAGE_READ:
                        // get message from blue tooth!
                        byte[] info = (byte[]) msg.obj;
                        switch (info[0]) {
                            case 0x01:
                                // message
                                for (int i = 0; i < info[1]; i++) {
//                                    info[i + 2];//true/false
                                }
                                break;
                            case 0x03:
                                // switch
                                break;
                            case 0x02:
                                // detect
                                break;
                            case 0x04:
                                // control
                                break;
                        }
                        updateAppliancesInfo();
                        break;
                    default:
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    // city already chosen
                    String city_name = data.getStringExtra("city_name");
                    String city_code = data.getStringExtra("city_code");
//                    Toast.makeText(this, returnedData, Toast.LENGTH_SHORT).show();
                    editor = pref.edit();
                    editor.putString("city_code", city_code);
                    editor.putString("city_name", city_name);
                    remember_city_name = city_name;
//                    Log.d(TAG, city_code);
                    editor.apply();
                    QueryWeatherUtil.queryWeatherCode(city_code);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    // Activity_bluetooth_connection
                    Log.d(TAG, "onActivityResult: CONNECTION SUCCESSFUL!");
//                    Activity_Bluetooth_Connection.mThread.write("Hello!This is main activity!".getBytes());
                    button_connect_bluetooth.setVisibility(View.GONE);
                    recyclerView.setEnabled(true);
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("position", -1);
                    if (data.getBooleanExtra("isRemoved", false)) {
                        Toast.makeText(mainActivity, applianceList.get(position).getName() +
                                        " has been removed!"
                                , Toast.LENGTH_SHORT).show();
                        applianceList.remove(position);
                        recyclerView.removeViewAt(position);
                    } else {
                        // OK
                        Appliance appliance = applianceList.get(position);
                        int power = data.getIntExtra("power", -1);
                        appliance.setCurrentPower(power);
                        Activity_Bluetooth_Connection.mThread.write(
                                new byte[]{0x04, 0x02, (byte)(position+1), (byte)(power/10)}
                        );
                    }
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
            case 4:
                if (resultCode == RESULT_OK) {
                    String name = data.getStringExtra("name");
                    int power = data.getIntExtra("power", 0);
                    int maxPower = data.getIntExtra("max_power", 0);
                    applianceList.add(new Appliance(name, 0, false, power, maxPower));
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
        }
    }


    private void initAppliances() {
        Appliance fridge = new Appliance("Refrigerator", R.drawable.fridge, true, 200, 0);
        applianceList.add(fridge);
        Appliance washingMachine = new Appliance("Washing Machine", R.drawable.washing_machine, true, 300, 0);
        applianceList.add(washingMachine);
        Appliance geyser = new Appliance("Geyser", R.drawable.geyser, true, 3000, 5000);
        applianceList.add(geyser);
        Appliance lightBulb = new Appliance("Light Bulb", R.drawable.light_bulb, true, 25, 100);
        applianceList.add(lightBulb);
        Appliance airConditioner = new Appliance("Air Conditioner", R.drawable.air_conditioner, false, 2000, 3000);
        applianceList.add(airConditioner);
        Appliance television = new Appliance("Television", R.drawable.television, false, 110, 0);
        applianceList.add(television);
    }

    private void updateTodayWeather(TodayWeather obj) {

        textView_city_name.setText(remember_city_name);
        textView_quality.setText(obj.getQuality());
        textView_type.setText(obj.getType());
        textView_shidu.setText(obj.getShidu());
        textView_current_wendu.setText(obj.getWendu() + "℃");

        switch (obj.getType()) {
            case "暴雪":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                imageView_weather.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
        }
    }

    private void updateAppliancesInfo() {

    }
}