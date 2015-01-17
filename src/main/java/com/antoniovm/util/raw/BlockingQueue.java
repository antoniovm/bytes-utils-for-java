package com.antoniovm.util.raw;

import java.util.concurrent.Semaphore;

/**
 * This class represents a BlockingQueue to handle concurrent threads that can
 * access to stored raw data in this Queue
 * 
 * @author Antonio Vicente Martin
 *
 */
public class BlockingQueue extends Queue {

	/**
	 * This enum classifies the push policy when inserting new data into the
	 * Queue
	 * 
	 * @author Antonio Vicente Martin
	 *
	 */
	public enum PushPolicy {
		PRESERVE_OLD_DATA, OVERWRITE_OLD_DATA;
	}

	private PushPolicy blockingPolicy;

	private Semaphore freeSpaceAvailable;
	private Semaphore newDataAvailable;

	private int totalAmountPushedData;
	private int amountOfDataToRelease;

	/**
	 * Builds a new BlockingQueue
	 * 
	 * @param data
	 *            The buffer to store the data
	 * @param blockingPolicy
	 *            The push policy
	 * @param amountOfDataToRelease
	 *            The minimum amount of data to release the data available
	 *            semaphore
	 */
	public BlockingQueue(byte[] data, PushPolicy blockingPolicy, int amountOfDataToRelease) {
		super(data);
		init(blockingPolicy, amountOfDataToRelease);
	}

	/**
	 * Initializes all fields
	 * 
	 * @param pushPolicy
	 *            The push policy this queue is coing to use
	 * @param amountOfDataToRelease
	 *            The minimum amount of data to release the data available
	 *            semaphore
	 */
	private void init(PushPolicy pushPolicy, int amountOfDataToRelease) {
		this.freeSpaceAvailable = new Semaphore(1);
		this.newDataAvailable = new Semaphore(0);
		this.totalAmountPushedData = 0;
		this.blockingPolicy = pushPolicy;
		this.amountOfDataToRelease = amountOfDataToRelease;
	}

	/**
	 * A synced version of Queue.push()
	 * 
	 * @param src
	 *            The data to push
	 */
	public void push(byte[] src) {
		if (blockingPolicy == PushPolicy.PRESERVE_OLD_DATA) {
			try {
				freeSpaceAvailable.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		synchronized (this) {
			super.push(src);
		}

		releaseWhenEnoughDataAvailable(src.length, amountOfDataToRelease);

	}

	/**
	 * Releases the newDataAvailable semaphore when there is stored enough data
	 * 
	 * @param pushedData
	 *            The amount of data that is being pushed
	 * @param amountOfDataToRelease
	 *            The min amount of data to release the semaphore
	 * @return true if the semaphore was released, false otherwise
	 */
	private boolean releaseWhenEnoughDataAvailable(int pushedData, int amountOfDataToRelease) {

		if ((amountOfDataToRelease < totalAmountPushedData) || (amountOfDataToRelease < getSize())) {
			totalAmountPushedData += amountOfDataToRelease;
			return false;
		}
		newDataAvailable.release();
		return true;

	}

	@Override
	public int pop(byte[] dst) {
		try {
			// Block semaphore to guarantee safe data reading
			newDataAvailable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int bytesRemoved = 0;

		synchronized (this) {
			bytesRemoved = super.pop(dst);
		}

		// If PRESERVE_OLD_DATA is choosen, then free locked treads
		if (blockingPolicy == PushPolicy.PRESERVE_OLD_DATA) {
			freeSpaceAvailable.release();
		}

		return bytesRemoved;
	}

}
