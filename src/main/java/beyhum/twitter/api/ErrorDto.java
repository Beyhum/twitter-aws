package beyhum.twitter.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorDto {

    @JsonProperty
    public final String message;

    public ErrorDto(String message) {
        this.message = message;
    }
}
