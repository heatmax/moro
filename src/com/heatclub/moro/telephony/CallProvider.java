package com.heatclub.moro.telephony;

import com.android.internal.telephony.ITelephony;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.net.Uri;
import java.util.Random;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import java.lang.reflect.Method;
import android.view.KeyEvent;
import com.heatclub.moro.db.MoroBase;

import android.os.*;

import java.util.concurrent.TimeUnit;
import java.io.*;
import java.sql.*;
import android.preference.*;

public class CallProvider{
	
	private Context tContext;
	private TelephonyManager tm;
	private MoroBase db;
	private static ITelephony iTel;
	private static Handler h;
	
//	private static final int NUMBER_TYPE_SINGLE = 0;
//	private static final int NUMBER_TYPE_MULTI = 1;
	
	private final int STATUS_NONE = 0; // нет подключения
	private final int STATUS_OUTGOING = 1; // звоним
	private final int STATUS_WAIT = 2; // В ожидании дозвона
	private final int STATUS_ANSWER = 3; // подняли трубку
	private final int STATUS_INCOMING = 4; // входящий вызов
		
//	private static String phoneNumber;
	private static String[] phoneNumberList;
//	private static int numberType;
	public static boolean isNumberRandom;
	public static boolean isNumberRepeat;
	private static int[] usedNumberIndex;
	
	
	private static int delayAutoCall;
	private static int durationAutoCall;
	
	public static boolean isAutoCall = false;
//	private static Thread threadAutoCall;
	
	private static int delayAutoAnswer = 0;	
	public static boolean isAutoAnswer = false;
	
	public static int currentStatus;
	public static int previousStatus;
	
	
	// определяем обработчики событий изменений состояния сети
	private PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(
			final int state, final String incomingNumber) {     
			String msg="null";
			previousStatus = currentStatus;
			
			h.removeCallbacks(rAutoCall);
					
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					msg = "N0NE";
					currentStatus = STATUS_NONE;
					if(isAutoCall && (previousStatus!= STATUS_NONE)){
						h.postDelayed(rAutoCall, delayAutoCall*1000);
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					if(previousStatus == STATUS_NONE){
						msg = "OUTGOING";
						currentStatus = STATUS_OUTGOING;
					}
					else
					if(previousStatus == STATUS_INCOMING){
						msg = "ANSWER";
						currentStatus = STATUS_ANSWER;					
					}
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					msg = "INCOMING";
					currentStatus = STATUS_INCOMING;
					if(isAutoAnswer)
						answerCall();
					db.insertNumber(incomingNumber);
					break;
				default:
					msg = "Not defined";
			}
			
				//displayMessage("Изменение состояния вызова - "+msg); 
						 

        }  
	};	
	
	CallProvider(Context context){
		tContext = context;
		// создаем экземпляр TelephonyManager
        tm = (TelephonyManager)tContext.getSystemService(
			Context.TELEPHONY_SERVICE);
		tm.listen(listener, 
				   PhoneStateListener.LISTEN_CALL_STATE);   
		db = new MoroBase(tContext);
		h = new Handler();
		h.post(rITelephony);
	}
	
	//создать в отдельном потоке экземпляр класса ITelephone//	public void createTelephonyService(){
	Runnable rITelephony = new Runnable() { 
            @Override 
            public void run() { 
				try{			
					String cName = tm.getClass().getName();
					Class c = Class.forName(cName);
					Method m = c.getDeclaredMethod("getITelephony");
					m.setAccessible(true);
					iTel = (ITelephony) m.invoke(tm);
				}
				catch(Exception e){
					Thread.currentThread().interrupt();
					displayMessage("ОШИБКА: in Runnable rITelephony"); 				
					//	Log.e("ERROR", "Thread Interrupted");
				}			
			}
	};

	
	//Runnable окончаниия вызова
	Runnable rEndCall = new Runnable() { 
		@Override 
		public void run() { 
			try{
			//	displayMessage("endStatus "+currentStatus);
				if(currentStatus == STATUS_OUTGOING)
					iTel.endCall();
			}
			catch(Exception e){
				Thread.currentThread().interrupt();
				displayMessage("ОШИБКА: in Runnable rEndCall"); 
				//	Log.e("ERROR", "Thread Interrupted");
			}			
		}
	};
