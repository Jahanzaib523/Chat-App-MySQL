package BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AirplaneMode_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isAirMode = intent.getBooleanExtra("state", false);
        if(isAirMode)
        {
            Toast.makeText(context, "Airplane Mode Enabled", Toast.LENGTH_LONG).show();
        }
        /*else
        {
            Toast.makeText(context, "Airplane Mode Disabled", Toast.LENGTH_LONG).show();
        }*/
    }
}
