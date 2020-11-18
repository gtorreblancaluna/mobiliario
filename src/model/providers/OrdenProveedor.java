/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.providers;

import exceptions.BusinessException;
import java.sql.Timestamp;
import java.util.List;
import mobiliario.ApplicationConstants;
import model.Renta;
import model.Usuario;

/**
 *
 * @author torreblanca gerardo
 */
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

    public Float getAbonos() {
        return abonos;
    }

    public void setAbonos(Float abonos) {
        this.abonos = abonos;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }
    
    

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
          
        this.statusDescription = statusDescription;
    }
    
    

    public List<PagosProveedor> getPagosProveedor() {
        return pagosProveedor;
    }

    public void setPagosProveedor(List<PagosProveedor> pagosProveedor) {
        this.pagosProveedor = pagosProveedor;
    }
    
    

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Renta getRenta() {
        return renta;
    }

    public void setRenta(Renta renta) {
        this.renta = renta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public String getFgActivo() {
        return fgActivo;
    }

    public void setFgActivo(String fgActivo) {
        this.fgActivo = fgActivo;
    }

    public String getStatus() {
        return status;
    }

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

    public List<DetalleOrdenProveedor> getDetalleOrdenProveedorList() {
        return detalleOrdenProveedorList;
    }

    public void setDetalleOrdenProveedorList(List<DetalleOrdenProveedor> detalleOrdenProveedorList) {
        this.detalleOrdenProveedorList = detalleOrdenProveedorList;
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
