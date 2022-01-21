
package services;

import dao.AccountDAO;
import java.util.List;
import model.Cuenta;

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
