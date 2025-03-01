package model;

import common.model.CategoriaDTO;
import common.model.Usuario;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class AsignaCategoria {
    
    private int asignaCategoriaId;
    private Usuario usuario;
    private CategoriaDTO categoria;
    private Timestamp fechaAlta;
    
}
