package BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryLow_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.intent.action.BATTERY_LOW".equals(BatteryManager.EXTRA_BATTERY_LOW))
        {
            Toast.makeText(context, "Battery Low" + BatteryManager.EXTRA_HEALTH, Toast.LENGTH_SHORT).show();
        }
    }
}
