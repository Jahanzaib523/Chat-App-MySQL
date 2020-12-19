package BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PowerConnected_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED"))
        {
            Toast.makeText(context, "Charging", Toast.LENGTH_SHORT).show();
        }
        else if (intent.getAction().equals("android.intent.action.ACTION_POWER_DISCONNECTED"))
        {
            Toast.makeText(context, "Not Charging", Toast.LENGTH_SHORT).show();
        }
    }
}
