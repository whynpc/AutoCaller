package edu.ucla.cs.wing.bill.autocaller;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

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

	public static void init(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		try {
			Class c = Class.forName(tm.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			telephonyService = (ITelephony) m.invoke(tm);
			Log.d("autocaller", "Telephony service initialized");

		} catch (Exception e) {
			Log.d("autocaller",
					"Error in accessing Telephony Manager: " + e.toString());
		}
		PhoneCall.context = context;
	}

	public static void reset() {
		telephonyService = null;
		context = null;
	}

	public static void endCall() {
		try {
			if (telephonyService != null)
				telephonyService.endCall();
		} catch (RemoteException e) {
			Log.d("autocaller", "End call error: " + e.toString());
		}
	}

	public static void call() {
		if (context != null && function == Fun.CALL) {
			Log.d("autocaller", "Before call: " + System.currentTimeMillis());
			Intent phoneIntent = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + phoneNum));
			context.startActivity(phoneIntent);
			Log.d("autocaller", "Begin sleep: " + System.currentTimeMillis());
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d("autocaller", "End sleep " + System.currentTimeMillis());
			PhoneCall.endCall();
			Log.d("autocaller", "After call: " + System.currentTimeMillis());
		}
	}

	private static void sleep(int time) {
		try {
			Log.d("autocaller", "Begin sleep: " + System.currentTimeMillis());
			Thread.sleep(time);
			Log.d("autocaller", "End sleep " + System.currentTimeMillis());

		} catch (Exception e) {
		}
	}

	private static void answer() {
		Intent answer = new Intent(Intent.ACTION_MEDIA_BUTTON);
		answer.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(answer, null);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (telephonyService != null && telephonyService.isRinging()) {
				Bundle bundle = intent.getExtras();
				String incomingNum = bundle
						.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				Log.d("autocaller",
						"On receive " + incomingNum + ": "
								+ System.currentTimeMillis());
				if (incomingNum.equals(phoneNum)) {
					switch (function) {
					case ANSWER:
						sleep(duration);
						answer();
						break;
					case REJECT:
						sleep(duration);
						telephonyService.endCall();
						break;
					case ANS_END:
						sleep(duration);
						answer();
						sleep(duration2);
						telephonyService.endCall();
						break;
					default:
						break;
					}
				}
			}
		} catch (RemoteException e) {
			Log.d("autocaller", "Receive call error: " + e.toString());
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

}
