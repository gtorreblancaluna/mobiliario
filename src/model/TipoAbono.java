package model;

public class TipoAbono {
    private int tipoAbonoId;
    private String descripcion;
    private char fgActivo;
    private String fechaRegistro;
    private Cuenta cuenta;

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }
    
    

    public int getTipoAbonoId() {
        return tipoAbonoId;
    }

    public void setTipoAbonoId(int tipoAbonoId) {
        this.tipoAbonoId = tipoAbonoId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public char getFgActivo() {
        return fgActivo;
    }

    public void setFgActivo(char fgActivo) {
        this.fgActivo = fgActivo;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    
}
