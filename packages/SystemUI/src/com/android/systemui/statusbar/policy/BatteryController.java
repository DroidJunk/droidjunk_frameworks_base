/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.systemui.R;
import droidjunk.colorfitermaker.ColorFilterMaker;



public class BatteryController extends LinearLayout {

    private Context mContext;

    private ImageView mBatteryIconCircle; 
    private ImageView mBatteryIcon;
    private ImageView mBatteryIconColor; 
    private ImageView mBatteryIconNumber; 
    private ImageView mBatteryBar;
    private ImageView mBatteryBarOff;
    private ImageView mBatteryCharge;

    private int mLevel = -1;
    private boolean mPlugged = false;

    private final String Junk_Battery_Settings = "JUNK_BATTERY_SETTINGS";
    private SharedPreferences mPrefs; 
    
    private final int BATTERY_STOCK = 0;
    private final int BATTERY_NUMBER = 1;
    private final int BATTERY_CIRCLE = 2;
    private final int BATTERY_PIE = 3;
    private final int BATTERY_THINKING_MAN = 4;
    private final int BATTERY_THE_DOT = 5;
    private final int BATTERY_NONE = 6;
    
    private final int BAR_NONE = 0;
    private final int BAR_BOTTOM = 1;
    private final int BAR_RIGHT = 2;

	private final String BATTERY_ICONS = "battery_icon_num";
	private final String BATTERY_SHOW_CIRCLE = "battery_show_circle";
	private final String BATTERY_SHOW_SQUARE = "battery_show_square";
	private final String BATTERY_SHOW_NUMBER = "battery_show_number";
	private final String BATTERY_CIRCLE_COLOR_ONE = "battery_circle_color_one";
	private final String BATTERY_CIRCLE_COLOR_TWO = "battery_circle_color_two";
	private final String BATTERY_CIRCLE_COLOR_THREE = "battery_circle_color_three";
	private final String BATTERY_NUMBER_COLOR_ONE = "battery_number_color_one";
	private final String BATTERY_NUMBER_COLOR_TWO = "battery_number_color_two";
	private final String BATTERY_NUMBER_COLOR_THREE = "battery_number_color_three";
	private final String BATTERY_BAR_BOTTOM = "battery_bar_bottom";
	private final String BATTERY_BAR_RIGHT = "battery_bar_right";
	private final String BATTERY_BAR_WIDTH = "battery_bar_width";
	private final String BATTERY_LEVEL_ONE = "battery_levels_one";
	private final String BATTERY_LEVEL_COLOR_ONE = "battery_levels_color_one";
	private final String BATTERY_LEVEL_TWO = "battery_levels_two";
	private final String BATTERY_LEVEL_COLOR_TWO = "battery_levels_color_two";
	private final String BATTERY_LEVEL_COLOR_THREE = "battery_levels_color_three";  
	
	private final String CHARGING_LEVEL_ONE = "charge_levels_one";
	private final String CHARGING_LEVEL_COLOR_ONE = "charge_levels_color_one";
	private final String CHARGING_LEVEL_TWO = "charge_levels_two";
	private final String CHARGING_LEVEL_COLOR_TWO = "charge_levels_color_two";
	private final String CHARGING_LEVEL_COLOR_THREE = "charge_levels_color_three";    
	
