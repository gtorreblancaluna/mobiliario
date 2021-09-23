
package model;

import java.sql.Timestamp;

public class Articulo {
    
    private int articuloId;
    private int categoriaId;
    private int usuarioId;
    private Color color;
    private float cantidad;
    private String descripcion;
    private String fechaIngreso;
    private float precioCompra;
    private float precioRenta;
    private String activo;
    private float stock;
    private String codigo;
    private float enRenta;
    private CategoriaDTO categoria;
    
    private String rentados;
    private String faltantes;
    private String reparacion;
    private String accidenteTrabajo;
    private String devolucion;
    // dato para actualizar la ultima fecha de modificacion
    private Timestamp fechaUltimaModificacion;
    
    private Float totalCompras;
    private Float utiles;

    public Float getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(Float totalCompras) {
        this.totalCompras = totalCompras;
    }

    public Float getUtiles() {
        return utiles;
    }

    public void setUtiles(Float utiles) {
        this.utiles = utiles;
    }
    
    

    public Timestamp getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }
    public void setFechaUltimaModificacion(Timestamp fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }
    public String getRentados() {
        return rentados;
    }

    public void setRentados(String rentados) {
        this.rentados = rentados;
    }

    public String getFaltantes() {
        return faltantes;
    }

    public void setFaltantes(String faltantes) {
        this.faltantes = faltantes;
    }

    public String getReparacion() {
        return reparacion;
    }

    public void setReparacion(String reparacion) {
        this.reparacion = reparacion;
    }

    public String getAccidenteTrabajo() {
        return accidenteTrabajo;
    }

    public void setAccidenteTrabajo(String accidenteTrabajo) {
        this.accidenteTrabajo = accidenteTrabajo;
    }

    public String getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(String devolucion) {
        this.devolucion = devolucion;
    }

    
    
    

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }
    
    

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

   
    

    public int getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(int articuloId) {
        this.articuloId = articuloId;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public float getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(float precioCompra) {
        this.precioCompra = precioCompra;
    }

    public float getPrecioRenta() {
        return precioRenta;
    }

    public void setPrecioRenta(float precioRenta) {
        this.precioRenta = precioRenta;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public float getStock() {
        return stock;
    }

    public void setStock(float stock) {
        this.stock = stock;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public float getEnRenta() {
        return enRenta;
    }

    public void setEnRenta(float enRenta) {
        this.enRenta = enRenta;
    }

    @Override
    public String toString() {
        return "Articulo{" + "articuloId=" + articuloId + ", categoriaId=" + categoriaId + ", usuarioId=" + usuarioId + ", color=" + color + ", cantidad=" + cantidad + ", descripcion=" + descripcion + ", fechaIngreso=" + fechaIngreso + ", precioCompra=" + precioCompra + ", precioRenta=" + precioRenta + ", activo=" + activo + ", stock=" + stock + ", codigo=" + codigo + ", enRenta=" + enRenta + ", categoria=" + categoria + ", rentados=" + rentados + ", faltantes=" + faltantes + ", reparacion=" + reparacion + ", accidenteTrabajo=" + accidenteTrabajo + ", devolucion=" + devolucion + '}';
    }

    
    
    
    
}
