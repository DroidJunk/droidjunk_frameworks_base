/*
 * Copyright (C) 2008 The Android Open Source Project
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
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import com.android.systemui.R;

import java.util.Date;

public final class DateViewHeader extends TextView {
    private static final String TAG = "DateView";

    private boolean mAttachedToWindow;
    private boolean mWindowVisible;
    private boolean mUpdating;
    // Junk
	private final String Junk_Pulldown_Settings = "JUNK_PULLDOWN_SETTINGS";
	private final String HEADER_DATE_SHOW = "header_date_show";
	private final String HEADER_DATE_COLOR = "header_date_color";
	private final String HEADER_DATE_SIZE = "header_date_size";

	private SharedPreferences sp;
	private boolean mHeaderDateShow = true;
	private int mHeaderDateColor = 0xffffffff;
    private int mHeaderDateSize = 10;
    //End Junk
    
    
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_TIME_TICK.equals(action)
                    || Intent.ACTION_TIME_CHANGED.equals(action)
                    || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
                // Junk
            	if (mHeaderDateShow) updateClock();
            	// End Junk
            }
            // Junk
            if (action.equals(Junk_Pulldown_Settings)) {
            	mHeaderDateShow = intent.getBooleanExtra(HEADER_DATE_SHOW, mHeaderDateShow);
            	mHeaderDateColor = intent.getIntExtra(HEADER_DATE_COLOR, mHeaderDateColor);	
            	mHeaderDateSize = intent.getIntExtra(HEADER_DATE_SIZE, mHeaderDateSize);
            	setVisibility(mHeaderDateShow ? VISIBLE : GONE);
            	if (mHeaderDateShow) updateClock();
            }
            // End Junk
        }
    };

    public DateViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        // Junk
  		Context settingsContext = getContext();
		try {
			settingsContext = getContext().createPackageContext("com.junk.settings",0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
 		
		sp = settingsContext.getSharedPreferences("Junk_Settings", Context.MODE_WORLD_READABLE);
		mHeaderDateShow = sp.getBoolean(HEADER_DATE_SHOW, mHeaderDateShow);
		mHeaderDateColor = sp.getInt(HEADER_DATE_COLOR, mHeaderDateColor);
		mHeaderDateSize = sp.getInt(HEADER_DATE_SIZE, mHeaderDateSize);
		setVisibility(mHeaderDateShow ? VISIBLE : GONE);
        // End Junk
        setUpdates();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
        setUpdates();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mWindowVisible = visibility == VISIBLE;
        setUpdates();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        setUpdates();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        // makes the large background bitmap not force us to full width
        return 0;
    }

    private final void updateClock() {
        final Context context = getContext();
        Date now = new Date();
        CharSequence dow = DateFormat.format("EEEE", now);
        CharSequence date = DateFormat.getLongDateFormat(context).format(now);
        setText(context.getString(R.string.status_bar_date_formatter, dow, date));
        // Junk
        setTextColor(mHeaderDateColor);
        setTextSize(mHeaderDateSize);
        // End Junk
    }

    private boolean isVisible() {
        View v = this;
        while (true) {
            if (v.getVisibility() != VISIBLE) {
                return false;
            }
            final ViewParent parent = v.getParent();
            if (parent instanceof View) {
                v = (View)parent;
            } else {
                return true;
            }
        }
    }

    private void setUpdates() {
        boolean update = mAttachedToWindow && mWindowVisible;
        if (update != mUpdating) {
            mUpdating = update;
            if (update) {
                // Register for Intent broadcasts for the clock and battery
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_TIME_TICK);
                filter.addAction(Intent.ACTION_TIME_CHANGED);
                filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
                // Junk
                filter.addAction(Junk_Pulldown_Settings);
                // End Junk
                mContext.registerReceiver(mIntentReceiver, filter, null, null);
                updateClock();
            } else {
                mContext.unregisterReceiver(mIntentReceiver);
            }
        }
    }
}
