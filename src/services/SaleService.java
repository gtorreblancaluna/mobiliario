/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package services;

import clases.sqlclass;
import dao.SalesDAO;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import javax.swing.JOptionPane;
import mobiliario.ApplicationConstants;
import mobiliario.iniciar_sesion;
import model.Abono;
import model.Articulo;
import model.Cliente;
import model.DetalleRenta;
import model.EstadoEvento;
import model.Renta;
import model.Tipo;
import model.TipoAbono;
import model.Usuario;

/**
 *
 * @author jerry
 */
public class SaleService {
    private static Logger log = Logger.getLogger(iniciar_sesion.class.getName());
    private static SalesDAO salesDao = new SalesDAO();
    // inserta un detalle de renta y devuelve el ultimo id insertado
    public int insertarDetalleRenta(String[] datos, sqlclass sql){
        try {
            sql.InsertarRegistro(datos, "INSERT INTO detalle_renta (id_renta,cantidad,id_articulo,p_unitario,porcentaje_descuento) values (?,?,?,?,?)");
            String id = sql.GetData("id_detalle_renta", "SELECT id_detalle_renta FROM detalle_renta ORDER BY id_detalle_renta DESC LIMIT 1 ");
            return new Integer(id);
        } catch (SQLException ex) {
//            Logger.getLogger(SaleService.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al insertar registro ", "Error", JOptionPane.ERROR);
            return 0;
        }
    }
    
    // obtener la disponibilidad de articulos en un rango de fechas
    public List<Renta> obtenerDisponibilidadRenta(String fechaInicial,String fechaFinal,sqlclass sql){
        
        
//        String stringSql = "SELECT * FROM renta renta "
//                +"INNER JOIN detalle_renta detalle ON (detalle.id_renta = renta.id_renta) "   
//                +"INNER JOIN articulo articulo ON (articulo.id_articulo = detalle.id_articulo) "
//                +"WHERE "
//                +"(((STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y') >= STR_TO_DATE(renta.fecha_entrega,'%d/%m/%Y')) "
//                +"AND (STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y') <= STR_TO_DATE(renta.fecha_devolucion,'%d/%m/%Y'))) "
//                +"OR "
//                +"((STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y') >= STR_TO_DATE(renta.fecha_entrega,'%d/%m/%Y')) "
//                +"AND (STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y') <= STR_TO_DATE(renta.fecha_devolucion,'%d/%m/%Y')))) "
//                +"AND renta.id_tipo = "+ApplicationConstants.TIPO_PEDIDO+" "
//                +"AND renta.id_estado IN ("+ApplicationConstants.ESTADO_APARTADO+","+ApplicationConstants.ESTADO_EN_RENTA+") "
//                +"OR "
//                + "(((STR_TO_DATE(renta.fecha_entrega,'%d/%m/%Y') >= STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y')) "
//                +"AND (STR_TO_DATE(renta.fecha_entrega,'%d/%m/%Y') <= STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y'))) "
//                +"OR "
//                +"((STR_TO_DATE(renta.fecha_devolucion,'%d/%m/%Y') >= STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y')) "
//                +"AND (STR_TO_DATE(renta.fecha_devolucion,'%d/%m/%Y') <= STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y')))) "
//                +"AND renta.id_tipo = "+ApplicationConstants.TIPO_PEDIDO+" "
//                +"AND renta.id_estado IN ("+ApplicationConstants.ESTADO_APARTADO+","+ApplicationConstants.ESTADO_EN_RENTA+") "
//                +"ORDER BY articulo.descripcion "
                
                // ajuste de consulta 2019.03.01
                String stringSql = "SELECT * FROM renta renta "
                +"WHERE "
                +"STR_TO_DATE(renta.fecha_entrega,'%d/%m/%Y') "
                +"BETWEEN STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y') "
                +"AND STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y') "
                +"AND renta.id_tipo = "+ApplicationConstants.TIPO_PEDIDO+" "
                +"AND renta.id_estado IN ("+ApplicationConstants.ESTADO_APARTADO+","+ApplicationConstants.ESTADO_EN_RENTA+") "
                +"UNION "
                +"SELECT * FROM renta renta "
                +"WHERE "
                +"STR_TO_DATE(renta.fecha_devolucion,'%d/%m/%Y') "
                +"BETWEEN STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y') "
                +"AND STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y') "
                +"AND renta.id_tipo = "+ApplicationConstants.TIPO_PEDIDO+" "
                +"AND renta.id_estado IN ("+ApplicationConstants.ESTADO_APARTADO+","+ApplicationConstants.ESTADO_EN_RENTA+") "
                +"UNION "
                +"SELECT * FROM renta renta "
                +"WHERE "
                +"STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y') "
                +"BETWEEN STR_TO_DATE(renta.fecha_entrega,'%d/%m/%Y') "
                +"AND STR_TO_DATE(renta.fecha_devolucion,'%d/%m/%Y') "
                +"AND renta.id_tipo = "+ApplicationConstants.TIPO_PEDIDO+" "
                +"AND renta.id_estado IN ("+ApplicationConstants.ESTADO_APARTADO+","+ApplicationConstants.ESTADO_EN_RENTA+") "
                +"UNION "
                +"SELECT * FROM renta renta "
                +"WHERE "
                +"STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y') "
                +"BETWEEN STR_TO_DATE(renta.fecha_entrega,'%d/%m/%Y') "
                +"AND STR_TO_DATE(renta.fecha_devolucion,'%d/%m/%Y') "
                +"AND renta.id_tipo = "+ApplicationConstants.TIPO_PEDIDO+" "
                +"AND renta.id_estado IN ("+ApplicationConstants.ESTADO_APARTADO+","+ApplicationConstants.ESTADO_EN_RENTA+") "
                +"";
        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(obtenerColumnasRenta(), "renta",stringSql );
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }       
        
         if(dtconduc == null || dtconduc.length <= 0)
            return null;
         
        List<Renta> rentas = new ArrayList<>();
        CustomerService customerService = new CustomerService();
        UserService userService = new UserService();
        
