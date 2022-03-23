package dao;

import exceptions.DataOriginException;
import java.util.Date;
import model.OrderStatusChange;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class OrderStatusChangeDAO {
    
     private static Logger log = Logger.getLogger(OrderStatusChangeDAO.class.getName());
     private SqlSessionFactory sqlSessionFactory;
     private static final OrderStatusChangeDAO SINGLE_INSTANCE = null;
     
     private OrderStatusChangeDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
     
     public static OrderStatusChangeDAO getInstance(){
        
         if (SINGLE_INSTANCE == null) {
            return new OrderStatusChangeDAO();
        }
        return SINGLE_INSTANCE;
    }
     
     public void save (OrderStatusChange orderStatusChange) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            orderStatusChange.setUpdatedAt(new Date());
            if (orderStatusChange.getId() == null || orderStatusChange.getId().toString().isEmpty()) {
                orderStatusChange.setCreatedAt(new Date());
                session.insert("MapOrderStatusChange.insertOrderStatusChange",orderStatusChange);     
            } else {
                //session.update("MapOrderStatusChange.updateOrderStatusChange",orderTypeChange);     
            }
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    }
    
}
