package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import cmn.AccessTokenCache;
import cmn.FBManager;
import cmn.PostDataWrapper;
import cmn.RecordStore;
import cmn.UserProfile;
import models.Record;
import play.*;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.WS;
import play.mvc.Result;
import static play.libs.F.Function;
import static play.libs.F.Promise;

public class Application extends Controller {
	
	private static Form<Record> recordForm = Form.form(Record.class);
	
	public static final void addError(String errorHeader, String errorMsg) {
		if(errorHeader == null && errorMsg == null) {
			return;
		}
		flash("error-flag", "yes");
		if(errorHeader != null)
			flash("error-header", errorHeader);
		if(errorMsg != null)
			flash("error-msg", errorMsg);
	}

    public static Result index() {
    	Logger.debug("Value = " + Play.application().configuration().getString("user_perm"));
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
    
    public static Result finalposts() {
    	// Session check
    	String uid = session("uid");
    	if(uid == null) {
    		addError("No user logged in", "No valid Facebook user has logged in, Please first login using you Facebook account!");
    		return ok(home.render());
    	}
    	
    	// Go ahead do your stuff
    	Logger.debug("Working on user with id = " + uid);
    	final Map<String, String[]> postData = request().body().asFormUrlEncoded();
    	Logger.debug("Post Data keys = " + postData.keySet());
    	final Set<Map.Entry<String,String[]>> entries = postData.entrySet();
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
    	return ok(builder.toString());
    }
    
    public static Promise<Result> fbsignin() {
    	
    	Logger.debug("Invoked fbsignin");
    	
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
    	
    	final Map<String, String> accessAttrMap = new HashMap<String, String>();
    	
    	if(attrMap.containsKey("code")) {
    		Logger.debug("Received code = " + attrMap.get("code"));
    		
    		result = WS.url("https://graph.facebook.com/oauth/access_token")
    				.setQueryParameter("client_id", "169640416559253")
    				.setQueryParameter("redirect_uri", Play.application().configuration().getString("app_redirect_uri"))
    				.setQueryParameter("client_secret", "490f2388bb03e22ae33366fa64c9dbf5")
    				.setQueryParameter("code", attrMap.get("code")).get().flatMap(
    	            new Function<WS.Response, Promise<Result>>() {
    	                public Promise<Result> apply(WS.Response response) {
    	                	Logger.debug("Outer Response = " + response.getBody());
    	                	String[] tokens = response.getBody().split("&");
    	                	for(String token : tokens) {
    	                		String[] nvp = token.split("=");
    	                		accessAttrMap.put(nvp[0], nvp[1]);
    	                	}
    	                    return WS.url("https://graph.facebook.com/oauth/access_token")
    	                    		.setQueryParameter("client_id", "169640416559253")
    	                    		.setQueryParameter("client_secret", "490f2388bb03e22ae33366fa64c9dbf5")
    	                    		.setQueryParameter("grant_type", "client_credentials")
    	                    		.setQueryParameter("redirect_uri", Play.application().configuration().getString("app_redirect_uri"))
    	                    		.get().flatMap(
    	                            new Function<WS.Response, Promise<Result>>() {
    	                                public Promise<Result> apply(WS.Response response) {
    	                                	Logger.debug("Inner Response = " + response.getBody());
    	                                	String appAccessToken = response.getBody().split("=")[1];
    	                                	accessAttrMap.put("app_access_token", appAccessToken);
    	                                	Logger.debug("Access map = " + accessAttrMap);
    	                                	return WS.url("https://graph.facebook.com/debug_token")
    	                                		.setQueryParameter("input_token", accessAttrMap.get("access_token"))
    	                                		.setQueryParameter("access_token", accessAttrMap.get("app_access_token"))
    	                                		.setQueryParameter("redirect_uri", Play.application().configuration().getString("app_redirect_uri"))
    	                                		.get().map((
    	                                	            new Function<WS.Response, Result>() {
    	                            	                public Result apply(WS.Response response) {
    	                            	                	Logger.debug("Check Response = " + response.getBody());
    	                            	                	JsonNode json = response.asJson();
    	                            	                	JsonNode node = json.findValue("is_valid");
    	                            	                	JsonNode userIdNode = json.findValue("user_id");
    	                            	                	if(node != null && userIdNode != null) {
    	                            	                		Logger.debug("Node value = " + node + " - " + node.asText());
    	                            	                		if(Boolean.parseBoolean(node.asText())) {
    	                            	                			// token has been validated successfully, need to see permissions now
    	                            	                			Logger.debug("Token validated successfully");
    	                            	                			JsonNode scopeNode = json.findValue("scopes");
    	                            	                			List<String> permList = new ArrayList<String>();
    	                            	                			if(scopeNode != null) {
    	                            	                				Iterator<JsonNode> itr = scopeNode.elements();
    	                            	                				while(itr.hasNext())
    	                            	                					permList.add(itr.next().asText());
    	                            	                			}
    	                            	                			Logger.debug("Granted perm list = " + permList + ", size = " + permList.size());
    	                            	                			List<String> requestedPermissions = Arrays.asList(Play.application().configuration().getString("user_perm").split(","));
    	                            	                			Logger.debug("Requested perm list = " + requestedPermissions + ", size = " + requestedPermissions.size());
    	                            	                			if(permList.containsAll(requestedPermissions)) {
    	                            	                				// User has given us all permissions, its time to redirect user to new page
    	                            	                				Logger.debug("All permissions received");
    	                            	                				// Fetch posts, match them up
    	                            	                				AccessTokenCache.getInstance().addToken(userIdNode.asText(), accessAttrMap.get("access_token"));
    	                            	                				UserProfile user = FBManager.getInstance().fetchUserProfile(userIdNode.asText());
    	                            	                				PostDataWrapper wrapper = FBManager.getInstance().findNonBirthdayPosts(userIdNode.asText());
    	                            	                				session("uid", user.userId);
    	                            	                				return ok(userposts.render(wrapper.getNotMatched(), user, 
    	                            	                						wrapper.getTotalCount(), wrapper.getNotMatchedCount()));
    	                            	                			} else {
    	                            	                				// Not all permissions received
    	                            	                				session().remove("uid");
    	    	                            	                		addError("Inadequate permissions!", "In order to process all posts effectively, "
    	    	                            	                				+ "we need certain permssion temporarily on you facebook account, "
    	    	                            	                				+ "please provide those necessary permssions next time!");
    	    	                            	                		return ok(home.render());
    	                            	                			}
    	                            	                		}
    	                            	                	} else {
    	                            	                		Logger.debug("Failed to find is_valid");
    	                            	                		Logger.debug("Check Response = " + response.getBody());
    	                            	                		session().remove("uid");
    	                            	                		addError("Ooops something is not right!", "Seems like we have problem signing you in "
    	                            	                				+ "using Facebook account, Please try again later!");
    	                            	                		return ok(home.render());
    	                            	                	}
    	                            	                    return ok(landing.render());
    	                            	                }
    	                            	            }
    	                            	    ));
    	                                }
    	                            }
    	                    );
    	                }
    	            }
    	    );
    	    return result;
        }
    	return result;
    }
    
    public static Result landing() {
    	session("uid", "aisdasdahslkhdalkjsd");
    	return ok(landing.render());
    }
    
    public static Result home() {
    	return ok(home.render());
    }
    
    public static WebSocket<String> datastream(final String uid) {
    	System.out.println("Datastream for " + uid);
    	return new WebSocket<String>() {

            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {

                // For each event received on the socket,
                in.onMessage(new Callback<String>() {
                    public void invoke(String event) {

                        // Log events to the console
                        System.out.println(event);

                    }
                });

                // When the socket is closed.
                in.onClose(new Callback0() {
                    public void invoke() {

                        System.out.println("Disconnected");

                    }
                });

                try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                // Send a single 'Hello!' message
                out.write("Getting posts for " + uid);
                try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                out.write("Detecting birthday posts for " + uid);
                try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                out.write("Sending back response for " + uid);
                out.close();
            }

        };
    }
    
}
