package model.material.inventory;

import java.util.Date;

public class MaterialArea {
    
    private Long id;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    
    public MaterialArea () {}

    public MaterialArea(Long id, String description) {
        this.id = id;
        this.description = description;
    }
    
    

    public MaterialArea(Long id, String description, Date createdAt, Date updatedAt) {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public MaterialArea(Long id) {
        this.id = id;
    }

    public MaterialArea(String description) {
        this.description = description;
    }
    
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    

    @Override
    public String toString() {
        return description;
    }
    
    
    
    
}
