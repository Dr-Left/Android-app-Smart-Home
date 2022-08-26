package com.chris.smarthome;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class seekForCity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private EditText editText;
    private static final String TAG = "seekForCity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_seek_for_city);

        editText = findViewById(R.id.editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                queryDataBase_then_setText(editText.getText().toString());
            }
        });

//        queryDataBase_then_setText(" ");
    }

    private void queryDataBase_then_setText(String keyWord) {
        City[] data ;
//        move database
        DatabaseUtil.packDataBase(this);
//        read from database
        dbHelper = new MyDatabaseHelper(this, "city.db", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String myQuery = "SELECT * FROM  city WHERE province LIKE ? OR city LIKE ? OR allpy LIKE ? OR allfirstpy LIKE ?";
        keyWord = "%" + keyWord + "%";
        Cursor cursor = db.rawQuery(myQuery, new String[]{keyWord,keyWord,keyWord,keyWord});
        data = new City[cursor.getCount()];
        String[] shownStrings = new String[cursor.getCount()];
        if (cursor.moveToFirst()) {
            do {
                // _id, province, city, number, allpy, allfirstpy, firstpy
                int id = cursor.getInt(0);
                String province = cursor.getString(1);
                String city = cursor.getString(2);
                String number = cursor.getString(3);
                Log.d("Total", cursor.getCount() + "");
                Log.d("Position", cursor.getPosition() + "");
                data[cursor.getPosition()] = new City();
                data[cursor.getPosition()].province_name = province;
                data[cursor.getPosition()].city_name = city;
                data[cursor.getPosition()].city_code = number;
                shownStrings[cursor.getPosition()] =  "No: " + id + " " + number + " " + province + " " + city;
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(seekForCity.this, android.R.layout.simple_list_item_1, shownStrings);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, i, l) -> {
            String result = ((TextView) view).getText().toString();
//            Toast.makeText(seekForCity.this, "城市是：" + result, Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("city_name", data[i].city_name);
            intent.putExtra("city_code", data[i].city_code);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}

class City {
    String province_name;
    String city_name;
    String city_code;
}