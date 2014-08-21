package com.heatclub.moro.cmd;

import android.content.Intent;
import org.jivesoftware.smack.packet.Message;
import java.util.Map;
import java.util.HashMap;
//import java.lang.app

import com.heatclub.moro.MainActivity;
import com.heatclub.moro.xmpp.XMPPService;
import com.heatclub.moro.telephony.CallService;


public class CommandGenerator
{
	private Intent intent;
	private static final String ACTION = "com.heatclub.moro.ACTION_MESSAGE";
	public static final int EXECUTOR_TYPE_UKNOWN = 0;
	public static final int EXECUTOR_TYPE_ACTIVITY = 1;
	public static final int EXECUTOR_TYPE_SERVICE = 2;
	
	private Map<String, Class> executorMap = new HashMap<String, Class>();
//	private Map<String, Integer> executorTypeMap = new HashMap<String, Integer>();
	
	public CommandGenerator(){
		setCommandIntent(new Intent(ACTION));	
		fillExecutorMap();
	}

	public CommandGenerator(Intent intent) throws Exception{
		if(!isCommandIntent(intent))
			throw new Exception("This is not command intent in constructor of CommandGenerator");
	//	converteIntent(intent);
			
		setCommandIntent(intent);
		fillExecutorMap();
			
	}
	
	private Intent converteIntent(Intent intent){
		fillExecutorMap();		
		return new Intent(ACTION);
	}
	
	
	private void fillExecutorMap(){
		executorMap.put("main", MainActivity.class);
		executorMap.put("xmpp", XMPPService.class);
		executorMap.put("tel", CallService.class);		
	}
	

	public void setCommandIntent(Intent intent){
			this.intent = intent;	
	}
	
/*	
	public boolean isCommandIntent(Intent intent){
		if(checkCommandIntent(intent))		
			return true;	
		else
			return false;
	}
/*	
	private boolean checkCommandIntent(Intent intent){
		return true;
	}
*/		
	public Intent getCommandIntent(){
		return this.intent; 	
	}
	
	public Intent getCommandIntent(Message msg){
		fillIntentFromXMPPMessage(msg);
		return getCommandIntent();
	}
/*	
	public static Command getDefault(Intent intent){
		CommandGenerator cg = new CommandGenerator();
		return cg.getCommandFromIntent(intent);	
	}
*/	
	private void fillIntentFromXMPPMessage(Message msg){
		String[] msgElements = msg.getBody().split(" ");
		String to = null;
		String command = null;
		String[] args = null;
		
		setAuthor(msg.getFrom());		
		setFrom("xmpp");	
		
		//вычислить получателя команды
		if(msgElements.length>0)
			if(!msgElements[0].isEmpty())	
				to = msgElements[0];		
		
		//вычислить имя команды
		if(msgElements.length>1)
			if(!msgElements[1].isEmpty())	
				command = msgElements[1];		
		
		//вычислить аргументы команды
		if(msgElements.length>2){
			args = new String[msgElements.length-2];
	
			for(int i = 2 ; msgElements.length > i; i++){
				if(msgElements[i].isEmpty())
					args[i-2] = "null";
				else
					args[i-2] = msgElements[i];
			}
		}
		
		setTo(to);
		
		setCommandName(command);
		
		setArgs(args);
	}
/*	
	public Command getCommandFromIntent(Intent intent){
	//	msgElements = msg.getBody().split(" ");
		Command cmd = new Command();
		cmd.setAuthor(intent.getStringExtra("Author"));		
		cmd.setFrom(intent.getStringExtra("From"));	
		cmd.setTo(intent.getStringExtra("To"));
		cmd.setCommand(intent.getStringExtra("Command"));
		cmd.setArgs(intent.getStringArrayExtra("Args"));

		return cmd;
	}
/*	
	public void setAction(String action){
		this.intent.setAction(action);
	}
*/
	public void setFrom(String from){
		this.intent.putExtra("From", from);
	}

	public void setTo(String to){
		this.intent.putExtra("To", to);
	}

	public void setAuthor(String author){
		this.intent.putExtra("Author", author);
	}

	public void setCommandName(String command){
		this.intent.putExtra("Command", command);
	}

	public void setArgs(String[] args){
		this.intent.putExtra("Args", args);
	}

	public String getFrom(){
		return this.intent.getStringExtra("From");
	}
	
	public String getFrom(Intent tIntent){
		return tIntent.getStringExtra("From");
	}
	

	public String getTo(){
		return this.intent.getStringExtra("To");
	}
	
	public String getTo(Intent tIntent){
		return tIntent.getStringExtra("To");
	}
	
	public String getAuthor(){
		return this.intent.getStringExtra("Author");
	}
	
	public String getAuthor(Intent tIntent){
		return tIntent.getStringExtra("Author");
	}
	
	public String getCommandName(){
		return this.intent.getStringExtra("Command");		
	}
	
	public String getCommandName(Intent tIntent){
		return tIntent.getStringExtra("Command");		
	}
	
	public String[] getArgs(){
		return this.intent.getStringArrayExtra("Args");
	}

	public String[] getArgs(Intent tIntent){
		return tIntent.getStringArrayExtra("Args");
	}
	
	public String getArg(int i){
		String[] result = this.intent.getStringArrayExtra("Args");
		if(result != null)
			if(result.length > i)
				return result[i];
		return null;
	}
	
	public String getArg(int i, Intent tIntent){
		String[] result = tIntent.getStringArrayExtra("Args");
		if(result != null)
			if(result.length > i)
				return result[i];
		return null;
	}
	
	public Class getExecutorClass(String exName){
		Class cls = executorMap.get(exName);		
		if(cls == null)
			return null;
		return cls;
	}
	
	public Class getExecutorClass(Intent tIntent){
		Class cls = executorMap.get(getTo(tIntent));		
		if(cls == null)
			return null;
		return cls;
	}
	
	public Class getExecutorClass(){
		Class cls = executorMap.get(getTo());		
		if(cls == null)
			return null;
		return cls;
	}
	
	public int getExecutorClassType(){
		return getExecutorClassType(this.intent);
	}	
	
	public int getExecutorClassType(Intent tIntent){
		String activity = "android.app.Activity";
		String service = "android.app.Service";
		
		if(!isCommandIntent(tIntent))
			return EXECUTOR_TYPE_UKNOWN;	
		if(executorMap.get(getTo(tIntent)).getSuperclass().getName().equals(activity))
			return EXECUTOR_TYPE_ACTIVITY;
		if(executorMap.get(getTo(tIntent)).getSuperclass().getName().equals(service))
			return EXECUTOR_TYPE_SERVICE;
		
		return EXECUTOR_TYPE_UKNOWN;
		
	}
	
	public boolean isCommandIntent(Intent tIntent){
		if(tIntent == null)
			return false;
		if(getTo(tIntent) == null)
			return false;		
		if(getExecutorClass(tIntent) == null)
			return false;
		if(!tIntent.getAction().equals(ACTION))
			return false;
		if(getFrom(tIntent) == null)
			return false;
		if(getAuthor(tIntent) == null)
			return false;
		return true;	
	}
	
}
