package com.android.systemui.junktoggles;

import com.android.systemui.R;
import com.android.systemui.junktoggles.JunkRotateButton.RotateModeObserver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.ServiceManager;
import android.provider.Settings;



public class JunkDataButton extends JunkToggleButton {

	private View showData;
	private View mIndicator;
	private ImageView mIcon;
	private WifiManager mWifiManager; 
	private ConnectivityManager mConnectivityManager; 
	Handler mHandler = new Handler();
	final DataModeObserver mDataModeObserver = new DataModeObserver(mHandler) ;




	// Data Mode observer
	class DataModeObserver extends ContentObserver{

		public DataModeObserver(Handler handler) {
			super(handler);
		}

	    @Override
	    public void onChange(boolean selfChange){

	    	updateResources();
	    }
	}	




	public JunkDataButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}





	protected void onAttachedToWindow(){
		super.onAttachedToWindow();

		showData = (View) getRootView().findViewById(R.id.button_9);
		mIndicator = (View) getRootView().findViewById(R.id.indicator_9);
		mIcon = (ImageView) getRootView().findViewById(R.id.data_icon);
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);


		getContext().getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.MOBILE_DATA), true,
                mDataModeObserver);
       

		updateResources();
	}


	protected void onDetachedFromWindow(){

		getContext().getContentResolver().unregisterContentObserver(mDataModeObserver);
	}


	@Override
	protected boolean getStatusOn(){

		return mConnectivityManager.getMobileDataEnabled();
	}

	@Override
	void updateResources() {
		
		mIcon.clearColorFilter();

		if (mConnectivityManager.getMobileDataEnabled()) {
			mIndicator.setBackgroundColor(JunkToggleViewTop.mToggleIndOnColor);
			mIcon.setImageResource(R.drawable.junktoggle_data_on);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
			setTextColor(JunkToggleViewTop.mToggleTextOnColor);

		} else {
			mIcon.setImageResource(R.drawable.junktoggle_data_off);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
			mIndicator.setBackgroundColor(JunkToggleViewTop.mToggleIndOffColor);
			setTextColor(JunkToggleViewTop.mToggleTextOffColor);
		}

		if (JunkToggleButton.mShowData) {
			showData.setVisibility(View.VISIBLE);
		} else {
			showData.setVisibility(View.GONE);
		}
		
	}


	@Override
	void toggleOn() {

		mConnectivityManager.setMobileDataEnabled(true);
		updateResources();

	}


	@Override
	void toggleOff() {


		mConnectivityManager.setMobileDataEnabled(false);
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
