package de.devsurf.twiddns.tweets;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.twiddns.SingleTweeter;

@Bind(multiple=true)
public class WieIstMeineIP extends SingleTweeter{
	private static final String H_CLASS_IP = "<h1 class=\"ip\">";
	
	protected String getOwnIP() throws IOException {
		HttpClient client = new HttpClient();
		GetMethod gm = new GetMethod("http://www.wieistmeineip.de/");

		int status = client.executeMethod(gm);
		if (status == HttpStatus.SC_OK) {
			String html = gm.getResponseBodyAsString();
			int start = html.indexOf(H_CLASS_IP);
			int end = html.indexOf("</h1>", start);
			
			String ip = html.substring(start+H_CLASS_IP.length(), end);
			return ip;
		} else {
			throw new IOException("wieistmeineip.de has returned a wrong status: " + status);
		}
	}

	@Override
	public String singleTweet() throws IOException {
		return getOwnIP();
	}
}
