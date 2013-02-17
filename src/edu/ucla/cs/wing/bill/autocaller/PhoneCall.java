package edu.ucla.cs.wing.bill.autocaller;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import com.android.internal.telephony.ITelephony;

import edu.ucla.cs.wing.bill.autocaller.EventLog.Type;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class PhoneCall extends BroadcastReceiver {
	public static final int DEFAULT_DURATION = 3000;

	public enum Fun {
		NONE, CALL, REJECT, ANSWER, ANS_END
	};

	private static ITelephony telephonyService;
	private static Context context;

	private static String phoneNum = "";
	private static int duration = DEFAULT_DURATION;
	private static int duration2 = DEFAULT_DURATION;

	private static Fun function;
	
	private static Timer timer = new Timer();

	public static void init(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		try {
			Class<?> c = Class.forName(tm.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			telephonyService = (ITelephony) m.invoke(tm);
		} catch (Exception e) {
		}
		PhoneCall.context = context;
	}

	public static void reset() {
		function = Fun.NONE;
		telephonyService = null;
		context = null;
	}

	public static void endCall(String phoneNum) {
		try {
			if (telephonyService != null) {
				EventLog.write(Type.END_CALL, phoneNum);
				telephonyService.endCall();
			}	
		} catch (RemoteException e) {
		}
	}

	public static void call() {
		if (context != null && function == Fun.CALL) {
			Intent phoneIntent = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + phoneNum));
			EventLog.write(Type.CALL, phoneNum);
			context.startActivity(phoneIntent);
			sleep(duration);
			PhoneCall.endCall(phoneNum);
		}
	}

	private static void sleep(int time) {
		try {
			EventLog.write(Type.SLEEP, "0");
			Thread.sleep(time);
			EventLog.write(Type.SLEEP, "1");
		} catch (Exception e) {
		}
	}

	private static void answer(String phoneNum) {
		EventLog.write(Type.ANSWER, phoneNum);
		Intent answer = new Intent(Intent.ACTION_MEDIA_BUTTON);
		answer.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(answer, null);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		EventLog.write(Type.DEBUG, "onReceive broadcast");
		try {
			if (telephonyService != null && telephonyService.isRinging()) {
				Bundle bundle = intent.getExtras();
				String incomingNum = bundle
						.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				EventLog.write(Type.DEBUG, "Incoming call: " + incomingNum);
				
				if (incomingNum.endsWith(phoneNum)) {
					switch (function) {
					case ANSWER:
						sleep(duration);
						answer(incomingNum);
						break;
					case REJECT:
						sleep(duration);
						endCall(incomingNum);
						break;
					case ANS_END:
						sleep(duration);
						answer(incomingNum);
						scheduleDelayEndCall(duration2, incomingNum);
						break;
					default:
						break;
					}
				}
			}
		} catch (RemoteException e) {		
		}
	}

	public static String getPhoneNum() {
		return phoneNum;
	}

	public static void setPhoneNum(String phoneNum) {
		PhoneCall.phoneNum = phoneNum;
	}

	public static int getDuration() {
		return duration;
	}

	public static void setDuration(int duration) {
		PhoneCall.duration = duration;
	}

	public static Fun getFunction() {
		return function;
	}

	public static void setFunction(Fun function) {
		PhoneCall.function = function;
	}

	public static int getDuration2() {
		return duration2;
	}

	public static void setDuration2(int duration2) {
		PhoneCall.duration2 = duration2;
	}
	
	public static void scheduleDelayEndCall(int duration2, String phoneNum) {
		timer.schedule(new EndCallTask(phoneNum), duration2);
	}

}
