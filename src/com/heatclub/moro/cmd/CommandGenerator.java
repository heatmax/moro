package com.heatclub.moro.cmd;

import android.content.Intent;
import org.jivesoftware.smack.packet.Message;
import java.util.Map;
import java.util.HashMap;
//import java.lang.app

import com.heatclub.moro.MainActivity;

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

	public CommandGenerator(Intent intent){
		setCommandIntent(intent);
		fillExecutorMap();
	}
	
	private void fillExecutorMap(){
		executorMap.put("main", MainActivity.class);
	//	executorTypeMap.put("main", EXECUTOR_TYPE_ACTIVITY);	
	}
	

	public boolean setCommandIntent(Intent intent){
		if(isCommandIntent(intent)){
			this.intent = intent;	
			return true;
		}
		else
			return false;
	}
	
	
	public boolean isCommandIntent(Intent intent){
		if(checkCommandIntent(intent))		
			return true;	
		else
			return false;
	}
	
	private boolean checkCommandIntent(Intent intent){
		return true;
	}
		
	public Intent getCommandIntent(){
		return this.intent; 	
	}
	
	public Intent createCommandIntent(Message msg){
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

	public String getTo(){
		return this.intent.getStringExtra("To");
	}

	public String getAuthor(){
		return this.intent.getStringExtra("Author");
	}

	public String getCommandName(){
		return this.intent.getStringExtra("Command");		
	}

	public String[] getArgs(){
		return this.intent.getStringArrayExtra("Args");
	}

	public String getArg(int i){
		String[] result = this.intent.getStringArrayExtra("Args");
		if(result != null)
			if(result.length > i)
				return result[i];
		return null;
	}

	public Class getExecutorClass(){
		Class cls = executorMap.get(getTo());		
		if(cls == null)
			return null;
		return cls;
	//	return "MainActivity";
	}
	
	public int getExecutorClassType(){
		String activity = "android.app.Activity";
		String service = "android.app.Service";

		if(!isCommandeMoro())
			return EXECUTOR_TYPE_UKNOWN;	
		if(executorMap.get(getTo()).getSuperclass().getName().equals(activity))
			return EXECUTOR_TYPE_ACTIVITY;
		if(executorMap.get(getTo()).getSuperclass().getName().equals(service))
			return EXECUTOR_TYPE_ACTIVITY;
		
		return EXECUTOR_TYPE_UKNOWN;
		
	}
	
	public boolean isCommandeMoro(){
		if(getExecutorClass() == null)
			return false;
		if(!intent.getAction().equals(ACTION))
			return false;
	//	if(getTo() == null)
	//		return false;
		if(getFrom() == null)
			return false;
		if(getAuthor() == null)
			return false;
		return true;	
	}
	
}
