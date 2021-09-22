package model.material.inventory;

import java.util.Date;

public class MaterialArea {
    private Long id;
    private String description;
    private Date createdAt;
    private Date uodatedAt;

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

    public Date getUodatedAt() {
        return uodatedAt;
    }

    public void setUodatedAt(Date uodatedAt) {
        this.uodatedAt = uodatedAt;
    }
    
    
    
}
