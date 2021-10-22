package beyhum.twitter;

import beyhum.twitter.api.auth.AuthResource;
import beyhum.twitter.api.tweet.TweetResource;
import beyhum.twitter.repo.DynamoTweetRepo;
import beyhum.twitter.repo.DynamoUserRepo;
import beyhum.twitter.repo.TweetRepo;
import beyhum.twitter.repo.UserRepo;
import beyhum.twitter.service.AuthService;
import beyhum.twitter.service.SimpleAuthService;
import beyhum.twitter.service.TweetService;
import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.server.validation.ValidationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpLambdaHandler implements RequestStreamHandler {
    public static final ResourceConfig jerseyApplication = new ResourceConfig()
            .register(ValidationExceptionMapper.class)
            .register(ExceptionLogger.class)
            .register(CustomObjectMapperProvider.class)
            .register(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(dynamoDbClient()).to(DynamoDbClient.class);
                    bindFactory(UserRepoFactory.class).to(UserRepo.class);
                    bindFactory(AuthServiceFactory.class).to(AuthService.class);
                    bindFactory(TweetRepoFactory.class).to(TweetRepo.class);
                    bindFactory(TweetServiceFactory.class).to(TweetService.class);
                }
            })
            .register(AuthResource.class)
            .register(TweetResource.class)
            .register(JacksonFeature.class)
            .register(ValidationFeature.class);

    private static final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler
            = JerseyLambdaContainerHandler.getAwsProxyHandler(jerseyApplication);

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }

    public static class AuthServiceFactory implements Factory<AuthService> {

        @Inject
        private UserRepo userRepo;


        @Override
        public AuthService provide() {
            String secretKey = System.getenv("JWT_KEY");
            if (secretKey == null) {
                secretKey = "development_mode_value";
            }
            return new SimpleAuthService(secretKey, userRepo);
        }

        @Override
        public void dispose(AuthService authService) {
        }
    }

    public static class UserRepoFactory implements Factory<UserRepo> {

        @Inject
        private DynamoDbClient dynamoDbClient;

        @Override
        public UserRepo provide() {
            return new DynamoUserRepo(dynamoDbClient);
        }

        @Override
        public void dispose(UserRepo authService) {
        }
    }


    public static class TweetServiceFactory implements Factory<TweetService> {

        @Inject
        private TweetRepo tweetRepo;

        @Override
        public TweetService provide() {
            return new TweetService(tweetRepo);
        }

        @Override
        public void dispose(TweetService authService) {
        }
    }

    public static class TweetRepoFactory implements Factory<TweetRepo> {

        @Inject
        private DynamoDbClient dynamoDbClient;

        @Override
        public TweetRepo provide() {
            return new DynamoTweetRepo(dynamoDbClient);
        }

        @Override
        public void dispose(TweetRepo authService) {
        }
    }

    private static DynamoDbClient dynamoDbClient() {

        return DynamoDbClient.builder()
                .region(Region.EU_WEST_1)
                .build();
    }

    @Singleton
    @Provider
    public static class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

        @Override
        public Response toResponse(ConstraintViolationException e) {
            Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
            Map<String, List<String>> propertiesWithViolations = violations.stream().collect(
                    Collectors.groupingBy(
                            v -> v.getPropertyPath().toString(),
                            Collectors.mapping(v -> v.getMessage(), Collectors.toList())));

            return Response.status(Response.Status.BAD_REQUEST).entity(propertiesWithViolations).build();
        }

    }

    @Provider
    public static class ExceptionLogger implements ApplicationEventListener, RequestEventListener {

        private static final Logger log = LoggerFactory.getLogger(HttpLambdaHandler.class);

        @Override
        public void onEvent(final ApplicationEvent applicationEvent) {
        }

        @Override
        public RequestEventListener onRequest(final RequestEvent requestEvent) {
            return this;
        }

        @Override
        public void onEvent(RequestEvent paramRequestEvent) {
            if(paramRequestEvent.getType() == RequestEvent.Type.ON_EXCEPTION) {
                log.error("", paramRequestEvent.getException());
            }
        }
    }

    @Provider
    public static class CustomObjectMapperProvider implements ContextResolver<ObjectMapper> {

        @Override
        public ObjectMapper getContext(Class<?> aClass) {
            return new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).registerModule(new JavaTimeModule());
        }
    }
}
