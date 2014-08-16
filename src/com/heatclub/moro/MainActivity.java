package com.heatclub.moro;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.Intent;
import com.heatclub.moro.xmpp.XMPPService;
import com.heatclub.moro.cmd.CommandGenerator;



public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Intent intent = new Intent(this, XMPPService.class);
		stopService(intent);
		startService(intent);
		
		TextView i = (TextView)findViewById(R.id.info);
		
		Intent iCommand = getIntent();
		CommandGenerator cg = new CommandGenerator(iCommand);
		i.setText(cg.getAuthor());
		
    }

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
	}
		
	
	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		Intent intent = new Intent(this, XMPPService.class);
		stopService(intent);
		
	}
	
	
}
