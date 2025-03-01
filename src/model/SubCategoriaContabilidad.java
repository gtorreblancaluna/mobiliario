package model;

import lombok.Data;

@Data
public class SubCategoriaContabilidad {
    
    private Integer subCategoriaContabilidadId;
    private CategoriaContabilidad categoriaContabilidad;
    private String descripcion;
    private String ingreso;
    private String fgActivo;
    
}
