package com.heatclub.frauder;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.Intent;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Intent intent = new Intent(this, MyService.class);
		startService(intent);
    }
}
