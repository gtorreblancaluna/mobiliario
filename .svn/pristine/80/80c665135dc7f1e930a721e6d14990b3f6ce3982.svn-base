/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import dao.AbonosDAO;
import java.util.List;
import model.Abono;

/**
 *
 * @author idscomercial
 */
public class AbonosService {
    
    private AbonosDAO abonosDAO = new AbonosDAO();
    
    public List<Abono> getAbonosByDates(String initDate,String endDate){
       
        return abonosDAO.getAbonosByDates(initDate, endDate);
    }
    
    public List<Abono> getAbonosByDatesGroupByBankAccounts(String initDate,String endDate){
       
        return abonosDAO.getAbonosByDatesGroupByBankAccounts(initDate, endDate);
    }
    
}
