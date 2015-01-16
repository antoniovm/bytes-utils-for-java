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
	 * This enum classifies the push policy when inserting new data to the Queue
	 * 
	 * @author Antonio Vicente Martin
	 *
	 */
	public enum PushPolicy {
		PRESERVE_OLD_DATA, OVERWRITE_OLD_DATA;
	}

	private Semaphore freeSpaceAvailable;
	private Semaphore newDataAvailable;
	private int totalAmountPushedData;
	private PushPolicy blockingPolicy;

	public BlockingQueue(byte[] data, PushPolicy blockingPolicy) {
		super(data);
		init(blockingPolicy);
	}

	/**
	 * Initializes all fields
	 * 
	 * @param pushPolicy
	 *            The push policy this queue is coing to use
	 */
	private void init(PushPolicy pushPolicy) {
		this.freeSpaceAvailable = new Semaphore(1);
		this.newDataAvailable = new Semaphore(0);
		this.totalAmountPushedData = 0;
		this.blockingPolicy = pushPolicy;
	}

	public void push(byte[] src, int amountOfDataToRelease) {
		if (blockingPolicy == PushPolicy.PRESERVE_OLD_DATA) {
			try {
				freeSpaceAvailable.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.push(src);
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
			newDataAvailable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int bytesRemoved = super.pop(dst);
		if (blockingPolicy == PushPolicy.PRESERVE_OLD_DATA) {
			freeSpaceAvailable.release();
		}

		return bytesRemoved;
	}

}
