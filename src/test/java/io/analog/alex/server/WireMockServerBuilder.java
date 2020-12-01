package io.analog.alex.server;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class WireMockServerBuilder {
    private static final Map<Integer, WireMockServer> SERVERS = new HashMap<>();

    public static void startOnPort(Integer port) {

        WireMockServer server = new WireMockServer(port);
        server.start();
        setupStubs(server);
        SERVERS.put(port, server);
    }

    public static void stopOnPort(Integer port) {

        WireMockServer server = SERVERS.get(port);

        if (server != null) {
            server.stop();
        }
    }

    public static void stopAll() {
        SERVERS.entrySet().forEach(e -> e.getValue().stop());
    }

    // ------

    public static void setupStubs(WireMockServer server) {
        responseCodesStubs(server);
        personCrudStubs(server);
        headersStubs(server);
        queryParamsStubs(server);
    }

    // fill the server
    private static void responseCodesStubs(WireMockServer server) {

        server.stubFor(get(urlEqualTo("/codes/informational"))
                .willReturn(aResponse()
                        .withStatus(201)));

        server.stubFor(get(urlEqualTo("/codes/successful"))
                .willReturn(aResponse()
                        .withStatus(200)));

        server.stubFor(get(urlEqualTo("/codes/redirection"))
                .willReturn(aResponse()
                        .withStatus(300)));

        server.stubFor(get(urlEqualTo("/codes/clientError"))
                .willReturn(aResponse()
                        .withStatus(400)));

        server.stubFor(get(urlEqualTo("/codes/serverError"))
                .willReturn(aResponse()
                        .withStatus(500)));

    }

    private static void personCrudStubs(WireMockServer server) {

        server.stubFor(get(urlEqualTo("/person"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/persons.json")));

        server.stubFor(get(urlEqualTo("/person/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/person.json")));

        server.stubFor(post(urlEqualTo("/person"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBodyFile("json/person.json")));

        server.stubFor(put(urlEqualTo("/person/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBodyFile("json/person.json")));

        server.stubFor(patch(urlEqualTo("/person/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(204)
                        .withBodyFile("json/person.json")));

        server.stubFor(delete(urlEqualTo("/person/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(204)
                        .withBodyFile("json/person.json")));
    }

    private static void headersStubs(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/headerInOut"))
                .withHeader("Authorization", containing("Bearer <jwt>"))
                .willReturn(aResponse()
                        .withHeader("Authorization", "Bearer <jwt>")
                        .withStatus(200)
                        .withBodyFile("json/person.json")));

        server.stubFor(post(urlEqualTo("/headerJson"))
                .withHeader("Content-Type", containing("json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBodyFile("json/person.json")));

        server.stubFor(post(urlEqualTo("/headerMultiForm"))
                .withHeader("Content-Type", containing("multipart/form-data"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBody("{}")));

        server.stubFor(post(urlEqualTo("/headerUrlencoded"))
                .withHeader("Content-Type", containing("x-www-form-urlencoded"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBody("{}")));

        server.stubFor(post(urlEqualTo("/payloadWithExtras?name=User"))
                .withHeader("Content-Type", containing("json"))
                .withHeader("Authorization", containing("Bearer <jwt>"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBody("{}")));
    }

    private static void queryParamsStubs(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/person-withParams?name=User&age=99"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/persons.json")));

        server.stubFor(post(urlEqualTo("/person-withBody?name=User"))
                .withRequestBody(equalToJson("{ \"name\": \"User\" }"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(201)
                        .withBodyFile("json/persons.json")));
    }
}
