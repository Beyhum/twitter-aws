package beyhum.twitter.api.auth;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CredentialsDto {

    @Size(min = 1, max = 255)
    @NotNull
    @JsonProperty
    public String username;

    @Size(min = 1, max = 255)
    @NotNull
    @JsonProperty
    public String password;
}
