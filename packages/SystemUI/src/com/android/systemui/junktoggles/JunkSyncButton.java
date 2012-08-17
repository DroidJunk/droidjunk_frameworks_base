package com.android.systemui.junktoggles;



import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncStatusObserver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.systemui.R;
import com.android.systemui.junktoggles.JunkGpsButton.GpsObserver;


public class JunkSyncButton extends JunkToggleButton {

	private View showSync;
	private View mIndicator;
	private ImageView mIcon;
	private View mDivider;
	private BroadcastReceiver mBroadcastReciver;
	private Cursor syncCursor;
	Handler mHandler = new Handler();



	private SyncStatusObserver syncStatusObserver = new SyncStatusObserver() {

	    @Override
	    public void onStatusChanged(int which) {
            mHandler.post(new Runnable() {
                public void run() {
                	updateResources();;
                }
            });


	    }
	};





	public JunkSyncButton(Context context, AttributeSet attrs) {
		super(context, attrs);

	}






	protected void onAttachedToWindow(){
		super.onAttachedToWindow();

		showSync = (View) getRootView().findViewById(R.id.button_8);
		mIndicator = (View) getRootView().findViewById(R.id.indicator_8);
		mIcon = (ImageView) getRootView().findViewById(R.id.sync_icon);	
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
		mDivider = (View) getRootView().findViewById(R.id.divider_8);

		ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS , syncStatusObserver);

		updateResources();
	}


	protected void onDetachedFromWindow(){

		ContentResolver.removeStatusChangeListener(syncStatusObserver);

	}


	@Override
	protected boolean getStatusOn(){

		return ContentResolver.getMasterSyncAutomatically();

	}

	@Override
	void updateResources() {

		mIcon.clearColorFilter();
		
		if (ContentResolver.getMasterSyncAutomatically()) {
			mIcon.setImageResource(R.drawable.junktoggle_sync_on);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOnColor);
			setTextColor(JunkToggles.mToggleTextOnColor);

		} else {
			mIcon.setImageResource(R.drawable.junktoggle_sync_off);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOffColor);
			setTextColor(JunkToggles.mToggleTextOffColor);
		}

		mDivider.setBackgroundColor(JunkToggles.mToggleDivColor);
		
		if (JunkToggleButton.mShowSync) {
			showSync.setVisibility(View.VISIBLE);
		} else {
			showSync.setVisibility(View.GONE);
		}
	}



	@Override
	void toggleOn() {

		ContentResolver.setMasterSyncAutomatically(true);
		updateResources();

	}


	@Override
	void toggleOff() {

		ContentResolver.setMasterSyncAutomatically(false);
		updateResources();
	}


	@Override
	void showSettings() {

	    Intent i = new Intent();
	    i.setAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
	    getContext().sendBroadcast(i);
	    i.setAction("android.settings.SYNC_SETTINGS");
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
	}





}  // 
