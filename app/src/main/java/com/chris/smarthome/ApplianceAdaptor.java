package com.chris.smarthome;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApplianceAdaptor extends RecyclerView.Adapter<ApplianceAdaptor.ViewHolder> {

    private List<Appliance> mApplianceList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        // inner class
        ImageView applianceImage;
        TextView applianceName;

        public ViewHolder(View view) {
            super(view);
            applianceImage = (ImageView) view.findViewById(R.id.appliance_image);
            applianceName = (TextView) view.findViewById(R.id.appliance_name);
        }
    }

    public ApplianceAdaptor(List<Appliance> applianceList) {
        mApplianceList = applianceList;
    }

    @NonNull
    @Override
    public ApplianceAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appliance_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appliance appliance = mApplianceList.get(position);
        holder.applianceImage.setImageResource(appliance.getImageId()); // TODO:
        holder.applianceName.setText(appliance.getName()); //TODO:
    }

    @Override
    public int getItemCount() {
        return mApplianceList.size();
    }
}
