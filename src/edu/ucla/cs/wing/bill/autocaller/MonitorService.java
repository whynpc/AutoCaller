package edu.ucla.cs.wing.bill.autocaller;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.ucla.cs.wing.bill.autocaller.EventLog.Type;

import android.R.integer;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MonitorService extends Service {
	private static final int MONITOR_INTERVAL = 1000;

	private boolean started;
	
	private MobileInfo mobileInfo;
	
	private Timer timer;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}	

	@Override
	public void onCreate() {
		super.onCreate();		
		mobileInfo = new MobileInfo(this);
		timer = new Timer();
		start();
	}

	@Override
	public void onDestroy() {
		stop();
		super.onDestroy();
	}
	
	public boolean isStarted() {
		return started;
	}

	public void start() {
		started = true;
		timer.schedule(new MonitorTask(), 0, MONITOR_INTERVAL);		
	}

	public void stop() {
		started = false;
		timer.cancel();
		timer = new Timer();
	}
	
	public class MonitorTask extends TimerTask {

		@Override
		public void run() {
			StringBuilder sb = new StringBuilder();
			sb.append(mobileInfo.getNetworkType());
			sb.append(EventLog.SEPARATOR);
			sb.append(mobileInfo.getSignalStrengthDBM());
			sb.append(EventLog.SEPARATOR);
			sb.append(mobileInfo.getMobileRxByte());
			sb.append(EventLog.SEPARATOR);
			sb.append(mobileInfo.getMobileTxByte());
			EventLog.write(Type.MONITOR, sb.toString());
		}
	}
}
