# rentspotter_osgi_backend
Just some sharing here, try not to modify anything in pom.xml, unless consuming other modules(see below)

## 1. Download Karaf 4.4.9
https://karaf.apache.org/download

## 2. Initiate Karaf
````
bin\karaf
````

## 3. Copy .jar file into /deploy folder
e.g. rentalspotter_users-1.0-SNAPSHOT.jar

## 4. In Karaf,
To view bundle running, 'Active' means success
````
karaf@root()> list
````

if nopee, then check what requirement not installed in Karaf
````
karaf@root()> bundle:diag <bundle-id>
````

what i installed on my end before
````
feature:install \
  scr \
  config \
  log \
  http \
  pax-web-jetty \
  pax-web-http-whiteboard \
  aries-jax-rs-whiteboard \
  aries-jax-rs-whiteboard-jackson \
  jackson
````

if cannot try below, cuz some requirement very tricky in installing
````
feature:repo-add mvn:org.apache.cxf.karaf/apache-cxf/3.5.5/xml/features
feature:install cxf-specs
karaf@root()> feature:install aries-jax-rs-whiteboard
feature:install aries-jax-rs-whiteboard-jackson
feature:install mongodb-driver
feature:repo-add mongodb
feature:install mongodb-driver
feature:install jackson
````

If still got missing requirement, try sending pom.xml to Gemini, and the requirement missed out

### However, pom.xml is mostly correct. Only need to configure things need installed inside Karaf


# How to communicate between different modules
Make sure in your pom.xml. has this line, to export ur modules out for other ppl to use (As a Provider)<br/>
I dy included it
````
<Export-Package>
    authentication.*
</Export-Package>
````

To consume other modules (as a consumer), for example i consume "users" module
````
<dependency>
    <groupId>com.example.osgi</groupId>
    <artifactId>rentalspotter_users</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
````

