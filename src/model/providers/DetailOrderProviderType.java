
package model.providers;


public class DetailOrderProviderType {
    
    private Long id;
    private String description;
    private String fgActive;

    public DetailOrderProviderType() {
    }
    
    

    public DetailOrderProviderType(Long id, String description) {
        this.id = id;
        this.description = description;
    }
    
    public DetailOrderProviderType(Long id) {
        this.id = id;
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

    public String getFgActive() {
        return fgActive;
    }

    public void setFgActive(String fgActive) {
        this.fgActive = fgActive;
    }

    @Override
    public String toString() {
        return description;
    }
    
    
    
}
