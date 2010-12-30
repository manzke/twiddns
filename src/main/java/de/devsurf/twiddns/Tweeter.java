package de.devsurf.twiddns;

import java.io.IOException;
import java.util.List;

public interface Tweeter {
	List<String> tweet() throws IOException;
	CronFormat schedule();
	
	public static class CronFormat{
		protected String schedule;
		
		public static CronFormat schedule(String cronLikeSchedule){
			CronFormat cron = new CronFormat();
			cron.schedule = cronLikeSchedule;
			return cron;
		}
	}
}
