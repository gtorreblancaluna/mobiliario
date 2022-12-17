package model;

import common.model.Renta;
import common.model.Tipo;
import common.model.Usuario;
import java.util.Date;
import lombok.Data;


@Data
public class OrderTypeChange {
    
    private Long id;
    private Renta renta;
    private Usuario user;
    private Tipo currentType;
    private Tipo changeType;
    private Date createdAt;
    private Date updatedAt;
    private String fgActive;
    
}
