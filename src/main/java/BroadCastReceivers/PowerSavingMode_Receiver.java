package BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;

public class PowerSavingMode_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED))
        {
            final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (pm.isPowerSaveMode())
                {
                    Toast.makeText(context, "Power Saving Mode ON", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Power Saving Mode OFF", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
