/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;


public class JunkQuietTimeService {

    private Context myContext;
    private NotificationManager nManager;
    private Notification notification;
    PendingIntent contentIntent;
    
    // Junk
    private final String Junk_QuietTime_Settings = "JUNK_QUIET_TIME_SETTINGS";
    private final String Junk_QuietTime_Settings_Daily = "JUNK_QUIET_TIME_SETTINGS_daily";
    private final String Junk_QuietTime_Settings_Sun = "JUNK_QUIET_TIME_SETTINGS_sun";
    private final String Junk_QuietTime_Settings_Mon = "JUNK_QUIET_TIME_SETTINGS_mon";
    private final String Junk_QuietTime_Settings_Tue = "JUNK_QUIET_TIME_SETTINGS_tue";
    private final String Junk_QuietTime_Settings_Wed = "JUNK_QUIET_TIME_SETTINGS_wed";
    private final String Junk_QuietTime_Settings_Thur = "JUNK_QUIET_TIME_SETTINGS_thur";
    private final String Junk_QuietTime_Settings_Fri = "JUNK_QUIET_TIME_SETTINGS_fri";
    private final String Junk_QuietTime_Settings_Sat = "JUNK_QUIET_TIME_SETTINGS_sat";
    private final String Junk_QuietTime_Settings_Changed = "JUNK_QUIET_TIME_SETTINGS_CHANGED";
	private final String QUIET_TIME = "quiet_time_on";
	private final String QUIET_TIME_NOTIF = "quiet_time_notif";
	private final String START_HOUR = "qt_start_hour";
	private final String START_MIN = "qt_start_min";
	private final String STOP_HOUR = "qt_stop_hour";
	private final String STOP_MIN = "qt_stop_min";
	private final String NOTIF_LED_ON = "qt_led_on";
	private final String NOTIF_SOUND_ON = "qt_sound_on";
	private final String NOTIF_VIBRATE_ON = "qt_vibrate_on";
	private final String QT_DAILY = "_daily";
	private final String QT_SUN = "_sun";
	private final String QT_MON = "_mon";
	private final String QT_TUE = "_tue";
	private final String QT_WED = "_wed";
	private final String QT_THUR = "_thur";
	private final String QT_FRI = "_fri";
	private final String QT_SAT = "_sat";
	
    private SharedPreferences sp;
	
    private boolean mQTOnOff = false;

    private boolean dailyQuietTime, sunQuietTime, monQuietTime, tueQuietTime = false;
    private boolean wedQuietTime, thurQuietTime, friQuietTime, satQuietTime = false;
    private int dailyStartHour, sunStartHour, monStartHour, tueStartHour  = 0;
    private int wedStartHour, thurStartHour, friStartHour, satStartHour  = 0;
    private int dailyStartMin, sunStartMin, monStartMin, tueStartMin  = 0;
    private int wedStartMin, thurStartMin, friStartMin, satStartMin  = 0;
    private int dailyStopHour, sunStopHour, monStopHour, tueStopHour  = 0;
    private int wedStopHour, thurStopHour, friStopHour, satStopHour  = 0;
    private int dailyStopMin, sunStopMin, monStopMin, tueStopMin  = 0;
    private int wedStopMin, thurStopMin, friStopMin, satStopMin  = 0;
    private boolean dailyTurnOffLed, sunTurnOffLed, monTurnOffLed, tueTurnOffLed = false;
    private boolean wedTurnOffLed, thurTurnOffLed, friTurnOffLed, satTurnOffLed = false;
    private boolean dailyTurnOffSound, sunTurnOffSound, monTurnOffSound, tueTurnOffSound = false;
    private boolean wedTurnOffSound, thurTurnOffSound, friTurnOffSound, satTurnOffSound = false;
    private boolean dailyTurnOffVibrate, sunTurnOffVibrate, monTurnOffVibrate, tueTurnOffVibrate = false;
    private boolean wedTurnOffVibrate, thurTurnOffVibrate, friTurnOffVibrate, satTurnOffVibrate = false;
    private boolean QuietTime, TurnOffLed, TurnOffSound, TurnOffVibrate = false;
    private int StartHour, StartMin, StopHour, StopMin = 0;
	private boolean withinQuietTime = false;
    private boolean mUseNotif = false;
    private String notifStart, notifStop, notifDayString;    
    private String notifText, minPad;
    private int oldDay = -1;
    private int currentDay = 0;
	int nowMins = 0;
	int startMins = 0;
	int stopMins = 0;
	



