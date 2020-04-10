/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import com.example.demo.generated.PallierType;
import com.example.demo.generated.ProductType;
import com.example.demo.generated.World;
import com.google.gson.Gson;
import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import javax.servlet.http.HttpServletRequest;
import static javax.swing.text.html.FormSubmitEvent.MethodType.GET;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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
    
     
    public Response getXml(@Context HttpServletRequest request) throws Exception {
    String username = request.getHeader("X-user");
    return Response.ok(services.getWorld(username)).build();
    }
    
    
    //@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    @PUT
    @Path("productUpdate")
    public void PUTProduct(@Context HttpServletRequest request,String data) throws Exception{
        ProductType product = new Gson().fromJson(data, ProductType.class);
        String username = request.getHeader("X-user");
        services.updateProduct(username, product);
        System.out.println(username+""+product);
    }
    @PUT
    @Path("managerUpdate")
    public void PUTManager(@Context HttpServletRequest request,String data) throws Exception{
        PallierType manager = new Gson().fromJson(data, PallierType.class);
        String username = request.getHeader("X-user");
        services.updateManager(username, manager);
    }
    @GET
    @Path("NewWorld")
    public World GETWorld(@Context HttpServletRequest request) throws Exception{
        String username = request.getHeader("X-user");
        return services.getWorld(username);    
    }
    
    @PUT
    @Path("upgrade")
    public void PUTUpgrade(@Context HttpServletRequest request, String data) throws Exception{
        PallierType upgrade = new Gson().fromJson(data, PallierType.class);
        String username = request.getHeader("X-user");
        services.updateUpgrade(username, upgrade);
    }
    @PUT
    @Path("angelUpgrade")
    public void PUTAngelUpgrade(@Context HttpServletRequest request,String data) throws Exception{
        PallierType angelUpgrade = new Gson().fromJson(data, PallierType.class);
        String username = request.getHeader("X-user");
        services.updateAngelUpgrades(username, angelUpgrade);
        System.out.println(username+""+angelUpgrade);
    }
    
    public void DELETEWorld(@Context HttpServletRequest request) throws Exception{
        String username = request.getHeader("X-user");
       services.reset(username);
    }
  
}
