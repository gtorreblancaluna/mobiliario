/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mobiliario.ApplicationConstants;
import model.Articulo;
import model.CategoriaDTO;
import model.Color;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author jerry
 */
public class ItemDAO {
    private static Logger log = Logger.getLogger(ItemDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
 
    public ItemDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    @SuppressWarnings("unchecked")
    public List<Articulo> obtenerArticulosBusquedaInventario( Map<String,Object> map) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            List<Articulo> articulos = (List<Articulo>) session.selectList("MapperArticulos.obtenerArticulosBusquedaInventario",map);
            if(articulos == null || articulos.size()<=0)
                return null;            
           
            for(Articulo articulo : articulos){                
                map.put("articuloId", articulo.getArticuloId());
                articulo.setRentados( (String) session.selectOne("MapperArticulos.obtenerEnRenta",map));
                articulo.setFaltantes((String) session.selectOne("MapperArticulos.obtenerFaltantes",map));
                articulo.setReparacion((String) session.selectOne("MapperArticulos.obtenerReparacion",map));
                articulo.setAccidenteTrabajo((String) session.selectOne("MapperArticulos.obtenerAccidenteTrabajo",map));
                articulo.setDevolucion((String) session.selectOne("MapperArticulos.obtenerDevolucion",map));
            }
            return articulos;
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
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
            
            
            item.setRentados( (String) session.selectOne("MapperArticulos.obtenerEnRenta",map));
            item.setFaltantes((String) session.selectOne("MapperArticulos.obtenerFaltantes",map));
            item.setReparacion((String) session.selectOne("MapperArticulos.obtenerReparacion",map));
            item.setAccidenteTrabajo((String) session.selectOne("MapperArticulos.obtenerAccidenteTrabajo",map));
            item.setDevolucion((String) session.selectOne("MapperArticulos.obtenerDevolucion",map));
         
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
