package services.providers;

import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import dao.providers.ProviderStatusBitacoraDAO;
import java.sql.Timestamp;
import java.util.List;
import model.providers.CatalogStatusProvider;
import model.providers.StatusProviderByRenta;


public class ProviderStatusBitacoraService {
    
    private ProviderStatusBitacoraService(){}
    
    private static ProviderStatusBitacoraService SINGLE_INSTANCE;
    
    public static synchronized ProviderStatusBitacoraService getInstance(){
        if (SINGLE_INSTANCE == null) {
            SINGLE_INSTANCE = new ProviderStatusBitacoraService();
        }
        return SINGLE_INSTANCE;
    }
    
    private final ProviderStatusBitacoraDAO providerStatusBitacoraDAO = ProviderStatusBitacoraDAO.getInstance();
    
    public StatusProviderByRenta getLastStatusProviderByRenta(Long rentaId) throws DataOriginException{
        return providerStatusBitacoraDAO.getLastStatusProviderByRenta(rentaId);
    }
    
    public List<StatusProviderByRenta> getStatusProviderByRenta(Long rentaId)throws DataOriginException{
        return providerStatusBitacoraDAO.getStatusProviderByRenta(rentaId);
    }
    
    public void insertStatusProvicerByRenta (StatusProviderByRenta statusProviderByRenta) throws BusinessException{
        try {
            statusProviderByRenta.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            statusProviderByRenta.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            statusProviderByRenta.setFgActive(ApplicationConstants.FG_ACTIVE_TRUE);
            providerStatusBitacoraDAO.insertStatusProvicerByRenta(statusProviderByRenta);
        } catch (DataOriginException dataOriginException) {
            throw new BusinessException(dataOriginException.getMessage(),dataOriginException);
        }
    }
    
    public List<CatalogStatusProvider> getAll()throws BusinessException{
        try{
            return providerStatusBitacoraDAO.getCatalogStatusProvider();
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
    }
    
    
}