         for (int i = 0; i < dtconduc.length; i++) {
             Renta renta = new Renta();
             renta.setRentaId(new Integer(dtconduc[i][0].toString()));
             if(dtconduc[i][1] != null)
                renta.setEstadoId(new Integer(dtconduc[i][1].toString()));
             
             if(dtconduc[i][2] != null)
                renta.setCliente(customerService.obtenerClientePorId(sql, new Integer(dtconduc[i][2].toString()))); 
             
             if(dtconduc[i][3] != null)
                renta.setUsuario(userService.obtenerUsuarioPorId(sql, new Integer(dtconduc[i][3].toString())));  
             
             if(dtconduc[i][4] != null)
                 renta.setFechaPedido(dtconduc[i][4].toString());
             
             if(dtconduc[i][5] != null)
                 renta.setFechaEntrega(dtconduc[i][5].toString());
             
             if(dtconduc[i][6] != null)
                 renta.setHoraEntrega(dtconduc[i][6].toString());
             
             if(dtconduc[i][7] != null)
                 renta.setFechaDevolucion(dtconduc[i][7].toString());
             
             if(dtconduc[i][8] != null)
                 renta.setDescripcion(dtconduc[i][8].toString());
             
             if(dtconduc[0][9] == null)
                 renta.setDescuento(0f);
             else if(dtconduc[0][9].toString().equals(""))
                 renta.setDescuento(0f);
             else
                 renta.setDescuento(new Float(dtconduc[0][9].toString()));
             
             if(dtconduc[i][10] != null)
                 renta.setCantidadDescuento(new Float(dtconduc[i][10].toString()));
             if(dtconduc[i][11] != null)
                 renta.setIva(new Float(dtconduc[i][11].toString()));
              else
                 renta.setIva(0f);
             
             if(dtconduc[i][12] != null)
                 renta.setComentario(dtconduc[i][12].toString());
             
             if(dtconduc[i][13] != null)
                 renta.setUsuarioChoferId(new Integer(dtconduc[i][13].toString()));
             
             if(dtconduc[i][14] != null)
                 renta.setFolio(new Integer(dtconduc[i][14].toString()));
             
             if(dtconduc[i][15] != null)
                 renta.setStock(dtconduc[i][15].toString());
              
             if(dtconduc[i][16] != null)
                 renta.setTipo(this.obtenerTipoPorId(sql,new Integer(dtconduc[i][16].toString())));
             
             
             if(dtconduc[i][17] != null)
                 renta.setHoraDevolucion(dtconduc[i][17].toString());
             
             if(dtconduc[i][18] != null)
                 renta.setFechaEvento(dtconduc[i][18].toString());
              
             if(dtconduc[i][19] == null)
                 renta.setDepositoGarantia(0f);
             else
                 renta.setDepositoGarantia(new Float(dtconduc[i][19].toString()));
             
             if(dtconduc[i][20] == null)
                 renta.setEnvioRecoleccion(0f);
             else
                 renta.setEnvioRecoleccion(new Float(dtconduc[i][20].toString()));
             
             if(dtconduc[0][21] == null)
                 renta.setMostrarPreciosPdf("0");
             else
                 renta.setMostrarPreciosPdf(dtconduc[0][21]+"");
             
             
             renta.setEstado(this.obtenerEstadoEventoPorId(sql, renta.getEstadoId()));
             // obtenemos el detalle de la renta
             renta.setChofer(userService.obtenerUsuarioPorId(sql,renta.getUsuarioChoferId()));
             renta.setDetalleRenta(this.obtenerDetalleRenta(sql,new Integer(dtconduc[i][0].toString())));
             renta.setAbonos(this.obtenerAbonosPorRentaId(sql,new Integer(dtconduc[i][0].toString())));
             rentas.add(renta);
             
         }
        
