package model.providers;

import common.constants.ApplicationConstants;
import java.sql.Timestamp;
import model.Articulo;

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

    public String getStatus() {
        return status;
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

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
    
    

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    
    

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdOrdenProveedor() {
        return idOrdenProveedor;
    }

    public void setIdOrdenProveedor(Long idOrdenProveedor) {
        this.idOrdenProveedor = idOrdenProveedor;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public String getFgActivo() {
        return fgActivo;
    }

    public void setFgActivo(String fgActivo) {
        this.fgActivo = fgActivo;
    }

    public Timestamp getCreado() {
        return creado;
    }

    public void setCreado(Timestamp creado) {
        this.creado = creado;
    }

    public Timestamp getActualizado() {
        return actualizado;
    }

    public void setActualizado(Timestamp actualizado) {
        this.actualizado = actualizado;
    }
    
    
    
}
