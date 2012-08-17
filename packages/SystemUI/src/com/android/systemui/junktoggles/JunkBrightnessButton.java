package com.android.systemui.junktoggles;


import com.android.systemui.R;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;




public class JunkBrightnessButton extends JunkToggleButton {

	private View showBrightness;
	private View mIndicator;
	private ImageView mIcon;
	private View mDivider;
	private Context mContext; 
	private IPowerManager mPower;
	private int mBrightness = 20;
	private boolean toggling = false ;
	private final int BRIGHTNESS_AUTO = 19;
	private final int BRIGHTNESS_DIM = 20;
	private final int BRIGHTNESS_LOW = 67;
	private final int BRIGHTNESS_MEDLOW = 114;
	private final int BRIGHTNESS_MED = 161;
	private final int BRIGHTNESS_MEDHIGH = 208;
	private final int BRIGHTNESS_HIGH = 255;
	Handler mHandler = new Handler();
	final BrightnessModeObserver mBrightnessModeObserver = new BrightnessModeObserver(mHandler) ;



	// Brightness settings observer
	class BrightnessModeObserver extends ContentObserver{

		public BrightnessModeObserver(Handler handler) {
			super(handler);
		}

	    @Override
	    public void onChange(boolean selfChange){

	    	updateIcons();
	    }
	}



	public JunkBrightnessButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;
	}



	protected void onAttachedToWindow(){
		super.onAttachedToWindow();

		showBrightness = (View) getRootView().findViewById(R.id.button_6);
		mIndicator = (View) getRootView().findViewById(R.id.indicator_6);
		mIcon = (ImageView) getRootView().findViewById(R.id.brightness_icon);
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
		mDivider = (View) getRootView().findViewById(R.id.divider_6);		

        getContext().getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE), true,
                mBrightnessModeObserver);
        mPower = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
       	
		updateResources();
	}


	protected void onDetachedFromWindow(){

        getContext().getContentResolver().unregisterContentObserver(mBrightnessModeObserver);

	}



	@Override
	protected boolean getStatusOn(){

		boolean mTrue = true;
		try {
			mTrue = (Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == 1);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return mTrue;
	}

    private void setBrightness(int brightness) {
    	
            try {
				mPower.setBacklightBrightness(brightness);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
            mBrightness = brightness;
        	Settings.System.putInt(getContext().getContentResolver(),
	        		  Settings.System.SCREEN_BRIGHTNESS,brightness);
    }

	@Override
	void updateResources() {
		
		mIcon.clearColorFilter();

	    boolean autoBrightnessStatus = true;
		try {
			autoBrightnessStatus = (Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == 1);
		} catch (SettingNotFoundException e) {
		}

		try {
			mBrightness = (Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS));
		} catch (SettingNotFoundException e) {
			
		}
		
	    if(autoBrightnessStatus){
			mBrightness = BRIGHTNESS_AUTO;
        	Settings.System.putInt(getContext().getContentResolver(),
	        		  Settings.System.SCREEN_BRIGHTNESS,BRIGHTNESS_AUTO);

	    } else if (toggling) {
	    	
	    	toggling = false;
	    	
	    	if (mBrightness < BRIGHTNESS_AUTO) {
	    		mBrightness = BRIGHTNESS_AUTO;
	    	} else if (mBrightness > BRIGHTNESS_DIM & mBrightness < BRIGHTNESS_LOW) {
	    		mBrightness = BRIGHTNESS_LOW;
	    	} else if (mBrightness > BRIGHTNESS_LOW & mBrightness < BRIGHTNESS_MEDLOW) {
	    		mBrightness = BRIGHTNESS_MEDLOW;
	    	} else if (mBrightness > BRIGHTNESS_MEDLOW & mBrightness < BRIGHTNESS_MED) {
	    		mBrightness = BRIGHTNESS_MED;
	    	} else if (mBrightness > BRIGHTNESS_MED & mBrightness < BRIGHTNESS_MEDHIGH) {
	    		mBrightness = BRIGHTNESS_MEDHIGH;
	    	} else if (mBrightness > BRIGHTNESS_MEDHIGH) {
	    		mBrightness = BRIGHTNESS_HIGH;
	    	}
	    		
	    		
	    		
	    	switch (mBrightness) {

	        case BRIGHTNESS_AUTO:
	        	setBrightness(BRIGHTNESS_DIM);
	            break;
	    	
	  
	        case BRIGHTNESS_DIM:
	        	setBrightness(BRIGHTNESS_LOW);
	            break;
	        
	        case BRIGHTNESS_LOW:
	        	setBrightness(BRIGHTNESS_MEDLOW);
	            break;
	        
	        case BRIGHTNESS_MEDLOW:
	        	setBrightness(BRIGHTNESS_MED);
	            break;
	        
	        case BRIGHTNESS_MED:
	        	setBrightness(BRIGHTNESS_MEDHIGH);
	            break;
	        
	        case BRIGHTNESS_MEDHIGH:
	        	setBrightness(BRIGHTNESS_HIGH);
	        	break;
	        
	        default:
	        	setBrightness(BRIGHTNESS_AUTO);
	            break;
	        } 
	    }

	    updateIcons();	
	}


	private void updateIcons() {
		
		mIcon.clearColorFilter();

	    boolean autoBrightnessStatus = true;
		try {
			autoBrightnessStatus = (Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == 1);
		} catch (SettingNotFoundException e) {
		}

	    if(autoBrightnessStatus){

			mIcon.setImageResource(R.drawable.junktoggle_brightness_on);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
		    mIndicator.setBackgroundColor(JunkToggles.mToggleIndOnColor);
		    setTextColor(JunkToggles.mToggleTextOnColor);
	    }else{

	    	switch (mBrightness) {

	    	case BRIGHTNESS_DIM:
	    		mIcon.setImageResource(R.drawable.junktoggle_brightness_dim);
	            break;
	    	case BRIGHTNESS_LOW:
	    		mIcon.setImageResource(R.drawable.junktoggle_brightness_low);
	    		break;
	        case BRIGHTNESS_MEDLOW:
		    	mIcon.setImageResource(R.drawable.junktoggle_brightness_lowmed);
	            break;
	        case BRIGHTNESS_MED:
		    	mIcon.setImageResource(R.drawable.junktoggle_brightness_med);
	            break;
	        case BRIGHTNESS_MEDHIGH:
		    	mIcon.setImageResource(R.drawable.junktoggle_brightness_medhigh);
	            break;
	        default:
	        	mIcon.setImageResource(R.drawable.junktoggle_brightness_high);
	        	break;
	           } 
	    	
	    	mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);  
	    	mIndicator.setBackgroundColor(JunkToggles.mToggleIndOffColor);
	    	setTextColor(JunkToggles.mToggleTextOffColor);
	    }
	    

    	
		
		
		mDivider.setBackgroundColor(JunkToggles.mToggleDivColor);
		  
		if (JunkToggleButton.mShowBrightness) {
			showBrightness.setVisibility(View.VISIBLE);
		} else {
			showBrightness.setVisibility(View.GONE);
		}
		  
	}

	@Override
	void toggleOn() {

		if (mBrightness == BRIGHTNESS_HIGH) {
			Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 1);
		}
		toggling = true;
		updateResources();
	}


	@Override
	void toggleOff() {
		
		Settings.System.putInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
		toggling = true;
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
