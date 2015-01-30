package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.RadarKidsListViewAdapter;
import com.twinly.eyebb.model.Device;
import com.twinly.eyebb.model.SerializableDeviceMap;
import com.twinly.eyebb.service.AntiLostServiceBackup;

public class AntiLostFragmentBackup extends Fragment {
	private HashMap<String, Device> macaronHashMap;
	private ArrayList<Device> deviceList;
	private SerializableDeviceMap serializableMacaronMap;
	private ListView listView;
	private RadarKidsListViewAdapter mAdapter;
	private boolean isAntiLostOn = false;
	private boolean isSingleMode;

	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AntiLostServiceBackup.MSG_SET_VALUE:
				updateView(msg);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_anti_lost, container, false);
		listView = (ListView) v.findViewById(R.id.listView);
		deviceList = new ArrayList<Device>();
		mAdapter = new RadarKidsListViewAdapter(getActivity(), deviceList);
		listView.setAdapter(mAdapter);
		return v;
	}

	public void start(ArrayList<String> antiLostDeviceList) {
		if (antiLostDeviceList.size() > AntiLostServiceBackup.MAX_DUAL_MODE_SIZE) {
			isSingleMode = true;
		} else {
			isSingleMode = false;
		}
		Intent antiLostServiceIntent = new Intent();
		antiLostServiceIntent.setClass(getActivity(), AntiLostServiceBackup.class);
		antiLostServiceIntent.putStringArrayListExtra(
				AntiLostServiceBackup.EXTRA_DEVICE_LIST, antiLostDeviceList);
		getActivity().startService(antiLostServiceIntent);
		doBindService();
		isAntiLostOn = true;
	}

	public void stop() {
		isAntiLostOn = false;
		doUnbindService();
	}

	public void updateView() {
		if (isAntiLostOn) {
			if (mService == null)
				return;

			Message msg = Message.obtain(null,
					AntiLostServiceBackup.MSG_REGISTER_CLIENT);
			msg = Message.obtain(null, AntiLostServiceBackup.MSG_SET_VALUE,
					this.hashCode(), 0);
			try {
				mService.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void updateView(Message msg) {
		if (isAntiLostOn) {
			serializableMacaronMap = (SerializableDeviceMap) msg.getData().get(
					AntiLostServiceBackup.EXTRA_DEVICE_LIST);
			macaronHashMap = serializableMacaronMap.getMap();
			String macAddress;
			Iterator<String> it = macaronHashMap.keySet().iterator();
			deviceList.clear();
			while (it.hasNext()) {
				macAddress = it.next();
				if (isSingleMode) {
					if (System.currentTimeMillis()
							- macaronHashMap.get(macAddress)
									.getLastAppearTime() < RadarFragment.LOST_TIMEOUT) {
						macaronHashMap.get(macAddress).setMissed(false);
					} else {
						macaronHashMap.get(macAddress).setMissed(true);
					}
				}
				deviceList.add(macaronHashMap.get(macAddress));
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mService = new Messenger(service);

			// We want to monitor the service for as long as we are connected to it.
			try {
				Message msg = Message.obtain(null,
						AntiLostServiceBackup.MSG_REGISTER_CLIENT);
				msg.replyTo = mMessenger;
				mService.send(msg);

				// Give it some value as an example.
				msg = Message.obtain(null, AntiLostServiceBackup.MSG_SET_VALUE,
						this.hashCode(), 0);
				mService.send(msg);
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even
				// do anything with it; we can count on soon being
				// disconnected (and then reconnected if it can be restarted)
				// so there is no need to do anything here.
			}

		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
		}
	};

	void doBindService() {
		// Establish a connection with the service.  We use an explicit
		// class name because there is no reason to be able to let other
		// applications replace our component.
		getActivity().bindService(
				new Intent(getActivity(), AntiLostServiceBackup.class), mConnection,
				Context.BIND_ABOVE_CLIENT);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with
			// it, then now is the time to unregister.
			if (mService != null) {
				try {
					Message msg = Message.obtain(null,
							AntiLostServiceBackup.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mService.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service
					// has crashed.
				}
			}

			// Detach our existing connection.
			getActivity().unbindService(mConnection);
			mIsBound = false;
		}
	}
}
