package com.heatclub.moro;

import com.heatclub.moro.util.AppProtection;
import com.heatclub.moro.cmd.CommandGenerator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TabHost;
import android.text.method.ScrollingMovementMethod;
import android.telephony.TelephonyManager;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
//import junit.framework.*;
import android.app.ActionBar;
import java.text.SimpleDateFormat;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import org.xml.sax.*;

public class MainActivity extends Activity implements View.OnClickListener
{
/*	private static String ACTION = "com.lim.frauder.RUN";
    private static final String TYPE = "type";
    private static final int ID_ACTION_AUTOCALL_START = 0;
    private static final int ID_ACTION_AUTOCALL_STOP = 1;
	private static final int ID_ACTION_AUTOANSWER_START = 2;
	private static final int ID_ACTION_AUTOANSWER_STOP = 3;
	private static final int ID_ACTION_BROADCAST_STOP = 4;
	private static final int ID_ACTION_BROADCAST_START = 5;	
	private static final int ID_ACTION_SERVICE_STOP = 6;
	private static final int ID_ACTION_SERVICE_START = 7;	
*/	
	private static final int ID_MENU_PREF = 100;
	private static final int ID_MENU_EXIT = 101;
	private static final int ID_MENU_AUTOCALL = 102;
	private static final int ID_MENU_AUTOANSWER = 103;
	
	private static final int ID_NS_MANUAL = 1;
	private static final int ID_NS_FILE = 2;
	private static final int ID_NS_DB = 3;
	
	private TextView logView;
	private TextView info;
	private ProgressBar pbCall;
	private ActionBar actionBar;
	private SharedPreferences prefs;
	private AppProtection protect;
	
	private static boolean isAutoCall;
	private static boolean isAutoAnswer;
	private boolean bound;
	private ServiceConnection sConn;
	
	private String delayAutoAnswer;
	private String delay;
	private String callDuration;
	private String prefix;
	private String isRepeatNumber;
	private String isRandomNumber;
	private int numberSource;
	
	private CommandGenerator cmd;
    
	
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		actionBar = this.getActionBar();
	//	actionBar.setNavigationMode(3);	
	//	actionBar.setDisplayShowTitleEnabled(false);
	//	actionBar.setSelectedNavigationItem(1);
		//Проверить активацию приложения
		protect = new AppProtection(this);		
		protect.checkActivation(true);	
		
		//Запустить службу XMPP
		cmd = new CommandGenerator();
		if(!cmd.isCommandIntent(getIntent())){
			cmd.setAuthor("local");
			cmd.setFrom("MainActivity");
			cmd.setTo("xmpp");
			cmd.setCommandName("start");
			sendBroadcast(cmd.getCommandIntent());
		}
		else{
			//	processCommand(getIntent());
		}
		
	
		//Запустить службу приложения
	//	Intent intent2 = new Intent(ACTION);
	//	intent.putExtra(TYPE, ID_ACTION_SERVICE_START);
	//	sendBroadcast(intent);		
		
		
		setContentView(R.layout.main);		
		TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);       
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tag1");
		
        spec.setContent(R.id.tab1);
        spec.setIndicator("Главный");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Журнал");
        tabs.addTab(spec);
 /*
        spec = tabs.newTabSpec("tag3");
        spec.setContent(R.id.tabPage3);
        spec.setIndicator("Document 3");
        tabs.addTab(spec);
*/
        tabs.setCurrentTab(0);
		
		logView = (TextView)findViewById(R.id.logView);
		logView.setMovementMethod(new ScrollingMovementMethod());
		info = (TextView)findViewById(R.id.info);
		info.setMovementMethod(new ScrollingMovementMethod());		
		
		pbCall = (ProgressBar) findViewById(R.id.pbCall);

		if (prefs.getBoolean("isAutoAnswerStart", true)){
			autoAnswerStart();
		}
		
		sConn = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				addToLog("Приложение подключено к сервису \n");
				bound = true;
			}

			public void onServiceDisconnected(ComponentName name) {
				addToLog("Приложение отключено от сервиса \n");
				bound = false;
			}
		};
		
