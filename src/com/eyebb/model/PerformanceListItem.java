package com.eyebb.model;

public class PerformanceListItem {
	private String title;
	private String subTitle;
	private int titleBackground;
	private int progressBarstyle;
	private int time;
	private int progress;
	private int maxProgress;
	private boolean flag;

	public PerformanceListItem(String title, String subTitle,
			int titleBackground, int progressBarstyle, int time, int progress,
			int maxProgress) {
		super();
		this.title = title;
		this.subTitle = subTitle;
		this.titleBackground = titleBackground;
		this.progressBarstyle = progressBarstyle;
		this.time = time;
		this.progress = progress;
		this.maxProgress = maxProgress;
	}

	public String getTitle() {
		return title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public int getTitleBackground() {
		return titleBackground;
	}

	public int getProgressBarstyle() {
		return progressBarstyle;
	}

	public int getTime() {
		return time;
	}

	public int getProgress() {
		return progress;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
