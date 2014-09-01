package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import play.Logger;

/**
 * Handles the deletion of temporary files created by the system.
 * 
 * @author Aaron Cohn
 */
public class FileDeletionHandler {
	private static class DeleteFileCommand implements Runnable {
		File file;

		public DeleteFileCommand(File file) {
			this.file = file;
		}

		@Override
		public void run() {
			try {
				Path path = file.toPath();
				Files.delete(path);
				Logger.debug("File deleted successfully [" + path + "]");
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
				Logger.debug("File will be deleted on JVM exit.");
				file.deleteOnExit();
			}
		}
	}

	private ScheduledThreadPoolExecutor executor;
	private static FileDeletionHandler instance;

	public static FileDeletionHandler getInstance() {
		if (instance == null) {
			instance = new FileDeletionHandler();
		}
		return instance;
	}

	/**
	 * Deletes the file after a 5-second delay. I created this as a workaround
	 * when I ran into problems with deleting a file immediately after creating
	 * it. File was still in use, so couldn't delete it. Waiting 5 seconds seems
	 * to work well.
	 */
	public static void deleteFile(File file) {
		getInstance().executor.schedule(new DeleteFileCommand(file), 5,
				TimeUnit.SECONDS);
	}

	private FileDeletionHandler() {
		executor = new ScheduledThreadPoolExecutor(1);
	}
}
