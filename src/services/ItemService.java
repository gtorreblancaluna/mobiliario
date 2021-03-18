/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import clases.sqlclass;
import dao.ItemDAO;
import dao.providers.OrderProviderDAO;
import exceptions.BusinessException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import mobiliario.ApplicationConstants;
import model.Articulo;
import model.CategoriaDTO;
import model.Color;
import model.Compra;
import model.Faltante;
import model.Renta;
import model.Usuario;
import model.providers.DetalleOrdenProveedor;
import org.apache.log4j.Logger;
import services.providers.OrderProviderService;

/**
 *
 * @author jerry
 */
public class ItemService {
//    sqlclass funcion = new sqlclass();
    // singlenton instance
    private static final ItemService SINGLE_INSTANCE = new ItemService();
    private ItemService(){}
    public static ItemService getInstance() {
      return SINGLE_INSTANCE;
    }
    
  
    private ItemDAO itemDao = new ItemDAO();
    private ComprasService comprasService = new ComprasService();
    private OrderProviderService orderProviderService = OrderProviderService.getInstance();
    private static Logger LOGGER = Logger.getLogger(ItemService.class.getName());
    
    public Articulo obtenerArticuloPorId(sqlclass sql, int articuloId){
//        Servicio para obtener un articulo con su id        
       
        //nombre de columnas, tabla, instruccion sql    
        
         String[] colName = {
                            "id_articulo", 
                            "id_categoria",
                            "id_usuario", 
                            "cantidad", 
                            "descripcion", 
                            "id_color",
                            "fecha_ingreso", 
                            "precio_compra", 
                            "precio_renta", 
                            "activo",
                            "stock",
                            "codigo",
                            "en_renta",
                            "color.id_color",
                            "color.color",
                            "color.tono",
                            "categoria.id_categoria",
                            "categoria.descripcion"
            };
        
         Object[][] dtconduc = null;
         try {
          dtconduc = sql.GetTabla(colName, "articulo", 
                "SELECT articulo.*,"
                        + "color.id_color, "
                        + "color.color, "
                        + "color.tono, "
                        + "categoria.id_categoria, "
                        + "categoria.descripcion "
                + "FROM articulo articulo "
                + "INNER JOIN color color ON (color.id_color = articulo.id_color)"
                + "INNER JOIN categoria categoria ON (categoria.id_categoria = articulo.id_categoria)"
                + "WHERE id_articulo="+articuloId);
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
        Articulo articulo = new Articulo();       
        articulo.setArticuloId(dtconduc[0][0] != null ? new Integer(dtconduc[0][0].toString()) : null);
        articulo.setCategoriaId(dtconduc[0][1] != null ?new Integer(dtconduc[0][1].toString()) : null);
        articulo.setUsuarioId(dtconduc[0][2] != null ?new Integer(dtconduc[0][2].toString()) : null);
        articulo.setCantidad(dtconduc[0][3] != null ? new Float(dtconduc[0][3].toString()) : null);
        articulo.setDescripcion(dtconduc[0][4] != null ? dtconduc[0][4].toString() : null);
        
        // COLOR
        Color color = new Color();
        color.setColorId(dtconduc[0][5] != null ? new Integer(dtconduc[0][5].toString()) : null);
        if(dtconduc[0][14] != null)
            color.setColor(dtconduc[0][14].toString());
         if(dtconduc[0][15] != null)
            color.setTono(dtconduc[0][15].toString());        
        articulo.setColor(color);
        
        articulo.setFechaIngreso(dtconduc[0][6] != null ? dtconduc[0][6].toString() : null);
        articulo.setPrecioCompra(dtconduc[0][7] != null ? new Float(dtconduc[0][7].toString()) : null);
        articulo.setPrecioRenta(dtconduc[0][8] != null ? new Float(dtconduc[0][8].toString()) : null);
        articulo.setActivo(dtconduc[0][9] != null ? dtconduc[0][6].toString() : null);
//        articulo.setStock(dtconduc[0][10] != null ? new Float(dtconduc[0][10].toString()) : null );
//        articulo.setCodigo(dtconduc[0][11] != null ? dtconduc[0][11].toString() : null);
        if(dtconduc[0][10] != null)
            articulo.setStock(new Float(dtconduc[0][10].toString()));
        if(dtconduc[0][11] != null)
            articulo.setCodigo(dtconduc[0][11].toString());
         if(dtconduc[0][12] != null)
            articulo.setEnRenta(new Float(dtconduc[0][12].toString()));
//        articulo.setEnRenta(dtconduc[0][12] != null ? new Float(dtconduc[0][12].toString()) : null);

        CategoriaDTO categoria = new CategoriaDTO();
        if(dtconduc[0][16] != null)
            categoria.setCategoriaId(new Integer(dtconduc[0][16]+""));
         if(dtconduc[0][17] != null)
             categoria.setDescripcion( (dtconduc[0][17]+""));
         articulo.setCategoria(categoria);
        System.out.println("se obtuvo el articulo: "+articulo.toString());
        return articulo;
    }
    
     public List<Articulo> obtenerArticulos(sqlclass sql, String stringSql){
//        Servicio para obtener un articulos por query armado desde la vista    
        //nombre de columnas, tabla, instruccion sql         
         Object[][] dtconduc = null;
         try {
         dtconduc = sql.GetTabla(this.obtenerColumnasArticulo(), "articulo", stringSql);
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
        
        List<Articulo> articulos = new ArrayList<>();
        for (int i = 0; i < dtconduc.length; i++) {
        Articulo articulo = new Articulo();       
        articulo.setArticuloId(dtconduc[i][0] != null ? new Integer(dtconduc[i][0].toString()) : null);
        articulo.setCategoriaId(dtconduc[i][1] != null ?new Integer(dtconduc[i][1].toString()) : null);
        articulo.setUsuarioId(dtconduc[i][2] != null ?new Integer(dtconduc[i][2].toString()) : null);
        articulo.setCantidad(dtconduc[i][3] != null ? new Float(dtconduc[i][3].toString()) : null);
        articulo.setDescripcion(dtconduc[i][4] != null ? dtconduc[i][4].toString() : null);
        
        // COLOR
        Color color = new Color();
        color.setColorId(dtconduc[i][5] != null ? new Integer(dtconduc[i][5].toString()) : null);
        if(dtconduc[i][14] != null)
            color.setColor(dtconduc[i][14].toString());
         if(dtconduc[i][15] != null)
            color.setTono(dtconduc[i][15].toString());        
        articulo.setColor(color);
        
        articulo.setFechaIngreso(dtconduc[i][6] != null ? dtconduc[i][6].toString() : null);
        articulo.setPrecioCompra(dtconduc[i][7] != null ? new Float(dtconduc[i][7].toString()) : null);
        articulo.setPrecioRenta(dtconduc[i][8] != null ? new Float(dtconduc[i][8].toString()) : null);
        articulo.setActivo(dtconduc[i][9] != null ? dtconduc[i][6].toString() : null);
//        articulo.setStock(dtconduc[i][10] != null ? new Float(dtconduc[i][10].toString()) : null );
//        articulo.setCodigo(dtconduc[i][11] != null ? dtconduc[i][11].toString() : null);
        if(dtconduc[i][10] != null)
            articulo.setStock(new Float(dtconduc[i][10].toString()));
        if(dtconduc[i][11] != null)
            articulo.setCodigo(dtconduc[i][11].toString());
         if(dtconduc[i][12] != null)
            articulo.setEnRenta(new Float(dtconduc[i][12].toString()));
//        articulo.setEnRenta(dtconduc[i][12] != null ? new Float(dtconduc[i][12].toString()) : null);
            articulos.add(articulo);
        
        }// end for

        return articulos;
    }
     
    public String[] obtenerColumnasArticulo(){
        String[] colName = {"id_articulo", 
            "id_categoria", "id_usuario", "cantidad", "descripcion", "id_color", 
            "fecha_ingreso", "precio_compra", "precio_renta", "activo","stock","codigo","en_renta","color.id_color",
                "color.color","color.tono"
            };
        
        return colName;
        
    }
    
    public Articulo getItemAvailable (Integer id) {
     Articulo item;
     
     item = itemDao.getItemAvailable(id);
     setUtiles(item);
     
     return item;
    }
    
    public List<Articulo> obtenerArticulosBusquedaInventario(sqlclass sql, String stringSql){
//        Servicio para obtener un articulos por query armado desde la vista    
        //nombre de columnas, tabla, instruccion sql 
        String[] colName = {"id_articulo","codigo","cantidad","en_renta","descripcion_categoria",
                      "descripcion","color","fecha_ingreso","precio_compra","precio_renta"};
        
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
        
        List<Articulo> articulos = new ArrayList<>();
        for (int i = 0; i < dtconduc.length; i++) {
        Articulo articulo = new Articulo();       
        articulo.setArticuloId(dtconduc[i][0] != null ? new Integer(dtconduc[i][0].toString()) : null);
        if(dtconduc[i][1] != null)
            articulo.setCodigo(dtconduc[i][1]+"");
        if(dtconduc[i][2] != null)
            articulo.setStock(new Float(dtconduc[i][2]+""));
        if(dtconduc[i][3] != null)
         articulo.setEnRenta(new Float(dtconduc[i][3]+""));
        
        CategoriaDTO categoria = new CategoriaDTO();
        if(dtconduc[i][4] != null)
             categoria.setDescripcion(dtconduc[i][4]+"");
        articulo.setCategoria(categoria);
         
       if(dtconduc[i][5] != null)
            articulo.setDescripcion(dtconduc[i][5]+"");
       
        // COLOR
        Color color = new Color();
        color.setColor(dtconduc[i][6] != null ? dtconduc[i][6]+"" : null);
        articulo.setColor(color);
        
        if(dtconduc[i][7] != null)
            articulo.setFechaIngreso(dtconduc[i][7]+"");
        
       if(dtconduc[i][8] != null)
           articulo.setPrecioCompra(new Float(dtconduc[i][8]+""));
       
       if(dtconduc[i][9] != null)
           articulo.setPrecioRenta(new Float(dtconduc[i][9]+""));
           articulos.add(articulo);
        
        }// end for

        return articulos;
    }
    
    public List<Articulo> obtenerArticulosBusquedaInventario(Map<String,Object> map){
        List<Articulo> articulos = itemDao.obtenerArticulosBusquedaInventario(map);
        
        if(articulos != null){
            for(Articulo articulo : articulos){    
              setUtiles(articulo);     
            } // end for articulos
        }
         
        return articulos;
    }
    
    private void setUtiles (Articulo item) {
        
        List<Compra> compras = comprasService.obtenerComprasPorArticuloId(false, item.getArticuloId(), null);
        float totalCompras = 0f;


       if(compras != null && compras.size()>0){
           for(Compra compra : compras){
               totalCompras += compra.getCantidad();
           } // end for compras
       }

       // get all shop from provider
       float totalShopFromProvider = 0f;
       Map<String,Object> parameters = new HashMap<>();
       parameters.put("articuloId", item.getArticuloId());
       parameters.put("statusOrderFinish", ApplicationConstants.STATUS_ORDER_PROVIDER_FINISH);
       parameters.put("statusOrder", ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER);
       parameters.put("typeOrderDetail", ApplicationConstants.TYPE_DETAIL_ORDER_SHOPPING);

       try{
           List<DetalleOrdenProveedor> detalleOrdenProveedor =
                   orderProviderService.getDetailProvider(parameters);

           if(detalleOrdenProveedor != null && detalleOrdenProveedor.size()>0){
               for(DetalleOrdenProveedor detalle : detalleOrdenProveedor){
                   totalShopFromProvider += detalle.getCantidad();
               }
           }
       }catch(BusinessException e){
           LOGGER.error(e);
       }
    
        float enRenta = 0f;
        float faltantes = 0f;
        float reparacion = 0f;
        float accidenteTrabajo = 0f;
        float devolucion = 0f;

        try{
            enRenta = item.getEnRenta();
        }catch (Exception e){
            System.out.println(e);
        }
        try{
            faltantes = new Float(item.getFaltantes());
        }catch (Exception e){
            System.out.println(e);
        }
        try{
            reparacion = new Float(item.getReparacion());
        }catch (Exception e){
            System.out.println(e);
        }
        try{
            accidenteTrabajo = new Float(item.getAccidenteTrabajo());
        }catch (Exception e){
            System.out.println(e);
        }
        try{
            devolucion = new Float(item.getDevolucion());
        }catch (Exception e){
            System.out.println(e);
        }

        item.setUtiles( (item.getCantidad() - faltantes - reparacion - accidenteTrabajo ) + devolucion + totalCompras + totalShopFromProvider);
        item.setTotalCompras(totalCompras+totalShopFromProvider);

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
                faltante.setFaltanteId(new Integer(dtconduc[i][0]+""));
            
            Articulo articulo = new Articulo();
            if(dtconduc[i][1] != null)
                articulo.setArticuloId(new Integer(dtconduc[i][1]+""));
            
             if(dtconduc[i][16] != null){
                 articulo.setPrecioCompra(new Float(dtconduc[i][16]+""));
             }
            
            Renta renta = new Renta();
            if(dtconduc[i][2] != null)
                renta.setRentaId(new Integer(dtconduc[i][2]+""));
            
            Usuario usuario = new Usuario();
            if(dtconduc[i][3] != null)
                usuario.setUsuarioId(new Integer(dtconduc[i][3]+""));
            
            if(dtconduc[i][4] != null)
                faltante.setFechaRegistro(dtconduc[i][4].toString());
            
            if(dtconduc[i][5] != null)
                faltante.setCantidad(new Float(dtconduc[i][5].toString()));
            
            if(dtconduc[i][6] != null)
                faltante.setComentario(dtconduc[i][6].toString());
            
            if(dtconduc[i][7] != null)
                faltante.setFgFaltante(new Integer(dtconduc[i][7].toString()));
            if(dtconduc[i][8] != null)
                faltante.setFgDevolucion(new Integer(dtconduc[i][8].toString()));
            if(dtconduc[i][9] != null)
                faltante.setFgActivo(new Integer(dtconduc[i][9].toString()));
            
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
                faltante.setFgAccidenteTrabajo(new Integer(dtconduc[i][14].toString()));
            
            if(dtconduc[i][15] != null){
                faltante.setPrecioCobrar(new Float(dtconduc[i][15].toString()));
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
                renta.setRentaId(new Integer(dtconduc[i][0]+""));
            if(dtconduc[i][1] != null)
                renta.setFolio(new Integer(dtconduc[i][1]+""));
            
            if(dtconduc[i][2] != null)
                faltante.setCantidad(new Float(dtconduc[i][2]+""));
            
            if(dtconduc[i][3] != null)
                articulo.setArticuloId(new Integer(dtconduc[i][3]+""));
            
            if(dtconduc[i][4] != null)
                articulo.setDescripcion(dtconduc[i][4]+"");
            
            if(dtconduc[i][5] != null)
                color.setColor(dtconduc[i][5]+"");
            
            if(dtconduc[i][6] != null)
                faltante.setFgFaltante(new Integer(dtconduc[i][6].toString()));
            if(dtconduc[i][7] != null)
                faltante.setFgDevolucion(new Integer(dtconduc[i][7].toString()));
            if(dtconduc[i][8] != null)
                faltante.setFgActivo(new Integer(dtconduc[i][8].toString()));
            if(dtconduc[i][9] != null)
                faltante.setFgAccidenteTrabajo(new Integer(dtconduc[i][9].toString()));
            
           if(dtconduc[i][10] != null)
                faltante.setComentario((dtconduc[i][10].toString()));
            
            faltante.setRenta(renta);        
            articulo.setColor(color);
            faltante.setArticulo(articulo);
            
            faltantes.add(faltante);
            
        }
        
        return faltantes;
        
    }
     
     public CategoriaDTO obtenerCategoriaPorDescripcion(String descripcion){
         return itemDao.obtenerCategoriaPorDescripcion(descripcion);
     }
     
     public Color obtenerColorPorDescripcion(String descripcion){
         return itemDao.obtenerColorPorDescripcion(descripcion);
     }
     
     public void insertarArticulo(Articulo articulo){
        itemDao.insertarArticulo(articulo);
     }
     
     public void actualizarArticulo(Articulo articulo){
        itemDao.actualizarArticulo(articulo);
     }
     
     public Articulo obtenerArticuloPorId(int id){
         return itemDao.obtenerArticuloPorId(id);
     }
     
     public List<Color> obtenerColores(){
         return itemDao.obtenerColores();
     }
    
}
