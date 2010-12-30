package de.devsurf.twiddns;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SingleTweeter implements Tweeter{
	private static final Logger LOGGER = Logger.getLogger(SingleTweeter.class.getName());
	
	public List<String> tweet() throws IOException {
		try {
			return Collections.singletonList(singleTweet());
		} catch (IOException ioe) {
			LOGGER.log(Level.WARNING, ioe.getMessage(), ioe);
			throw ioe;
		}
	}
	
	public abstract String singleTweet() throws IOException;

	@Override
	public CronFormat schedule() {
		return CronFormat.schedule("* * * * * 30 *");
	}
}
