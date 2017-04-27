package com.datacomm.gpstrack;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: Network.java
--
-- PROGRAM: AndroidGpsTrack
--
-- FUNCTIONS:
-- void connect ()
-- void send(String lat, String lng, String name, String ipAddress)
-- String createPacket(String lat, String lng, String name, String ipAddress)
--
--
-- NOTES: This program is used to connect client to web server.
--	Use UDP socket to communicate with server.
--  Open server and received value from LocationActivity and create SQL query
--  as a String type, and send after making DatagramPacket
--
----------------------------------------------------------------------------------------------------------------------*/
public class Network {
    static final int DgramSize = 1024;
    String ServerName;
    int ServerPort;
    String ClientString;
    byte[] PacketData;
    InetAddress Addr;
    DatagramSocket ClientSocket;
    DatagramPacket dgram;


    //network constructor. initialize server ip and port number
    public Network(String server, int port)  {
        this.ServerName = server;
        this.ServerPort = port;
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- Function: connect
    --
    -- INTERFACE: void getLocation()
    --
    -- RETURNS:   void
    --
    -- NOTES:
    --   This function is to connect server through UDP socket.
    --   Using IP and socket input value, create socket and intialize bytestream packet data.
    --
    ----------------------------------------------------------------------------------------------------------------------*/
    public void connect ()
    {

        // Get the IP address of the Server
        //** get server information
        Log.d("CHECK SERVER IP", ServerName);
        try {
            Addr = InetAddress.getByName(ServerName);
            ClientSocket = new DatagramSocket();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        PacketData = new byte[DgramSize];

    }

    /*------------------------------------------------------------------------------------------------------------------
    -- Function: send
    --
    -- INTERFACE: void send(String lat, String lng, String name, String ipAddress)
    --					String lat  : latitude value
    --					String lng	: longitude value
    --					String name : user name
    --					String ipAddress : user device ip address
    --
    -- RETURNS:   void
    --
    -- NOTES:
    --  This function is to send data to server.
    --  using input variables, get sql query using createPacket function.
    --  convert string to Byte type and make Datagram Packet using byte data and Server information.
    --  semd datagram using clientSocket --
    ----------------------------------------------------------------------------------------------------------------------*/
    public void send(String lat, String lng, String name, String ipAddress) {
        ClientString = createPacket(lat, lng, name, ipAddress);
        System.arraycopy(ClientString.getBytes(), 0, PacketData, 0, ClientString.length());
        Log.e("SEND CHECK", ClientString);
        // Create the complete datagram
        dgram = new DatagramPacket(PacketData, PacketData.length, Addr, ServerPort);

        try {
            // Send the Datagram to the server
            String temp = new String("Sending Datagram to: " + ServerName + " on port " + ServerPort);
            Log.w("will be sendto", temp);

            ClientSocket.send(dgram);
        } catch (IOException ie) {
            System.out.println("Send Failure: " + ie.getMessage());
            System.exit(0);
        }
    }

    /*------------------------------------------------------------------------------------------------------------------
    -- Function: createPacket
    --
  -- INTERFACE: String createPacket(String lat, String lng, String name, String ipAddress)
    --					String lat  : latitude value
    --					String lng	: longitude value
    --					String name : user name
    --					String ipAddress : user device ip address
    --
    -- RETURNS:   String
    --                    -- return SqlQuery type string
    --
    -- NOTES:
    --   This function is make a String which form is like SQL query using parameters.--
    ----------------------------------------------------------------------------------------------------------------------*/
    public String createPacket(String lat, String lng, String name, String ipAddress){
       return new String("INSERT INTO `markers`(`name`, `lat`, `lng`, `ip`) VALUES ('"+name+"',"+lat+","+lng+",'"+ipAddress+"');");
    }

}
