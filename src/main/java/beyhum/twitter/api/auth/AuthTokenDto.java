package beyhum.twitter.api.auth;

import beyhum.twitter.service.AuthToken;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthTokenDto {

    @JsonProperty
    public String token;

    public static AuthTokenDto create(AuthToken authToken) {
        AuthTokenDto dto = new AuthTokenDto();
        dto.token = authToken.token;
        return dto;
    }
}
