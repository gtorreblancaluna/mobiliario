package model.providers;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class CatalogStatusProvider {
    
    public CatalogStatusProvider () {}
    
    public CatalogStatusProvider(Long id, String description) {
        this.id = id;
        this.description = description;
    }
    
    private Long id;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String fgActive;
    
    @Override
    public String toString() {
        return description;
    }
    
}
