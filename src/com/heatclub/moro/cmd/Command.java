package com.heatclub.moro.cmd;

import android.content.Intent;
import org.apache.harmony.javax.security.sasl.*;

public class Command extends Intent{
	private static final String ACTION = "com.heatclub.moro.ACTION_MESSAGE";
	
	Command(){
		setAction(ACTION);
	}
	
	public void setFrom(String from){
		putExtra("From", from);
	}
	
	public void setTo(String to){
		putExtra("To", to);
	}
	
	public void setAuthor(String author){
		putExtra("Author", author);
	}
	
	public void setCommand(String command){
		putExtra("Command", command);
	}
	
	public void setArgs(String[] args){
		putExtra("Args", args);
	}
	
	public String getFrom(){
		return getStringExtra("From");
	}
	
	public String getTo(){
		return getStringExtra("To");
	}
	
	public String getAuthor(){
		return getStringExtra("Author");
	}
	
	public String getCommand(){
		return getStringExtra("Command");		
	}
	
	public String[] getArgs(){
		return getStringArrayExtra("Args");
	}
	
	public String getArgs(int i){
		String[] result = getStringArrayExtra("Args");
		if(result != null)
			if(result.length > i)
				return result[i];
		return null;
	}
	
	
	
}
