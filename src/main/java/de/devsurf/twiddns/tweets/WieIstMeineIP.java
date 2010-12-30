package de.devsurf.twiddns.tweets;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import de.devsurf.injection.guice.annotations.Bind;

@Bind(multiple=true)
public class WieIstMeineIP implements Tweeter{
	private static final Logger LOGGER = Logger.getLogger(WieIstMeineIP.class.getName());
	private static final String H_CLASS_IP = "<h1 class=\"ip\">";
	
	public List<String> tweet() throws IOException {
		try {
			return Collections.singletonList(getOwnIP());
		} catch (IOException ioe) {
			LOGGER.log(Level.WARNING, ioe.getMessage(), ioe);
			throw ioe;
		}
	}
	
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
	public CronFormat schedule() {
		return CronFormat.schedule("* * * * * 30 *");
	}
}
