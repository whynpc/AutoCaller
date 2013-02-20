package edu.ucla.cs.wing.bill.autocaller;

import java.util.TimerTask;

public class CallTask extends TimerTask {
	
	private String phoneNum;
	
	public CallTask(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	@Override
	public void run() {
		PhoneCall.call(phoneNum);

	}

}
