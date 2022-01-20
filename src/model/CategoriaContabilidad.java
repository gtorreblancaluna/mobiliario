package model;

import java.sql.Timestamp;

public class CategoriaContabilidad {
    
    private Integer categoriaContabilidadId;
    private String descripcion;
    private String fgActivo;
    private Timestamp fechaRegistro;

    public Integer getCategoriaContabilidadId() {
        return categoriaContabilidadId;
    }

    public void setCategoriaContabilidadId(Integer categoriaContabilidadId) {
        this.categoriaContabilidadId = categoriaContabilidadId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFgActivo() {
        return fgActivo;
    }

    public void setFgActivo(String fgActivo) {
        this.fgActivo = fgActivo;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    
    
}
