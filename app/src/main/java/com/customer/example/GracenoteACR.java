package com.customer.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.customer.example.R;
import com.gracenote.gnsdk.*;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
//import com.microsoft.band.sdk.sampleapp.streaming.R;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.SampleRate;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GracenoteACR extends Activity
{
	/**
	 * Constants
	 */	
	private static final int 		ACR_OPTIMIZATION_REQUEST_CODE 	= 1000;
	
	/*
	 * TODO Protect your Gracenote client and license information
	 */
	private static final String		UNSECURED_CLIENT_ID = "3634176";
	private static final String		UNSECURED_CLIENT_TAG = "2259084EE1A969DE65AEE54947FED8FB";
	private static final String		UNSECURED_LICENSE = "-- BEGIN LICENSE v1.0 172B6B97 --\\r\\nname: \\r\\nnotes: Gracenote Open Developer Program\\r\\nstart_date: 0000-00-00\\r\\nclient_id: 3634176\\r\\nmusicid_file: enabled\\r\\nmusicid_text: enabled\\r\\nmusicid_stream: enabled\\r\\nmusicid_cd: enabled\\r\\nplaylist: enabled\\r\\nvideoid: enabled\\r\\nvideo_explore: enabled\\r\\nlocal_images: enabled\\r\\nlocal_mood: enabled\\r\\nvideoid_explore: enabled\\r\\nacr: enabled\\r\\nepg: enabled\\r\\n-- SIGNATURE 172B6B97 --\\r\\nlAADAgAeqheusSMqrDtQ+GJg6zJsn3RlLed04ay4ggWAgN1ZAB8BWWYrgvFOCm+c/AhFpBUttXsEa9MaEIQzs8PPHBUM\\r\\n-- END LICENSE 172B6B97 --\\r\\n";
	
	/**
	 * Global variables
	 */	
	private volatile boolean 	isListening 	= false;	
	private GnAcrAudio 			gnAcrAudio 		= null;
	private GnUser				gnUser 			= null;
	private GnAcrMatch			gnAcrMatch		= null;

	private String Username = null;

	private int MatchCount = 0;
	private String MatchTitle = null;
	private String MatchShow = null;
	private String CurrentTitle = null;
	private String CurrentShow = null;

	private Map<Long, Integer> heart_map = new HashMap<Long, Integer>();
	private Long startTime = System.currentTimeMillis();

	private BandClient client = null;
	private Button btnStart;
	private TextView txtStatus;

	/**
	 * Private classes
	 */	
	private class GnAcrEvents implements IGnAcrEvents
	{		
		@Override
		public void statusEvent(GnAcrStatus acrStatus)
		{
			String statusMessage = null;
			boolean isStatusShown = true;
			
			switch (acrStatus.statusType()) {
			case kAcrStatusAudioSilent:
				statusMessage = String.format("Silence %.2f", acrStatus.value());
				isStatusShown = showAcrStatus(Settings.SILENCE_KEY);
				break;
			case kAcrStatusSilenceRatio:
				statusMessage = String.format("Silence ratio %.2f", acrStatus.value());
				isStatusShown = showAcrStatus(Settings.RATIO_KEY);
				break;
			case kAcrStatusTransition:
				statusMessage = "Transition detected...";
				isStatusShown = showAcrStatus(Settings.TRANSITION_KEY);
				break;
			case kAcrStatusAudioFpStarted:
				statusMessage = "Fingerprint started...";
				isStatusShown = showAcrStatus(Settings.FP_KEY);
				break;
			case kAcrStatusAudioFpGenerated:
				statusMessage = "Fingerprint generated...";
				isStatusShown = showAcrStatus(Settings.FP_KEY);
				break;
			case kAcrStatusQueryCompleteLocal:
				statusMessage = "Local lookup complete...";
				isStatusShown = showAcrStatus(Settings.LOCAL_KEY);
				break;
			case kAcrStatusQueryBegin:
				statusMessage = "Query begin...";
				isStatusShown = showAcrStatus(Settings.NETWORK_KEY);
				break;
			case kAcrStatusConnecting:
				statusMessage = "Connecting...";
				isStatusShown = showAcrStatus(Settings.NETWORK_KEY);
				break;
			case kAcrStatusSending:
				statusMessage = "Sending...";
				isStatusShown = showAcrStatus(Settings.NETWORK_KEY);
				break;
			case kAcrStatusReceiving:
				statusMessage = "Receiving...";
				isStatusShown = showAcrStatus(Settings.NETWORK_KEY);
				break;
			case kAcrStatusQueryCompleteOnline:
				statusMessage = "Online lookup complete...";
				isStatusShown = showAcrStatus(Settings.ONLINE_KEY);
				break;
			case kAcrStatusMusic:
				statusMessage = String.format("Music %.2f", acrStatus.value());
				isStatusShown = showAcrStatus(Settings.NSM_KEY);
				break;
			case kAcrStatusNonPitched:
				statusMessage = "Non pitched...";
				isStatusShown = showAcrStatus(Settings.NSM_KEY);
				break;
			case kAcrStatusSpeech:
				statusMessage = String.format("Speech %.2f", acrStatus.value());
				isStatusShown = showAcrStatus(Settings.NSM_KEY);
				break;
			case kAcrStatusNormalMatchMode:
				statusMessage = "Normal match mode...";
				isStatusShown = showAcrStatus(Settings.MODE_KEY);
				break;
			case kAcrStatusNoMatchMode:
				statusMessage = "No match mode...";
				isStatusShown = showAcrStatus(Settings.MODE_KEY);
				break;
			case kAcrStatusError:
				statusMessage = String.format("Error: %s (0x%x)", acrStatus.error().errorDescription(), acrStatus.error().errorCode());
				isStatusShown = showAcrStatus(Settings.ERROR_KEY);
				break;
			case kAcrStatusDebug:
			default:
				statusMessage = String.format("Debug: %s", acrStatus.message());
				isStatusShown = showAcrStatus(Settings.DEBUG_KEY);
				break;
			}
			
			if (isStatusShown & statusMessage != null & statusMessage.length() > 0) 
			{
				updateStatusView(statusMessage, false);
			}			
		}

		private Long heartScore() {
			Long heart_beats = 0L;
			for (Map.Entry<Long, Integer> entry : heart_map.entrySet()) {
				//key is milliseconds, heartrate is bpm
				heart_beats += entry.getKey()/1000/3600 * entry.getValue();
			}
			return heart_beats;
		}

		private void mediaTransition() {
			MatchCount = 0;
			MatchShow = null;
			MatchTitle = null;
			updateResultView("RESET", true);
			//TODO send the heart rate, show, title, duration to the server
			// send startTime-now
			// calc Heart stuff
			// send show, title
			Long now = System.currentTimeMillis();

			heart_map.clear();
			startTime = now;

		}

		@Override
		public void resultEvent(GnResponseAcrMatch responseAcrMatch, GnAcrMatchSourceType acrMatchSourceType)
		{
			try 
			{
				if (responseAcrMatch.resultCount() == 0) {
					//updateResultView("ACR: No match", false);
					mediaTransition();
				}
				else
				{
					GnAcrMatchIterator acrMatchIterator = responseAcrMatch.acrMatches().getIterator();
					int matchCounter = 0;
					
					while (acrMatchIterator.hasNext()) 
					{
						GnAcrMatch acrMatch = acrMatchIterator.next();
						matchCounter++;
	
						// Remember the first match for use with a secondary query
						if (matchCounter == 1)
						{
							gnAcrMatch = acrMatch;
						}
						
						// Get formatted match position
						String matchPosition = formatMatchPosition(acrMatch.matchPosition());
						
						// Get title
						String officialTitle = acrMatch.officialTitle().display();
						
						// Get TV airings if available
						GnTVAiring tvAiring = acrMatch.tvAiring();
						if (!tvAiring.isNull())
						{
							GnTitle subtitle = acrMatch.subtitle();
							GnTVChannel tvChannel = tvAiring.tvChannel();

							if (CurrentTitle == null) {
								CurrentTitle = officialTitle;
							}
							if (CurrentShow == null) {
								CurrentShow = subtitle.display();
							}

							if (MatchTitle == null) {
								MatchTitle = CurrentTitle;
							}
							if (MatchShow == null) {
								MatchShow = CurrentShow;
							}

							if (MatchTitle == CurrentTitle && MatchShow == CurrentShow) {
								MatchCount += 1;
								updateResultView(String.format("Hit %d: %s: %s", MatchCount, MatchTitle, MatchShow), false);
                            } else {
								mediaTransition();
							}

							if (MatchCount >= 3) {
								updateResultView(String.format("Show: %s: %s", MatchTitle, MatchShow), false);
							}
						}
					}
				}
			} catch (GnException e) {
				updateStatusView(String.format("Error: %s", e.getLocalizedMessage()), false);
				e.printStackTrace();
			}
		}
		
		private boolean showAcrStatus(String prefKey)
		{		
			SharedPreferences prefs=getSharedPreferences(Settings.SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
			return prefs.getBoolean(prefKey, true);
		}
		
		private String formatMatchPosition(long position) 
		{
			long secPosition = position / 1000; 

			long hour = secPosition / 3600;
			long min = (secPosition / 60) % 60;
			long sec = secPosition % 60;

			return String.format("%02d:%02d:%02d", hour, min, sec);
		}	
	}
	
	private class GnBundleSource implements IGnBundleSource
	{
		private InputStream inputStream;
		
		public GnBundleSource(File bundleFile) throws FileNotFoundException
		{
			inputStream = new FileInputStream(bundleFile);
		}
		
		@Override
		public long getBundleData(ByteBuffer dataBuffer, long dataSize, IGnCancellable canceller)
		{
			if (canceller.isCancelled())
				return 0;
			
			byte[] data = new byte[(int)dataSize];
			long bytesRead = 0;

			try 
			{
				// Read bytes from the input stream.
				bytesRead = inputStream.read(data, 0, (int)dataSize);
				
				// Put bytes read into the data buffer. Use put() in case buffer is not backed by arrays.
				dataBuffer.put(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Bytes read is -1 when the end of the file has been reached; reset to 0
			if (bytesRead == -1)
				bytesRead = 0;			
			
			// Must return bytes read to caller
			return bytesRead;
		}
		
	}

	private class TextViewUpdator implements Runnable {
		
		private final static String LOG_TAG = "EntourageSDK";
		private final static int TEXT_VIEW_MAX_LINES = 50;
		public final static int CALLBACK_TYPE_STATUS = 0;
		public final static int CALLBACK_TYPE_RESULT = 1;
		
		private String message;
		private boolean clearMessage;
		private int callbackType;

		private TextViewUpdator(String message, boolean clearMessage, int callbackType) {
			this.message = message;
			this.clearMessage = clearMessage;
			this.callbackType = callbackType;
		}	
		
		@Override
		public void run() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ", Locale.US);
			TextView textView = null;
			
			textView = (TextView) findViewById(R.id.txtResult);

			if (clearMessage) {
				textView.setText(dateFormat.format(new Date()) + this.message);
			} 
			else if (callbackType == CALLBACK_TYPE_RESULT){
				CharSequence text = textView.getText();			
				int numLines = textView.getLineCount();
								
				// Remove excess lines
				if (numLines > TEXT_VIEW_MAX_LINES) {
					int lineEnd = textView.getLayout().getLineEnd(TEXT_VIEW_MAX_LINES);
					text = text.subSequence(0, lineEnd);
				}

				StringBuilder sb = new StringBuilder();
				sb.append(dateFormat.format(new Date()));
				sb.append(this.message);
				sb.append(System.getProperty("line.separator"));
				sb.append(text);
							
				textView.setText(sb.toString());				
			}
			Log.i(LOG_TAG, this.message);
		}
	}	
	
	/**
	 * Public methods
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);


		createUI();
		initializeSDK();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (client != null) {
			try {
				client.getSensorManager().unregisterAccelerometerEventListeners();
				client.getSensorManager().unregisterHeartRateEventListeners();
			} catch (BandIOException e) {
				appendToUI(e.getMessage());
			}
		}
	}

	private class appTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (getConnectedBandClient()) {
					appendToUI("Band is connected.\n");
					client.getSensorManager().requestHeartRateConsent(GracenoteACR.this, consentListener);
					client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS128);

				} else {
					appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
				}
			} catch (BandException e) {
				String exceptionMessage="";
				switch (e.getErrorType()) {
					case UNSUPPORTED_SDK_VERSION_ERROR:
						exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
						break;
					case SERVICE_ERROR:
						exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
						break;
					default:
						exceptionMessage = "Unknown error occured: " + e.getMessage();
						break;
				}
				appendToUI(exceptionMessage);

			} catch (Exception e) {
				appendToUI(e.getMessage());
			}
			return null;
		}
	}

	private void appendToUI(final String string) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText(string);
			}
		});
	}

	private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
		@Override
		public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
			if (event != null) {
				//appendToUI(String.format(" X = %.3f \n Y = %.3f\n Z = %.3f", event.getAccelerationX(),
				//		event.getAccelerationY(), event.getAccelerationZ()));
				Integer a = 1;
			}
		}
	};

	private HeartRateConsentListener consentListener = new HeartRateConsentListener() {
		@Override
		public void userAccepted(boolean consentGiven) {
// handle user's heart rate consent decision
			if (consentGiven) {
				startHRListener();
			} else {
				appendToUI(String.valueOf(consentGiven));
			}
		}
	};

	private BandHeartRateEventListener heartRateListener = new BandHeartRateEventListener() {
		@Override
		public void onBandHeartRateChanged(BandHeartRateEvent event) {
			if (event != null) {
				Long t = System.currentTimeMillis();
				heart_map.put(t, event.getHeartRate());
				//can remove the appendToUI. Just to debug...
				appendToUI(Long.toString(t) + " " + Integer.toString(heart_map.get(t)));
			}
		}
	};

	private boolean getConnectedBandClient() throws InterruptedException, BandException {
		if (client == null) {
			BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
			if (devices.length == 0) {
				appendToUI("Band isn't paired with your phone.\n");
				return false;
			}
			client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
		} else if (ConnectionState.CONNECTED == client.getConnectionState()) {
			return true;
		}

		appendToUI("Band is connecting...\n");
		return ConnectionState.CONNECTED == client.connect().await();
	}
	public void startHRListener() {
		try {
			// register HR sensor event listener
			client.getSensorManager().registerHeartRateEventListener(heartRateListener);
		} catch (BandIOException ex) {
			appendToUI(ex.getMessage());
		} catch (BandException e) {
			String exceptionMessage="";
			switch (e.getErrorType()) {
				case UNSUPPORTED_SDK_VERSION_ERROR:
					exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
					break;
				case SERVICE_ERROR:
					exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
					break;
				default:
					exceptionMessage = "Unknown error occurred: " + e.getMessage();
					break;
			}
			appendToUI(exceptionMessage);

		} catch (Exception e) {
			appendToUI(e.getMessage());
		}
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		isListening = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (event.getAction() == KeyEvent.ACTION_DOWN) 
		{
			switch(keyCode) 
			{
				case KeyEvent.KEYCODE_BACK:
					this.finish();
					return true;					
				default:
					return false;
			}
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == ACR_OPTIMIZATION_REQUEST_CODE && resultCode == RESULT_OK)
		{
			if (gnAcrAudio != null)
			{
				try 
				{
					GnAcrOptions acrOptions = gnAcrAudio.options();
					acrOptions.optimizationType(getAcrOptimizationType());
					
					updateStatusView(String.format("Optimization Type: %s", acrOptions.optimizationType().name()), false);
					
				} catch (GnException e) 
				{
					updateStatusView(String.format("Error: %s", e.getLocalizedMessage()), false);
					e.printStackTrace();
				}
			}
		}
	}	
	
	
	/**
	 * initializeSDK
	 */
	private void initializeSDK() 
	{	
		try {
		
			// Initialize SDK manager. License enables both video and music.
			GnManager gnManager = new GnManager(
										getApplicationContext(), 
										UNSECURED_LICENSE, 
										GnLicenseInputMode.kLicenseInputModeString);
			
			// Initialize user store to store some app info
			GnUserStore gnUserStore = new GnUserStore(getApplicationContext());
			
			// Create user for video
			gnUser = new GnUser(
							gnUserStore, 
							UNSECURED_CLIENT_ID, 
							UNSECURED_CLIENT_TAG, 
							"1.0");
			
			// Set group default locale to retrieve language-specific and locale-dependent metadata
			AsyncTask<Void, Void, Void> loadLocaleAsyncTask = new AsyncTask<Void, Void, Void>(){

				@Override
				protected Void doInBackground(Void... params) {
					try {
						GnLocale locale = new GnLocale(
								GnLocaleGroup.kLocaleGroupAcr, 
								GnLanguage.kLanguageEnglish, 
								GnRegion.kRegionUS, 
								GnDescriptor.kDescriptorDefault, 
								gnUser);
						locale.setGroupDefault();
						Log.i("EntourageSDK", "Locale loaded");
						
					} catch (GnException e) {
						e.printStackTrace();
					}			
					
					return null;
				}
				
			};
			loadLocaleAsyncTask.execute();
			
		} catch (GnException e) {
			updateStatusView("ERROR: " + e.getLocalizedMessage(), false);
			enableButtons(false);
			e.printStackTrace();
		}
	}
	
	private void startOrStopListening() 
	{		
		if (!isListening) 
		{
			((Button) findViewById(R.id.btnAcr)).setText("Stop ACR");

			updateStatusView("ACR Version: " + GnManager.productVersion(), true);		
											
			// Generally, process audio in a separate thread
			Thread audioProcessThread = new Thread(new Runnable()
			{					
				@Override
				public void run()
				{
					GnMic gnMicrophone = null;
					try
					{
						// Initialize mic
						gnMicrophone = new GnMic(44100, 16, 1);
						if (gnMicrophone.sourceInit() != 0)
						{
							updateStatusView("Failed to acquire mic", false);
						}
						else
						{
							gnAcrAudio = new GnAcrAudio(gnUser, new GnAcrEvents());

							// Enable acr options - e.g. receiving external IDs in the acr response result
							gnAcrAudio.options().externalId(true);				

							// Initialize acr with the same audio config as the microphone.
							gnAcrAudio.audioProcessStart(
									gnMicrophone.samplesPerSecond(), 
									gnMicrophone.sampleSizeInBits() == 16 ? GnAcrAudioSampleFormat.kAcrAudioSampleFormatPcm16 : GnAcrAudioSampleFormat.kAcrAudioSampleFormatPcm8, 
									gnMicrophone.numberOfChannels(), 
                                    GnAcrAudioFPQueryMode.kAcrAudioFPQueryAutomatic);
							
							ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024*4);
							long bytesRead = 0;
							
							// Read audio data from mic and feed it to acr
							isListening = true;				
							while(isListening)
							{
								bytesRead = gnMicrophone.getData(byteBuffer, byteBuffer.capacity());								
								if (bytesRead <= 0)
								{
									updateStatusView("Failed to read audio from mic", false);
									break;
								}
								else
								{
									gnAcrAudio.audioProcess(byteBuffer, bytesRead);									
								}	
							}	
						}
					}
					catch (GnException e)
					{
						e.printStackTrace();
					}
					finally
					{											
						isListening = false;						

						if (gnMicrophone != null)
						{
							// Stop microphone. The object can no longer be used after the call to sourceClose.
							gnMicrophone.sourceClose();
							gnMicrophone = null;
						}
						
						if (gnAcrAudio != null)
						{
							try {
								// Stop ACR
								gnAcrAudio.audioProcessStop();
								
								// Delete the native ACR resources. The object can no longer be used after the call to delete.
								gnAcrAudio.delete();	
								gnAcrAudio = null;
	
							} catch (GnException e) {
								e.printStackTrace();
							}
						}							
					}						
					
					updateStatusView("Listening stopped", false);
					
					final Button btnAcr = ((Button) findViewById(R.id.btnAcr));
					if (btnAcr != null)
					{
						btnAcr.post(new Runnable() {
							
							@Override
							public void run() {
								btnAcr.setEnabled(true);
								btnAcr.setText("Start ACR");
							}
						});
					}
				}
			});
			audioProcessThread.start();			
		}
		else
		{				
			isListening = false;	
			findViewById(R.id.btnAcr).setEnabled(false);
		}
	}
	
	private void videoLookup() 
	{
		if (!isListening) {
			updateStatusView("Error: Requested video lookup while ACR is not running.", false);
			return;
		}

		try 
		{
			gnAcrAudio.videoLookup();
			updateStatusView("Video lookup requested", false);
		} catch (GnException e) 
		{
			updateStatusView(String.format("Error: %s", e.getLocalizedMessage()), false);
			e.printStackTrace();
		}
	}
	
	private GnAcrOptimizationType getAcrOptimizationType()
	{
		SharedPreferences prefs = getSharedPreferences(Settings.SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		int index = prefs.getInt(Settings.ACR_OPTIMIZATION_MODE_KEY, 0);	

		String[] optimizationModes = getResources().getStringArray(R.array.acr_optimization_mode);
		String selectedOptimizationMode = optimizationModes[index];
		
		if (selectedOptimizationMode.equals("Optimize for accuracy"))
		{
			return GnAcrOptimizationType.kAcrOptimizationTypeAccuracy;
		}
		if (selectedOptimizationMode.equals("Optimize for speed"))
		{
			return GnAcrOptimizationType.kAcrOptimizationTypeSpeed;
		}
		if (selectedOptimizationMode.equals("Optimize for adaptive"))
		{
			return GnAcrOptimizationType.kAcrOptimizationTypeAdaptive;
		}
		
		return GnAcrOptimizationType.kAcrOptimizationTypeDefault;
	}	
	
	private void enableButtons(boolean isEnabled)
	{
		findViewById(R.id.btnAcr).setEnabled(isEnabled);
	}

	private void createUI() 
	{
		setContentView(R.layout.landing);

		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setContentView(R.layout.main);
				TextView t = (TextView) findViewById(R.id.textView);
				t.setText(String.format("hi %s", Username));
				findViewById(R.id.btnAcr).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// Start or stop ACR
						startOrStopListening();
						txtStatus.setText("");
						new appTask().execute();
					}
				});

				txtStatus = (TextView) findViewById(R.id.msText);
			}
		});
	}


	private void updateStatusView(String message, boolean clearMessage) 
	{
		if (message == null || message.length() <= 0) {
			return;
		}
		TextViewUpdator textViewUpdator = new TextViewUpdator(message, clearMessage, TextViewUpdator.CALLBACK_TYPE_STATUS);
		this.runOnUiThread(textViewUpdator);
	}
	
	private void updateResultView(String message, boolean clearMessage) 
	{
		if (message == null || message.length() <= 0) {
			return;
		}
		TextViewUpdator textViewUpdator = new TextViewUpdator(message, clearMessage, TextViewUpdator.CALLBACK_TYPE_RESULT);
		this.runOnUiThread(textViewUpdator);
	}
}
