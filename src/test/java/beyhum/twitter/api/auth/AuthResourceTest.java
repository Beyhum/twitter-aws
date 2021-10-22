package beyhum.twitter.api.auth;

import beyhum.twitter.service.MockAuthService;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

public class AuthResourceTest {

    @Test
    public void loginReturns200WithTokenOnValidCredentials() {

        AuthResource authResource = new AuthResource();
        authResource.authService = new MockAuthService(true);
        CredentialsDto credentialsDto = new CredentialsDto();
        credentialsDto.username = "a";
        credentialsDto.password = "b";

        Response response = authResource.login(credentialsDto);
        assertEquals(200, response.getStatus());
        AuthTokenDto tokenInResponse = (AuthTokenDto) response.getEntity();
        assertNotNull(tokenInResponse.token);
    }

    @Test
    public void loginReturns401OnInvalidCredentials() {

        AuthResource authResource = new AuthResource();
        authResource.authService = new MockAuthService(false);
        CredentialsDto credentialsDto = new CredentialsDto();
        credentialsDto.username = "a";
        credentialsDto.password = "b";

        Response response = authResource.login(credentialsDto);
        assertEquals(401, response.getStatus());
        assertFalse(response.getEntity() instanceof AuthTokenDto);
    }
}