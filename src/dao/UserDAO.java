/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import mobiliario.iniciar_sesion;
import model.Usuario;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author jerry
 */
public class UserDAO {
    private static Logger log = Logger.getLogger(UserDAO.class.getName());
    private SqlSessionFactory sqlSessionFactory;
 
    public UserDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    @SuppressWarnings("unchecked")
    public Usuario obtenerUsuarioPorPassword(String password) {
        SqlSession session = sqlSessionFactory.openSession();
        log.debug("session db: "+session.getConnection());
        try {
            Usuario usuario = (Usuario) session.selectOne("MapperUsuarios.obtenerUsuarioPorPassword",password);
            if(usuario != null)
                log.debug("usuario obtenido es: "+usuario.getNombre()+" "+usuario.getApellidos());
            else
                log.debug("usuario no econtrado para la contrasenia: "+password);
            return usuario;
        }catch(Exception e){           
            log.error(e);
             return null;
        } finally {
            session.close();
        }
    }
 
    
}
