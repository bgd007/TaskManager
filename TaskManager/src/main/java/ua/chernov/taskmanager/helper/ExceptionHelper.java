package ua.chernov.taskmanager.helper;

public class ExceptionHelper {
	public static String getLastNotNullMessage(Throwable e) {
		String message = e.getMessage();
		if (message == null) {
			if (e.getCause() != null) {
				message = getLastNotNullMessage(e.getCause());
			}
		}

		return message;
	}
}
