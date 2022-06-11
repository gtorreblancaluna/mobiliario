package dao.providers;

import common.exceptions.DataOriginException;
import dao.MyBatisConnectionFactory;
import java.sql.Timestamp;
import java.util.List;
import model.providers.PagosProveedor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class ProvidersPaymentsDAO {
    
    private static Logger LOGGER = Logger.getLogger(ProvidersDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
    
    private ProvidersPaymentsDAO(){
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    private static final ProvidersPaymentsDAO SINGLE_INSTANCE = new ProvidersPaymentsDAO();
    public static ProvidersPaymentsDAO getInstance(){
        return SINGLE_INSTANCE;
    }
    
    
    public List<PagosProveedor> getAllProviderPaymentsByOrderId(Long orderId)throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        
        List<PagosProveedor> list = null;
        
         try{
            list = (List<PagosProveedor>) session.selectList("MapperPagosProveedores.getAllProviderPayments",orderId);
         }catch(Exception ex){
            LOGGER.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        return list;
    }
    
    public void addPayment(PagosProveedor pagosProveedor)throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
         pagosProveedor.setActualizado(new Timestamp(System.currentTimeMillis()));
         pagosProveedor.setCreado(new Timestamp(System.currentTimeMillis()));
         
         try{
            session.insert("MapperPagosProveedores.addPayment",pagosProveedor);
            session.commit();
         }catch(Exception ex){
            LOGGER.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
       
    }
    
}
