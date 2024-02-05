package forms.inventario;

import common.constants.ApplicationConstants;
import common.exceptions.DataOriginException;
import common.form.items.AgregarArticuloDisponibilidadDialog;
import common.model.Articulo;
import common.model.DesgloseAlmacenModel;
import common.services.DesgloseAlmacenService;
import common.services.ItemService;
import common.tables.DesgloseAlmacenByItemInitTable;
import common.tables.TableItems;
import common.utilities.UtilityCommon;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class DesgloseAlmacenForm extends javax.swing.JInternalFrame {
    
    private final Articulo item;
    private final List<Articulo> items;
    private String itemRelationId;
    private String desgloseAlmacenIdToUpdate;
    private final ItemService itemService =
            ItemService.getInstance();
    private final DesgloseAlmacenService desgloseAlmacenService = 
            DesgloseAlmacenService.getInstance();
    private final DesgloseAlmacenByItemInitTable desgloseAlmacenByItemInitTable;
    
    public DesgloseAlmacenForm(Articulo item, List<Articulo> items) {
        this.item = item;
        this.items = items;
        desgloseAlmacenByItemInitTable = new DesgloseAlmacenByItemInitTable();
        initComponents();
        init();
    }
    
    // close dialog when esc is pressed.
    private void addEscapeListener() {
        ActionListener escListener = (ActionEvent e) -> {
            setVisible(false);
            dispose();
        };

        this.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

    }
        
    private void init () {
        UtilityCommon.addJtableToPane(400, 600, this.tablePane, desgloseAlmacenByItemInitTable);
        this.setClosable(true);        
        // center window
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - this.getWidth()) / 2;
        final int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x,y);
        // center window
        
        this.setTitle("Desglose de almacen por artículo.");
        addEscapeListener();
        this.lblTitle.setText("Desglose de almacen para el artículo: "
                +item.getDescripcion() + " " + item.getColor().getColor());
        disableButtons();
        disableTextFields();        
        getAndfillTable();        
        desgloseAlmacenByItemInitTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                //int row = desgloseAlmacenByItemInitTable.rowAtPoint(evt.getPoint());
                //int col = desgloseAlmacenByItemInitTable.columnAtPoint(evt.getPoint());
                setOrDisableButtonsWhenMouseClickedTable();                
            }
        });
        desgloseAlmacenByItemInitTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {                
                if (evt.getClickCount() == 2) {
                    String id = String.valueOf(desgloseAlmacenByItemInitTable.
                                getValueAt(
                                    desgloseAlmacenByItemInitTable.getSelectedRow(), 
                                    DesgloseAlmacenByItemInitTable.Column.ID.getNumber()));
                    if (id != null) {
                        desgloseAlmacenIdToUpdate = id;
                        update();
                    }
                }
            }
        });
        
        txtAmount.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                JTextField textField = (JTextField) evt.getSource();
                if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (desgloseAlmacenIdToUpdate == null) {
                        insertItem();
                    } else {
                        save();
                    }
                } else if (!textField.getText().isEmpty()) {
                    String onlyNumber = UtilityCommon.onlyNumbers(textField.getText().trim());
                    textField.setText(onlyNumber);
                }
            }
        });
        
        txtComment.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (desgloseAlmacenIdToUpdate == null) {
                        insertItem();
                    } else {
                        save();
                    }
                }           
            }
        });        
        

    }
    
    private void setOrDisableButtonsWhenMouseClickedTable () {
        
        int total = getTotalRowsChecked();                
        if (total == 1) {
            btnUpdateOrSave.setEnabled(true);
            btnDelete.setEnabled(true);
        } else if (total > 1) {
            btnUpdateOrSave.setEnabled(false);
            btnDelete.setEnabled(true);
        } else if (total == 0 && desgloseAlmacenIdToUpdate == null){
            btnUpdateOrSave.setEnabled(false);
            btnDelete.setEnabled(false);
        } else if (total == 0 && desgloseAlmacenIdToUpdate != null){
            btnDelete.setEnabled(false);
        }
    }
    
    private boolean validateForm () {
        
        boolean valid = true;        
        String amount = txtAmount.getText().trim();        

        if (amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cantidad es requerido.", 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            txtAmount.requestFocus();
            valid = false;
        }
        
        return valid;
    }
    
    private void save () {
        if (!validateForm()) {
            return;
        }
        if (desgloseAlmacenIdToUpdate == null) {
            JOptionPane.showMessageDialog(this, 
                    ApplicationConstants.MESSAGE_UNEXPECTED_ERROR_CONTACT_SUPPORT, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
        DesgloseAlmacenModel desgloseAlmacenModelToUpdate = getModelFromTextFileds();
        
        desgloseAlmacenModelToUpdate.setId(
                Long.parseLong(desgloseAlmacenIdToUpdate));
        
        try {
            desgloseAlmacenService.saveOrUpdate(desgloseAlmacenModelToUpdate);
            desgloseAlmacenIdToUpdate = null;
            disableButtons();
            cleanTextFields();
            disableTextFields();
            getAndfillTable();            
        } catch (DataOriginException dataOriginException) {
            JOptionPane.showMessageDialog(this, dataOriginException, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        } finally {
            Toolkit.getDefaultToolkit().beep();
        }
        
        
    }
    
    private void update () {
        
        try {            
            DesgloseAlmacenModel desgloseAlmacenModel = 
                desgloseAlmacenService.getById(desgloseAlmacenIdToUpdate);
            itemRelationId = String.valueOf(desgloseAlmacenModel.getItemRelation().getArticuloId());
            btnUpdateOrSave.setText("Guardar");
            btnUpdateOrSave.setEnabled(true);
            btnCancel.setEnabled(true);
            enableTextFields();
            txtAmount.setText(String.valueOf(desgloseAlmacenModel.getAmount()));
            txtItem.setText(desgloseAlmacenModel.getItemRelation().getDescripcion() + " " +
                    desgloseAlmacenModel.getItemRelation().getColor().getColor());
            txtComment.setText(desgloseAlmacenModel.getComment());
            txtAmount.requestFocus();            
        } catch (DataOriginException dataOriginException) {
            JOptionPane.showMessageDialog(this, dataOriginException, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        } finally {
            Toolkit.getDefaultToolkit().beep();
        }
        
    }
    
    private DesgloseAlmacenModel getModelFromTextFileds () {
        String comment = txtComment.getText().trim();
        String amount = txtAmount.getText().trim();
        
        DesgloseAlmacenModel desgloseAlmacenModel =
                new DesgloseAlmacenModel();
        
        Articulo itemInit = new Articulo();
        itemInit.setArticuloId(item.getArticuloId());
        Articulo itemRelation = new Articulo();
        itemRelation.setArticuloId(Integer.parseInt(itemRelationId));        
        
        desgloseAlmacenModel.setItemInit(itemInit);
        desgloseAlmacenModel.setItemRelation(itemRelation);
        desgloseAlmacenModel.setComment(comment);
        desgloseAlmacenModel.setAmount(Integer.parseInt(amount));
        
        return desgloseAlmacenModel;
    }
    
    private void insertItem () { 
        
        if (!validateForm()) {
            return;
        }
        
        if (itemRelationId == null) {
            JOptionPane.showMessageDialog(this, "Elige un artículo del inventario (botón 'Articulos').", 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DesgloseAlmacenModel desgloseAlmacenModelToInsert = getModelFromTextFileds();
        
        try {
            desgloseAlmacenService.saveOrUpdate(desgloseAlmacenModelToInsert);
            getAndfillTable();
            disableButtons();
            cleanTextFields();
            disableTextFields();
        } catch (DataOriginException dataOriginException) {
            JOptionPane.showMessageDialog(this, dataOriginException, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        } finally {
            Toolkit.getDefaultToolkit().beep();
        }
        

        
    }
    
    private int getTotalRowsChecked () {
        int count = 0;
        for (int i = 0 ; i < desgloseAlmacenByItemInitTable.getRowCount() ; i++) {
            if (Boolean.parseBoolean(desgloseAlmacenByItemInitTable.getValueAt(i, TableItems.Column.BOOLEAN.getNumber())
                            .toString())) {
                count++;
            
            }
        }
        return count;
    }
    
    private void getAndfillTable () {
        
        try {
            
            List<DesgloseAlmacenModel> desgloseAlmacenItems = 
                    desgloseAlmacenService.getItemsDesgloseAlmacenByInitItem(String.valueOf(item.getArticuloId()));

            desgloseAlmacenByItemInitTable.format();        
            DefaultTableModel tableModel = (DefaultTableModel) desgloseAlmacenByItemInitTable.getModel();        
            desgloseAlmacenItems.forEach(desgloseAlmacenItem -> {
                Object row[] = {  
                    false,
                    desgloseAlmacenItem.getId(),
                    desgloseAlmacenItem.getItemRelation().getArticuloId(),
                    desgloseAlmacenItem.getAmount(),
                    desgloseAlmacenItem.getItemRelation().getDescripcion(),
                    desgloseAlmacenItem.getItemRelation().getColor().getColor(),
                    desgloseAlmacenItem.getComment(),
                    desgloseAlmacenItem.getCreatedAt(),
                    desgloseAlmacenItem.getUpdatedAt()                
                  };
                  tableModel.addRow(row);
            });
        } catch (DataOriginException dataOriginException) {
            JOptionPane.showMessageDialog(this, dataOriginException, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    private void disableTextFields() {
        txtItem.setEnabled(false);
        txtComment.setEnabled(false);
        txtAmount.setEnabled(false);
    }
    
    private void enableTextFields() {
        txtComment.setEnabled(true);
        txtAmount.setEnabled(true);
    }
    
    private void cleanTextFields() {
        txtItem.setText(ApplicationConstants.EMPTY_STRING);
        txtComment.setText(ApplicationConstants.EMPTY_STRING);
        txtAmount.setText(ApplicationConstants.EMPTY_STRING);
    }
    
    private void discheckedTable () {
        
        UtilityCommon.selectAllCheckboxInTable(
                desgloseAlmacenByItemInitTable,
                DesgloseAlmacenByItemInitTable.Column.BOOLEAN.getNumber(),
                false);
        
    }
    
    
    private void disableButtons() {
        this.btnDelete.setEnabled(false);
        this.btnAdd.setEnabled(false);
        this.btnUpdateOrSave.setText("Actualizar");
        this.btnUpdateOrSave.setEnabled(false);
        btnCancel.setEnabled(false);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tablePane = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnShowItems = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdateOrSave = new javax.swing.JButton();
        txtAmount = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtItem = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtComment = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();

        javax.swing.GroupLayout tablePaneLayout = new javax.swing.GroupLayout(tablePane);
        tablePane.setLayout(tablePaneLayout);
        tablePaneLayout.setHorizontalGroup(
            tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        tablePaneLayout.setVerticalGroup(
            tablePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 325, Short.MAX_VALUE)
        );

        lblTitle.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lblTitle.setText("jLabel1");

        btnShowItems.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnShowItems.setMnemonic('I');
        btnShowItems.setText("Inventario");
        btnShowItems.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnShowItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowItemsActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnDelete.setMnemonic('E');
        btnDelete.setText("Eliminar");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnUpdateOrSave.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnUpdateOrSave.setMnemonic('U');
        btnUpdateOrSave.setText("Actualizar");
        btnUpdateOrSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdateOrSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateOrSaveActionPerformed(evt);
            }
        });

        txtAmount.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAmount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAmountKeyReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Cantidad:");

        txtItem.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Articulo:");

        txtComment.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtComment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCommentKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Comentario:");

        btnAdd.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnAdd.setMnemonic('A');
        btnAdd.setText("Agregar");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel4.setText("?");
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnCancel.setText("Cancelar");
        btnCancel.setToolTipText("Cancelar");
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnShowItems)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtAmount)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtItem, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtComment)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(80, 80, 80)
                                        .addComponent(btnAdd)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnDelete)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnUpdateOrSave)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnCancel))
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtComment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnShowItems)
                    .addComponent(btnDelete)
                    .addComponent(btnUpdateOrSave)
                    .addComponent(btnAdd)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tablePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablePane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnShowItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowItemsActionPerformed
        AgregarArticuloDisponibilidadDialog dialog = new AgregarArticuloDisponibilidadDialog(null, true, items);
        itemRelationId = dialog.showDialog();
        if (itemRelationId != null) {            
            Articulo itemRelation = itemService.obtenerArticuloPorId(Integer.parseInt(itemRelationId));
            cleanTextFields();
            txtItem.setText(itemRelation.getDescripcion()+" "+itemRelation.getColor().getColor());
            txtAmount.requestFocus();
            btnAdd.setEnabled(true);
            enableTextFields();
        }
        //addItem(itemId);
    }//GEN-LAST:event_btnShowItemsActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if (itemRelationId != null) {
            insertItem();
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void txtCommentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCommentKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCommentKeyPressed

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        JOptionPane.showMessageDialog(this, ""
                + "Indica los artículos que se mostrarán en el PDF (Reporte por categorías) "
                + "para: "+item.getDescripcion()+" "+item.getColor().getColor()+"."
                        +"\n\nPrincipalmente funciona para indicar a los almacenistas "
                        + "cuantos artículos deben arreglar por artículo. "
                        + "\n\nEJEMPLO: una sala debe de llevar 2 cojines, 2 almohadas, 1 cubre sala, etc",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jLabel4MouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        List<String> ids = UtilityCommon.getIdsSelected(desgloseAlmacenByItemInitTable, 
                DesgloseAlmacenByItemInitTable.Column.BOOLEAN.getNumber(), 
                DesgloseAlmacenByItemInitTable.Column.ID.getNumber());
        
        if (ids.isEmpty()) {
            return;
        }
        
        int seleccion = JOptionPane.showOptionDialog(this, ""
                + "Total de elementos a eliminar: ["+ ids.size() + "]. Confirma para continuar.", 
                ApplicationConstants.MESSAGE_TITLE_CONFIRM_DELETE, 
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
        if (seleccion != 0) {//presiono que no
           return;
        }
        
        try {
            desgloseAlmacenService.delete(ids);
            discheckedTable();
            getAndfillTable();
            disableButtons();
        } catch (DataOriginException dataOriginException) {
            JOptionPane.showMessageDialog(this, dataOriginException, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnUpdateOrSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateOrSaveActionPerformed

        if (btnUpdateOrSave.getText().equals("Guardar")) {
            save();
        } else {
            String idSelected = UtilityCommon.getIdSelected(
            desgloseAlmacenByItemInitTable, 
            DesgloseAlmacenByItemInitTable.Column.BOOLEAN.getNumber(), 
            DesgloseAlmacenByItemInitTable.Column.ID.getNumber());
        
            if (idSelected != null) {
                desgloseAlmacenIdToUpdate = idSelected;
                update();                
            }
        }
        
        

        
    }//GEN-LAST:event_btnUpdateOrSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        desgloseAlmacenIdToUpdate = null;
        cleanTextFields();
        disableButtons();
        disableTextFields();
        discheckedTable();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtAmountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAmountKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAmountKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnShowItems;
    private javax.swing.JButton btnUpdateOrSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel tablePane;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtComment;
    private javax.swing.JTextField txtItem;
    // End of variables declaration//GEN-END:variables
}
