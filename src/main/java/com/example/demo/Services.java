/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;
import com.example.demo.generated.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


/**
 *
 * @author Merveille Divine
 */
public class Services {
    World world;
    
    public World readWorldFromXml() throws JAXBException {
        JAXBContext cont = JAXBContext.newInstance(World.class);
        Unmarshaller u = cont.createUnmarshaller();
        InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");//lire le fichier world.xml qui sera créé
        world = (World) u.unmarshal(input); 
        return world;
        }
    public void saveWordlToXml(World world) throws JAXBException, FileNotFoundException{
        JAXBContext cont = JAXBContext.newInstance(World.class);
        Marshaller m = cont.createMarshaller();
        File file = new File("world.xml");
        OutputStream output = new FileOutputStream(file);// ecrire le fichier world.xml
        m.marshal(world,output);       
        }

}
