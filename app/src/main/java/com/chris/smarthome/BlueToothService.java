package com.chris.smarthome;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BlueToothService extends Service {
    public BlueToothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}