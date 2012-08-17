package com.android.systemui.junktoggles;


import com.android.systemui.R;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;




public class JunkGpsButton extends JunkToggleButton {

	private View showGps;
	private View mIndicator;
	private ImageView mIcon;
	private View mDivider;
	Handler mHandler = new Handler();
	final GpsObserver mGpsObserver = new GpsObserver(mHandler) ;


	// Gps settings observer
	class GpsObserver extends ContentObserver{

		public GpsObserver(Handler handler) {
			super(handler);
		}

	    @Override
	    public void onChange(boolean selfChange){

	    	updateResources();
	    }
	}



	public JunkGpsButton(Context context, AttributeSet attrs) {
		super(context, attrs);

	}



	protected void onAttachedToWindow(){
		super.onAttachedToWindow();

		showGps = (View) getRootView().findViewById(R.id.button_2);
		mIndicator = (View) getRootView().findViewById(R.id.indicator_2);
		mIcon = (ImageView) getRootView().findViewById(R.id.gps_icon);	
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
		mDivider = (View) getRootView().findViewById(R.id.divider_2);



		getContext().getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED), true,
                mGpsObserver);


		updateResources();
	}


	protected void onDetachedFromWindow(){

		getContext().getContentResolver().unregisterContentObserver(mGpsObserver);
	}




	@Override
	protected boolean getStatusOn(){

		return Settings.Secure.isLocationProviderEnabled(getContext().getContentResolver(), LocationManager.GPS_PROVIDER);
	}

	@Override
	void updateResources() {
		
		mIcon.clearColorFilter();

		ContentResolver contentResolver = getContext().getContentResolver();
	    boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
	    if(gpsStatus){
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOnColor);
			mIcon.setImageResource(R.drawable.junktoggle_gps_on);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
			setTextColor(JunkToggles.mToggleTextOnColor);
	    }else{
	    	mIcon.setImageResource(R.drawable.junktoggle_gps_off);
	    	mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOffColor);
			setTextColor(JunkToggles.mToggleTextOffColor);

	    }
	    
		if (JunkToggleButton.mShowGps) {
			showGps.setVisibility(View.VISIBLE);
		} else {
			showGps.setVisibility(View.GONE);
		}

		mDivider.setBackgroundColor(JunkToggles.mToggleDivColor);
	}



	@Override
	void toggleOn() {

		ContentResolver contentResolver = getContext().getContentResolver();
	    Settings.Secure.setLocationProviderEnabled(contentResolver,"gps" ,true);

		updateResources();
	}


	@Override
	void toggleOff() {
		ContentResolver contentResolver = getContext().getContentResolver();
	     Settings.Secure.setLocationProviderEnabled(contentResolver,"gps" ,false);		

		updateResources();
	}


	@Override
	void showSettings() {

	    Intent i = new Intent();
	    i.setAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
	    getContext().sendBroadcast(i);
	    i.setAction("android.settings.LOCATION_SOURCE_SETTINGS");
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
	}





}  // 
