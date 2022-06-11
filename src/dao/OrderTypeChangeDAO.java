package dao;

import common.exceptions.DataOriginException;
import java.util.Date;
import model.OrderTypeChange;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class OrderTypeChangeDAO {
    
     private static Logger log = Logger.getLogger(OrderTypeChangeDAO.class.getName());
     private SqlSessionFactory sqlSessionFactory;
     private static final OrderTypeChangeDAO SINGLE_INSTANCE = null;
     
     private OrderTypeChangeDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
     
     public static OrderTypeChangeDAO getInstance(){
        
         if (SINGLE_INSTANCE == null) {
            return new OrderTypeChangeDAO();
        }
        return SINGLE_INSTANCE;
    }
     
     public void save (OrderTypeChange orderTypeChange) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            orderTypeChange.setUpdatedAt(new Date());
            if (orderTypeChange.getId() == null || orderTypeChange.getId().toString().isEmpty()) {
                orderTypeChange.setCreatedAt(new Date());
                session.insert("MapOrderTypeChange.insertOrderTypeChange",orderTypeChange);     
            } else {
                session.update("MapOrderTypeChange.updateOrderTypeChange",orderTypeChange);     
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
