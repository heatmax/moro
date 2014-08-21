package com.heatclub.moro.telephony;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import com.heatclub.moro.cmd.CommandGenerator;

//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//import java.util.Timer;
//import java.util.TimerTask;

public class CallService extends Service {
/*	
	private static final String TYPE = "type";
	private static final int ID_ACTION_AUTOCALL_START = 0;
    private static final int ID_ACTION_AUTOCALL_STOP = 1;
	private static final int ID_ACTION_AUTOANSWER_START = 2;
	private static final int ID_ACTION_AUTOANSWER_STOP = 3;

	private static final int ID_ACTION_ENDCALL= 20;
	
	private static final int ID_NS_MANUAL = 1;
	private static final int ID_NS_FILE = 2;
	private static final int ID_NS_DB = 3;
	
	private final int STATUS_NONE = 0; // нет подключения
	private final int STATUS_OUTGOING = 1; // звоним
	private final int STATUS_WAIT = 2; // В ожидании дозвона
	private final int STATUS_ANSWER = 3; // подняли трубку
	private final int STATUS_INCOMING = 4; // входящий вызов
	
	private Timer timer;
	private TimerTask tTask;
	private static int callDuration;
	private static int delayCall;
	private static String[] numberList;
	
	private static boolean isAutoCall=false;	
	*/
	private static CallProvider call;
	private static CommandGenerator cmd;

	boolean isRandomNumber = true;
	boolean isRepeatNumber = true;
	
	
//	private SharedPreferences prefs;
	
	
	@Override
	public void onCreate() {					   
		call = new CallProvider(getApplicationContext());	
		cmd = new CommandGenerator();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		cmd.setCommandIntent(intent);
		
		if(cmd.getCommandName().equals("autocall"))
			autocall(cmd.getArgs());
		if(cmd.getCommandName().equals("endcall"))
			call.endCall();
			
		/*
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		switch(intent.getIntExtra(TYPE, -1)){
			case ID_ACTION_AUTOANSWER_START:	
				int delayAutoAnswer = Integer.parseInt(
					prefs.getString(getString(R.string.defaultDelayAutoAnswer), "0"));
				tm.autoAnswerOn(delayAutoAnswer);
	//			isAutoAnswer = true;
	//			Toast.makeText(getBaseContext(), "Автоподьем включен с задержкой "+delayAutoAnswer, Toast.LENGTH_SHORT).show();			
				break;	
				
			case ID_ACTION_AUTOANSWER_STOP:
				tm.autoAnswerOff();	
		//		Toast.makeText(getBaseContext(), "Автоподьем выключен.", Toast.LENGTH_SHORT).show();			
				break;
				
			case ID_ACTION_AUTOCALL_START:		
				callDuration = Integer.parseInt(
					prefs.getString(getString(R.string.defaultCallTime), "9"));
		
				String nums = prefs.getString(getString(R.string.defaultPrefix), "+38067xxxxxxx");
				numberList = nums.split("\n");
				
				delayCall = Integer.parseInt(
					prefs.getString(getString(R.string.defaultDelayAutoCall), "9"));
				
				boolean isRandomNumber = false;
				boolean isRepeatNumber = false;
				
				switch(Integer.parseInt(prefs.getString("listMethod", "0"))){
					case ID_NS_MANUAL: //источнтик номеров ручной ввод
						if (prefs.getBoolean(getString(R.string.isRandomManualNumber), false)) 
							isRandomNumber=true;
				
						if (prefs.getBoolean(getString(R.string.isRepeatManualNumber), false)) 
							isRepeatNumber=true;
						break;

					case ID_NS_FILE: //"Из файла (не реализован)";
						break;

					case ID_NS_DB: //"Из БД (не реализован)";
						break;

				}
								
				isAutoCall = true;
		//		schedule(delayCall, numberList, isRandomNumber, isRepeatNumber);		
				
				tm.autoCallOn(delayCall, numberList, isRandomNumber, isRepeatNumber);		
				//Toast.makeText(getBaseContext(), "Автодозвон включен с интервалом "+delayCall, Toast.LENGTH_SHORT).show();			
				break;
				
			case ID_ACTION_AUTOCALL_STOP:	
				isAutoCall = false;
				tm.autoCallOff();			
				//Toast.makeText(getBaseContext(), "Автодозвон выключен ", Toast.LENGTH_SHORT).show();			
				break;			

			case ID_ACTION_ENDCALL:
				if(isAutoCall){
				//	Toast.makeText(getBaseContext(), "Окончание вызова через "+callDuration+" с.", Toast.LENGTH_SHORT).show();						
					tm.endCall(callDuration);
				}
				break;
			}
			*/
		return START_REDELIVER_INTENT;
	}
	
	private void autocall(String[] args){
		if(args[0].equals("start")){
			String[] numberList = args[1].split("\n");
			int callDelay =Integer.parseInt(args[2]);		
			int callDuration = Integer.parseInt(args[3]);
	
			if(args[4] != null)
				isRandomNumber = Boolean.parseBoolean(args[4]);	
			if(args[5] != null)
				isRepeatNumber =  Boolean.parseBoolean(args[5]);
					
			call.autoCallOn(callDelay, callDuration, numberList, isRandomNumber, isRepeatNumber);		
			
			
			
		}
		else if(args[0].equals("stop")){
			call.autoCallOff();			
		}
	}
	/*
	public void schedule(int delay, final String[] lst, final boolean random, final boolean repeat) {
		if (tTask != null) tTask.cancel();
		if (delay > 0) {
			tTask = new TimerTask() {
				public void run() {
					if((tm.currentStatus == STATUS_NONE)){
						tm.isNumberRandom = random;
						tm.isNumberRepeat = repeat;
						String num = tm.getPhoneNumber(lst);
						tm.Call(num);
					}	
				}
			};
			timer.schedule(tTask, delay, delay);
		}
	}
*/
	@Override
	public IBinder onBind(Intent intent)
	{
		// Мы не предоставляем возможность привязки, поэтому возвращаем null.
		return null;
	}

	@Override
	public void onDestroy()
	{
		Toast.makeText(this, "Служба остановленна", Toast.LENGTH_SHORT).show();
	}
	
}
