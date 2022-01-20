package model;

import java.sql.Timestamp;

public class AsignaCategoria {
    
    private int asignaCategoriaId;
    private Usuario usuario;
    private CategoriaDTO categoria;
    private Timestamp fechaAlta;

    public int getAsignaCategoriaId() {
        return asignaCategoriaId;
    }

    public void setAsignaCategoriaId(int asignaCategoriaId) {
        this.asignaCategoriaId = asignaCategoriaId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

   

    public Timestamp getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Timestamp fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
    
    
    
}