//		if (prefs.getBoolean("isAutoCallStart", false)) 	
//			bAutoCall.setChecked(true);
	
    }
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		cmd.setTo("tel");
		cmd.setCommandName("stop");
		sendBroadcast(cmd.getCommandIntent());
		
		cmd.setTo("xmpp");
		cmd.setCommandName("stop");
		sendBroadcast(cmd.getCommandIntent());
		
		
	//	Intent intent = new Intent(ACTION);
	//	intent.putExtra(TYPE, ID_ACTION_BROADCAST_STOP);
	//	sendBroadcast(intent);
	}
	
	//сохранить состояние приложения
	@Override
    protected void onSaveInstanceState(Bundle outState) {/*
        outState.putString("logCall", logCall.getText().toString());
	  */  
	}
	
	//Загрузить состояние приложения
	protected void onLoadInstanceState(Bundle inState) {/*
        if (inState != null) {
            logCall.setText(inState.getString("logCall"));
	    }*/
    }


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		}
	}
	
	
	//запустить цикл автодозвона
	private void autoCallStart(){
		if(protect.checkActivation(true)) {	
			if(!isAutoCall){
		//		CommandGenerator command = CommandGenerator.getTelCommand();
			//	command.setCommand();
			
				
				String args[] = new String[6];
				args[0] = "start";
			 	args[1] = prefix;
				args[2] = delay;
				args[3] = callDuration;
				args[4] = isRandomNumber;
				args[5] = isRepeatNumber;
				
				cmd.setTo("tel");
				cmd.setCommandName("autocall");
				cmd.setArgs(args);
				sendBroadcast(cmd.getCommandIntent());
				addToLog("Дозвон запущен...\n");
				isAutoCall = true;
			}
		}
		else
			Toast.makeText(getApplicationContext(), 
						   "Нужно активировать приложение", Toast.LENGTH_SHORT).show();											
		
	}
	
	
	//остановить выполнение цикла автодозвона
	private void autoCallStop(){
		if(isAutoCall){
			String[] args = new String[1];
			args[0] = "stop";
			cmd.setTo("tel");
			cmd.setCommandName("autocall");
			cmd.setArgs(args);
			sendBroadcast(cmd.getCommandIntent());
			addToLog("Дозвон остановлен...\n");
			isAutoCall = false;
		}
	}
	
	
	//Включить выполнение автоприема вызова
	private void autoAnswerStart(){
		if(!isAutoAnswer){
			String[] args = new String[1];
			args[0] = "on";
			cmd.setTo("tel");
			cmd.setCommandName("autoanswer");
			cmd.setArgs(args);
			sendBroadcast(cmd.getCommandIntent());
			addToLog("Автоответчик активирован...\n");
			isAutoAnswer = true;
		}
	}
	
	//остановить выполнение автоответчика
	private void autoAnswerStop(){
		if(isAutoAnswer){
			String[] args = new String[1];
			args[0] = "off";
			cmd.setTo("tel");
			cmd.setCommandName("autoanswer");
			cmd.setArgs(args);
			sendBroadcast(cmd.getCommandIntent());
			addToLog("Автоответчик деактивирован...\n");
			isAutoAnswer = false;
		}
	}
	
	
	//============

    @Override
    public void onResume() {
        super.onResume();
		StringBuilder sb = new StringBuilder();
		//info.setTextColor(9);
	
		String EOL = "\n";
		//String msgNS = "неизвестен";
		//Получить данные из настроек пользователя
		//Время задержки между вызовами
		delay =prefs.getString(getString(R.string.defaultDelayAutoCall), "9");
		//Длительность вызова
		callDuration = prefs.getString(getString(R.string.defaultCallTime), "13");
		//Массив номеров дозвона
        prefix = prefs.getString(getString(R.string.defaultPrefix), "+38067xxxxxxx");
	//	prefix = nums.split("\n");
      	//Задержка перед автоподьемом
		delayAutoAnswer = prefs.getString(getString(R.string.defaultDelayAutoAnswer), "0");
		
		//String listValue = prefs.getString("list", "не выбрано");
		switch(Integer.parseInt(prefs.getString("listMethod", "0"))){
		case ID_NS_MANUAL:
			numberSource = ID_NS_MANUAL;
			if (prefs.getBoolean(getString(R.string.isRandomManualNumber), true)) 
     	       isRandomNumber="true";
    	    else
				isRandomNumber="false";
		
			if(prefs.getBoolean(getString(R.string.isRepeatManualNumber), true)) 
    	        isRepeatNumber="true";
    	    else
				isRepeatNumber="false";
			sb.append("Повторять: "+isRepeatNumber).append(EOL);
			sb.append("Случайно: "+isRandomNumber).append(EOL);			
			sb.append("Источник номеров: Ручной ввод").append(EOL);
			break;
			
		case ID_NS_FILE:
				sb.append("Источник номеров: Из файла (не реализован)").append(EOL);
			break;
				
		case ID_NS_DB:
				sb.append("Источник номеров: Из БД (не реализован)").append(EOL);
			break;
				
		}
		

		 
	/*	isRandomManualNumber = Boolean.parseBoolean(
			prefs.getString(getString(R.string), "9"));
		*/
		
		sb.append("Повтор дозвона через:    ").append(delay).append(" с.").append(EOL);
		sb.append("Длительность вызова:    ").append(callDuration).append(" с.").append(EOL);
		sb.append("Поднимать трубку через:    ").append(delayAutoAnswer).append(" с.").append(EOL);
		
		if (prefs.getBoolean("isAutoAnswerStart", true)){
			autoAnswerStart();
			sb.append("Автоответчик:  Активирован").append(EOL);			
		}
		else{
			autoAnswerStop();
			sb.append("Автоответчик: Деактивирован").append(EOL);		
		}
		
		//infoMsg+="Источник номеров:    "+msgNS+"\n";
		sb.append("Номера дозвона:").append(EOL+EOL).append(prefix).append(EOL);	

		info.setText(sb);
	
		
		/*info.append("Текущий интервал дозвона: "+delay+"\n");
		info.append("Текущий код оператора: "+prefix+"\n");
		info.append("Текущий интервал сброса: "+callDuration+"\n");
		info.append("Текущий интервал автоприема: "+delayAutoAnswer+"\n");
	*/	
	/*
		if(bAutoCall.isChecked()){
			autoCallStop();
			autoCallStart();
		}
	*/
    }

	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		String acMsg = getString(R.string.infoAutoCallOff);
		int acIcon = R.drawable.ic_menu_autocall_off;
		
		if(isAutoCall){
			acMsg = getString(R.string.infoAutoCallOn);
			acIcon = R.drawable.ic_menu_autocall_on;		
		}
		menu.add(Menu.NONE, ID_MENU_AUTOCALL, 1, acMsg)
            .setIcon(acIcon)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | 
							MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, ID_MENU_PREF, 3, "Параметры")
            .setIcon(R.drawable.ic_menu_preferences)
          	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | 
							 MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add(Menu.NONE, ID_MENU_EXIT, 4, "Выход")
            .setIcon(R.drawable.ic_menu_exit)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | 
							 MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
     /*       case IDM_OPEN:
                openFile(FILENAME);
                break;
            case IDM_SAVE:
                saveFile(FILENAME);
                break;*/
			case ID_MENU_AUTOCALL:
				if(isAutoCall){
					item.setTitle(getString(R.string.infoAutoCallOff));
					item.setIcon(R.drawable.ic_menu_autocall_off);				
					autoCallStop();
				}
				else{
					item.setTitle(getString(R.string.infoAutoCallOn));
					item.setIcon(R.drawable.ic_menu_autocall_on);				
					autoCallStart();
				}
				break;
            case ID_MENU_PREF:
                Intent i = new Intent();
                i.setClass(this, PreferencesActivity.class);
                startActivity(i);
                break;
            case ID_MENU_EXIT:
                appExit();
                break;
            default:
                return false;
        }
        return true;
    }
	
	public void addToLog(String msg){

		long curTime = System.currentTimeMillis(); 
		String curStringDate = new SimpleDateFormat("dd.MM HH:mm:ss").format(curTime); 
		logView.append(curStringDate+" >>> "+msg);
	}
	
	public void appExit(){
		autoCallStop();
		autoAnswerStop();
					
		finish();
	}
