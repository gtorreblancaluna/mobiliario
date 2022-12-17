package model.providers;

import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import java.sql.Timestamp;
import java.util.List;
import common.model.Renta;
import common.model.Usuario;
import lombok.Data;

@Data
public class OrdenProveedor {
    
    private Long id;
    private Renta renta;
    private Usuario usuario;
    private Proveedor proveedor;
    private String fgActivo;
    private String status;
    private String statusDescription;
    private Timestamp creado;
    private Timestamp actualizado;
    private List<DetalleOrdenProveedor> detalleOrdenProveedorList;
    private List<PagosProveedor> pagosProveedor;
    private String comentario;
    // estas variables son para calcular y mostrar en la vista
    private Float abonos;
    private Float total;

    

    public void setStatus(String status) {
        
        switch(status){
            case ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER:
                this.setStatusDescription(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER);
                break;
            case ApplicationConstants.STATUS_ORDER_PROVIDER_PENDING:
                this.setStatusDescription(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_PENDING);
                break;
            case ApplicationConstants.STATUS_ORDER_PROVIDER_CANCELLED:
                this.setStatusDescription(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_CANCELLED);
                break;
            case ApplicationConstants.STATUS_ORDER_PROVIDER_FINISH:
                this.setStatusDescription(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_FINISH);
                break;
        }
        
      this.status = status;
       
    }
    
    
    public String getStatusFromDescription(String description)throws BusinessException{
        String status=null;
        switch(description){
            case ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER:
                status = ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER;
                break;
            case ApplicationConstants.DS_STATUS_ORDER_PROVIDER_PENDING:
                status = ApplicationConstants.STATUS_ORDER_PROVIDER_PENDING;
                break;
            case ApplicationConstants.DS_STATUS_ORDER_PROVIDER_CANCELLED:
                status = ApplicationConstants.STATUS_ORDER_PROVIDER_CANCELLED;
                break;
            case ApplicationConstants.DS_STATUS_ORDER_PROVIDER_FINISH:
                status = ApplicationConstants.STATUS_ORDER_PROVIDER_FINISH;
                break;
            default:
                throw new BusinessException("No econtramos el tipo de descripción para el estatus de orden proveedor >>> ["+description+"]");
                
        }
        
        return status;
    }
    
    
}
