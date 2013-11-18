package controllers;

import cmn.RecordStore;
import models.Record;
import play.*;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
	
	private static Form<Record> recordForm = Form.form(Record.class);

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    public static Result pappu() {
    	return ok("Hello there!");
    }
    
    public static Result record() {
    	return ok(views.html.record.render(Record.all(), recordForm));
    }
    
    public static Result newRecord() {
    	Form<Record> filledForm = recordForm.bindFromRequest();
    	if(filledForm.hasErrors()) {
    		return badRequest(record.render(Record.all(), filledForm));
    	} else {
    		Record.create(filledForm.get());
    		return redirect(routes.Application.record());
    	}
    }
    
    public static Result delrecord(Long id) {
    	RecordStore.deleteRecord(id);
    	return redirect(routes.Application.record());
    }

}
