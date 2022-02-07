
package model;

public class CategoriaDTO {
    
    private int categoriaId;
    private String descripcion;

    public CategoriaDTO() {
    }
    
    

    public CategoriaDTO(int categoriaId, String descripcion) {
        this.categoriaId = categoriaId;
        this.descripcion = descripcion;
    }
    
    

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
    
    
    
    
    
}
