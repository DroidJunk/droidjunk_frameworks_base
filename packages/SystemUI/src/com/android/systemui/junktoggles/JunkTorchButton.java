package com.android.systemui.junktoggles;

import com.android.systemui.R;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.os.Handler;
import android.provider.Settings;







public class JunkTorchButton extends JunkToggleButton {


	private View showTorch;
	private View mIndicator;
	private ImageView mIcon;
	private View mDivider;
    private String INTENT_TORCH_ON = "com.android.systemui.INTENT_TORCH_ON";
    private String INTENT_TORCH_OFF = "com.android.systemui.INTENT_TORCH_OFF";
    private boolean mTorchOn = false;

	




	public JunkTorchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	// NetworkMode settings observer
	class NetworkModeObserver extends ContentObserver{

		public NetworkModeObserver(Handler handler) {
			super(handler);
		}

	    @Override
	    public void onChange(boolean selfChange){

	    	updateResources();
	    }
	}	



	protected void onAttachedToWindow(){
		super.onAttachedToWindow();

		showTorch = (View) getRootView().findViewById(R.id.button);
		mIndicator = (View) getRootView().findViewById(R.id.indicator);
		mIcon = (ImageView) getRootView().findViewById(R.id.torch_icon);
		mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
		mDivider = (View) getRootView().findViewById(R.id.divider);

		updateResources();
	}


	protected void onDetachedFromWindow(){
	}


	@Override
	protected boolean getStatusOn(){

		return mTorchOn;
	}

	@Override
	void updateResources() {
		
		mIcon.clearColorFilter();

		if (mTorchOn) {
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOnColor);
			mIcon.setImageResource(R.drawable.junktoggle_torch_on);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOnColor);
			setTextColor(JunkToggles.mToggleTextOnColor);
		} else {
			mIcon.setImageResource(R.drawable.junktoggle_torch_off);
			mIcon.setColorFilter(JunkToggleButton.mToggleIconOffColor);
			mIndicator.setBackgroundColor(JunkToggles.mToggleIndOffColor);
			setTextColor(JunkToggles.mToggleTextOffColor);
		}
		
		mDivider.setBackgroundColor(JunkToggles.mToggleDivColor);

		if (JunkToggleButton.mShowTorch) {
			showTorch.setVisibility(View.VISIBLE);
		} else {
			showTorch.setVisibility(View.GONE);
		}
	}


	@Override
	void toggleOn() {

		Intent i = new Intent(INTENT_TORCH_ON);
        i.setAction(INTENT_TORCH_ON);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startService(i);
        i = null;
        mTorchOn = true;
        
		updateResources();
	}


	@Override
	void toggleOff() {

		Intent i = new Intent(INTENT_TORCH_OFF);
        i.setAction(INTENT_TORCH_OFF);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startService(i);
        i = null;
        mTorchOn = false;
        
		updateResources();
	}


	@Override
	void showSettings() {

/*	    Intent i = new Intent();
	    i.setAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
	    getContext().sendBroadcast(i);
	    i.setAction("android.settings.DATA_ROAMING_SETTINGS");
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);*/
	}





}  //
