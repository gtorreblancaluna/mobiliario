package model;

public class SubCategoriaContabilidad {
    
    private Integer subCategoriaContabilidadId;
    private CategoriaContabilidad categoriaContabilidad;
    private String descripcion;
    private String ingreso;
    private String fgActivo;

    public String getFgActivo() {
        return fgActivo;
    }

    public void setFgActivo(String fgActivo) {
        this.fgActivo = fgActivo;
    }
    
    

    public Integer getSubCategoriaContabilidadId() {
        return subCategoriaContabilidadId;
    }

    public void setSubCategoriaContabilidadId(Integer subCategoriaContabilidadId) {
        this.subCategoriaContabilidadId = subCategoriaContabilidadId;
    }

    public CategoriaContabilidad getCategoriaContabilidad() {
        return categoriaContabilidad;
    }

    public void setCategoriaContabilidad(CategoriaContabilidad categoriaContabilidad) {
        this.categoriaContabilidad = categoriaContabilidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIngreso() {
        return ingreso;
    }

    public void setIngreso(String ingreso) {
        this.ingreso = ingreso;
    }
    
    
    
}
