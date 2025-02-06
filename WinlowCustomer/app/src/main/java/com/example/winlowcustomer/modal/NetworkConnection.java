package com.example.winlowcustomer.modal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.winlowcustomer.R;
import com.google.android.material.snackbar.Snackbar;

public class NetworkConnection {

    private static AlertDialog alertDialog;
    public static boolean hasConnection;

    public static void register(Context context){

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);

                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }

                hasConnection = true;
                Toast.makeText(context, R.string.connection_available,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);

                hasConnection = false;
                Toast.makeText(context, R.string.connection_lost,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();

                hasConnection = false;

                if (alertDialog == null) {
                    alertDialog = new AlertDialog.Builder(context)
                            .setTitle(R.string.connection_unavailable_title)
                            .setMessage(R.string.connection_unavailable)
                            .create();
                }

                if (!alertDialog.isShowing()) {
                    alertDialog.show();
                }
            }
        };

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        connectivityManager.registerNetworkCallback(networkRequest,networkCallback);

    }

}
