/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.android.systemui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.BatteryManager;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Slog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;



/**
 * This widget display an analogic clock with two hands for hours and
 * minutes.
 */
public class TempLabel extends TextView {

	private final String Junk_Pulldown_Settings = "JUNK_PULLDOWN_SETTINGS";
	private final String SHOW_TEMP_LABEL = "show_temp_label";
	private final String TEMP_LABEL_COLOR = "temp_label_color";
	private final String TEMP_LABEL_SIZE = "temp_label_size";
    private boolean mAttached;
	private SharedPreferences mPrefs;
    private boolean mShowTemp = true;
    private int mTempColor = 0xff33b5e5;
    private int mTempSize = 14;
    private int temp = 0;
    

    

    public TempLabel(Context context) {
        this(context, null);
    }

    public TempLabel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempLabel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            
      		Context settingsContext = getContext();
			try {
				settingsContext = getContext().createPackageContext("com.android.settings",0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     		
			mPrefs = settingsContext.getSharedPreferences("Junk_Settings", Context.MODE_PRIVATE);
     		
     		mShowTemp = mPrefs.getBoolean(SHOW_TEMP_LABEL, true);
    		mTempColor = mPrefs.getInt(TEMP_LABEL_COLOR, 0xff33b5e5);
    		mTempSize = mPrefs.getInt(TEMP_LABEL_SIZE, 14);
    		
            updateTempLabel();
    		
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            filter.addAction(Junk_Pulldown_Settings);
            getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                updateTempLabel();        
               }
            
            if (action.equals(Junk_Pulldown_Settings)) {
             	mShowTemp = intent.getBooleanExtra(SHOW_TEMP_LABEL, mShowTemp);
            	mTempColor = intent.getIntExtra(TEMP_LABEL_COLOR, mTempColor);	
            	mTempSize = intent.getIntExtra(TEMP_LABEL_SIZE, mTempSize);
            	updateTempLabel();
            }
             
        }
    };


    
    
    void updateTempLabel(){
    	
        if (mShowTemp) {
        	setVisibility(View.VISIBLE);
        } else {
        	setVisibility(View.GONE);
        }
        
        setTextColor(mTempColor);
        setTextSize(mTempSize);
        
        float tempF = (((temp / 10) * 9f) / 5f) + 32f;
        setText(tempF + "º");

    }
    
    
}
