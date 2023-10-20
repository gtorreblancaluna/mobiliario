package services;

import clases.sqlclass;
import common.constants.ApplicationConstants;
import dao.SalesDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import javax.swing.JOptionPane;
import common.model.Abono;
import common.model.Articulo;
import common.model.Cliente;
import common.model.DetalleRenta;
import common.model.EstadoEvento;
import common.model.Renta;
import common.model.Tipo;
import common.model.TipoAbono;
import common.model.Usuario;
import common.services.UserService;
import common.utilities.UtilityCommon;
import model.querys.rentas.ItemByFolioResultQuery;
import model.querys.rentas.SearchItemByFolioParams;


public class SaleService {
    private static final Logger log = Logger.getLogger(SaleService.class.getName());
    private final SalesDAO salesDao;
    private final UserService userService = UserService.getInstance();
    
    
    private SaleService () {
        salesDao = SalesDAO.getInstance();
    }
    
    private static final SaleService SINGLE_INSTANCE = null;
    
    public static SaleService getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new SaleService();
        }
        return SINGLE_INSTANCE;
    }
    public List<ItemByFolioResultQuery> getItemsByFolio(SearchItemByFolioParams searchItemByFolioParams) throws DataOriginException{
        
        List<ItemByFolioResultQuery> itemByFolioResultQuerys
                = salesDao.getItemsByFolio(searchItemByFolioParams);
        
        itemByFolioResultQuerys
                .stream()
                .forEach(item -> {
                    float unitPrice = item.getItemUnitPrice() != null ? item.getItemUnitPrice() : 0;
                    float itemAmount = item.getItemAmount() != null ? item.getItemAmount() : 0;
                    float total = itemAmount * unitPrice;
                    if (item.getItemDiscountRate() != null && item.getItemDiscountRate() > 0) {
                        float percentaje = item.getItemDiscountRate() / 100;
                        float discount = total * percentaje;
                        total = total - discount;
                    }
                    item.setItemSubTotal(total);
                });
        
        return itemByFolioResultQuerys;
    }
    
    // inserta un detalle de renta y devuelve el ultimo id insertado
    public int insertarDetalleRenta(String[] datos, sqlclass sql){
        try {
            sql.InsertarRegistro(datos, "INSERT INTO detalle_renta (id_renta,cantidad,id_articulo,p_unitario,porcentaje_descuento) values (?,?,?,?,?)");
            String id = sql.GetData("id_detalle_renta", "SELECT id_detalle_renta FROM detalle_renta ORDER BY id_detalle_renta DESC LIMIT 1 ");
            return Integer.parseInt(id);
        } catch (SQLException ex) {
//            Logger.getLogger(SaleService.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al insertar registro ", "Error", JOptionPane.ERROR);
            return 0;
        }
    }
    
    public List<DetalleRenta> getDetailByRentId (String rentId) throws DataOriginException{
      return salesDao.getDetailByRentId(rentId);
    }
    
    
    // obtener la disponibilidad de articulos en un rango de fechas
    @Deprecated
    public List<Renta> obtenerDisponibilidadRenta(String fechaInicial,String fechaFinal,sqlclass sql)throws Exception{
        
                
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
        CustomerService customerService = CustomerService.getInstance();
         
         for (int i = 0; i < dtconduc.length; i++) {
             Renta renta = new Renta();
             renta.setRentaId(Integer.parseInt(dtconduc[i][0].toString()));
             if(dtconduc[i][1] != null)
                renta.setEstadoId(Integer.parseInt(dtconduc[i][1].toString()));
             
             if(dtconduc[i][2] != null)
                renta.setCliente(customerService.obtenerClientePorId(Long.parseLong(dtconduc[i][2].toString()))); 
             
             if(dtconduc[i][3] != null)
                renta.setUsuario(userService.getById(Integer.parseInt(dtconduc[i][3].toString())));  
             
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
                 renta.setDescuento(Float.parseFloat(dtconduc[0][9].toString()));
             
             if(dtconduc[i][10] != null)
                 renta.setCantidadDescuento(Float.parseFloat(dtconduc[i][10].toString()));
             if(dtconduc[i][11] != null)
                 renta.setIva(Float.parseFloat(dtconduc[i][11].toString()));
              else
                 renta.setIva(0f);
             
             if(dtconduc[i][12] != null)
                 renta.setComentario(dtconduc[i][12].toString());
             
             if(dtconduc[i][13] != null)
                 renta.setUsuarioChoferId(Integer.parseInt(dtconduc[i][13].toString()));
             
             if(dtconduc[i][14] != null)
                 renta.setFolio(Integer.parseInt(dtconduc[i][14].toString()));
             
             if(dtconduc[i][15] != null)
                 renta.setStock(dtconduc[i][15].toString());
              
             if(dtconduc[i][16] != null)
                 renta.setTipo(this.obtenerTipoPorId(sql,Integer.parseInt(dtconduc[i][16].toString())));
             
             
             if(dtconduc[i][17] != null)
                 renta.setHoraDevolucion(dtconduc[i][17].toString());
             
             if(dtconduc[i][18] != null)
                 renta.setFechaEvento(dtconduc[i][18].toString());
              
             if(dtconduc[i][19] == null)
                 renta.setDepositoGarantia(0f);
             else
                 renta.setDepositoGarantia(Float.parseFloat(dtconduc[i][19].toString()));
             
             if(dtconduc[i][20] == null)
                 renta.setEnvioRecoleccion(0f);
             else
                 renta.setEnvioRecoleccion(Float.parseFloat(dtconduc[i][20].toString()));
             
             if(dtconduc[0][21] == null)
                 renta.setMostrarPreciosPdf("0");
             else
                 renta.setMostrarPreciosPdf(dtconduc[0][21]+"");
             
             
             renta.setEstado(this.obtenerEstadoEventoPorId(sql, renta.getEstadoId()));
             // obtenemos el detalle de la renta
             renta.setChofer(userService.getById(renta.getUsuarioChoferId()));
             renta.setDetalleRenta(this.obtenerDetalleRenta(Integer.parseInt(dtconduc[i][0].toString())));
             renta.setAbonos(this.obtenerAbonosPorRentaId(sql,Integer.parseInt(dtconduc[i][0].toString())));
             rentas.add(renta);
             
         }
        
        return rentas;
                
    } // fin disponibilidad renta por     
    
    // obtenemos los pedidos por una consulta armada desde la vista
    public List<Renta> obtenerPedidosPorConsultaSql(String querySql,sqlclass sql)throws Exception {
        
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
         
        CustomerService customerService = CustomerService.getInstance();
        
         for (int i = 0; i < dtconduc.length; i++) {
             Renta renta = new Renta();
             renta.setRentaId(Integer.parseInt(dtconduc[i][0].toString()));
             if(dtconduc[i][1] != null)
                renta.setEstadoId(Integer.parseInt(dtconduc[i][1].toString()));
             
             if(dtconduc[i][2] != null)
                renta.setCliente(customerService.obtenerClientePorId(Long.parseLong(dtconduc[i][2].toString()))); 
             
             if(dtconduc[i][3] != null)
                renta.setUsuario(userService.getById( Integer.parseInt(dtconduc[i][3].toString())));  
             
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
                 renta.setDescuento(Float.parseFloat(dtconduc[0][9].toString()));
             
             if(dtconduc[i][10] != null)
                 renta.setCantidadDescuento(Float.parseFloat(dtconduc[i][10].toString()));
             if(dtconduc[i][11] != null)
                 renta.setIva(Float.parseFloat(dtconduc[i][11].toString()));
              else
                 renta.setIva(0f);
             
             if(dtconduc[i][12] != null)
                 renta.setComentario(dtconduc[i][12].toString());
             
             if(dtconduc[i][13] != null)
                 renta.setUsuarioChoferId(Integer.parseInt(dtconduc[i][13].toString()));
             
             if(dtconduc[i][14] != null)
                 renta.setFolio(Integer.parseInt(dtconduc[i][14].toString()));
             
             if(dtconduc[i][15] != null)
                 renta.setStock(dtconduc[i][15].toString());
              
             if(dtconduc[i][16] != null)
                 renta.setTipo(this.obtenerTipoPorId(sql,Integer.parseInt(dtconduc[i][16].toString())));
             
             if(dtconduc[i][17] != null)
                 renta.setHoraDevolucion(dtconduc[i][17].toString());
             
             if(dtconduc[i][18] != null)
                 renta.setFechaEvento(dtconduc[i][18].toString());
              
             if(dtconduc[i][19] == null)
                 renta.setDepositoGarantia(0f);
             else
                 renta.setDepositoGarantia(Float.parseFloat(dtconduc[i][19].toString()));
             
             if(dtconduc[i][20] == null)
                 renta.setEnvioRecoleccion(0f);
             else
                 renta.setEnvioRecoleccion(Float.parseFloat(dtconduc[i][20].toString()));
             
             if(dtconduc[0][21] == null)
                 renta.setMostrarPreciosPdf("0");
             else
                 renta.setMostrarPreciosPdf(dtconduc[0][21]+"");
             
             
             renta.setEstado(this.obtenerEstadoEventoPorId(sql, renta.getEstadoId()));
             renta.setChofer(userService.getById(renta.getUsuarioChoferId()));
             // obtenemos el detalle de la renta
             renta.setDetalleRenta(this.obtenerDetalleRenta(Integer.parseInt(dtconduc[i][0].toString())));
             renta.setAbonos(this.obtenerAbonosPorRentaId(sql,Integer.parseInt(dtconduc[i][0].toString())));
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
             renta.setRentaId(Integer.parseInt(dtconduc[i][0].toString()));
                         
             if(dtconduc[i][1] != null)
                renta.setFolio(Integer.parseInt(dtconduc[i][1].toString()));
             
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
                 abono.setAbono(Float.parseFloat(dtconduc[i][8].toString()));
             
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
    
    
     public List<DetalleRenta> obtenerDetalleRenta(int rentaId) throws Exception{
        
       return salesDao.getDetailByRentId(rentaId+"");
                
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
            tipo.setTipoId(Integer.parseInt(dtconduc[0][0]+""));
         if(dtconduc[0][1] != null)
            tipo.setTipo(dtconduc[0][1]+"");
         
         return tipo;
         
     }
          
     public Renta obtenerRentaPorId(int rentaId) throws Exception { 
        
        Renta renta = salesDao.obtenerRentaPorId(rentaId);
        if (renta != null) {
            renta.setDetalleRenta(this.obtenerDetalleRenta(renta.getRentaId()));
            UtilityCommon.calcularTotalesPorRenta(renta);
        }
        
        return renta;
                
    } // renta por id
     
      public Renta obtenerRentaPorIdSinDetalle(int rentaId) throws Exception { 
        
        return salesDao.obtenerRentaPorId(rentaId);
      
                
    } // renta por id
     
     public Renta obtenerRentaPorIdSinSumas(int rentaId) throws Exception { 
        
        Renta renta = salesDao.obtenerRentaPorIdSinSumas(rentaId);
        if (renta != null) {
            renta.setDetalleRenta(this.obtenerDetalleRenta(renta.getRentaId()));
            UtilityCommon.calcularTotalesPorRenta(renta);
        }
        
        return renta;
                
    } // renta por id
     
     public Renta obtenerRentaPorFolio(Integer folio) throws BusinessException { 
        try {
            Renta renta = salesDao.obtenerRentaPorFolio(folio);
            if (renta != null) {
              renta.setDetalleRenta(this.obtenerDetalleRenta(renta.getRentaId()));
            }
            return renta;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage(),e);
        }
                
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
                   abono.setAbonoId(Integer.parseInt(dtconduc[i][0].toString()) );
               if(dtconduc[i][1] != null)
                    abono.setRentaId(Integer.parseInt(dtconduc[i][1].toString()) );
               if(dtconduc[i][2] != null)
                    abono.setUsuarioId(Integer.parseInt(dtconduc[i][2].toString()) );
               if(dtconduc[i][3] != null)
                    abono.setFecha(dtconduc[i][3].toString() );
               if(dtconduc[i][4] != null)
                    abono.setAbono(Float.parseFloat(dtconduc[i][4].toString()) );
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
             estado.setEstadoId(Integer.parseInt(dtconduc[0][0]+""));
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
 private enum ColumnRenta {
     ID_RENTA(0,"r.id_renta"),
     FOLIO(1,"r.folio"),
     CUSTOMER_NAME(2,"c.nombre"),
     CUSTOMER_LAST_NAME(3,"c.apellidos"),
     STATUS_ID(4,"estado.id_estado"),
     STATUS_DESCRIPTION(5,"estado.descripcion"),
     CREATED_AT(6,"r.fecha_pedido"),
     EVENT_DATE(7,"r.fecha_evento"),
     DELIVERY_DATE(8,"r.fecha_entrega"),
     DELIVERY_HOUR(9,"r.hora_entrega"),
     EVENT_DESCRIPTION(10,"r.descripcion"),
     DRIVER_NAME(11,"chofer.nombre"),
     DRIVER_LAST_NAME(12,"chofer.apellidos"),
     EVENT_TYPE_ID(13,"tipo.id_tipo"),
     EVENT_TYPE_DESCRIPTION(14,"tipo.tipo"),
     DISCOUNT_AMOUNT(15,"r.cantidad_descuento"),
     IVA(16,"r.iva"),
     GUARANTEE_DEPOSIT(17,"r.deposito_garantia"),
     SHIPPING_RECOLECTION(18,"r.envio_recoleccion"),
     USER_NAME(19,"nombre_usuario"),
     USER_LAST_NAME(20,"apellidos_usuario");
     
     private final Integer column;
     private final String sqlColumnName;
     
     ColumnRenta (Integer column, String sqlColumnName) {
         this.column = column;
         this.sqlColumnName = sqlColumnName;
     }

    public Integer getColumn() {
        return column;
    }

    public String getSqlColumnName() {
        return sqlColumnName;
    }
     
     
 }
 
 public List<Renta> obtenerRentasPorParametros(Map<String,Object> parameters)throws Exception{
        
        List<Renta> rentas = salesDao.obtenerRentasPorParametros(parameters);
       
        
         for (Renta renta : rentas) {
             UtilityCommon.calcularTotalesPorRenta(renta);
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
                   tipo.setTipoAbonoId(Integer.parseInt(dtconduc[i][0].toString()) );
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
             articulo.setArticuloId(Integer.parseInt(dtconduc[i][0].toString()));
             
             Renta renta = new Renta();
             renta.setRentaId(Integer.parseInt(dtconduc[i][1].toString()));
             
             if(dtconduc[i][2] != null)
                renta.setFolio(Integer.parseInt(dtconduc[i][2].toString()));
             
             if(dtconduc[i][3] != null)
                articulo.setCodigo(dtconduc[i][3]+"");
               
            
             if(dtconduc[i][4] != null)
                articulo.setDescripcion(dtconduc[i][4]+"");
                detalle.setArticulo(articulo);
             
             if(dtconduc[i][5] != null) 
                 detalle.setCantidad(Float.parseFloat(dtconduc[i][5]+""));
             
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
  
  public TipoAbono obtenerTipoAbonoPorDescripcion(String descripcion){
      return salesDao.obtenerTipoAbonoPorDescripcion(descripcion);      
  }
  
  public void actualizarAbonoPorId(Abono abono){
      salesDao.actualizarAbonoPorId(abono);
  }
 
}
