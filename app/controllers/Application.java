package controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cmn.RecordStore;
import models.Record;
import play.*;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import play.libs.F;
import play.libs.WS;
import play.mvc.Result;
import static play.libs.F.Function;
import static play.libs.F.Promise;

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
    
    public static Promise<Result> fbsignin() {
    	final Set<Map.Entry<String,String[]>> entries = request().queryString().entrySet();
    	Map<String, String> attrMap = new HashMap<String, String>();
    	
    	for (Map.Entry<String,String[]> entry : entries) {
            final String key = entry.getKey();
            final String value = entry.getValue()[0];
            Logger.debug("Incoming parameter = " + key + ":" + value);
            attrMap.put(key, value);
        }
    	
    	Logger.debug("Attribute map = " + attrMap);
    	
    	Promise<Result> result = null;
    	
    	if(attrMap.containsKey("code")) {
    		Logger.debug("Received code = " + attrMap.get("code"));
    		
    		result = WS.url("https://graph.facebook.com/oauth/access_token")
    				.setQueryParameter("client_id", "169640416559253")
    				.setQueryParameter("redirect_uri", "http://play-tech-demo.herokuapp.com/fbsignin")
    				.setQueryParameter("client_secret", "490f2388bb03e22ae33366fa64c9dbf5")
    				.setQueryParameter("code", attrMap.get("code")).get().flatMap(
    	            new Function<WS.Response, Promise<Result>>() {
    	                public Promise<Result> apply(WS.Response response) {
    	                	Logger.debug("Outer Response = " + response.getBody());
    	                    return WS.url("https://graph.facebook.com/oauth/access_token")
    	                    		.setQueryParameter("client_id", "169640416559253")
    	                    		.setQueryParameter("client_secret", "490f2388bb03e22ae33366fa64c9dbf5")
    	                    		.setQueryParameter("grant_type", "client_credentials")
    	                    		.setQueryParameter("redirect_uri", "http://play-tech-demo.herokuapp.com/fbsignin")
    	                    		.get().map(
    	                            new Function<WS.Response, Result>() {
    	                                public Result apply(WS.Response response) {
    	                                	Logger.debug("Inner Response = " + response.getBody());
    	                                    return ok(landing.render());
    	                                }
    	                            }
    	                    );
    	                }
    	            }
    	    );
    	    return result;
    		
//        	// redirect to another link
//    		Promise<WS.Response> confirmId = WS.url("https://graph.facebook.com/oauth/access_token")
//    				.setQueryParameter("client_id", "169640416559253")
//    				.setQueryParameter("redirect_uri", "http://play-tech-demo.herokuapp.com/fbsignin")
//    				.setQueryParameter("client_secret", "490f2388bb03e22ae33366fa64c9dbf5")
//    				.setQueryParameter("code", attrMap.get("code")).get();
//    		result = confirmId.map((
//    	            new Function<WS.Response, Result>() {
//    	                public Result apply(WS.Response response) {
//    	                	Logger.debug("Response = " + response.getBody());
//    	                    return ok(landing.render());
//    	                }
//    	            }
//    	    ));
        }
    	return result;
    }
    
    public static Result landing() {
    	return ok(landing.render());
    }
    
}
