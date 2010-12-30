package de.devsurf.twiddns.tweets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.configuration.Configuration;
import de.devsurf.injection.guice.configuration.PathConfig;

@Bind(multiple=true)
@Configuration(name=@Named("urls"), location=@PathConfig("/urls.properties"))
public class URLPublisher extends WieIstMeineIP{
	private static final Logger LOGGER = Logger.getLogger(URLPublisher.class.getName());

	public static final String URL_CONFIG_COUNT = "url.count";
	public static final String URL_CONFIG_ELEMENT = "url.";
	
	private List<String> urls;
	
	@Inject
	public void init(@Named("urls") Properties configuration) {
		String filterCountStr = configuration.getProperty(URL_CONFIG_COUNT, "0");
		int count;
		try {
			count = Integer.parseInt(filterCountStr);
		} catch (NumberFormatException e) {
			LOGGER.log(Level.SEVERE, "The Count for the URLs couldn't be parsed.", e);
			count = 0;
		}

		urls = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			String url = configuration.getProperty(URL_CONFIG_ELEMENT + (i + 1));
			urls.add(url);
		}
	}
	
	public List<String> tweet() throws IOException {
		try {
			List<String> tweets = new ArrayList<String>();
			String ip = getOwnIP();
			for (String url : urls) {
				url = url.replace("${IP}", ip);
				tweets.add(url);
			}
			return tweets;
		} catch (IOException ioe) {
			LOGGER.log(Level.WARNING, ioe.getMessage(), ioe);
			throw ioe;
		}
	}
	

	@Override
	public CronFormat schedule() {
		return CronFormat.schedule("* * * * * 30 *");
	}
}
