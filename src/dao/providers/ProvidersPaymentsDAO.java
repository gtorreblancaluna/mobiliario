package dao.providers;

import common.exceptions.DataOriginException;
import common.utilities.MyBatisConnectionFactory;
import java.sql.Timestamp;
import java.util.List;
import model.providers.PagosProveedor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class ProvidersPaymentsDAO {
    
    private static final Logger LOGGER = Logger.getLogger(ProvidersDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    
    private ProvidersPaymentsDAO(){
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    private static final ProvidersPaymentsDAO SINGLE_INSTANCE = new ProvidersPaymentsDAO();
    public static ProvidersPaymentsDAO getInstance(){
        return SINGLE_INSTANCE;
    }
    
    
    public List<PagosProveedor> getAllProviderPaymentsByOrderId(Long orderId)throws DataOriginException{
        SqlSession session = null;
        
        List<PagosProveedor> list = null;
        
        try{
             session = sqlSessionFactory.openSession();
            list = (List<PagosProveedor>) session.selectList("MapperPagosProveedores.getAllProviderPayments",orderId);
         }catch(Exception ex){
            LOGGER.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            if (session != null)
                session.close();
        }
        
        return list;
    }
    
    public void addPayment(PagosProveedor pagosProveedor)throws DataOriginException{
         SqlSession session = null;
         pagosProveedor.setActualizado(new Timestamp(System.currentTimeMillis()));
         pagosProveedor.setCreado(new Timestamp(System.currentTimeMillis()));
         
         try{
            session = sqlSessionFactory.openSession();
            session.insert("MapperPagosProveedores.addPayment",pagosProveedor);
            session.commit();
         }catch(Exception ex){
            LOGGER.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
             if (session != null)
                session.close();
        }
        
       
    }
    
    public void delete(Long id)throws DataOriginException{
         SqlSession session = null;
         PagosProveedor pagosProveedor = new PagosProveedor();
         pagosProveedor.setId(id);
         pagosProveedor.setActualizado(new Timestamp(System.currentTimeMillis()));
         
         try{
            session = sqlSessionFactory.openSession();
            session.update("MapperPagosProveedores.deletePayment",pagosProveedor);
            session.commit();
         }catch(Exception e){
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e.getCause());
        } finally {
             if (session != null)
                session.close();
        }
        
       
    }
    
}
