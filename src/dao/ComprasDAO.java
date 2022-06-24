package dao;

import common.utilities.MyBatisConnectionFactory;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Compra;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class ComprasDAO {
    
     private static Logger log = Logger.getLogger(ComprasDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
 
    public ComprasDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    @SuppressWarnings("unchecked")
    public void insertCompra(Compra compra) {
       
        SqlSession session = sqlSessionFactory.openSession();
        try {
           compra.setCreado(new Timestamp(System.currentTimeMillis()));
           compra.setActualizado(new Timestamp(System.currentTimeMillis()));
           session.insert("MapperCompras.insertCompra",compra);     
           session.commit();
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")    
    public List<Compra> obtenerComprasPorArticuloId(Boolean articuloComplete, Integer articuloId, Integer limit){
       SqlSession session = sqlSessionFactory.openSession();
       
       Map<String,Object> map = new HashMap<>();
       map.put("articuloId", articuloId);
       if(limit == null){
        map.put("limit",0);
       }else{
         map.put("limit",limit);
       }
        
        try {
            List<Compra> compras = (List<Compra>) session.selectList("MapperCompras.obtenerComprasPorArticuloId",map);
            if(compras == null || compras.size()<=0)
                return null; 
            return compras;
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
}
