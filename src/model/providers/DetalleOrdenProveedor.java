package model.providers;

import common.constants.ApplicationConstants;
import java.sql.Timestamp;
import common.model.Articulo;
import lombok.Data;

@Data
public class DetalleOrdenProveedor {
    
    private Long id;
    private Long idOrdenProveedor;
    private Articulo articulo;
    private Float cantidad;
    private Float precio;
    private String fgActivo;
    private Timestamp creado;
    private Timestamp actualizado;
    private String comentario;
    private String status;
    private String statusDescription;
    private DetailOrderProviderType detailOrderProviderType;

    public DetailOrderProviderType getDetailOrderProviderType() {
        return detailOrderProviderType;
    }

    public void setDetailOrderProviderType(DetailOrderProviderType detailOrderProviderType) {
        this.detailOrderProviderType = detailOrderProviderType;
    }    


    public void setStatus(String status) {
         switch(status){
            case ApplicationConstants.STATUS_ORDER_DETAIL_PROVIDER_PENDING:
                this.setStatusDescription(ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_PENDING);
                break;
            case ApplicationConstants.STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED:
                this.setStatusDescription(ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED);
                break;
        }
        
        this.status = status;
    }   
    
}
