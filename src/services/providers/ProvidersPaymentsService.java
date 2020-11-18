package services.providers;

import dao.providers.ProvidersPaymentsDAO;
import exceptions.BusinessException;
import exceptions.DataOriginException;
import java.util.List;
import model.providers.PagosProveedor;

/**
 *
 * @author gerardo torreblanca luna
 */
public class ProvidersPaymentsService {
    
    private ProvidersPaymentsService(){}
    
    private static final ProvidersPaymentsService SINGLE_INSTANCE = new ProvidersPaymentsService();
    public static ProvidersPaymentsService getInstance(){
        return SINGLE_INSTANCE;
    }
    
    private final ProvidersPaymentsDAO providersPaymentsDAO = ProvidersPaymentsDAO.getInstance();
    
    
    public List<PagosProveedor> getAllProviderPaymentsByOrderId(Long orderId)throws BusinessException{
        try{
            return providersPaymentsDAO.getAllProviderPaymentsByOrderId(orderId);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public void addPayment(PagosProveedor pagosProveedor)throws BusinessException{
        try{
            providersPaymentsDAO.addPayment(pagosProveedor);
         }catch(DataOriginException e){
            throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
}
