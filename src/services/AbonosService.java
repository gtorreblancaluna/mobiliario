package services;

import dao.AbonosDAO;
import common.exceptions.DataOriginException;
import java.util.List;
import java.util.Map;
import common.model.Abono;


public class AbonosService {
    
    private AbonosService(){}
    private static final AbonosService SINGLE_INSTANCE = null;
    
    private AbonosDAO abonosDAO = AbonosDAO.getInstance();
    
    public static AbonosService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new AbonosService();
        }
        return SINGLE_INSTANCE;
    } 
    
    public List<Abono> getByParameters(Map<String,Object> parameters) throws DataOriginException {
       
        return abonosDAO.getByParameters(parameters);
    }
    
    public List<Abono> getAbonosByDates(String initDate,String endDate) throws DataOriginException {
       
        return abonosDAO.getAbonosByDates(initDate, endDate);
    }
    
    public List<Abono> getAbonosByDatesGroupByBankAccounts(String initDate,String endDate) throws DataOriginException {
       
        return abonosDAO.getAbonosByDatesGroupByBankAccounts(initDate, endDate);
    }
    
}
