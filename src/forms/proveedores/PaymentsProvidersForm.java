/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms.proveedores;


import clases.sqlclass;
import exceptions.BusinessException;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import mobiliario.ApplicationConstants;
import mobiliario.iniciar_sesion;
import model.TipoAbono;
import model.providers.OrdenProveedor;
import model.providers.PagosProveedor;
import services.SaleService;
import services.SystemService;
import services.providers.OrderProviderService;
import services.providers.ProvidersPaymentsService;


/**
 *
 * @author Gerardo Torreblanca
 */
public class PaymentsProvidersForm extends javax.swing.JInternalFrame {

    
    private final SystemService systemService = SystemService.getInstance();
    private final OrderProviderService orderService = OrderProviderService.getInstance();
    private final ProvidersPaymentsService providersPaymentsService = ProvidersPaymentsService.getInstance();
    private final SaleService saleService;
    sqlclass funcion = new sqlclass(); 
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    private OrdenProveedor ordenProveedorGlobal = new OrdenProveedor();
    
    public static String g_idRenta=null;
    public static String g_idOrder=null;
    
    private static final int HEADER_ID = 0;
    private static final int HEADER_ID_ORDER_PROVIDER = 1;
    private static final int HEADER_USER = 2;
    private static final int HEADER_PAYMENT_TYPE = 3;
    private static final int HEADER_AMOUNT = 4;
    private static final int HEADER_COMMENT = 5;
    private static final int HEADER_CREATED = 6;
    private static final int HEADER_UPDATED = 7;
    
    /**
     * Creates new form clientes
     */
    public PaymentsProvidersForm() {
        funcion.conectate();
        initComponents();
        saleService = SaleService.getInstance();
        this.setTitle("Pagos al proveedor");
        tableFormat();
        fillPaymentsType();
        getPayments();
        
        
        
    }
    
