package model;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class CategoriaContabilidad {
    
    private Integer categoriaContabilidadId;
    private String descripcion;
    private String fgActivo;
    private Timestamp fechaRegistro;
    
}
