
package services;

import dao.ComprasDAO;
import java.util.List;
import model.Compra;
import org.apache.log4j.Logger;


public class ComprasService {
    
    private static Logger log = Logger.getLogger(ComprasService.class.getName());
    private ComprasDAO comprasDAO = new ComprasDAO();
    
    
    public void insertCompra(Compra compra){
        comprasDAO.insertCompra(compra);
    }
    
    public List<Compra> obtenerComprasPorArticuloId(Boolean articuloComplete, Integer articuloId, Integer limit){
        return comprasDAO.obtenerComprasPorArticuloId(articuloComplete, articuloId, limit);
    }
    
    
}
