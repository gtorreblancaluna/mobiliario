package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Abono;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author idscomercial
 */
public class AbonosDAO {
    
    private static Logger log = Logger.getLogger(AbonosDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
    
    public AbonosDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    public List<Abono> getAbonosByDates(String initDate,String endDate){
        SqlSession session = sqlSessionFactory.openSession();
     
        Map<String,Object> map = new HashMap<>();
        map.put("initDate", initDate);
        map.put("endDate", endDate);
        try{
            List<Abono> list;
                list = (List<Abono>) session.selectList("MapperAbonos.getAbonosByDates",map);
            return list;
         }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
     public List<Abono> getAbonosByDatesGroupByBankAccounts(String initDate,String endDate){
        SqlSession session = sqlSessionFactory.openSession();
     
        Map<String,Object> map = new HashMap<>();
        map.put("initDate", initDate);
        map.put("endDate", endDate);
        try{
            List<Abono> list;
                list = (List<Abono>) session.selectList("MapperAbonos.getAbonosByDatesGroupByBankAccounts",map);
            return list;
         }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
}
