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

package com.android.systemui.statusbar.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.View;
import android.widget.TextView;

/**
 * This widget display an analogic clock with two hands for hours and
 * minutes.
 */
public class CarrierLabel extends TextView {
	
	// Junk
	private final String Junk_Pulldown_Settings = "JUNK_PULLDOWN_SETTINGS";
	private final String SHOW_CARRIER = "show_carrier";
	private final String CARRIER_COLOR = "carrier_color";
	private final String CARRIER_CUSTOM = "carrier_custom";
	private final String CARRIER_CUSTOM_TEXT = "carrier_custom_text";
	private final String CARRIER_SIZE = "carrier_size";
	private SharedPreferences sp;
    private boolean mShowCarrier = true;
    private int mCarrierColor = 0xff33b5e5;
    private boolean mCustomCarrier = false;
    private String mCustomCarrierText = "- J  U  N  K -";
    private int mCarrierSize = 15;
    private String mDefaultCarrierText = "";	
	// End Junk

    private boolean mAttached;    

    

    public CarrierLabel(Context context) {
        this(context, null);
    }

    public CarrierLabel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarrierLabel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        updateNetworkName(false, null, false, null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;

            // Junk
      		Context settingsContext = getContext();
			try {
				settingsContext = getContext().createPackageContext("com.android.settings",0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     		
			sp = settingsContext.getSharedPreferences("Junk_Settings", Context.MODE_PRIVATE);

     		mShowCarrier = sp.getBoolean(SHOW_CARRIER, mShowCarrier);
    		mCarrierColor = sp.getInt(CARRIER_COLOR, mCarrierColor);
    		mCustomCarrier = sp.getBoolean(CARRIER_CUSTOM, mCustomCarrier);
    		mCustomCarrierText = sp.getString(CARRIER_CUSTOM_TEXT,  "- J  U  N  K -");
    		mCarrierSize = sp.getInt(CARRIER_SIZE, mCarrierSize);
    		
            updateCarrierLabel();
			// End Junk    		

            IntentFilter filter = new IntentFilter();
            filter.addAction(Telephony.Intents.SPN_STRINGS_UPDATED_ACTION);
			// Junk
            filter.addAction(Junk_Pulldown_Settings);
			// End Junk
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
            if (Telephony.Intents.SPN_STRINGS_UPDATED_ACTION.equals(action)) {
                updateNetworkName(intent.getBooleanExtra(Telephony.Intents.EXTRA_SHOW_SPN, false),
                        intent.getStringExtra(Telephony.Intents.EXTRA_SPN),
                        intent.getBooleanExtra(Telephony.Intents.EXTRA_SHOW_PLMN, false),
                        intent.getStringExtra(Telephony.Intents.EXTRA_PLMN));
            }
            // Junk
            if (action.equals(Junk_Pulldown_Settings)) {
             	mShowCarrier = intent.getBooleanExtra(SHOW_CARRIER, mShowCarrier);
            	mCustomCarrier = intent.getBooleanExtra(CARRIER_CUSTOM, mCustomCarrier);
            	mCarrierColor = intent.getIntExtra(CARRIER_COLOR, mCarrierColor);	
            	if (intent.getStringExtra(CARRIER_CUSTOM_TEXT) != null) mCustomCarrierText	= intent.getStringExtra(CARRIER_CUSTOM_TEXT);
            	mCarrierSize = intent.getIntExtra(CARRIER_SIZE, mCarrierSize);
            	updateCarrierLabel();
            }
            // End Junk 
        }
    };

    void updateNetworkName(boolean showSpn, String spn, boolean showPlmn, String plmn) {
        if (false) {
            Slog.d("CarrierLabel", "updateNetworkName showSpn=" + showSpn + " spn=" + spn
                    + " showPlmn=" + showPlmn + " plmn=" + plmn);
        }
        final String str;
        // match logic in KeyguardStatusViewManager
        final boolean plmnValid = showPlmn && !TextUtils.isEmpty(plmn);
        final boolean spnValid = showSpn && !TextUtils.isEmpty(spn);
        if (plmnValid && spnValid) {
            str = plmn + "|" + spn;
        } else if (plmnValid) {
            str = plmn;
        } else if (spnValid) {
            str = spn;
        } else {
            str = "";
        }
        // Junk
        mDefaultCarrierText = (String) getText();
        updateCarrierLabel();
        // End Junk
    }

    
    // Junk
    void updateCarrierLabel(){
    	
        if (mShowCarrier) {
        	setVisibility(View.VISIBLE);
        } else {
        	setVisibility(View.GONE);
        }

        setTextColor(mCarrierColor);
        
        if (mCustomCarrier) {
        	setText(mCustomCarrierText);
        } else {
        	setText(mDefaultCarrierText);
        }
        
        setTextSize(mCarrierSize);
    }
    // End Junk
    
    
}


