/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.providers;

import dao.providers.OrderProviderDAO;
import dao.providers.ProvidersPaymentsDAO;
import exceptions.BusinessException;
import exceptions.DataOriginException;
import java.util.List;
import java.util.Map;
import mobiliario.ApplicationConstants;
import model.providers.DetalleOrdenProveedor;
import model.providers.OrdenProveedor;
import model.providers.PagosProveedor;
import parametersVO.ParameterOrderProvider;

/**
 *
 * @author idscomercial
 */
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
    
    public OrdenProveedor getOrderById(Long id)throws BusinessException{
        
        OrdenProveedor ordenProveedor;
        try{
            ordenProveedor =  orderProviderDAO.getOrderById(id);
            
            if(ordenProveedor!= null){
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
        
        List<OrdenProveedor> list;
        try{
            list = orderProviderDAO.getOrdersByParameters(parameter);
            
            if(list != null && list.size()>0){
                // add payments
                for(OrdenProveedor orden : list){
                    orden.setPagosProveedor(providersPaymentsDAO.getAllProviderPaymentsByOrderId(orden.getId()));
                    
                    // calculate total payments to provider
                    if(orden.getPagosProveedor() != null && orden.getPagosProveedor().size()>0){
                        float fPagos = 0f;
                        for(PagosProveedor pagos : orden.getPagosProveedor()){
                            fPagos += pagos.getCantidad();
                        }
                        orden.setAbonos(fPagos);
                    }else{ // end if
                        orden.setAbonos(0f);
                    }
                    // calculate total amount by order
                     if(orden.getDetalleOrdenProveedorList() != null && 
                            orden.getDetalleOrdenProveedorList().size()>0){
                            float fTotal = 0f;
                            for(DetalleOrdenProveedor detalle : orden.getDetalleOrdenProveedorList()){
                                fTotal += (detalle.getCantidad() * detalle.getPrecio());
                            }
                            orden.setTotal(fTotal);
                     }else{ // end if
                            orden.setTotal(0f);
                     }
                } // end for
                
            }
            
        }catch(DataOriginException e){
          throw new BusinessException(e.getMessage(),e.getCause());
        }
     
        return list;
    }
    
    public List<DetalleOrdenProveedor> getDetailProvider(Map<String,Object> map)throws BusinessException{
        try{
         return orderProviderDAO.getDetailProvider(map);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage());
        }
    }
    
    
}
