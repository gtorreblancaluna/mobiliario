package dao;

import common.exceptions.DataOriginException;
import common.utilities.MyBatisConnectionFactory;
import java.util.List;
import common.model.Cliente;
import java.sql.Timestamp;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;


public class CustomerDao {
    
    private final static Logger log = Logger.getLogger(CustomerDao.class);
    private static CustomerDao SINGLE_INSTANCE = null;
    private final SqlSessionFactory sqlSessionFactory;

    private CustomerDao() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (SINGLE_INSTANCE == null) { 
            SINGLE_INSTANCE = new CustomerDao();
        }
    }
    
    public static CustomerDao getInstance() {
        if (SINGLE_INSTANCE == null) createInstance();
            return SINGLE_INSTANCE;
    }
    
    public List<Cliente> getAll () throws DataOriginException {
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
    
    public Cliente getById(Long id) throws DataOriginException {
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
    
    @SuppressWarnings("unchecked")
    public void saveOrUpdate(Cliente cliente) {
       
        SqlSession session = sqlSessionFactory.openSession();
        try {
           if (cliente.getId() == null) {
                cliente.setCreatedAt(
                        new Timestamp(System.currentTimeMillis())
                );
                cliente.setUpdatedAt(
                        new Timestamp(System.currentTimeMillis())
                );
                session.insert("MapperCustomer.insertCustomer",cliente);
           } else {
                cliente.setUpdatedAt(
                        new Timestamp(System.currentTimeMillis())
                );
                session.insert("MapperCustomer.updateCustomer",cliente);
           }
           session.commit();
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
}
