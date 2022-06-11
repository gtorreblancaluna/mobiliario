package forms.abonos;

import common.utilities.UtilityCommon;
import common.exceptions.DataOriginException;
import common.services.UtilityService;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.Abono;
import services.AbonosService;

public class PaymentsForm extends javax.swing.JInternalFrame {

    
    private final AbonosService abonosService = AbonosService.getInstance();
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    private final UtilityService utilityService = UtilityService.getInstance();
    
    public PaymentsForm() {
        this.setClosable(true);
        initComponents();
        formatoTabla();
    }
    
    private String getFormatDate (Date date) {
        String result = null;
        if (date != null) {
            result = new SimpleDateFormat("dd/MM/yyyy").format(date);
        }
        
        return result;
    }

    private void tabla_abonos() {
        
        String createdAtInitDate = getFormatDate(cmb_fecha_inicial.getDate());
        String createdAtEndDate = getFormatDate(cmb_fecha_final.getDate());
        
        String paymentInitDate = getFormatDate(txtPaymentInitDate.getDate());
        String paymentEndDate = getFormatDate(txtPaymentEndDate.getDate());
              
        if ( (createdAtInitDate == null || createdAtEndDate == null) && (paymentInitDate == null || paymentEndDate == null)) {
            JOptionPane.showMessageDialog(this, "Ingresa un rango de fechas", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        formatoTabla();
        
        Map<String,Object> parameters = new HashMap<>();
        
        parameters.put("createdAtInitDate", createdAtInitDate);
        parameters.put("createdAtEndDate", createdAtEndDate);
        parameters.put("paymentInitDate", paymentInitDate);
        parameters.put("paymentEndDate", paymentEndDate);
        
        List<Abono> abonos;
        try {
            abonos = abonosService.getByParameters(parameters);
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (abonos.isEmpty()) {
            lblInfo.setText("No se obtubieron resultados");
        } else {
            lblInfo.setText("Total de pagos: "+abonos.size());
        }

        for(Abono abono : abonos){
              DefaultTableModel temp = (DefaultTableModel) tabla_abonos.getModel();

               Object fila[] = {
                        abono.getRenta().getRentaId()+"",
                        abono.getRenta().getFolio(),
                        abono.getRenta().getCliente().getNombre()+" "+abono.getRenta().getCliente().getApellidos(),
                        abono.getRenta().getDescripcion(),
                        abono.getFecha(),
                        abono.getUsuario().getNombre()+" "+abono.getUsuario().getApellidos(), 
                        decimalFormat.format(abono.getAbono()),
                        abono.getComentario(),
                        abono.getFechaPago(),
                        abono.getTipoAbono().getDescripcion()
                    };
                    temp.addRow(fila);
        }      
        
        total();
        
    }
    
    private void formatoTabla() {
        
        Object[][] data = {{"","", "", "", "", "","","","",""}};
        String[] columNames = {"Id","Folio", "Cliente", "Descripción pedido", "Fecha creación", "Recibio", "Pago", "Comentario","Fecha pago","Tipo"};    
        
        DefaultTableModel TableModel = new DefaultTableModel(data, columNames);
        tabla_abonos.setModel(TableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(TableModel); 
        tabla_abonos.setRowSorter(ordenarTabla);
        
        DefaultTableCellRenderer alignRight = new DefaultTableCellRenderer();
        alignRight.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

         int[] anchos = {10,20,80, 80, 100, 100, 50, 80,80,80};

        for (int inn = 0; inn < tabla_abonos.getColumnCount(); inn++)
            tabla_abonos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]); 
        
        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_abonos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }

        tabla_abonos.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_abonos.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_abonos.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        tabla_abonos.getColumnModel().getColumn(0).setCellRenderer(centrar);
        tabla_abonos.getColumnModel().getColumn(3).setCellRenderer(centrar);  
        tabla_abonos.getColumnModel().getColumn(5).setCellRenderer(centrar);   
        tabla_abonos.getColumnModel().getColumn(6).setCellRenderer(alignRight);  

    }
    
    private void total() {
        float total = 0;
        for (int i = 0; i < tabla_abonos.getRowCount(); i++) {
            total = total + Float.parseFloat(UtilityCommon.deleteCharacters(tabla_abonos.getValueAt(i, 6).toString(), "$,"));
        }
        lblTotal.setText("Pagos: $"+decimalFormat.format(total));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        cmb_fecha_final = new com.toedter.calendar.JDateChooser();
        cmb_fecha_inicial = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtPaymentInitDate = new com.toedter.calendar.JDateChooser();
        txtPaymentEndDate = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_abonos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        lblTotal = new javax.swing.JLabel();
        lblInfo = new javax.swing.JLabel();

        cmb_fecha_final.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        cmb_fecha_inicial.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        cmb_fecha_inicial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmb_fecha_inicialMouseClicked(evt);
            }
        });
        cmb_fecha_inicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmb_fecha_inicialKeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Fecha de creación");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Fecha de pago");

        txtPaymentInitDate.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txtPaymentInitDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPaymentInitDateMouseClicked(evt);
            }
        });
        txtPaymentInitDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPaymentInitDateKeyPressed(evt);
            }
        });

        txtPaymentEndDate.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jButton1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton2.setText("Exportar Excel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmb_fecha_inicial, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmb_fecha_final, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPaymentInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPaymentEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 199, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmb_fecha_inicial, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPaymentInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmb_fecha_final, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPaymentEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton1)
                                .addComponent(jButton2)))))
                .addContainerGap())
        );

        tabla_abonos.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
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
        tabla_abonos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabla_abonos.setRowHeight(14);
        tabla_abonos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_abonosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabla_abonos);

        lblTotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        lblInfo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 18, Short.MAX_VALUE)
                    .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmb_fecha_inicialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmb_fecha_inicialMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_fecha_inicialMouseClicked

    private void cmb_fecha_inicialKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmb_fecha_inicialKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_fecha_inicialKeyPressed

    private void txtPaymentInitDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPaymentInitDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPaymentInitDateMouseClicked

    private void txtPaymentInitDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPaymentInitDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPaymentInitDateKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        tabla_abonos();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
         utilityService.exportarExcel(tabla_abonos);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void tabla_abonosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_abonosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tabla_abonosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static com.toedter.calendar.JDateChooser cmb_fecha_final;
    public static com.toedter.calendar.JDateChooser cmb_fecha_inicial;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblTotal;
    public static javax.swing.JTable tabla_abonos;
    public static com.toedter.calendar.JDateChooser txtPaymentEndDate;
    public static com.toedter.calendar.JDateChooser txtPaymentInitDate;
    // End of variables declaration//GEN-END:variables
}
