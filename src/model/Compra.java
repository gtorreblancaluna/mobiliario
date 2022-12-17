
package model;

import common.model.Articulo;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class Compra {
    
    private Integer id;
    private Integer idArticulo;
    private Articulo articulo;
    private String comentario;
    private Float cantidad;
    private Float precioCompra;
    private String fgActivo;
    private Timestamp creado;
    private Timestamp actualizado;
    
}
