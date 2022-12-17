
package model.providers;

import lombok.Data;

@Data
public class DetailOrderProviderType {
    
    private Long id;
    private String description;
    private String fgActive;

    public DetailOrderProviderType() {
    }

    public DetailOrderProviderType(Long id) {
        this.id = id;
    }

    public DetailOrderProviderType(Long id, String description) {
        this.id = id;
        this.description = description;
    }
    
    @Override
    public String toString() {
        return description;
    }
    
    
    
}
