package de.devsurf.twiddns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.http.AccessToken;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.configuration.Configuration;
import de.devsurf.injection.guice.configuration.PathConfig;
import de.devsurf.injection.guice.configuration.Configuration.Type;
import de.devsurf.injection.guice.configuration.features.ConfigurationFeature;
import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.asm.ASMClasspathScanner;
import de.devsurf.twiddns.tweets.Tweeter;

@Configuration(location=@PathConfig("/configuration.properties"), type=Type.VALUES)
public class TwitterPublisher {
	private static final Logger LOGGER = Logger.getLogger(TwitterPublisher.class.getName());
	private static final String CONSUMER_KEY = "sYMke2xPv6YWlP6O0TUeg";
	private static final String CONSUMER_SECRET = "CQbjmMw3EtCKxzzXZeqdPCVWDQoBETxJlusTu0hEAs";

	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm:ss");
	
	@Inject(optional=true) @Named("shutdown.timeout")
	private int timeOut = 2000;
	
	private List<Tweeter> tweeters = new ArrayList<Tweeter>();
	private AccessToken token;
	
	private AsyncTwitterFactory factory = new AsyncTwitterFactory(new TwitterAdapter() {
		@Override
		public void onException(TwitterException e, TwitterMethod method) {
			LOGGER.warning("Exception updating Twitter status: " + e.toString()+" Twitter says it is: "+e.getMessage());
		}

		@Override
		public void updatedStatus(Status statuses) {
			LOGGER.info("Updated Twitter status: " + statuses.getText());
		}
	});
	
	@Inject
	public TwitterPublisher(Set<Tweeter> implementations, AccessTokenProvider provider) {
		super();
		this.tweeters = new ArrayList<Tweeter>(implementations);
		this.token = provider.get();
	}
	
	public void tweet() throws Exception{
		AsyncTwitter twitter =  factory.getOAuthAuthorizedInstance(CONSUMER_KEY, CONSUMER_SECRET, token);
		for(Tweeter tweeter : tweeters){
			twitter.updateStatus(FORMATTER.format(new Date())+" - "+tweeter.tweet());
		}
		Thread.sleep(timeOut);	
		twitter.shutdown();
	}
	
	public static void main(String[] args) throws Exception {
		StartupModule startup = StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create("de.devsurf.twiddns"));
		startup.addFeature(ConfigurationFeature.class);
		Injector injector = Guice.createInjector(startup);
		
		TwitterPublisher publisher = injector.getInstance(TwitterPublisher.class);
		publisher.tweet();
	}
}
