package dao;

import common.utilities.MyBatisConnectionFactory;
import java.sql.Timestamp;
import java.util.List;
import common.model.Cuenta;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class AccountDAO {
    
    private static Logger log = Logger.getLogger(AccountDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
 
    public AccountDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    
    @SuppressWarnings("unchecked")
    public List<Cuenta> getAccounts() {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            List<Cuenta> list = (List<Cuenta>) session.selectList("MapperAccounts.getAccounts");
            if(list == null || list.size()<=0)
                return null; 
            return list;
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public Cuenta getAccountByDescription(String description) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Cuenta cuenta = (Cuenta) session.selectOne("MapperAccounts.getAccountByDescription",description);
            if(cuenta == null)
                return null; 
            return cuenta;
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void insertAccount( Cuenta cuenta) {
       
        SqlSession session = sqlSessionFactory.openSession();
        try {
           cuenta.setCreatedAt(new Timestamp(System.currentTimeMillis()));
           session.insert("MapperAccounts.insertAccount",cuenta);     
           session.commit();
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
    
    
}
