package ua.chernov.taskmanager.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

		private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

		private static SimpleDateFormat dateFormatter;

		public static String formatDate(Date date) {
			if (dateFormatter == null)
				dateFormatter = new SimpleDateFormat(DATE_FORMAT);
			
			return dateFormatter.format(date);		
		}
		
		public static Date parse(final String dateStr) throws ParseException {
			if (dateFormatter == null)
				dateFormatter = new SimpleDateFormat(DATE_FORMAT);

			return dateFormatter.parse(dateStr);
		}
}
