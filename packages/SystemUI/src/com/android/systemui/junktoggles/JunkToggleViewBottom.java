package com.android.systemui.junktoggles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Telephony;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.systemui.R;



public class JunkToggleViewBottom extends LinearLayout {

	private final String Junk_Toggle_Settings = "JUNK_TOGGLE_SETTINGS";
	private final String TOGGLES_ON = "toggles_show_toggles";
	private final String TOGGLES_TOP = "toggles_top";
	private final String TOGGLE_COLOR = "toggle_color";
	private final String TOGGLE_IND_ON_COLOR = "toggle_ind_on_color";
	private final String TOGGLE_IND_OFF_COLOR = "toggle_ind_off_color";
	private final String TOGGLE_TEXT_ON_COLOR = "toggle_text_on_color";
	private final String TOGGLE_TEXT_OFF_COLOR = "toggle_text_off_color";
	private final String TOGGLE_DIVIDER_COLOR = "toggle_divider_color";
	private SharedPreferences mPrefs;


    


	public JunkToggleViewBottom(Context context, AttributeSet attrs) {
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

   		JunkToggleViewTop.mToggleColor = mPrefs.getInt(TOGGLE_COLOR, JunkToggleViewTop.mToggleColor);
   		JunkToggleViewTop.mTogglesOn = mPrefs.getBoolean(TOGGLES_ON, JunkToggleViewTop.mTogglesOn);
   		JunkToggleViewTop.mTogglesTop = mPrefs.getBoolean(TOGGLES_TOP, JunkToggleViewTop.mTogglesTop);
   		JunkToggleViewTop.mToggleIndOnColor = mPrefs.getInt(TOGGLE_IND_ON_COLOR, JunkToggleViewTop.mToggleIndOnColor);
   		JunkToggleViewTop.mToggleIndOffColor = mPrefs.getInt(TOGGLE_IND_OFF_COLOR, JunkToggleViewTop.mToggleIndOffColor);
   		JunkToggleViewTop.mToggleTextOnColor = mPrefs.getInt(TOGGLE_TEXT_ON_COLOR, JunkToggleViewTop.mToggleTextOnColor);
   		JunkToggleViewTop.mToggleTextOffColor = mPrefs.getInt(TOGGLE_TEXT_OFF_COLOR, JunkToggleViewTop.mToggleTextOffColor);
   		JunkToggleViewTop.mToggleDivColor = mPrefs.getInt(TOGGLE_DIVIDER_COLOR, JunkToggleViewTop.mToggleDivColor);
   		
   		setBackgroundColor(JunkToggleViewTop.mToggleColor);
   		
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
             	
            	JunkToggleViewTop.mToggleColor = intent.getIntExtra(TOGGLE_COLOR, JunkToggleViewTop.mToggleColor);	
            	setBackgroundColor(JunkToggleViewTop.mToggleColor);
            	
            	updateView();

            }
        }    
    };
       

    private void updateView(){
    
    	if (!JunkToggleViewTop.mTogglesOn) {
    		setVisibility(View.GONE);
    	} else if (JunkToggleViewTop.mTogglesTop) {
    		setVisibility(View.GONE);
    		} else {
    			setVisibility(View.VISIBLE);
    		}
    	}

}
