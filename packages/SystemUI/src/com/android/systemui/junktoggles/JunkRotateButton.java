package com.android.systemui.junktoggles;


import com.android.systemui.R;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;




public class JunkRotateButton extends JunkToggleButton {

	private View showRotate;
	private View mIndicator;
	private ImageView mIcon;
	private View mDivider;
	private Context mContext; 
	Handler mHandler = new Handler();
	final RotateModeObserver mRotateModeObserver = new RotateModeObserver(mHandler) ;



	// Rotate settings observer
	class RotateModeObserver extends ContentObserver{

		public RotateModeObserver(Handler handler) {
			super(handler);
		}

	    @Override
	    public void onChange(boolean selfChange){

	    	updateResources();
	    }
	}



	public JunkRotateButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;
	}



	protected void onAttachedToWindow(){
		super.onAttachedToWindow();

		showRotate = (View) getRootView().findViewById(R.id.button_7);
		mIndicator = (View) getRootView().findViewById(R.id.indicator_7);
		mIcon = (ImageView) getRootView().findViewById(R.id.rotate_icon);
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
		mDivider = (View) getRootView().findViewById(R.id.divider_7);

        getContext().getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), true,
                mRotateModeObserver);
       	
		updateResources();
	}


	protected void onDetachedFromWindow(){

        getContext().getContentResolver().unregisterContentObserver(mRotateModeObserver);

	}



	@Override
	protected boolean getStatusOn(){

		boolean mTrue = true;
		try {
			mTrue = (Settings.System.getInt(getContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return mTrue;
	}



	@Override
	void updateResources() {
		
		mIcon.clearColorFilter();
		
	    boolean rotateStatus = true;
		try {
			rotateStatus = (Settings.System.getInt(getContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

	    if(rotateStatus){
			mIndicator.setBackgroundColor(JunkToggleViewTop.mToggleIndOnColor);
			mIcon.setImageResource(R.drawable.junktoggle_rotate_on);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
			setTextColor(JunkToggleViewTop.mToggleTextOnColor);
	      }else{
			mIcon.setImageResource(R.drawable.junktoggle_rotate_off);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
			mIndicator.setBackgroundColor(JunkToggleViewTop.mToggleIndOffColor);
			setTextColor(JunkToggleViewTop.mToggleTextOffColor);
	      }

		  mDivider.setBackgroundColor(JunkToggleViewTop.mToggleDivColor);
		  
		  if (JunkToggleButton.mShowRotate) {
			showRotate.setVisibility(View.VISIBLE);
		  } else {
			showRotate.setVisibility(View.GONE);
		  }
		  
	}



	@Override
	void toggleOn() {


		Settings.System.putInt(getContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
		updateResources();
	}


	@Override
	void toggleOff() {

		Settings.System.putInt(getContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);	
		updateResources();
	}




	@Override
	void showSettings() {

	    Intent i = new Intent();
	    i.setAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
	    mContext.sendBroadcast(i);
	    i.setAction("android.settings.DISPLAY_SETTINGS");
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
	}





}  //  
