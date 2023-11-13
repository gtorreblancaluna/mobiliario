package services;

import dao.CustomerDao;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import java.util.List;
import common.model.Cliente;
import common.utilities.UtilityCommon;
import javax.mail.MessagingException;


public class CustomerService {
    
    private static CustomerService SINGLE_INSTANCE = null;
    private final CustomerDao customerDao = CustomerDao.getInstance();

    private CustomerService() {}
    
    private synchronized static void createInstance() {
        if (SINGLE_INSTANCE == null) {
            SINGLE_INSTANCE = new CustomerService();
        }
    }
    
    public static CustomerService getInstance() {
        if (SINGLE_INSTANCE == null) createInstance();
            return SINGLE_INSTANCE;
    }
    
    
    public List<Cliente> getAll () throws BusinessException {
        try {
            return customerDao.getAll();
        } catch (DataOriginException e) {
            throw new BusinessException (e.getMessage(),e);
        }
    }
    
    public Cliente getById(Long id)throws BusinessException {
         try {
            return customerDao.getById(id);
        } catch (DataOriginException e) {
            throw new BusinessException (e.getMessage(),e);
        }
    }
    
    public void saveOrUpdate (Cliente cliente) throws BusinessException {
        
        if (cliente.getNombre().isEmpty() || cliente.getApellidos().isEmpty()) {
            throw new BusinessException("Nombre y apellidos son requeridos.");
        }
        
        if (cliente.getSocialMedia() == null || cliente.getSocialMedia().getId().equals(0)) {
            throw new BusinessException("Medio de contacto es requerido.");
        }
        
        if (!cliente.getEmail().isEmpty()) {
            try {
                UtilityCommon.isEmail(cliente.getEmail());
            } catch (MessagingException messagingException) {
                throw new BusinessException("Email no válido.");
            }
        }
        
        customerDao.saveOrUpdate(cliente);
        
    }
    
}
