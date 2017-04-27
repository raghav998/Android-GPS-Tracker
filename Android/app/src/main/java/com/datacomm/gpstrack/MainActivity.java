package com.datacomm.gpstrack;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;
/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: MainActivity.java
--
-- PROGRAM: AndroidGpsTrack
--
-- FUNCTIONS:
-- void daemonize (void)
-- int initialize_inotify_watch (int fd, char pathname[MAXPATHLEN])
-- int ProcessFiles (char pathname[MAXPATHLEN])
-- unsigned int GetProcessID (char *process)
--
-- NOTES: This program will be first page of android application
--        Initialize EditText and Button in xml file and get user input value
--      which are name, server ip address and port number.
--      check each value and pass to locationActivity.
--
----------------------------------------------------------------------------------------------------------------------*/

public class MainActivity extends AppCompatActivity {
    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    EditText nameEdit, ipEdit, portEdit;
    TextView errMsg;
    String nameStr, ipStr;
    int portNum;
    Button mapBtn;


    /*------------------------------------------------------------------------------------------------------------------
    -- FUNCTION: onCreate
    --
    -- INTERFACE: void init();
    --
    -- RETURNS: void
    --
    -- NOTES:
    -- This function is used to show xml file.
    ----------------------------------------------------------------------------------------------------------------------*/
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }



    /*------------------------------------------------------------------------------------------------------------------
    -- FUNCTION: init
    --
    -- INTERFACE: boolean checkInput()
    --
    -- RETURNS: void
    --
    -- NOTES:
    -- This function is used to initialize EditText, TextView, and Button
    -- in xml to get a values. Also set up button action to get value.
    -- If all input is valid, connect to location screen.
    --
    ----------------------------------------------------------------------------------------------------------------------*/
    public void init(){

        nameEdit = (EditText)findViewById(R.id.eName);
        ipEdit = (EditText)findViewById(R.id.eIP);
        portEdit = (EditText)findViewById(R.id.ePort);
        errMsg = (TextView)findViewById(R.id.errMsg);

        nameEdit.setText("");
        ipEdit.setText("");
        errMsg.setText("");

        mapBtn = (Button)findViewById(R.id.btn_gps);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LocationActivity.class);
                errMsg.setText("");
                if(!checkInput()){
                    return;
                }

                i.putExtra("name", nameStr);
                i.putExtra("ip", ipStr);
                i.putExtra("port", portNum);
                startActivity(i);
            }
        });
    }


    /*------------------------------------------------------------------------------------------------------------------
    -- FUNCTION: checkInput
    --
    -- RETURNS: boolean - check if all input value is valid or not
    --
    -- INTERFACE: boolean validate(String ip)
    --                  ip: ip address input in string type
    --
    -- NOTES:
    -- This function is used to check input value.
    -- get name, ip, and port number first. and check if there is empty space
    -- and compare ip address to regular expression pattern using valid function.
    ----------------------------------------------------------------------------------------------------------------------*/
    private boolean checkInput(){
        nameStr = nameEdit.getText().toString();
        ipStr = ipEdit.getText().toString();



        Log.e("CHECK INOUT", "check start");
        if(nameStr.equals("")|| ipStr.equals("") || portEdit.getText().toString().equals("")){
            errMsg.setText("Please fill out the form.");
            Log.e("CHECK INOUT", "emptyspace");
            return false;
        }
        if (!ipStr.equals("") && !validate(ipStr)) {
            errMsg.setText("Please check ip address.");
            Log.e("CHECK INOUT", "format check");
            return false;
        }else {
            String[] splits = ipStr.split("\\.");
            for (int i = 0; i < splits.length; i++) {
                if (Integer.valueOf(splits[i]) > 255) {
                    errMsg.setText("Please check ip address.");
                    Log.e("CHECK INOUT", "ip individual num check");
                    return false;
                }
            }
        }
        //default port number is null
        portNum = Integer.parseInt(portEdit.getText().toString());
        if (portNum < 0 || portNum > 65546) {
            errMsg.setText("Please check port number");
            return false;
        }

        return true;
    }
    /*------------------------------------------------------------------------------------------------------------------
    -- FUNCTION: Validate
    --
    -- RETURNS: boolean - check if all input value is valid or not
    --
    -- INTERFACE: boolean validate(String ip)
    --                ip: ip address input to compare
    -- NOTES:
    -- compare IP address format pattern to ip input.
    ----------------------------------------------------------------------------------------------------------------------*/
    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }
}