	private final String DEPLETED_LEVEL_ONE = "depleted_levels_one";
	private final String DEPLETED_LEVEL_COLOR_ONE = "depleted_levels_color_one";
	private final String DEPLETED_LEVEL_TWO = "depleted_levels_two";
	private final String DEPLETED_LEVEL_COLOR_TWO = "depleted_levels_color_two";
	private final String DEPLETED_LEVEL_COLOR_THREE = "depleted_levels_color_three";    

    
    private int mBatteryIconNum = 0;
    private boolean mBarBottom = false;
    private boolean mBarRight = false;
    private boolean mShowCircle = false;
    private boolean mShowSquare = false;
    private boolean mShowNumber = false;
    private int mCircleColorOne = 0xffff0000;
    private int mCircleColorTwo = 0xffff9000;
    private int mCircleColorThree = 0xff3fa2c7;
    private int mNumberColorOne = 0xffff0000;
    private int mNumberColorTwo = 0xffff9000;
    private int mNumberColorThree = 0xff3fa2c7;
    private int mBarWidth = 5;
    private int mBarPosition = 0; 
    private int mOldBarPosition = 0;
    private int mBatteryLevelsOne = 10;
    private int mBatteryLevelsColorOne = 0xffff0000;
    private int mBatteryLevelsTwo = 30;
    private int mBatteryLevelsColorTwo = 0xffff9000;
    private int mBatteryLevelsColorThree = 0xff3fa2c7;
    private int mChargingLevelsOne = 10;
    private int mChargingLevelsColorOne = 0xffff0000;
    private int mChargingLevelsTwo = 30;
    private int mChargingLevelsColorTwo = 0xffff9000;
    private int mChargingLevelsColorThree = 0xff3fa2c7;
    private int mDepletedLevelsOne = 10;
    private int mDepletedLevelsColorOne = 0xff800000;
    private int mDepletedLevelsTwo = 30;
    private int mDepletedLevelsColorTwo = 0xffba6900;
    private int mDepletedLevelsColorThree = 0xff296c85;
    private boolean colorBaseIcon = false;
    private boolean rotatingIcons = false;
    private boolean thinkingMan = false;
    
