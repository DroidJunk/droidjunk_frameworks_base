/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.systemui.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.systemui.statusbar.policy.NetworkController;

import com.android.systemui.R;

import droidjunk.colorfitermaker.ColorFilterMaker;

// Intimately tied to the design of res/layout/signal_cluster_view.xml
public class SignalClusterView
        extends LinearLayout
        implements NetworkController.SignalCluster {

    static final boolean DEBUG = false;
    static final String TAG = "SignalClusterView";
    
    // Junk
    private final String Junk_Icon_Settings = "JUNK_ICON_SETTINGS";
	private final String ICON_COLOR = "icon_color";
    private SharedPreferences mPrefs;
    private int mIconColor = 0xff3fa2c7;
    // End Junk
    
    NetworkController mNC;

    private boolean mWifiVisible = false;
    private int mWifiStrengthId = 0,mWifiStrengthColorId, mWifiActivityId = 0;
    private boolean mMobileVisible = false;
    private int mMobileStrengthId = 0, mMobileStrengthColorId = 0, mMobileActivityId = 0, mMobileTypeId = 0;
    private boolean mIsAirplaneMode = false;
    private int mAirplaneIconId = 0;
    private String mWifiDescription, mMobileDescription, mMobileTypeDescription;

    ViewGroup mWifiGroup, mMobileGroup;
    // Junk - added color items
    ImageView mWifi, mWifiColor, mMobile, mMobileColor, mWifiActivity, mMobileActivity, mMobileType, mAirplane;
    View mSpacer;

    public SignalClusterView(Context context) {
        this(context, null);
    }

    public SignalClusterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignalClusterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setNetworkController(NetworkController nc) {
        if (DEBUG) Slog.d(TAG, "NetworkController=" + nc);
        mNC = nc;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mWifiGroup      = (ViewGroup) findViewById(R.id.wifi_combo);
        mWifi           = (ImageView) findViewById(R.id.wifi_signal);
        // Junk
        mWifiColor      = (ImageView) findViewById(R.id.wifi_signal_color);
        // End Junk
        mWifiActivity   = (ImageView) findViewById(R.id.wifi_inout);
        mMobileGroup    = (ViewGroup) findViewById(R.id.mobile_combo);
        mMobile         = (ImageView) findViewById(R.id.mobile_signal);
        // Junk
        mMobileColor         = (ImageView) findViewById(R.id.mobile_signal_color);
        // End Junk
        mMobileActivity = (ImageView) findViewById(R.id.mobile_inout);
        mMobileType     = (ImageView) findViewById(R.id.mobile_type);
        mSpacer         =             findViewById(R.id.spacer);
        mAirplane       = (ImageView) findViewById(R.id.airplane);


        // Junk
  		Context settingsContext = getContext();
		try {
			settingsContext = getContext().createPackageContext("com.android.settings",0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     		
		mPrefs = settingsContext.getSharedPreferences("Junk_Settings", Context.MODE_PRIVATE);
   		mIconColor = mPrefs.getInt(ICON_COLOR, 0xff3fa2c7);
   		
        IntentFilter filter = new IntentFilter();
        filter.addAction(Junk_Icon_Settings);
        getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
        // End Junk
        
        apply();
    }

    @Override
    protected void onDetachedFromWindow() {
        mWifiGroup      = null;
        mWifi           = null;
        mWifiActivity   = null;
        mMobileGroup    = null;
        mMobile         = null;
        mMobileActivity = null;
        mMobileType     = null;
        mSpacer         = null;
        mAirplane       = null;

        super.onDetachedFromWindow();
    }

    // Junk
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            if (action.equals(Junk_Icon_Settings)) {
            	mIconColor= intent.getIntExtra(ICON_COLOR, mIconColor);
            	apply();
            }
        }
    };     
    // End Junk
    
    // Junk - added color items
    @Override
    public void setWifiIndicators(boolean visible, int strengthIcon, int strengthIconColor, int activityIcon,
            String contentDescription) {
        mWifiVisible = visible;
        mWifiStrengthId = strengthIcon;
        mWifiStrengthColorId = strengthIconColor;
        mWifiActivityId = activityIcon;
        mWifiDescription = contentDescription;

        apply();
    }

    // Junk - added color items
    @Override
    public void setMobileDataIndicators(boolean visible, int strengthIcon, int strengthIconColor, int activityIcon,
            int typeIcon, String contentDescription, String typeContentDescription) {
        mMobileVisible = visible;
        mMobileStrengthId = strengthIcon;
        mMobileStrengthColorId = strengthIconColor;
        mMobileActivityId = activityIcon;
        mMobileTypeId = typeIcon;
        mMobileDescription = contentDescription;
        mMobileTypeDescription = typeContentDescription;

        apply();
    }

    @Override
    public void setIsAirplaneMode(boolean is, int airplaneIconId) {
        mIsAirplaneMode = is;
        mAirplaneIconId = airplaneIconId;

        apply();
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        // Standard group layout onPopulateAccessibilityEvent() implementations
        // ignore content description, so populate manually
        if (mWifiVisible && mWifiGroup.getContentDescription() != null)
            event.getText().add(mWifiGroup.getContentDescription());
        if (mMobileVisible && mMobileGroup.getContentDescription() != null)
            event.getText().add(mMobileGroup.getContentDescription());
        return super.dispatchPopulateAccessibilityEvent(event);
    }

    // Run after each indicator change.
    private void apply() {
        if (mWifiGroup == null) return;
        
        // Junk - added color items
        if (mWifiVisible) {
            mWifiGroup.setVisibility(View.VISIBLE);
            mWifi.setImageResource(mWifiStrengthId);
            mWifiColor.setImageResource(mWifiStrengthColorId);
            mWifiActivity.setImageResource(mWifiActivityId);
            mWifiGroup.setContentDescription(mWifiDescription);
           	mWifiColor.setVisibility(View.VISIBLE);
           	mWifiColor.setColorFilter(ColorFilterMaker.changeColor(mIconColor, .6f));
        } else {
            mWifiGroup.setVisibility(View.GONE);
        }

        if (DEBUG) Slog.d(TAG,
                String.format("wifi: %s sig=%d act=%d",
                    (mWifiVisible ? "VISIBLE" : "GONE"),
                    mWifiStrengthId, mWifiActivityId));
        
        // Junk - added color items
        if (mMobileVisible && !mIsAirplaneMode) {
            mMobileGroup.setVisibility(View.VISIBLE);
            mMobile.setImageResource(mMobileStrengthId);
            mMobileColor.setImageResource(mMobileStrengthColorId);
            mMobileActivity.setImageResource(mMobileActivityId);
            mMobileType.setImageResource(mMobileTypeId);
            mMobileGroup.setContentDescription(mMobileTypeDescription + " " + mMobileDescription);
           	mMobileColor.setVisibility(View.VISIBLE);
           	mMobileColor.setColorFilter(ColorFilterMaker.changeColor(mIconColor, .6f));
        } else {
            mMobileGroup.setVisibility(View.GONE);
        }

        if (mIsAirplaneMode) {
            mAirplane.setVisibility(View.VISIBLE);
            mAirplane.setImageResource(mAirplaneIconId);
        } else {
            mAirplane.setVisibility(View.GONE);
        }

        if (mMobileVisible && mWifiVisible && mIsAirplaneMode) {
            mSpacer.setVisibility(View.INVISIBLE);
        } else {
            mSpacer.setVisibility(View.GONE);
        }

        if (DEBUG) Slog.d(TAG,
                String.format("mobile: %s sig=%d act=%d typ=%d",
                    (mMobileVisible ? "VISIBLE" : "GONE"),
                    mMobileStrengthId, mMobileActivityId, mMobileTypeId));

        mMobileType.setVisibility(
                !mWifiVisible ? View.VISIBLE : View.GONE);
    }
}

