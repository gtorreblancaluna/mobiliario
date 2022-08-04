package dao;

import common.constants.ApplicationConstants;
import common.exceptions.DataOriginException;
import common.utilities.MyBatisConnectionFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import common.model.Articulo;
import common.model.CategoriaDTO;
import common.model.Color;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class ItemDAO {
    private static Logger log = Logger.getLogger(ItemDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    private static final ItemDAO SINGLE_INSTANCE = null;
 
    private ItemDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    public static ItemDAO getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new ItemDAO();
        }
        return SINGLE_INSTANCE;
    }
    
    @SuppressWarnings("unchecked")
    public List<Articulo> obtenerArticulosActivos() {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return (List<Articulo>) session.selectList("MapperArticulos.obtenerArticulosActivos");
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Articulo> obtenerArticulosBusquedaInventario( Map<String,Object> map) throws DataOriginException{
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            return session.selectList("MapperArticulos.obtenerArticulosBusquedaInventario",map);
        }catch(Exception e){
            log.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Deprecated
    public Articulo getItemAvailable( Integer id) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("articuloId", id);
            map.put("estado_renta", ApplicationConstants.ESTADO_EN_RENTA);
            map.put("tipo_pedido", ApplicationConstants.TIPO_PEDIDO);
            
            Articulo item = (Articulo) session.selectOne("MapperArticulos.obtenerArticuloPorId",id);
            if(item == null){
                return null; 
            }
            
            
            item.setRentados( session.selectOne("MapperArticulos.obtenerEnRenta",map).toString());
            item.setFaltantes(Float.parseFloat(session.selectOne("MapperArticulos.obtenerFaltantes",map).toString()));
            item.setReparacion(Float.parseFloat(session.selectOne("MapperArticulos.obtenerReparacion",map).toString()));
            item.setAccidenteTrabajo(Float.parseFloat(session.selectOne("MapperArticulos.obtenerAccidenteTrabajo",map).toString()));
            item.setDevolucion(Float.parseFloat(session.selectOne("MapperArticulos.obtenerDevolucion",map).toString()));
         
            return item;
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public CategoriaDTO obtenerCategoriaPorDescripcion( String descripcion) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (CategoriaDTO) session.selectOne("MapperArticulos.obtenerCategoriaPorDescripcion",descripcion);

        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public Color obtenerColorPorDescripcion( String descripcion) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (Color) session.selectOne("MapperArticulos.obtenerColorPorDescripcion",descripcion);
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void insertarArticulo( Articulo articulo) {
       
        SqlSession session = sqlSessionFactory.openSession();
        try {
           session.insert("MapperArticulos.insertarArticulo",articulo);     
           session.commit();
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
     @SuppressWarnings("unchecked")
    public void actualizarArticulo( Articulo articulo) {
       
        SqlSession session = sqlSessionFactory.openSession();
        try {
           session.update("MapperArticulos.actualizarArticulo",articulo);     
           session.commit();
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
     @SuppressWarnings("unchecked")
    public Articulo obtenerArticuloPorId(int id) {
       
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (Articulo) session.selectOne("MapperArticulos.obtenerArticuloPorId",id);          
        }catch(Exception ex){
            log.error(ex);   
            return null;
        } finally {
            session.close();
        }
    }
    
     @SuppressWarnings("unchecked")
    public List<Color> obtenerColores() {
       
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (List<Color>) session.selectList("MapperArticulos.obtenerColores");          
        }catch(Exception ex){
            log.error(ex);   
            return null;
        } finally {
            session.close();
        }
    }
    
    
 
    
}
