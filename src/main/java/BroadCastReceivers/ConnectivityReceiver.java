package BroadCastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectivityReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo!=null)
        {
            if(networkInfo.getType() == connectivityManager.TYPE_MOBILE)
            {
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
                {
                    boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                    if(!noConnectivity)
                    {
                        Toast.makeText(context, "Mobile Data Enabled", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context, "Mobile Data Disabled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else if (networkInfo.getType() == connectivityManager.TYPE_WIFI)
            {
                Toast.makeText(context, "Using Wifi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
