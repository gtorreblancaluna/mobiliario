package model;

import common.model.Articulo;
import common.model.Renta;
import common.model.Usuario;
import lombok.Data;

@Data
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
    
}
