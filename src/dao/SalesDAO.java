package dao;

import common.constants.ApplicationConstants;
import common.exceptions.DataOriginException;
import common.utilities.MyBatisConnectionFactory;
import java.util.List;
import java.util.Map;
import common.model.Abono;
import common.model.DetalleRenta;
import common.model.Renta;
import common.model.TipoAbono;
import common.model.AvailabilityItemResult;
import model.querys.rentas.ItemByFolioResultQuery;
import model.querys.rentas.SearchItemByFolioParams;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class SalesDAO {
    
    private static Logger log = Logger.getLogger(SalesDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
 
    private SalesDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    private static final SalesDAO SINGLE_INSTANCE = null;
    
    public static SalesDAO getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new SalesDAO();
        }
        return SINGLE_INSTANCE;
    }
    
    @SuppressWarnings("unchecked")
    public List<ItemByFolioResultQuery> getItemsByFolio(SearchItemByFolioParams searchItemByFolioParams) throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (List<ItemByFolioResultQuery>) 
                   session.selectList("MapperPedidos.getItemsByFolio",searchItemByFolioParams);
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex);
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<DetalleRenta> getDetailByRentId(String rentId) throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (List<DetalleRenta>) session.selectList("MapperPedidos.getDetailByRentId",rentId);
        }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex);
        } finally {
            session.close();
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public TipoAbono obtenerTipoAbonoPorDescripcion( String descripcion) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (TipoAbono) session.selectOne("MapperPedidos.obtenerTipoAbonoPorDescripcion",descripcion);

        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void actualizarAbonoPorId(Abono abono) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
           session.update("MapperPedidos.actualizarAbonoPorId",abono);
           session.commit();
        }catch(Exception ex){
            log.error(ex);           
        } finally {
            session.close();
        }
    }
    
    public Renta obtenerRentaPorId (Integer id) throws Exception {
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (Renta) session.selectOne("MapperPedidos.obtenerRentaPorId",id);       
        } finally {
            session.close();
        }
    }
    
    public Renta obtenerRentaPorIdSinSumas (Integer id) throws Exception {
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (Renta) session.selectOne("MapperPedidos.obtenerRentaPorIdSinSumas",id);       
        } finally {
            session.close();
        }
    }
    
    public Renta obtenerRentaPorFolio (Integer folio) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (Renta) session.selectOne("MapperPedidos.obtenerRentaPorFolio",folio);       
        } catch (Exception e) {
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            session.close();
        }
    }
    
    public List<Renta> obtenerRentasPorParametros (Map<String,Object> parameters) throws Exception {
        SqlSession session = sqlSessionFactory.openSession();
        try {
           return (List<Renta>) session.selectList("MapperPedidos.obtenerRentaPorParametros",parameters);       
        } finally {
            session.close();
        }
    }
    
}
