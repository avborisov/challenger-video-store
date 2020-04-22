package im.challenger.videostore.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public abstract class AuthApiTools {

    AuthApiTools() {
        throw new UnsupportedOperationException("This class is Utils");
    }

    public static boolean isAuthTokenValid(String auth) throws IOException {
        URL url = new URL("https://challenger.im/backend/user/isTokenValid");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty (HttpHeaders.AUTHORIZATION, auth);
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        int responseCode = con.getResponseCode();
        log.info("isTokenValid token response code: {}", responseCode);
        con.disconnect();
        return HttpStatus.OK.value() == responseCode;
    }

}
