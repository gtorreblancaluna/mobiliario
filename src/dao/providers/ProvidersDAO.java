package dao.providers;

import common.exceptions.DataOriginException;
import dao.MyBatisConnectionFactory;
import java.sql.Timestamp;
import java.util.List;
import model.providers.Proveedor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class ProvidersDAO {
    
    private static Logger log = Logger.getLogger(ProvidersDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
    
    private ProvidersDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    private static final ProvidersDAO SINGLE_INSTANCE = new ProvidersDAO();
    public static ProvidersDAO getInstance(){
        return SINGLE_INSTANCE;
    }
    
    public List<Proveedor> getAll()throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        
        List<Proveedor> list = null;
        
         try{
            list = (List<Proveedor>) session.selectList("MapperProveedores.getAllProvider");
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        return list;
    }
    
    public List<Proveedor> searchByData(String data)throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        
        List<Proveedor> list = null;
        
         try{
            list = (List<Proveedor>) session.selectList("MapperProveedores.searchByData",data);
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        return list;
    }
    
     public Proveedor getById(Long id)throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        
        Proveedor proveedor = null;
        
         try{
            proveedor = (Proveedor) session.selectOne("MapperProveedores.getById",id);
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        return proveedor;
    }
     
      public void deleteById(Long id)throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
                
         try{
            session.update("MapperProveedores.deleteById",id);
            session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        
    }
    
     public void save(Proveedor proveedor)throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        proveedor.setCreado(new Timestamp(System.currentTimeMillis()));
        proveedor.setActualizado(new Timestamp(System.currentTimeMillis()));
               
         try{
            session.insert("MapperProveedores.saveProvider",proveedor);
            session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
     }
     
     public void update(Proveedor proveedor)throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        proveedor.setActualizado(new Timestamp(System.currentTimeMillis()));
               
         try{
            session.update("MapperProveedores.updateProvider",proveedor);
            session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
     }
    
}
