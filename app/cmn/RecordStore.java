package cmn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import models.Record;

public class RecordStore {
	
	private static Map<Long, Record> dataStore = new HashMap<Long, Record>();
	
	private static Lock lock = new ReentrantLock();
	
	private static AtomicLong counter = new AtomicLong();
	
	public static void addRecord(Record record) {
		lock.lock();
		try {
			record.id = counter.incrementAndGet();
			dataStore.put(record.id, record);
		} finally {
			lock.unlock();
		}
	}
	
	public static void deleteRecord(long id) {
		lock.lock();
		try {
			dataStore.remove(id);
		} finally {
			lock.unlock();
		}
	}
	
	public static List<Record> getAllRecords() {
		lock.lock();
		try {
			List<Record> dataSnap = new ArrayList<Record>();
			dataSnap.addAll(dataStore.values());
			return dataSnap;
		} finally {
			lock.unlock();
		}
	}
	
	public static Record getRecord(long id) {
		lock.lock();
		try {
			return dataStore.get(id);
		} finally {
			lock.unlock();
		}
	}

}
