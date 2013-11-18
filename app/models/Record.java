package models;

import java.util.ArrayList;
import java.util.List;

import cmn.RecordStore;
import play.data.validation.Constraints.Required;

public class Record {
	
	public Long id;
	
	@Required
	public String data;
	
	public static List<Record> all() {
		return RecordStore.getAllRecords();
	}
	
	public static void create(Record newRecord) {
		RecordStore.addRecord(newRecord);
	}
	
	public static void delete(Long id) {
		RecordStore.deleteRecord(id);
	}

}
