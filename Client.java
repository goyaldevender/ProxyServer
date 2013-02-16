/*client.java
Code by : Devender Goyal
*/


import java.io.*;
import java.net.*;
import java.util.*;

class Client { 
	public static void main(String args[]) throws Exception
	{
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket(); 
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] sendData = new byte[1024]; 
		byte[] receiveData = new byte[102400];
		String sentence = inFromUser.readLine();
		String sentenceCopy=sentence;
		String toSend;
		String token[]=new String[2];
		int i=0;
		long startTime;
		long endTime;
		long responseTime;
		double rtt;
		StringTokenizer st = new StringTokenizer(sentenceCopy,"/");
		while (st.hasMoreTokens())
		{
			token[i]=st.nextToken();
			i++;
			if(i==1)
				{break;}
		}
		int len=token[0].length();
		if(sentenceCopy.length()>len)
		{token[1]=sentenceCopy.substring(len);}
		if(token[1] == null)
		{
			if(token[0].contains("iiit.ac.in"))
			//token[1]="index.html";
				{
					toSend="GET "+"/"+" HTTP/1.1"+"\n"+"Host: "+token[0]+"\n"+"\n";
				}
				else
				{
					toSend="GET "+"http://"+token[0]+"/"+" HTTP/1.1"+"\n"+"Host: "+token[0]+"\n"+"\n";
				}
		}
		else
		{
			if(token[0].contains("iiit.ac.in"))
			//System.out.println("host:  " +token[1] );
			{
				toSend="GET "+token[1]+" HTTP/1.1"+"\n"+"Host: "+token[0]+"\n"+"\n";
			}
			else
			{
				toSend="GET "+"http://"+token[0]+token[1]+" HTTP/1.1"+"\n"+"Host: "+token[0]+"\n"+"\n";	
			}
		}
		if(token[0].contains("print cache"))
		{
			toSend="print cache";
		}
		if(token[0].contains("search "))
		{
			toSend=sentence;
			//System.out.println("sending:"+toSend);
		}
		
		if(!token[0].contains("print cache") || !token[0].contains("search "))
		{
			System.out.println("the input url:"+sentence);
			String checkUrl="http://"+sentence;
			System.out.println("check url: "+checkUrl);
			try
			{
				URL url=new URL(checkUrl);
			}
			catch(Exception e)
			{
				System.out.println("%%%%%%%%%% INVALID URL %%%%%%%%%%% ENTER PROPER URL %%%%%%%%%%");
			}
				
		}
		
		System.out.println("to send:" +toSend );
		sendData = toSend.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,9876);
		startTime=System.nanoTime(); 
		clientSocket.send(sendPacket); 
		
		//DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
		String modifiedSentence=""; /**/
		String toPrint="";
		while(!modifiedSentence.contains("end of info"))
		{
			modifiedSentence="";
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
			clientSocket.receive(receivePacket);
			modifiedSentence = new String(receivePacket.getData(),0,0,receivePacket.getLength());
			//System.out.println(modifiedSentence);
			if(!modifiedSentence.contains("end of info"))
			{
				toPrint=toPrint+modifiedSentence;
			}
			else
			{
				toPrint=toPrint+"\n################### end of info ################### \n";
			}
			//clientSocket.close();
			//if(modifiedSentence.contains("end of info"));
			//{
			//	break;
			//}
			//System.out.println(modifiedSentence); 
		}
		endTime=System.nanoTime();
		responseTime=endTime-startTime;
		rtt=(responseTime*1.0)/1000000000; 
		System.out.println(toPrint+"\n"+"response time = "+rtt+" seconds"+"\n");
		clientSocket.close();
	}
}