package BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class IncomingCall_BroadCastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (intent.getAction().equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
            Bundle extras = intent.getExtras();
            if (extras != null)
            {
                String state_ = extras.getString(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
                {
                    String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Toast.makeText(context, "Incoming Call " + phoneNumber, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
