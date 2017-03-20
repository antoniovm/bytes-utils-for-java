/*
 * Copyright (C) 2014 Loopin Software
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
package com.loopinsoftware.util.time;

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
