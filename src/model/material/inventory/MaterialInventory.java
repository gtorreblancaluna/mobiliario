package model.material.inventory;

import java.util.Date;

public class MaterialInventory {
    
    private Long id;
    private MaterialArea area;
    private Float stock;
    // unidad de medida
    private MeasurementUnit measurementUnit;
    // cantidad em unidad de medida para realizar la compra
    private Float purchaseAmount;
    // unidad de medida para compra
    private MeasurementUnit measurementUnitPurchase;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private String fgActive;

    public MaterialInventory(Long id) {
        this.id = id;
    }

    public MaterialInventory() {
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MaterialArea getArea() {
        return area;
    }

    public void setArea(MaterialArea area) {
        this.area = area;
    }

    public Float getStock() {
        return stock;
    }

    public void setStock(Float stock) {
        this.stock = stock;
    }

    public MeasurementUnit getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(MeasurementUnit measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public MeasurementUnit getMeasurementUnitPurchase() {
        return measurementUnitPurchase;
    }

    public void setMeasurementUnitPurchase(MeasurementUnit measurementUnitPurchase) {
        this.measurementUnitPurchase = measurementUnitPurchase;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(Float purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
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
