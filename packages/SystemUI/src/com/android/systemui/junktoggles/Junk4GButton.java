package com.android.systemui.junktoggles;

import com.android.systemui.R;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.os.Handler;
import android.provider.Settings;







public class Junk4GButton extends JunkToggleButton {

	private final String Junk_Toggle_Settings = "JUNK_TOGGLE_SETTINGS";
	private View showFourg;
	private View mIndicator;
	private ImageView mIcon;
	private View mDivider;
	Handler mHandler = new Handler();
	final NetworkModeObserver mNetworkModeObserver = new NetworkModeObserver(mHandler) ;
	




	public Junk4GButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	// NetworkMode settings observer
	class NetworkModeObserver extends ContentObserver{

		public NetworkModeObserver(Handler handler) {
			super(handler);
		}

	    @Override
	    public void onChange(boolean selfChange){

	    	updateResources();
	    }
	}	



	protected void onAttachedToWindow(){
		super.onAttachedToWindow();

		showFourg = (View) getRootView().findViewById(R.id.button_0);
		mIndicator = (View) getRootView().findViewById(R.id.indicator_0);
		mIcon = (ImageView) getRootView().findViewById(R.id.fourg_icon);
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
		mDivider = (View) getRootView().findViewById(R.id.divider_0);

		getContext().getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.PREFERRED_NETWORK_MODE), true,
                mNetworkModeObserver);

		updateResources();
	}


	protected void onDetachedFromWindow(){

		getContext().getContentResolver().unregisterContentObserver(mNetworkModeObserver);
	}


	@Override
	protected boolean getStatusOn(){

		return (Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.PREFERRED_NETWORK_MODE, 4) == 7);

	}

	@Override
	void updateResources() {
		
		mIcon.clearColorFilter();

		if ((Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.PREFERRED_NETWORK_MODE, 4) == 7)) {
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOnColor);
			mIcon.setImageResource(R.drawable.junktoggle_fourg_on);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
			setTextColor(JunkToggles.mToggleTextOnColor);
		} else {
			mIcon.setImageResource(R.drawable.junktoggle_fourg_off);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOffColor);
			setTextColor(JunkToggles.mToggleTextOffColor);
		}
		
		mDivider.setBackgroundColor(JunkToggles.mToggleDivColor);

		if (JunkToggleButton.mShowFourg) {
			showFourg.setVisibility(View.VISIBLE);
		} else {
			showFourg.setVisibility(View.GONE);
		}

		
	}


	@Override
	void toggleOn() {

		Settings.Secure.putInt(getContext().getContentResolver(), Settings.Secure.PREFERRED_NETWORK_MODE, 7);

		Intent i = new Intent();
	    i.setAction(Junk_Toggle_Settings);
	    i.putExtra("NetworkMode",7);
	    getContext().sendBroadcast(i);

		updateResources();
	}


	@Override
	void toggleOff() {

		Settings.Secure.putInt(getContext().getContentResolver(), Settings.Secure.PREFERRED_NETWORK_MODE, 4);

		Intent i = new Intent();
	    i.setAction(Junk_Toggle_Settings);
	    i.putExtra("NetworkMode",4);
	    getContext().sendBroadcast(i);

		updateResources();
	}


	@Override
	void showSettings() {

	    Intent i = new Intent();
	    i.setAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
	    getContext().sendBroadcast(i);
	    i.setAction("android.settings.DATA_ROAMING_SETTINGS");
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
	}





}  //
