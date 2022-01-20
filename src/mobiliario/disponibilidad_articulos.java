/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobiliario;

import services.SaleService;
import clases.sqlclass;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.Articulo;
import model.DetalleRenta;
import model.Renta;
import services.ItemService;

/**
 *
 * @author Carlos Alberto
 */
public class disponibilidad_articulos extends java.awt.Dialog {

    sqlclass funcion = new sqlclass();
    private final SaleService saleService;
    ItemService itemService = ItemService.getInstance();
    Object[][] dtconduc;
    String fecha_inicial, fecha_final, id_renta;
    boolean es_consultar = false, es_agregar = false;
    int cant_filas = 0;

    /**
     * Creates new form disponibilidad_articulos
     */
    public disponibilidad_articulos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        saleService = SaleService.getInstance();
        this.setLocationRelativeTo(null);

        if (consultar_renta.validar_consultar.equals("1")) {
            cant_filas = 0;
            fecha_inicial = consultar_renta.fecha_inicial.toString();
            fecha_final = consultar_renta.fecha_final.toString();
            // id_renta = consultar_renta.id_renta.toString();
            cant_filas = consultar_renta.tabla_detalle.getRowCount();
            es_consultar = true;
        } else {
            cant_filas = 0;
            fecha_inicial = agregar_renta.fecha_inicial.toString();
            fecha_final = agregar_renta.fecha_final.toString();
            //id_renta = agregar_renta.id_ultima_renta.toString();
            cant_filas = agregar_renta.tabla_detalle.getRowCount();
            es_agregar = true;
        }
        consultar_renta.validar_consultar = "0";
        agregar_renta.validar_agregar = "0";

