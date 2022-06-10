package model;

import common.model.Usuario;
import java.util.Date;


public class OrderTypeChange {
    
    private Long id;
    private Renta renta;
    private Usuario user;
    private Tipo currentType;
    private Tipo changeType;
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
    

    public Tipo getCurrentType() {
        return currentType;
    }

    public void setCurrentType(Tipo currentType) {
        this.currentType = currentType;
    }

    public Tipo getChangeType() {
        return changeType;
    }

    public void setChangeType(Tipo changeType) {
        this.changeType = changeType;
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
