/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import dao.TipoAbonosDAO;
import java.util.List;
import model.TipoAbono;

/**
 *
 * @author Gerardo Torreblanca
 */
public class TipoAbonosService {
    
    private TipoAbonosDAO abonosDao = new TipoAbonosDAO();
    
    public List<TipoAbono> getAbonos(){
    
        return abonosDao.getAbonos();
    }
    
     public List<TipoAbono> getAbonosLike(String search){
    
        return abonosDao.getAbonosLike(search);
    }
     
    public void insert(TipoAbono tipo){
       abonosDao.insert(tipo);
    }
    
    public TipoAbono getTipoAbonoByDescription(String description){
        return abonosDao.getTipoAbonoByDescription(description);
    }
    
}
