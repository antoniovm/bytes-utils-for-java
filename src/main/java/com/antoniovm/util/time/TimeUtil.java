package com.antoniovm.util.time;

/**
 * This class is an utility to measure time.
 * 
 * Created by Antonio Vicente Martin on 17/04/15.
 */
public class TimeUtil {

	private long before;

	/**
	 * Creates a new TimerUtil
	 */
	public TimeUtil() {
		reset();
	}

	/**
	 * Starts measuring
	 */
	public void start() {
		this.before = System.currentTimeMillis();
	}

	/**
	 * Stops and returns the elapsed time between start() and stop()
	 * 
	 * @return the time elapsed time between start() and stop()
	 */
	public long stop() {
		long after = System.currentTimeMillis();
		long time = this.before < 0 ? -1 : after - this.before;
		reset();
		return time;
	}

	/**
	 * Resets the time util object
	 */
	public void reset() {
		this.before = -1;
	}

	/**
	 * 
	 * Returns the elapsed time by the measurable argument
	 *
	 * @param measurable
	 *            The measurable object
	 * @return The time elapsed
	 */
	public long measure(Measurable m) {
		start();

		m.measure();

		return stop();
	}

}
