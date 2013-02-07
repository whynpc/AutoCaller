package edu.ucla.cs.wing.bill.autocaller;

import edu.ucla.cs.wing.bill.autocaller.PhoneCall.Fun;
import android.os.Bundle;
import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity {

	private EditText editText_phoneNum;
	private EditText editText_duration;

	private Spinner spinner_function;
	private Button button_exe;

	private SharedPreferences settings;

	// For prefernces
	public static final String PREF_FILE = "pref";
	public static final String KEY_PHONE_NUM = "phone_num";
	public static final String KEY_DURATION = "duration";
	public static final String KEY_FUNCTION = "function";

	public static final String VALUE_CALL = "Call";
	public static final String VALUE_REJECT = "Reject";
	public static final String VALUE_ANSWER = "Answer";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.editText_phoneNum = (EditText) findViewById(R.id.editText_phoneNum);
		this.editText_duration = (EditText) findViewById(R.id.editText_duration);
		this.spinner_function = (Spinner) findViewById(R.id.spinner_function);
		this.button_exe = (Button) findViewById(R.id.button_exe);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] {
						VALUE_CALL, VALUE_ANSWER, VALUE_REJECT });
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_function.setAdapter(adapter);
		spinner_function
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						String text = spinner_function.getSelectedItem()
								.toString();
						if (text.equals(VALUE_CALL)) {
							PhoneCall.setFunction(Fun.CALL);
							Log.d("phonecaller", "switch function: call");
						} else if (text.equals(VALUE_ANSWER)) {
							PhoneCall.setFunction(Fun.ANSWER);
							Log.d("phonecaller", "switch function: answer");
						} else if (text.equals(VALUE_REJECT)) {
							PhoneCall.setFunction(Fun.REJECT);
							Log.d("phonecaller", "switch function: reject");
						} else {
							PhoneCall.setFunction(Fun.NONE);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						PhoneCall.setFunction(Fun.NONE);
					}
				});

		settings = getSharedPreferences(PREF_FILE, 0);
		if (settings.contains(KEY_PHONE_NUM)) {
			editText_phoneNum.setText(settings.getString(KEY_PHONE_NUM, ""));
		}
		if (settings.contains(KEY_DURATION)) {
			int duration = settings.getInt(KEY_DURATION, 3000);
			editText_duration.setText("" + (duration / 1000.0));
		}

		PhoneCall.init(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		PhoneCall.setFunction(Fun.NONE);
	}

	public void onClickButtonExe(View view) {
		String phoneNum = editText_phoneNum.getText().toString();
		int duration;
		try {
			duration = (int) Double.parseDouble(editText_duration.getText()
					.toString()) * 1000;
		} catch (Exception e) {
			duration = 3000;
		}
		updateDefaultSettings(phoneNum, duration);
		
		PhoneCall.setPhoneNum(phoneNum);
		PhoneCall.setDuration(duration);		
		if (PhoneCall.getFunction() == Fun.CALL) {
			PhoneCall.call();
		}
	}

	private void updateDefaultSettings(String phoneNum, int duration) {
		Editor editor = settings.edit();
		editor.putString(KEY_PHONE_NUM, phoneNum);
		editor.putInt(KEY_DURATION, duration);
		editor.commit();
	}
}
