import java.sql.*;
import java.io.*;
import java.net.*;

public class ServerThread extends Thread
{
	static Connection conn=null;
	static Statement stmt=null;
	
	static final String JDBC_DRIVER="com.mysql.jdbc.Driver";
	static final String DB_URL="jdbc:mysql://localhost:3306/gps";
	
	static final String USER="root";
	static final String PASS="";
	
	
	String clientString;
	int clientPort;
	private DatagramSocket listeningSocket;
	DatagramPacket dGram;
	static final int dGramSize=2048;
	byte[] packetData;
	InetAddress addr;
	
	public ServerThread(int port) throws IOException
	{
		listeningSocket =new DatagramSocket(port);
		listeningSocket.setSoTimeout(100000);
	}
	
	public void run()
	{
		try
		{
			Class.forName(JDBC_DRIVER);
			System.out.println("Connecting to DB");
			conn=DriverManager.getConnection(DB_URL, USER, PASS);
			stmt=conn.createStatement();
		}
		catch (SQLException se) {
				System.out.println("SQL Exception");
				se.printStackTrace();
				System.exit(0);
		}
		catch (Exception e) {
			System.out.println("Handle Exception");
			e.printStackTrace();
		}
		
		
		while(true)
		{
			packetData=new byte[dGramSize];
			dGram=new DatagramPacket(packetData, dGramSize);
			
			clientString=null;
			
			try
			{
				System.out.println("Listening on port: "+listeningSocket.getLocalPort());
				listeningSocket.receive(dGram);
				addr=dGram.getAddress();
				clientPort=dGram.getPort();
				System.out.println("Datagram from: "+addr+":"+clientPort);
				
				clientString=new String(packetData,0,dGram.getLength());
				System.out.println("Message : "+clientString.trim());
				
				int rs=stmt.executeUpdate(clientString);
				dGram=new DatagramPacket(packetData, dGramSize,addr,clientPort);
				packetData=null;
				clientString=null;
				try
				{
					listeningSocket.send(dGram);
				}
				catch(IOException ex)
				{
					System.out.println("Could not send : "+ex.getMessage());
					System.exit(0);
				}
			}
			catch (SocketTimeoutException s) {
					System.out.println("Socket timed out");
					listeningSocket.close();
					break;
			}
			catch (SQLException se) {
					System.out.println("SQlException");
					se.printStackTrace();
			}
			catch (IOException io) {
					System.out.println(io);
					listeningSocket.close();
					break;
			}
			
		}
		try
		{
			if(stmt!=null)
				stmt.close();
		}
		catch(SQLException se2){}
		try
		{
			if(conn!=null)
				conn.close();
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}

	}
}