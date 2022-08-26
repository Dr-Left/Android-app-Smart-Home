package com.chris.smarthome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApplianceAdaptor extends RecyclerView.Adapter<ApplianceAdaptor.ViewHolder> {

    private List<Appliance> mApplianceList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        // inner class
        ImageView applianceImage;
        TextView applianceName;
        TextView on_or_off;
        TextView current_power;
        Button powerButton;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            applianceImage = (ImageView) view.findViewById(R.id.appliance_image);
            applianceName = (TextView) view.findViewById(R.id.appliance_name);
            on_or_off = (TextView) view.findViewById(R.id.on_or_off);
            current_power = (TextView) view.findViewById(R.id.current_power);
            powerButton = (Button) view.findViewById(R.id.button);
            linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);
        }
    }

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
        holder.powerButton.setOnClickListener((View v)-> {
            // power button is pushed!
            int position = holder.getAdapterPosition();
            Appliance appliance = mApplianceList.get(position);
            if (appliance.getOn()) {
                // is on initially
                appliance.setOn(false);
                holder.linearLayout.setBackgroundColor(0x7FBB86FC);
                Toast.makeText(v.getContext(), appliance.getName() + " is turned off",
                    Toast.LENGTH_SHORT).show();
            }
            else {
                // is off initially
                appliance.setOn(true);
                holder.linearLayout.setBackgroundColor(0x7FFFFFFF);
                Toast.makeText(v.getContext(), appliance.getName() + " is turned on",
                        Toast.LENGTH_SHORT).show();
            }
            onBindViewHolder(holder, position);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // when the view is refreshed
        Appliance appliance = mApplianceList.get(position);
        holder.applianceImage.setImageResource(appliance.getImageId());
        holder.applianceName.setText(appliance.getName());
        holder.on_or_off.setText(appliance.getOn()?"On":"Off");
        holder.current_power.setText(appliance.getCurrentPower()+"W");
    }

    @Override
    public int getItemCount() {
        return mApplianceList.size();
    }
}
