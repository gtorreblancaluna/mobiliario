/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import model.Abono;
import model.CategoriaDTO;
import model.TipoAbono;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

/**
 * Dao relacionado con los pedidos
 * 
 * @author jerry
 */
public class SalesDAO {
    
     private static Logger log = Logger.getLogger(SalesDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
 
    public SalesDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
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
    
}
