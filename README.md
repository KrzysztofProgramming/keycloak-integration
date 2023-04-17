# Keycloak-integration

Microservices project with custom keycloak user storage provider.

### Modules:

- User-provider - user storage provider for keycloak. Uses HTTP to fetch data from user-service
- User-service - user storage service, that allows creating and updating users via HTTP. Serves also as a credential
  validator. Supports composite roles.
- Api-gateway - simple API gateway to redirect requests.
- Eureka-server - service discovery
- Resources-service - HTTP server which uses Keycloak to authorize users and check their roles before giving access to
  the specified endpoint.

## Running:

`mvn package` - builds images and user-provider

`docker-compose up` - starts the docker container

Go to http://localhost:8180 and log in as admin user with username and password ***admin***. Create a new realm from
***keycloak-realm-export.json***  
New users can be created by making requests to http://localhost:8081/users/api/v1/users

### TODO list:
- improve users management directly from Keycloak admin console
- add hibernate text search
- write more tests
