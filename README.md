# AWS Twitter - Omar Beyhum

Source code and configuration for simple AWS Twitter API.
The API is available at: https://3pnk6xssh8.execute-api.eu-west-1.amazonaws.com/Prod

The API is implemented using AWS Lambda, API Gateway, DynamoDB, and CloudFormation.

A postman collection under twitter_api_beyhum.postman_collection.json is available to run the API.

The lambda function entrypoint is under `beyhum.twitter.HttpLambdaHandler` which exposes a Jersey application under which routes in `beyhum.twitter.api` are registered.

# Testing the API
The postman collection contains 5 requests:
Login, Create Tweet, Tweets, My Tweets, Logout

By default these requests will run under "user1", which can be changed in `Login`'s request body.
Running the entire postman collection will go through the process of logging in, posting a tweet, viewing all tweets, and logging out.

The `Logout` request does not actually invoke an API as authentication is implemented through JSON Web Tokens with the Bearer scheme, in which case logging out is done on the client side since Refresh tokens are not implemented in this project.
The `Logout` request simply discards the token parameter in the postman client.

# Building and deploying from source
To build and deploy the solution from source, you will need:
- Java 11 and Maven 3.6+ in your path variables
- The AWS CLI and AWS SAM CLI configured with your AWS credentials
- An available S3 bucket which will store the lambda source code
- A `cfn-params.json` file with a secret key which will sign the API's auth tokens (see `cfn-params.json.template` for an example)

You can run the `provision-and-deploy.sh` script and pass it the name of your configured S3 bucket.
The script will build the project, deploy it to S3, and provision the required resources on your AWS account to expose the API publicly.