/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobiliario;

import clases.sqlclass;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.Abono;
import model.Renta;
import services.SaleService;
import services.SystemService;

/**
 *
 * @author Carlos Alberto
 */
public class abonos extends java.awt.Dialog {

    sqlclass funcion = new sqlclass();
    sqlclass general = new sqlclass();
    Object[][] dtconduc;
    Object[] datos_combo;
    private final SaleService saleService;
    private final SystemService systemService = SystemService.getInstance();

    /**
     * Creates new form abonos
     */
    public abonos(java.awt.Frame parent, boolean modal) {        
        super(parent, modal);
         initComponents();
         saleService = SaleService.getInstance();
        funcion.conectate();
        formato_tabla();       
        tabla_abonos();
        total();
        this.setLocationRelativeTo(null);
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

    public String EliminaCaracteres(String s_cadena, String s_caracteres) {
        String nueva_cadena = "";
        Character caracter = null;
        boolean valido = true;

        /* Va recorriendo la cadena s_cadena y copia a la cadena que va a regresar,
         sólo los caracteres que no estén en la cadena s_caracteres */
        for (int i = 0; i < s_cadena.length(); i++) {
            valido = true;
            for (int j = 0; j < s_caracteres.length(); j++) {
                caracter = s_caracteres.charAt(j);

                if (s_cadena.charAt(i) == caracter) {
                    valido = false;
                    break;
                }
            }
            if (valido) {
                nueva_cadena += s_cadena.charAt(i);
            }
        }

        return nueva_cadena;
    }

    public void total() {
        float total = 0;
        for (int i = 0; i < tabla_abonos.getRowCount(); i++) {
            total = total + Float.parseFloat(EliminaCaracteres(tabla_abonos.getValueAt(i, 5).toString(), "$,"));
        }
        txt_totales.setValue(total);
    }

    public void formato_tabla() {
        Object[][] data = {{"", "", "", "", "","","","",""}};
        String[] columNames = {"Folio", "Cliente", "Descripción pedido", "Fecha pago", "Recibio", "Pago", "Comentario","Fecha pago","Tipo"};    
        DefaultTableModel TableModel = new DefaultTableModel(data, columNames);
        tabla_abonos.setModel(TableModel);
        
         TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(TableModel); 
        tabla_abonos.setRowSorter(ordenarTabla);

         int[] anchos = {20,80, 80, 100, 100, 50, 80,80,80};

        for (int inn = 0; inn < tabla_abonos.getColumnCount(); inn++)
            tabla_abonos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);        

        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_abonos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        
        tabla_abonos.getColumnModel().getColumn(0).setCellRenderer(centrar);
        tabla_abonos.getColumnModel().getColumn(2).setCellRenderer(centrar);
        tabla_abonos.getColumnModel().getColumn(3).setCellRenderer(centrar);  
        tabla_abonos.getColumnModel().getColumn(5).setCellRenderer(centrar);   

    }
    

    public void tabla_abonos() {   // funcion para llenar al abrir la ventana  
//        List<Renta> rentas = saleService.obtenerPedidosPorConsultaSql(consultar_abonos.SQL, funcion);
        List<Renta> rentas = saleService.obtenerAbonos(consultar_abonos.SQL, funcion);
        if(rentas == null || rentas.size() <= 0){
            JOptionPane.showMessageDialog(null, "No hay elementos a mostrar ", "Error", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            return;
        }
        for(Renta renta : rentas){ 
            for(Abono abono : renta.getAbonos()){
                  DefaultTableModel temp = (DefaultTableModel) tabla_abonos.getModel();
                  
                   Object fila[] = {
                            renta.getRentaId()+"",
                            renta.getCliente().getNombre()+" "+renta.getCliente().getApellidos(),
                            renta.getDescripcion(),
                            abono.getFecha(),
                            renta.getUsuario().getNombre()+" "+renta.getUsuario().getApellidos(), 
                            abono.getAbono()+"",
                            abono.getComentario(),
                            abono.getFechaPago(),
                            abono.getTipoAbono().getDescripcion()
                        };
                        temp.addRow(fila);
            }        
        }
        
        /*
        funcion.conectate();
        tabla_abonos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id ", "Usuario", "Fecha", "Abono", "Comentario", "Descripcion", "Cliente"};
        String[] colName = {"id_abonos", "usuario", "fecha", "abono", "comentario", "descripcion", "cliente"};
        //nombre de columnas, tabla, instruccion sql        

        dtconduc = funcion.GetTabla(colName, "abonos", consultar_abonos.SQL);

        int filas = dtconduc.length;
        String fecha, fecha2;

        for (int i = 0; i < filas; i++) {
            fecha = dtconduc[i][2].toString();
            System.out.println("fecha" + " " + fecha);
            fecha2 = dia_semana(fecha);
            dtconduc[i][2] = fecha2;
        }
        for (int i = 0; i < dtconduc.length; i++) {
            String valor = dtconduc[i][3].toString();
            dtconduc[i][3] = conviertemoneda(valor).toString();

        }

        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        tabla_abonos.setModel(datos);

        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        int[] anchos = {10, 120, 250, 120, 250, 200, 250};

        for (int inn = 0; inn < tabla_abonos.getColumnCount(); inn++) {
            tabla_abonos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        tabla_abonos.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_abonos.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_abonos.getColumnModel().getColumn(0).setPreferredWidth(0);

        //tabla_abonos.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tabla_abonos.getColumnModel().getColumn(2).setCellRenderer(centrar);
        tabla_abonos.getColumnModel().getColumn(3).setCellRenderer(centrar);      
        funcion.desconecta();
        */

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_abonos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        txt_totales = new javax.swing.JFormattedTextField();
        jtbnExportarExcel = new javax.swing.JButton();

        setTitle("ABONOS");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        tabla_abonos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tabla_abonos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tabla_abonos);

        txt_totales.setEditable(false);
        txt_totales.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_totales.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_totales.setToolTipText("Muestra el total de abonos");
        txt_totales.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 14)); // NOI18N

        jtbnExportarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/excel-icon.png"))); // NOI18N
        jtbnExportarExcel.setToolTipText("Exportar a Excel");
        jtbnExportarExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtbnExportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbnExportarExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jtbnExportarExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txt_totales, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1242, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_totales, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jtbnExportarExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

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

    private void jtbnExportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnExportarExcelActionPerformed
        systemService.exportarExcel(tabla_abonos);
        
    }//GEN-LAST:event_jtbnExportarExcelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                abonos dialog = new abonos(new java.awt.Frame(), true);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jtbnExportarExcel;
    private javax.swing.JTable tabla_abonos;
    private javax.swing.JFormattedTextField txt_totales;
    // End of variables declaration//GEN-END:variables
}