        return rentas;
                
    } // fin disponibilidad renta por 
    
    
     public List<Renta> obtenerDisponibilidadRentaPorConsulta(String query,sqlclass sql){

        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(obtenerColumnasRenta(), "renta",query );
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }       
        
         if(dtconduc == null || dtconduc.length <= 0)
            return null;
         
        List<Renta> rentas = new ArrayList<>();
        CustomerService customerService = new CustomerService();
        UserService userService = new UserService();
        
         for (int i = 0; i < dtconduc.length; i++) {
             Renta renta = new Renta();
             renta.setRentaId(new Integer(dtconduc[i][0].toString()));
             if(dtconduc[i][1] != null)
                renta.setEstadoId(new Integer(dtconduc[i][1].toString()));
             
             if(dtconduc[i][2] != null)
                renta.setCliente(customerService.obtenerClientePorId(sql, new Integer(dtconduc[i][2].toString()))); 
             
             if(dtconduc[i][3] != null)
                renta.setUsuario(userService.obtenerUsuarioPorId(sql, new Integer(dtconduc[i][3].toString())));  
             
             if(dtconduc[i][4] != null)
                 renta.setFechaPedido(dtconduc[i][4].toString());
             
             if(dtconduc[i][5] != null)
                 renta.setFechaEntrega(dtconduc[i][5].toString());
             
             if(dtconduc[i][6] != null)
                 renta.setHoraEntrega(dtconduc[i][6].toString());
             
             if(dtconduc[i][7] != null)
                 renta.setFechaDevolucion(dtconduc[i][7].toString());
             
             if(dtconduc[i][8] != null)
                 renta.setDescripcion(dtconduc[i][8].toString());
             
             if(dtconduc[0][9] == null)
                 renta.setDescuento(0f);
             else if(dtconduc[0][9].toString().equals(""))
                 renta.setDescuento(0f);
             else
                 renta.setDescuento(new Float(dtconduc[0][9].toString()));
             
             if(dtconduc[i][10] != null)
                 renta.setCantidadDescuento(new Float(dtconduc[i][10].toString()));
             if(dtconduc[i][11] != null)
                 renta.setIva(new Float(dtconduc[i][11].toString()));
              else
                 renta.setIva(0f);
             
             if(dtconduc[i][12] != null)
                 renta.setComentario(dtconduc[i][12].toString());
             
             if(dtconduc[i][13] != null)
                 renta.setUsuarioChoferId(new Integer(dtconduc[i][13].toString()));
             
             if(dtconduc[i][14] != null)
                 renta.setFolio(new Integer(dtconduc[i][14].toString()));
             
             if(dtconduc[i][15] != null)
                 renta.setStock(dtconduc[i][15].toString());
              
             if(dtconduc[i][16] != null)
                 renta.setTipo(this.obtenerTipoPorId(sql,new Integer(dtconduc[i][16].toString())));
             
             
             if(dtconduc[i][17] != null)
                 renta.setHoraDevolucion(dtconduc[i][17].toString());
             
             if(dtconduc[i][18] != null)
                 renta.setFechaEvento(dtconduc[i][18].toString());
              
             if(dtconduc[i][19] == null)
                 renta.setDepositoGarantia(0f);
             else
                 renta.setDepositoGarantia(new Float(dtconduc[i][19].toString()));
             
             if(dtconduc[i][20] == null)
                 renta.setEnvioRecoleccion(0f);
             else
                 renta.setEnvioRecoleccion(new Float(dtconduc[i][20].toString()));
             
             if(dtconduc[0][21] == null)
                 renta.setMostrarPreciosPdf("0");
             else
                 renta.setMostrarPreciosPdf(dtconduc[0][21]+"");
             
             
             renta.setEstado(this.obtenerEstadoEventoPorId(sql, renta.getEstadoId()));
             // obtenemos el detalle de la renta
             renta.setChofer(userService.obtenerUsuarioPorId(sql,renta.getUsuarioChoferId()));
             renta.setDetalleRenta(this.obtenerDetalleRenta(sql,new Integer(dtconduc[i][0].toString())));
             renta.setAbonos(this.obtenerAbonosPorRentaId(sql,new Integer(dtconduc[i][0].toString())));
             rentas.add(renta);
             
         }
        
        return rentas;
                
    } // fin disponibilidad renta por fechas
    
    
    // obtenemos los pedidos por una consulta armada desde la vista
    public List<Renta> obtenerPedidosPorConsultaSql(String querySql,sqlclass sql){
        
        List<Renta> rentas = new ArrayList<>();
      
        //nombre de columnas, tabla, instruccion sql
        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(obtenerColumnasRenta(), "renta", querySql);
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }    
        
         if(dtconduc == null || dtconduc.length <= 0)
            return null;
         
        CustomerService customerService = new CustomerService();
        UserService userService = new UserService();
        
         for (int i = 0; i < dtconduc.length; i++) {
             Renta renta = new Renta();
             renta.setRentaId(new Integer(dtconduc[i][0].toString()));
             if(dtconduc[i][1] != null)
                renta.setEstadoId(new Integer(dtconduc[i][1].toString()));
             
             if(dtconduc[i][2] != null)
                renta.setCliente(customerService.obtenerClientePorId(sql, new Integer(dtconduc[i][2].toString()))); 
             
             if(dtconduc[i][3] != null)
                renta.setUsuario(userService.obtenerUsuarioPorId(sql, new Integer(dtconduc[i][3].toString())));  
             
             if(dtconduc[i][4] != null)
                 renta.setFechaPedido(dtconduc[i][4].toString());
             
             if(dtconduc[i][5] != null)
                 renta.setFechaEntrega(dtconduc[i][5].toString());
             
             if(dtconduc[i][6] != null)
                 renta.setHoraEntrega(dtconduc[i][6].toString());
             
             if(dtconduc[i][7] != null)
                 renta.setFechaDevolucion(dtconduc[i][7].toString());
             
             if(dtconduc[i][8] != null)
                 renta.setDescripcion(dtconduc[i][8].toString());
             
             if(dtconduc[0][9] == null)
                 renta.setDescuento(0f);
             else if(dtconduc[0][9].toString().equals(""))
                 renta.setDescuento(0f);
             else
                 renta.setDescuento(new Float(dtconduc[0][9].toString()));
             
             if(dtconduc[i][10] != null)
                 renta.setCantidadDescuento(new Float(dtconduc[i][10].toString()));
             if(dtconduc[i][11] != null)
                 renta.setIva(new Float(dtconduc[i][11].toString()));
              else
                 renta.setIva(0f);
             
             if(dtconduc[i][12] != null)
                 renta.setComentario(dtconduc[i][12].toString());
             
             if(dtconduc[i][13] != null)
                 renta.setUsuarioChoferId(new Integer(dtconduc[i][13].toString()));
             
             if(dtconduc[i][14] != null)
                 renta.setFolio(new Integer(dtconduc[i][14].toString()));
             
             if(dtconduc[i][15] != null)
                 renta.setStock(dtconduc[i][15].toString());
              
             if(dtconduc[i][16] != null)
                 renta.setTipo(this.obtenerTipoPorId(sql,new Integer(dtconduc[i][16].toString())));
             
             if(dtconduc[i][17] != null)
                 renta.setHoraDevolucion(dtconduc[i][17].toString());
             
             if(dtconduc[i][18] != null)
                 renta.setFechaEvento(dtconduc[i][18].toString());
              
             if(dtconduc[i][19] == null)
                 renta.setDepositoGarantia(0f);
             else
                 renta.setDepositoGarantia(new Float(dtconduc[i][19].toString()));
             
             if(dtconduc[i][20] == null)
                 renta.setEnvioRecoleccion(0f);
             else
                 renta.setEnvioRecoleccion(new Float(dtconduc[i][20].toString()));
             
             if(dtconduc[0][21] == null)
                 renta.setMostrarPreciosPdf("0");
             else
                 renta.setMostrarPreciosPdf(dtconduc[0][21]+"");
             
             
             renta.setEstado(this.obtenerEstadoEventoPorId(sql, renta.getEstadoId()));
             renta.setChofer(userService.obtenerUsuarioPorId(sql,renta.getUsuarioChoferId()));
             // obtenemos el detalle de la renta
             renta.setDetalleRenta(this.obtenerDetalleRenta(sql,new Integer(dtconduc[i][0].toString())));
             renta.setAbonos(this.obtenerAbonosPorRentaId(sql,new Integer(dtconduc[i][0].toString())));
             rentas.add(renta);
         }
        
        return rentas;
                
    } // fin disponibilidad renta por fechas
    
    
     // obtenemos los pedidos por una consulta armada desde la vista
    public List<Renta> obtenerAbonos(String querySql,sqlclass sql){
        
        List<Renta> rentas = new ArrayList<>();
      
        //nombre de columnas, tabla, instruccion sql
        Object[][] dtconduc = null;
         String[] columnas = {"id_renta", "folio","nombre_cliente","apellidos_cliente","descripcion","fecha",
             "nombre_usuario","apellidos_usuario","abono","comentario","fecha_pago","tipo_pago"             
            };
         
        try {
            dtconduc = sql.GetTabla(columnas, "renta", querySql);
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }    
        
              
         if(dtconduc == null || dtconduc.length <= 0)
            return null;       
        
         for (int i = 0; i < dtconduc.length; i++) {
             Renta renta = new Renta();
             renta.setRentaId(new Integer(dtconduc[i][0].toString()));
                         
             if(dtconduc[i][1] != null)
                renta.setFolio(new Integer(dtconduc[i][1].toString()));
             
             Cliente cliente = new Cliente();
             if(dtconduc[i][2] != null)
                cliente.setNombre(dtconduc[i][2].toString());
             
             if(dtconduc[i][3] != null)
               cliente.setApellidos(dtconduc[i][3].toString());
             
             renta.setCliente(cliente);
             
             if(dtconduc[i][4] != null)
                 renta.setDescripcion(dtconduc[i][4].toString());
             
             Abono abono = new Abono();
             if(dtconduc[i][5] != null)
                 abono.setFecha(dtconduc[i][5].toString());
             
             Usuario usuario = new Usuario();
             if(dtconduc[i][6] != null)
                 usuario.setNombre(dtconduc[i][6].toString());
             
             if(dtconduc[i][7] != null)
                 usuario.setApellidos(dtconduc[i][7].toString());
             
             renta.setUsuario(usuario);
             
             if(dtconduc[i][8] != null)
                 abono.setAbono(new Float(dtconduc[i][8].toString()));
             
              if(dtconduc[i][9] != null)
                 abono.setComentario((dtconduc[i][9].toString()));
              
              if(dtconduc[i][10] != null)
                 abono.setFechaPago((dtconduc[i][10].toString()));

             
             TipoAbono tipo = new TipoAbono();
             if(dtconduc[i][11] != null)
                 tipo.setDescripcion(dtconduc[i][11].toString());
             abono.setTipoAbono(tipo);
             
            List<Abono> abonos = new ArrayList<>(); 
            abonos.add(abono);
            renta.setAbonos(abonos);
             
             rentas.add(renta);
         }
        
        return rentas;
                
    } // fin disponibilidad renta por fechas
    
    
     public List<DetalleRenta> obtenerDetalleRenta(sqlclass sql, int rentaId){
        
        List<DetalleRenta> detalleRentas = new ArrayList<>();
        String[] colName = {"id_detalle_renta", "id_renta","cantidad", 
            "id_articulo", "p_unitario", "comentario", 
            "se_desconto","porcentaje_descuento" };
        //nombre de columnas, tabla, instruccion sql
        
         Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "detalle_renta", "SELECT * FROM detalle_renta "
                + "WHERE id_renta="+rentaId );
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }    
        
         if(dtconduc == null || dtconduc.length <= 0)
            return null;
         
        ItemService itemService = new ItemService();
        
         for (int i = 0; i < dtconduc.length; i++) {
             DetalleRenta detalle = new DetalleRenta();
             detalle.setDetalleRentaId(new Integer(dtconduc[i][0].toString()));
             
             if(dtconduc[i][1] != null)
                detalle.setRentaId(new Integer(dtconduc[i][1].toString()));
             
             if(dtconduc[i][2] != null)
                detalle.setCantidad(new Float(dtconduc[i][2].toString()));
             
             if(dtconduc[i][3] != null)
                detalle.setArticulo(itemService.obtenerArticuloPorId(sql, new Integer(dtconduc[i][3].toString())));
             
             if(dtconduc[i][4] != null)
                detalle.setPrecioUnitario(new Float(dtconduc[i][4].toString()));
             
               if(dtconduc[i][5] != null)
                detalle.setComentario(dtconduc[i][5].toString());
              
                if(dtconduc[i][6] != null)
                detalle.setSeDesconto(dtconduc[i][6].toString());
                
                if(dtconduc[i][7] != null)
                    detalle.setPorcentajeDescuento(new Float (dtconduc[i][7]+""));
                else
                    detalle.setPorcentajeDescuento(0f);

             detalleRentas.add(detalle);
         }
        
        return detalleRentas;
                
    } // fin detalle renta
     
     public Tipo obtenerTipoPorId(sqlclass sql, int tipoId){
         
         String[] colName = {"id_tipo", "tipo"};
        
         Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "tipo", "SELECT * FROM tipo WHERE id_tipo="+tipoId );
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }    
         
          if(dtconduc == null || dtconduc.length <= 0)
            return null;
         Tipo tipo = new Tipo();
         if(dtconduc[0][0] != null)
            tipo.setTipoId(new Integer(dtconduc[0][0]+""));
         if(dtconduc[0][1] != null)
            tipo.setTipo(dtconduc[0][1]+"");
         
         return tipo;
         
     }
     
         
     
     public Renta obtenerRentaPorId(int rentaId,sqlclass sql){
        
        Renta renta = new Renta();
        
        //nombre de columnas, tabla, instruccion sql
         Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(this.obtenerColumnasRenta(), "renta", "SELECT * FROM renta renta "
                +"WHERE renta.id_renta = "+rentaId );
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }    
       
         if(dtconduc == null || dtconduc.length <= 0)
            return null;
         
         // servicio para los clientes
        CustomerService customerService = new CustomerService();
        // serivcio para los usuarios
        UserService userService = new UserService();        
        
             renta.setRentaId(new Integer(dtconduc[0][0].toString()));
             if(dtconduc[0][1] != null)
                renta.setEstadoId(new Integer(dtconduc[0][1].toString()));
             
             if(dtconduc[0][2] != null)
                renta.setCliente(customerService.obtenerClientePorId(sql, new Integer(dtconduc[0][2].toString()))); 
             
             if(dtconduc[0][3] != null)
                renta.setUsuario(userService.obtenerUsuarioPorId(sql, new Integer(dtconduc[0][3].toString())));  
             
             if(dtconduc[0][4] != null)
                 renta.setFechaPedido(dtconduc[0][4].toString());
             
             if(dtconduc[0][5] != null)
                 renta.setFechaEntrega(dtconduc[0][5].toString());
             
             if(dtconduc[0][6] != null)
                 renta.setHoraEntrega(dtconduc[0][6].toString());
             
             if(dtconduc[0][7] != null)
                 renta.setFechaDevolucion(dtconduc[0][7].toString());
             
             if(dtconduc[0][8] != null)
                 renta.setDescripcion(dtconduc[0][8].toString());
             
             if(dtconduc[0][9] == null)
                 renta.setDescuento(0f);
             else if(dtconduc[0][9].toString().equals(""))
                 renta.setDescuento(0f);
             else
                 renta.setDescuento(new Float(dtconduc[0][9].toString()));
             
             if(dtconduc[0][10] != null)
                 renta.setCantidadDescuento(new Float(dtconduc[0][10].toString()));
             
             if(dtconduc[0][11] != null)
                 renta.setIva(new Float(dtconduc[0][11].toString()));
             else
                 renta.setIva(0f);
             
             if(dtconduc[0][12] != null)
                 renta.setComentario(dtconduc[0][12].toString());
             
             if(dtconduc[0][13] != null)
                 renta.setUsuarioChoferId(new Integer(dtconduc[0][13].toString()));
             
             if(dtconduc[0][14] != null)
                 renta.setFolio(new Integer(dtconduc[0][14].toString()));
             
             if(dtconduc[0][15] != null)
                 renta.setStock(dtconduc[0][15].toString());
              
             if(dtconduc[0][16] != null)
                 renta.setTipo(this.obtenerTipoPorId(sql,new Integer(dtconduc[0][16].toString())));
             
             if(dtconduc[0][17] != null)
                 renta.setHoraDevolucion(dtconduc[0][17].toString());
             
             if(dtconduc[0][18] != null)
                 renta.setFechaEvento(dtconduc[0][18].toString());
              
             if(dtconduc[0][19] == null)
                 renta.setDepositoGarantia(0f);
             else
                 renta.setDepositoGarantia(new Float(dtconduc[0][19].toString()));
             
             if(dtconduc[0][20] == null)
                 renta.setEnvioRecoleccion(0f);
             else
                 renta.setEnvioRecoleccion(new Float(dtconduc[0][20].toString()));
             
             if(dtconduc[0][21] == null)
                 renta.setMostrarPreciosPdf("0");
             else
                 renta.setMostrarPreciosPdf(dtconduc[0][21]+"");
             
             
             renta.setEstado(this.obtenerEstadoEventoPorId(sql, renta.getEstadoId()));
             renta.setChofer(userService.obtenerUsuarioPorId(sql,renta.getUsuarioChoferId()));
             // obtenemos el detalle de la renta
             renta.setDetalleRenta(this.obtenerDetalleRenta(sql,new Integer(dtconduc[0][0].toString())));
             renta.setAbonos(this.obtenerAbonosPorRentaId(sql,new Integer(dtconduc[0][0].toString())));
             
             // calculando faltantes
            
             float fTotalFaltantes = 0f;
             
             String totalFaltantes = sql.GetData("total",
//                      "SELECT SUM(IF( ( f.fg_devolucion = '0' AND f.fg_accidente_trabajo = '0'),(f.cantidad * a.precio_compra),0) )AS total "
                              "SELECT SUM(IF( ( f.fg_devolucion = '0' AND f.fg_accidente_trabajo = '0'),(f.cantidad * f.precio_cobrar),0) )AS total "
                    + "FROM faltantes f "
                    + "INNER JOIN articulo a ON (f.id_articulo = a.id_articulo) "
                    + "WHERE f.id_renta=" + renta.getRentaId() + " "
                    + "AND f.fg_activo = '1' "
                    );
            if(totalFaltantes == null || totalFaltantes.equals(""))
                totalFaltantes = "0";
            
            try {
                fTotalFaltantes = new Float(totalFaltantes);
            } catch (Exception e) {
            }
           
            if(fTotalFaltantes > 0){
                // el pedido tiene pago pendiente por faltante
                if(renta.getDepositoGarantia()>0){
                    // a dejado deposito en garantia
                    fTotalFaltantes -= renta.getDepositoGarantia();
                }                
            }
            
            renta.setTotalFaltantes(fTotalFaltantes);
        
        return renta;
                
    } // renta por id
     
     public List<Abono> obtenerAbonosPorRentaId(sqlclass sql, int rentaId){
         List<Abono> abonos = new ArrayList<>();
         
        String[] colName = {"id_abonos","id_renta","id_usuario","fecha","abono","comentario" };
        //nombre de columnas, tabla, instruccion sql
        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "abonos", "SELECT * FROM abonos "
                + "WHERE id_renta="+rentaId );
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }    
        
         if(dtconduc == null || dtconduc.length <= 0)
            return null;
         
           for (int i = 0; i < dtconduc.length; i++) {
               Abono abono = new Abono();
               if(dtconduc[i][0] != null)
                   abono.setAbonoId(new Integer (dtconduc[i][0].toString()) );
               if(dtconduc[i][1] != null)
                    abono.setRentaId(new Integer (dtconduc[i][1].toString()) );
               if(dtconduc[i][2] != null)
                    abono.setUsuarioId(new Integer (dtconduc[i][2].toString()) );
               if(dtconduc[i][3] != null)
                    abono.setFecha(dtconduc[i][3].toString() );
               if(dtconduc[i][4] != null)
                    abono.setAbono(new Float (dtconduc[i][4].toString()) );
               if(dtconduc[i][5] != null)
                    abono.setComentario(dtconduc[i][5].toString());               
               abonos.add(abono);               
           }
         
         return abonos;
     }
     
     public EstadoEvento obtenerEstadoEventoPorId(sqlclass sql, int estadoId){
         String[] colName = {"id_estado","descripcion" };
         Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "estado", "SELECT * FROM estado WHERE id_estado="+estadoId );
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } 
      
         
          if(dtconduc == null || dtconduc.length <= 0)
            return null;
         EstadoEvento estado = new EstadoEvento();
          
         if(dtconduc[0][0] != null)
             estado.setEstadoId(new Integer(dtconduc[0][0]+""));
         if(dtconduc[0][1] != null)
             estado.setDescripcion(dtconduc[0][1]+"");        
         
         return estado;
     }
     
     public float calcularSubtotalRenta(List<DetalleRenta> listaDetalle){
     
         float subtotal = 0f;
         for(DetalleRenta detalle : listaDetalle){
             subtotal += detalle.getPrecioUnitario() * detalle.getCantidad();
         }
         return subtotal;
     }
     
     public float calcularAbonos(List<Abono> listaAbonos){
         float abonos = 0f;
         for(Abono abono : listaAbonos){
             abonos += abono.getAbono();
         }
         
         return abonos;
     }
     
     public String[] obtenerColumnasRenta(){
     
         String[] columnas = {"id_renta", "id_estado","id_clientes",  "id_usuarios", "fecha_pedido", "fecha_entrega", 
            "hora_entrega", "fecha_devolucion", "descripcion", "descuento","cantidad_descuento",
            "iva","comentario","id_usuario_chofer","folio","stock","id_tipo" ,"hora_devolucion","fecha_evento","deposito_garantia","envio_recoleccion",
            "mostrar_precios_pdf"
            };
         
         return columnas;
         
     }
 public List<Renta> obtenerPedidosPorConsultaSqlSinDetalle(String querySql,sqlclass sql){
        
        List<Renta> rentas = new ArrayList<>();
       String[] columnas = {
            "r.id_renta", // 0
            "r.folio", // 1
            "c.nombre", // 2
            "c.apellidos",// 3
            "estado.id_estado",// 4
            "estado.descripcion",// 5
           "r.fecha_evento",// 6
           "r.fecha_entrega",// 7
           "r.hora_entrega",// 8
           "r.descripcion",// 9
           "chofer.nombre",// 10
           "chofer.apellidos",// 11
           "tipo.id_tipo",// 12
           "tipo.tipo",// 13
           "r.cantidad_descuento", // 14
           "r.iva",// 15
           "r.deposito_garantia",// 16
           "r.envio_recoleccion",// 17
           "nombre_usuario",// 18
           "apellidos_usuario"// 19
       };
        //nombre de columnas, tabla, instruccion sql
        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(columnas, "renta", querySql);
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }    
        
         if(dtconduc == null || dtconduc.length <= 0)
            return null;
        
         for (int i = 0; i < dtconduc.length; i++) {
             Renta renta = new Renta();
             renta.setRentaId(new Integer(dtconduc[i][0].toString()));
             
             if(dtconduc[i][1] != null)
                renta.setFolio(new Integer(dtconduc[i][1].toString())); 
             
             // CLIENTE
             Cliente cliente = new Cliente();             
             if(dtconduc[i][2] != null)
                 cliente.setNombre(dtconduc[i][2]+"");             
             if(dtconduc[i][3] != null)
                 cliente.setApellidos(dtconduc[i][3]+"");
             
                renta.setCliente(cliente);  
             
             EstadoEvento estado = new EstadoEvento();
             if(dtconduc[i][4] != null)
                 estado.setEstadoId(new Integer(dtconduc[i][4]+""));
             if(dtconduc[i][5] != null)
                 estado.setDescripcion(dtconduc[i][5]+"");
             
             renta.setEstado(estado);
             
             if(dtconduc[i][6] != null)
                 renta.setFechaEvento(dtconduc[i][6]+"");
             
             if(dtconduc[i][7] != null)
                 renta.setFechaEntrega(dtconduc[i][7]+"");
             
             if(dtconduc[i][8] != null)
                 renta.setHoraEntrega(dtconduc[i][8]+"");
             
             if(dtconduc[i][9] != null)
                 renta.setDescripcion(dtconduc[i][9]+"");
             
             Usuario chofer = new Usuario();
             if(dtconduc[i][10] != null)
                 chofer.setNombre(dtconduc[i][10]+"");
             if(dtconduc[i][11] != null)
                 chofer.setApellidos(dtconduc[i][11]+"");
                 
            renta.setChofer(chofer);
            
            Tipo tipo = new Tipo();
            if(dtconduc[i][12] != null)
                tipo.setTipoId(new Integer(dtconduc[i][12]+""));
            if(dtconduc[i][13] != null)
                tipo.setTipo(dtconduc[i][13]+"");
            
            float fDescuento = 0f;
            float fIva = 0f;
            float fDepositoGarantia = 0f;
            float fEnvioRecoleccion = 0f;
            float fTotal = 0f;
            float fTotalIva = 0f;            
            
            if(dtconduc[i][14] != null)
                fDescuento = new Float(dtconduc[i][14]+"");
            if(dtconduc[i][15] != null){
                fIva = new Float(dtconduc[i][15]+"");
                
            }
            if(dtconduc[i][16] != null){
                fDepositoGarantia = new Float(dtconduc[i][16]+"");
                renta.setDepositoGarantia(fDepositoGarantia);
            }
            if(dtconduc[i][17] != null){
                fEnvioRecoleccion = new Float(dtconduc[i][17]+"");
                renta.setEnvioRecoleccion(fEnvioRecoleccion);
            }
            
            Usuario usuario = new Usuario();
            if(dtconduc[i][18] != null)
                usuario.setNombre(dtconduc[i][18]+"");
            
            if(dtconduc[i][19] != null)
                usuario.setApellidos(dtconduc[i][19]+"");
            
            renta.setUsuario(usuario);
                      
            renta.setTipo(tipo);
            renta.setCantidadDescuento(fDescuento);
         
            if(renta.getTipo().getTipoId() == new Integer(ApplicationConstants.TIPO_COTIZACION))
            {
                 renta.setDescripcionCobranza(ApplicationConstants.COBRANZA_NO_PAGADO);
                 rentas.add(renta);
                 continue;
            }
             renta.setAbonos(this.obtenerAbonosPorRentaId(sql,new Integer(dtconduc[i][0].toString())));
//             renta.setDetalleRenta(this.obtenerDetalleRenta(sql,new Integer(dtconduc[i][0].toString())));
             
//             // modulo cobranza
           String subtotal = sql.GetData("suma", "SELECT IF(porcentaje_descuento >= 0, "
                        + "(SUM( (cantidad*p_unitario) - ((cantidad*p_unitario) * (porcentaje_descuento / 100) ))), "
                        + "(SUM( (cantidad*p_unitario) )) "
                        + ")AS suma "
                        + "FROM detalle_renta WHERE id_renta = "+renta.getRentaId());
           
          if(subtotal == null || subtotal.isEmpty() || subtotal.equals(""))
              subtotal = "0";
           renta.setSubTotal(new Float(subtotal));
           
            float fAbonos = 0f;
            float fSubTotal = 0f;
            
            if(subtotal != null)
                fSubTotal = new Float(subtotal);
             
             if(renta.getAbonos() != null && renta.getAbonos().size()>0){
                for(Abono abono : renta.getAbonos())
                    fAbonos += abono.getAbono();
             }
             
                 
             fTotal =  (fEnvioRecoleccion + fDepositoGarantia + fTotalIva + fSubTotal);
             fTotal -= fDescuento;
             fTotalIva = fTotal * ( fIva/100 );
             renta.setIva(fTotalIva);
             fTotal =  (fEnvioRecoleccion + fDepositoGarantia + fTotalIva + fSubTotal);
             fTotal -= fDescuento;
             fTotal -= fAbonos;
             if(fTotal >0 && fTotal<1)
                 fTotal = 0;
             
             
             // calculando faltantes
            
             float fTotalFaltantes = 0f;
             
             String totalFaltantes = sql.GetData("total",
//                      "SELECT SUM(IF( ( f.fg_devolucion = '0' AND f.fg_accidente_trabajo = '0'),(f.cantidad * a.precio_compra),0) )AS total "
                              "SELECT SUM(IF( ( f.fg_devolucion = '0' AND f.fg_accidente_trabajo = '0'),(f.cantidad * f.precio_cobrar),0) )AS total "
                    + "FROM faltantes f "
                    + "INNER JOIN articulo a ON (f.id_articulo = a.id_articulo) "
                    + "WHERE f.id_renta=" + renta.getRentaId() + " "
                    + "AND f.fg_activo = '1' "
                    );
            if(totalFaltantes == null || totalFaltantes.equals(""))
                totalFaltantes = "0";
            
            try {
                fTotalFaltantes = new Float(totalFaltantes);
            } catch (Exception e) {
            }
           
            renta.setTotalFaltantes(fTotalFaltantes);
            
            if(fTotalFaltantes > 0){
                // el pedido tiene pago pendiente por faltante
                if(renta.getDepositoGarantia()>0){
                    // a dejado deposito en garantia
                    fTotalFaltantes -= renta.getDepositoGarantia();
                }                
            }
            // fin calcular faltantes
             
             fTotal += fTotalFaltantes;
             renta.setTotalFaltantesPorCubrir(fTotalFaltantes);
             if(fTotal <= 0){
                 fTotal = 0;
                 renta.setDescripcionCobranza(ApplicationConstants.COBRANZA_PAGADO);
             }
             else if(fTotal > 0 && fAbonos == 0)
                 renta.setDescripcionCobranza(ApplicationConstants.COBRANZA_NO_PAGADO);
             else if (fTotal > 0 && fAbonos > 0)
                 renta.setDescripcionCobranza(ApplicationConstants.COBRANZA_PARCIAL_PAGADO);   
             
             renta.setTotal(fTotal);
             
             rentas.add(renta);
         }
        
        return rentas;
                
    } // fin renta sin detalle
 
 
  public List<TipoAbono> obtenerTiposAbono(sqlclass sql){
         List<TipoAbono> tipoAbonos = new ArrayList<>();
         
        String[] colName = {"id_tipo_abono","descripcion","fg_activo","fecha_registro" };
        //nombre de columnas, tabla, instruccion sql
        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(colName, "tipo_abono", "SELECT * FROM tipo_abono "
                + "WHERE fg_activo = '1' ORDER BY descripcion " );
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }    
        
         if(dtconduc == null || dtconduc.length <= 0)
            return null;
         
           for (int i = 0; i < dtconduc.length; i++) {
               TipoAbono tipo = new TipoAbono();
               if(dtconduc[i][0] != null)
                   tipo.setTipoAbonoId(new Integer (dtconduc[i][0].toString()) );
               if(dtconduc[i][1] != null)
                    tipo.setDescripcion(dtconduc[i][1]+"");
               if(dtconduc[i][2] != null)
                    tipo.setFgActivo(dtconduc[i][2].toString().charAt(0));
                        
               tipoAbonos.add(tipo);               
           }
         
         return tipoAbonos;
     }
  
  public List<Renta> obtenerFoliosPorArticulo(String query,sqlclass sql){
        
        List<Renta> rentas = new ArrayList<>();
       String[] columnas = {"id_articulo","id_renta","folio","codigo_articulo","articulo_descripcion","cantidad_pedido","tipo_pedido","descripcion_estado"};
               
        //nombre de columnas, tabla, instruccion sql
        Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(columnas, "renta", query);
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }    
        
         if(dtconduc == null || dtconduc.length <= 0)
            return null;
        
         for (int i = 0; i < dtconduc.length; i++) {
             List<DetalleRenta> listDetalle = new ArrayList<>();
             DetalleRenta detalle = new DetalleRenta();
             Articulo articulo = new Articulo();
             articulo.setArticuloId(new Integer(dtconduc[i][0].toString()));
             
             Renta renta = new Renta();
             renta.setRentaId(new Integer(dtconduc[i][1].toString()));
             
             if(dtconduc[i][2] != null)
                renta.setFolio(new Integer(dtconduc[i][2].toString()));
             
             if(dtconduc[i][3] != null)
                articulo.setCodigo(dtconduc[i][3]+"");
               
            
             if(dtconduc[i][4] != null)
                articulo.setDescripcion(dtconduc[i][4]+"");
                detalle.setArticulo(articulo);
             
             if(dtconduc[i][5] != null) 
                 detalle.setCantidad(new Float(dtconduc[i][5]+""));
             
            Tipo tipo = new Tipo();
            if(dtconduc[i][6] != null)
                tipo.setTipo(dtconduc[i][6]+"");
                renta.setTipo(tipo);
             
             EstadoEvento estado = new EstadoEvento();             
             if(dtconduc[i][7] != null)
                 estado.setDescripcion(dtconduc[i][7]+"");             
             renta.setEstado(estado);           
             
             listDetalle.add(detalle);
             renta.setDetalleRenta(listDetalle);
             rentas.add(renta);
         } // fin for
        
        return rentas;
                
    } // fin folios por articulo
  
  public Renta obtenerRentaPorFolio(int folio,sqlclass sql){
        
        Renta renta = new Renta();
        
        //nombre de columnas, tabla, instruccion sql
         Object[][] dtconduc = null;
        try {
            dtconduc = sql.GetTabla(this.obtenerColumnasRenta(), "renta", "SELECT * FROM renta renta "
                +"WHERE renta.folio = '"+folio+"' " );
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }    
       
         if(dtconduc == null || dtconduc.length <= 0)
            return null;
         
         // servicio para los clientes
        CustomerService customerService = new CustomerService();
        // serivcio para los usuarios
        UserService userService = new UserService();        
        
             renta.setRentaId(new Integer(dtconduc[0][0].toString()));
             if(dtconduc[0][1] != null)
                renta.setEstadoId(new Integer(dtconduc[0][1].toString()));
             
             if(dtconduc[0][2] != null)
                renta.setCliente(customerService.obtenerClientePorId(sql, new Integer(dtconduc[0][2].toString()))); 
             
             if(dtconduc[0][3] != null)
                renta.setUsuario(userService.obtenerUsuarioPorId(sql, new Integer(dtconduc[0][3].toString())));  
             
             if(dtconduc[0][4] != null)
                 renta.setFechaPedido(dtconduc[0][4].toString());
             
             if(dtconduc[0][5] != null)
                 renta.setFechaEntrega(dtconduc[0][5].toString());
             
             if(dtconduc[0][6] != null)
                 renta.setHoraEntrega(dtconduc[0][6].toString());
             
             if(dtconduc[0][7] != null)
                 renta.setFechaDevolucion(dtconduc[0][7].toString());
             
             if(dtconduc[0][8] != null)
                 renta.setDescripcion(dtconduc[0][8].toString());
             
             if(dtconduc[0][9] == null)
                 renta.setDescuento(0f);
             else if(dtconduc[0][9].toString().equals(""))
                 renta.setDescuento(0f);
             else
                 renta.setDescuento(new Float(dtconduc[0][9].toString()));
             
             if(dtconduc[0][10] != null)
                 renta.setCantidadDescuento(new Float(dtconduc[0][10].toString()));
             
             if(dtconduc[0][11] != null)
                 renta.setIva(new Float(dtconduc[0][11].toString()));
             else
                 renta.setIva(0f);
             
             if(dtconduc[0][12] != null)
                 renta.setComentario(dtconduc[0][12].toString());
             
             if(dtconduc[0][13] != null)
                 renta.setUsuarioChoferId(new Integer(dtconduc[0][13].toString()));
             
             if(dtconduc[0][14] != null)
                 renta.setFolio(new Integer(dtconduc[0][14].toString()));
             
             if(dtconduc[0][15] != null)
                 renta.setStock(dtconduc[0][15].toString());
              
             if(dtconduc[0][16] != null)
                 renta.setTipo(this.obtenerTipoPorId(sql,new Integer(dtconduc[0][16].toString())));
             
             if(dtconduc[0][17] != null)
                 renta.setHoraDevolucion(dtconduc[0][17].toString());
             
             if(dtconduc[0][18] != null)
                 renta.setFechaEvento(dtconduc[0][18].toString());
              
             if(dtconduc[0][19] == null)
                 renta.setDepositoGarantia(0f);
             else
                 renta.setDepositoGarantia(new Float(dtconduc[0][19].toString()));
             
             if(dtconduc[0][20] == null)
                 renta.setEnvioRecoleccion(0f);
             else
                 renta.setEnvioRecoleccion(new Float(dtconduc[0][20].toString()));
             
             if(dtconduc[0][21] == null)
                 renta.setMostrarPreciosPdf("0");
             else
                 renta.setMostrarPreciosPdf(dtconduc[0][21]+"");
             
             
             renta.setEstado(this.obtenerEstadoEventoPorId(sql, renta.getEstadoId()));
             renta.setChofer(userService.obtenerUsuarioPorId(sql,renta.getUsuarioChoferId()));
             // obtenemos el detalle de la renta
             renta.setDetalleRenta(this.obtenerDetalleRenta(sql,new Integer(dtconduc[0][0].toString())));
             renta.setAbonos(this.obtenerAbonosPorRentaId(sql,new Integer(dtconduc[0][0].toString())));
        
        return renta;
                
    } // renta por folio
  
  public TipoAbono obtenerTipoAbonoPorDescripcion(String descripcion){
      return salesDao.obtenerTipoAbonoPorDescripcion(descripcion);      
  }
  
  public void actualizarAbonoPorId(Abono abono){
      salesDao.actualizarAbonoPorId(abono);
  }
 
}
