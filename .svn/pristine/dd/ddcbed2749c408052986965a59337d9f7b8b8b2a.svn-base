/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobiliario;

import services.SaleService;
import clases.sqlclass;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
//import static mobiliario.inventario.funcion;
import model.DetalleRenta;
import model.Renta;
import services.SystemService;

/**
 *
 * @author Carlos Alberto
 */
public class VerDisponibilidadArticulos extends java.awt.Dialog {

    sqlclass funcion = new sqlclass();
    
    Object[][] dtconduc;
    boolean existe, editar = false;
    String id_color;
    float cant = 0; 
    SaleService saleService = new SaleService();
    private final SystemService systemService = SystemService.getInstance();
   
    
    public String conviertemoneda(String valor) {

        DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');

        float entero = Float.parseFloat(valor);
        DecimalFormat formateador = new DecimalFormat("###,###.##", simbolo);
        String entero2 = formateador.format(entero);

        if (entero2.contains(".")) {
            entero2 = "$" + entero2;

        } else {
            entero2 = "$" + entero2 + ".00";
        }

        return entero2;

    }
    public VerDisponibilidadArticulos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();    
      
        funcion.conectate();
        this.setLocationRelativeTo(null);
        this.lblEncontrados.setText("");
        this.setTitle("Ver disponibilidad de articulos ");
        formato_tabla();
        formato_tabla_unicos();
      
