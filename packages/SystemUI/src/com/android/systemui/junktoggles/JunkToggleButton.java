package com.android.systemui.junktoggles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


public abstract class JunkToggleButton extends TextView {

	private final String Junk_Toggle_Settings = "JUNK_TOGGLE_SETTINGS";
	private final String TOGGLE_ICON_ON_COLOR = "toggles_icon_on_color";
	private final String TOGGLE_ICON_INTER_COLOR = "toggles_icon_inter_color";
	private final String TOGGLE_ICON_OFF_COLOR = "toggles_icon_off_color";
	private final String TOGGLES_TORCH_ON = "toggles_show_torch";
	private final String TOGGLES_4G_ON = "toggles_show_fourg";
	private final String TOGGLES_WIFI_ON = "toggles_show_wifi";
	private final String TOGGLES_GPS_ON = "toggles_show_gps";
	private final String TOGGLES_BLUETOOTH_ON = "toggles_show_bluetooth";
	private final String TOGGLES_NFC_ON = "toggles_show_nfc";
	private final String TOGGLES_SOUND_ON = "toggles_show_sound";
	private final String TOGGLES_AIRPLANE_ON = "toggles_show_airplane";
	private final String TOGGLES_BRIGHTNESS_ON = "toggles_show_brightness";
	private final String TOGGLES_ROTATE_ON = "toggles_show_rotate";
	private final String TOGGLES_SYNC_ON = "toggles_show_sync";
	private final String TOGGLES_DATA_ON = "toggles_show_data";
	private final String TOGGLES_UPDATE = "toggles_update";
	private SharedPreferences mPrefs;
	public static boolean mShowTorch = true;
	public static boolean mShowFourg = true;
	public static boolean mShowWifi = true;
	public static boolean mShowGps = true;
	public static boolean mShowBluetooth = true;
	public static boolean mShowNfc = true;
	public static boolean mShowSound = true;
	public static boolean mShowAirplane = true;
	public static boolean mShowBrightness = true;
	public static boolean mShowRotate = true;
	public static boolean mShowSync = true;
	public static boolean mShowData = true;
	public static int mToggleIconOnColor = 0xff3792b4;
	public static int mToggleIconInterColor = 0xffff0000;
	public static int mToggleIconOffColor = 0xff555555;

	


