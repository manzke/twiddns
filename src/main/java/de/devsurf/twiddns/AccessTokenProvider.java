package de.devsurf.twiddns;

import twitter4j.http.AccessToken;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class AccessTokenProvider implements Provider<AccessToken> {
	private AccessToken token;
	
	@Inject
	public AccessTokenProvider(@Named("private.key") String key, @Named("private.secret") String secret) {
		token = new AccessToken(key, secret);
	}

	@Override
	public AccessToken get() {
		return token;
	}
}
