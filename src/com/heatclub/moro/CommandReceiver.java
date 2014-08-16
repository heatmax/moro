package com.heatclub.moro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;
import com.heatclub.moro.cmd.Command;
import com.heatclub.moro.cmd.CommandGenerator;




public class CommandReceiver extends BroadcastReceiver{

	private static final String ACTION_TYPE = "com.heatclub.moro.ACTION_MESSAGE";
	private static final String TYPE = "type";
	/*	private static final int ID_ACTION_AUTOCALL_START = 0;
	 private static final int ID_ACTION_AUTOCALL_STOP = 1;
	 private static final int ID_ACTION_AUTOANSWER_START = 2;
	 private static final int ID_ACTION_AUTOANSWER_STOP = 3;
	 */

    @Override
    public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_TYPE)) {
			CommandGenerator cmd = new CommandGenerator(intent);
	/*		
			Toast.makeText(context, "Exe name: "+cmd.getExecutorClass()+"\n Exe type: "+cmd.getExecutorClassType(),
						   Toast.LENGTH_LONG).show();	
	*/		
			switch(cmd.getExecutorClassType()){
				case CommandGenerator.EXECUTOR_TYPE_ACTIVITY:
					if(cmd.getExecutorClass() != null){
						intent.setClass(context, cmd.getExecutorClass());		
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);  	
					}
					break;
					
				case CommandGenerator.EXECUTOR_TYPE_SERVICE:
					if(cmd.getExecutorClass() != null){
						intent.setClass(context, cmd.getExecutorClass());		
						context.startService(intent);  	
					}				
					break;
			}		
		}
	}
}

