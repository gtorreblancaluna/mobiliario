package dao;

import common.utilities.MyBatisConnectionFactory;
import java.util.List;
import common.model.CategoriaDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class CategoryDAO {
    private static Logger log = Logger.getLogger(CategoryDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
 
    public CategoryDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    @SuppressWarnings("unchecked")
    public List<CategoriaDTO> obtenerCategorias( ) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            List<CategoriaDTO> categorias = (List<CategoriaDTO>) session.selectList("MapperCategorias.obtenerCategorias");
            if(categorias == null || categorias.size()<=0)
                return null; 
            return categorias;
        }catch(Exception ex){
            log.error(ex);
            return null;
        } finally {
            session.close();
        }
    }    
    
    
 
    
}
