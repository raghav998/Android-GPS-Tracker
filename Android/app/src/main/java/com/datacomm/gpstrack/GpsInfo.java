package com.datacomm.gpstrack;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Enumeration;

/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: GpsInfo.java
--
-- PROGRAM: AndroidGpsTrack
--
-- FUNCTIONS:
-- void getLocation()
-- String getWifiIpAddress(Context context)
-- void stopUsingGPS()
-- boolean isGetLocation()
-- void onLocationChanged(Location location)
-- void onStatusChanged(String provider, int status, Bundle extras)
--
--
-- DATE: March 9, 2016
--
-- REVISIONS:
--
-- DESIGNER:  Eunwon, Krystle, Oscar, Gabriel
--
-- PROGRAMMER: Eunwon Moon
--
-- NOTES: This program will get location information using either GPS or NetworkProvider
-- first check if the location setting is on, and check location using gps or network provider
--
----------------------------------------------------------------------------------------------------------------------*/
public class GpsInfo extends Service implements LocationListener {
    private static final long MIN_DISTANCE_UPDATES = 1;
    private static final long MIN_TIME_UPDATES =  10000;

    private final Context mContext;
    boolean GPSEnabled = false;
    static boolean NetworkEnabled = false;
    boolean GetLocationEnabled = false;

    private Location mylocation;

    protected LocationManager locManager = null;

    //constructor - initialize context
    public GpsInfo(Context c) {
        this.mContext = c;
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- Function: getLocation

    -- INTERFACE: void getLocation()
    --
    -- RETURNS:   void
    --
    -- NOTES:
    -- check GPS and network provider if it is working or not.
    -- using Criteria, get more accurate information and or coarse information
    -- in case ACCURATE_FINE is not working.
    --
    ----------------------------------------------------------------------------------------------------------------------*/
    //start to get location
    public void getLocation() {
        //permission check
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }

        try {

            //check if the service is possible or not
            locManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_UPDATES,MIN_DISTANCE_UPDATES,this);
            GPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_UPDATES,MIN_DISTANCE_UPDATES,this);
            NetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.e("CHECK", "getLoc");
            Criteria cr = new Criteria();

            cr.setPowerRequirement(Criteria.POWER_MEDIUM);
            cr.setAltitudeRequired(true);
            cr.setSpeedRequired(true);
            cr.setCostAllowed(true);
            cr.setBearingRequired(true);

            //API level 9 and up
            cr.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            cr.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
            cr.setBearingAccuracy(Criteria.ACCURACY_LOW);
            cr.setSpeedAccuracy(Criteria.ACCURACY_MEDIUM);

            cr.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locManager.getBestProvider(cr, true);
            cr.setAccuracy(Criteria.ACCURACY_COARSE);
            String providerCoarse = locManager.getBestProvider(cr, true);