        formato_tabla();
        formato_tabla_comparativa();
        tabla_disponibilidad();
        System.out.println("Fecha inicial: " + fecha_inicial);
        System.out.println("Fecha Final: " + fecha_final);

    }

    public String dia_semana(String fecha) {
        //String fecha1[];  
        String[] fecha1 = fecha.split("/");

        if (fecha1.length != 3) {
            return null;
        }
        //Vector para calcular día de la semana de un año regular.  
        int[] regular = {0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5};
        //Vector para calcular día de la semana de un año bisiesto.  
        int[] bisiesto = {0, 3, 4, 0, 2, 5, 0, 3, 6, 1, 4, 6};
        //Vector para hacer la traducción de resultado en día de la semana.  
        String[] semana = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        //Día especificado en la fecha recibida por parametro.  
        int d = Integer.parseInt(fecha1[0]);
        //Módulo acumulado del mes especificado en la fecha recibida por parametro.  
        int m = Integer.parseInt(fecha1[1]) - 1;
        //Año especificado por la fecha recibida por parametros.  
        int a = Integer.parseInt(fecha1[2]);
        //Comparación para saber si el año recibido es bisiesto.  
        int dia = (int) d;
        int mes = (int) m;
        int anno = (int) a;

        if ((anno % 4 == 0) && !(anno % 100 == 0 && anno % 400 != 0)) {
            mes = bisiesto[mes];
        } else {
            mes = regular[mes];
        }
        //Se retorna el resultado del calculo del día de la semana. 
        int dd = (int) Math.ceil(Math.ceil(Math.ceil((anno - 1) % 7) + Math.ceil((Math.floor((anno - 1) / 4) - Math.floor((3 * (Math.floor((anno - 1) / 100) + 1)) / 4)) % 7) + mes + dia % 7) % 7);

        String DD = semana[dd].toString();
        String MM = meses[m].toString();
        String fechafinal = DD + " " + d + " de " + MM + " " + a;
        return fechafinal;
    }

    public void tabla_disponibilidad() {
        //String fecha, fecha2;
        funcion.conectate();
        tabla_disponibilidad.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);      

        if (es_consultar == true) {
            System.out.println("Cantidad filas: " + cant_filas);

            for (int i = 0; i < cant_filas; i++) { //filas de los otros formularios

                for (int j = 0; j < dtconduc.length; j++) {
                    System.out.println("dtconduc: " + dtconduc[j][2].toString());
                    System.out.println("tabla detalle: " + consultar_renta.tabla_detalle.getValueAt(i, 1).toString());
                    if (dtconduc[j][1].equals(consultar_renta.tabla_detalle.getValueAt(i, 1).toString())) { //si articulo es igual entonces agregamos la fila a la tabla disponibilidad

                        DefaultTableModel temp = (DefaultTableModel) tabla_disponibilidad.getModel();
                        float cantidad_pedido = Float.parseFloat(dtconduc[j][7].toString());
                        float cantidad_inventario = Float.parseFloat(dtconduc[j][8].toString());
                        String descripcionEvento =  dtconduc[j][9].toString();
                        //float disponible = (cantidad_inventario - cantidad_pedido);

                        Object nuevo[] = {
                            dtconduc[j][1].toString(), 
                            String.valueOf(cantidad_pedido).toString(), 
                            String.valueOf(cantidad_inventario).toString(), 
                            dtconduc[j][6].toString(), dtconduc[j][4].toString(), 
                            dtconduc[j][5].toString(), dtconduc[j][3].toString(),
                            descripcionEvento
                        };
                        temp.addRow(nuevo);
                    }

                }
            }

        } else { //es en agregar renta
            System.out.println("Cantidad filas: " + cant_filas);
            List<Renta> rentas = saleService.obtenerDisponibilidadRenta(fecha_inicial, fecha_final, funcion);
             
            System.out.println("Agregando el pedido actual");
            // AGREGAMOS EL PEDIDO ACTUAL EN LA TABLA DE DETALLE
            for (int z = 0; z < agregar_renta.tabla_detalle.getRowCount(); z ++ ){
                Integer id = new Integer(agregar_renta.tabla_detalle.getValueAt(z,1).toString());
                Articulo availabeItem = itemService.getItemAvailable(id);
                if(availabeItem == null)
                    continue;
                System.out.println("ID AGREGADO: "+availabeItem.getArticuloId());
                DefaultTableModel temp2 = (DefaultTableModel) tabla_disponibilidad.getModel();
                Object nuevo2[] = {
                            availabeItem.getArticuloId()+"",
                            agregar_renta.tabla_detalle.getValueAt(z, 0).toString(),
                            availabeItem.getUtiles(),
                            availabeItem.getDescripcion()+" "+availabeItem.getColor().getColor(),
                            fecha_inicial,
                            fecha_final,
                            "Pedido actual",
                            agregar_renta.txt_descripcion.getText(),
                            ""
                        };
                temp2.addRow(nuevo2);
                
                
                // TAMBIEN AGREGAMOS LOS ARTICULOS EN LA TABLA UNICOS
                // id_articulo", "cantidad_pedido", "cantidad_inventario", "disponible", "articulo", "fecha_entrega", "fecha_devolucion"
                            
                DefaultTableModel tablaUnicosModel = (DefaultTableModel) tabla_comparativa.getModel();
                Object unico[] = {
                    availabeItem.getArticuloId()+"", // 0
                    agregar_renta.tabla_detalle.getValueAt(z, 0).toString(), // 1
                    availabeItem.getUtiles()+"", // 2
                    availabeItem.getUtiles()- Float.parseFloat( agregar_renta.tabla_detalle.getValueAt(z, 0).toString() ) , // 3                           
                    availabeItem.getDescripcion()+" "+availabeItem.getColor().getColor() //4
                };
                tablaUnicosModel.addRow(unico);
                
            } // fin agregar pedido actual 
            
        
        // AGREGANDO LOS ARTICULOS ENCONTRADOS POR LA FECHA INDICADA
        if(rentas == null)
            return;
        System.out.println("Agregando articulos encontrados en bd ");
        for(Renta renta : rentas){        
            for(DetalleRenta detalle : renta.getDetalleRenta()){
              for (int i = 0; i < agregar_renta.tabla_detalle.getRowCount(); i++) { 
                  // recorremos la tabla para identificar los articulos 
                  String id = detalle.getArticulo().getArticuloId()+"";
                  if (!id.equals(agregar_renta.tabla_detalle.getValueAt(i, 1).toString()))
                      continue;
                    Articulo availabeItem = itemService.getItemAvailable(detalle.getArticulo().getArticuloId());
                    // vamos agregar el articulo encontrado en la tabla detalle
                    DefaultTableModel temp = (DefaultTableModel) tabla_disponibilidad.getModel();
                     Object nuevo[] = {
                            detalle.getArticulo().getArticuloId()+"",
                            detalle.getCantidad()+"",
                            availabeItem.getUtiles(),
                            detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor(),
                            renta.getFechaEntrega(),
                            renta.getFechaDevolucion(),
                            renta.getCliente().getNombre()+" "+renta.getCliente().getApellidos(),
                            renta.getDescripcion(),
                            renta.getTipo().getTipo()         
                        };
                        temp.addRow(nuevo);
                        
                        // VAMOS AGREGAR ARTICULOS UNICOS
                        // recorremos la tabla de unicos para encontrar algun articulo y sumar y calcular la disponibilidad
                            boolean encontrado = false;
                            for(int j=0 ; j < tabla_comparativa.getRowCount() ; j++){                                
                                
                                if(tabla_comparativa.getValueAt(j, 0).toString().equals(detalle.getArticulo().getArticuloId()+"") ){
                                    // articulo encontrado :)                       
                                    float cantidadPedido = new Float(tabla_comparativa.getValueAt(j, 1).toString());
                                    
                               
                                    tabla_comparativa.setValueAt((cantidadPedido + detalle.getCantidad()), j, 1);
                                    tabla_comparativa.setValueAt((availabeItem.getUtiles() - (cantidadPedido+detalle.getCantidad())), j, 3);
                                    encontrado = true;
                                }
                            } // end for tablaArticulosUnicos, para realizar la busqueda
                                if(!encontrado){
                                    System.out.println("TABLA UNICOS, agregando articulo: "+detalle.getArticulo().getDescripcion());
                                // si no se encontro en la tabla, procedemos a agregar el articulo                                
                                    DefaultTableModel tablaUnicosModel = (DefaultTableModel) tabla_comparativa.getModel();
                                    Object unico1[] = {
                                        detalle.getArticulo().getArticuloId()+"",
                                        detalle.getCantidad()+"",
                                        availabeItem.getUtiles(),
                                        availabeItem.getUtiles() - detalle.getCantidad(), // 3                                   
                                        detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor()                                       
                                    };
                                    tablaUnicosModel.addRow(unico1);
                             } // fin if, encontrado!                                 
                           
              }               
            } // end for detalle renta
        }// end for renta  


        } // end else agregar renta
       
        
         funcion.desconecta();
    }

    public void formato_tabla() {
        Object[][] data = {{"", "", "", "", "", "", "","",""}};
        String[] columnNames = {"id_articulo", "Cantidad Pedido", "Útiles", "Articulo", "Fecha entrega","Fecha Devolución", "Cliente","Descripción evento","Tipo"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tabla_disponibilidad.setModel(TableModel);

        int[] anchos = {20, 100, 100, 180, 100, 120,120,100,100};

        for (int inn = 0; inn < tabla_disponibilidad.getColumnCount(); inn++) {
            tabla_disponibilidad.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_disponibilidad.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tabla_disponibilidad.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_disponibilidad.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_disponibilidad.getColumnModel().getColumn(0).setPreferredWidth(0);

        tabla_disponibilidad.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tabla_disponibilidad.getColumnModel().getColumn(2).setCellRenderer(centrar);

    }

    public void formato_tabla_comparativa() {
        Object[][] data = {{"", "", "", "", ""}};
        String[] columnNames = {"id_articulo", "Cantidad Pedido", "Utiles", "Disponible", "Articulo"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tabla_comparativa.setModel(TableModel);

        int[] anchos = {20, 60, 60, 60, 120};

        for (int inn = 0; inn < tabla_comparativa.getColumnCount(); inn++) {
            tabla_comparativa.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_comparativa.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tabla_comparativa.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_comparativa.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_comparativa.getColumnModel().getColumn(0).setPreferredWidth(0);

        tabla_comparativa.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tabla_comparativa.getColumnModel().getColumn(2).setCellRenderer(centrar);
        tabla_comparativa.getColumnModel().getColumn(3).setCellRenderer(centrar);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabla_comparativa = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_disponibilidad = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabla_comparativa.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tabla_comparativa.setModel(new javax.swing.table.DefaultTableModel(
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
        tabla_comparativa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabla_comparativa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_comparativaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tabla_comparativa);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 1100, 530));

        jTabbedPane1.addTab("Articulos unicos", jPanel3);

        jPanel4.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabla_disponibilidad.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tabla_disponibilidad.setModel(new javax.swing.table.DefaultTableModel(
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
        tabla_disponibilidad.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabla_disponibilidad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_disponibilidadMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabla_disponibilidad);

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 1100, 530));

        jTabbedPane1.addTab("Detalle", jPanel4);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1110, 590));
        jTabbedPane1.getAccessibleContext().setAccessibleName("Detalle de articulos");
        jTabbedPane1.getAccessibleContext().setAccessibleDescription("sss");

        add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void tabla_disponibilidadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_disponibilidadMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_tabla_disponibilidadMouseClicked

    private void tabla_comparativaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_comparativaMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tabla_comparativaMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                disponibilidad_articulos dialog = new disponibilidad_articulos(new java.awt.Frame(), true);
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tabla_comparativa;
    private javax.swing.JTable tabla_disponibilidad;
    // End of variables declaration//GEN-END:variables
}