    public void getPayments(){
        
        if(OrderProviderForm.g_order_provider_id != null){
            // viene de ver detalle orden
            try{
                ordenProveedorGlobal = 
                    orderService.getOrderById(OrderProviderForm.g_order_provider_id);
            }catch(BusinessException e){
                JOptionPane.showMessageDialog(null, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }else if(ViewOrdersProviders.g_idOrder != null &&
                    !ViewOrdersProviders.g_idOrder.equals("")
                ){
            
            // viene de ver ordenes en general
                    try{
                        ordenProveedorGlobal = 
                            orderService.getOrderById(new Long(ViewOrdersProviders.g_idOrder));
                    }catch(BusinessException e){
                        JOptionPane.showMessageDialog(null, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
            
        
        }else{
                JOptionPane.showMessageDialog(null,"No se recibio la orden, porfavor reinicia la ventana", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
        }
        
       
        if(ordenProveedorGlobal.getPagosProveedor() != null 
                && ordenProveedorGlobal.getPagosProveedor().size()>0
                ){
            
            DefaultTableModel modelTable = (DefaultTableModel) tablePayments.getModel();
             
            for(PagosProveedor pago : ordenProveedorGlobal.getPagosProveedor()){
                
                 Object fila[] = {                                          
                        pago.getId(),
                        ordenProveedorGlobal.getId(),
                        pago.getUsuario().getNombre() +" "+pago.getUsuario().getApellidos(),
                        pago.getTipoAbono().getDescripcion(),
                        pago.getCantidad(),
                        pago.getComentario(),
                        pago.getCreado(),
                        pago.getActualizado()
                    };
                    modelTable.addRow(fila);
            }
        }
        
        this.lblInfoGeneral.setText("Orden No. "+ordenProveedorGlobal.getId()+", FOLIO [ "+ordenProveedorGlobal.getRenta().getFolio()+" ]");
        this.total();
    
    }
    
    public void total(){
        float total = 0f;
        for (int i = 0; i < tablePayments.getRowCount(); i++) {
           total += new Float(tablePayments.getValueAt(i, HEADER_AMOUNT).toString());
        }
        
        this.lblTotal.setText("Total: "+decimalFormat.format(total));
    }
    
    public void fillPaymentsType() {

        List<TipoAbono> tiposAbonos = 
                saleService.obtenerTiposAbono(funcion);
        
        this.cmbTipoPago.removeAllItems();
        cmbTipoPago.addItem(ApplicationConstants.CMB_SELECCIONE);
        if(tiposAbonos != null && tiposAbonos.size()>0){
            for(TipoAbono tipo : tiposAbonos){               
                   cmbTipoPago.addItem(tipo.getDescripcion());
            }
        }        
    }
    
    public void resetInputs(){
        txtCantidad.setText("");
        txtComentario.setText("");
        cmbTipoPago.setSelectedIndex(0);
    }
    
    public void addPayment(){
        
        StringBuilder message = new StringBuilder();
        Float cantidad = 0f;
        try{
            cantidad = new Float(txtCantidad.getText());
        }catch(NumberFormatException e){
            message.append("Solo se permiten números\n");
        }
        
        if(this.cmbTipoPago.getSelectedItem().equals(ApplicationConstants.CMB_SELECCIONE)){
            message.append("Selecciona el tipo de pago\n");
        }
        
        if(!message.toString().equals("")){
            JOptionPane.showMessageDialog(null,message.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        PagosProveedor pagosProveedor = new PagosProveedor();
               
        TipoAbono tipoAbono = 
                saleService.obtenerTipoAbonoPorDescripcion(cmbTipoPago.getSelectedItem().toString());
        
         pagosProveedor.setTipoAbono(tipoAbono);
         pagosProveedor.setOrdenProveedor(ordenProveedorGlobal);
         pagosProveedor.setUsuario(iniciar_sesion.usuarioGlobal);
         pagosProveedor.setCantidad(cantidad);
         pagosProveedor.setComentario(txtComentario.getText());
         
         try{
            providersPaymentsService.addPayment(pagosProveedor);
            tableFormat();
            getPayments();
         }catch(BusinessException e){
            JOptionPane.showMessageDialog(null, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
         }
         
         resetInputs();
         
    }
   
   public void tableFormat() {
        Object[][] data = {{"","","","","","","",""}};
        String[] columnNames = {                        
                        "Id",
                        "Id orden proveedor", 
                        "Usuario",
                        "Tipo abono", 
                        "Cantidad",                        
                        "Comentario",
                        "Creado",
                        "Actualizado"
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        this.tablePayments.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablePayments.setRowSorter(ordenarTabla);

        int[] anchos = {20,20,80,40,40, 80,100,100};

        for (int inn = 0; inn < tablePayments.getColumnCount(); inn++) {
            tablePayments.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablePayments.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        tablePayments.getColumnModel().getColumn(HEADER_ID).setMaxWidth(0);
        tablePayments.getColumnModel().getColumn(HEADER_ID).setMinWidth(0);
        tablePayments.getColumnModel().getColumn(HEADER_ID).setPreferredWidth(0);
        
        tablePayments.getColumnModel().getColumn(HEADER_ID_ORDER_PROVIDER).setMaxWidth(0);
        tablePayments.getColumnModel().getColumn(HEADER_ID_ORDER_PROVIDER).setMinWidth(0);
        tablePayments.getColumnModel().getColumn(HEADER_ID_ORDER_PROVIDER).setPreferredWidth(0);
     
        tablePayments.getColumnModel().getColumn(HEADER_USER).setCellRenderer(centrar);
        tablePayments.getColumnModel().getColumn(HEADER_AMOUNT).setCellRenderer(centrar);
        
    }
   
   
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel9 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jMenuItem3 = new javax.swing.JMenuItem();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablePayments = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel1 = new javax.swing.JPanel();
        txtCantidad = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtComentario = new javax.swing.JTextField();
        btnAgregar = new javax.swing.JButton();
        cmbTipoPago = new javax.swing.JComboBox();
        jLabel47 = new javax.swing.JLabel();
        lblInfoGeneral = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();

        jLabel9.setText("jLabel9");

        jMenuItem3.setText("jMenuItem3");

        setClosable(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablePayments.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        tablePayments.setModel(new javax.swing.table.DefaultTableModel(
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
        tablePayments.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablePayments.setRowHeight(14);
        tablePayments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablePaymentsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablePayments);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 573, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 590, 270));

        txtCantidad.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Cantidad:");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Comentario:");

        txtComentario.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        btnAgregar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnAgregar.setText("(+) agregar");
        btnAgregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        cmbTipoPago.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbTipoPago.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTipoPago.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel47.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel47.setText("Tipo de pago:");

        lblInfoGeneral.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        lblTotal.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(txtComentario, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel47))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmbTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblInfoGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 9, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblInfoGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtComentario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 580, 180));

        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuBar1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenu2.setText("Exportar");
        jMenu2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jMenuItem4.setText("Exportar a Excel");
        jMenuItem4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablePaymentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablePaymentsMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            
        }
    }//GEN-LAST:event_tablePaymentsMouseClicked

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        systemService.exportarExcel(tablePayments);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // TODO add your handling code here:

       addPayment();
    }//GEN-LAST:event_btnAgregarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbTipoPago;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblInfoGeneral;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tablePayments;
    public static javax.swing.JTextField txtCantidad;
    public static javax.swing.JTextField txtComentario;
    // End of variables declaration//GEN-END:variables

    private void setLocationRelativeTo(Object object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
