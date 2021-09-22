package dao.material.inventory;

import dao.MyBatisConnectionFactory;
import exceptions.DataOriginException;
import java.util.List;
import java.util.Map;
import model.material.inventory.MaterialArea;
import model.material.inventory.MaterialInventory;
import model.material.inventory.MeasurementUnit;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class MaterialInventoryDAO {
    private static Logger log = Logger.getLogger(MaterialInventoryDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
    
    private MaterialInventoryDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    private static final MaterialInventoryDAO SINGLE_INSTANCE = null;
    
    public static MaterialInventoryDAO getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new MaterialInventoryDAO();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<MaterialInventory> get(Map<String,Object> filter) throws DataOriginException{
        List<MaterialInventory> list = null;
        SqlSession session = sqlSessionFactory.openSession();
        
        try{
            list = (List<MaterialInventory>) session.selectList("MapperMaterialInventory.getAllMaterialInventory", filter);
         }catch(Exception e){
            log.error(e);
            throw new DataOriginException(e.getMessage(),e.getCause());
        } finally {
            session.close();
        }
        
        return list;
    }
    
    public void save (MaterialInventory materialInventory) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            if (materialInventory.getId() == null || materialInventory.getId().toString().isEmpty()) {
                session.insert("MapperMaterialInventory.insertMaterialInventory",materialInventory);     
            } else {
                session.update("MapperMaterialInventory.updateMaterialInventory",materialInventory);     
            }
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    }
    
    public List<MeasurementUnit> getMeasurementUnits () throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return (List<MeasurementUnit>) session.selectList("MapperMaterialInventory.getMeasurementUnits");
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    }
    
    public List<MaterialArea> getMaterialAreaList () throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return (List<MaterialArea>) session.selectList("MapperMaterialInventory.getMaterialAreaList");
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    }
}
