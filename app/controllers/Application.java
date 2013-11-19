package controllers;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

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
    	return ok("Hello there buoy!");
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
    
    public static Result signin() {
    	final Set<Map.Entry<String,String[]>> entries = request().queryString().entrySet();
    	StringBuilder builder = new StringBuilder();
    	builder.append("<ul>");
        for (Map.Entry<String,String[]> entry : entries) {
            final String key = entry.getKey();
            final String value = Arrays.toString(entry.getValue());
            builder.append("<li>")
            	.append(key).append("-")
            	.append(value).append("</li>");
            Logger.debug(key + " " + value);
        }
        builder.append("</ul>");
        Logger.debug(request().getQueryString("token"));
        Logger.debug(request().getQueryString("errorMessage"));
        Logger.debug(request().getQueryString("errorCode"));
    	return ok(builder.toString());
    }
    
}