            if(provider!=null && !provider.equals("")){
                this.GetLocationEnabled = true;
                if (GPSEnabled || NetworkEnabled) {
                    Log.e("CHECK", "GPS ENABLE");
                    if (mylocation == null) {
                        locManager.requestLocationUpdates(provider, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, this);
                        if (locManager != null) {
                            mylocation = locManager.getLastKnownLocation(provider);
                        }
                    }
                    if(mylocation == null)
                    {

                        locManager.requestLocationUpdates(providerCoarse, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, this);
                        if (locManager == null) {
                            mylocation = locManager.getLastKnownLocation(providerCoarse);
                        }

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    /*------------------------------------------------------------------------------------------------------------------
    -- Function: getWifiIpAddress
    -- INTERFACE: String getWifiIpAddress(Context context)
    --
    -- RETURNS:   String
    --                    -- return this device IP address
    --
    -- NOTES:
    --  This function is used to get Wifi IP address.
    --  Get ip address using wifiManager, and convert its into a byte array which is
    --  passed into a function to get host address.
    --  If there is null value of IP address, check IP using network provider
    ----------------------------------------------------------------------------------------------------------------------*/
    public String getWifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        if(ipAddressString == null){
            ipAddressString = GetNetworkIpAddress();
        }
        if(ipAddressString == null){
            Toast.makeText(mContext, "INTERNET NOT CONNECTED!",Toast.LENGTH_LONG);
            return "Internet Not Connected";
        }
        Log.d("client IP", ipAddressString);
        return ipAddressString;
    }
    /*------------------------------------------------------------------------------------------------------------------
    -- Function: GetNetworkIpAddress
    --
    -- INTERFACE: String GetNetworkIpAddress()
    --
    -- RETURNS:   String
    --                    -- return this device network IP address
    --
    -- NOTES:
    --  This function is used to get ip address using network providers.
    ----------------------------------------------------------------------------------------------------------------------*/
    private String GetNetworkIpAddress(){
        try{
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = ( NetworkInterface ) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = ( InetAddress ) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress instanceof Inet4Address)
                           return inetAddress.getHostAddress().toString();

                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*------------------------------------------------------------------------------------------------------------------
    -- Function: stopUsingGPS
    --
    -- INTERFACE: void stopUsingGPS()
    --
    -- RETURNS:   void
    --
    -- NOTES:
    -- if the locationManager is not null, remove this context from location provider
    --
    ----------------------------------------------------------------------------------------------------------------------*/
    public void stopUsingGPS() {
        if (locManager != null) {
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return ;
            }
            GetLocationEnabled = false;
            locManager.removeUpdates(GpsInfo.this);
        }
    }



    /*------------------------------------------------------------------------------------------------------------------
    -- Function: getNetwork
    --
    -- INTERFACE: boolean getNetwork()
    --
    -- RETURNS:   boolean
    --                    -- return network connect information
    --
    -- NOTES:
    -- return value which is showing if the network is working or not.
    ----------------------------------------------------------------------------------------------------------------------*/
    public static boolean getNetwork(){
        return NetworkEnabled;
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- Function: getLatLng
    --
    -- RETURNS:   Location
    --                    -- return current location information
    --
    -- NOTES:
    -- return value which is showing if the network is working or not.
    ----------------------------------------------------------------------------------------------------------------------*/
    public Location getLatLng(){
        return mylocation;
    }


    /*------------------------------------------------------------------------------------------------------------------
    -- Function: showSettingsAlert
    --
    -- RETURNS:    void
    --
    -- NOTES:
    -- display dialog to ask turnning on the Location setting.
    -- If the user choose 'Cancel', stay there
    -- Otherwise, move to Setting screen
    ----------------------------------------------------------------------------------------------------------------------*/
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS use setting");
        alertDialog.setMessage("GPS setting on?");

        //check setting Location
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });
        //cancle;
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }



    /*------------------------------------------------------------------------------------------------------------------
     -- Function: onLocationChanged
     -- RETURNS:    void
     --
     -- NOTES:
     -- This function is override LocationListener function.
     -- It will be update depending on requestLodcationUpdates function call parameter.
     -- This application will be update location information at least every 10 seconds,
     -- and if the person move over 1 meters.
     -- This new location information will be change global mlocation value.
     --
     ----------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
    }


    /*------------------------------------------------------------------------------------------------------------------
    -- Function: onStatusChanged
    --
    -- INTERFACE:  void onLocationChanged(Location location)
    --
    -- RETURNS:    void
    --
    -- NOTES:
    -- This function is override LocationListener function.
    -- It will be update depending on requestLodcationUpdates function call parameter.
    -- This application will be update location information at least every 3 seconds,
    -- and if the person move over 2 meters.
    -- This new location information will be change global mlocation value.
    --
    ----------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Toast.makeText(mContext, "Provider Out of Service", Toast.LENGTH_LONG);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Toast.makeText(mContext, "Provider Temporarily Unavailable", Toast.LENGTH_LONG);
                break;
            case LocationProvider.AVAILABLE:
                Toast.makeText(mContext, "Provider Available", Toast.LENGTH_LONG);
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


