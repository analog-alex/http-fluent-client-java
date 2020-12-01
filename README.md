# Http Fluent Client 
A fluent http client working over Apache Http Client

---

![Build](https://github.com/analog-alex/http-fluent-client/workflows/Java%20CI%20with%20Maven/badge.svg)

## Author
Miguel Alexandre @2019

## Introduction

This is a simple and small wrapper around the [Apache HttpComponents](https://hc.apache.org) project that provides a more fluent
interface to perform Http calls with less code and less configuration but with no read loss of functionality. Some extra features
are added like the possibility to make an async call or to subscribe to a Response using an RxJava observable. 
The goal is to add more features in the near future and improve the QoL and syntatic expressiveness. 

The library is licensed with the **MIT license** so use away in any environment you see fit to: public, private, corporate and non-profit.

## Usage

Here we give a by no-means exhaustive description of the utility.

### Simple examples

A **Get** request follows:

```java

Response response = Http.Get("endpoint").executeToOptional()
	.orElseThrow(NoSuchElementException::new);
T clazz = response.parseAs(T.class);

```
An entry point class **Http** is used to statically initialize method sub-classes, enumerated as the usual specified methods: GET, POST,
PUT, PATCH and DELETE. 

```java

String json = "{ \"key\" : \"value\" }"
Response response = Http.Post("endpoint").addBody(json)
	.executeToOptional().orElseThrow(NoSuchElementException::new);

```

The library takes opinionated decisions like sending the above *String* payload as an `'application/json'` request body.

Sending query params and request headers is possible (and easy!) as well:

```java

Get get = Http.Get(new URI("endpoint"));
get.addParameter("key", value").addHeader("Authorization", "Bearer <token>").execute();

```

### Response object

The response object exposes some ease-of-life utilites.

```java

Response response = Http... /* some method */
response.getStatusCode(); 
response.isSuccessful();
response.isRedirection();
response.isClientError();
response.isServerError();
```

It also allows for an expressive parsing to a POJO or a JsonElement.

```java

Response response = Http... /* some method */
response.parseAsGson(); 
response.parseAs(Class.class);
response.parseAsCollectionOf(Class.class);

```

### Forms

Sending multi-form requests is also possible via the Form class.

```java

Form data = new Form()
               .addPart("key", "value")
               .addPart("anotherKey", new File("aGif.gif"), ContentType.IMAGE_GIF);
               
Http.Post("endpoint").addBody(data).execute();


```

## Tests

There is a small suite of tests using jUnit 5 and WireMock Server. The goal is to increase test coverage and provide a robust testing
base for further improvements down the line.

## Acknowledgments

To the very useful:

The Apache HttpComponents [here](https://hc.apache.org)  
WireMock server [here](http://wiremock.org)  
