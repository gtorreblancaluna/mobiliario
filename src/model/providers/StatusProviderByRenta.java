package model.providers;

import common.model.Renta;
import common.model.Usuario;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class StatusProviderByRenta {
    
    private Long id;
    private Renta renta;
    private Usuario user;
    private CatalogStatusProvider catalogStatusProvider;
    private String comment;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String fgActive;
    
}
