package dao.task.deliveryChofer;

import common.exceptions.DataOriginException;
import common.model.TaskChoferDeliveryVO;
import common.utilities.MyBatisConnectionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class TaskDeliveryChoferUpdateDAO {
    
    private static final TaskDeliveryChoferUpdateDAO SINGLE_INSTANCE = null;
    
    public static TaskDeliveryChoferUpdateDAO getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new TaskDeliveryChoferUpdateDAO();
        }
        return SINGLE_INSTANCE;
    } 
    
    private final Logger LOGGER = Logger.getLogger(TaskDeliveryChoferUpdateDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    
    private TaskDeliveryChoferUpdateDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    @SuppressWarnings("unchecked")
    public void save(TaskChoferDeliveryVO taskChoferDeliveryVO) throws DataOriginException {
       
        SqlSession session = null;
        try {
           session = sqlSessionFactory.openSession();
           session.insert("MapperTaskDeliveryChoferUpdate.saveTaskDeliveryChofer",taskChoferDeliveryVO);     
           session.commit();
        }catch(Exception e){
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null){
                session.close();
            }
        }
    }
    
}
