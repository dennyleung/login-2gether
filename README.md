# login-2gether

## Run
To startup the REST server, run the following in the commandline. Server is accessible via:

```
http://localhost:9000
```
 
### Implemented endpoints are:

- /users      (GET) : returning all users
- /secrets    (GET) : returning all secrets
- /secrets    (POST): creating a new secret
- /secrets/:secretId    (PUT): updating an existing secret
- /secrets/:secretId    (DELETE): deleting an existing secret
- /secrets/:secretId/viewers (PUT): updating the permitted viewers for an existing secret  

```bash
sbt run
```
