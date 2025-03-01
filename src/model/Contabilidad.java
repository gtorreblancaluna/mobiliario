package model;

import common.model.Cuenta;
import common.model.Usuario;
import java.sql.Timestamp;
import lombok.Data;

@Data
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
    
}
