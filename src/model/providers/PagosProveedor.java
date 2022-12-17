package model.providers;

import java.sql.Timestamp;
import common.model.TipoAbono;
import common.model.Usuario;
import lombok.Data;

@Data
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

    
}
