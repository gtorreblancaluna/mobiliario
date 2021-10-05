
package forms.material.inventory;

import exceptions.BusinessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import mobiliario.ApplicationConstants;
import model.material.inventory.MaterialArea;
import model.material.inventory.MaterialInventory;
import model.material.inventory.MeasurementUnit;
import org.apache.log4j.Priority;
import services.SystemService;
import services.material.inventory.MaterialInventoryService;


public class MaterialInventoryView extends javax.swing.JInternalFrame {

    private static MaterialInventoryService materialInventoryService;
    private final SystemService systemService;
    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(MaterialInventoryView.class.getName());
    private String idToUpdate = "";
    
    public MaterialInventoryView() {
        initComponents();
        cmbMeasurementUnit.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                getInfo();
            }
        });
        cmbMeasurementPurchaseUnit.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                getInfo();
            }
        });
        txtPurchaseAmount.addKeyListener (new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                getInfo();
            }
        });
        
        super.setTitle("Inventario de material");
        materialInventoryService = MaterialInventoryService.getInstance();
        systemService = SystemService.getInstance();
        this.setClosable(true);
        this.btnUpdate.setEnabled(false);
        loadComboBoxs();
        getItems();
    }
    
    private void getInfo () {
        
        try {
            new Float(txtPurchaseAmount.getText());
        } catch (NumberFormatException e) {
            System.out.println(e);
            return;
        }
        
        System.out.println("GET INFO");
        MeasurementUnit measurementUnit = (MeasurementUnit) cmbMeasurementUnit.getSelectedItem();
        MeasurementUnit measurementUnitPurchaseUnit = (MeasurementUnit) cmbMeasurementPurchaseUnit.getSelectedItem();
        String result = "";
        String total = txtPurchaseAmount.getText().isEmpty() ? "0" : txtPurchaseAmount.getText();
        if (measurementUnit.getId() != 0 && measurementUnitPurchaseUnit.getId() != 0) {
            lblMeasurementUnitInfo.setText(
                    total + " " +measurementUnit.getDescription() + " PARA COMPRAR UN " + measurementUnitPurchaseUnit.getDescription()
            );
        } else {
            lblMeasurementUnitInfo.setText("");
        }
    }
    
    private void delete () {
        if (table.getSelectedRow() != -1) {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Deseas eliminar la unidad de medida "+ table.getValueAt(table.getSelectedRow(), 1) +"?", "Eliminar", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                String id = table.getValueAt(table.getSelectedRow(), 0).toString();
                try {
                    MaterialInventory materialInventory = new MaterialInventory();
                    materialInventory.setId(new Long(id));
                    materialInventoryService.delete(materialInventory);
                    MaterialInventoryView.loadComboBoxs();
                    getItems();
                } catch (Exception e) {
                    LOGGER.log(Priority.ERROR,e);
                    JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_UNEXPECTED_ERROR + "\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para editar", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }
     
    private void edit () {
        if (table.getSelectedRow() != -1) {
            
            btnUpdate.setEnabled(true);
            btnAdd.setEnabled(false);
            
            final String id = table.getValueAt(table.getSelectedRow(), 0).toString();
            try {
                MaterialInventory materialInventory = materialInventoryService.getById(new Long(id));
                
                if (materialInventory == null) {
                    throw new Exception("No data found");
                }
                
                txtStock.setText(materialInventory.getStock().toString());
                txtPurchaseAmount.setText(materialInventory.getPurchaseAmount().toString());
                txtDescription.setText(materialInventory.getDescription());
                cmbMeasurementUnit.getModel().setSelectedItem(materialInventory.getMeasurementUnit());
                cmbMeasurementPurchaseUnit.getModel().setSelectedItem(materialInventory.getMeasurementUnitPurchase());
                cmbMaterialArea.getModel().setSelectedItem(materialInventory.getArea());         
                idToUpdate = id;
            } catch (Exception e) {
                LOGGER.log(Priority.ERROR,e);
                JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_UNEXPECTED_ERROR + "\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para editar", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showAddMeasurementUnit () {
        UnitMeasurementDialogForm dialog = new UnitMeasurementDialogForm(null, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
    }
    
    private void showAddMaterialArea () {
        MaterialAreaDialogForm dialog = new MaterialAreaDialogForm(null, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
    }
    
    private void cleanForm () {
        txtStock.setText("");
        txtDescription.setText("");
        txtPurchaseAmount.setText("");
        loadComboBoxs();
    }
    
    private void save () {
        
        String errorMessage = "";
        String description = txtDescription.getText();
        String stock = txtStock.getText();
        String purchaseAmount = txtPurchaseAmount.getText();
        MeasurementUnit measurementUnit = (MeasurementUnit) cmbMeasurementUnit.getSelectedItem();
        MeasurementUnit measurementUnitPurchase = (MeasurementUnit) cmbMeasurementPurchaseUnit.getSelectedItem();
        MaterialArea materialArea = (MaterialArea) cmbMaterialArea.getSelectedItem();
        
        if (description == null || description.isEmpty()) {
            errorMessage += "Descripción es requerido\n";
        }
        if (stock == null || stock.isEmpty()) {
            errorMessage += "Stock es requerdio\n";
        }
        if (purchaseAmount == null || purchaseAmount.isEmpty()) {
            errorMessage += "Cantidad de compra es requerido\n";
        }
        if (measurementUnit == null || measurementUnit.getId() == 0) {
            errorMessage += "Unidad de medida es requerido\n";
        }
        if (measurementUnitPurchase == null || measurementUnitPurchase.getId() == 0) {
            errorMessage += "Unidad de medida para compra es requerido\n";
        }
        if (materialArea == null || materialArea.getId() == 0) {
            errorMessage += "Área es requerido\n";
        }
        
        if (!errorMessage.isEmpty()) {
            JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        MaterialInventory materialInventory = new MaterialInventory();
        materialInventory.setDescription(description);
        materialInventory.setPurchaseAmount(new Float(purchaseAmount));
        materialInventory.setStock(new Float(stock));
        materialInventory.setMeasurementUnit(measurementUnit);
        materialInventory.setMeasurementUnitPurchase(measurementUnitPurchase);
        materialInventory.setArea(materialArea);
        
        try {
            if (!idToUpdate.isEmpty()) {
                materialInventory.setId(new Long(idToUpdate));
            }
            materialInventoryService.save(materialInventory);
            cleanForm();
            getItems();
            
        } catch (Exception e) {
            LOGGER.log(Priority.ERROR,e);
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_UNEXPECTED_ERROR + "\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            btnUpdate.setEnabled(false);
            idToUpdate = "";
        }
    }
    
    private void getItems () {
        formatTable();
        try {
            
            Map<String, Object> filter = new HashMap<>();
            
            String description = txtDescription.getText();
            MeasurementUnit measurementUnit = (MeasurementUnit) cmbMeasurementUnit.getSelectedItem();
            MeasurementUnit measurementUnitPurchase = (MeasurementUnit) cmbMeasurementUnit.getSelectedItem();
            MaterialArea materialArea = (MaterialArea) cmbMaterialArea.getSelectedItem();
            
            if (description != null && !description.isEmpty()) {
                filter.put("description", description);
            }
            
            if (measurementUnit != null && measurementUnit.getId() != 0) {
                filter.put("measurementUnitId", measurementUnit.getId());
            }
            
            if (measurementUnitPurchase != null && measurementUnitPurchase.getId() != 0) {
                filter.put("measurementUnitPurchaseId", measurementUnitPurchase.getId());
            }
            
            if (materialArea != null && materialArea.getId() != 0) {
                filter.put("materialAreaId", materialArea.getId());
            }
            
            List<MaterialInventory> results = materialInventoryService.get(filter);
            
            for(MaterialInventory materialInventory : results){
                DefaultTableModel temp = (DefaultTableModel) table.getModel();
                Object row[] = {
                    materialInventory.getId(),
                    materialInventory.getStock(),
                    materialInventory.getMeasurementUnit().getDescription(),
                    materialInventory.getDescription(),
                    materialInventory.getArea().getDescription(),
                    materialInventory.getPurchaseAmount(),
                    materialInventory.getMeasurementUnitPurchase().getDescription(),
                    materialInventory.getCreatedAt(),
                    materialInventory.getUpdatedAt()
                };
                temp.addRow(row); 
            }
            
        } catch (Exception e) {
            LOGGER.log(Priority.ERROR,e);
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_UNEXPECTED_ERROR + "\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void formatTable () {
        Object[][] data = {{"", "", "","","","","","",""}};
        String[] columnNames = {"id", "Stock", "U. Medida","Descripción","Área","Cant Compra","U. Medida Compra","Creado","Actualizado"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        table.setModel(TableModel);

        int[] anchos = {20,80,120,300,120,80,120,120,120};

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
    
    public static void loadComboBoxs() {
        cmbMaterialArea.removeAllItems();
        cmbMeasurementUnit.removeAllItems();
        cmbMeasurementPurchaseUnit.removeAllItems();
        try {
            List<MaterialArea> materialAreas = materialInventoryService.getMaterialAreas();
                cmbMaterialArea.addItem(
                        new MaterialArea(0l, ApplicationConstants.CMB_SELECCIONE)
                );
            for (MaterialArea area : materialAreas) {
                cmbMaterialArea.addItem(area);
            }
            
        } catch (BusinessException e) {
            
        }
        
        try {
            List<MeasurementUnit> measurementUnits = materialInventoryService.getMeasurementUnits();
                cmbMeasurementUnit.addItem(
                        new MeasurementUnit(0l, ApplicationConstants.CMB_SELECCIONE)
                );
                cmbMeasurementPurchaseUnit.addItem(
                        new MeasurementUnit(0l, ApplicationConstants.CMB_SELECCIONE)
                );
            for (MeasurementUnit measurementUnit : measurementUnits) {
                cmbMeasurementUnit.addItem(measurementUnit);
                cmbMeasurementPurchaseUnit.addItem(measurementUnit);
            }
        } catch (BusinessException e) {
            
        }
        
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
        jLabel1 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cmbMeasurementUnit = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbMaterialArea = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtPurchaseAmount = new javax.swing.JTextField();
        cmbMeasurementPurchaseUnit = new javax.swing.JComboBox<>();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        lblAddMeasurementUnit2 = new javax.swing.JLabel();
        lblAddMeasurementUnit = new javax.swing.JLabel();
        lblAddMeasurementUnit3 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnPDF = new javax.swing.JButton();
        lblMeasurementUnitInfo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        lblInfo = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Stock");

        txtStock.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Unidad de medida");

        cmbMeasurementUnit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbMeasurementUnit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Descripción");

        txtDescription.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Área");

        cmbMaterialArea.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbMaterialArea.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Cantidad y unidad de medida para compra");
        jLabel5.setToolTipText("Cantidad en unidad de medida que será igual a una unidad de medida de compra");

        txtPurchaseAmount.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        cmbMeasurementPurchaseUnit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbMeasurementPurchaseUnit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnAdd.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnAdd.setText("Agregar");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnEdit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnEdit.setText("Editar");
        btnEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
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

        btnUpdate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnUpdate.setText("Actualizar");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        lblAddMeasurementUnit2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lblAddMeasurementUnit2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAddMeasurementUnit2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAddMeasurementUnit2MouseClicked(evt);
            }
        });
        lblAddMeasurementUnit2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblAddMeasurementUnit2KeyPressed(evt);
            }
        });

        lblAddMeasurementUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lblAddMeasurementUnit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAddMeasurementUnit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAddMeasurementUnitMouseClicked(evt);
            }
        });
        lblAddMeasurementUnit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblAddMeasurementUnitKeyPressed(evt);
            }
        });

        lblAddMeasurementUnit3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lblAddMeasurementUnit3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAddMeasurementUnit3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAddMeasurementUnit3MouseClicked(evt);
            }
        });
        lblAddMeasurementUnit3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblAddMeasurementUnit3KeyPressed(evt);
            }
        });

        btnSearch.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnSearch.setText("Buscar");
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnPDF.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnPDF.setText("Exportar Excel");
        btnPDF.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPDFActionPerformed(evt);
            }
        });

        lblMeasurementUnitInfo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(txtPurchaseAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPDF)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblMeasurementUnitInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblAddMeasurementUnit))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addGap(8, 8, 8)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbMeasurementUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblAddMeasurementUnit2))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(cmbMeasurementPurchaseUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmbMaterialArea, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 76, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblAddMeasurementUnit3)
                                .addGap(84, 84, 84))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4))
                    .addComponent(lblAddMeasurementUnit2)
                    .addComponent(lblAddMeasurementUnit3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMeasurementUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMaterialArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(lblAddMeasurementUnit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPurchaseAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAdd)
                        .addComponent(btnEdit)
                        .addComponent(btnDelete)
                        .addComponent(btnUpdate)
                        .addComponent(btnSearch)
                        .addComponent(btnPDF))
                    .addComponent(cmbMeasurementPurchaseUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMeasurementUnitInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        table.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
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

        lblInfo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 809, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 670, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        save();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void lblAddMeasurementUnit2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddMeasurementUnit2MouseClicked
        // TODO add your handling code here:
        showAddMeasurementUnit();
    }//GEN-LAST:event_lblAddMeasurementUnit2MouseClicked

    private void lblAddMeasurementUnit2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblAddMeasurementUnit2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblAddMeasurementUnit2KeyPressed

    private void lblAddMeasurementUnitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddMeasurementUnitMouseClicked
        // TODO add your handling code here:
        showAddMeasurementUnit();
    }//GEN-LAST:event_lblAddMeasurementUnitMouseClicked

    private void lblAddMeasurementUnitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblAddMeasurementUnitKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblAddMeasurementUnitKeyPressed

    private void lblAddMeasurementUnit3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddMeasurementUnit3MouseClicked
        showAddMaterialArea();
    }//GEN-LAST:event_lblAddMeasurementUnit3MouseClicked

    private void lblAddMeasurementUnit3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblAddMeasurementUnit3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblAddMeasurementUnit3KeyPressed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        getItems();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        save();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        edit();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPDFActionPerformed
        systemService.exportarExcel(table);
    }//GEN-LAST:event_btnPDFActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnPDF;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    public static javax.swing.JComboBox<MaterialArea> cmbMaterialArea;
    public static javax.swing.JComboBox<MeasurementUnit> cmbMeasurementPurchaseUnit;
    public static javax.swing.JComboBox<MeasurementUnit> cmbMeasurementUnit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAddMeasurementUnit;
    private javax.swing.JLabel lblAddMeasurementUnit2;
    private javax.swing.JLabel lblAddMeasurementUnit3;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblMeasurementUnitInfo;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtPurchaseAmount;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables
}
