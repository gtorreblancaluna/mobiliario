package dao.providers;

import common.exceptions.DataOriginException;
import dao.MyBatisConnectionFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.providers.DetalleOrdenProveedor;
import model.providers.OrdenProveedor;
import model.providers.DetailOrderProviderType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import parametersVO.ParameterOrderProvider;

public class OrderProviderDAO {
    
    private static Logger log = Logger.getLogger(OrderProviderDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
    
    private OrderProviderDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
   
    private static final OrderProviderDAO SINGLE_INSTANCE = new OrderProviderDAO();
    public static OrderProviderDAO getInstance(){
        return SINGLE_INSTANCE;
    }
    
     public List<OrdenProveedor> getOrdersByRentaId(Integer rentaId)throws DataOriginException{
         List<OrdenProveedor> list = new ArrayList<>();
         SqlSession session = sqlSessionFactory.openSession();
          try{
           list = (List<OrdenProveedor>) session.selectList("MapperOrdenProveedor.getOrderByRentaId",rentaId);   
           
            if(list != null && list.size()>0){
                for(OrdenProveedor ordenProveedor : list){
                     ordenProveedor.setDetalleOrdenProveedorList((List<DetalleOrdenProveedor>) 
                       session.selectList("MapperOrdenProveedor.getDetailOrderByOrderId",ordenProveedor.getId()));
                }
            }
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
                    
        return list;
         
     
     }
     
      public OrdenProveedor getOrderById(Long id)throws DataOriginException{
         OrdenProveedor orden = new OrdenProveedor();
         SqlSession session = sqlSessionFactory.openSession();
          try{
           orden = (OrdenProveedor) session.selectOne("MapperOrdenProveedor.getOrderById",id);   
           
            if(orden != null ){
                     orden.setDetalleOrdenProveedorList((List<DetalleOrdenProveedor>) 
                       session.selectList("MapperOrdenProveedor.getDetailOrderByOrderId",orden.getId()));
                
            }
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
                    
        return orden;
         
     
     }
     
     public List<OrdenProveedor> getOrdersByParameters(ParameterOrderProvider parameter)throws DataOriginException{
         
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return session.selectList("MapperOrdenProveedor.getOrdersByParameters",parameter);
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
       
     }
     
     public void updateOrder(OrdenProveedor orden)throws DataOriginException{
       
        orden.setActualizado(new Timestamp(System.currentTimeMillis()));
        
        SqlSession session = sqlSessionFactory.openSession();
        try{
           session.update("MapperOrdenProveedor.updateOrder",orden);     
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        for(DetalleOrdenProveedor detail : orden.getDetalleOrdenProveedorList()){
            detail.setCreado(new Timestamp(System.currentTimeMillis()));
            detail.setActualizado(new Timestamp(System.currentTimeMillis()));
            detail.setIdOrdenProveedor(orden.getId());
            saveOrderDetail(detail);
        }
    }
     
     public void updateDetailOrderProvider(DetalleOrdenProveedor detail)throws DataOriginException{
        
        
        SqlSession session = null;
        try{
           session = sqlSessionFactory.openSession();
           session.update("MapperDetalleOrdenProveedor.updateDetailOrderProvider",detail);     
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            if (session != null)
                session.close();
        }
        
    }
    
    public void saveOrder(OrdenProveedor orden)throws DataOriginException{
        orden.setCreado(new Timestamp(System.currentTimeMillis()));
        orden.setActualizado(new Timestamp(System.currentTimeMillis()));
        
        SqlSession session = sqlSessionFactory.openSession();
        try{
           session.insert("MapperOrdenProveedor.saveOrder",orden);     
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        for(DetalleOrdenProveedor detail : orden.getDetalleOrdenProveedorList()){
            detail.setCreado(new Timestamp(System.currentTimeMillis()));
            detail.setActualizado(new Timestamp(System.currentTimeMillis()));
            detail.setIdOrdenProveedor(orden.getId());
            saveOrderDetail(detail);
        }
    }
    
    public void saveOrderDetail(DetalleOrdenProveedor detail)throws DataOriginException{
       
        
        SqlSession session = sqlSessionFactory.openSession();
        try{
           session.insert("MapperOrdenProveedor.saveOrderDetail",detail);     
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    }
    
    public DetalleOrdenProveedor getDetailOrderById(Long id)throws DataOriginException{
        DetalleOrdenProveedor detalle = new DetalleOrdenProveedor();
        SqlSession session = sqlSessionFactory.openSession();
        try{
           detalle = (DetalleOrdenProveedor) 
                   session.selectOne("MapperOrdenProveedor.getDetailOrderById",id);     
           
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        return detalle;
    }
    
    public void changeStatusDetailOrderById(Long id,String statusToChange)throws DataOriginException{
        
        Map<String,Object> map = new HashMap<>();
        map.put("id", id);
        map.put("statusToChange", statusToChange);
        map.put("dateTimestamp", new Timestamp(System.currentTimeMillis()));
        
        SqlSession session = sqlSessionFactory.openSession();
        try{
            session.update("MapperOrdenProveedor.changeStatusDetailOrderById",map);     
            session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
    }
    
    public List<DetalleOrdenProveedor> getDetailProvider(Map<String,Object>  map)throws DataOriginException{
        
        List<DetalleOrdenProveedor> list;
        SqlSession session = sqlSessionFactory.openSession();
        try{
            list = session.selectList("MapperOrdenProveedor.getDetailProvider",map);     
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
        
        return list;
        
    }
    
    public List<DetailOrderProviderType> getTypesOrderDetailProvider()throws DataOriginException{
        
       
        SqlSession session = null;
        try{
            session = sqlSessionFactory.openSession();
            return session.selectList("MapperOrdenProveedor.getTypesOrderDetailProvider");     
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            if (session != null)
                session.close();
        }
        
    }
    
}