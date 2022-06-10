package services;

import clases.sqlclass;
import common.model.Usuario;
import dao.CategoryDAO;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.AsignaCategoria;
import model.CategoriaDTO;

public class CategoryService {
    
    private final UserService userService = UserService.getInstance();
    CategoryDAO categoryDao = new CategoryDAO();
    
    // obtener las categorias asignadas a un usuario
    public List<AsignaCategoria> obtenerCategoriasAsignadasPorUsuarioId(sqlclass sql, int usuarioId){
        
       String[] colName = {"id_asigna_categoria", "id_usuarios", "id_categoria", "fecha_alta"};
         
        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "asigna_categoria", "SELECT * "
            + "FROM asigna_categoria "              
            + "WHERE id_usuarios="+usuarioId);
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        if(dtconduc == null || dtconduc.equals(""))
            return null;
        
        List<AsignaCategoria> asignaCategorias = new ArrayList<>();
        
        for (int i = 0; i < dtconduc.length; i++){
           try {
               AsignaCategoria asignaCategoria = new AsignaCategoria();
               asignaCategoria.setAsignaCategoriaId(Integer.parseInt(dtconduc[i][0].toString()));
               asignaCategoria.setUsuario((Usuario) userService.obtenerUsuarioPorId(sql, Integer.parseInt(dtconduc[i][1].toString())));
               asignaCategoria.setCategoria(this.obtenerCategoriaPorId(sql, Integer.parseInt(dtconduc[i][2].toString())));
               
               SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
               Date parsedDate = dateFormat.parse(dtconduc[i][3].toString());
               Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
               asignaCategoria.setFechaAlta(timestamp);
               
               asignaCategorias.add(asignaCategoria);
           }
           catch (ParseException ex) {
               Logger.getLogger(CategoryService.class.getName()).log(Level.SEVERE, null, ex);
           }
        }                             
            
        return asignaCategorias;
    
    }
    
    // obtener una categoria por id
    public CategoriaDTO obtenerCategoriaPorId(sqlclass sql, int categoriaId){
        String[] colName = {"id_categoria", "descripcion"};
        
        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "categoria", "SELECT * "
                + "FROM categoria "              
                + "WHERE id_categoria="+categoriaId);
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        
         CategoriaDTO categoria = new CategoriaDTO();
         if(dtconduc == null || dtconduc.equals(""))
            return null;
         
         categoria.setCategoriaId(Integer.parseInt(dtconduc[0][0].toString()));
         categoria.setDescripcion(dtconduc[0][1].toString());
         
         return categoria;  
       
    }
    
    // obtenemos todas las categorias 
    public List<CategoriaDTO> obtenerCategorias(sqlclass sql){
        
       String[] colName = {"id_categoria", "descripcion"};   
         Object[][] dtconduc = null;
         try {
            dtconduc = sql.GetTabla(colName, "categoria", "SELECT * FROM categoria ");
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        if(dtconduc == null || dtconduc.equals(""))
            return null;
        List<CategoriaDTO> categorias = new ArrayList<>();
        
            for (int i = 0; i < dtconduc.length; i++) {
                CategoriaDTO categoria = new CategoriaDTO();
                
                categoria.setCategoriaId(Integer.parseInt(dtconduc[i][0].toString()));
               
                if(dtconduc[i][1] != null)
                    categoria.setDescripcion(dtconduc[i][1].toString());
                categorias.add(categoria);        
                
            }// end for
            
        return categorias;
    
    }
    
    // insertara una categoria en la tabla --asigna_categoria--
    public boolean insertarCategoriaEnAsignaCategoria(sqlclass sql, String datos[]){
        try {
            sql.InsertarRegistro(datos, "INSERT INTO asigna_categoria "
                    + "(id_usuarios,id_categoria) values(?,?)");
            return true;
        } catch (SQLException ex) {
            
            Logger.getLogger(CategoryService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
    }
    
    public List<CategoriaDTO> obtenerCategorias(){
        return categoryDao.obtenerCategorias();
    }
    
}
