# cakes-api
This is a simple secured REST application about cakes. It is designed to fulfill a back-end service which lists, adds, updates and removes records that represent cakes.
It uses X509 authentication to authenticate client requests. Once authenticated, a client is only permitted
operations associated with the roles possessed by the client.

## Setup
Some of the steps below require access to a linux command shell. Here it is assumed that the shell is `bash` and your linux OS is from the Red Hat family. If you are using a different OS
some of these commands may not work. For instance, you may have to check the documentation on how to use `curl` for your particular OS. Also you may choose a different Docker container image builder and runner.
For instance on Fedora, `podman` may be better suited.
- Ensure you have a Java 17 JDK installed.
- Ensure you have Maven installed.
- Ensure you have Docker or an equivalent installed. If you are using Docker, make sure the daemon is running.
- Clone this repo to a local directory and cd into it.
- Build the project and generate your jar file: open a linux command shell and enter

   `mvn package`

  or

  `./mvnw package`

  This will generate a jar file and place it in the `./target` subdirectory.
- Build a docker image:

  `docker build -t firecub/cakes .`

## Launch the application
 - From your linux shell:

   `docker run -d -p 8443:8443 firecub/cakes`
 - Find the ID of your running container: 

   `docker container ls`
   
   It should show something like this

    ```
    CONTAINER ID  IMAGE                           COMMAND     CREATED         STATUS         PORTS                   NAMES
    bb24850c31a1  localhost/firecub/cakes:latest              20 seconds ago  Up 19 seconds  0.0.0.0:8443->8443/tcp  stoic_feistel`
   ``` 
   
    Check that it has finished loading by checking the logs for that container, using the ID:

    `docker logs bb24850c31a1`

    The last line should show a message like `Started CakesApplication in 6.236 seconds (process running for 7.673)`.
    If not, you might be too early. Wait two seconds and try again. You can also use a `-f` switch to monitor the logs
    and then hit ctrl-C when the message appears.

    The following steps allow you to run tests of this container. Once you are done
    testing, stop the container (for instance, using the command `docker container stop bb24850c31a1`) and remove it (`docker container rm bb24850c31a1`).

## Test the application
The application is designed to act as a back-end service. It requires authentication at both the client and server end. It uses X509 authentication
both for server authentication at the client end and for client authentication at the server end. The following tests will test using different
clients. First we will test using a client with privileges to execute all operations. Next we will test using a client with only read-ony access.
Finally, we will test using a client with a valid certificate, but which is unrecognised by the server.
### Test using 'admin' client
- Open a linux command shell and `cd` into the `cakes-api/src/test/resources` directory.
- Test 'getCakes':
   ```
     curl --cacert server/auth/server-cert.pem -E ./client/auth/admin-cert.pem --key ./client/auth/privadminkey.pem https://localhost:8443/cakes
   ```
  The `--cacert server/auth/server-cert.pem` tells curl to accept the certificate presented by the server.
  The `-E ./client/auth/admin-cert.pem` tells curl to present the 'admin' client certificate to the server. This is used for the client authentication and used by the
  server for encrypting data sent to the client.
  The `--key ./client/auth/privadminkey.pem` tells curl the private key to use for client authentication and decrypting encrypted data from
  the server. After running this command you should get a JSON response from the server, like this
  ```
  [{"id":1,"title":"Lemon cheesecake","description":"A cheesecake made of lemon","image":"https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg"},{"id":2,"title":"victoria sponge","description":"sponge with jam","image":"http://www.bbcgoodfood.com/sites/bbcgoodfood.com/files/recipe_images/recipe-image-legacy-id--1001468_10.jpg"},{"id":3,"title":"Carrot cake","description":"Bugs bunnys favourite","image":"http://www.villageinn.com/i/pies/profile/carrotcake_main1.jpg"},{"id":4,"title":"Banana cake","description":"Donkey kongs favourite","image":"http://ukcdn.ar-cdn.com/recipes/xlarge/ff22df7f-dbcd-4a09-81f7-9c1d8395d936.jpg"},{"id":5,"title":"Birthday cake","description":"a yearly treat","image":"http://cornandco.com/wp-content/uploads/2014/05/birthday-cake-popcorn.jpg"}]
  ```
- Test 'addCake':
  ```
  curl --cacert server/auth/server-cert.pem -E ./client/auth/admin-cert.pem --key ./client/auth/privadminkey.pem --json '{"title":"Yellowcake","description":"A cake made from uranium ore.","image":"https://upload.wikimedia.org/wikipedia/commons/d/d4/Yellowcake.jpg"}' -X POST https://localhost:8443/cakes
  ```
  This will give the response
  ```
  {"id":6,"title":"Yellowcake","description":"A cake made from uranium ore.","image":"https://upload.wikimedia.org/wikipedia/commons/d/d4/Yellowcake.jpg"}
  ```
  You may test that this cake has been added, by running the 'getCakes' test again.
- Test 'updateCake':
  ```
  curl --cacert server/auth/server-cert.pem -E ./client/auth/admin-cert.pem --key ./client/auth/privadminkey.pem --json '{"title":"Victoria sponge","description":"sponge with jam","image":"http://www.bbcgoodfood.com/sites/bbcgoodfood.com/files/recipe_images/recipe-image-legacy-id--1001468_10.jpg"}' -X PUT https://localhost:8443/cakes/2
  ```
  This should change the title of the Victoria sponge cake to start with an upper case letter.
- Test 'deleteCake':
  ```
  curl --cacert server/auth/server-cert.pem -E ./client/auth/admin-cert.pem --key ./client/auth/privadminkey.pem -X DELETE https://localhost:8443/cakes/2
  ```
  This will delete the Victoria sponge cake.

### Test using 'client1' client
When we tested using 'admin' (above) we were able to do all of the operations. This is because the 'admin' client has the 'ADMIN' role. When we test using the 'client1'
client we will find that we can only list cakes. This is because listing cakes does not require the client to
possess any particular roles. All of the operations except 'getCakes' requires an 'ADMIN' role.
- Test 'getCakes':
  ```
  curl --cacert server/auth/server-cert.pem -E ./client/auth/client1-cert.pem --key ./client/auth/privclient1key.pem https://localhost:8443/cakes
  ```
  You should see all the cakes listed in JSON format.
- Test 'deleteCake'
  ```
  curl --cacert server/auth/server-cert.pem -E ./client/auth/client1-cert.pem --key ./client/auth/privclient1key.pem -X DELETE https://localhost:8443/cakes/3
  ```
  You will see a 'forbidden' message like
  ```
  {"timestamp":"2023-03-31T12:17:57.847+00:00","status":403,"error":"Forbidden","path":"/cakes/3"}
  ```
### Test using a new client unrecognised by server
To test using a client not recognised by the server, first make a new certificate and key, and then try to do a 'getCakes' request.
- Follow the steps in [make-new-client-cert.md](make-new-client-cert.md) to create a new key and certificate for 'client2'.
- Now that we have a new key and certificate created for 'client2' we can use it to make a 'getCakes' request. This should result in a 'unauthorised' response. Using the `-v` switch
  will show the HTTP status. Do a `cd ../..` to get back to the `src/test/resources` directory and enter
  ```
  curl --cacert server/auth/server-cert.pem -E ./client/auth/client2-cert.pem --key ./client/auth/privclient2key.pem -v https://localhost:8443/cakes
   ```
  This will show a 403 (forbidden) HTTP status. To fix this, edit the [initial-user-detail.json](src/main/resources/static/initial-user-detail.json) JSON file and add a new row for `client2`.
  You will need to stop the container, rebuild and launch a new container.

