package com.heatclub.moro.message;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MsgManagerService extends Service
{

	ExecutorService executor;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		executor = Executors.newFixedThreadPool(1);
	}

	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		String command = intent.getStringExtra("command");
		String[] args = intent.getStringArrayExtra("args");
		commandRun cmd = new commandRun(command, args, startId);
		executor.execute(cmd);
		
		return Service.START_NOT_STICKY;
		
	}
		
	class commandRun implements Runnable {

		String command;
		String[] args;
		int startId;

		public commandRun(String command, String[] args, int startId) {
			this.command = command;
			this.args = args;
			this.startId = startId;
		}

		public void run() {
			try {
	//			MessageRouter com = new MessageRouter(command, args);	
//				com.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stop();
		}

		void stop() {
			stopSelf(startId);
		}
	}
	
	
}
