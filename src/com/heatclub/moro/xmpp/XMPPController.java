package com.heatclub.moro.xmpp;

import com.heatclub.moro.util.BlockingQueue;
import com.heatclub.moro.message.MessageRouter;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message;

import android.util.Log;
//import java.util.concurrent.TimeUnit;
import org.jivesoftware.smack.ConnectionListener;
//import org.apache.http.*;
//import java.lang.reflect.*;

public class XMPPController
{
	private static final String TAG = "moro";
		
	private xConnection connection;
	private Chat chat;
	
	private static BlockingQueue messageQueue;
	private MessageRouter messageRouter;
	//private ExecutorService messageEs;
	//private Thread checker;
	
	private boolean isTakeMessage;
	
	
//	public boolean isConnects;
//	public boolean isAutoConnect;
	
//	private String username;
//	private String password;	
	
//	private String recipient;	
		

	private MessageListener msgListener = new MessageListener(){
		public void processMessage(Chat chat, Message message) {
	//		message.getBody();
		//	sendMessage("Received message: "+message.getBody(), chat.getParticipant());
			
		}
	};
	
	PacketListener packetListener = new PacketListener(){
		public void processPacket(Packet packet) 
		{
			if (packet instanceof Message) 
			{
				Message message = (Message) packet;
				if(message.getBody() != null){
					try{	
						messageRouter.put(message);
						String msg;
						msg = "Message received: '" + message.getBody();// + "' on thread: " + getThreadSignature();
						sendMessage(msg, message.getFrom());
					}
					catch(Exception e){
					
					}
				}
				// обработка входящего сообщения
				//	processMessage(message);
				
			}
		}
	};	
	
	ConnectionListener connectionListener = new ConnectionListener() {

		@Override
		public void connected(XMPPConnection p1)
		{
	//		sendMessage("connected", "frauder_admin@jabber.ru");
			// TODO: Implement this method
		}


		
		@Override
		public void authenticated(XMPPConnection p1)
		{
			isTakeMessage = true;
			
			while(isTakeMessage){
				try{
					xMessage xm = (xMessage) messageQueue.take();
					new Thread(xm).start();	
				}
				catch(InterruptedException e){
					
				}
			}
		//	sendMessage("authenticated", "frauder_admin@jabber.ru");
			
			// TODO: Implement this method
		}

		@Override
		public void reconnectionSuccessful() {
		//	logger.info("Successfully reconnected to the XMPP server.");
		}

		@Override
		public void reconnectionFailed(Exception arg0) {
			isTakeMessage = false;
			
	//	logger.info("Failed to reconnect to the XMPP server.");
		}

		@Override
		public void reconnectingIn(int seconds) {
		//	logger.info("Reconnecting in " + seconds + " seconds.");
		}

		@Override
		public void connectionClosedOnError(Exception arg0) {
			isTakeMessage = false;
			
		//	logger.error("Connection to XMPP server was lost.");
		}

		@Override
		public void connectionClosed() {
			isTakeMessage = false;
			
		//	logger.info("XMPP connection was closed.");
		}
	};
	
	
	//Конструкто класса
	XMPPController(){
		connection = new xConnection();
	//	es = Executors.newFixedThreadPool(1);
	//	messageEs = Executors.newSingleThreadExecutor();
		messageQueue = new BlockingQueue(20);
		messageRouter = new MessageRouter();
	//	Log.d(TAG, "create xmpp");
	
	}
			
	public void sendMessage(final String msg, final String recipient){
	
	//	if(connection.xmpp.isConnected()){
			try{
				xMessage xmsg = new xMessage(msg, recipient);
				messageQueue.put(xmsg);
			}
			catch(InterruptedException e){
			
			}
		//	messageEs.execute(xmsg);
	//	}
		
		
	//	if(!connection.xmpp.isConnected() || !connection.xmpp.isAuthenticated() )
	//		pushMessage(msg);
//		else
//			new Thread(rSendMessage).start();			
	}	
	
	public void connect(final String domen, final int port, final String server){
						
		connection.connect(domen, port, server);
	//	es.execute(connection);
		
	}
	
	public void disconnect(){		
		connection.disconnect(false);
	}
	
	public void login(final String username, final String password){
		
		if(connection.xmpp != null) 
			connection.login(username, password);		
	}
		
	class xMessage implements Runnable{
	
	private String recipient;
	private String msg;
	
	xMessage(String msg, String recipient){
		this.msg = msg;
		this.recipient = recipient;
	}
	
	@Override
	public void run(){
		
		try {
			if(chat == null){
				chat = ChatManager.getInstanceFor(connection.xmpp).createChat(recipient, msgListener);
			}
			else{
				if(!chat.getParticipant().equals(recipient))
					chat = ChatManager.getInstanceFor(connection.xmpp).createChat(recipient, msgListener);					
			}	

			chat.sendMessage(msg);
			
		}
		catch (final Exception e) {
			Log.e(TAG, "Could not send message", e);
			System.exit(0);
		//	return;
			}	
		}
	}

	class xConnection{
		
		private String domen;
		private int port;
		private String server;
		private ExecutorService connectionEs;
		
		public boolean isConnects;
		
		public XMPPConnection xmpp;
		
		xConnection(){
			connectionEs = Executors.newFixedThreadPool(1);			
		}
		
		public void connect(String domen, int port, String server){
			this.domen = domen;
			this.port = port;
			this.server = server;
			
			ConnectionConfiguration config = new ConnectionConfiguration(domen, port, server); 	
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class));

			xmpp = new XMPPTCPConnection(config);		
			xmpp.addPacketListener(packetListener, filter);			
			xmpp.addConnectionListener(connectionListener);
			
				
			Runnable rConnect = new Runnable(){

				@Override
				public void run(){
					
				try {

					isConnects = true;					
					xmpp.connect();	
					isConnects = false;	

				}
				catch (final XMPPException e) {
					Log.e(TAG, "Could not connect to Xmpp server.", e);
					isConnects = false;
					return;
				}
				catch (final Exception e) {
					Log.e(TAG, "Could not connect to Xmpp server.", e);
					return;
				}
				}
			};	
			
			connectionEs.execute(rConnect);
			
		}
		
		public void disconnect(boolean now){
			
			Runnable rDisconnect = new Runnable(){

				@Override
				public void run(){
					try{
						xmpp.disconnect();
					}
					catch(Exception e){

					}
				}
			};
			
			if(xmpp.isConnected()){
				if(now)
					new Thread(rDisconnect).start();
				else
					connectionEs.execute(rDisconnect);
			}
			else
				connectionEs.shutdownNow();
			
		}
		
		public void login(final String username, final String password){

			Runnable rLogin = new Runnable(){

				@Override
				public void run()
				{
					try {
						connection.xmpp.login(username, password);
					}
					catch (final XMPPException e) {
						Log.e(TAG, "Could not login to Xmpp server.", e);
						return;
					}
					catch (final Exception e) {
						Log.e(TAG, "Could not login to Xmpp server.", e);
						return;
					}

					if (!connection.xmpp.isAuthenticated()) {
						Log.e(TAG, "Could not login to the Xmpp server.");
						return;
					}

					Log.i(TAG, "Yey! We're entered to the Xmpp server!");
					return;
				}

			};

			connectionEs.execute(rLogin);

		}
		
	
	}
	
	
}
