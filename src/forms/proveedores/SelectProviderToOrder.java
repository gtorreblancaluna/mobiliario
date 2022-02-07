package forms.proveedores;
import exceptions.BusinessException;
import forms.material.inventory.MaterialSaleItemsView;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.providers.Proveedor;
import services.providers.ProvidersService;

public class SelectProviderToOrder extends javax.swing.JDialog {

    private String INVOKED_FROM = "";
    
    private final ProvidersService providersService = ProvidersService.getInstance();
    
    public SelectProviderToOrder(java.awt.Frame parent, boolean modal, String invokedFrom) {
        super(parent, modal);
        initComponents();
        this.setTitle("Seleccionar proveedor");
        INVOKED_FROM = invokedFrom;
        fillTable();
        txtSearchProvider.requestFocus();
    }
    
    public void fillTableSearch(String data){
        List<Proveedor> proveedores;
        try{
         proveedores
                = providersService.searchByData(data);
        }catch(BusinessException e){
                JOptionPane.showMessageDialog(null, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
          }
        this.formatTable();
        if(proveedores == null || proveedores.size()<=0){
            return;
        }
       
        
        DefaultTableModel tabla = (DefaultTableModel) tableProviders.getModel();
        for(Proveedor proveedor : proveedores){
            String fila[] = {
                proveedor.getId()+"",
                proveedor.getNombre(),
                proveedor.getApellidos(),
                proveedor.getDireccion(),
                proveedor.getTelefonos()
            };         
            tabla.addRow(fila);
        } // end for
    }
    
        
    public void fillTable(){
        this.formatTable();
        List<Proveedor> proveedores;
                try{
                    proveedores= providersService.getAll();
                }catch(BusinessException e){
                    JOptionPane.showMessageDialog(null, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
        if(proveedores == null || proveedores.size()<=0){
            return;
        }
                
        DefaultTableModel tabla = (DefaultTableModel) tableProviders.getModel();
        for(Proveedor proveedor : proveedores){
            String fila[] = {
                proveedor.getId()+"",
                proveedor.getNombre(),
                proveedor.getApellidos(),
                proveedor.getDireccion(),
                proveedor.getTelefonos()
            };         
            tabla.addRow(fila);
        } // end for
    
    }
    
     public void formatTable() {
        Object[][] data = {{"","","","","",}};
        String[] columnNames = {
                        "id",
                        "Nombre", 
                        "Apellidos",
                        "Dirección", 
                        "Teléfonos"                     
                        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tableProviders.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tableProviders.setRowSorter(ordenarTabla);

        int[] anchos = {20,20,40,140,140};

        for (int inn = 0; inn < tableProviders.getColumnCount(); inn++) {
            tableProviders.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tableProviders.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tableProviders.getColumnModel().getColumn(0).setMaxWidth(0);
        tableProviders.getColumnModel().getColumn(0).setMinWidth(0);
        tableProviders.getColumnModel().getColumn(0).setPreferredWidth(0);
      
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableProviders = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        txtSearchProvider = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tableProviders.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tableProviders.setModel(new javax.swing.table.DefaultTableModel(
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
        tableProviders.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tableProviders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableProvidersMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableProviders);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtSearchProvider.setToolTipText("Buscar proveedor");
        txtSearchProvider.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchProviderKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchProviderKeyReleased(evt);
            }
        });

        jLabel1.setText("Selecciona proveedor");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtSearchProvider)
                        .addGap(108, 108, 108))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtSearchProvider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableProvidersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableProvidersMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

           String id = tableProviders.getValueAt(this.tableProviders.getSelectedRow(), 0).toString();
           String name = tableProviders.getValueAt(this.tableProviders.getSelectedRow(), 1).toString();
           String lastName = tableProviders.getValueAt(this.tableProviders.getSelectedRow(), 2).toString();
           
           switch (INVOKED_FROM) {
               case "MATERIAL_SALE_ITEMS":
                   MaterialSaleItemsView.gProviderId = id;
                   MaterialSaleItemsView.txtProvider.setText(name + " " + lastName);
                   MaterialSaleItemsView.txtAmount.requestFocus();
                   break;
               case "ORDER_PROVIDER":
                    OrderProviderForm.g_provider_id = Long.parseLong(id);
                    OrderProviderForm.txtProviderName.setText(name + " " + lastName);
                   break;
           }
          
           this.dispose();

        }
    }//GEN-LAST:event_tableProvidersMouseClicked

    private void txtSearchProviderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchProviderKeyPressed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_txtSearchProviderKeyPressed

    private void txtSearchProviderKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchProviderKeyReleased
        // TODO add your handling code here:
        
        String data = this.txtSearchProvider.getText();
        System.out.println("Data keypressed: "+data);
       
            this.fillTableSearch(data);
        
    }//GEN-LAST:event_txtSearchProviderKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SelectProviderToOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SelectProviderToOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SelectProviderToOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SelectProviderToOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SelectProviderToOrder dialog = new SelectProviderToOrder(new javax.swing.JFrame(), true, "");
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tableProviders;
    private javax.swing.JTextField txtSearchProvider;
    // End of variables declaration//GEN-END:variables
}
