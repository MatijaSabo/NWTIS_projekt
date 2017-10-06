/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.web.servisi;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 * Jersey REST client generated for REST resource:RestUredajiResourceContainer
 * [/UredajiREST]<br>
 * USAGE:
 * <pre>
 *        RESTUredaji client = new RESTUredaji();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Matija
 */
public class RESTUredaji {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8080/matsaboli2_aplikacija_1/webresources";

    public RESTUredaji() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("UredajiREST");
    }

    public String postJson(Object requestEntity) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String getJson() throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public void close() {
        client.close();
    }
    
}
