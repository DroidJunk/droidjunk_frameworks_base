package com.android.systemui.junktoggles;


import com.android.systemui.R;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;




public class JunkBluetoothButton extends JunkToggleButton {

	private View showBluetooth;
	private View mIndicator;
	private ImageView mIcon;
	private View mDivider;
	private BroadcastReceiver mBroadcastReciver;
	private BluetoothAdapter mBluetoothAdaper;	


	public JunkBluetoothButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		mBluetoothAdaper = BluetoothAdapter.getDefaultAdapter();

	}




	protected void onAttachedToWindow(){
		super.onAttachedToWindow();

		showBluetooth = (View) getRootView().findViewById(R.id.button_3);
		mIndicator = (View) getRootView().findViewById(R.id.indicator_3);
		mIcon = (ImageView) getRootView().findViewById(R.id.bluetooth_icon);
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
		mDivider = (View) getRootView().findViewById(R.id.divider_3);	

	    final IntentFilter mFilter = new IntentFilter();
	    mFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
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
		getContext().unregisterReceiver(mBroadcastReciver);
	}


	@Override
	protected boolean getStatusOn(){
		return mBluetoothAdaper.isEnabled();
	}

	@Override
	void updateResources() {
		
		mIcon.clearColorFilter();

		if (mBluetoothAdaper.isEnabled()) {
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOnColor);
			mIcon.setImageResource(R.drawable.junktoggle_bluetooth_on);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
			setTextColor(JunkToggles.mToggleTextOnColor);

		} else {
			mIcon.setImageResource(R.drawable.junktoggle_bluetooth_off);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOffColor);
			setTextColor(JunkToggles.mToggleTextOffColor);
		}

		mDivider.setBackgroundColor(JunkToggles.mToggleDivColor);

		if (JunkToggleButton.mShowBluetooth) {
			showBluetooth.setVisibility(View.VISIBLE);
		} else {
			showBluetooth.setVisibility(View.GONE);
		}
		
	}



	@Override
	void toggleOn() {

		mBluetoothAdaper.enable();
		updateResources();
	}


	@Override
	void toggleOff() {

		mBluetoothAdaper.disable();
		updateResources();
	}


	@Override
	void showSettings() {

	    Intent i = new Intent();
	    i.setAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
	    getContext().sendBroadcast(i);
	    i.setAction("android.settings.BLUETOOTH_SETTINGS");
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
	}





}  // 
