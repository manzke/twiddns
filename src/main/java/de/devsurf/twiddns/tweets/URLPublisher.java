package de.devsurf.twiddns.tweets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.configuration.Configuration;
import de.devsurf.injection.guice.configuration.PathConfig;
import de.devsurf.injection.guice.configuration.Configuration.Type;

@Bind(multiple=true)
@Configuration(name=@Named("urls"), location=@PathConfig("/urls.properties"), alternative=@PathConfig("/urls.override.properties"), type=Type.BOTH)
public class URLPublisher extends WieIstMeineIP{
	private static final Logger LOGGER = Logger.getLogger(URLPublisher.class.getName());

	public static final String URL_CONFIG_COUNT = "url.count";
	public static final String URL_CONFIG_ELEMENT = "url.";
	
	@Inject(optional=true) @Named("bitly.username")
	private String username;
	@Inject(optional=true) @Named("bitly.apikey")
	private String apiKey;
	@Inject(optional=true) @Named("url.shorten")
	private boolean shorten;
	
	private List<String> urls;
	
	@Inject
	public void init(@Named("urls") Properties configuration) throws IOException {
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
				if(shorten){
					url = shorten(url);
				}
				tweets.add(url);
			}
			return tweets;
		} catch (IOException ioe) {
			LOGGER.log(Level.WARNING, ioe.getMessage(), ioe);
			throw ioe;
		}
	}
	
	protected String shorten(String url) throws IOException {
		HttpClient client = new HttpClient();
		GetMethod gm = new GetMethod("http://api.bit.ly/v3/shorten");
		gm.setQueryString("login="+username+"&apiKey="+apiKey+"&longUrl="+url+"&format=txt");

		int status = client.executeMethod(gm);
		if (status == HttpStatus.SC_OK) {
			String html = gm.getResponseBodyAsString();

			return html;
		} else {
			throw new IOException("wieistmeineip.de has returned a wrong status: " + status);
		}
	}

	@Override
	public CronFormat schedule() {
		return CronFormat.schedule("* * * * * 30 *");
	}
}
