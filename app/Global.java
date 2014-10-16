
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import core.CMSGuidedFormFill;
import play.Application;
import play.GlobalSettings;
import play.Logger;

public class Global extends GlobalSettings {
	@Override
	@SuppressWarnings("unused")
	public void onStart(Application app) {
		// set decisions file
		String os = System.getProperty("os.name");
		Path decisionsPath;
		if (os.toLowerCase().contains("win")) {
			String appData = System.getenv("APPDATA");
			decisionsPath = Paths.get(appData, "CMS Guided Forms", "decisions");
			Logger.info("Windows OS detected.");
		} else {
			decisionsPath = Paths.get("~/.local", "CMS Guided Forms", "decisions");
			Logger.info("Non-Windows OS detected, assuming *nix.");
		}
		File decisionsFile = decisionsPath.toFile();
		CMSGuidedFormFill.setDecisionsFile(decisionsFile);
		Logger.info("Decisions files will be stored at: " + decisionsFile);

		// ensure that decisions file exists in file system
		if (Files.notExists(decisionsPath, LinkOption.NOFOLLOW_LINKS)) {
			try {
				Logger.info("App data directory doesn't exist; It will be created.");
				Files.createDirectories(decisionsPath);
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
			}
		}
	}
}
