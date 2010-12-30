package de.devsurf.twiddns.tweets;

import java.io.IOException;

public interface Tweeter {
	String tweet() throws IOException;
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
