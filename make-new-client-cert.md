## Adding a new client certificate
This shows how to create a new client certificate. In the following instructions it is assumed that the name of the new client is "client2". Substitute this for the name you require.
- Open a linux shell and `cd` into `src/test/resources/client/auth`
- Generate a new client key (client2):
  ```
  openssl genpkey -out privclient2key.pem -outform PEM -algorithm RSA -pkeyopt rsa_keygen_bits:2048
  ```
- Generate a config file giving the details required in the certificate by creating a file named `client2-cert-config`. Open a text editor and paste the following.
  ```
  [req]
  prompt=no
  req_extensions=req_extensions_sec
  distinguished_name=distinguished_name_sec

  [req_extensions_sec]
  basicConstraints=critical,CA:FALSE,pathlen:1
  keyUsage=digitalSignature,dataEncipherment
  extendedKeyUsage=clientAuth

  [distinguished_name_sec]
  CN=client2
  OU=example

  ```
  Save it with the name of `client2-cert-config`.
- Generate a certificate request:
  ```
  openssl req -outform PEM -out client2-cert-req.pem -new -key privclient2key.pem -keyform PEM -config client2-cert-config
  ```
- Generate a certificate:
  ```
  openssl x509 -in client2-cert-req.pem -inform PEM -outform PEM -out client2-cert.pem -days 30 -req -CA root-cert.pem -CAkey privcakey01.pem -CAserial ca-serial -CAcreateserial
  ```
- You have now generated a key and certificate for 'client2'. The key is in the `privclient2key.pem` file and the certificate is
  in the `client2-cert.pem` file. You may delete the `client2-cert-config` and `client2-cert-req.pem` files.

