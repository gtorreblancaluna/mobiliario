package services.providers;

import dao.providers.ProvidersDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import java.util.List;
import model.providers.Proveedor;

public class ProvidersService {
    
    private ProvidersService(){}
    private static final ProvidersService SINGLE_INSTANCE = new ProvidersService();
    public static ProvidersService getInstance(){
        return SINGLE_INSTANCE;
    }
    
    private final ProvidersDAO providersDAO = ProvidersDAO.getInstance();
    
    public List<Proveedor> getAll()throws BusinessException{
        try{
            return providersDAO.getAll();
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
    }
    
    public List<Proveedor> searchByData(String data)throws BusinessException{
        try{
            return providersDAO.searchByData(data);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public Proveedor getById(Long id)throws BusinessException{
        try{
            return providersDAO.getById(id);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public void deleteById(Long id)throws BusinessException{
        try{
            providersDAO.deleteById(id);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public void save(Proveedor proveedor)throws BusinessException{
        try{
            providersDAO.save(proveedor);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public void update(Proveedor proveedor)throws BusinessException{
        try{
            providersDAO.update(proveedor);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
}
