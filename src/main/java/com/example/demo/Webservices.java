/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import com.example.demo.generated.PallierType;
import com.example.demo.generated.ProductType;
import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import static javax.swing.text.html.FormSubmitEvent.MethodType.GET;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Merveille Divine
 */
 
@Path("generic")

public class Webservices {
    Services services;
    
    public Webservices() {
    services = new Services();
    }
    
    @GET
    @Path("world")
    @Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    
    public Response getXml(@Context HttpServletRequest request) throws JAXBException {
    String username = request.getHeader("X-user");
    return Response.ok(services.getWorld(username)).build();
    }
    public void PUTProduct(String data){
        ProductType product = new Gson().fromJson(data, ProductType.class);
    }
    public void PUTManager(String data){
        PallierType manager = new Gson().fromJson(data, PallierType.class);
    }
  
}
