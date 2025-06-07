package oauth;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;

/**
 * Author: davecahill
 *
 * Adapted from user Sqeezer's StackOverflow post at
 * http://stackoverflow.com/questions/15194182/examples-for-oauth1-using-google-api-java-oauth
 * to work with Goodreads' OAuth API.
 *
 * Get a key / secret by registering at https://www.goodreads.com/api/keys
 * and replace YOUR_KEY_HERE / YOUR_SECRET_HERE in the code below.
 */
public class GoodreadsOAuthSample {

    public static final String BASE_GOODREADS_URL = "https://www.goodreads.com";
    public static final String TOKEN_SERVER_URL = BASE_GOODREADS_URL + "/oauth/request_token";
    public static final String AUTHENTICATE_URL = BASE_GOODREADS_URL + "/oauth/authorize";
    public static final String ACCESS_TOKEN_URL = BASE_GOODREADS_URL + "/oauth/access_token";

    public static final String GOODREADS_KEY = "YOUR_KEY_HERE";
    public static final String GOODREADS_SECRET = "YOUR_SECRET_HERE";

    public static void main(String[] args) throws IOException, InterruptedException {
        OAuthHmacSigner signer = new OAuthHmacSigner();
        // Get Temporary Token
        OAuthGetTemporaryToken getTemporaryToken = new OAuthGetTemporaryToken(TOKEN_SERVER_URL);
        signer.clientSharedSecret = GOODREADS_SECRET;
        getTemporaryToken.signer = signer;
        getTemporaryToken.consumerKey = GOODREADS_KEY;
        getTemporaryToken.transport = new NetHttpTransport();
        OAuthCredentialsResponse temporaryTokenResponse = getTemporaryToken.execute();

        // Build Authenticate URL
        OAuthAuthorizeTemporaryTokenUrl accessTempToken = new OAuthAuthorizeTemporaryTokenUrl(AUTHENTICATE_URL);
        accessTempToken.temporaryToken = temporaryTokenResponse.token;
        String authUrl = accessTempToken.build();


        // Have the user manually visit the Authenticate URL to grant access (no verifier code)
        System.out.println("Goodreads oAuth sample: Please visit the following URL to authorize:");
        System.out.println(authUrl);
        System.out.println("Waiting 10s to allow time for visiting auth URL and authorizing...");
        Thread.sleep(10000);

        System.out.println("Waiting time complete - assuming access granted and attempting to get access token");
        // Get Access Token using the temporary token
        OAuthGetAccessToken getAccessToken = new OAuthGetAccessToken(ACCESS_TOKEN_URL);
        getAccessToken.signer = signer;
        // NOTE: This is the main difference from the StackOverflow example
        signer.tokenSharedSecret = temporaryTokenResponse.tokenSecret;
        getAccessToken.temporaryToken = temporaryTokenResponse.token;
        getAccessToken.transport = new NetHttpTransport();
        getAccessToken.consumerKey = GOODREADS_KEY;
        OAuthCredentialsResponse accessTokenResponse = getAccessToken.execute();

        // Build OAuthParameters in order to use them while accessing the resource
        OAuthParameters oauthParameters = new OAuthParameters();
        signer.tokenSharedSecret = accessTokenResponse.tokenSecret;
        oauthParameters.signer = signer;
        oauthParameters.consumerKey = GOODREADS_KEY;
        oauthParameters.token = accessTokenResponse.token;

        // Use OAuthParameters to access the desired Resource URL
        HttpRequestFactory requestFactory = new ApacheHttpTransport().createRequestFactory(oauthParameters);
        GenericUrl genericUrl = new GenericUrl("https://www.goodreads.com/api/auth_user");
        HttpResponse resp = requestFactory.buildGetRequest(genericUrl).execute();
        System.out.println(resp.parseAsString());
    }
}
