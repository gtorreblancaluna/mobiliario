package dao;

import common.utilities.MyBatisConnectionFactory;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.CategoriaContabilidad;
import model.Contabilidad;
import model.SubCategoriaContabilidad;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class ContabilidadDAO {
    
    private static Logger log = Logger.getLogger(ContabilidadDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
    
    public ContabilidadDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    public void deleteContabilidadById(Integer id){
        SqlSession session = sqlSessionFactory.openSession();
        try{
            session.update("MapperContabilidad.deleteContabilidadById",id);
            session.commit();
         }catch(Exception ex){
            log.error(ex);
           
        } finally {
            session.close();
        }
    }
    
    public Contabilidad getContabilidadById(Integer id){
        SqlSession session = sqlSessionFactory.openSession();
        try{
            Contabilidad contabilidad;
                contabilidad = (Contabilidad) session.selectOne("MapperContabilidad.getContabilidadById",id);
            return contabilidad;
         }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
 
    public List<Contabilidad> getAllContabilidad(){
        SqlSession session = sqlSessionFactory.openSession();
        try{
            List<Contabilidad> listContabilidad;
                listContabilidad = (List<Contabilidad>) session.selectList("MapperContabilidad.getAllContabilidad");
            return listContabilidad;
         }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
     public List<Contabilidad> getAllContabilidadByDates(Timestamp initDate, Timestamp endDate){
        SqlSession session = sqlSessionFactory.openSession();
     
        Calendar c= null;
    c=Calendar.getInstance();
    c.setTimeInMillis(initDate.getTime());
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);

    initDate.setTime(c.getTimeInMillis());
    
    c=Calendar.getInstance();
    c.setTimeInMillis(endDate.getTime());
    c.set(Calendar.HOUR_OF_DAY, 23);
    c.set(Calendar.MINUTE, 59);
    c.set(Calendar.SECOND, 59);
    c.set(Calendar.MILLISECOND, 0);

    endDate.setTime(c.getTimeInMillis());
        
        
        Map<String,Object> map = new HashMap<>();
        map.put("initDate", initDate);
        map.put("endDate", endDate);
        try{
            List<Contabilidad> listContabilidad;
                listContabilidad = (List<Contabilidad>) session.selectList("MapperContabilidad.getAllContabilidadByDates",map);
            return listContabilidad;
         }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
     
     
     public List<Contabilidad> getAllContabilidadByDatesGroupByBankAccounts(Timestamp initDate, Timestamp endDate){
        SqlSession session = sqlSessionFactory.openSession();
     
        Calendar c= null;
    c=Calendar.getInstance();
    c.setTimeInMillis(initDate.getTime());
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);

    initDate.setTime(c.getTimeInMillis());
    
    c=Calendar.getInstance();
    c.setTimeInMillis(endDate.getTime());
    c.set(Calendar.HOUR_OF_DAY, 23);
    c.set(Calendar.MINUTE, 59);
    c.set(Calendar.SECOND, 59);
    c.set(Calendar.MILLISECOND, 0);

    endDate.setTime(c.getTimeInMillis());
        
        
        Map<String,Object> map = new HashMap<>();
        map.put("initDate", initDate);
        map.put("endDate", endDate);
        try{
            List<Contabilidad> listContabilidad;
                listContabilidad = (List<Contabilidad>) session.selectList("MapperContabilidad.getAllContabilidadByDatesGroupByBankAccounts",map);
            return listContabilidad;
         }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    public List<SubCategoriaContabilidad> getAllSubCategoriasContabilidadByCategoriaId(Integer categoriaId){
        SqlSession session = sqlSessionFactory.openSession();
        try{
            List<SubCategoriaContabilidad> listSubCategoriaContabilidad;
                listSubCategoriaContabilidad = (List<SubCategoriaContabilidad>) session.selectList("MapperContabilidad.getAllSubCategoriasContabilidadByCategoriaId",categoriaId);
            return listSubCategoriaContabilidad;
         }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    public List<CategoriaContabilidad> getAllCategoriasContabilidad(){
        SqlSession session = sqlSessionFactory.openSession();
        try{
            List<CategoriaContabilidad> listCategoriaContabilidad;
                listCategoriaContabilidad = (List<CategoriaContabilidad>) session.selectList("MapperContabilidad.getAllCategoriasContabilidad");
            return listCategoriaContabilidad;
         }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
    public CategoriaContabilidad getCategoryByName(String category){
        SqlSession session = sqlSessionFactory.openSession();
        try{
            return (CategoriaContabilidad) session.selectOne("MapperContabilidad.getCategoryByName",category);
         }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }
    
     public SubCategoriaContabilidad getSubCategoryByName(String subCategory){
        SqlSession session = sqlSessionFactory.openSession();
         try {
            return (SubCategoriaContabilidad) session.selectOne("MapperContabilidad.getSubCategoryByName",subCategory);
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
         
     }
    
     @SuppressWarnings("unchecked")
    public void save (Contabilidad contabilidad){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.insert("MapperContabilidad.save",contabilidad);
            session.commit();
            log.debug("se guardo con exito la contabilidad: "+contabilidad.getSubCategoriaContabilidad().getDescripcion());
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void saveCategory (CategoriaContabilidad category){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.insert("MapperContabilidad.saveCategory",category);
            session.commit();
            log.debug("se guardo con exito la categoria: "+category.getDescripcion());
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
     @SuppressWarnings("unchecked")
    public void saveSubCategory (SubCategoriaContabilidad category){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.insert("MapperContabilidad.saveSubCategory",category);
            session.commit();
            log.debug("se guardo con exito la categoria: "+category.getDescripcion());
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void updateCategory (CategoriaContabilidad category){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.update("MapperContabilidad.updateCategory",category);
            session.commit();
            log.debug("se actualizo con exito la categoria: "+category.getDescripcion());
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
     @SuppressWarnings("unchecked")
    public void deleteSubCategoryById (Integer id){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.delete("MapperContabilidad.deleteSubCategoryById",id);
            session.commit();
            log.debug("se elimino con exito la sub categoria: ");
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
     @SuppressWarnings("unchecked")
    public void updateSubCategory (SubCategoriaContabilidad category){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            session.update("MapperContabilidad.updateSubCategory",category);
            session.commit();
            log.debug("se actualizo con exito la sub categoria: "+category.getDescripcion());
        }catch(Exception ex){
            log.error(ex);            
        } finally {
            session.close();
        }
    }
    
    
}
