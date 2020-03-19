/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import com.example.demo.generated.PallierType;
import com.example.demo.generated.ProductType;
import com.example.demo.generated.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller; 

/**
 *
 * @author Merveille Divine
 */
public class Services {

    public World getWorld(String username) throws JAXBException  {
        World world;
        world= readWorldFromXml(username);
        updateScore(world);
        return world;
        
    }
    
    public void updateScore(World world) {
        
        //mise à jour du score
        int tempsEcoule = (int) (System.currentTimeMillis()-world.getLastupdate());
        int n=0;//nombre de produits fabriqués
        
       for (ProductType p:world.getProducts().getProduct()){
           if (p.isManagerUnlocked()== false){
               if (p.getTimeleft() != 0){
                   long timeLeft = tempsEcoule - p.getTimeleft();
                   if (timeLeft < 0){
                       n=0;
                       p.setTimeleft(-timeLeft);// on met à jour le timeLeft inverse du calcul plus haut
                   }
                   else {
                       p.setTimeleft(0);
                       n=1;// un produit est créé
                   }
               }
           }
           else {
            long timeLeft = tempsEcoule- p.getTimeleft(); // temps ecoulé moins le temps qui restait pour la production d'un produit
            if (timeLeft <0){
                p.setTimeleft(-timeLeft);
            }
            else {
              int nbreP = (int) (timeLeft/p.getVitesse());// division entière
              p.setTimeleft(timeLeft%p.getVitesse());// reste de la division entiere qui represente le temps restant ou on pas pu finir la production
              n= nbreP+1;
            }
           }
         world.setMoney(world.getMoney()+(n*p.getRevenu()));// mise a jour de l 'argent qui peut augmenter ou diminuer
         world.setScore(world.getScore()+(n*p.getRevenu()));// mise a jour du score qui lui augmente toujours
       }
      world.setLastupdate(System.currentTimeMillis());  
    }

    public World readWorldFromXml(String username) throws JAXBException {
         World world;
        JAXBContext cont = JAXBContext.newInstance(World.class);
        Unmarshaller u = cont.createUnmarshaller();

        File f = new File(username + "world.xml");
        if (f.exists()) {
            world = (World) u.unmarshal(f);
        } else {
            InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");//lire le fichier world.xml qui sera créé
            world = (World) u.unmarshal(input);
        }
        return world;
    }

    public void saveWordlToXml(World world, String username) throws JAXBException, FileNotFoundException {
        JAXBContext cont = JAXBContext.newInstance(World.class);
        Marshaller m = cont.createMarshaller();
        File file = new File(username + "world.xml");

        OutputStream output = new FileOutputStream(file);// ecrire le fichier world.xml
        m.marshal(world, output);
    }

