package dao;

import common.exceptions.DataOriginException;
import common.utilities.MyBatisConnectionFactory;
import model.DatosGenerales;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class SystemDAO {
    
    private static final Logger log = Logger.getLogger(SystemDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
   
    
    private SystemDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    private static final SystemDAO SINGLE_INSTANCE = new SystemDAO();
   public static SystemDAO getInstance(){
        return SINGLE_INSTANCE;
    }
        
    @SuppressWarnings("unchecked")
    public DatosGenerales getGeneralData() {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            DatosGenerales datosGenerales = (DatosGenerales) session.selectOne("MapperDatosGenerales.getGeneralData");
            if(datosGenerales == null)
                return null; 
            return datosGenerales;
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void saveDatosGenerales (DatosGenerales datosGenerales) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.update("MapperDatosGenerales.saveDatosGenerales",datosGenerales);
            session.commit();
            log.debug("se guardo con exito datos generales ");
        }catch(Exception ex){
            log.error(ex);
         
        } finally {
            session.close();
        }
    }
    
     @SuppressWarnings("unchecked")
    public void updateInfoPDFSummary (DatosGenerales datosGenerales)throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        
        try {
            session.update("MapperDatosGenerales.updateInfoPDFSummary",datosGenerales);
            session.commit();
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage());
        } finally {
            session.close();
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public String getDataConfigurationByKey (String key)throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        String result;
        try {
            result = (String) session.selectOne("MapperDatosGenerales.getDataConfigurationByKey",key);
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage());
         
        } finally {
            session.close();
        }
        
        return result;
    }
    
   
    
    
    
}
