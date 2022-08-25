package com.chris.smarthome;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Appliance> applianceList = new ArrayList<Appliance>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAppliances(); // init info of appliances
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ApplianceAdaptor adaptor = new ApplianceAdaptor(applianceList);
        recyclerView.setAdapter(adaptor);
    }

    private void initAppliances() {
        Appliance fridge = new Appliance("Refrigerator", R.drawable.fridge);
        applianceList.add(fridge);
        Appliance washingMachine = new Appliance("Washing Machine", R.drawable.washing_machine);
        applianceList.add(washingMachine);
        Appliance geyser = new Appliance("Geyser", R.drawable.geyser);
        applianceList.add(geyser);
        Appliance lightBulb = new Appliance("Light Bulb", R.drawable.light_bulb);
        applianceList.add(lightBulb);
        Appliance airConditioner = new Appliance("Air Conditioner", R.drawable.air_conditioner);
        applianceList.add(airConditioner);
    }
}