/*
    private void openFile(String fileName) {
        try {
            InputStream inStream = openFileInput(FILENAME);

            if (inStream != null) {
                InputStreamReader tmp = 
                    new InputStreamReader(inStream);
                BufferedReader reader = new BufferedReader(tmp);
                String str;
                StringBuffer buffer = new StringBuffer();

                while ((str = reader.readLine()) != null) {
                    buffer.append(str + "\n");
                }

                inStream.close();
                edit.setText(buffer.toString());
            }
        }
        catch (Throwable t) {
            Toast.makeText(getApplicationContext(), 
						   "Exception: " + t.toString(), Toast.LENGTH_LONG)
                .show();
        }     
    }

    private void saveFile(String FileName) {
        try {
            OutputStreamWriter outStream = 
				new OutputStreamWriter(openFileOutput(FILENAME, 0));

            outStream.write(edit.getText().toString());
            outStream.close();      
        }
        catch (Throwable t) {
            Toast.makeText(getApplicationContext(), 
						   "Exception: " + t.toString(), Toast.LENGTH_LONG)
                .show();
        }
    }

    class FloatKeyListener extends NumberKeyListener {
        private static final String CHARS="0123456789-.";

        protected char[] getAcceptedChars() {
            return(CHARS.toCharArray()); 
        }

		@Override
		public int getInputType() {
			// TODO Auto-generated method stub
			return 0;
		}
    }*/
}
