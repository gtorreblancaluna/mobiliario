/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import java.util.List;

/**
 *
 * @author jerry
 */
public class Renta {
    
    private int rentaId;
    private int estadoId;
    private Cliente cliente;
    private Usuario usuario;
    private String fechaPedido;
    private String fechaEntrega;
    private String horaEntrega;
    private String fechaDevolucion;
    private String descripcion;
    private float descuento;
    private float cantidadDescuento;
    private float iva;
    private String comentario;
    private int usuarioChoferId;
    private int folio;
    private String stock;
    private Tipo tipo;
    private Double totalAbonos;
    private List<DetalleRenta> detalleRenta;
    private List<Abono> abonos;
    private String horaDevolucion;
    private String fechaEvento;
    private float depositoGarantia;
    private float envioRecoleccion;
    private Usuario chofer;
    private EstadoEvento estado;
    private String mostrarPreciosPdf;
    private String descripcionCobranza;
    private float subTotal;
    // nos servira para almacenar el total y mostrarlo en la consulta
    private float total;
    // almacena el total de faltantes
    private float totalFaltantes;
    // dato del faltante por cubrir
    private float totalFaltantesPorCubrir;

    public Double getTotalAbonos() {
        return totalAbonos;
    }

    public void setTotalAbonos(Double totalAbonos) {
        this.totalAbonos = totalAbonos;
    }
    
    

    public float getTotalFaltantesPorCubrir() {
        return totalFaltantesPorCubrir;
    }

    public void setTotalFaltantesPorCubrir(float totalFaltantesPorCubrir) {
        this.totalFaltantesPorCubrir = totalFaltantesPorCubrir;
    }
    
    

    public float getTotalFaltantes() {
        return totalFaltantes;
    }

    public void setTotalFaltantes(float totalFaltantes) {
        this.totalFaltantes = totalFaltantes;
    }

    
    
    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
    
    

    public float getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(float subTotal) {
        this.subTotal = subTotal;
    }
    
    

    public String getDescripcionCobranza() {
        return descripcionCobranza;
    }

    public void setDescripcionCobranza(String descripcionCobranza) {
        this.descripcionCobranza = descripcionCobranza;
    }
    
    

    public String getMostrarPreciosPdf() {
        return mostrarPreciosPdf;
    }

    public void setMostrarPreciosPdf(String mostrarPreciosPdf) {
        this.mostrarPreciosPdf = mostrarPreciosPdf;
    }
    
    

    public EstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
    }
    
    

    public Usuario getChofer() {
        return chofer;
    }

    public void setChofer(Usuario chofer) {
        this.chofer = chofer;
    }
    
    

    public String getHoraDevolucion() {
        return horaDevolucion;
    }

    public void setHoraDevolucion(String horaDevolucion) {
        this.horaDevolucion = horaDevolucion;
    }

    public String getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(String fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public float getDepositoGarantia() {
        return depositoGarantia;
    }

    public void setDepositoGarantia(float depositoGarantia) {
        this.depositoGarantia = depositoGarantia;
    }

    public float getEnvioRecoleccion() {
        return envioRecoleccion;
    }

    public void setEnvioRecoleccion(float envioRecoleccion) {
        this.envioRecoleccion = envioRecoleccion;
    }

   
    

    
    

    public List<Abono> getAbonos() {
        return abonos;
    }

    public void setAbonos(List<Abono> abonos) {
        this.abonos = abonos;
    }
    
    

    public List<DetalleRenta> getDetalleRenta() {
        return detalleRenta;
    }

    public void setDetalleRenta(List<DetalleRenta> detalleRenta) {
        this.detalleRenta = detalleRenta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }    

    public int getRentaId() {
        return rentaId;
    }

    public void setRentaId(int rentaId) {
        this.rentaId = rentaId;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
   

    public String getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(String horaEntrega) {
        this.horaEntrega = horaEntrega;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

   

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getDescuento() {
        return descuento;
    }

    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }

   

    public float getCantidadDescuento() {
        return cantidadDescuento;
    }

    public void setCantidadDescuento(float cantidadDescuento) {
        this.cantidadDescuento = cantidadDescuento;
    }

    public float getIva() {
        return iva;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getUsuarioChoferId() {
        return usuarioChoferId;
    }

    public void setUsuarioChoferId(int usuarioChoferId) {
        this.usuarioChoferId = usuarioChoferId;
    }

    public int getFolio() {
        return folio;
    }

    public void setFolio(int folio) {
        this.folio = folio;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    
    
    
    
    
}
