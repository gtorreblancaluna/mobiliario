package dao;

import common.model.EstadoEvento;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class EstadoEventoDao {
    
    private static EstadoEventoDao INSTANCE = null;
    private SqlSessionFactory sqlSessionFactory;
    
    // Private constructor suppresses 
    private EstadoEventoDao(){
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new EstadoEventoDao();
        }
    }

    public static EstadoEventoDao getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<EstadoEvento> get () {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            
            return (List<EstadoEvento>) session.selectList("MapperEstadoEvento.getEstadoEvento");
         
        } finally {
            session.close();
        }
    }
    
}
