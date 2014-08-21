package com.heatclub.moro.telephony;

import com.heatclub.moro.cmd.CommandGenerator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;



public class CallReceiver extends BroadcastReceiver{	
			
    @Override
    public void onReceive(Context context, Intent intent) {
		
			if (intent.getAction().equals(intent.ACTION_NEW_OUTGOING_CALL)){
				String phoneNum = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
				char[] phoneArr = phoneNum.toCharArray();
				if(phoneArr[0] != '*'){			
					CommandGenerator cmd = new CommandGenerator();
					cmd.setAuthor("local");
					cmd.setFrom("CallReceiver");
					cmd.setTo("tel");
					cmd.setCommandName("endcall");				
					Intent iCmd = cmd.getCommandIntent();
					iCmd.setClass(context, cmd.getExecutorClass());		
					context.startService(iCmd);  	
					
		//			context.startService(cmd.getCommandIntent());
			//		Toast.makeText(context, "Дозвон начат ",
		//						   Toast.LENGTH_LONG).show();		
					
				}
			}
			
				
		//		receiveOutCall();		
			
	}
}
