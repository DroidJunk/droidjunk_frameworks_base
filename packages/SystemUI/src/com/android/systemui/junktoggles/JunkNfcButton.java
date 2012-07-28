package com.android.systemui.junktoggles;

import com.android.systemui.R;
import com.android.systemui.junktoggles.JunkBrightnessButton.BrightnessModeObserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Handler;
import android.provider.Settings;





public class JunkNfcButton extends JunkToggleButton {

	private View showNfc;
	private View mIndicator;
	private ImageView mIcon;
	private View mDivider;
	private NfcAdapter mNFCAdapter;
	private IntentFilter mIntentFilter;

	

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED.equals(action)) {
                updateResources();
            }
        }
    };


	public JunkNfcButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}



	protected void onAttachedToWindow(){
		super.onAttachedToWindow();
		mIntentFilter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        mContext.registerReceiver(mReceiver, mIntentFilter);
        //mNFCAdapter = NfcAdapter.getDefaultAdapter(getContext());
        
        NfcManager manager = (NfcManager) getContext().getSystemService(Context.NFC_SERVICE);
        mNFCAdapter = manager.getDefaultAdapter();
        
		showNfc = (View) getRootView().findViewById(R.id.button_nfc);
		mIndicator = (View) getRootView().findViewById(R.id.indicator_nfc);
		mIcon = (ImageView) getRootView().findViewById(R.id.nfc_icon);
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
		mDivider = (View) getRootView().findViewById(R.id.divider_nfc);

		updateResources();
	}


	protected void onDetachedFromWindow(){

		
	}


	@Override
	protected boolean getStatusOn(){
		//mNFCAdapter = NfcAdapter.getDefaultAdapter(getContext());
		return (mNFCAdapter.isEnabled());

	}

	@Override
	void updateResources() {
		
		mIcon.clearColorFilter();
		
		
		if (mNFCAdapter.isEnabled()) {
				mIndicator.setBackgroundColor(JunkToggleViewTop.mToggleIndOnColor);
				mIcon.setImageResource(R.drawable.junktoggle_nfc_on);
				mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
				setTextColor(JunkToggleViewTop.mToggleTextOnColor);
			} else {
				mIcon.setImageResource(R.drawable.junktoggle_nfc_off);
				mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
				mIndicator.setBackgroundColor(JunkToggleViewTop.mToggleIndOffColor);
				setTextColor(JunkToggleViewTop.mToggleTextOffColor);
		}
		
		mDivider.setBackgroundColor(JunkToggleViewTop.mToggleDivColor);

		if (JunkToggleButton.mShowNfc) {
			showNfc.setVisibility(View.VISIBLE);
		} else {
			showNfc.setVisibility(View.GONE);
		}

		
	}


	@Override
	void toggleOn() {

		mNFCAdapter.enable();

		Intent i = new Intent();
	    i.setAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
	    getContext().sendBroadcast(i);
	    i = null;

		updateResources();
	}


	@Override
	void toggleOff() {

		mNFCAdapter.disable();

		Intent i = new Intent();
	    i.setAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
	    getContext().sendBroadcast(i);
	    i = null;

		updateResources();
	}


	@Override
	void showSettings() {

	    Intent i = new Intent();
	    i.setAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
	    getContext().sendBroadcast(i);
	    i.setAction("android.settings.AIRPLANE_MODE_SETTINGS");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
	}





}  //
