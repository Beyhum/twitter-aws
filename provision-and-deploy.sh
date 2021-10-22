#!/bin/sh


mvn clean package

aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket $1

aws cloudformation deploy  --template-file output-sam.yaml --stack-name twitter --capabilities CAPABILITY_IAM --parameter-overrides file://cfn-params.json


aws dynamodb put-item --table-name User --item '{"Id": {"S": "3e2077f1-d8f1-4bf8-9eef-750c6ef9661b"}, "Username": {"S": "user1"}}'
aws dynamodb put-item --table-name User --item '{"Id": {"S": "7f6dd9a0-058a-4e51-a3c0-6364631440be"}, "Username": {"S": "user2"}}'
aws dynamodb put-item --table-name User --item '{"Id": {"S": "f5b47c98-168b-429b-9ea3-66b55f7771d1"}, "Username": {"S": "user3"}}'
aws dynamodb put-item --table-name User --item '{"Id": {"S": "dac9c598-17ca-4ece-8ef8-45819f416acf"}, "Username": {"S": "user4"}}'
aws dynamodb put-item --table-name User --item '{"Id": {"S": "966386ec-e3d1-4107-bbb0-55edfe33e394"}, "Username": {"S": "user5"}}'

# aws cloudformation delete-stack --stack-name twitter