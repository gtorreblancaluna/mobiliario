package dao.task.almacen;

import common.exceptions.DataOriginException;
import common.model.TaskAlmacenVO;
import common.utilities.MyBatisConnectionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class TaskAlmacenUpdateDAO {
    
    private static final TaskAlmacenUpdateDAO SINGLE_INSTANCE = null;
    
    public static TaskAlmacenUpdateDAO getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new TaskAlmacenUpdateDAO();
        }
        return SINGLE_INSTANCE;
    } 
    
    private final Logger LOGGER = Logger.getLogger(TaskAlmacenUpdateDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    
    private TaskAlmacenUpdateDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    @SuppressWarnings("unchecked")
    public void save(TaskAlmacenVO taskAlmacenVO) throws DataOriginException {
       
        SqlSession session = null;
        try {
           session = sqlSessionFactory.openSession();
           session.insert("MapperTaskAlmacenUpdate.saveTaskAlmacen",taskAlmacenVO);     
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
