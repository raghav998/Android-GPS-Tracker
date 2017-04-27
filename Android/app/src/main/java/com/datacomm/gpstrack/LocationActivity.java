package com.datacomm.gpstrack;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: LocationActivity.java
--
-- PROGRAM: AndroidGpsTrack
--
-- FUNCTIONS:
-- void onCreate(Bundle savedInstanceState)
-- void initVal()
-- void onMapReady(GoogleMap googleMap)
-- void drawMarker(Location loc)
-- class MapUpdate extends AsyncTask<String, Location, Location>
--    protected Location doInBackground(Void)
--    protected void onProgressUpdate(Location... loc)
--
--
-- NOTES: This program will be tracking and connecting screen of android application
--   using MainActivity input value, connect socket, update location information
--   and send to server.
--   Using GoogleMap fragment, display my current location.
--   location tracking keep updating using AsyncTask.
--
----------------------------------------------------------------------------------------------------------------------*/
public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public TextView latTxt, lngTxt;
    Button homeBtn, startBtn;
    boolean start = false, init;
    private GpsInfo gps;
    String CltName, serverIp;
    int portNum;
    Location prevLoc;
    Network socketNtw;
    String ipAddress;
    GpsInfo gpsInfo;

    /*------------------------------------------------------------------------------------------------------------------
    -- FUNCTION: onCreate
    --
    --
    -- INTERFACE: void onCreate(Bundle savedInstanceState)
    --            saveInstanceState :
    --
    -- RETURNS: void
    --
    -- NOTES:
    -- This function is used to show xml file, and add mapfragment to get map
    -- Asynchronously.
    ----------------------------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initVal();
    }


    /*------------------------------------------------------------------------------------------------------------------
    -- FUNCTION: initVal
    --
    -- INTERFACE: void initVal()
    --
    -- RETURNS: void
    --
    -- NOTES:
    -- This function is used to initialize global values.
    -- Receive input values from previous page using intent
    -- and initialize boolean values to control each options.
    -- Also declare other java class values to get location and Connect to server
    -- This function initialize all information related to xml display and button listener.
    ----------------------------------------------------------------------------------------------------------------------*/

    public void initVal()  {
        gpsInfo = new GpsInfo(getApplicationContext());
        //get input value from main page
        Intent i = getIntent();
        CltName = i.getStringExtra("name");
        serverIp = i.getStringExtra("ip");
        portNum = i.getIntExtra("port", 51234);
        ipAddress = gpsInfo.getWifiIpAddress(getApplicationContext());

        socketNtw = new Network(serverIp, portNum);

        //initialize global value.
        init = true;
        prevLoc = null;
        gps = new GpsInfo(LocationActivity.this);

        //set xml view
        latTxt = (TextView) findViewById(R.id.latVal);
        lngTxt = (TextView) findViewById(R.id.lngVal);

        homeBtn = (Button) findViewById(R.id.mainBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                Intent i = new Intent(LocationActivity.this, MainActivity.class);
                gps.stopUsingGPS();
                startActivity(i);
            }
        });

        startBtn = (Button)findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = !start;
                if (start) {
                    gps.getLocation();
                    if(gps.GetLocationEnabled) {
                        init = true;
                        socketNtw.connect();
                        new MapUpdate().execute();
                        startBtn.setText("STOP");
                    }
                    else
                        start = false;

                } else {
                    gps.stopUsingGPS();
                    startBtn.setText("START");

                }
            }
        });
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- FUNCTION: onMapReady
    --
    -- INTERFACE: void onMapReady(GoogleMap googleMap)
    --                  googleMap : GoogleMap display using API
    --
    -- RETURNS: void
    --
    -- NOTES:
    --  This function is neccesary by implementing OnMapReadyCallback.
    --  Initialize google map and default map view
    ----------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LatLng bcit = new LatLng(49.2485, -123.0014);
        LatLng bcit = new LatLng(11.6059762,78.0076199);
        //11.6059762,78.0076199
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bcit, 10));
    }


    /*------------------------------------------------------------------------------------------------------------------
    -- FUNCTION: drawMarker
    --
    -- INTERFACE: void drawMarker(Location loc)
    --                 loc : the location information to add marker in map
    --
    -- RETURNS: void
    --
    -- NOTES:
    -- This function is to add location marker using location information.
    -- also move the display of map to show current location on the map.
    ----------------------------------------------------------------------------------------------------------------------*/

    private void drawMarker(Location loc){
        LatLng curPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
        //first point move the camera
        if(init){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPosition, 16));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
            init = !init;
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(curPosition));

        mMap.addMarker(new MarkerOptions().position(curPosition)
                .snippet("Lat:" + loc.getLatitude() + "Lng:" + loc.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(CltName));
    }




    /*------------------------------------------------------------------------------------------------------------------
    -- CLASS: MapUpdate
    --
    -- FUNCTIONS:
    -- void onCreate(Bundle savedInstanceState)
    -- void initVal()
    -- void onMapReady(GoogleMap googleMap)
    -- void drawMarker(Location loc)
    -- class MapUpdate extends AsyncTask<String, Location, Location>
    --    protected Location doInBackground(Void)
    --    protected void onProgressUpdate(Location... loc)
    --
    --
    -- INTERFACE: extend AsyncTask<Null, Location, Null>
    --            Location will be used for onProgressUpdate to change XML display
    --
    -- RETURNS: void
    --
    -- NOTES:
    -- This class is sub-class of location activity to update location information
    -- Asynchronously. By implementing AsyncTask, override doInBackground
    -- and onProgressUpdate functions.
    --
    ----------------------------------------------------------------------------------------------------------------------*/
    private class MapUpdate extends AsyncTask<Void, Location, Void> {
        int cnt =  5;
        Location curLoc;



        /*------------------------------------------------------------------------------------------------------------------
        -- Function: doInBackround
        --
        --
        -- INTERFACE: Location doInBackground(Void... params)
        --            Location will be last locatin
        --
        -- RETURNS: void
        --
        -- NOTES:
        -- This function will keep running until close the application.
        -- However, receiving update information when user start.
        -- get recent location information using GpsInfo class and check marker
        -- If the gps is in the same location, deduct count value instead of update information.
        -- If the user locate same spot until count become 0, just send same location info.
        -- the data will be update every 10 seconds.
        ----------------------------------------------------------------------------------------------------------------------*/
        @Override
        protected Void doInBackground(Void... params) {
            while(true) {
                if (start) {
                    curLoc = gps.getLatLng();
                    if(curLoc == null)
                        continue;
                    if(prevLoc == null){
                        String latStr = String.format("%.08f", curLoc.getLatitude());
                        String lngStr = String.format("%.08f", curLoc.getLongitude());
                        publishProgress(curLoc);
                        socketNtw.send(latStr, lngStr, CltName, ipAddress);
                        prevLoc = curLoc;
                    }
                    else if(cnt ==0 || (prevLoc.getLatitude() != curLoc.getLatitude() || prevLoc.getLongitude()!=curLoc.getLongitude())) {
                        cnt = 5;

                        String latStr = String.format("%.08f", curLoc.getLatitude());
                        String lngStr = String.format("%.08f", curLoc.getLongitude());

                        publishProgress(curLoc);
                        socketNtw.send(latStr, lngStr, CltName, ipAddress);
                        prevLoc = curLoc;
                    }
                    else {
                        cnt--;
                    }

                    //wait for a second.
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        /*------------------------------------------------------------------------------------------------------------------
        -- Function: onProgressUpdate
        --
        --
        -- INTERFACE: void onProgressUpdate(Location... loc)
        --            loc : current location data from doInBackground
        -- RETURNS:   void
        --
        -- NOTES:
        -- This function will update XML textView and add marker on the map.
        -- The data will be sent from doInBackground
        --
        ----------------------------------------------------------------------------------------------------------------------*/
        @Override
        protected void onProgressUpdate(Location... loc){
            Location myloc = loc[0];
            if(myloc != null) {
                latTxt.setText(Double.toString(myloc.getLatitude()));
                lngTxt.setText(Double.toString(myloc.getLongitude()));
                drawMarker(myloc);

                prevLoc = myloc;

                //JUST FOR CHECKING PROGRESS
                String lat = Double.toString(myloc.getLatitude());
                String lng = Double.toString(myloc.getLongitude());

                String s = lat + ", " + lng;
                Log.e("TEST on Progress", s);
            }
        }

    }



}

