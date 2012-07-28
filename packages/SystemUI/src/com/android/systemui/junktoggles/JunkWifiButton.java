package com.android.systemui.junktoggles;

import com.android.systemui.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.net.wifi.WifiManager;


public class JunkWifiButton extends JunkToggleButton {

	private View showWifi;
	private View mIndicator;
	private ImageView mIcon;
	private View mDivider;
	private WifiManager mWifiManager; 
	private BroadcastReceiver mBroadcastReciver;



	public JunkWifiButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

	}




	protected void onAttachedToWindow(){
		super.onAttachedToWindow();

		showWifi = (View) getRootView().findViewById(R.id.button_1);
		mIndicator = (View) getRootView().findViewById(R.id.indicator_1);
		mIcon = (ImageView) getRootView().findViewById(R.id.wifi_icon);	
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
		mDivider = (View) getRootView().findViewById(R.id.divider_1);

	    final IntentFilter mFilter = new IntentFilter();
	    mFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
	    mBroadcastReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            	updateResources();
            }
        };

        getContext().registerReceiver(mBroadcastReciver, mFilter);
		updateResources();
	}


	protected void onDetachedFromWindow(){
		mContext.unregisterReceiver(mBroadcastReciver);
	}


	@Override
	protected boolean getStatusOn(){
		return mWifiManager.isWifiEnabled();
	}

	@Override
	void updateResources() {

		mIcon.clearColorFilter();
		
		if (mWifiManager.isWifiEnabled()) {
			mIndicator.setBackgroundColor(JunkToggleViewTop.mToggleIndOnColor);
			mIcon.setImageResource(R.drawable.junktoggle_wifi_on);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
			setTextColor(JunkToggleViewTop.mToggleTextOnColor);

		} else {
			mIcon.setImageResource(R.drawable.junktoggle_wifi_off);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
			mIndicator.setBackgroundColor(JunkToggleViewTop.mToggleIndOffColor);
			setTextColor(JunkToggleViewTop.mToggleTextOffColor);
		}
		
		if (JunkToggleButton.mShowWifi) {
			showWifi.setVisibility(View.VISIBLE);
		} else {
			showWifi.setVisibility(View.GONE);
		}

		mDivider.setBackgroundColor(JunkToggleViewTop.mToggleDivColor);
	}



	@Override
	void toggleOn() {

		mWifiManager.setWifiEnabled(true);
		updateResources();
	}


	@Override
	void toggleOff() {

		mWifiManager.setWifiEnabled(false);
		updateResources();
	}


	@Override
	void showSettings() {

	    Intent i = new Intent();
	    i.setAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
	    mContext.sendBroadcast(i);
	    i.setAction("android.settings.WIFI_SETTINGS");
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
	}





}  // 
