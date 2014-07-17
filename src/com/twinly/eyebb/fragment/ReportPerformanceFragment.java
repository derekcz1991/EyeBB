package com.twinly.eyebb.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.adapter.PerformanceListViewAdapter;
import com.twinly.eyebb.model.PerformanceListItem;

public class ReportPerformanceFragment extends Fragment {

	private ListView dailyListView;

	public ReportPerformanceFragment() {
		System.out.println("ReportPerformanceFragment constructor");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_report_performance,
				container, false);
		dailyListView = (ListView) v.findViewById(R.id.listView);

		dailyListView.setAdapter(getAdapter());
		return v;
	}

	public void updateView() {
		System.out.println("updateView");
		dailyListView.setAdapter(getAdapter());
	}

	private PerformanceListViewAdapter getAdapter() {
		List<PerformanceListItem> list = new ArrayList<PerformanceListItem>();

		PerformanceListItem dailyTitle = new PerformanceListItem(getResources()
				.getString(R.string.text_daily), "",
				R.drawable.bg_report_daily, -1, 0, 0, 0);
		list.add(dailyTitle);
		PerformanceListItem dailyMessHall = new PerformanceListItem("",
				getResources().getString(R.string.text_mess_hall), -1,
				R.drawable.my_progress_green01, 220, 220, 800);
		list.add(dailyMessHall);
		PerformanceListItem dailyPlayground = new PerformanceListItem("",
				getResources().getString(R.string.text_playground), -1,
				R.drawable.my_progress_yellow, 360, 360, 800);
		list.add(dailyPlayground);
		PerformanceListItem dailySleeping = new PerformanceListItem("",
				getResources().getString(R.string.text_sleeping), -1,
				R.drawable.my_progress_blue01, 20, 20, 800);
		list.add(dailySleeping);
		//		PerformanceListItem dailyMusicRoom = new PerformanceListItem("",
		//				getResources().getString(R.string.text_music_room), -1,
		//				R.drawable.my_progress_red, 440, 440, 800);
		//		list.add(dailyMusicRoom);
		//		PerformanceListItem dailyClassRoom2A = new PerformanceListItem("",
		//				getResources().getString(R.string.text_class_room_2a), -1,
		//				R.drawable.my_progress_blue02, 300, 300, 800);
		//		list.add(dailyClassRoom2A);
		//		PerformanceListItem dailyComputerRoom = new PerformanceListItem("",
		//				getResources().getString(R.string.text_computer_room), -1,
		//				R.drawable.my_progress_pink, 220, 220, 800);
		//		list.add(dailyComputerRoom);
		//		PerformanceListItem dailyArtRoom = new PerformanceListItem("",
		//				getResources().getString(R.string.text_art_room), -1,
		//				R.drawable.my_progress_green02, 220, 220, 800);
		//		list.add(dailyArtRoom);

		PerformanceListItem weeklyTitle = new PerformanceListItem(
				getResources().getString(R.string.text_weekly), "",
				R.drawable.bg_report_weekly, -1, 0, 0, 0);
		list.add(weeklyTitle);
		PerformanceListItem weeklyMessHall = new PerformanceListItem("",
				getResources().getString(R.string.text_mess_hall), -1,
				R.drawable.my_progress_green01, 220, 220, 800);
		list.add(weeklyMessHall);
		PerformanceListItem weeklyPlayground = new PerformanceListItem("",
				getResources().getString(R.string.text_playground), -1,
				R.drawable.my_progress_yellow, 360, 360, 800);
		list.add(weeklyPlayground);
		PerformanceListItem weeklySleeping = new PerformanceListItem("",
				getResources().getString(R.string.text_sleeping), -1,
				R.drawable.my_progress_blue01, 20, 20, 800);
		list.add(weeklySleeping);
		//		PerformanceListItem weeklyMusicRoom = new PerformanceListItem("",
		//				getResources().getString(R.string.text_music_room), -1,
		//				R.drawable.my_progress_red, 440, 440, 800);
		//		list.add(weeklyMusicRoom);
		//		PerformanceListItem weeklyClassRoom2A = new PerformanceListItem("",
		//				getResources().getString(R.string.text_class_room_2a), -1,
		//				R.drawable.my_progress_blue02, 300, 300, 800);
		//		list.add(weeklyClassRoom2A);
		//		PerformanceListItem weeklyComputerRoom = new PerformanceListItem("",
		//				getResources().getString(R.string.text_computer_room), -1,
		//				R.drawable.my_progress_pink, 220, 220, 800);
		//		list.add(weeklyComputerRoom);
		//		PerformanceListItem weeklyArtRoom = new PerformanceListItem("",
		//				getResources().getString(R.string.text_art_room), -1,
		//				R.drawable.my_progress_green02, 220, 220, 800);
		//		list.add(weeklyArtRoom);

		PerformanceListViewAdapter adapter = new PerformanceListViewAdapter(
				getActivity(), list);
		return adapter;
	}
}
