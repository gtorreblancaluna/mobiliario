
package services;

import dao.OrderTypeChangeDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import java.util.Date;
import model.OrderTypeChange;
import common.model.Renta;
import common.model.Tipo;
import common.model.Usuario;
import org.apache.log4j.Logger;


public class OrderTypeChangeService {
    
    private static Logger log = Logger.getLogger(OrderTypeChangeService.class.getName());
    private final OrderTypeChangeDAO orderTypeChangeDAO;
    private static final OrderTypeChangeService SINGLE_INSTANCE = null;
    
    private OrderTypeChangeService () {
        orderTypeChangeDAO = OrderTypeChangeDAO.getInstance();
    }
    
    public static OrderTypeChangeService getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new OrderTypeChangeService();
        }
        return SINGLE_INSTANCE;
    }
    
    public void insert (Integer rentaId, Integer currentTypeId, Integer changeTypeId, Integer userId) throws BusinessException {
        try {
            OrderTypeChange orderTypeChange = new OrderTypeChange();
            
            orderTypeChange.setRenta(
                    new Renta(rentaId)
            );
            orderTypeChange.setCurrentType(
                    new Tipo(currentTypeId)
            );
            orderTypeChange.setChangeType(
                    new Tipo(changeTypeId)
            );
            orderTypeChange.setUser(
                    new Usuario(userId)
            );
            orderTypeChange.setCreatedAt(new Date());
            orderTypeChange.setUpdatedAt(new Date());
            orderTypeChange.setFgActive("1");
            orderTypeChangeDAO.save(orderTypeChange);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
}
