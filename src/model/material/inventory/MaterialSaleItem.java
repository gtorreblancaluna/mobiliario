package model.material.inventory;

import java.util.Date;
import common.model.Articulo;
import common.model.providers.Proveedor;

public class MaterialSaleItem {
    
    private Long id;
    private MaterialInventory materialInventory;
    private Proveedor provider;
    private Articulo item;
    private Float amount;
    private Date createdAt;
    private Date updatedAt;
    private String fgActive;

    public MaterialSaleItem(Long id) {
        this.id = id;
    }

    public MaterialSaleItem() {
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MaterialInventory getMaterialInventory() {
        return materialInventory;
    }

    public void setMaterialInventory(MaterialInventory materialInventory) {
        this.materialInventory = materialInventory;
    }

    public Proveedor getProvider() {
        return provider;
    }

    public void setProvider(Proveedor provider) {
        this.provider = provider;
    }

    public Articulo getItem() {
        return item;
    }

    public void setItem(Articulo item) {
        this.item = item;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
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
