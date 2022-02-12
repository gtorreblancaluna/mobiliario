package services;


import dao.CustomerDao;
import exceptions.BusinessException;
import exceptions.DataOriginException;
import java.util.List;
import model.Cliente;


public class CustomerService {
    
    private static final CustomerService SINGLE_INSTANCE = null;
    private final CustomerDao customerDao;

    private CustomerService() {
        customerDao = CustomerDao.getInstance();
    }
    
    public static CustomerService getInstance() {
        if (SINGLE_INSTANCE == null) {
            return new CustomerService();
        }
        return SINGLE_INSTANCE;
    }
    
    
    public List<Cliente> obtenerClientesActivos () throws BusinessException {
        try {
            return customerDao.obtenerClientesActivos();
        } catch (DataOriginException e) {
            throw new BusinessException (e.getMessage(),e);
        }
    }
    
    public Cliente obtenerClientePorId(Long id)throws BusinessException {
         try {
            return customerDao.obtenerClientePorId(id);
        } catch (DataOriginException e) {
            throw new BusinessException (e.getMessage(),e);
        }
    }
    
}
