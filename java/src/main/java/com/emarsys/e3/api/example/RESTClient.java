package com.emarsys.e3.api.example;

import java.io.IOException;
import java.util.Arrays;

import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.StringRepresentation;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * <p>
 * Very basic REST client which provides a convenient interface to perform HTTP requests.
 * </p><p>
 * RESTClient uses the Restlet framework (http://restlet.org) version 1.1.x
 * in order to perform the HTTP calls.
 * </p>
 *
 * @author Alex Kraml <kraml@emarsys.com>
 */
final class RESTClient {

    private Client client;

    private String apiUsername;
    private String apiPasswordHash;

    public RESTClient( ClientConfiguration config ) {
        this();
        this.apiUsername = config.getApiUsername();
        this.apiPasswordHash = config.getApiPasswordHash();

        this.client = new Client(Arrays.asList(Protocol.HTTP, Protocol.HTTPS));
    }

    /**
     * Prevent default constructor to be called.
     */
    private RESTClient() {
    }


    /**
     * Performs an HTTP GET request to the specified URL.
     *
     * @param url
     * @return
     * @throws IOException
     */
    Response doGet(String url) throws IOException {
        return doRequest(new Request(Method.GET, url));
    }

    /**
     * Performs an HTTP POST request.
     *
     * @param url
     * @param xmlContent
     * @return
     * @throws IOException
     */
    Response doPost(String url, String xmlContent) throws IOException {
        return doRequest(new Request(Method.POST, url,
                new StringRepresentation(xmlContent, MediaType.APPLICATION_XML)));
    }

    /**
     * Performs an HTTP PUT request.
     *
     * @param url
     * @param xmlContent
     * @return
     * @throws IOException
     */
    Response doPut(String url, String xmlContent) throws IOException {
        return doRequest(new Request(Method.PUT, url,
                new StringRepresentation(xmlContent, MediaType.APPLICATION_XML)));
    }

    /**
     * Generic method for HTTP requests via restlet.org.
     *
     * @param request
     * @return
     * @throws IOException
     */
    private Response doRequest(Request request) throws IOException {

        initChallengeScheme(request);

        Response response = this.client.handle(request);

        out.println(
            request.getResourceRef().toString() + " had response " +
            response.getStatus().toString()
        );

        if (!Status.SUCCESS_OK.equals(response.getStatus())) {
            err.println( "ERROR: Unsuccessful response " + response.getStatus().toString()
                    + " for request " + request.toString());
        }

        return response;
    }

    /**
     * Adds this client's credentials for HTTP basic authentication to the passed request.
     *
     * @param request
     */
    private void initChallengeScheme(Request request) {
        request.setChallengeResponse(
                new ChallengeResponse(ChallengeScheme.HTTP_BASIC, apiUsername, apiPasswordHash)
        );
    }
}//class TxmRESTClient
