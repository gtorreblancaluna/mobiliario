package model;

import common.model.Renta;
import common.model.EstadoEvento;
import common.model.Usuario;
import java.util.Date;

public class OrderStatusChange {
   
    private Long id;
    private Renta renta;
    private Usuario user;
    private EstadoEvento currentStatus;
    private EstadoEvento changeStatus;
    private Date createdAt;
    private Date updatedAt;
    private String fgActive;

    public Renta getRenta() {
        return renta;
    }

    public void setRenta(Renta renta) {
        this.renta = renta;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

   

    public EstadoEvento getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(EstadoEvento currentStatus) {
        this.currentStatus = currentStatus;
    }

    public EstadoEvento getChangeStatus() {
        return changeStatus;
    }

    public void setChangeStatus(EstadoEvento changeStatus) {
        this.changeStatus = changeStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFgActive() {
        return fgActive;
    }

    public void setFgActive(String fgActive) {
        this.fgActive = fgActive;
    }
    
    
    
}
