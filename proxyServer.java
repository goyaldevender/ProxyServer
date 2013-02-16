/*proxyServer.java
Code by : Devender Goyal
*/


import java.io.*; 
import java.util.*;
import java.net.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

class store
{
	String req;
	String host;
	String loc;
	String data;
	String d;
}

class proxyServer 
{
	public static void main(String args[]) throws Exception 
	{
		store mycache[] = new store[100]; //// 
		DatagramSocket serverSocket = new DatagramSocket(9876);
		byte[] receiveData = new byte[1024]; 
		byte[] sendData = new byte[102400];
		int ai=0; //// array index
		String df; // date format
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss");
		String tempString;
		while(true) 
		{
			int flag=0;
			String stringToSend="";
			byte[] send_data;
			Date now=new Date(); ////
			int si=-1; //// search index
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String inputClient = new String(receivePacket.getData(),0,0,receivePacket.getLength());
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort(); 
			///////////////////////////////////
			if(inputClient.contains("print cache"))
			{
				int pciter;
				for(pciter=0;pciter<ai;pciter++)
				{
					stringToSend=stringToSend+"host: "+mycache[pciter].host+"\n"+"requested object: "+mycache[pciter].loc+"\n"+"data: "+mycache[pciter].data+"\n"+"******** end of an entry *********\n";
				}
			}
			else if(inputClient.contains("search "))
			{
				System.out.println("in search");
				String toSearch=new String();
				/*StringTokenizer stn = new StringTokenizer(inputClient," ");
				String token[]=new String[2];
				int tknpos=0;
				while (stn.hasMoreTokens())
				{
					token[tknpos]=stn.nextToken();
					tknpos++;
					if(tknpos==2)
						{break;}
				}
				
				System.out.println(token[1]);*/
				int pciter;
				toSearch=inputClient.substring(7);//,inputClient.length()-1);
				System.out.println("ip frm client: "+inputClient);
				System.out.println("to search: "+toSearch);
				
				for(pciter=0;pciter<ai;pciter++)
				{
					if((mycache[pciter].req).contains(toSearch))
					{
						stringToSend=stringToSend+mycache[pciter].req+"\n"+"***** next *****\n";
					}
				}
				
			}
			else
			{
			String inputClientCopy=new String(inputClient);
			//System.out.println(inputClientCopy );
			String token[]=new String[2];
			int i=0;
			StringTokenizer st = new StringTokenizer(inputClientCopy,"\n");
			while (st.hasMoreTokens())
			{
				token[i]=st.nextToken();
				i++;
				if(i==2)
					{break;}
			}
			System.out.println(inputClient );
			System.out.println("token1:" +token[0] );
			System.out.println("token2:" +token[1] );
			
			String addr;
			if(token[1].contains("iiit.ac.in"))
			{
				addr=token[1].substring(6);
				System.out.println("addr:"+addr );
			}
			else
			{
				addr=token[1].substring(13);
				System.out.println("addr:"+addr );
			}
			
			////
			for(int j=0;j<ai;j++)
			{
				if(inputClient.equals(mycache[j].req))
				{
					si=j;
				}
			}
			if(si==-1)
			{
				System.out.println("inside if "+inputClient);
				mycache[ai]=new store();
				mycache[ai].req=inputClient;
				mycache[ai].host=addr;
				mycache[ai].loc=token[0].substring(4,token[0].length()-9);
				//tempString=inputClient;
				//inputClient=tempString;
			}
			else
			{
				System.out.println("in cache");
				//df=sdf.format(mycache[si].d);
				//tempString=inputClient+"If-modified-since: "+df+"\n"+"\n";
				tempString=token[0]+"\n"+token[1]+"\n"+"If-Modified-Since: "+mycache[si].d+"\n";
				//System.out.println("coditional get request \n"+tempString);
				inputClient=tempString;
				System.out.println("coditional get request \n"+inputClient);
			}
			////
			InetAddress ipad;
			Socket proxySocket;
			
			if(addr.contains("iiit.ac.in"))
			{
				try
				{
					ipad=InetAddress.getByName(addr);
				}
				catch (Exception e)
				{
					String urlError="%%%%%%%%%% INVALID URL %%%%%%%%%%% ENTER PROPER URL %%%%%%%%%%";
					DatagramPacket sendPack;
					send_data=urlError.getBytes();
					sendPack = new DatagramPacket(send_data, send_data.length, IPAddress,port); 
					serverSocket.send(sendPack);
					//
					String endme="end of info";
					//smallMsg="end of info";
					send_data=endme.getBytes();
					sendPack = new DatagramPacket(send_data, send_data.length, IPAddress,port); 
					serverSocket.send(sendPack);
					//
					continue;
				}
				
				proxySocket = new Socket(ipad,80);
			}
			else
			{
				ipad=InetAddress.getByName("hostelproxy.iiit.ac.in");
				proxySocket = new Socket(ipad,8080);
				
			}
			//System.out.println("addr:  " + requestAddr);
			//DataOutputStream outToServer=new DataOutputStream(proxySocket.getOutputStream());
			PrintWriter outToServer = new PrintWriter(proxySocket.getOutputStream(), true);
			BufferedReader inFromServer=new BufferedReader(new InputStreamReader(proxySocket.getInputStream()));
			
			/*tempString=inputClient;
			inputClient=tempString;*/
			System.out.println("modified request\n"+inputClient);
			outToServer.println(inputClient);
			StringBuilder webpage = new StringBuilder();
			while ((i = inFromServer.read()) != -1) {
			            webpage.append((char) i);
			        }
			/////System.out.println(webpage.toString());
			
			////
			System.out.println("first 12 characters "+(webpage.toString()).substring(0,12));
			if(((webpage.toString()).substring(0,12)).equals("HTTP/1.1 304"))
			{
				//=> cache hit
				System.out.println("cache hit");
				/*byte[]*/ /*send_data=(mycache[si].data).getBytes();*/
				stringToSend=mycache[si].data;
				
			}
			else if(((webpage.toString()).substring(0,12)).equals("HTTP/1.0 504"))
			{
				stringToSend="%%%%%%%%%% INVALID URL %%%%%%%%%%% ENTER PROPER URL %%%%%%%%%%";
			}
			else{
				////
			
				/*byte[]*/ /*send_data = webpage.toString().getBytes();*/
				stringToSend=webpage.toString();
				String split[]=(webpage.toString()).split("\n");
				String lastmodified = split[3].substring(15);
				System.out.println("last-modified="+lastmodified);
				////System.out.println(webpage.toString());
				if(si==-1)
				{
					mycache[ai].data=webpage.toString();
					mycache[ai].d=lastmodified;
					ai++;
				}
				else
				{
					mycache[si].data=webpage.toString();
					mycache[si].d=lastmodified;
				}
			}
		} //////end of type of request
			// now break stringToSend into strings of sizes 1024 and send using packets
			DatagramPacket sendPacket; 
			int stlen=stringToSend.length();
			int iter=0;
			int low,high;
			String smallMsg;
			while(flag==0)
			{
				smallMsg="";
				low=1024*iter;
				high=1024*(iter+1);
				if(high>stlen)
				{
					high=stlen;
					flag=1;
				}
				smallMsg=stringToSend.substring(low,high);
				//System.out.println(smallMsg);
				send_data=smallMsg.getBytes();
				sendPacket = new DatagramPacket(send_data, send_data.length, IPAddress,port); 
				serverSocket.send(sendPacket);
				iter++;
				
			}
			String endit="end of info";
			//smallMsg="end of info";
			send_data=endit.getBytes();
			sendPacket = new DatagramPacket(send_data, send_data.length, IPAddress,port); 
			serverSocket.send(sendPacket);
		}
	}
}