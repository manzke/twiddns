package de.devsurf.fakeddns.twitter;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class Main {
	private static final String CONSUMER_KEY = "sYMke2xPv6YWlP6O0TUeg";;
	private static final String CONSUMER_SECRET = "CQbjmMw3EtCKxzzXZeqdPCVWDQoBETxJlusTu0hEAs";
	
	public static void main(String[] args) throws Exception {
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			Desktop desktop = Desktop.getDesktop();
			if(desktop != null){
				desktop.browse(new URI(requestToken.getAuthorizationURL()));	
			}
			
			System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			String pin = br.readLine();
			try{
				if(pin.length() > 0){
					accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				}else{
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if(401 == te.getStatusCode()){
					System.out.println("Unable to get the access token.");
				}else{
					te.printStackTrace();
				}
				System.exit(1);
			}
		}
		storeAccessToken(twitter.verifyCredentials().getId() , accessToken);
		System.exit(0);
	}
	
	private static void storeAccessToken(int useId, AccessToken accessToken) throws IOException{
		System.out.println("access token:" + accessToken.getToken());
		System.out.println("access token secret:" + accessToken.getTokenSecret());
		
		Properties properties = new Properties();
		properties.setProperty("private.key", accessToken.getToken());
		properties.setProperty("private.secret", accessToken.getTokenSecret());
		
		properties.store(new FileOutputStream(new File("configuration.properties")), "");
	}
}
