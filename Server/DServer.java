import java.sql.*;
import java.io.*;
import java.net.*;

public class DServer
{
	
	
	
	public static void main(String args[])
	{
		int port = 51234;
		if(args.length == 0){}
		else if(args.length == 1)
			port=Integer.parseInt(args[0]);
		else
		{
			System.out.println("Usage : java DServer <port>");
			System.exit(0);
		}
				
		
		try
		{
			Thread t= new ServerThread(port);
			t.start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}