    public JunkQuietTimeService(Context context) {
    	myContext = context;
    }
    
    public void systemReady() {
    	
  		Context settingsContext = myContext;
		try {
			settingsContext = myContext.createPackageContext("com.android.settings",0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		sp = settingsContext.getSharedPreferences("Junk_Settings", Context.MODE_PRIVATE);
    	
    	IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(Junk_QuietTime_Settings);
        filter.addAction(Junk_QuietTime_Settings_Daily);
        filter.addAction(Junk_QuietTime_Settings_Sun);
        filter.addAction(Junk_QuietTime_Settings_Mon);
        filter.addAction(Junk_QuietTime_Settings_Tue);
        filter.addAction(Junk_QuietTime_Settings_Wed);
        filter.addAction(Junk_QuietTime_Settings_Thur);
        filter.addAction(Junk_QuietTime_Settings_Fri);
        filter.addAction(Junk_QuietTime_Settings_Sat);
        myContext.registerReceiver(mIntentReceiver, filter);
        nManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);
   	 	notification = new Notification();
        Intent notificationIntent = new Intent();
   	 	notificationIntent.setAction("android.settings.junk.CUSTOM_QUIETTIME_SETTINGS");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   	 	contentIntent = PendingIntent.getActivity(myContext, (int)
   	 			System.currentTimeMillis(), notificationIntent, 
   	 			PendingIntent.FLAG_UPDATE_CURRENT);
   	 	notification.flags = Notification.FLAG_ONGOING_EVENT; 
   	 	notification.icon = com.android.internal.R.drawable.junk_quiettime_on_no;
   	 	
       	getQuietTimeSettings();
       	withinQuietTime = inQuietTime();
       	setupNotifStrings();
    	setupNotification(mUseNotif);
    	sendNewSettingsIntent(withinQuietTime);
    }
    
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
            	withinQuietTime = inQuietTime();
            	setupNotification(mQTOnOff);
            }
            if (action.equals(Intent.ACTION_DATE_CHANGED)) {
            	withinQuietTime = inQuietTime();
            	setupNotifStrings();
            	setupNotification(mQTOnOff);
            }
            if (action.equals(Junk_QuietTime_Settings)) {
                mQTOnOff = intent.getBooleanExtra(QUIET_TIME, mQTOnOff);  
                mUseNotif = intent.getBooleanExtra(QUIET_TIME_NOTIF, mUseNotif);  
            	withinQuietTime = inQuietTime();
            	setupNotifStrings();
            	setupNotification(mQTOnOff);
            }
            if (action.equals(Junk_QuietTime_Settings_Daily)) {
            	dailyQuietTime = intent.getBooleanExtra(QUIET_TIME, dailyQuietTime); 
            	dailyStartHour = intent.getIntExtra(START_HOUR, dailyStartHour);
            	dailyStartMin = intent.getIntExtra(START_MIN, dailyStartMin);
            	dailyStopHour = intent.getIntExtra(STOP_HOUR, dailyStopHour);
            	dailyStopMin = intent.getIntExtra(STOP_MIN, dailyStopMin);
            	dailyTurnOffLed = intent.getBooleanExtra(NOTIF_LED_ON, dailyTurnOffLed);
            	dailyTurnOffSound = intent.getBooleanExtra(NOTIF_SOUND_ON, dailyTurnOffSound);
            	dailyTurnOffVibrate = intent.getBooleanExtra(NOTIF_VIBRATE_ON, dailyTurnOffVibrate);
            	withinQuietTime = inQuietTime();
            	setupNotifStrings();
            	setupNotification(mQTOnOff);
            }
            if (action.equals(Junk_QuietTime_Settings_Sun)) {
            	sunQuietTime = intent.getBooleanExtra(QUIET_TIME, sunQuietTime); 
            	sunStartHour = intent.getIntExtra(START_HOUR, sunStartHour);
            	sunStartMin = intent.getIntExtra(START_MIN, sunStartMin);
            	sunStopHour = intent.getIntExtra(STOP_HOUR, sunStopHour);
            	sunStopMin = intent.getIntExtra(STOP_MIN, sunStopMin);
            	sunTurnOffLed = intent.getBooleanExtra(NOTIF_LED_ON, sunTurnOffLed);
            	sunTurnOffSound = intent.getBooleanExtra(NOTIF_SOUND_ON, sunTurnOffSound);
            	sunTurnOffVibrate = intent.getBooleanExtra(NOTIF_VIBRATE_ON, sunTurnOffVibrate);
            	withinQuietTime = inQuietTime();
            	setupNotifStrings();
            	setupNotification(mQTOnOff);
            }
            if (action.equals(Junk_QuietTime_Settings_Mon)) {
            	monQuietTime = intent.getBooleanExtra(QUIET_TIME, monQuietTime); 
            	monStartHour = intent.getIntExtra(START_HOUR, monStartHour);
            	monStartMin = intent.getIntExtra(START_MIN, monStartMin);
            	monStopHour = intent.getIntExtra(STOP_HOUR, monStopHour);
            	monStopMin = intent.getIntExtra(STOP_MIN, monStopMin);
            	monTurnOffLed = intent.getBooleanExtra(NOTIF_LED_ON, monTurnOffLed);
            	monTurnOffSound = intent.getBooleanExtra(NOTIF_SOUND_ON, monTurnOffSound);
            	monTurnOffVibrate = intent.getBooleanExtra(NOTIF_VIBRATE_ON, monTurnOffVibrate);
            	withinQuietTime = inQuietTime();
            	setupNotifStrings();
            	setupNotification(mQTOnOff);
            }
            if (action.equals(Junk_QuietTime_Settings_Tue)) {
            	tueQuietTime = intent.getBooleanExtra(QUIET_TIME, tueQuietTime); 
            	tueStartHour = intent.getIntExtra(START_HOUR, tueStartHour);
            	tueStartMin = intent.getIntExtra(START_MIN, tueStartMin);
            	tueStopHour = intent.getIntExtra(STOP_HOUR, tueStopHour);
            	tueStopMin = intent.getIntExtra(STOP_MIN, tueStopMin);
            	tueTurnOffLed = intent.getBooleanExtra(NOTIF_LED_ON, tueTurnOffLed);
            	tueTurnOffSound = intent.getBooleanExtra(NOTIF_SOUND_ON, tueTurnOffSound);
            	tueTurnOffVibrate = intent.getBooleanExtra(NOTIF_VIBRATE_ON, tueTurnOffVibrate);
            	withinQuietTime = inQuietTime();
            	setupNotifStrings();
            	setupNotification(mQTOnOff);
            }
            if (action.equals(Junk_QuietTime_Settings_Wed)) {
            	wedQuietTime = intent.getBooleanExtra(QUIET_TIME, wedQuietTime); 
            	wedStartHour = intent.getIntExtra(START_HOUR, wedStartHour);
            	wedStartMin = intent.getIntExtra(START_MIN, wedStartMin);
            	wedStopHour = intent.getIntExtra(STOP_HOUR, wedStopHour);
            	wedStopMin = intent.getIntExtra(STOP_MIN, wedStopMin);
            	wedTurnOffLed = intent.getBooleanExtra(NOTIF_LED_ON, wedTurnOffLed);
            	wedTurnOffSound = intent.getBooleanExtra(NOTIF_SOUND_ON, wedTurnOffSound);
            	wedTurnOffVibrate = intent.getBooleanExtra(NOTIF_VIBRATE_ON, wedTurnOffVibrate);
            	withinQuietTime = inQuietTime();
            	setupNotifStrings();
            	setupNotification(mQTOnOff);
            }
            if (action.equals(Junk_QuietTime_Settings_Thur)) {
            	thurQuietTime = intent.getBooleanExtra(QUIET_TIME, thurQuietTime); 
            	thurStartHour = intent.getIntExtra(START_HOUR, thurStartHour);
            	thurStartMin = intent.getIntExtra(START_MIN, thurStartMin);
            	thurStopHour = intent.getIntExtra(STOP_HOUR, thurStopHour);
            	thurStopMin = intent.getIntExtra(STOP_MIN, thurStopMin);
            	thurTurnOffLed = intent.getBooleanExtra(NOTIF_LED_ON, thurTurnOffLed);
            	thurTurnOffSound = intent.getBooleanExtra(NOTIF_SOUND_ON, thurTurnOffSound);
            	thurTurnOffVibrate = intent.getBooleanExtra(NOTIF_VIBRATE_ON, thurTurnOffVibrate);
            	withinQuietTime = inQuietTime();
            	setupNotifStrings();
            	setupNotification(mQTOnOff);
            }
            if (action.equals(Junk_QuietTime_Settings_Fri)) {
            	friQuietTime = intent.getBooleanExtra(QUIET_TIME, friQuietTime); 
            	friStartHour = intent.getIntExtra(START_HOUR, friStartHour);
            	friStartMin = intent.getIntExtra(START_MIN, friStartMin);
            	friStopHour = intent.getIntExtra(STOP_HOUR, friStopHour);
            	friStopMin = intent.getIntExtra(STOP_MIN, friStopMin);
            	friTurnOffLed = intent.getBooleanExtra(NOTIF_LED_ON, friTurnOffLed);
            	friTurnOffSound = intent.getBooleanExtra(NOTIF_SOUND_ON, friTurnOffSound);
            	friTurnOffVibrate = intent.getBooleanExtra(NOTIF_VIBRATE_ON, friTurnOffVibrate);
            	withinQuietTime = inQuietTime();
            	setupNotifStrings();
            	setupNotification(mQTOnOff);
            }
            if (action.equals(Junk_QuietTime_Settings_Sat)) {
            	satQuietTime = intent.getBooleanExtra(QUIET_TIME, satQuietTime); 
            	satStartHour = intent.getIntExtra(START_HOUR, satStartHour);
            	satStartMin = intent.getIntExtra(START_MIN, satStartMin);
            	satStopHour = intent.getIntExtra(STOP_HOUR, satStopHour);
            	satStopMin = intent.getIntExtra(STOP_MIN, satStopMin);
            	satTurnOffLed = intent.getBooleanExtra(NOTIF_LED_ON, satTurnOffLed);
            	satTurnOffSound = intent.getBooleanExtra(NOTIF_SOUND_ON, satTurnOffSound);
            	satTurnOffVibrate = intent.getBooleanExtra(NOTIF_VIBRATE_ON, satTurnOffVibrate);
            	withinQuietTime = inQuietTime();
            	setupNotifStrings();
            	setupNotification(mQTOnOff);
            }

        }    
    };


    
    // Are we within the Quiet Time hours    
    private boolean inQuietTime() {
    	if (!mQTOnOff) return false;
    	Calendar calendar = Calendar.getInstance();
    	currentDay = calendar.get(Calendar.DAY_OF_WEEK);

        getDayValues();
    	if (oldDay != currentDay) oldDay = currentDay;
    	if (!QuietTime) return false;
    	
        nowMins = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        startMins = StartHour * 60 + StartMin;
        stopMins = StopHour * 60 + StopMin;    	
        if (stopMins < startMins) {
        	return (nowMins >= startMins) || (nowMins < stopMins);
        } else {
        	return (nowMins >= startMins) && (nowMins < stopMins);
        }     		
    }
    

    // Get the settings    
    private void getQuietTimeSettings(){
        mQTOnOff = sp.getBoolean(QUIET_TIME, mQTOnOff);
        mUseNotif = sp.getBoolean(QUIET_TIME_NOTIF, mUseNotif);
       
        dailyQuietTime = sp.getBoolean(QUIET_TIME + QT_DAILY, dailyQuietTime);
        dailyStartHour = sp.getInt(START_HOUR + QT_DAILY, dailyStartHour);
        dailyStartMin = sp.getInt(START_MIN + QT_DAILY, dailyStartMin);
        dailyStopHour = sp.getInt(STOP_HOUR + QT_DAILY, dailyStopHour);
        dailyStopMin = sp.getInt(STOP_MIN + QT_DAILY, dailyStopMin);
        dailyTurnOffLed = sp.getBoolean(NOTIF_LED_ON + QT_DAILY, dailyTurnOffLed);
        dailyTurnOffSound = sp.getBoolean(NOTIF_SOUND_ON + QT_DAILY, dailyTurnOffSound);
        dailyTurnOffVibrate = sp.getBoolean(NOTIF_VIBRATE_ON + QT_DAILY, dailyTurnOffVibrate);

        sunQuietTime = sp.getBoolean(QUIET_TIME + QT_SUN, sunQuietTime);
        sunStartHour = sp.getInt(START_HOUR + QT_SUN, sunStartHour);
        sunStartMin = sp.getInt(START_MIN + QT_SUN, sunStartMin);
        sunStopHour = sp.getInt(STOP_HOUR + QT_SUN, sunStopHour);
        sunStopMin = sp.getInt(STOP_MIN + QT_SUN, sunStopMin);
        sunTurnOffLed = sp.getBoolean(NOTIF_LED_ON + QT_SUN, sunTurnOffLed);
        sunTurnOffSound = sp.getBoolean(NOTIF_SOUND_ON + QT_SUN, sunTurnOffSound);
        sunTurnOffVibrate = sp.getBoolean(NOTIF_VIBRATE_ON + QT_SUN, sunTurnOffVibrate);
        
        monQuietTime = sp.getBoolean(QUIET_TIME + QT_MON, monQuietTime);
        monStartHour = sp.getInt(START_HOUR + QT_MON, monStartHour);
        monStartMin = sp.getInt(START_MIN + QT_MON, monStartMin);
        monStopHour = sp.getInt(STOP_HOUR + QT_MON, monStopHour);
        monStopMin = sp.getInt(STOP_MIN + QT_MON, monStopMin);
        monTurnOffLed = sp.getBoolean(NOTIF_LED_ON + QT_MON, monTurnOffLed);
        monTurnOffSound = sp.getBoolean(NOTIF_SOUND_ON + QT_MON, monTurnOffSound);
        monTurnOffVibrate = sp.getBoolean(NOTIF_VIBRATE_ON + QT_MON, monTurnOffVibrate);
        
        tueQuietTime = sp.getBoolean(QUIET_TIME + QT_TUE, tueQuietTime);
        tueStartHour = sp.getInt(START_HOUR + QT_TUE, tueStartHour);
        tueStartMin = sp.getInt(START_MIN + QT_TUE, tueStartMin);
        tueStopHour = sp.getInt(STOP_HOUR + QT_TUE, tueStopHour);
        tueStopMin = sp.getInt(STOP_MIN + QT_TUE, tueStopMin);
        tueTurnOffLed = sp.getBoolean(NOTIF_LED_ON + QT_TUE, tueTurnOffLed);
        tueTurnOffSound = sp.getBoolean(NOTIF_SOUND_ON + QT_TUE, tueTurnOffSound);
        tueTurnOffVibrate = sp.getBoolean(NOTIF_VIBRATE_ON + QT_TUE, tueTurnOffVibrate);
        
        wedQuietTime = sp.getBoolean(QUIET_TIME + QT_WED, wedQuietTime);
        wedStartHour = sp.getInt(START_HOUR + QT_WED, wedStartHour);
        wedStartMin = sp.getInt(START_MIN + QT_WED, wedStartMin);
        wedStopHour = sp.getInt(STOP_HOUR + QT_WED, wedStopHour);
        wedStopMin = sp.getInt(STOP_MIN + QT_WED, wedStopMin);
        wedTurnOffLed = sp.getBoolean(NOTIF_LED_ON + QT_WED, wedTurnOffLed);
        wedTurnOffSound = sp.getBoolean(NOTIF_SOUND_ON + QT_WED, wedTurnOffSound);
        wedTurnOffVibrate = sp.getBoolean(NOTIF_VIBRATE_ON + QT_WED, wedTurnOffVibrate);
        
        thurQuietTime = sp.getBoolean(QUIET_TIME + QT_THUR, thurQuietTime);
        thurStartHour = sp.getInt(START_HOUR + QT_THUR, thurStartHour);
        thurStartMin = sp.getInt(START_MIN + QT_THUR, thurStartMin);
        thurStopHour = sp.getInt(STOP_HOUR + QT_THUR, thurStopHour);
        thurStopMin = sp.getInt(STOP_MIN + QT_THUR, thurStopMin);
        thurTurnOffLed = sp.getBoolean(NOTIF_LED_ON + QT_THUR, thurTurnOffLed);
        thurTurnOffSound = sp.getBoolean(NOTIF_SOUND_ON + QT_THUR, thurTurnOffSound);
        thurTurnOffVibrate = sp.getBoolean(NOTIF_VIBRATE_ON + QT_THUR, thurTurnOffVibrate);
        
        friQuietTime = sp.getBoolean(QUIET_TIME + QT_FRI, friQuietTime);
        friStartHour = sp.getInt(START_HOUR + QT_FRI, friStartHour);
        friStartMin = sp.getInt(START_MIN + QT_FRI, friStartMin);
        friStopHour = sp.getInt(STOP_HOUR + QT_FRI, friStopHour);
        friStopMin = sp.getInt(STOP_MIN + QT_FRI, friStopMin);
        friTurnOffLed = sp.getBoolean(NOTIF_LED_ON + QT_FRI, friTurnOffLed);
        friTurnOffSound = sp.getBoolean(NOTIF_SOUND_ON + QT_FRI, friTurnOffSound);
        friTurnOffVibrate = sp.getBoolean(NOTIF_VIBRATE_ON + QT_FRI, friTurnOffVibrate);
        
        satQuietTime = sp.getBoolean(QUIET_TIME + QT_SAT, satQuietTime);
        satStartHour = sp.getInt(START_HOUR + QT_SAT, satStartHour);
        satStartMin = sp.getInt(START_MIN + QT_SAT, satStartMin);
        satStopHour = sp.getInt(STOP_HOUR + QT_SAT, satStopHour);
        satStopMin = sp.getInt(STOP_MIN + QT_SAT, satStopMin);
        satTurnOffLed = sp.getBoolean(NOTIF_LED_ON + QT_SAT, satTurnOffLed);
        satTurnOffSound = sp.getBoolean(NOTIF_SOUND_ON + QT_SAT, satTurnOffSound);
        satTurnOffVibrate = sp.getBoolean(NOTIF_VIBRATE_ON + QT_SAT, satTurnOffVibrate);

    }

    private void getDayValues() {
    	Calendar calendar = Calendar.getInstance();
    	int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        if (currentDay == 1) {
        	QuietTime = sunQuietTime;
        	StartHour = sunStartHour;
        	StartMin = sunStartMin;
        	StopHour = sunStopHour;
        	StopMin = sunStopMin;
        	TurnOffLed = sunTurnOffLed;
        	TurnOffSound = sunTurnOffSound;
        	TurnOffVibrate = sunTurnOffVibrate;
        	notifDayString = "Sunday";
        } else if (currentDay == 2) {
        	QuietTime = monQuietTime;
        	StartHour = monStartHour;
        	StartMin = monStartMin;
        	StopHour = monStopHour;
        	StopMin = monStopMin;
        	TurnOffLed = monTurnOffLed;
        	TurnOffSound = monTurnOffSound;
        	TurnOffVibrate = monTurnOffVibrate;
        	notifDayString = "Monday";
        } else if (currentDay == 3) {
        	QuietTime = tueQuietTime;
        	StartHour = tueStartHour;
        	StartMin = tueStartMin;
        	StopHour = tueStopHour;
        	StopMin = tueStopMin;
        	TurnOffLed = tueTurnOffLed;
        	TurnOffSound = tueTurnOffSound;
        	TurnOffVibrate = tueTurnOffVibrate;
        	notifDayString = "Tuesday";
        } else if (currentDay == 4) {
        	QuietTime = wedQuietTime;
        	StartHour = wedStartHour;
        	StartMin = wedStartMin;
        	StopHour = wedStopHour;
        	StopMin = wedStopMin;
        	TurnOffLed = wedTurnOffLed;
        	TurnOffSound = wedTurnOffSound;
        	TurnOffVibrate = wedTurnOffVibrate;
        	notifDayString = "Wednesday";
        } else if (currentDay == 5) {
        	QuietTime = thurQuietTime;
        	StartHour = thurStartHour;
        	StartMin = thurStartMin;
        	StopHour = thurStopHour;
        	StopMin = thurStopMin;
        	TurnOffLed = thurTurnOffLed;
        	TurnOffSound = thurTurnOffSound;
        	TurnOffVibrate = thurTurnOffVibrate;
        	notifDayString = "Thursday";
        } else if (currentDay == 6) {
        	QuietTime = friQuietTime;
        	StartHour = friStartHour;
        	StartMin = friStartMin;
        	StopHour = friStopHour;
        	StopMin = friStopMin;
        	TurnOffLed = friTurnOffLed;
        	TurnOffSound = friTurnOffSound;
        	TurnOffVibrate = friTurnOffVibrate;
        	notifDayString = "Friday";
        } else if (currentDay == 7) {
        	QuietTime = satQuietTime;
        	StartHour = satStartHour;
        	StartMin = satStartMin;
        	StopHour = satStopHour;
        	StopMin = satStopMin;
        	TurnOffLed = satTurnOffLed;
        	TurnOffSound = satTurnOffSound;
        	TurnOffVibrate = satTurnOffVibrate;
        	notifDayString = "Saturday";
        } ;
        
        if (!QuietTime) {
        	QuietTime = dailyQuietTime;
        	StartHour = dailyStartHour;
        	StartMin = dailyStartMin;
        	StopHour = dailyStopHour;
        	StopMin = dailyStopMin;
        	TurnOffLed = dailyTurnOffLed;
        	TurnOffSound = dailyTurnOffSound;
        	TurnOffVibrate = dailyTurnOffVibrate;
        	notifDayString = "Daily";
        }
    } 

    private void setupNotifStrings() {
        minPad = "";
      	if (StopMin < 10) minPad = "0";
        if (StopHour > 12) {
           	notifStop = String.valueOf(StopHour - 12) + ":" + minPad + String.valueOf(StopMin) + "pm";;
        } else {
           	notifStop = String.valueOf(StopHour) + ":" + minPad + String.valueOf(StopMin) + "am";;
        }
        minPad = "";
      	if (StartMin < 10) minPad = "0";
        if (StartHour > 12) {
           	notifStart = String.valueOf(StartHour - 12) + ":" + minPad + String.valueOf(StartMin) + "pm";
        } else {
           	notifStart = String.valueOf(StartHour) + ":" + minPad + String.valueOf(StartMin) + "am";;
        }
    	notifText = notifDayString + " * " + notifStart + " - " + notifStop;
    }

    
    private void setupNotification(boolean isOn) {
   		if (isOn) {
       		if (withinQuietTime) {
           		if (mUseNotif) {
           			notification.icon = com.android.internal.R.drawable.junk_quiettime_on_yes;
           			notification.when = System.currentTimeMillis();
           			notification.setLatestEventInfo(myContext, "Quiet Time - Within Time", notifText, contentIntent);
           			nManager.notify(com.android.internal.R.string.quiet_time, notification);
           		} else {
           			nManager.cancel(com.android.internal.R.string.quiet_time);
           		}
           		sendNewSettingsIntent(true);
       		} else {
       			
       			notification.icon = com.android.internal.R.drawable.junk_quiettime_on_no;
       			notification.when = System.currentTimeMillis();
       			notification.setLatestEventInfo(myContext, "Quiet Time", notifText, contentIntent);
           		if (mUseNotif) {
           			nManager.notify(com.android.internal.R.string.quiet_time, notification);
           		} else {
           			nManager.cancel(com.android.internal.R.string.quiet_time);
           		}
           		sendNewSettingsIntent(false);
       		}

   		} else {
       		nManager.cancel(com.android.internal.R.string.quiet_time);
       		sendNewSettingsIntent(false);
    	}
    }
    

    private void sendNewSettingsIntent(boolean qtOn) {
     	Intent i = new Intent();
     	i.setAction(Junk_QuietTime_Settings_Changed);
     	i.putExtra("QuietTimeOn", qtOn);
     	i.putExtra("QuietTimeLedOn", TurnOffLed);
     	i.putExtra("QuietTimeSoundOn", TurnOffSound);
     	i.putExtra("QuietTimeVibrateOn", TurnOffVibrate);
     	myContext.sendBroadcast(i);
     	i = null;
    }
    
}
