package com.customer.example;

import com.customer.example.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

public class Settings extends Activity {

	static final String TRANSITION_KEY 				= "TRANSITION";
	static final String SILENCE_KEY 				= "SILENCE";
	static final String NSM_KEY 					= "NOISE,SPEECH,MUSIC";
	static final String RATIO_KEY 					= "RATIO";
	static final String LOCAL_KEY 					= "LOCAL";
	static final String ONLINE_KEY 					= "ONLINE";
	static final String NETWORK_KEY 				= "NETWORK";
	static final String DEBUG_KEY 					= "DEBUG";
	static final String ERROR_KEY 					= "ERROR";
	static final String FP_KEY 						= "FP";
	static final String MODE_KEY 					= "MODE";	
	static final String ACR_OPTIMIZATION_MODE_KEY 	= "ACR_OPTIMIZATION_MODE";		
	static final String SETTINGS_SHARED_PREFERENCES = "SettingsPref";
	
	public boolean getSettingsValue(String prefKey){
		
		SharedPreferences prefs=getSharedPreferences(SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		return prefs.getBoolean(prefKey, true);
	}
	
	public void setSettingsValue(String prefKey, boolean value){
		SharedPreferences prefs= getSharedPreferences(SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean(prefKey, value);
        editor.commit();
	}

	private int getAcrOptimizationModeIndex() {
		SharedPreferences prefs = getSharedPreferences(SETTINGS_SHARED_PREFERENCES, MODE_PRIVATE);
		return prefs.getInt(ACR_OPTIMIZATION_MODE_KEY, 0);
	}
	
	private void setAcrOptimizationModeIndex(int pos){
		SharedPreferences prefs= getSharedPreferences(SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
		editor.putInt(ACR_OPTIMIZATION_MODE_KEY, pos);
		editor.commit();
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createUI();		
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}
	
	private void createUI(){
		setContentView(R.layout.settings);
		
		CheckBox checkTransition = (CheckBox) findViewById(R.id.checkTransition);
		checkTransition.setChecked(getSettingsValue(TRANSITION_KEY));
		checkTransition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(TRANSITION_KEY, true);
				} else {
					setSettingsValue(TRANSITION_KEY, false);
				}
			}
		});
		
		CheckBox checkSilent = (CheckBox) findViewById(R.id.checkSilent);
		checkSilent.setChecked(getSettingsValue(SILENCE_KEY));
		checkSilent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(SILENCE_KEY, true);
				} else {
					setSettingsValue(SILENCE_KEY, false);
				}
			}
		});
		
		CheckBox checkNSM = (CheckBox) findViewById(R.id.checkNSM);
		checkNSM.setChecked(getSettingsValue(NSM_KEY));
		checkNSM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(NSM_KEY, true);
				} else {
					setSettingsValue(NSM_KEY, false);
				}
			}
		});
		
		CheckBox checkSilentRatio = (CheckBox) findViewById(R.id.checkSilentRatio);
		checkSilentRatio.setChecked(getSettingsValue(RATIO_KEY));
		checkSilentRatio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(RATIO_KEY, true);
				} else {
					setSettingsValue(RATIO_KEY, false);
				}
			}
		});
		
		CheckBox checkLocalQuery = (CheckBox) findViewById(R.id.checkLocalQuery);
		checkLocalQuery.setChecked(getSettingsValue(LOCAL_KEY));
		checkLocalQuery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(LOCAL_KEY, true);
				} else {
					setSettingsValue(LOCAL_KEY, false);
				}
			}
		});
		
		CheckBox checkOnlineQuery = (CheckBox) findViewById(R.id.checkOnlineQuery);
		checkOnlineQuery.setChecked(getSettingsValue(ONLINE_KEY));
		checkOnlineQuery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(ONLINE_KEY, true);
				} else {
					setSettingsValue(ONLINE_KEY, false);
				}
			}
		});
		
		CheckBox checkNetwork = (CheckBox) findViewById(R.id.checkNetwork);
		checkNetwork.setChecked(getSettingsValue(NETWORK_KEY));
		checkNetwork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(NETWORK_KEY, true);
				} else {
					setSettingsValue(NETWORK_KEY, false);
				}
			}
		});
		
		CheckBox checkDebug = (CheckBox) findViewById(R.id.checkDebug);
		checkDebug.setChecked(getSettingsValue(DEBUG_KEY));
		checkDebug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(DEBUG_KEY, true);
				} else {
					setSettingsValue(DEBUG_KEY, false);
				}
			}
		});
		
		CheckBox checkError = (CheckBox) findViewById(R.id.checkError);
		checkError.setChecked(getSettingsValue(ERROR_KEY));
		checkError.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(ERROR_KEY, true);
				} else {
					setSettingsValue(ERROR_KEY, false);
				}
			}
		});
		
		CheckBox checkFP = (CheckBox) findViewById(R.id.checkFP);
		checkFP.setChecked(getSettingsValue(FP_KEY));
		checkFP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(FP_KEY, true);
				} else {
					setSettingsValue(FP_KEY, false);
				}
			}
		});
		
		CheckBox checkMatchMode = (CheckBox) findViewById(R.id.checkMatchMode);
		checkMatchMode.setChecked(getSettingsValue(MODE_KEY));
		checkMatchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					setSettingsValue(MODE_KEY, true);
				} else {
					setSettingsValue(MODE_KEY, false);
				}
			}
		});
		
		Spinner spinnerAcrOptimizationMode = (Spinner) findViewById(R.id.spinnerACROptimize);
		spinnerAcrOptimizationMode.setSelection(getAcrOptimizationModeIndex());
		spinnerAcrOptimizationMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// Save the index of the selected optimization mode
				setAcrOptimizationModeIndex(parent.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing
			}
			
		});
		
	}
	
}
