package com.chris.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Dialog_Appliance_Setting extends AppCompatActivity {

    private Button button_remove;
    private Button button_ok;
    private SeekBar seekBar;
    private Appliance appliance;
    private TextView textView_power;
    private TextView textView_caption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_appliance_setting);

        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        textView_caption = (TextView) findViewById(R.id.textView);
        button_ok = (Button) findViewById(R.id.button6);
        button_remove = (Button) findViewById(R.id.button5);
        textView_power = (TextView) findViewById(R.id.textView4);
        appliance = (Appliance) getIntent().getSerializableExtra("appliance");
        seekBar.setMax(appliance.getMaxPower());
        seekBar.setProgress(appliance.getCurrentPower());
        textView_power.setText(appliance.getCurrentPower() + "/" + appliance.getMaxPower() + "W");
        if (appliance.getMaxPower() == 0) {
            textView_caption.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);
            textView_power.setVisibility(View.GONE);
            button_ok.setVisibility(View.GONE);
        }
        button_remove.setOnClickListener((View v)-> {
            Intent intent = new Intent();
            intent.putExtra("isRemoved", true);
            intent.putExtra("position", getIntent().getIntExtra("position", 0));
            intent.putExtra("power", seekBar.getProgress());
            setResult(RESULT_OK, intent);
            finish();
        });
        button_ok.setOnClickListener((View v)-> {
            Intent intent = new Intent();
            intent.putExtra("isRemoved", false);
            intent.putExtra("position", getIntent().getIntExtra("position", 0));
            intent.putExtra("power", seekBar.getProgress());
            setResult(RESULT_OK, intent);
            finish();
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView_power.setText(seekBar.getProgress() + "/" + seekBar.getMax() + "W");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}