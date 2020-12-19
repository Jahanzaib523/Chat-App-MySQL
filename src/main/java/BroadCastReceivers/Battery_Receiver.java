package BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class Battery_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        if(status ==  BatteryManager.BATTERY_STATUS_CHARGING)
        {
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            if(usbCharge)
            {
                Toast.makeText(context, "USB Charging" + chargePlug + " %", Toast.LENGTH_SHORT).show();
            }
            else if(acCharge)
            {
                Toast.makeText(context, "AC Charging" + chargePlug + " %", Toast.LENGTH_SHORT).show();
            }
        }
        else if(status == BatteryManager.BATTERY_STATUS_FULL)
        {
            Toast.makeText(context, "Battery Full", Toast.LENGTH_SHORT).show();
        }
    }
}
