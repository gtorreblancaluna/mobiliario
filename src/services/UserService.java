
package services;

import clases.sqlclass;
import dao.UserDAO;
import exceptions.DataOriginException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Puesto;
import model.Usuario;
import org.apache.log4j.Logger;

public class UserService {
    private final static Logger log = Logger.getLogger(UserService.class.getName());
    
    private final UserDAO usuariosDao;
    private static UserService INSTANCE = null;
   
    // Private constructor suppresses 
    private UserService(){
        usuariosDao = UserDAO.getInstance();
    }

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new UserService();
        }
    }

    public static UserService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
     public Usuario obtenerUsuarioPorId(sqlclass sql, int usuarioId){
         String[] colName = {"id_usuarios", 
            "nombre", "apellidos", "tel_movil", "tel_fijo", 
            "direccion", "administrador", "nivel1","nivel2","contrasenia","activo","id_puesto"
            };
        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "usuarios", "SELECT * "
                + "FROM usuarios "              
                + "WHERE id_usuarios="+usuarioId);
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } 
        
        if(dtconduc == null || dtconduc.length<=0)
            return null;
         
        Usuario usuario = new Usuario();
        
        if(dtconduc[0][0] != null)
            usuario.setUsuarioId(Integer.parseInt(dtconduc[0][0].toString()));
        if(dtconduc[0][1] != null)
            usuario.setNombre(dtconduc[0][1].toString());
        if(dtconduc[0][2] != null)
            usuario.setApellidos(dtconduc[0][2].toString());
        if(dtconduc[0][3] != null)
             usuario.setTelMovil(dtconduc[0][3].toString());
        if(dtconduc[0][4] != null)
             usuario.setTelFijo(dtconduc[0][4].toString());
        if(dtconduc[0][5] != null)
            usuario.setDireccion(dtconduc[0][5].toString());
        if(dtconduc[0][6] != null)
            usuario.setAdministrador(dtconduc[0][6].toString());
        if(dtconduc[0][7] != null)
            usuario.setNivel1(dtconduc[0][7].toString());
        if(dtconduc[0][8] != null)
            usuario.setNivel2(dtconduc[0][8].toString());
        if(dtconduc[0][9] != null)
            usuario.setContrasenia(dtconduc[0][9].toString());
         if(dtconduc[0][10] != null)
            usuario.setActivo(dtconduc[0][10].toString());
          if(dtconduc[0][11] != null)
            usuario.setPuesto(this.obtenerPuestoPorId(sql, Integer.parseInt(dtconduc[0][11].toString())));
        
        System.out.println("se obtuvo el usuario: "+usuario.toString());
        return usuario;
    }
     
      public Puesto obtenerPuestoPorId(sqlclass sql, int puestoId){
         String[] colName = {"id_puesto", "descripcion"};
          Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "usuarios", "SELECT * "
                + "FROM puesto "              
                + "WHERE id_puesto="+puestoId);
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } 
        
        if(dtconduc == null || dtconduc.length<=0)
            return null;
         
        Puesto puesto = new Puesto();
        
        if(dtconduc[0][0] != null)
            puesto.setPuestoId(Integer.parseInt(dtconduc[0][0].toString()));
        if(dtconduc[0][1] != null)
            puesto.setDescripcion(dtconduc[0][1].toString());
        
        return puesto;
    }
      
    
      
    public List<Usuario> obtenerUsuarios(sqlclass sql){
        List<Usuario> usuarios = new ArrayList<>();
        
         String[] colName = {"id_usuarios", "nombre", "apellidos", "tel_movil", "tel_fijo", 
            "direccion", "administrador", "nivel1","nivel2","contrasenia","activo","id_puesto"
            };
         
        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "usuarios", "SELECT * FROM usuarios ORDER BY nombre");
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } 
 
        if(dtconduc == null || dtconduc.length<=0)
            return null;
        
        for(int i=0; i<dtconduc.length;i++){
            Usuario usuario = new Usuario();
        
            if(dtconduc[i][0] != null)
                usuario.setUsuarioId(Integer.parseInt(dtconduc[i][0].toString()));
            if(dtconduc[i][1] != null)
                usuario.setNombre(dtconduc[i][1].toString());
            if(dtconduc[i][2] != null)
                usuario.setApellidos(dtconduc[i][2].toString());
            if(dtconduc[i][3] != null)
                 usuario.setTelMovil(dtconduc[i][3].toString());
            if(dtconduc[i][4] != null)
                 usuario.setTelFijo(dtconduc[i][4].toString());
            if(dtconduc[i][5] != null)
                usuario.setDireccion(dtconduc[i][5].toString());
            if(dtconduc[i][6] != null)
                usuario.setAdministrador(dtconduc[i][6].toString());
            if(dtconduc[i][7] != null)
                usuario.setNivel1(dtconduc[i][7].toString());
            if(dtconduc[i][8] != null)
                usuario.setNivel2(dtconduc[i][8].toString());
            if(dtconduc[i][9] != null)
                usuario.setContrasenia(dtconduc[i][9].toString());
            if(dtconduc[i][10] != null)
                usuario.setActivo(dtconduc[i][10].toString());
            if(dtconduc[i][11] != null)
                usuario.setPuesto(this.obtenerPuestoPorId(sql, Integer.parseInt(dtconduc[i][11].toString())));
              
            usuarios.add(usuario);
        }
        
        return usuarios;
    }
    
    public boolean verificarContraseniaExistente(sqlclass sql, String contrasenia){
        
        List<Usuario> usuarios = this.obtenerUsuarios(sql);
        
        for(Usuario usuario : usuarios){
            if(usuario.getContrasenia().equals(contrasenia))
                return true;
        }
        
        return false;
    }
    
    public List<Usuario> getChoferes () throws DataOriginException {
        return usuariosDao.getChoferes();
    }
    public Usuario obtenerUsuarioPorPassword(sqlclass sql, String psw){
         String[] colName = {"id_usuarios", 
            "nombre", "apellidos", "tel_movil", "tel_fijo", 
            "direccion", "administrador", "nivel1","nivel2","contrasenia","activo","id_puesto"
            };
         
         Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "usuarios", "SELECT * FROM usuarios WHERE activo = 1 AND contrasenia='"+psw+"'");
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } 
       
        if(dtconduc == null || dtconduc.length<=0)
            return null;
         
        Usuario usuario = new Usuario();
        
        if(dtconduc[0][0] != null)
            usuario.setUsuarioId(Integer.parseInt(dtconduc[0][0].toString()));
        if(dtconduc[0][1] != null)
            usuario.setNombre(dtconduc[0][1].toString());
        if(dtconduc[0][2] != null)
            usuario.setApellidos(dtconduc[0][2].toString());
        if(dtconduc[0][3] != null)
             usuario.setTelMovil(dtconduc[0][3].toString());
        if(dtconduc[0][4] != null)
             usuario.setTelFijo(dtconduc[0][4].toString());
        if(dtconduc[0][5] != null)
            usuario.setDireccion(dtconduc[0][5].toString());
        if(dtconduc[0][6] != null)
            usuario.setAdministrador(dtconduc[0][6].toString());
        if(dtconduc[0][7] != null)
            usuario.setNivel1(dtconduc[0][7].toString());
        if(dtconduc[0][8] != null)
            usuario.setNivel2(dtconduc[0][8].toString());
        if(dtconduc[0][9] != null)
            usuario.setContrasenia(dtconduc[0][9].toString());
         if(dtconduc[0][10] != null)
            usuario.setActivo(dtconduc[0][10].toString());
          if(dtconduc[0][11] != null)
            usuario.setPuesto(this.obtenerPuestoPorId(sql, Integer.parseInt(dtconduc[0][11].toString())));
        
        System.out.println("se obtuvo el usuario: "+usuario.toString());
        return usuario;
    }
    
     public Usuario obtenerUsuarioPorPassword(String psw){
         return usuariosDao.obtenerUsuarioPorPassword(psw);
     }
    
    
}
