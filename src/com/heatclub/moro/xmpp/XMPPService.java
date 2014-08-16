package com.heatclub.moro.xmpp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.XMPPConnection;
import com.heatclub.moro.cmd.*;

public class XMPPService extends Service {

	private XMPPProvider xp;
	private final static String ACTION_TYPE = "com.heatclub.moro.INPUT_MESSAGE";
	private static String username = "frauder_cl1";
	private static String password = "mainkey7";
	
	private static String domain = "jabber.ru";
	private static int port = 5222;
	private static String server = "jabber.ru";
	
	private static final String TAG = "moro";
	
	@Override
	public IBinder onBind(final Intent intent) {
		return new LocalBinder<XMPPService>(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		xp = new XMPPProvider(domain, port, server);		
		xp.setListener(new stateListener());
	//	System.exit(0);
		xp.connect(username, password);	
/*		
		xp.sendMessage("test1", "frauder_admin@jabber.ru");	
		xp.sendMessage("test2", "frauder_admin@jabber.ru");	
//		xp.processSending(0);
		xp.sendMessage("test3", "frauder_admin@jabber.ru");	
		xp.sendMessage("test4", "frauder_admin@jabber.ru");	
//		xp.processSending(1);
/*		xp.sendMessage("test5", "frauder_admin@jabber.ru");	
		xp.sendMessage("test6", "frauder_admin@jabber.ru");	
		xp.sendMessage("test7", "frauder_admin@jabber.ru");	
		//Log.d("MORO", xp.readMessage());
//		xp.connect(domain, port, server);
/*		nm.sendMessage("test2", "frauder_admin@jabber.ru");		
		nm.login("frauder_cl1", "mainkey7");
		nm.sendMessage("test3", "frauder_admin@jabber.ru");
//		nm.disconnect();
//		nm.createChat("frauder_admin@jabber.ru");
	//	nm.sendMessage("test4", "frauder_admin@jabber.ru");
*/
	}
	
	
	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
	
	//	nm.connect();
//		nm.sendMessage("command1", "frauder_admin@jabber.ru");
//		nm.sendMessage("command2", "frauder_admin@jabber.ru");
	//	nm.sendMessage("command3", "frauder_cl1@jabber.ru");
		
		return Service.START_NOT_STICKY;
	}

	@Override
	public boolean onUnbind(final Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		xp.disconnect();
	}

	public static String getThreadSignature() {
		final Thread t = Thread.currentThread();
		return new StringBuilder(t.getName()).append("[id=").append(t.getId()).append(", priority=")
				.append(t.getPriority()).append("]").toString();
	}

	class stateListener extends XMPPStateListener{
		
		@Override
		public void authenticated(XMPPConnection p1)
		{
			xp.sendMessage(username+" в сети", "frauder_admin@jabber.ru");	
			
		}
		
		@Override
		public void processPacket(Packet packet) 
		{
			if (packet instanceof Message) 
			{
				Message message = (Message) packet;
				if(message.getBody() != null){
					try{
				//		Log.d(TAG, "command: "+CommandGenerator.getDefault(message));
						CommandGenerator cmd = new CommandGenerator();
						cmd.createCommandIntent(message);
						sendBroadcast(cmd.getCommandIntent());
					//	String msg = "command: "+"\n"+cmd.getAuthor()+"\n"+cmd.getFrom()+"\n"+cmd.getTo()+"\n"+cmd.getCommand()+"\n"+cmd.getArgs(1);
					//	msg = "command: " + msg;// + "' on thread: " + getThreadSignature();
					//	xp.sendMessage(msg, message.getFrom());
					}
					catch(Exception e){

					}
				}	
			}
		}
	}		
	
}
