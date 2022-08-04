/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import common.model.Cuenta;
import common.model.Usuario;
import java.sql.Timestamp;

/**
 *
 * @author idscomercial
 */
public class Contabilidad {
    
    private Integer contabilidadId;
    private SubCategoriaContabilidad subCategoriaContabilidad;
    private Usuario usuario;
    private Timestamp fechaRegistro;
    private String comentario;
    private String fgActivo;
    private Float cantidad;
    private Timestamp fechaMovimiento;
    
    private Float totalIngresos;
    private Float totalEgresos;
    
    private Cuenta cuenta;

    public Timestamp getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Timestamp fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    
    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }
    
    

    public Float getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(Float totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public Float getTotalEgresos() {
        return totalEgresos;
    }

    public void setTotalEgresos(Float totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

   

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }
    
    

    public Integer getContabilidadId() {
        return contabilidadId;
    }

    public void setContabilidadId(Integer contabilidadId) {
        this.contabilidadId = contabilidadId;
    }

    public SubCategoriaContabilidad getSubCategoriaContabilidad() {
        return subCategoriaContabilidad;
    }

    public void setSubCategoriaContabilidad(SubCategoriaContabilidad subCategoriaContabilidad) {
        this.subCategoriaContabilidad = subCategoriaContabilidad;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
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
    
    
    
}
