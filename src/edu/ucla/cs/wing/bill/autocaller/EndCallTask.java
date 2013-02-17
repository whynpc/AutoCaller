package edu.ucla.cs.wing.bill.autocaller;

import java.util.TimerTask;

public class EndCallTask extends TimerTask {

	private String phoneNum;
	
	public EndCallTask(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		PhoneCall.endCall(phoneNum);
		
	}


}