        if(inventario.jcheckIncluirTodos.isSelected())
            mostrarDisponibilidadTodos();
        else
            mostrarDisponibilidad();
        
        
        
    }
   
    public void mostrarDisponibilidadTodos(){       
        StringBuilder mensaje = new StringBuilder();
        String fechaInicial = new SimpleDateFormat("dd/MM/yyyy").format(inventario.txtDisponibilidadFechaInicial.getDate());
        String fechaFinal = new SimpleDateFormat("dd/MM/yyyy").format(inventario.txtDisponibilidadFechaFinal.getDate());
        
        String stringSql = null;
        
        if(inventario.radioBtnTodos.isSelected()){
        mensaje.append("Se incluyen todos los traslapes - ");
            // MOSTRAREMOS TODOS
        stringSql = "SELECT * FROM renta renta "
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
        }
        
        if(inventario.radioBtnFechaEntrega.isSelected()){
            // MOSTRAR POR FECHA DE ENTREGA
            mensaje.append("Se incluyen por fecha de entrega - ");
             stringSql = "SELECT * FROM renta renta "
                +"WHERE "
                +"STR_TO_DATE(renta.fecha_entrega,'%d/%m/%Y') "
                +"BETWEEN STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y') "
                +"AND STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y') "
                +"AND renta.id_tipo = "+ApplicationConstants.TIPO_PEDIDO+" "
                +"AND renta.id_estado IN ("+ApplicationConstants.ESTADO_APARTADO+","+ApplicationConstants.ESTADO_EN_RENTA+") "
                +"";
        }
        
        if(inventario.radioBtnFechaDevolucion.isSelected()){
            // MOSTRAR POR FECHA DE DEVOLUCION
            mensaje.append("Se incluyen por fecha de devolucion - ");
             stringSql = "SELECT * FROM renta renta "
                +"WHERE "
                +"STR_TO_DATE(renta.fecha_devolucion,'%d/%m/%Y') "
                +"BETWEEN STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y') "
                +"AND STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y') "
                +"AND renta.id_tipo = "+ApplicationConstants.TIPO_PEDIDO+" "
                +"AND renta.id_estado IN ("+ApplicationConstants.ESTADO_APARTADO+","+ApplicationConstants.ESTADO_EN_RENTA+") "
                +"";
        }
        
        List<Renta> rentas = saleService.obtenerDisponibilidadRentaPorConsulta(stringSql, funcion);
        
        if(rentas == null || rentas.size()<=0){
            lblEncontrados.setText("No se obtuvieron resultados :(");
            return;
        }
        mensaje.append("Total de folios "+rentas.size()+" - ");
          for(Renta renta : rentas){        
            for(DetalleRenta detalle : renta.getDetalleRenta()){     
                 String faltante = null;
                 String devolucion = null;
                 float fFaltante = 0f;
                 float fDevolucion = 0f;
                 
                      // vamos agregar el articulo encontrado en la tabla detalle
                    DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
                     Object nuevo[] = {
                            detalle.getArticulo().getArticuloId()+"",
                            detalle.getCantidad()+"",
                            detalle.getArticulo().getCantidad()+"",
                            detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor(),
                            renta.getFechaEvento(),
                            renta.getFechaEntrega(),
                            renta.getHoraEntrega(),
                            renta.getFechaDevolucion(),
                            renta.getHoraDevolucion(),
                            renta.getCliente().getNombre()+" "+renta.getCliente().getApellidos(),
                            renta.getFolio()+"",
                            renta.getDescripcion(),
                            renta.getTipo().getTipo(),
                            renta.getEstado().getDescripcion()
                        };
                        temp.addRow(nuevo);
                        
                        
                            faltante = funcion.GetData("cantidad", "SELECT SUM(faltante.cantidad)AS cantidad "
                                + "FROM faltantes faltante "                   
                                + "WHERE faltante.id_articulo = " + detalle.getArticulo().getArticuloId() + " "                               
                                + "AND faltante.fg_devolucion = '0' "        
                                + "AND faltante.fg_activo = '1' "
                            );
           
                            devolucion = funcion.GetData("cantidad", "SELECT SUM(faltante.cantidad)AS cantidad "
                                    + "FROM faltantes faltante "                   
                                    + "WHERE faltante.id_articulo = " + detalle.getArticulo().getArticuloId() + " "
                                    + "AND faltante.fg_devolucion = '1' "
                                    + "AND faltante.fg_faltante = '0' "
                                    + "AND faltante.fg_activo = '1' "
                            );
                            
                            if(faltante == null || faltante.equals(""))
                                        faltante = "0";
                            if(devolucion == null || devolucion.equals(""))
                                devolucion = "0";
                            
                            try {
                                fFaltante = new Float(faltante);
                                fDevolucion = new Float(devolucion);
                            }catch (NumberFormatException e) {                                
                                JOptionPane.showMessageDialog(null, "Ocurrrio un error al calcular faltantes\n"+e, "Error", JOptionPane.INFORMATION_MESSAGE);            
                                Toolkit.getDefaultToolkit().beep();
                                return;
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Ocurrrio un error al calcular faltantes\n"+e, "Error", JOptionPane.INFORMATION_MESSAGE);                        
                                Toolkit.getDefaultToolkit().beep();
                                return;                                
                            }
                       
                        
                        
                        if(this.tablaArticulosUnicos.getRowCount() == 0){                            
                            
                            // es la primer agregado, procedemos a agregar
//                              "id_articulo", "cantidad_pedido", "cantidad_inventario", "disponible", "articulo" 
                            DefaultTableModel tablaUnicosModel = (DefaultTableModel) tablaArticulosUnicos.getModel();
                            Object unico[] = {
                                detalle.getArticulo().getArticuloId()+"", // 0 id articulo
                                detalle.getCantidad()+"", // 1 cantidad pedido
                                detalle.getArticulo().getCantidad()+"", // 2 cantidad inventario
                                fFaltante+"",
                                fDevolucion+"",
                                "",
//                                ( ( detalle.getArticulo().getCantidad() - detalle.getCantidad() - fFaltante )  + fDevolucion), // 3   disponible                        
                                detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor() //4 articulo
                              
                            };                           
                            tablaUnicosModel.addRow(unico);
                        }else{
                            // recorremos la tabla para encontrar algun articulo y sumar y calcular la disponibilidad
                                                                                    
                            boolean encontrado = false;
                            for(int j=0 ; j < tablaArticulosUnicos.getRowCount() ; j++){
                                
                                if(tablaArticulosUnicos.getValueAt(j, 0).toString().equals(detalle.getArticulo().getArticuloId()+"") ){
                                                                        
                                    // articulo encontrado :)
                                    float cantidadPedido = new Float(tablaArticulosUnicos.getValueAt(j, 1).toString());
//                                    float cantidadInventario = new Float(tablaArticulosUnicos.getValueAt(j, 2).toString());
//                                    
//                                    float faltantes = new Float(tablaArticulosUnicos.getValueAt(j, 3).toString());
//                                    float devoluciones = new Float(tablaArticulosUnicos.getValueAt(j, 4).toString());
                                                                        
                                    tablaArticulosUnicos.setValueAt((cantidadPedido + detalle.getCantidad()), j, 1);
//                                    tablaArticulosUnicos.setValueAt((cantidadInventario - (cantidadPedido+detalle.getCantidad())), j, 3);
//                                    tablaArticulosUnicos.setValueAt( ((cantidadInventario - cantidadPedido + detalle.getCantidad() - faltantes)  + devoluciones), j, 5);
                                    encontrado = true;
                                }
                            } // end for tablaArticulosUnicos, para realizar la busqueda
                            
                            if(!encontrado){
                                // si no se encontro en la tabla, procedemos a agregar el articulo
                                
                                DefaultTableModel tablaUnicosModel = (DefaultTableModel) tablaArticulosUnicos.getModel();
                                Object unico1[] = {
                                    detalle.getArticulo().getArticuloId()+"",
                                    detalle.getCantidad()+"",
                                    detalle.getArticulo().getCantidad()+"",
                                    fFaltante+"",
                                    fDevolucion+"",
                                    "",
//                                    detalle.getArticulo().getCantidad() - detalle.getCantidad(),         
//                                    ( ( detalle.getArticulo().getCantidad() - detalle.getCantidad() ) - fFaltante + fDevolucion),
                                    detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor()

                                };
                                tablaUnicosModel.addRow(unico1);
                             } // fin if, encontrado!
                        }
                  } // end if, comparativa ids articulos           
              } 
          
          
          for(int j=0 ; j < tablaArticulosUnicos.getRowCount() ; j++){
                float cantidadInventario = new Float(tablaArticulosUnicos.getValueAt(j, 2).toString());
                float faltantes = new Float(tablaArticulosUnicos.getValueAt(j, 3).toString());
                float devoluciones = new Float(tablaArticulosUnicos.getValueAt(j, 4).toString());
                float pedido = new Float(tablaArticulosUnicos.getValueAt(j, 1).toString());
                tablaArticulosUnicos.setValueAt( ( cantidadInventario - pedido - faltantes + devoluciones ), j, 5);
                
           }
          
           if(inventario.check_solo_negativos.isSelected()) {
                mensaje.append("Mostrando solo los negativos - ");
                mostrarSoloNegativosTablaUnicos();
           }
               
           this.lblEncontrados.setText(mensaje.toString());
              
         
    } // en funcion mostrarDisponibilidadTodos
    
    public void mostrarSoloNegativosTablaUnicos(){
        
         for(int j=tablaArticulosUnicos.getRowCount() - 1 ; j >=0 ; j--){
             float disponible = new Float(tablaArticulosUnicos.getValueAt(j, 5).toString());
             if(disponible >= 0){
                 DefaultTableModel temp = (DefaultTableModel) tablaArticulosUnicos.getModel();
                 temp.removeRow(j);
             }
         }
    
    }

    public void mostrarDisponibilidad(){       
        String fechaInicial = new SimpleDateFormat("dd/MM/yyyy").format(inventario.txtDisponibilidadFechaInicial.getDate());
        String fechaFinal = new SimpleDateFormat("dd/MM/yyyy").format(inventario.txtDisponibilidadFechaFinal.getDate());
        StringBuilder mensaje = new StringBuilder();
        
        String stringSql = null;
        
        if(inventario.radioBtnTodos.isSelected()){
            // MOSTRAREMOS TODOS
             mensaje.append("Se incluyen todos los traslapes - ");
        stringSql = "SELECT * FROM renta renta "
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
        }
        
        if(inventario.radioBtnFechaEntrega.isSelected()){
            // MOSTRAR POR FECHA DE ENTREGA
             mensaje.append("Se incluyen por fecha de entrega - ");
             stringSql = "SELECT * FROM renta renta "
                +"WHERE "
                +"STR_TO_DATE(renta.fecha_entrega,'%d/%m/%Y') "
                +"BETWEEN STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y') "
                +"AND STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y') "
                +"AND renta.id_tipo = "+ApplicationConstants.TIPO_PEDIDO+" "
                +"AND renta.id_estado IN ("+ApplicationConstants.ESTADO_APARTADO+","+ApplicationConstants.ESTADO_EN_RENTA+") "
                +"";
        }
        
        if(inventario.radioBtnFechaDevolucion.isSelected()){
            // MOSTRAR POR FECHA DE DEVOLUCION
             mensaje.append("Se incluyen por fecha devolucion - ");
             stringSql = "SELECT * FROM renta renta "
                +"WHERE "
                +"STR_TO_DATE(renta.fecha_devolucion,'%d/%m/%Y') "
                +"BETWEEN STR_TO_DATE('"+fechaInicial+"','%d/%m/%Y') "
                +"AND STR_TO_DATE('"+fechaFinal+"','%d/%m/%Y') "
                +"AND renta.id_tipo = "+ApplicationConstants.TIPO_PEDIDO+" "
                +"AND renta.id_estado IN ("+ApplicationConstants.ESTADO_APARTADO+","+ApplicationConstants.ESTADO_EN_RENTA+") "
                +"";
        }
        
        List<Renta> rentas = saleService.obtenerDisponibilidadRentaPorConsulta(stringSql, funcion);
        
        
        if(rentas == null || rentas.size()<=0){
            lblEncontrados.setText("No se obtuvieron resultados :(");
            return;
        }
        
         mensaje.append("Total de folios "+rentas.size()+" - ");
          for(Renta renta : rentas){        
            for(DetalleRenta detalle : renta.getDetalleRenta()){
                
                String faltante = null;
                 String devolucion = null;
                 float fFaltante = 0f;
                 float fDevolucion = 0f;
                 
              for (int i = 0; i < inventario.tablaDisponibilidadArticulos.getRowCount(); i++) { 
                  // recorremos la tabla para identificar los articulos 
                  String id = detalle.getArticulo().getArticuloId()+"";
                  if (id.equals(inventario.tablaDisponibilidadArticulos.getValueAt(i, 0).toString())) {
                      // vamos agregar el articulo encontrado en la tabla detalle
                    DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
                     Object nuevo[] = {
                         
                          detalle.getArticulo().getArticuloId()+"",
                            detalle.getCantidad()+"",
                            detalle.getArticulo().getCantidad()+"",
                            detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor(),
                            renta.getFechaEvento(),
                            renta.getFechaEntrega(),
                            renta.getHoraEntrega(),
                            renta.getFechaDevolucion(),
                            renta.getHoraDevolucion(),
                            renta.getCliente().getNombre()+" "+renta.getCliente().getApellidos(),
                            renta.getFolio()+"",
                            renta.getDescripcion(),
                            renta.getTipo().getTipo(),
                            renta.getEstado().getDescripcion()
                        };
                        temp.addRow(nuevo);
                        
                        faltante = funcion.GetData("cantidad", "SELECT SUM(faltante.cantidad)AS cantidad "
                                + "FROM faltantes faltante "                   
                                + "WHERE faltante.id_articulo = " + detalle.getArticulo().getArticuloId() + " "                               
                                + "AND faltante.fg_devolucion = '0' "        
                                + "AND faltante.fg_activo = '1' "
                            );
           
                            devolucion = funcion.GetData("cantidad", "SELECT SUM(faltante.cantidad)AS cantidad "
                                    + "FROM faltantes faltante "                   
                                    + "WHERE faltante.id_articulo = " + detalle.getArticulo().getArticuloId() + " "
                                    + "AND faltante.fg_devolucion = '1' "
                                    + "AND faltante.fg_faltante = '0' "
                                    + "AND faltante.fg_activo = '1' "
                            );
                            
                            if(faltante == null || faltante.equals(""))
                                        faltante = "0";
                            if(devolucion == null || devolucion.equals(""))
                                devolucion = "0";
                            
                            try {
                                fFaltante = new Float(faltante);
                                fDevolucion = new Float(devolucion);
                            }catch (NumberFormatException e) {                                
                                JOptionPane.showMessageDialog(null, "Ocurrrio un error al calcular faltantes\n"+e, "Error", JOptionPane.INFORMATION_MESSAGE);            
                                Toolkit.getDefaultToolkit().beep();
                                return;
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Ocurrrio un error al calcular faltantes\n"+e, "Error", JOptionPane.INFORMATION_MESSAGE);                        
                                Toolkit.getDefaultToolkit().beep();
                                return;                                
                            }
                        
                        
                        
                        if(this.tablaArticulosUnicos.getRowCount() == 0){
                            // es la primer agregado, procedemos a agregar
//                              "id_articulo", "cantidad_pedido", "cantidad_inventario", "disponible", "articulo" 
                            DefaultTableModel tablaUnicosModel = (DefaultTableModel) tablaArticulosUnicos.getModel();
                            Object unico[] = {
                                detalle.getArticulo().getArticuloId()+"", // 0
                                detalle.getCantidad()+"", // 1
                                detalle.getArticulo().getCantidad()+"", // 2
                                fFaltante,
                                fDevolucion,   
                                "",
//                                detalle.getArticulo().getCantidad() - detalle.getCantidad(), // 3 
//                                ( (detalle.getArticulo().getCantidad() - detalle.getCantidad() - fFaltante) + fDevolucion ), // 5
                                detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor() //4
                              
                            };
                            tablaUnicosModel.addRow(unico);
                        }else{
                            // recorremos la tabla para encontrar algun articulo y sumar y calcular la disponibilidad
                            boolean encontrado = false;
                            for(int j=0 ; j < tablaArticulosUnicos.getRowCount() ; j++){
                                
                                if(tablaArticulosUnicos.getValueAt(j, 0).toString().equals(detalle.getArticulo().getArticuloId()+"") ){
                                    // articulo encontrado :)
                                    float cantidadPedido = new Float(tablaArticulosUnicos.getValueAt(j, 1).toString());
//                                    float cantidadInventario = new Float(tablaArticulosUnicos.getValueAt(j, 2).toString());
                                    
//                                    float faltantes = new Float(tablaArticulosUnicos.getValueAt(j, 3).toString());
//                                    float devoluciones = new Float(tablaArticulosUnicos.getValueAt(j, 4).toString());
                                    
                                    tablaArticulosUnicos.setValueAt((cantidadPedido + detalle.getCantidad()), j, 1);
//                                    tablaArticulosUnicos.setValueAt((cantidadInventario - (cantidadPedido+detalle.getCantidad())), j, 3);
//                                    tablaArticulosUnicos.setValueAt( ( (cantidadInventario - cantidadPedido + detalle.getCantidad() - faltantes) + devoluciones), j, 5);
                                    encontrado = true;
                                }
                            } // end for tablaArticulosUnicos, para realizar la busqueda
                            
                            if(!encontrado){
                                // si no se encontro en la tabla, procedemos a agregar el articulo
                                
                                DefaultTableModel tablaUnicosModel = (DefaultTableModel) tablaArticulosUnicos.getModel();
                                Object unico1[] = {
                                    detalle.getArticulo().getArticuloId()+"",
                                    detalle.getCantidad()+"",
                                    detalle.getArticulo().getCantidad()+"",
                                    fFaltante,
                                    fDevolucion, 
                                    "",
//                                    ( (detalle.getArticulo().getCantidad() - detalle.getCantidad() - fFaltante) + fDevolucion ), // 5
//                                    detalle.getArticulo().getCantidad() - detalle.getCantidad(), // 3                                   
                                    detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor()

                                };
                                tablaUnicosModel.addRow(unico1);
                             } // fin if, encontrado!
                        }
                  } // end if, comparativa ids articulos
              }               
            } // end for detalle renta
        }    // en for renta  
          
           
          // calculando la disponibilidad
          for(int j=0 ; j < tablaArticulosUnicos.getRowCount() ; j++){
                float cantidadInventario = new Float(tablaArticulosUnicos.getValueAt(j, 2).toString());
                float faltantes = new Float(tablaArticulosUnicos.getValueAt(j, 3).toString());
                float devoluciones = new Float(tablaArticulosUnicos.getValueAt(j, 4).toString());
                float pedido = new Float(tablaArticulosUnicos.getValueAt(j, 1).toString());
                tablaArticulosUnicos.setValueAt( ( cantidadInventario - pedido - faltantes + devoluciones ), j, 5);
                
           }
          
          if(inventario.check_solo_negativos.isSelected()) {
                mensaje.append("Mostrando solo los negativos - ");
                mostrarSoloNegativosTablaUnicos();
           }
          
            this.lblEncontrados.setText(mensaje.toString());
          
    }// en funcion mostrarDisponibilidad
     public void formato_tabla() {
        Object[][] data = {{"","","", "", "", "", "", "", "","","","",""}};
        String[] columnNames = {"id_articulo", "cantidad pedido", "cantidad inventario", "articulo", "fecha evento","fecha entrega","hora entrega", "fecha_devolucion","hora devoluci\u00F3n" ,"cliente","folio","descripci\u00F3n evento","tipo","estado"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaArticulos.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablaArticulos.setRowSorter(ordenarTabla);

        int[] anchos = {20, 80, 80, 160,100, 100, 100, 80,240,80,80,80,80,80};

        for (int inn = 0; inn < tablaArticulos.getColumnCount(); inn++) {
            tablaArticulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tablaArticulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setPreferredWidth(0);

        tablaArticulos.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tablaArticulos.getColumnModel().getColumn(2).setCellRenderer(centrar);

    }
     
     public void formato_tabla_unicos() {
        Object[][] data = {{"", "", "", "", "","",""}};
        String[] columnNames = {"id_articulo", "cantidad_pedido", "cantidad_inventario","faltantes","devoluci\u00F3n","disponible", "articulo"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaArticulosUnicos.setModel(tableModel);
        
        // Instanciamos el TableRowSorter y lo añadimos al JTable
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablaArticulosUnicos.setRowSorter(ordenarTabla);

        int[] anchos = {20, 60, 60, 60,40,40, 120};

        for (int inn = 0; inn < tablaArticulosUnicos.getColumnCount(); inn++) {
            tablaArticulosUnicos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaArticulosUnicos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tablaArticulosUnicos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaArticulosUnicos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaArticulosUnicos.getColumnModel().getColumn(0).setPreferredWidth(0);

        tablaArticulosUnicos.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tablaArticulosUnicos.getColumnModel().getColumn(2).setCellRenderer(centrar);
        tablaArticulosUnicos.getColumnModel().getColumn(3).setCellRenderer(centrar);
        tablaArticulosUnicos.getColumnModel().getColumn(4).setCellRenderer(centrar);
        tablaArticulosUnicos.getColumnModel().getColumn(5).setCellRenderer(centrar);

    }
    
    public void agregar() {
        
    }

    public void guardar() {
      
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        lblEncontrados = new javax.swing.JLabel();
        jtbPaneVerDetalle = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaArticulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaArticulosUnicos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jToolBar1 = new javax.swing.JToolBar();
        jbtnExportarDetalle = new javax.swing.JButton();
        jbtnExportarUnicos = new javax.swing.JButton();

        setLocationRelativeTo(lblEncontrados);
        setTitle("Colores");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblEncontrados.setText("jLabel1");
        jPanel2.add(lblEncontrados, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 590, 530, -1));

        tablaArticulos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tablaArticulos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaArticulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaArticulosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaArticulos);

        jtbPaneVerDetalle.addTab("Detalle", jScrollPane1);

        tablaArticulosUnicos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tablaArticulosUnicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaArticulosUnicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaArticulosUnicosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaArticulosUnicos);

        jtbPaneVerDetalle.addTab("Unicos", jScrollPane2);

        jPanel2.add(jtbPaneVerDetalle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1170, 560));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jbtnExportarDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/excel-icon.png"))); // NOI18N
        jbtnExportarDetalle.setToolTipText("Exportar tabla detalle a Excel");
        jbtnExportarDetalle.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnExportarDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExportarDetalleActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtnExportarDetalle);

        jbtnExportarUnicos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/excel-icon.png"))); // NOI18N
        jbtnExportarUnicos.setToolTipText("Exportar tabla unicos a Excel");
        jbtnExportarUnicos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnExportarUnicos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExportarUnicosActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtnExportarUnicos);

        jPanel2.add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 590, 120, 40));

        add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
//        inventario.validar_colores = true;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void tablaArticulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaArticulosMouseClicked
        
    }//GEN-LAST:event_tablaArticulosMouseClicked

    private void tablaArticulosUnicosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaArticulosUnicosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaArticulosUnicosMouseClicked

    private void jbtnExportarDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportarDetalleActionPerformed
        // TODO add your handling code here:
        systemService.exportarExcel(tablaArticulos);
    }//GEN-LAST:event_jbtnExportarDetalleActionPerformed

    private void jbtnExportarUnicosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportarUnicosActionPerformed
        // TODO add your handling code here:
        systemService.exportarExcel(tablaArticulosUnicos);
    }//GEN-LAST:event_jbtnExportarUnicosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VerDisponibilidadArticulos dialog = new VerDisponibilidadArticulos(new java.awt.Frame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbtnExportarDetalle;
    private javax.swing.JButton jbtnExportarUnicos;
    private javax.swing.JTabbedPane jtbPaneVerDetalle;
    private javax.swing.JLabel lblEncontrados;
    private javax.swing.JTable tablaArticulos;
    private javax.swing.JTable tablaArticulosUnicos;
    // End of variables declaration//GEN-END:variables
}
