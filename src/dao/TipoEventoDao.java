package dao;

import model.Tipo;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class TipoEventoDao {
    
    private static TipoEventoDao INSTANCE = null;
    private SqlSessionFactory sqlSessionFactory;
    
    // Private constructor suppresses 
    private TipoEventoDao(){
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TipoEventoDao();
        }
    }

    public static TipoEventoDao getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<Tipo> get () {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            
            return (List<Tipo>) session.selectList("MapperTipoEvento.getTipoEvento");
         
        } finally {
            session.close();
        }
    }
    
}
