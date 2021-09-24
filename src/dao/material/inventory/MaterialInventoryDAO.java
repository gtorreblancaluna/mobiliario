package dao.material.inventory;

import dao.MyBatisConnectionFactory;
import exceptions.DataOriginException;
import java.util.Date;
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
    
    public MaterialInventory getById (Long id) throws DataOriginException{
        MaterialInventory materialInventory;
        SqlSession session = sqlSessionFactory.openSession();
        
        try{
            materialInventory = (MaterialInventory) session.selectOne("MapperMaterialInventory.getByIdMaterialInventory",id);
         }catch(Exception e){
            log.error(e);
            throw new DataOriginException(e.getMessage(),e.getCause());
        } finally {
            session.close();
        }
        
        return materialInventory;
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
            materialInventory.setUpdatedAt(new Date());
            if (materialInventory.getId() == null || materialInventory.getId().toString().isEmpty()) {
                materialInventory.setCreatedAt(new Date());
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
    
    public void save (MeasurementUnit measurementUnit) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            measurementUnit.setUpdatedAt(new Date());
            if (measurementUnit.getId() == null || measurementUnit.getId().toString().isEmpty()) {
                measurementUnit.setCreatedAt(new Date());
                session.insert("MapperMaterialInventory.insertMeasurementUnit",measurementUnit);     
            } else {
                session.update("MapperMaterialInventory.updateMeasurementUnit",measurementUnit);     
            }
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    }
    
    public void save (MaterialArea materialArea) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            materialArea.setUpdatedAt(new Date());
            if (materialArea.getId() == null || materialArea.getId().toString().isEmpty()) {
                materialArea.setCreatedAt(new Date());
                session.insert("MapperMaterialInventory.insertMaterialArea",materialArea);     
            } else {
                session.update("MapperMaterialInventory.updateMaterialArea",materialArea);     
            }
           session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    }
    
    public void delete (MeasurementUnit measurementUnit) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            measurementUnit.setUpdatedAt(new Date());
            session.update("MapperMaterialInventory.deleteMeasurementUnit",measurementUnit);     
            session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    }
    
    public void delete (MaterialInventory materialInventory) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            materialInventory.setUpdatedAt(new Date());
            session.update("MapperMaterialInventory.deleteMaterialInventory",materialInventory);     
            session.commit();
         }catch(Exception ex){
            log.error(ex);
            throw new DataOriginException(ex.getMessage(),ex.getCause());
        } finally {
            session.close();
        }
    }
    
    public void delete (MaterialArea materialArea) throws DataOriginException {
        SqlSession session = sqlSessionFactory.openSession();
        try{
            materialArea.setUpdatedAt(new Date());
            session.update("MapperMaterialInventory.deleteMaterialArea",materialArea);     
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
