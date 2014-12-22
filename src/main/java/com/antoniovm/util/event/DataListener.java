package com.antoniovm.util.event;

/**
 * This interaface will be the responsible of handle data events
 * 
 * @author Antonio Vicente Martin
 *
 */
public interface DataListener {

	/**
	 * Will be called when the data structure gets empty
	 */
	public void onFull();

	/**
	 * Will be called when the data structure gets empty
	 */
	public void onEmpty();

}
