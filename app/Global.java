
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import core.CMSGuidedFormFill;
import core.forms.ChangeOrderForm;

public class Global extends GlobalSettings {
	@Override
	@SuppressWarnings("unused")
	public void onStart(Application app) {
		// set decisions file
		String os = System.getProperty("os.name");
		Path formDataPath;
		if (os.toLowerCase().contains("win")) {
			String appData = System.getenv("APPDATA");
			formDataPath = Paths.get(appData, "CMS Guided Forms", "form data");
			Logger.info("Windows OS detected.");
		} else {
			String userHome = System.getProperty("user.home");
			formDataPath = Paths.get(userHome, ".local", "CMS_Guided_Forms",
					"form_data");
			Logger.info("Non-Windows OS detected, assuming *nix.");
		}
		File formDataFile = formDataPath.toFile();
		CMSGuidedFormFill.setFormDataFile(formDataFile);
		Logger.info("Form data files will be stored at: " + formDataFile);

		// ensure that decisions file exists in file system
		if (Files.notExists(formDataPath, LinkOption.NOFOLLOW_LINKS)) {
			try {
				Logger.info("App data directory doesn't exist; It will be created.");
				Files.createDirectories(formDataPath);
				if (Files.notExists(formDataPath)) {
					Logger.error("Woops, the file wasn't be created for some "
							+ "reason. Bad stuff gonna happen!");
				} else {
					Logger.info("Path created successfully.");
				}
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
			}
		}
		Logger.info("Initialize Change Order Form");
		ChangeOrderForm.getInstance();

		Logger.info("Startup tasks complete, application ready.");
	}
}
