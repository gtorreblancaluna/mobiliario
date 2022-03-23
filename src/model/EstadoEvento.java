package model;

public class EstadoEvento {
    
    
    
    private Integer estadoId;
    private String descripcion;

    public EstadoEvento(int estadoId, String descripcion) {
        this.estadoId = estadoId;
        this.descripcion = descripcion;
    }
    
    public EstadoEvento(int estadoId) {
        this.estadoId = estadoId;
    }

    public EstadoEvento() {
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
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
