package services;

import common.model.Articulo;
import common.model.Color;
import common.model.Renta;
import common.model.Usuario;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Faltante;
import clases.sqlclass;

public class FaltanteService {
    
    private static final FaltanteService SINGLE_INSTANCE = null;
    private FaltanteService(){}
    public static FaltanteService getInstance() {
      if (SINGLE_INSTANCE == null) {
            return new FaltanteService();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<Faltante> obtenerFaltantesPorRentaId(sqlclass sql, int rentaId){
        
        String[] colName = {
                            /* [0] */ "id_faltante",
                            /* [1] */ "id_articulo",
                            /* [2] */ "id_renta",
                            /* [3] */ "id_usuarios",
                            /* [4] */ "fecha_registro",
                            /* [5] */ "cantidad",
                            /* [6] */ "comentario",
                            /* [7] */ "fg_faltante",
                            /* [8] */ "fg_devolucion",
                            /* [9] */ "fg_activo",
                            /* [10] */ "descripcion",
                            /* [11] */ "color",
                            /* [12] */ "nombre",
                            /* [13] */ "apellidos",
                            /* [14] */ "fg_accidente_trabajo",
                            /* [15] */ "precio_cobrar",
                            /* [16] */ "precio_compra_articulo"
                            
        };
        String stringSql = "SELECT faltante.*, "
                + "articulo.descripcion, "
                + "color.color, "
                + "usuarios.nombre, "
                + "usuarios.apellidos, "
                + "articulo.precio_compra AS precio_compra_articulo "
                            + "FROM faltantes faltante "
                            + "INNER JOIN articulo articulo ON (articulo.id_articulo = faltante.id_articulo) "
                            + "INNER JOIN color color ON (color.id_color = articulo.id_color) "
                            + "INNER JOIN usuarios usuarios ON (usuarios.id_usuarios = faltante.id_usuarios) "
                            + "WHERE faltante.id_renta ='"+rentaId+"' "
                            + "AND faltante.fg_activo = '1' ";
         Object[][] dtconduc = null;
         try {
         dtconduc = sql.GetTabla(colName, "articulo", stringSql);
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
        
        List<Faltante> faltantes = new ArrayList<>();
        for (int i = 0; i < dtconduc.length; i++) {
            Faltante faltante = new Faltante();
            
            if(dtconduc[i][0] != null)
                faltante.setFaltanteId(Integer.parseInt(dtconduc[i][0]+""));
            
            Articulo articulo = new Articulo();
            if(dtconduc[i][1] != null)
                articulo.setArticuloId(Integer.parseInt(dtconduc[i][1]+""));
            
             if(dtconduc[i][16] != null){
                 articulo.setPrecioCompra(Float.parseFloat(dtconduc[i][16]+""));
             }
            
            Renta renta = new Renta();
            if(dtconduc[i][2] != null)
                renta.setRentaId(Integer.parseInt(dtconduc[i][2]+""));
            
            Usuario usuario = new Usuario();
            if(dtconduc[i][3] != null)
                usuario.setUsuarioId(Integer.parseInt(dtconduc[i][3]+""));
            
            if(dtconduc[i][4] != null)
                faltante.setFechaRegistro(dtconduc[i][4].toString());
            
            if(dtconduc[i][5] != null)
                faltante.setCantidad(Float.parseFloat(dtconduc[i][5].toString()));
            
            if(dtconduc[i][6] != null)
                faltante.setComentario(dtconduc[i][6].toString());
            
            if(dtconduc[i][7] != null)
                faltante.setFgFaltante(Integer.parseInt(dtconduc[i][7].toString()));
            if(dtconduc[i][8] != null)
                faltante.setFgDevolucion(Integer.parseInt(dtconduc[i][8].toString()));
            if(dtconduc[i][9] != null)
                faltante.setFgActivo(Integer.parseInt(dtconduc[i][9].toString()));
            
            if(dtconduc[i][10] != null)
                articulo.setDescripcion(dtconduc[i][10].toString());
            
            Color color = new Color();
            if(dtconduc[i][11] != null)
                color.setColor(dtconduc[i][11].toString());
            
            articulo.setColor(color);
            
            if(dtconduc[i][12] != null)
                usuario.setNombre(dtconduc[i][12].toString());
            
            if(dtconduc[i][13] != null)
                usuario.setApellidos(dtconduc[i][13].toString());
            
            if(dtconduc[i][14] != null)
                faltante.setFgAccidenteTrabajo(Integer.parseInt(dtconduc[i][14].toString()));
            
            if(dtconduc[i][15] != null){
                faltante.setPrecioCobrar(Float.parseFloat(dtconduc[i][15].toString()));
            }else{
                faltante.setPrecioCobrar(0f);
            }
            faltante.setRenta(renta);
            faltante.setUsuario(usuario);
            faltante.setArticulo(articulo);
            
            faltantes.add(faltante);
            
            
        }
        
        return faltantes;
        
    }
    
    
     public List<Faltante> obtenerFaltantesPorArticuloId(sqlclass sql, int articuloId){
        
        String[] colName = {
                            /* [0] */ "id_renta",
                            /* [1] */ "folio",
                            /* [2] */ "cantidad",
                            /* [3] */ "id_articulo",
                            /* [4] */ "descripcion_articulo",  
                            /* [5] */ "color_articulo",  
                            /* [6] */ "fg_faltante",
                            /* [7] */ "fg_devolucion",
                            /* [8] */ "fg_activo",
                            /* [9] */ "fg_accidente_trabajo",
                            /* [10] */ "comentario"
        };
        String stringSql = "SELECT renta.id_renta, "
                            + "renta.folio, "
                            + "faltante.cantidad, "
                            + "articulo.id_articulo, "
                            + "articulo.descripcion AS descripcion_articulo, "
                            + "color.color AS color_articulo, "
                            + "faltante.fg_faltante, "
                            + "faltante.fg_devolucion, "
                            + "faltante.fg_activo, "
                            + "faltante.fg_accidente_trabajo, "
                            + "faltante.comentario "
                            + "FROM faltantes faltante "
                            + "INNER JOIN renta renta ON (renta.id_renta = faltante.id_renta) "
                            + "INNER JOIN articulo articulo ON (articulo.id_articulo = faltante.id_articulo) "
                            + "INNER JOIN color color ON (color.id_color = articulo.id_color) "
                            + "WHERE faltante.id_articulo ='"+articuloId+"' "
                            + "AND faltante.fg_activo = '1' ";
        
         Object[][] dtconduc = null;
         try {
         dtconduc = sql.GetTabla(colName, "articulo", stringSql);
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
        
        List<Faltante> faltantes = new ArrayList<>();
        
        
        for (int i = 0; i < dtconduc.length; i++) {
            Faltante faltante = new Faltante();
            Renta renta = new Renta();
            Articulo articulo = new Articulo();
            Color color = new Color();
            
            if(dtconduc[i][0] != null)
                renta.setRentaId(Integer.parseInt(dtconduc[i][0]+""));
            if(dtconduc[i][1] != null)
                renta.setFolio(Integer.parseInt(dtconduc[i][1]+""));
            
            if(dtconduc[i][2] != null)
                faltante.setCantidad(Float.parseFloat(dtconduc[i][2]+""));
            
            if(dtconduc[i][3] != null)
                articulo.setArticuloId(Integer.parseInt(dtconduc[i][3]+""));
            
            if(dtconduc[i][4] != null)
                articulo.setDescripcion(dtconduc[i][4]+"");
            
            if(dtconduc[i][5] != null)
                color.setColor(dtconduc[i][5]+"");
            
            if(dtconduc[i][6] != null)
                faltante.setFgFaltante(Integer.parseInt(dtconduc[i][6].toString()));
            if(dtconduc[i][7] != null)
                faltante.setFgDevolucion(Integer.parseInt(dtconduc[i][7].toString()));
            if(dtconduc[i][8] != null)
                faltante.setFgActivo(Integer.parseInt(dtconduc[i][8].toString()));
            if(dtconduc[i][9] != null)
                faltante.setFgAccidenteTrabajo(Integer.parseInt(dtconduc[i][9].toString()));
            
           if(dtconduc[i][10] != null)
                faltante.setComentario((dtconduc[i][10].toString()));
            
            faltante.setRenta(renta);        
            articulo.setColor(color);
            faltante.setArticulo(articulo);
            
            faltantes.add(faltante);
            
        }
        
        return faltantes;
        
    }
}
