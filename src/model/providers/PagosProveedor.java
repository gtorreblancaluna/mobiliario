package model.providers;

import java.sql.Timestamp;
import model.TipoAbono;
import model.Usuario;

public class PagosProveedor {
    
    private Long id;
    private OrdenProveedor ordenProveedor;
    private Usuario usuario;
    private Float cantidad;
    private String comentario;
    private String fgActivo;
    private Timestamp creado;
    private Timestamp actualizado;
    private TipoAbono tipoAbono;

    public TipoAbono getTipoAbono() {
        return tipoAbono;
    }

    public void setTipoAbono(TipoAbono tipoAbono) {
        this.tipoAbono = tipoAbono;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrdenProveedor getOrdenProveedor() {
        return ordenProveedor;
    }

    public void setOrdenProveedor(OrdenProveedor ordenProveedor) {
        this.ordenProveedor = ordenProveedor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFgActivo() {
        return fgActivo;
    }

    public void setFgActivo(String fgActivo) {
        this.fgActivo = fgActivo;
    }

    public Timestamp getCreado() {
        return creado;
    }

    public void setCreado(Timestamp creado) {
        this.creado = creado;
    }

    public Timestamp getActualizado() {
        return actualizado;
    }

    public void setActualizado(Timestamp actualizado) {
        this.actualizado = actualizado;
    }
    
    
}
