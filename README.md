# springboot-mc
Spring boot admin micro service for verifying and adding user with role based.

Used Technical Stack:
Java 8
Spring boot 2.2.6
Maven
Embedded H2 Database
AWS cognito service

Note:

Changed aws.cognito.clientId,aws.cognito.userPoolId,aws.cognito.identityPoolId in properties file for security reasons.
Please use your own AWS credentials.

And in your local should contain the .aws folder with credentials and config files with access key and scecreat key

Steps followed:

Exposed two end points for verifying user and adding user.

Verify User API:

Method: POST
URL :http://localhost:8282/api/verifyUser
Request Body: {"username":"sreekanth3","password":"abcd"}
Response:
{
    "username": "sreekanth3",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzcmVla2FudGgzIiwiaWF0IjoxNTg1NDkzNzUxLCJleHAiOjE1ODU1ODAxNTF9.ctOqoy0yoAel3YuqPn-CzZB05bF-Ml0_pIb04WeeECGPXCl9RrvNdvgI0SyTHOlZz27L8gIHz3BGvE2afzSqGg",
    "roles": [
        "USER"
    ]
}

Add user API:

Note: Pass the generated token as part of the headers while adding the user.


Method: POST
URL :http://localhost:8282/api/addUser
Request Body: {
                  "username": "sreekanth6",
                  "password":"abcd",
                  "role":["user"]
              }
Response:
 sreekanth6 User Successfully created!

AWS Cognito storage:

1) Should have AWS account
2) Create a IAM user and download the credentials and configure in .aws folder as mentioned above.
3) Create a User pool and client and configure those in application.properties file.

DB Scripts:

Created three tables:

users -> for storing the user info
roles -> for storing roles(currently added only USER,ADMIN,DEFAULT roles)
user_roles -> for maintaing the relation between user and roles.




