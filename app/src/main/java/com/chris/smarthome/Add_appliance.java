package com.chris.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Add_appliance extends AppCompatActivity {

    private Button button_ok;
    private EditText editText_name;
    private EditText editText_power;
    private EditText editText_maxPower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appliance);

        editText_name = findViewById(R.id.editText1);
        editText_power = findViewById(R.id.editText2);
        editText_maxPower = findViewById(R.id.editText3);
        button_ok = findViewById(R.id.button7);
        button_ok.setOnClickListener(view -> {
            int power = Integer.parseInt(editText_power.getText().toString());
            int maxPower = Integer.parseInt(
                    editText_maxPower.getText().toString());
            if (power > maxPower) {
                Toast.makeText(this, "Illegal power quantity!"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("name", editText_name.getText().toString());
            intent.putExtra("power", power);
            intent.putExtra("max_power", maxPower);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}