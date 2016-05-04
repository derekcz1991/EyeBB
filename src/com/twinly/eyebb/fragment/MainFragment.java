package com.twinly.eyebb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.dialog.IndoorLocatorOptionsDialog;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class MainFragment extends Fragment implements
		LocatorFragment.CallbackInterface, OnClickListener {
	private IndoorLocatorFragment indoorLocatorFragment;
	private GroupLocatorFragment groupLocatorFragment;
	private CallbackInterface callback;

	private String userType;
	private boolean isViewAllRooms = false;
	private boolean isAutoUpdateOn;

	public interface CallbackInterface {
		/**
		 * Update the progressBar value when pull the listView
		 * 
		 * @param value
		 *            current progress
		 */
		public void updateProgressBarForLocator(int value);

		/**
		 * Cancel update the progressBar when release the listView
		 */
		public void cancelProgressBar();

		/**
		 * Reset the progressBar when finishing to update listView
		 */
		public void resetProgressBar();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		userType = SharePrefsUtils.getUserType(getActivity());

		FragmentTransaction fragmentTransaction = getChildFragmentManager()
				.beginTransaction();
		indoorLocatorFragment = new IndoorLocatorFragment();
		indoorLocatorFragment.setCallbackInterface(this);
		indoorLocatorFragment.setInTheFront(true);
		fragmentTransaction.add(R.id.containerLayout, indoorLocatorFragment);
		if (userType.equals("T")) {
			groupLocatorFragment = new GroupLocatorFragment();
			groupLocatorFragment.setCallbackInterface(this);
			groupLocatorFragment.setInTheFront(false);
			fragmentTransaction.add(R.id.containerLayout, groupLocatorFragment)
					.hide(groupLocatorFragment);
		}
		fragmentTransaction.commit();

		v.findViewById(R.id.kidslistBtn).setOnClickListener(this);
		v.findViewById(R.id.optionBtn).setOnClickListener(this);
		return v;
	}

	public void setCallbackInterface(CallbackInterface callback) {
		this.callback = callback;
	}

	public void updateView() {
		if (userType.equals("T")) {
			if (indoorLocatorFragment.isVisible()) {
				indoorLocatorFragment.updateView();
			} else if (groupLocatorFragment.isVisible()) {
				groupLocatorFragment.updateView();
			}

		} else {
			indoorLocatorFragment.updateView();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.kidslistBtn:
			if (indoorLocatorFragment.isInTheFront()) {
				indoorLocatorFragment.showKidsListActivity();
			} else if (groupLocatorFragment.isInTheFront()) {
				groupLocatorFragment.showKidsListActivity();
			}
			break;
		case R.id.optionBtn:
			Intent intent = new Intent(getActivity(),
					IndoorLocatorOptionsDialog.class);
			intent.putExtra(IndoorLocatorOptionsDialog.EXTRA_VIEW_ALL_ROOMS,
					isViewAllRooms);
			intent.putExtra(IndoorLocatorOptionsDialog.EXTRA_AUTO_REFRESH,
					isAutoUpdateOn);
			startActivityForResult(intent,
					ActivityConstants.REQUEST_GO_TO_OPTIONS_DIALOG);
			break;
		default:
			break;
		}
	}

	@Override
	public void updateProgressBarForLocator(int value) {
		if (callback != null)
			callback.updateProgressBarForLocator(value);

	}

	@Override
	public void cancelProgressBar() {
		callback.cancelProgressBar();
	}

	@Override
	public void resetProgressBar() {
		callback.resetProgressBar();
	}

	@Override
	public void switchFragment() {
		FragmentTransaction fragmentTransaction = getChildFragmentManager()
				.beginTransaction();
		if (userType.equals("T")) {
			if (indoorLocatorFragment.isInTheFront()) {
				fragmentTransaction.show(groupLocatorFragment)
						.hide(indoorLocatorFragment).commit();
				indoorLocatorFragment.setInTheFront(false);
				groupLocatorFragment.setInTheFront(true);
			} else if (groupLocatorFragment.isInTheFront()) {
				fragmentTransaction.show(indoorLocatorFragment)
						.hide(groupLocatorFragment).commit();
				indoorLocatorFragment.setInTheFront(true);
				groupLocatorFragment.setInTheFront(false);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityConstants.REQUEST_GO_TO_OPTIONS_DIALOG) {
			if (resultCode == ActivityConstants.RESULT_RESULT_OK) {
				if (isAutoUpdateOn != data.getBooleanExtra(
						IndoorLocatorOptionsDialog.EXTRA_AUTO_REFRESH,
						isAutoUpdateOn)) {
					isAutoUpdateOn = data.getBooleanExtra(
							IndoorLocatorOptionsDialog.EXTRA_AUTO_REFRESH,
							isAutoUpdateOn);
					indoorLocatorFragment.switchAutoUpdate(isAutoUpdateOn);
					if (groupLocatorFragment != null) {
						groupLocatorFragment.switchAutoUpdate(isAutoUpdateOn);
					}
				}

				if (isViewAllRooms != data.getBooleanExtra(
						IndoorLocatorOptionsDialog.EXTRA_VIEW_ALL_ROOMS,
						isViewAllRooms)) {
					isViewAllRooms = !isViewAllRooms;
					indoorLocatorFragment.switchViewAllRooms(isViewAllRooms);
					if (groupLocatorFragment != null) {
						indoorLocatorFragment
								.switchViewAllRooms(isViewAllRooms);
					}
				}
			}
		}
	}

}
