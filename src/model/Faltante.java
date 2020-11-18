/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author jerry
 */
public class Faltante {
    
    private int faltanteId;
    private Articulo articulo;
    private Renta renta;
    private Usuario usuario;
    private String fechaRegistro;
    private float cantidad;
    private String comentario;
    private int fgFaltante;
    private int fgDevolucion;
    private int fgActivo;
    private int fgAccidenteTrabajo;
    private Float precioCobrar;

    public Float getPrecioCobrar() {
        return precioCobrar;
    }

    public void setPrecioCobrar(Float precioCobrar) {
        this.precioCobrar = precioCobrar;
    }
    
    

    public int getFgAccidenteTrabajo() {
        return fgAccidenteTrabajo;
    }

    public void setFgAccidenteTrabajo(int fgAccidenteTrabajo) {
        this.fgAccidenteTrabajo = fgAccidenteTrabajo;
    }
    
    

    public int getFaltanteId() {
        return faltanteId;
    }

    public void setFaltanteId(int faltanteId) {
        this.faltanteId = faltanteId;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public Renta getRenta() {
        return renta;
    }

    public void setRenta(Renta renta) {
        this.renta = renta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getFgFaltante() {
        return fgFaltante;
    }

    public void setFgFaltante(int fgFaltante) {
        this.fgFaltante = fgFaltante;
    }

    public int getFgDevolucion() {
        return fgDevolucion;
    }

    public void setFgDevolucion(int fgDevolucion) {
        this.fgDevolucion = fgDevolucion;
    }

    public int getFgActivo() {
        return fgActivo;
    }

    public void setFgActivo(int fgActivo) {
        this.fgActivo = fgActivo;
    }
    
    
    
}
