package dao;

import common.utilities.MyBatisConnectionFactory;
import java.util.List;
import model.TipoAbono;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class TipoAbonosDAO {
    
    private static Logger log = Logger.getLogger(CategoryDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
 
    public TipoAbonosDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    @SuppressWarnings("unchecked")
    public List<TipoAbono> getAbonos( ) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            List<TipoAbono> tiposAbonos = (List<TipoAbono>) session.selectList("MapperTipoAbonos.obtenerTipoAbonos");
            if(tiposAbonos == null || tiposAbonos.size()<=0)
                return null; 
            return tiposAbonos;
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
     @SuppressWarnings("unchecked")
    public List<TipoAbono> getAbonosLike(String search) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            List<TipoAbono> tiposAbonos = (List<TipoAbono>) session.selectList("MapperTipoAbonos.obtenerTipoAbonosLike",search);
            if(tiposAbonos == null || tiposAbonos.size()<=0)
                return null; 
            return tiposAbonos;
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void insert( TipoAbono tipo) {
       
        SqlSession session = sqlSessionFactory.openSession();
        try {
           session.insert("MapperTipoAbonos.insertTipoAbono",tipo);     
           session.commit();
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
     @SuppressWarnings("unchecked")
    public TipoAbono getTipoAbonoByDescription(String description) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            TipoAbono tipoAbono = (TipoAbono) session.selectList("MapperTipoAbonos.getTipoAbonoByDescription",description);
            if(tipoAbono == null )
                return null; 
            return tipoAbono;
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
}
