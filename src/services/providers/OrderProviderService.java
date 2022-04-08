package services.providers;

import dao.providers.OrderProviderDAO;
import dao.providers.ProvidersPaymentsDAO;
import exceptions.BusinessException;
import exceptions.DataOriginException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import mobiliario.ApplicationConstants;
import model.Articulo;
import model.providers.DetalleOrdenProveedor;
import model.providers.OrdenProveedor;
import model.providers.DetailOrderProviderType;
import parametersVO.ParameterOrderProvider;


public class OrderProviderService {
    
    // singlenton instance
    private static final OrderProviderService SINGLE_INSTANCE = new OrderProviderService();
    
    private OrderProviderService(){}
    
    public static OrderProviderService getInstance() {
      return SINGLE_INSTANCE;
    }
    
    private final OrderProviderDAO orderProviderDAO = OrderProviderDAO.getInstance();
    private final ProvidersPaymentsDAO providersPaymentsDAO = ProvidersPaymentsDAO.getInstance();
    
    public String changeStatusDetailOrderById(Long id)throws BusinessException{
        
        String currentStatus;
        
        try{
            DetalleOrdenProveedor detalle 
                    = orderProviderDAO.getDetailOrderById(id);
            
            String statusToChange = null;
            
            if(detalle == null || detalle.getId() == null){
                throw new BusinessException("Error inesperado, no se obtuvo el detalle orden");
            }else{
                if(detalle.getStatus().equals(ApplicationConstants.STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED)){
                    statusToChange = ApplicationConstants.STATUS_ORDER_DETAIL_PROVIDER_PENDING;
                    currentStatus = ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_PENDING;
                }else{
                    currentStatus = ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED;
                    statusToChange = ApplicationConstants.STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED;
                }
                
                orderProviderDAO.changeStatusDetailOrderById(id,statusToChange);
            }
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
        return currentStatus;
    }
    
    public void saveOrder(OrdenProveedor orden)throws BusinessException{
        try{
            orderProviderDAO.saveOrder(orden);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public void updateOrder(OrdenProveedor orden)throws BusinessException{
        try{
            orderProviderDAO.updateOrder(orden);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
    }
    
    public void updateDetailOrderProvider(Long detalleOrdenProveedorId, Float cantidad, Float precio, String comentario, Long detailOrderProviderType)throws BusinessException{
        
        DetalleOrdenProveedor detail = new DetalleOrdenProveedor();
        DetailOrderProviderType type = new DetailOrderProviderType();
        
        type.setId(detailOrderProviderType);
        
        detail.setId(detalleOrdenProveedorId);
        detail.setCantidad(cantidad);
        detail.setPrecio(precio);
        detail.setComentario(comentario);
        detail.setDetailOrderProviderType(type);
        detail.setActualizado(new Timestamp(System.currentTimeMillis()));
        detail.setStatus("1");
        
        try{
            orderProviderDAO.updateDetailOrderProvider(detail);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
    }
    
    public OrdenProveedor getOrderById(Long id)throws BusinessException{
        
        OrdenProveedor ordenProveedor;
        try{
            ordenProveedor =  orderProviderDAO.getOrderById(id);
            
            if(ordenProveedor != null){
                ordenProveedor.setPagosProveedor(
                        providersPaymentsDAO.getAllProviderPaymentsByOrderId(ordenProveedor.getId()));
            }
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
        
        return ordenProveedor;
    }
    
    public List<OrdenProveedor> getOrdersByRentaId(Integer rentaId)throws BusinessException{
        try{
            return orderProviderDAO.getOrdersByRentaId(rentaId);
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
    }
    
    public List<OrdenProveedor> getOrdersByParameters(ParameterOrderProvider parameter)throws BusinessException{

        try{
            return orderProviderDAO.getOrdersByParameters(parameter);           
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
     
    }
    
    public List<DetalleOrdenProveedor> getDetailProvider(Map<String,Object> map)throws BusinessException{
        try{
         return orderProviderDAO.getDetailProvider(map);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage());
        }
    }
    
    public List<DetailOrderProviderType> getTypesOrderDetailProvider ()throws DataOriginException{
        
         return orderProviderDAO.getTypesOrderDetailProvider();
        
    }
    
    
}
