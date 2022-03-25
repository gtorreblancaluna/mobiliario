package forms.material.inventory;

import forms.proveedores.SelectProviderToOrder;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import mobiliario.ApplicationConstants;
import model.Articulo;
import model.material.inventory.MaterialInventory;
import model.material.inventory.MaterialSaleItem;
import model.providers.Proveedor;
import org.apache.log4j.Priority;
import services.ItemService;
import services.SystemService;
import services.material.inventory.MaterialInventoryService;

public class MaterialSaleItemsView extends javax.swing.JInternalFrame {

    private final MaterialInventoryService materialInventoryService;
    private final ItemService itemService;
    private final SystemService systemService;
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(MaterialSaleItemsView.class.getName());
    private Articulo gItem = null;
    public static String gMaterialId = "";
    public static String gProviderId = "";
    
    /**
     * @param itemId .> item id, to add material inventory
     */
    public MaterialSaleItemsView(String itemId) {
        initComponents();
        this.setTitle("Material para construcción de articulo");
        this.setClosable(true);
        itemService = ItemService.getInstance();
        materialInventoryService = MaterialInventoryService.getInstance();
        systemService = SystemService.getInstance();
        init(itemId);
    }
    
    private void delete () {
        if (table.getSelectedRow() != -1) {
            
            String id = table.getValueAt(table.getSelectedRow(), 0).toString();
            try {
                materialInventoryService.delete(new MaterialSaleItem(Long.parseLong(id)));
                getItems();
            } catch (Exception e) {
                printLog(e);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una fila", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void save () {
        try {
            
            String amount = txtAmount.getText();
            String errorMesage = "";
            
            if (amount == null || amount.isEmpty()) {
                errorMesage += "Cantidad es requerido\n";
            }
            if (gMaterialId.isEmpty()) {
                errorMesage += "Elige un material\n";
            }
            if (gProviderId.isEmpty()) {
                errorMesage += "Elige un proveedor\n";
            }
            if (gItem == null ) {
                errorMesage += "No se obtuvo el articulo, cierra y abre de nuevo la ventana\n";
            }
            
            if (!errorMesage.isEmpty()) {
                JOptionPane.showMessageDialog(this, errorMesage, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            MaterialSaleItem materialSaleItem = new MaterialSaleItem();
            materialSaleItem.setMaterialInventory(new MaterialInventory(Long.parseLong(gMaterialId)));
            materialSaleItem.setProvider(new Proveedor(Long.parseLong(gProviderId)));
            materialSaleItem.setItem(gItem);
            materialSaleItem.setAmount(new Float(amount));
            materialInventoryService.save(materialSaleItem);
            getItems();
            txtAmount.setText("");
            txtAmount.requestFocus();
            
        } catch (Exception e) {
            printLog(e);       
        } 
        
    }
    
    private void printLog (Exception e) {
        LOGGER.log(Priority.ERROR,e);
        JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_UNEXPECTED_ERROR + "\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void init (String itemId) {
        
        txtMaterial.setEnabled(false);
        txtMeasurementUnit.setEnabled(false);
        txtProvider.setEnabled(false);
       
        try {
            gItem = itemService.obtenerArticuloPorId(Integer.parseInt(itemId));
            getItems();
        } catch (Exception e) {
            printLog(e);
            return;
        }
        
        lblItem.setText("ARTICULO: "+ gItem.getDescripcion().toUpperCase() + " " + gItem.getColor().getColor().toUpperCase());
        
    }
    private void getItems () {
        formatTable();
        List<MaterialSaleItem> list;
        
        try {
            list = materialInventoryService.getMaterialSaleItemsByItemId(Long.parseLong(gItem.getArticuloId()+""));
        } catch (Exception e) {
            printLog(e);
            return;
        }
        
        for (MaterialSaleItem item : list) {
            DefaultTableModel temp = (DefaultTableModel) table.getModel();
            Object row[] = {
                item.getId(),
                item.getAmount(),
                item.getMaterialInventory().getDescription(),
                item.getMaterialInventory().getMeasurementUnit().getDescription(),
                item.getProvider().getNombre() + " " + item.getProvider().getApellidos(),
                item.getCreatedAt(),
                item.getUpdatedAt()
            };
            temp.addRow(row);
        }
        
    }
    
    private void formatTable () {
        Object[][] data = {{"", "", "","","","",""}};
        String[] columnNames = {"id", "Cantidad","Material","U. Medida","Proveedor","Creado","Actualizado"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        table.setModel(TableModel);

        int[] anchos = {20,60,300,60,120,80,80};

        for (int inn = 0; inn < table.getColumnCount(); inn++) {
            table.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) table.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }

        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
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
        txtAmount = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnProviders = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtMaterial = new javax.swing.JTextField();
        btnMaterial = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtMeasurementUnit = new javax.swing.JTextField();
        lblItem = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtProvider = new javax.swing.JTextField();
        btnExportExcel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        txtAmount.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAmountKeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Cantidad:");

        btnProviders.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnProviders.setText("Proveedores");
        btnProviders.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnProviders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProvidersActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnDelete.setText("Eliminar");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnAdd.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnAdd.setText("Agregar");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Material:");

        txtMaterial.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        btnMaterial.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnMaterial.setText("Materiales");
        btnMaterial.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaterialActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Unidad de medida:");

        txtMeasurementUnit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        lblItem.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Proveedor:");

        txtProvider.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        btnExportExcel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnExportExcel.setText("Exportar Excel");
        btnExportExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(7, 7, 7)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txtMaterial)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMeasurementUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtProvider, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)))
                            .addComponent(lblItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(9, 9, 9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnMaterial)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnProviders)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExportExcel)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(lblItem, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(7, 7, 7)
                            .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(7, 7, 7)
                            .addComponent(txtMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtProvider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMeasurementUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnDelete)
                    .addComponent(btnProviders)
                    .addComponent(btnMaterial)
                    .addComponent(btnExportExcel))
                .addContainerGap())
        );

        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        table.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(table);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(12, 12, 12)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(356, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(118, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnProvidersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProvidersActionPerformed
        
        SelectProviderToOrder win = new SelectProviderToOrder(null, true, "MATERIAL_SALE_ITEMS");
        win.setLocationRelativeTo(this);
        win.setVisible(true);
        
    }//GEN-LAST:event_btnProvidersActionPerformed

    private void btnMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaterialActionPerformed
        SelectMaterialInventoryDialogForm win = new SelectMaterialInventoryDialogForm(null, true);
        win.setLocationRelativeTo(this);
        win.setVisible(true);
    }//GEN-LAST:event_btnMaterialActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        save();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnExportExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportExcelActionPerformed
        systemService.exportarExcel(table);
    }//GEN-LAST:event_btnExportExcelActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void txtAmountKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAmountKeyPressed
        if (evt.getKeyCode() == 10) {
            save();
        }
    }//GEN-LAST:event_txtAmountKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExportExcel;
    private javax.swing.JButton btnMaterial;
    private javax.swing.JButton btnProviders;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblItem;
    private javax.swing.JTable table;
    public static javax.swing.JTextField txtAmount;
    public static javax.swing.JTextField txtMaterial;
    public static javax.swing.JTextField txtMeasurementUnit;
    public static javax.swing.JTextField txtProvider;
    // End of variables declaration//GEN-END:variables
}
