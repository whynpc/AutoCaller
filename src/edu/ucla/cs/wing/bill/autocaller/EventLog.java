package edu.ucla.cs.wing.bill.autocaller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import android.os.Environment;
import android.util.Log;

public class EventLog {

	public static final String TAG = "autocaller";
	public static final String SEPARATOR = ",";

	public enum Type {
		HANDOVER, MONITOR, DEBUG, SLEEP, CALL, END_CALL, ANSWER
	};

	private static PrintWriter logFileWriter;
	
	private static boolean enabled;

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		EventLog.enabled = enabled;
	}

	public static void close() {
		if (logFileWriter != null) {
			logFileWriter.flush();
			logFileWriter.close();
			logFileWriter = null;
		}
	}

	public static void newLogFile(String fileName) {
		if (logFileWriter != null) {
			logFileWriter.flush();
			logFileWriter.close();
		}
		try {
			logFileWriter = new PrintWriter(new FileOutputStream(new File(
					Environment.getExternalStorageDirectory(), fileName)));
		} catch (FileNotFoundException e) {
			logFileWriter = null;
			write(Type.DEBUG, "Fail to open log file: " + e.toString());
		}
	}

	public static String genLogFileName(String[] parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append("autocaller");
		for (String parameter : parameters) {
			sb.append("_");
			sb.append(parameter);
		}
		sb.append(".txt");
		return sb.toString();
	}

	public static void write(Type type, String data) {
		if (enabled) {
			StringBuilder sb = new StringBuilder();
			sb.append(System.currentTimeMillis());
			sb.append(SEPARATOR);
			sb.append(type);
			if (data != null) {
				sb.append(SEPARATOR);
				sb.append(data);
			}

			Log.d(TAG, sb.toString());

			if (logFileWriter != null) {
				logFileWriter.println(sb.toString());
				logFileWriter.flush();
			}
		}
	}

}
