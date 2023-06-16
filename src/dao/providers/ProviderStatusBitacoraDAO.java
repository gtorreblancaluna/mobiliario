package dao.providers;

import common.exceptions.DataOriginException;
import common.utilities.MyBatisConnectionFactory;
import java.util.List;
import model.providers.CatalogStatusProvider;
import model.providers.StatusProviderByRenta;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class ProviderStatusBitacoraDAO {
    
    private static final Logger log = Logger.getLogger(ProviderStatusBitacoraDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    
    private ProviderStatusBitacoraDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
   
    private static ProviderStatusBitacoraDAO SINGLE_INSTANCE;
    
    public StatusProviderByRenta getLastStatusProviderByRenta(Long rentaId) throws DataOriginException{
         
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return (StatusProviderByRenta) session.selectOne("MapperStatusProvider.getLastStatusProviderByRenta",rentaId);
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
       
     }
    
    public static synchronized ProviderStatusBitacoraDAO getInstance(){
        if (SINGLE_INSTANCE == null) {
            SINGLE_INSTANCE = new ProviderStatusBitacoraDAO();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<StatusProviderByRenta> getStatusProviderByRenta(Long rentaId)throws DataOriginException{
         
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return session.selectList("MapperStatusProvider.getStatusProviderByRenta",rentaId);
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
       
     }
    
    public void insertStatusProvicerByRenta (StatusProviderByRenta statusProviderByRenta) throws DataOriginException{
        
        SqlSession session = sqlSessionFactory.openSession();
                
         try{
            session.insert("MapperStatusProvider.insertStatusProvicerByRenta",statusProviderByRenta);
            session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    
    }
    
    public List<CatalogStatusProvider> getCatalogStatusProvider()throws DataOriginException{
         
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return session.selectList("MapperStatusProvider.getCatalogStatusProvider");
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
       
     }
    

    
}