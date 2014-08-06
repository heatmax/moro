package com.heatclub.frauder;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class MyService extends Service {
	private static final String TAG = "MyService";
	private static final String DOMAIN = "jabber.ru";
	private static final String PORT = "5222";
	private static final String HOST = "jabber.ru";
	private static final String USERNAME = "frauder_cl1";
	private static final String PASSWORD = "mainkey7";

	private XMPPConnection mXmppConnection;

	@Override
	public IBinder onBind(final Intent intent) {
		return new LocalBinder<MyService>(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	//	connect();
		new Thread(rConnection).start(); 		
/*
		if (!mXmppConnection.isConnected()) {
			Log.e(TAG, "Could not connect to the Xmpp server.");
			Toast.makeText(getBaseContext(), "Could not connect to Xmpp server."
						   , Toast.LENGTH_SHORT).show();
			
			return;
		}

		Log.i(TAG, "Yey! We're connected to the Xmpp server!");
		Toast.makeText(getBaseContext(), "Yey! We're connected to the Xmpp server!"
					   , Toast.LENGTH_SHORT).show();
					   
		ChatManager mChat = mXmppConnection.getChatManager();
		Chat chat = mChat.createChat("frauder_admin@jabber.ru", new MessageListener(){
		 public void processMessage(Chat chat, Message message) {
		 System.out.println("Received message: " + message);
		 }
		 });
		
		 try{
			 chat.sendMessage("Hello world");
		 }
		 catch(XMPPException e){
			 Toast.makeText(getBaseContext(), "ERRROR Send message"
							, Toast.LENGTH_SHORT).show();
			 
		 }
		 
		PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class));
		
		PacketListener myListener = new PacketListener() 
		{
			public void processPacket(Packet packet) 
			{
				if (packet instanceof Message) 
				{
					Message message = (Message) packet;
					// обработка входящего сообщения
				//	processMessage(message);
					String msg;
					msg = "Xmpp message received: '" + message.getBody() + "' on thread: " + getThreadSignature();
					//		Log.i(TAG, "Xmpp message received: '" + message.getBody() + "' on thread: " + getThreadSignature());
					Toast.makeText(getBaseContext(), "msg"
								   , Toast.LENGTH_SHORT).show();
					
				}
			}
		};
		
		mXmppConnection.addPacketListener(myListener, filter);
		//=========			   
/*		
		mChat.addChatListener(new ChatManagerListener() {

			@Override
			public void chatCreated(final Chat chat, final boolean createdLocally) {
				if (!createdLocally) {
					chat.addMessageListener(new MyMessageListener());
				}
			}
		});
*/
	}
	
	Runnable rConnection = new Runnable(){

		@Override
		public void run()
		{
			ConnectionConfiguration config = new ConnectionConfiguration("jabber.ru", 5222, "jabber.ru"); 	
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			
			mXmppConnection = new XMPPConnection(config);
			try {
				mXmppConnection.connect();
				mXmppConnection.login(USERNAME, PASSWORD);
			}
			catch (final XMPPException e) {
				Log.e(TAG, "Could not connect to Xmpp server.", e);
		//		Toast.makeText(getBaseContext(), "Could not connect to Xmpp server."
		//					   , Toast.LENGTH_SHORT).show();
				return;
			}
			catch (final Exception e) {
				Log.e(TAG, "-------Could not connect to Xmpp server.", e);
		//		Toast.makeText(getApplicationContext(), "Could not connect to Xmpp server."
		//					   , Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (!mXmppConnection.isConnected()) {
				Log.e(TAG, "------Could not connect to the Xmpp server.");
		//		Toast.makeText(getBaseContext(), "Could not connect to Xmpp server."
		//					   , Toast.LENGTH_SHORT).show();

				return;
			}

			Log.i(TAG, "--------Yey! We're connected to the Xmpp server!");
	//		Toast.makeText(getApplicationContext(), "Yey! We're connected to the Xmpp server!"
	//					   , Toast.LENGTH_SHORT).show();
			sendMessage("Service is start");
			
					
		}

	};
	
	public void sendMessage(String msg){
		
		ChatManager mChat = mXmppConnection.getChatManager();
		Chat chat = mChat.createChat("frauder_admin@jabber.ru", new MessageListener(){
				public void processMessage(Chat chat, Message message) {
					System.out.println("Received message: " + message);
				}
			});

		try{
			chat.sendMessage(msg);
		}
		catch(XMPPException e){
	//		Toast.makeText(getBaseContext(), "ERRROR Send message"
	//					   , Toast.LENGTH_SHORT).show();

		}

		
	}
	
	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		return Service.START_NOT_STICKY;
	}

	@Override
	public boolean onUnbind(final Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	//	mXmppConnection.disconnect();
	}

	private class MyMessageListener implements MessageListener {

		@Override
		public void processMessage(final Chat chat, final Message message) {
			String msg;
	//		msg = "Xmpp message received: '" + message.getBody() + "' on thread: " + getThreadSignature();
	//		Log.i(TAG, "Xmpp message received: '" + message.getBody() + "' on thread: " + getThreadSignature());
	//		Toast.makeText(getBaseContext(), "Xmpp message received" 
	//					   , Toast.LENGTH_SHORT).show();
			
			// --> this is another thread ('Smack Listener Processor') not the
			// main thread!
			// you can parse the content of the message here
			// if you need to download something from the Internet, spawn a new
			// thread here and then sync with the main thread (via a
			// Handler)
		}
	}

	public static String getThreadSignature() {
		final Thread t = Thread.currentThread();
		return new StringBuilder(t.getName()).append("[id=").append(t.getId()).append(", priority=")
				.append(t.getPriority()).append("]").toString();
	}

}
