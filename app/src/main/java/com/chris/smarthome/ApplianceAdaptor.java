package com.chris.smarthome;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApplianceAdaptor extends RecyclerView.Adapter<ApplianceAdaptor.ViewHolder> {

    private List<Appliance> mApplianceList;

    public ApplianceAdaptor(List<Appliance> applianceList) {
        mApplianceList = applianceList;
    }

    @NonNull
    @Override
    public ApplianceAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // when the views are shown the first time
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appliance_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.switch_power.setOnClickListener((View v) -> {
            // Power button is pressed!
            int position = holder.getAdapterPosition();
            Appliance appliance = mApplianceList.get(position);
            byte operation = (byte) (appliance.getOn() ?  0x00:0x01);
            appliance.toggle_power();
            Toast.makeText(v.getContext(),
                    appliance.getName() + " is turned " + (appliance.getOn()?"on":"off.")
                    , Toast.LENGTH_SHORT).show();
            if (Activity_Bluetooth_Connection.mThread != null)
                Activity_Bluetooth_Connection.mThread.write(new byte[]
                        {0x03, 0x02, (byte) (position + 1), operation});

            onBindViewHolder(holder, position);
        });
        view.setOnClickListener((View v)-> {
            // The whole appliance card is pressed!
            int position = holder.getAdapterPosition();
            Appliance appliance = mApplianceList.get(position);
            if (appliance.getOn()) {
                Intent intent = new Intent(v.getContext(), Dialog_Appliance_Setting.class);
                intent.putExtra("position", position);
                intent.putExtra("appliance", appliance);
                startActivityForResult(MainActivity.mainActivity, intent, 3, null);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // when the view is refreshed
        Appliance appliance = mApplianceList.get(position);
        holder.applianceImage.setImageResource(appliance.getImageId());
        holder.applianceName.setText(appliance.getName());
        holder.on_or_off.setText(appliance.getOn() ? "On" : "Off");
        holder.current_power.setText(appliance.getCurrentPower() + "W");
        holder.itemView.setBackgroundColor(appliance.getOn() ? 0x7FFFFFFF : 0x3F3B86FC);
        holder.switch_power.setChecked(appliance.getOn());
    }

    @Override
    public int getItemCount() {
        return mApplianceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // inner class
        ImageView applianceImage;
        TextView applianceName;
        TextView on_or_off;
        TextView current_power;
        Button powerButton;
        LinearLayout linearLayout;
        Switch switch_power;

        public ViewHolder(View view) {
            super(view);
            applianceImage = (ImageView) view.findViewById(R.id.appliance_image);
            applianceName = (TextView) view.findViewById(R.id.appliance_name);
            on_or_off = (TextView) view.findViewById(R.id.on_or_off);
            current_power = (TextView) view.findViewById(R.id.current_power);
            powerButton = (Button) view.findViewById(R.id.button);
            linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);
            switch_power = (Switch) view.findViewById(R.id.switch1);
        }
    }
}
