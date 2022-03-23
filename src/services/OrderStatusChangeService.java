
package services;

import model.OrderStatusChange;
import dao.OrderStatusChangeDAO;
import exceptions.BusinessException;
import exceptions.DataOriginException;
import java.util.Date;
import model.EstadoEvento;
import model.Renta;
import model.Usuario;
import org.apache.log4j.Logger;


public class OrderStatusChangeService {
    
    private static Logger log = Logger.getLogger(OrderStatusChangeService.class.getName());
    private final OrderStatusChangeDAO orderStatusChangeDAO;
    private static final OrderStatusChangeService SINGLE_INSTANCE = null;
    
    private OrderStatusChangeService () {
        orderStatusChangeDAO = OrderStatusChangeDAO.getInstance();
    }
    
    public static OrderStatusChangeService getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new OrderStatusChangeService();
        }
        return SINGLE_INSTANCE;
    }
    
    public void insert (Integer rentaId, Integer currentStatusId, Integer changeStatusId, Integer userId) throws BusinessException {
        try {
            OrderStatusChange orderStatusChange = new OrderStatusChange();
            
            orderStatusChange.setRenta(
                    new Renta(rentaId)
            );
            orderStatusChange.setCurrentStatus(
                    new EstadoEvento(currentStatusId)
            );
            orderStatusChange.setChangeStatus(
                    new EstadoEvento(changeStatusId)
            );
            orderStatusChange.setUser(
                    new Usuario(userId)
            );
            orderStatusChange.setCreatedAt(new Date());
            orderStatusChange.setUpdatedAt(new Date());
            orderStatusChange.setFgActive("1");
            orderStatusChangeDAO.save(orderStatusChange);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
}
