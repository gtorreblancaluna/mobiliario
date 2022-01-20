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
public class DetalleRenta {
    private int detalleRentaId;
    private int rentaId;
    private float cantidad;
    private Articulo articulo;
    private float precioUnitario;
    private String comentario;
    private String seDesconto;
    private float porcentajeDescuento;

    public float getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(float porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }
    
    

    @Override
    public String toString() {
        return "DetalleRenta{" + "detalleRentaId=" + detalleRentaId + ", rentaId=" + rentaId + ", cantidad=" + cantidad + ", articulo=" + articulo + ", precioUnitario=" + precioUnitario + ", comentario=" + comentario + ", seDesconto=" + seDesconto + '}';
    }
    
    

    public int getDetalleRentaId() {
        return detalleRentaId;
    }

    public void setDetalleRentaId(int detalleRentaId) {
        this.detalleRentaId = detalleRentaId;
    }

    public int getRentaId() {
        return rentaId;
    }

    public void setRentaId(int rentaId) {
        this.rentaId = rentaId;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getSeDesconto() {
        return seDesconto;
    }

    public void setSeDesconto(String seDesconto) {
        this.seDesconto = seDesconto;
    }
    
    
}
