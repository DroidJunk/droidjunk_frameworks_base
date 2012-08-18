package com.android.systemui.junktoggles;

import com.android.systemui.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;



public class JunkToggles extends LinearLayout {

	private final String Junk_Toggle_Settings = "JUNK_TOGGLE_SETTINGS";
	private final String TOGGLES_ON = "toggles_show_toggles";
	private final String TOGGLES_TOP = "toggles_top";
	private final String TOGGLE_COLOR = "toggle_color";
	private final String TOGGLE_SHOW_INDICATOR = "toggle_show_indicator";
	private final String TOGGLE_IND_ON_COLOR = "toggle_ind_on_color";
	private final String TOGGLE_IND_OFF_COLOR = "toggle_ind_off_color";
	private final String TOGGLE_SHOW_TEXT = "toggle_show_text";
	private final String TOGGLE_TEXT_ON_COLOR = "toggle_text_on_color";
	private final String TOGGLE_TEXT_OFF_COLOR = "toggle_text_off_color";
	private final String TOGGLE_SHOW_DIVIDER = "toggle_show_divider";
	private final String TOGGLE_DIVIDER_COLOR = "toggle_divider_color";
	private SharedPreferences mPrefs;
	public static boolean mTogglesOn = true;
	public static boolean mTogglesTop = true;
	public static int mToggleColor = 0xff141414;
    public static boolean mShowToggleInd = true;
    public static int mToggleIndOnColor = 0xffffffff;
    public static int mToggleIndOffColor = 0xff555555;
    public static boolean mShowToggleText = true;
    public static int mToggleTextOnColor = 0xffffffff;
    public static int mToggleTextOffColor = 0xff555555;   
    public static boolean mShowToggleDiv = true;
    public static int mToggleDivColor = 0xff2c2c2c;   
    public JunkToggles(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	protected void onFinishInflate(){
		super.onFinishInflate();
		

	}



	protected void onAttachedToWindow(){
		super.onAttachedToWindow();


  		Context settingsContext = getContext();
		try {
			settingsContext = getContext().createPackageContext("com.android.settings",0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     		


		mPrefs = settingsContext.getSharedPreferences("Junk_Settings", Context.MODE_PRIVATE);

		mTogglesOn = mPrefs.getBoolean(TOGGLES_ON, mTogglesOn);
		mTogglesTop = mPrefs.getBoolean(TOGGLES_TOP, mTogglesTop);
   		mToggleColor = mPrefs.getInt(TOGGLE_COLOR, mToggleColor);
   		mShowToggleInd = mPrefs.getBoolean(TOGGLE_SHOW_INDICATOR, mShowToggleInd);
   		mToggleIndOnColor = mPrefs.getInt(TOGGLE_IND_ON_COLOR, mToggleIndOnColor);
   		mToggleIndOffColor = mPrefs.getInt(TOGGLE_IND_OFF_COLOR, mToggleIndOffColor);
   		mShowToggleText = mPrefs.getBoolean(TOGGLE_SHOW_TEXT, mShowToggleText);
   		mToggleTextOnColor = mPrefs.getInt(TOGGLE_TEXT_ON_COLOR, mToggleTextOnColor);
   		mToggleTextOffColor = mPrefs.getInt(TOGGLE_TEXT_OFF_COLOR, mToggleTextOffColor);
   		mShowToggleDiv = mPrefs.getBoolean(TOGGLE_SHOW_DIVIDER, mShowToggleDiv);
   		mToggleDivColor = mPrefs.getInt(TOGGLE_DIVIDER_COLOR, mToggleDivColor);
   		
   		
   		if (!mShowToggleInd) {
   			mToggleIndOnColor = 0;
   			mToggleIndOffColor = 0;
   		}
   		
   		if (!mShowToggleText) {
   			mToggleTextOnColor = 0;
   			mToggleTextOffColor = 0;
   		}
   		
   		if (!mShowToggleDiv) {
   			mToggleDivColor = 0;
   		}
   		
   		setBackgroundColor(mToggleColor);
   		
   		updateView();
   		
        IntentFilter filter = new IntentFilter();
        filter.addAction(Junk_Toggle_Settings);
        getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
	}	




    
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Junk_Toggle_Settings)) {
             	
            	mTogglesOn = intent.getBooleanExtra(TOGGLES_ON, mTogglesOn);	
            	mTogglesTop = intent.getBooleanExtra(TOGGLES_TOP, mTogglesTop);
            	mToggleColor = intent.getIntExtra(TOGGLE_COLOR, mToggleColor);	
				mShowToggleInd = intent.getBooleanExtra(TOGGLE_SHOW_INDICATOR, mShowToggleInd);
            	mToggleIndOnColor = intent.getIntExtra(TOGGLE_IND_ON_COLOR, mToggleIndOnColor);
            	mToggleIndOffColor = intent.getIntExtra(TOGGLE_IND_OFF_COLOR, mToggleIndOffColor);
				mShowToggleText = intent.getBooleanExtra(TOGGLE_SHOW_TEXT, mShowToggleText);
            	mToggleTextOnColor = intent.getIntExtra(TOGGLE_TEXT_ON_COLOR, mToggleTextOnColor);
            	mToggleTextOffColor = intent.getIntExtra(TOGGLE_TEXT_OFF_COLOR, mToggleTextOffColor);
            	mShowToggleDiv = intent.getBooleanExtra(TOGGLE_SHOW_DIVIDER, mShowToggleDiv);
            	mToggleDivColor = intent.getIntExtra(TOGGLE_DIVIDER_COLOR, mToggleDivColor);

            	setBackgroundColor(mToggleColor);
            	updateView();

            }
        }    
    };
       
 
    private void updateView(){

		FrameLayout.LayoutParams
		params = (FrameLayout.LayoutParams)getLayoutParams();

    	if (!mTogglesOn) {
    		setVisibility(View.GONE);
    	} else if (mTogglesTop) {
    		setVisibility(View.VISIBLE);
    		params.gravity = Gravity.TOP;
    		params.bottomMargin = 0;
    		params.topMargin = (int) getResources().getDimension(R.dimen.junk_toggle_top_margin);
    		setLayoutParams(params);
    		params = null;
    		} else {
    			setVisibility(View.VISIBLE);
    			params.gravity=Gravity.BOTTOM;
    			params.bottomMargin = (int) getResources().getDimension(R.dimen.junk_toggle_bottom_margin);
    			params.topMargin = 0;
        		setLayoutParams(params);
        		params = null;

    		}
    	
    	
    	
    	
    	
    }
}
