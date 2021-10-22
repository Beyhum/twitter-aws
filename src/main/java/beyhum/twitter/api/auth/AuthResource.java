package beyhum.twitter.api.auth;

import beyhum.twitter.api.ErrorDto;
import beyhum.twitter.service.AuthService;
import beyhum.twitter.service.BadCredentialsException;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/login")
public class AuthResource {

    @Inject
    public AuthService authService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.WILDCARD)
    public Response login(@Valid @NotNull CredentialsDto credentialsDto) {
        try {
            var token = authService.login(credentialsDto.username, credentialsDto.password);
            return Response.ok(AuthTokenDto.create(token)).build();
        } catch (BadCredentialsException e) {
            return Response.status(401).entity(new ErrorDto(e.getMessage())).build();
        }
    }
}