/*	
	//Runnable вставки номера БД
	Runnable rDBInsertNumber = new Runnable() { 
		@Override 
		public void run() { 
			try{
				
				db.insertNumber();
			}
			catch(Exception e){
				Thread.currentThread().interrupt();
				//	Log.e("ERROR", "Thread Interrupted");
			}			
		}
	};
*/	
	//Runnable автоматического приема входящего вызова
	Runnable rAutoAnswerCall = new Runnable() { 
		@Override 
		public void run() { 
			try{
					switch(previousStatus){
					case STATUS_OUTGOING:
						TimeUnit.MILLISECONDS.sleep(500);
						Call("1");
					case STATUS_ANSWER:					
						h.post(rAnswerCall);
						TimeUnit.MILLISECONDS.sleep(1000);
						Call("3");
						break;
					default:
						h.post(rAnswerCall);	
					}
			}
			catch(Exception e){
				Thread.currentThread().interrupt();
				displayMessage("ОШИБКА: in Runnable rAutoAnswerCall"); 
				
				//	Log.e("ERROR", "Thread Interrupted");
			}			
		}
	};
	
	//Runneble принятия входящего вызова
	Runnable rAnswerCall = new Runnable() { 		
		@Override 
		public void run() { 
			try{
						
			//	iTel.answerRingingCall();			
				Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
				buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
				tContext.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");
				buttonDown = null;

				Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
				buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
				tContext.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
				buttonUp = null;
			}	
			catch(Exception e){
				Thread.currentThread().interrupt();
				displayMessage("ОШИБКА: in Runnable rAnswerCall"); 
				
				//	Log.e("EROR", "Thread Interrupted");
			}		
		}
	};
	
	//Runneble принятия входящего вызова
	Runnable rEndCall2 = new Runnable() { 		
		@Override 
		public void run() { 
			try{

//				iTel.call("2");

				Intent buttonDown = new Intent(Intent.ACTION_CALL_BUTTON);
				buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, 	KeyEvent.KEYCODE_ENDCALL));
				tContext.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");
				buttonDown = null;

				Intent buttonUp = new Intent(Intent.ACTION_CALL_BUTTON);
				buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENDCALL));
				tContext.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
				buttonUp = null;
				/*	
				 //	displayMessage(currentStatus);
				 if(previousStatus == STATUS_ANSWER)
				 iTel.call("3");
				 else*/
				//currentStatus = STATUS_ANSWER;

			}	
			catch(Exception e){
				Thread.currentThread().interrupt();
				displayMessage("ОШИБКА: in Runnable rAndCall2"); 
				
				//	Log.e("EROR", "Thread Interrupted");
			}		
		}
	};

	//Runnable автовызова
	Runnable rAutoCall = new Runnable() { 
		@Override 
		public void run() { 
			String num="null";
			try{
				num = getPhoneNumber(phoneNumberList);
				if((currentStatus == STATUS_NONE) && isAutoCall){
					Call(num);
				}
			//	h.postDelayed(rAutoCall, delayAutoCall*1000);
			}
			catch(Exception e){
				Thread.currentThread().interrupt();
				displayMessage("ОШИБКА: in Runnable rAutoCall. NUM :"+num); 
				
				//	Log.e("ERROR", "Thread Interrupted");
			}			
		}
	};
	
	public void displayMessage(String info){
		Toast.makeText(tContext, info, 
					   Toast.LENGTH_LONG).show();
	}
	
	//Закончить вызов
	public void endCall(int time){
		if(time > 0)
			h.postDelayed(rEndCall, time*1000);
		else
			h.post(rEndCall);		
	}
	
	//Закончить вызов
	public void endCall(){
		if(durationAutoCall > 0)
			h.postDelayed(rEndCall, durationAutoCall*1000);
		else
			h.post(rEndCall);		
	}
	
	//Принять вызов
	public boolean answerCall(){
		if(delayAutoAnswer > 0)
			h.postDelayed(rAutoAnswerCall, delayAutoAnswer*1000);
		else
			h.post(rAutoAnswerCall);
		
		return true;
	}
	
	//Совершить вызов
	public boolean Call(String number){	

	
		Uri uri = Uri.parse("tel:"+number);
		Intent dialogIntent = new Intent(Intent.ACTION_CALL, uri);
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		tContext.getApplicationContext().startActivity(dialogIntent);

		return true;
	}
	/*
	public String getNumber(){
		String num = "";
		
			switch(numberType){
			case NUMBER_TYPE_SINGLE:
				num = numberGenerator(phoneNumber);
				break;
			case NUMBER_TYPE_MULTI:
				num = numberGenerator(phoneNumberList);
				break;					
			}
		
			
		return num;
	}
	*/

	//Генератор случайного телефонного номера
	public String numberGenerator(String number){
		Random rand = new Random();
		Integer tm;
		char[] charArr = number.toCharArray();
		int kol = number.length();
		char mask = 'x';
		String result;
		
		for(int i=0; i<kol; i++){
			if(charArr[i] == mask){
				tm = rand.nextInt(9);
				charArr[i]=tm.toString().charAt(0);		
			}
		}
		result = new String(charArr);
		return result;
	}
	
	//Генератор случайного телефонного номера на основе списка
	public String getPhoneNumber(String [] number){
		Integer i=0;
		String result;		
			
	
		if(isNumberRandom){				
			Random rand = new Random();
			i=rand.nextInt(number.length);
	/*		Доработать режим рандом без повторения
			if(!isNumberRepeat){
				while(true){
					if(usedNumberIndex[i])
						i=rand.nextInt(number.length);
					else{
						usedNumberIndex[i]=i;
						break;
					}
				}
			}*/
		}
		else{
			if(usedNumberIndex.length >= number.length){
				if(isNumberRepeat){
					usedNumberIndex = new int[1];
				}
				else{
					autoCallOff();
					return "0";
				}
			}
			else{
				i=usedNumberIndex.length;
				usedNumberIndex = new int[i+1];				
			}
				
		}
		
		result=numberGenerator(number[i]);	
		return result;
	}
	
	//Установить номера дозвона
	public void autoCallOn(int delay, int duration, String [] nList, boolean random, boolean repeat){
		phoneNumberList = nList;
		delayAutoCall = delay;
		durationAutoCall = duration;
		isNumberRepeat = repeat;
		isNumberRandom = random;
		isAutoCall = true;
		if(random)
			usedNumberIndex = new int[nList.length];
		else
			usedNumberIndex = new int[0];
	
		h.postDelayed(rAutoCall, delayAutoCall*1000);
	}
	
	/*
	//Запустить автодозвон
	public void autoCallOn(int delay, String number){
		phoneNumber = number;
		delayAutoCall = delay;	
		numberType = NUMBER_TYPE_SINGLE;
		isAutoCall = true;
	//	h.post(rAutoCall);
		h.postDelayed(rAutoCall, delayAutoCall*1000);
	}
	*/
	//Остановить автодозвон
	public void autoCallOff(){
		isAutoCall = false;
	//	threadAutoCall.stop();
		h.removeCallbacks(rAutoCall);
	}
	
	//Включить автоподьем трубки
	public void autoAnswerOn(int delay){
		delayAutoAnswer = delay;	
		isAutoAnswer = true;
	}

	//Остановить автодозвон
	public void autoAnswerOff(){
		isAutoAnswer = false;
	}
	
	
}

