/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import dao.AccountDAO;
import java.util.List;
import model.Cuenta;

/**
 *
 * @author Gerardo Torreblanca
 */
public class AccountService {
    
    private AccountDAO accountDao = new AccountDAO();
    
    public List<Cuenta> getAccounts(){
    
        return accountDao.getAccounts();
    }
    
    public Cuenta getAccountByDescription(String description){
        return accountDao.getAccountByDescription(description);
    }
    
    public void insertAccount(Cuenta cuenta){
        accountDao.insertAccount(cuenta);
    }
    
}
