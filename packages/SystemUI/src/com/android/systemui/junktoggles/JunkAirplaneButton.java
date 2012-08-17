package com.android.systemui.junktoggles;


import com.android.systemui.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;




public class JunkAirplaneButton extends JunkToggleButton {

	private View showAirplane;
	private View mIndicator;
	private ImageView mIcon;
	private View mDivider;
	private BroadcastReceiver mBroadcastReciver;
	private boolean mState;


	public JunkAirplaneButton(Context context, AttributeSet attrs) {
		super(context, attrs);

	}




	protected void onAttachedToWindow(){
		super.onAttachedToWindow();

		showAirplane = (View) getRootView().findViewById(R.id.button_5);
		mIndicator = (View) getRootView().findViewById(R.id.indicator_5);
		mIcon = (ImageView) getRootView().findViewById(R.id.airplane_icon);	
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
		mDivider = (View) getRootView().findViewById(R.id.divider_5);	


	    final IntentFilter mFilter = new IntentFilter();
	    mFilter.addAction("android.intent.action.AIRPLANE_MODE");
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
		return (Settings.System.getInt(getContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1);
	}

	@Override
	void updateResources() {
		
		mIcon.clearColorFilter();

		if (Settings.System.getInt(getContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1) {
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOnColor);
			mIcon.setImageResource(R.drawable.junktoggle_airplane_on);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
			setTextColor(JunkToggles.mToggleTextOnColor);
			mState = true;

		} else {
			mIcon.setImageResource(R.drawable.junktoggle_airplane_off);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOffColor);
			setTextColor(JunkToggles.mToggleTextOffColor);
			mState = false;
		}

		mDivider.setBackgroundColor(JunkToggles.mToggleDivColor);
		
		if (JunkToggleButton.mShowAirplane) {
			showAirplane.setVisibility(View.VISIBLE);
		} else {
			showAirplane.setVisibility(View.GONE);
		}
		

	}



	@Override
	void toggleOn() {
		Settings.System.putInt(getContext().getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 1);
		updateResources();

	    Intent i = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
	    i.putExtra("state", mState);
	    getContext().sendBroadcast(i);
	}



	@Override
	void toggleOff() {

		Settings.System.putInt(getContext().getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0);
		updateResources();

	    Intent i = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
	    i.putExtra("state", mState);
	    getContext().sendBroadcast(i);
	}



	@Override
	void showSettings() {

	    Intent i = new Intent();
	    i.setAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
	    getContext().sendBroadcast(i);
	    i.setAction("android.settings.AIRPLANE_MODE_SETTINGS");
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
	}





}  // 
