import play.Application;
import play.GlobalSettings;
import play.Logger;


public class AppSettings extends GlobalSettings {
	
	public void onStart(Application app) {
        Logger.info("Application has started");
        System.out.println("Application has started :)");
    }

    public void onStop(Application app) {
        Logger.info("Application shutdown...");
        System.out.println("Application has stopped!");
    }

}
