package dao;

import common.exceptions.DataOriginException;
import common.utilities.MyBatisConnectionFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import common.model.Abono;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;


public class AbonosDAO {
      
    private static final AbonosDAO SINGLE_INSTANCE = null;
    
    public static AbonosDAO getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new AbonosDAO();
        }
        return SINGLE_INSTANCE;
    } 
    
    private final Logger log = Logger.getLogger(AbonosDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    
    private AbonosDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    public List<Abono> getByParameters(Map<String,Object> parameters) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
     
        try{
            return session.selectList("MapperAbonos.getByParameters",parameters);
            
         } catch(Exception e){
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            session.close();
        }
    }
    
    public List<Abono> getAbonosByDates(String initDate,String endDate) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
     
        Map<String,Object> map = new HashMap<>();
        map.put("initDate", initDate);
        map.put("endDate", endDate);
        try{
            List<Abono> list;
                list = (List<Abono>) session.selectList("MapperAbonos.getAbonosByDates",map);
            return list;
         }catch(Exception e){
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            session.close();
        }
    }
    
     public List<Abono> getAbonosByDatesGroupByBankAccounts(String initDate,String endDate) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
     
        Map<String,Object> map = new HashMap<>();
        map.put("initDate", initDate);
        map.put("endDate", endDate);
        try{
            List<Abono> list;
                list = (List<Abono>) session.selectList("MapperAbonos.getAbonosByDatesGroupByBankAccounts",map);
            return list;
         }catch(Exception e){
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            session.close();
        }
    }
    
}
