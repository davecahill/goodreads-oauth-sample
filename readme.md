Goodreads OAuth API sample
==============================


Description
---------------
A simple command line example of how to use the [Goodreads OAuth API](https://www.goodreads.com/api/)
from Java using the [Google OAuth Java package](https://code.google.com/p/google-oauth-java-client/).

How to run
---------------

Check out this repository, and run ```mvn compile exec:java``` in the base directory. You will need to have [Maven installed](https://maven.apache.org/install.html).

Note that the code sample will fail unless you set valid values for ```GOODREADS_KEY``` and ```GOODREADS_SECRET``` in ```GoodreadsOAuthSample.java```.

Credit
---------------
This is heavily based on this post by StackOverflow user [sqeezer](http://stackoverflow.com/users/587574/sqeezer):

http://stackoverflow.com/questions/15194182/examples-for-oauth1-using-google-api-java-oauth

Details
---------------
The main difference from the example in the post above is that the Goodreads OAuth API doesn't
return an oauth_verifier parameter (also known as "verifier code") after the user authorizes in
the browser. According to the [OAuth Bible](http://oauthbible.com/#oauth-10a-three-legged):

```
On Step 6 if the oauth_verifier has not been set, this is a failed OAuth 1.0a 3-Legged
implementation and probably only requires the oauth_token to be sent.
Rarely seen but they exist.
```

This can be worked around by doing as described in the code sample in this repo:
* Not doing anything with verifier code
* Setting the signer's tokenSharedSecret to the temporary token's shared secret when retrieving the "real" OAuth token


