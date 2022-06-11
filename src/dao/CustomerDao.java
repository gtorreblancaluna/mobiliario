package dao;

import common.exceptions.DataOriginException;
import java.util.List;
import model.Cliente;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;


public class CustomerDao {
    
    private final static Logger log = Logger.getLogger(CustomerDao.class);
    private static final CustomerDao SINGLE_INSTANCE = null;
    private final SqlSessionFactory sqlSessionFactory;

    private CustomerDao() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    public static CustomerDao getInstance() {
        if (SINGLE_INSTANCE == null) {
            return new CustomerDao();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<Cliente> obtenerClientesActivos () throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return (List<Cliente>) session.selectList("MapperCustomer.getAllCustomers");
         }catch(Exception e){
             log.error(e);
            throw new DataOriginException(e.getMessage(), e);
        } finally {
            session.close();
        }
    }
    
    public Cliente obtenerClientePorId(Long id) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return (Cliente) session.selectOne("MapperCustomer.getByIdCustomer",id);
         }catch(Exception e){
             log.error(e);
            throw new DataOriginException(e.getMessage(), e);
        } finally {
            session.close();
        }
    }
    
}