	public JunkToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Junk_Toggle_Settings);
        getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());

        
  		Context settingsContext = getContext();
		try {
			settingsContext = getContext().createPackageContext("com.android.settings",0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
     		
		mPrefs = settingsContext.getSharedPreferences("Junk_Settings", Context.MODE_PRIVATE);
		
		mShowTorch = mPrefs.getBoolean(TOGGLES_TORCH_ON, mShowTorch);
		mShowFourg = mPrefs.getBoolean(TOGGLES_4G_ON, mShowFourg);
		mShowWifi = mPrefs.getBoolean(TOGGLES_WIFI_ON, mShowWifi);
		mShowGps = mPrefs.getBoolean(TOGGLES_GPS_ON, mShowGps);
		mShowBluetooth = mPrefs.getBoolean(TOGGLES_BLUETOOTH_ON, mShowBluetooth);
		mShowNfc = mPrefs.getBoolean(TOGGLES_NFC_ON, mShowNfc);
		mShowSound = mPrefs.getBoolean(TOGGLES_SOUND_ON, mShowSound);
		mShowAirplane = mPrefs.getBoolean(TOGGLES_AIRPLANE_ON, mShowAirplane);
		mShowBrightness = mPrefs.getBoolean(TOGGLES_BRIGHTNESS_ON, mShowBrightness);
		mShowRotate = mPrefs.getBoolean(TOGGLES_ROTATE_ON, mShowRotate);
		mShowSync = mPrefs.getBoolean(TOGGLES_SYNC_ON, mShowSync);
		mShowData = mPrefs.getBoolean(TOGGLES_DATA_ON, mShowData);
   		mToggleIconOnColor = mPrefs.getInt(TOGGLE_ICON_ON_COLOR, mToggleIconOnColor);
   		mToggleIconInterColor = mPrefs.getInt(TOGGLE_ICON_INTER_COLOR, mToggleIconOnColor);
   		mToggleIconOffColor = mPrefs.getInt(TOGGLE_ICON_OFF_COLOR, mToggleIconOffColor);
		

   		
		setOnClickListener(new OnClickListener() { 
			public void onClick (View v){

				if (getStatusOn()) {
					toggleOff();
				} else {
					toggleOn();
				}
			}
		}
		);


		setOnLongClickListener(new OnLongClickListener() { 
			public boolean onLongClick (View v){
				showSettings();
				return true;
			}
		}
		);

	}


    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int oldColor = 0;
            
            if (action.equals(Junk_Toggle_Settings)) {
            	
            	oldColor = mToggleIconOnColor;
                mToggleIconOnColor = intent.getIntExtra(TOGGLE_ICON_ON_COLOR, mToggleIconOnColor);
            	if (oldColor != mToggleIconOnColor) updateResources();
            	oldColor = mToggleIconInterColor;
                mToggleIconInterColor = intent.getIntExtra(TOGGLE_ICON_INTER_COLOR, mToggleIconInterColor);
            	if (oldColor != mToggleIconInterColor) updateResources();
            	oldColor = mToggleIconOffColor;
                mToggleIconOffColor = intent.getIntExtra(TOGGLE_ICON_OFF_COLOR, mToggleIconOffColor);
            	if (oldColor != mToggleIconOffColor) updateResources();
            	
            	if (intent.getBooleanExtra(TOGGLES_UPDATE, false) == true) {
            		updateResources();
            	}
            	
            	if (intent.getBooleanExtra(TOGGLES_TORCH_ON, mShowTorch) == false) {
            		mShowTorch = false;
            		updateResources();
            	} else {
            		mShowTorch = true;
            		updateResources();
            	}
            	if (intent.getBooleanExtra(TOGGLES_4G_ON, mShowFourg) == false) {
            		mShowFourg = false;
            		updateResources();
            	} else {
            		mShowFourg = true;
            		updateResources();
            	}
            	if (intent.getBooleanExtra(TOGGLES_WIFI_ON, mShowWifi) == false) {
            		mShowWifi = false;
            		updateResources();
            	} else {
            		mShowWifi = true;
            		updateResources();
            	}            	
            	if (intent.getBooleanExtra(TOGGLES_GPS_ON, mShowGps) == false) {
            		mShowGps = false;
            		updateResources();
            	} else {
            		mShowGps = true;
            		updateResources();
            	}            	
            	if (intent.getBooleanExtra(TOGGLES_BLUETOOTH_ON, mShowBluetooth) == false) {
            		mShowBluetooth = false;
            		updateResources();
            	} else {
            		mShowBluetooth = true;
            		updateResources();
            	}            	
            	if (intent.getBooleanExtra(TOGGLES_NFC_ON, mShowNfc) == false) {
            		mShowNfc = false;
            		updateResources();
            	} else {
            		mShowNfc = true;
            		updateResources();
            	}            	
            	if (intent.getBooleanExtra(TOGGLES_SOUND_ON, mShowSound) == false) {
            		mShowSound = false;
            		updateResources();
            	} else {
            		mShowSound = true;
            		updateResources();
            	}
            	if (intent.getBooleanExtra(TOGGLES_AIRPLANE_ON, mShowAirplane) == false) {
            		mShowAirplane = false;
            		updateResources();
            	} else {
            		mShowAirplane = true;
            		updateResources();
            	}
            	if (intent.getBooleanExtra(TOGGLES_BRIGHTNESS_ON, mShowBrightness) == false) {
            		mShowBrightness = false;
            		updateResources();
            	} else {
            		mShowBrightness = true;
            		updateResources();
            	}            	
            	if (intent.getBooleanExtra(TOGGLES_ROTATE_ON, mShowRotate) == false) {
            		mShowRotate = false;
            		updateResources();
            	} else {
            		mShowRotate = true;
            		updateResources();
            	}            	
            	if (intent.getBooleanExtra(TOGGLES_SYNC_ON, mShowSync) == false) {
            		mShowSync = false;
            		updateResources();
            	} else {
            		mShowSync = true;
            		updateResources();
            	}            	
            	if (intent.getBooleanExtra(TOGGLES_DATA_ON, mShowData) == false) {
            		mShowData = false;
            		updateResources();
            	} else {
            		mShowData = true;
            		updateResources();
            	}            	
             	
            	
            }
        }
    };    

    
	abstract void updateResources();
	abstract void toggleOn();
	abstract void toggleOff();
	abstract boolean getStatusOn();
	abstract void showSettings();    
    

} //