    // prend en paramètre le pseudo du joueur et le produit
// sur lequel une action a eu lieu (lancement manuel de production ou
// achat d’une certaine quantité de produit)
// renvoie false si l’action n’a pas pu être traitée
    public Boolean updateProduct(String username, ProductType newproduct) throws JAXBException, FileNotFoundException {
        // aller chercher le monde qui correspond au joueur
        World world = getWorld(username);
        
        // trouver dans ce monde, le produit équivalent à celui passé
        // en paramètre
        ProductType product = findProductById(world, newproduct.getId());
        if (product == null) {
            return false;
        }

        // calculer la variation de quantité. Si elle est positive c'est
        // que le joueur a acheté une certaine quantité de ce produit
        // sinon c’est qu’il s’agit d’un lancement de production.
        int qtchange = newproduct.getQuantite() - product.getQuantite();
        if (qtchange > 0) {
            // soustraire de l'argent du joueur le cout de la quantité
            // achetée et mettre à jour la quantité de product
            world.setMoney(world.getMoney() - product.getCout()* ( (1 - Math.pow(product.getCroissance(),qtchange)) /
                    (1 - product.getCroissance())) );
         
            product.setQuantite(product.getQuantite()+qtchange);        
            product.setCout(product.getCout() * Math.pow(product.getCroissance(), qtchange));
            //regler le last update sur l'heure courante
            world.setLastupdate(System.currentTimeMillis());

        } else {
            // initialiser product.timeleft à product.vitesse
            // pour lancer la production
            product.setTimeleft(product.getVitesse());
        }
        
        //application des bonus en fonction des seuils sur les oranges
        if(newproduct.getQuantite()>=10 && newproduct.getId()==1){ 
            PallierType p =  newproduct.getPalliers().getPallier().get(0);// on recupère le premier pallier de la liste des palliers du produit 
            p.setUnlocked(true);// on le débloque
            newproduct.setVitesse(newproduct.getVitesse()/2); 
        } 
        else if (newproduct.getQuantite()>=20 && newproduct.getId()==1) {
            PallierType p =  newproduct.getPalliers().getPallier().get(1);//on recupère le deuxième pallier de la liste des palliers du produit 
            p.setUnlocked(true);// on le débloque
            double r = newproduct.getRevenu()*2;
            newproduct.setRevenu(r);//on met a jour le revenu du produit
        }
        else if (newproduct.getQuantite()>=30 && newproduct.getId()==1){
            PallierType p =  newproduct.getPalliers().getPallier().get(2);//on recupère le troisème pallier de la liste des palliers du produit 
            p.setUnlocked(true);
             /*newproduct*/
            world.setAngelbonus(4);
        }
        
        //application des bonus en fonction des seuils sur les chaussures
        if(newproduct.getQuantite()>=15 && newproduct.getId()==2){ 
            PallierType p =  newproduct.getPalliers().getPallier().get(0);// on recupère le premier pallier de la liste des palliers du produit 
            p.setUnlocked(true);// on le débloque
            newproduct.setVitesse(newproduct.getVitesse()/2); 
        } 
        else if (newproduct.getQuantite()>=30 && newproduct.getId()==2) {
            PallierType p =  newproduct.getPalliers().getPallier().get(1);//on recupère le deuxième pallier de la liste des palliers du produit 
            p.setUnlocked(true);// on le débloque
            double r = newproduct.getRevenu()*2;
            newproduct.setRevenu(r);//on met a jour le revenu du produit
        }
        else if (newproduct.getQuantite()>=45 && newproduct.getId()==2){
            PallierType p =  newproduct.getPalliers().getPallier().get(2);//on recupère le troisème pallier de la liste des palliers du produit 
            p.setUnlocked(true); 
             /*newproduct*/
            world.setAngelbonus(4);
        }
        
        //application des bonus en fonction des seuils sur les bijoux
        if(newproduct.getQuantite()>=10 && newproduct.getId()==3){ 
            PallierType p =  newproduct.getPalliers().getPallier().get(0);// on recupère le premier pallier de la liste des palliers du produit 
            p.setUnlocked(true);// on le débloque
            newproduct.setVitesse(newproduct.getVitesse()/2); 
        } 
        else if (newproduct.getQuantite()>=20 && newproduct.getId()==3) {
            PallierType p =  newproduct.getPalliers().getPallier().get(1);//on recupère le deuxième pallier de la liste des palliers du produit 
            p.setUnlocked(true);// on le débloque
            double r = newproduct.getRevenu()*2;
            newproduct.setRevenu(r);//on met a jour le revenu du produit
        }
        else if (newproduct.getQuantite()>=30 && newproduct.getId()==3){
            PallierType p =  newproduct.getPalliers().getPallier().get(2);//on recupère le troisème pallier de la liste des palliers du produit 
            p.setUnlocked(true);// 
             /*newproduct*/
            world.setAngelbonus(4);
        }
        
        //application des bonus en fonction des allunlocks
        if(newproduct.getQuantite()>=50){ 
            PallierType p =  (PallierType) newproduct.getPalliers().getPallier(); 
            p.setUnlocked(true);// on le débloque
            newproduct.setVitesse(newproduct.getVitesse()/2); 
        } 
        else if (newproduct.getQuantite()>=60 ) {
            PallierType p =  (PallierType) newproduct.getPalliers().getPallier(); 
            p.setUnlocked(true);// on le débloque
            double r = newproduct.getRevenu()*2;
            newproduct.setRevenu(r);//on met a jour le revenu du produit
        }
        else if (newproduct.getQuantite()>=80){
            PallierType p =  (PallierType) newproduct.getPalliers().getPallier(); 
            p.setUnlocked(true);
             /*newproduct*/
            world.setAngelbonus(4);
        }
        // sauvegarder les changements du monde
        saveWordlToXml(world, username);
        return true;
        
        
    }

    private ProductType findProductById(World world, int id) {
        for (ProductType product : world.getProducts().getProduct()) 
            if (product.getId() == id) return product;
        return null;
    }

    // prend en paramètre le pseudo du joueur et le manager acheté.
// renvoie false si l’action n’a pas pu être traitée
    public Boolean updateManager(String username, PallierType newmanager) throws JAXBException, FileNotFoundException {
        // aller chercher le monde qui correspond au joueur
        World world = getWorld(username);
        // trouver dans ce monde, le manager équivalent à celui passé
        // en paramètre
        PallierType manager = findManagerByName(world, newmanager.getName());
        if (manager == null) {
            return false;
        }

        // débloquer ce manager
        // trouver le produit correspondant au manager
        ProductType product = findProductById(world, manager.getIdcible());
        if (product == null) {
            return false;
        }
        // débloquer le manager de ce produit
         manager.setUnlocked(true);
         
        // soustraire de l'argent du joueur le cout du manager
        world.setMoney(world.getMoney()-manager.getSeuil());
        
        // sauvegarder les changements au monde
        saveWordlToXml(world, username);
        return true;
    }

    private PallierType findManagerByName(World world, String name) {
        
        for (PallierType manager : world.getManagers().getPallier()) {
            if (manager.getName().equals(name)) return manager;
        }
        return null;
    }

}
