package com.heatclub.moro.message;

public class Runner implements Runnable{

	String command;
	String[] args;

	public Runner(String command, String[] args) {
		this.command = command;
		this.args = args;
		run();
	}

	public void run() {
		try {


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}


