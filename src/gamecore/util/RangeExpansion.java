package gamecore.util;

import java.util.Arrays;

/**
 * 范围扩展
 */
public final class RangeExpansion {

	private int absoluteMin;
	private int absoluteMax;

	private int upMin;
	private int upMax;

	private int downMin;
	private int downMax;

	private int step;

	public RangeExpansion(int initMin, int initMax, int absoluteMin, int absoluteMax, int step) {
		this.upMin = initMin;
		this.downMin = initMin;

		this.upMax = initMax;
		this.downMax = initMax;

		this.absoluteMin = absoluteMin;
		this.absoluteMax = absoluteMax;

		this.step = step;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	//刚开始的
	public int[] init() {

		int[] r = { upMin, upMax, 1 };

		return r;
	}

	//范围上升
	public int[] up() {

		this.upMin = upMax;
		this.upMax = upMax + step;

		if (this.upMax > absoluteMax) {
			upMax = absoluteMax;
		}

		int canContinue = upMin == upMax ? 0 : 1;

		int[] r = { upMin, upMax, canContinue };

		return r;
	}

	//范围上升
	public int[] down() {

		this.downMax = downMin;
		this.downMin = downMin - step;

		if (this.downMin < absoluteMin) {
			downMin = absoluteMin;
		}

		int canContinue = downMin == downMax ? 0 : 1;

		int[] r = { downMin, downMax, canContinue };

		return r;
	}

}