    public BatteryController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
        mBatteryIconCircle = (ImageView) findViewById(R.id.battery_circle);
        mBatteryIcon = (ImageView) findViewById(R.id.battery);
        mBatteryIconColor = (ImageView) findViewById(R.id.battery_color);
        mBatteryIconNumber = (ImageView) findViewById(R.id.battery_number);
        mBatteryBar = (ImageView) findViewById(R.id.battery_horiz_bar_bottom);
        mBatteryBarOff = (ImageView) findViewById(R.id.battery_horiz_bar_bottom);
        mBatteryCharge = (ImageView) findViewById(R.id.charge_icon);
       
        
  		Context settingsContext = getContext();
		try {
			settingsContext = getContext().createPackageContext("com.junk.settings",0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		mPrefs = settingsContext.getSharedPreferences("Junk_Settings", Context.MODE_WORLD_READABLE);
		mBatteryIconNum = mPrefs.getInt(BATTERY_ICONS, mBatteryIconNum);
   		mBatteryLevelsOne = mPrefs.getInt(BATTERY_LEVEL_ONE, mBatteryLevelsOne);
   		mBatteryLevelsColorOne = mPrefs.getInt(BATTERY_LEVEL_COLOR_ONE, mBatteryLevelsColorOne);
   		mBatteryLevelsTwo = mPrefs.getInt(BATTERY_LEVEL_TWO, mBatteryLevelsTwo);
   		mBatteryLevelsColorTwo = mPrefs.getInt(BATTERY_LEVEL_COLOR_TWO, mBatteryLevelsColorTwo);
   		mBatteryLevelsColorThree = mPrefs.getInt(BATTERY_LEVEL_COLOR_THREE, mBatteryLevelsColorThree);
   		mChargingLevelsOne = mPrefs.getInt(CHARGING_LEVEL_ONE, mChargingLevelsOne);
   		mChargingLevelsColorOne = mPrefs.getInt(CHARGING_LEVEL_COLOR_ONE, mChargingLevelsColorOne);
   		mChargingLevelsTwo = mPrefs.getInt(CHARGING_LEVEL_TWO, mChargingLevelsTwo);
   		mChargingLevelsColorTwo = mPrefs.getInt(CHARGING_LEVEL_COLOR_TWO, mChargingLevelsColorTwo);
   		mChargingLevelsColorThree = mPrefs.getInt(CHARGING_LEVEL_COLOR_THREE, mChargingLevelsColorThree);
   		mDepletedLevelsOne = mPrefs.getInt(DEPLETED_LEVEL_ONE, mDepletedLevelsOne);
   		mDepletedLevelsColorOne = mPrefs.getInt(DEPLETED_LEVEL_COLOR_ONE, mDepletedLevelsColorOne);
   		mDepletedLevelsTwo = mPrefs.getInt(DEPLETED_LEVEL_TWO, mDepletedLevelsTwo);
   		mDepletedLevelsColorTwo = mPrefs.getInt(DEPLETED_LEVEL_COLOR_TWO, mDepletedLevelsColorTwo);
   		mDepletedLevelsColorThree = mPrefs.getInt(DEPLETED_LEVEL_COLOR_THREE, mDepletedLevelsColorThree);
   		mBarBottom = mPrefs.getBoolean(BATTERY_BAR_BOTTOM, mBarBottom);
   		mBarRight = mPrefs.getBoolean(BATTERY_BAR_RIGHT, mBarRight);
   		mBarWidth = mPrefs.getInt(BATTERY_BAR_WIDTH, mBarWidth);
   		mShowSquare = mPrefs.getBoolean(BATTERY_SHOW_SQUARE, mShowSquare);
   		mShowCircle = mPrefs.getBoolean(BATTERY_SHOW_CIRCLE, mShowCircle);
   		mCircleColorOne = mPrefs.getInt(BATTERY_CIRCLE_COLOR_ONE, mCircleColorOne);
   		mCircleColorTwo = mPrefs.getInt(BATTERY_CIRCLE_COLOR_TWO, mCircleColorTwo);
   		mCircleColorThree = mPrefs.getInt(BATTERY_CIRCLE_COLOR_THREE, mCircleColorThree);
   		mShowNumber = mPrefs.getBoolean(BATTERY_SHOW_NUMBER, mShowNumber);
   		mNumberColorOne = mPrefs.getInt(BATTERY_NUMBER_COLOR_ONE, mNumberColorOne);
   		mNumberColorTwo = mPrefs.getInt(BATTERY_NUMBER_COLOR_TWO, mNumberColorTwo);
   		mNumberColorThree = mPrefs.getInt(BATTERY_NUMBER_COLOR_THREE, mNumberColorThree);
   		
   		
   		mBarPosition = BAR_NONE;
   		if (mBarBottom) mBarPosition = BAR_BOTTOM;
   		if (mBarRight) mBarPosition = BAR_RIGHT;
   		
        setBatteryIcon(mLevel, mPlugged);
        
 
    }

    private void init() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Junk_Battery_Settings);
        mContext.registerReceiver(mBatteryBroadcastReceiver, filter);
    }

    
    private BroadcastReceiver mBatteryBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                final int level = intent.getIntExtra(
                        BatteryManager.EXTRA_LEVEL, 0);
                final boolean plugged = intent.getIntExtra(
                        BatteryManager.EXTRA_PLUGGED, 0) != 0;
                mLevel = level;
                mPlugged = plugged;
                mBatteryIcon.setRotation(0);
                mBatteryIconColor.setRotation(0);
                setBatteryIcon(level, plugged);
            }
  
            if (action.equals(Junk_Battery_Settings)) {
 
            	mBatteryIconNum = intent.getIntExtra(BATTERY_ICONS, mBatteryIconNum);
           		mShowCircle = intent.getBooleanExtra(BATTERY_SHOW_CIRCLE, mShowCircle);
           		mShowSquare = intent.getBooleanExtra(BATTERY_SHOW_SQUARE, mShowSquare);
           		mCircleColorOne = intent.getIntExtra(BATTERY_CIRCLE_COLOR_ONE, mCircleColorOne);
           		mCircleColorTwo = intent.getIntExtra(BATTERY_CIRCLE_COLOR_TWO, mCircleColorTwo);
           		mCircleColorThree = intent.getIntExtra(BATTERY_CIRCLE_COLOR_THREE, mCircleColorThree);
           		mShowNumber = intent.getBooleanExtra(BATTERY_SHOW_NUMBER, mShowNumber);
           		mNumberColorOne = intent.getIntExtra(BATTERY_NUMBER_COLOR_ONE, mNumberColorOne);
           		mNumberColorTwo = intent.getIntExtra(BATTERY_NUMBER_COLOR_TWO, mNumberColorTwo);
           		mNumberColorThree = intent.getIntExtra(BATTERY_NUMBER_COLOR_THREE, mNumberColorThree);
           		mBarBottom = intent.getBooleanExtra(BATTERY_BAR_BOTTOM, mBarBottom);
           		mBarRight = intent.getBooleanExtra(BATTERY_BAR_RIGHT, mBarRight);
           		mBarWidth = intent.getIntExtra(BATTERY_BAR_WIDTH, mBarWidth);
           		mBatteryLevelsOne = intent.getIntExtra(BATTERY_LEVEL_ONE, mBatteryLevelsOne);
           		mBatteryLevelsColorOne = intent.getIntExtra(BATTERY_LEVEL_COLOR_ONE, mBatteryLevelsColorOne);
           		mBatteryLevelsTwo = intent.getIntExtra(BATTERY_LEVEL_TWO, mBatteryLevelsTwo);
           		mBatteryLevelsColorTwo = intent.getIntExtra(BATTERY_LEVEL_COLOR_TWO, mBatteryLevelsColorTwo);
           		mBatteryLevelsColorThree = intent.getIntExtra(BATTERY_LEVEL_COLOR_THREE, mBatteryLevelsColorThree);
           		mChargingLevelsOne = intent.getIntExtra(CHARGING_LEVEL_ONE, mChargingLevelsOne);
           		mChargingLevelsColorOne = intent.getIntExtra(CHARGING_LEVEL_COLOR_ONE, mChargingLevelsColorOne);
           		mChargingLevelsTwo = intent.getIntExtra(CHARGING_LEVEL_TWO, mChargingLevelsTwo);
           		mChargingLevelsColorTwo = intent.getIntExtra(CHARGING_LEVEL_COLOR_TWO, mChargingLevelsColorTwo);
           		mChargingLevelsColorThree = intent.getIntExtra(CHARGING_LEVEL_COLOR_THREE, mChargingLevelsColorThree);
           		mDepletedLevelsOne = intent.getIntExtra(DEPLETED_LEVEL_ONE, mDepletedLevelsOne);
           		mDepletedLevelsColorOne = intent.getIntExtra(DEPLETED_LEVEL_COLOR_ONE, mDepletedLevelsColorOne);
           		mDepletedLevelsTwo = intent.getIntExtra(DEPLETED_LEVEL_TWO, mDepletedLevelsTwo);
           		mDepletedLevelsColorTwo = intent.getIntExtra(DEPLETED_LEVEL_COLOR_TWO, mDepletedLevelsColorTwo);
           		mDepletedLevelsColorThree = intent.getIntExtra(DEPLETED_LEVEL_COLOR_THREE, mDepletedLevelsColorThree);
           		
                mBatteryIcon.setRotation(0);
                mBatteryIconColor.setRotation(0);

           		mBarPosition = BAR_NONE;
           		if (mBarBottom) mBarPosition = BAR_BOTTOM;
           		if (mBarRight) mBarPosition = BAR_RIGHT;
            	setBatteryIcon(mLevel, mPlugged);

            }
        }
    };

    
    private void setBatteryIcon(int level, boolean plugged) {

        switch (mBatteryIconNum) {
        
        case BATTERY_STOCK:
            setVisibility(View.VISIBLE);
        	mBatteryIcon.setVisibility(View.VISIBLE);
    		mBatteryBar.setVisibility(View.GONE);
    		mBatteryBarOff.setVisibility(View.GONE);
    		mBatteryCharge.setVisibility(View.GONE);
    		mBatteryIconCircle.setVisibility(View.GONE);
    		mBatteryIconNumber.setVisibility(View.GONE);
    		mBatteryIconColor.setVisibility(View.VISIBLE);
            mBatteryIconColor.setImageResource(plugged ? R.drawable.junk_stock_charge_color
                	: R.drawable.junk_stock_color);
            mBatteryIcon.setImageResource(plugged ? R.drawable.stat_sys_battery_charge
                	: R.drawable.stat_sys_battery);
            colorBaseIcon = false;
            rotatingIcons = false;
            thinkingMan = false;
             break;
        
        case BATTERY_NUMBER:
        	setVisibility(View.VISIBLE);
    		mBatteryIcon.setVisibility(View.VISIBLE);
    		mBatteryIconColor.setVisibility(View.GONE);
    		if (mShowCircle) {
    			mBatteryIconCircle.setImageResource(R.drawable.junk_circle_large);
    			mBatteryIconCircle.setVisibility(View.VISIBLE);
    		} else if (mShowSquare) {
    			mBatteryIconCircle.setImageResource(R.drawable.junk_square_batt);
    			mBatteryIconCircle.setVisibility(View.VISIBLE);
    		} else mBatteryIconCircle.setVisibility(View.GONE); 
    		mBatteryIconNumber.setVisibility(View.GONE);
    		mBatteryCharge.setVisibility(plugged ? View.VISIBLE : View.GONE);
   			mBatteryCharge.setImageResource(R.drawable.junk_battery_charge);
            mBatteryIcon.setImageResource(R.drawable.junk_number);    	
        	getBatteryBar();
        	colorBaseIcon = true;
        	rotatingIcons = false;
        	thinkingMan = false;
            break;
            
        case BATTERY_CIRCLE:
            setVisibility(View.VISIBLE);
        	mBatteryIcon.setVisibility(View.VISIBLE);
    		mBatteryBar.setVisibility(View.GONE);
    		mBatteryBarOff.setVisibility(View.GONE);
    		mBatteryIconColor.setVisibility(View.VISIBLE);
    		mBatteryIconCircle.setVisibility(View.GONE);
    		mBatteryIconNumber.setVisibility(mShowNumber ? View.VISIBLE : View.GONE);
    		mBatteryIconColor.setImageResource(R.drawable.junk_circseg_color);
    		mBatteryCharge.setVisibility(plugged ? View.VISIBLE : View.GONE);
   			mBatteryCharge.setImageResource(R.drawable.junk_battery_charge);
            mBatteryIcon.setImageResource(R.drawable.junk_circseg);
            mBatteryIconNumber.setImageResource(R.drawable.junk_number_small);
            mBatteryIconNumber.setImageLevel(mLevel);
            colorBaseIcon = false;
            rotatingIcons = false;
            thinkingMan = false;
        	break;            
            
        case BATTERY_PIE:
            setVisibility(View.VISIBLE);
        	mBatteryIcon.setVisibility(View.VISIBLE);
    		mBatteryBar.setVisibility(View.GONE);
    		mBatteryBarOff.setVisibility(View.GONE);
    		mBatteryIconColor.setVisibility(View.VISIBLE);
    		mBatteryIconCircle.setVisibility(mShowCircle ? View.VISIBLE : View.GONE);
    		mBatteryIconNumber.setVisibility(mShowNumber ? View.VISIBLE : View.GONE);
            mBatteryIconColor.setImageResource(R.drawable.junk_pieseg_color);
    		mBatteryCharge.setVisibility(plugged ? View.VISIBLE : View.GONE);
   			mBatteryCharge.setImageResource(R.drawable.junk_battery_charge);
            mBatteryIcon.setImageResource(R.drawable.junk_pieseg);
            mBatteryIconCircle.setImageResource(R.drawable.junk_circle_large);
            mBatteryIconNumber.setImageResource(R.drawable.junk_number_small);
            mBatteryIconNumber.setImageLevel(mLevel);
            colorBaseIcon = false;
            rotatingIcons = false;
            thinkingMan = false;
        	break;            
        	
        case BATTERY_THINKING_MAN:
            setVisibility(View.VISIBLE);
        	mBatteryIcon.setVisibility(View.VISIBLE);
    		mBatteryBar.setVisibility(View.GONE);
    		mBatteryBarOff.setVisibility(View.GONE);
    		mBatteryIconColor.setVisibility(View.VISIBLE);
            mBatteryIconColor.setImageResource(R.drawable.junk_pointer_batt);
    		mBatteryIconCircle.setVisibility(mShowCircle ? View.VISIBLE : View.GONE);
    		mBatteryIconNumber.setVisibility(mShowNumber ? View.VISIBLE : View.GONE);
            mBatteryIconNumber.setImageResource(R.drawable.junk_number_small);
            mBatteryIconNumber.setImageLevel(mLevel);
            mBatteryIconCircle.setImageResource(R.drawable.junk_circle_small);
    		mBatteryCharge.setVisibility(plugged ? View.VISIBLE : View.GONE);
   			mBatteryCharge.setImageResource(R.drawable.junk_battery_charge);
            mBatteryIcon.setImageResource(R.drawable.junk_pointer_batt);
            colorBaseIcon = false;
            rotatingIcons = true;
            thinkingMan = true;
            char[] mLevelString = String.valueOf(mLevel).toCharArray();
            int mLevelDigit1 = 0;
            int mLevelDigit2 = 0;
            if (String.valueOf(mLevelString[0]) != "-") {
            if (mLevelString.length ==  3) {
            	mLevelDigit1 = 0;
            	mLevelDigit2 = 0;
            } else if (mLevelString.length == 2) {
    			try {
    				mLevelDigit1 = Integer.parseInt(String.valueOf(mLevelString[0]));
                	mLevelDigit2 = Integer.parseInt(String.valueOf(mLevelString[1]));
    			} catch (NumberFormatException e) {
    			}
            	
            }  else if (mLevelString.length == 1) {
    			try {
    				mLevelDigit2 = Integer.parseInt(String.valueOf(mLevelString[0]));
    			} catch (NumberFormatException e) {
    			}
            	mLevelDigit1 = 0;
            	
            }
            }
            mBatteryIcon.setRotation(mLevelDigit2 * 36f);
            mBatteryIconColor.setRotation(mLevelDigit1 * 36f);

        	break;   
        	
        case BATTERY_THE_DOT:
            setVisibility(View.VISIBLE);
        	mBatteryIcon.setVisibility(View.VISIBLE);
    		mBatteryBar.setVisibility(View.GONE);
    		mBatteryBarOff.setVisibility(View.GONE);
    		mBatteryIconColor.setVisibility(View.GONE);
    		mBatteryIconCircle.setVisibility(mShowCircle ? View.VISIBLE : View.GONE);
    		mBatteryIconNumber.setVisibility(mShowNumber ? View.VISIBLE : View.GONE);
            mBatteryIconCircle.setImageResource(R.drawable.junk_circle_small);
    		mBatteryIconNumber.setImageResource(R.drawable.junk_number_small);
            mBatteryIconNumber.setImageLevel(mLevel);
    		mBatteryCharge.setVisibility(plugged ? View.VISIBLE : View.GONE);
   			mBatteryCharge.setImageResource(R.drawable.junk_battery_charge);
   			mBatteryIcon.setImageResource(R.drawable.junk_pointer_batt);
            colorBaseIcon = true;
            rotatingIcons = true;
            thinkingMan = false;
            mBatteryIcon.setRotation((mLevel * 36f) / 10);


        	break;        	
        	
        case BATTERY_NONE:
    		setVisibility(View.GONE);
    		break;

    		
       default:
           mBatteryIcon.setVisibility(View.VISIBLE);
           setVisibility(View.VISIBLE);
           mBatteryIconColor.setVisibility(View.INVISIBLE);
           mBatteryIcon.setImageResource(plugged ? R.drawable.stat_sys_battery_charge
                   : R.drawable.stat_sys_battery);
            break;
        }        

        // Set the battery and charging colors
   		if (!rotatingIcons) {
   			mBatteryIconColor.setImageLevel(mLevel);
   			mBatteryIcon.setImageLevel(mLevel);
   		}
   		
   		
        mBatteryCharge.setImageLevel(mLevel);
        // Battery
   		if (mLevel > mBatteryLevelsTwo) {
   			if (colorBaseIcon) mBatteryIcon.setColorFilter(ColorFilterMaker.changeColorAlpha(mBatteryLevelsColorThree, .45f,0f));
   			mBatteryIconColor.setColorFilter(ColorFilterMaker.changeColorAlpha(mBatteryLevelsColorThree, .45f, 0f));
   	   		if (mBatteryIconCircle.getVisibility() == View.VISIBLE)
   	   			mBatteryIconCircle.setColorFilter(ColorFilterMaker.changeColorAlpha(mCircleColorThree, .45f, 0f));
   	   		if (mBatteryIconNumber.getVisibility() == View.VISIBLE)
   	   			mBatteryIconNumber.setColorFilter(ColorFilterMaker.changeColorAlpha(mNumberColorThree, .45f, 0f));
   		}
   		if (mLevel <= mBatteryLevelsTwo) {
   			if (colorBaseIcon) mBatteryIcon.setColorFilter(ColorFilterMaker.changeColorAlpha(mBatteryLevelsColorTwo, .45f, 0f));
   			mBatteryIconColor.setColorFilter(ColorFilterMaker.changeColorAlpha(mBatteryLevelsColorTwo, .45f, 0f));
   	   		if (mBatteryIconCircle.getVisibility() == View.VISIBLE)
   	   			mBatteryIconCircle.setColorFilter(ColorFilterMaker.changeColorAlpha(mCircleColorTwo, .45f, 0f));
   	   		if (mBatteryIconNumber.getVisibility() == View.VISIBLE)
   	   			mBatteryIconNumber.setColorFilter(ColorFilterMaker.changeColorAlpha(mNumberColorTwo, .45f, 0f));
   		}
   		if (mLevel <= mBatteryLevelsOne) {
   			if (colorBaseIcon) mBatteryIcon.setColorFilter(ColorFilterMaker.changeColorAlpha(mBatteryLevelsColorOne, .45f, 0f));
   			mBatteryIconColor.setColorFilter(ColorFilterMaker.changeColorAlpha(mBatteryLevelsColorOne, .45f, 0f));
   	   		if (mBatteryIconCircle.getVisibility() == View.VISIBLE)
   	   			mBatteryIconCircle.setColorFilter(ColorFilterMaker.changeColorAlpha(mCircleColorOne, .45f, 0f));
   	   		if (mBatteryIconNumber.getVisibility() == View.VISIBLE)
   	   			mBatteryIconNumber.setColorFilter(ColorFilterMaker.changeColorAlpha(mNumberColorOne, .45f, 0f));
   		}
   		
   		// Charging
   		if (mLevel > mChargingLevelsTwo) {
   			mBatteryCharge.setColorFilter(ColorFilterMaker.changeColorAlpha(mChargingLevelsColorThree, .45f, 0f));
   		}
   		if (mLevel <= mChargingLevelsTwo) {
   			mBatteryCharge.setColorFilter(ColorFilterMaker.changeColorAlpha(mChargingLevelsColorTwo, .45f, 0f));
   		}
   		if (mLevel <= mChargingLevelsOne) {
   			mBatteryCharge.setColorFilter(ColorFilterMaker.changeColorAlpha(mChargingLevelsColorOne, .45f, 0f));
   		}
   		
   		// Depleted
   		if (mLevel > mDepletedLevelsTwo) {
   			if (!colorBaseIcon) mBatteryIcon.setColorFilter(ColorFilterMaker.changeColorAlpha(mDepletedLevelsColorThree, .45f, 0f));
   		}
   		if (mLevel <= mDepletedLevelsTwo) {
   			if (!colorBaseIcon) mBatteryIcon.setColorFilter(ColorFilterMaker.changeColorAlpha(mDepletedLevelsColorTwo, .45f, 0f));
   		}
   		if (mLevel <= mDepletedLevelsOne) {
   			if (!colorBaseIcon) mBatteryIcon.setColorFilter(ColorFilterMaker.changeColorAlpha(mDepletedLevelsColorOne, .45f, 0f));
   		}   	
   		
   		
   		
   		
    };
    
   
  
    
    private void getBatteryBar(){
    	
    	int levelWidth =  0;
    	int levelHeight =  0;
    	if (mBarPosition != mOldBarPosition) {
    		mBatteryBarOff.setVisibility(View.GONE);
    		mBatteryBar.setVisibility(View.GONE);
    		mOldBarPosition = mBarPosition;
    	}
    	
        switch (mBarPosition) {
        
        case BAR_NONE:;
        	mBatteryBar.setVisibility(View.GONE);
        	mBatteryBarOff.setVisibility(View.GONE);
            break;
            
        case BAR_BOTTOM:
        	
        	mBatteryBarOff = (ImageView) findViewById(R.id.battery_horiz_bar_bottom_off);
        	mBatteryBarOff.setBackgroundResource(R.drawable.junk_batt_bar);        	
        	mBatteryBarOff.setVisibility(View.VISIBLE);
        	
        	levelWidth = (int) ((mBatteryIcon.getWidth() * 100) / 100);
        	mBatteryBarOff.getLayoutParams().width = levelWidth;
        	mBatteryBarOff.getLayoutParams().height = mBarWidth;
        	mBatteryBarOff.requestLayout();
        	
        	mBatteryBar = (ImageView) findViewById(R.id.battery_horiz_bar_bottom);
        	mBatteryBar.setBackgroundResource(R.drawable.junk_batt_bar);
        	mBatteryBar.setVisibility(View.VISIBLE);
        	
        	levelWidth = (int) ((mBatteryIcon.getWidth() * mLevel) / 100);
        	mBatteryBar.getLayoutParams().width = levelWidth;
        	mBatteryBar.getLayoutParams().height = mBarWidth;
        	mBatteryBar.requestLayout();
        	setBarColors();
 
            break;

        case BAR_RIGHT:
       	
        	mBatteryBarOff = (ImageView) findViewById(R.id.battery_vert_bar_off);
        	mBatteryBarOff.setBackgroundResource(R.drawable.junk_batt_bar);        	
        	mBatteryBarOff.setVisibility(View.VISIBLE);
        	
        	levelHeight = (int) ((mBatteryIcon.getHeight() * 100) / 100);
        	mBatteryBarOff.getLayoutParams().height = levelHeight;
        	mBatteryBarOff.getLayoutParams().width = mBarWidth;       	
        	mBatteryBarOff.requestLayout();
        	
        	mBatteryBar = (ImageView) findViewById(R.id.battery_vert_bar_right);
        	mBatteryBar.setBackgroundResource(R.drawable.junk_batt_bar);
        	mBatteryBar.setVisibility(View.VISIBLE);
        	
        	levelHeight = (int) ((mBatteryIcon.getHeight() * mLevel) / 100);
        	mBatteryBar.getLayoutParams().height = levelHeight;
        	mBatteryBar.getLayoutParams().width = mBarWidth;
        	mBatteryBar.requestLayout();
        	setBarColors();
        	
            break;
            
       default:
    	   mBatteryBar.setVisibility(View.GONE);
    	   mBatteryBarOff.setVisibility(View.GONE);
 
            break;
        }        
        };

    
    
    private void setBarColors() {
 

   		if (mLevel > mBatteryLevelsTwo) {
   			mBatteryBar.getBackground().setColorFilter(ColorFilterMaker.changeColorAlpha(mBatteryLevelsColorThree, .5f, 0f));
   	    	mBatteryBarOff.getBackground().setColorFilter(ColorFilterMaker.changeColorAlpha(mDepletedLevelsColorThree, .5f, 0f));
   		}
   		if (mLevel <= mBatteryLevelsTwo) {
   			mBatteryBar.getBackground().setColorFilter(ColorFilterMaker.changeColorAlpha(mBatteryLevelsColorTwo, .5f, 0f));
   	    	mBatteryBarOff.getBackground().setColorFilter(ColorFilterMaker.changeColorAlpha(mDepletedLevelsColorTwo, .5f, 0f));
   		}
   		if (mLevel <= mBatteryLevelsOne) {
   			mBatteryBar.getBackground().setColorFilter(ColorFilterMaker.changeColorAlpha(mBatteryLevelsColorOne, .5f, 0f));
   	    	mBatteryBarOff.getBackground().setColorFilter(ColorFilterMaker.changeColorAlpha(mDepletedLevelsColorOne, .5f, 0f));
   		}
    };


}
