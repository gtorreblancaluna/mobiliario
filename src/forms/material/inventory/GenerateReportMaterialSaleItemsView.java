package forms.material.inventory;

import common.constants.ApplicationConstants;
import common.services.UtilityService;
import common.utilities.UtilityCommon;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.material.inventory.MaterialSaleItemReport;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import services.material.inventory.MaterialInventoryService;


public class GenerateReportMaterialSaleItemsView extends javax.swing.JInternalFrame {
    
    private final UtilityService utilityService = UtilityService.getInstance();
    private final MaterialInventoryService materialInventoryService;
    private final String gRentId;
    
    public GenerateReportMaterialSaleItemsView(String rentId) {
        initComponents();
        materialInventoryService = MaterialInventoryService.getInstance();
        this.setClosable(true);
        this.setTitle("Generar reporte RECOLECCIÓN DE MATERIAL");
        this.lblInfo.setText("FOLIO: "+rentId);
        this.gRentId = rentId;
        init();
    }
    
    private enum Header {
        
        ORDER_AMOUNT(0),
        ITEM_DESCRIPTION(1),
        AMOUNT(2),
        MEASUREMENT_UNIT(3),
        MATERIAL_DESCRIPTION(4),
        PURCHASE_AMOUNT(5),
        PURCHASE_AMOUNT_ROUND(6),
        PURCHASE_MEASUREMENT_UNIT(7),
        PROVIDER_NAME(8),
        PROVIDER_ADDRESS(9),
        PROVIDER_TEL(10),
        PROVIDER_ID(11);
        
        private Header (Integer column) {
            this.column = column;
        }
        
        private Integer column;

        public Integer getColumn() {
            return column;
        }
        
    }
    
    private void generatePDF () {
        
       if(table.getRowCount() == 0){
           JOptionPane.showMessageDialog(this, "No hay elementos en la tabla para generar el reporte", "ERROR", JOptionPane.ERROR_MESSAGE);
           return;
       }
        
        List<String> providersId = new ArrayList<>();
        for (int i = 0; i < table.getRowCount(); i++) {
            if (providersId.isEmpty()) {
                providersId.add(table.getValueAt(i, Header.PROVIDER_ID.getColumn()).toString());
            } else {
                if (!providersId.contains(table.getValueAt(i, Header.PROVIDER_ID.getColumn()).toString())) {
                    providersId.add(table.getValueAt(i, Header.PROVIDER_ID.getColumn()).toString());
                }
            }
        }
        
        for (String providerId : providersId) {
            List<MaterialSaleItemReport> result = new ArrayList<>();
            for (int i = 0; i < table.getRowCount(); i++) {
                if ( table.getValueAt(i, Header.PROVIDER_ID.getColumn()).equals(providerId) ) {
                    MaterialSaleItemReport materialSaleItemReport = new MaterialSaleItemReport();
                    materialSaleItemReport.setDescriptionItem(table.getValueAt(i, Header.ITEM_DESCRIPTION.getColumn()).toString());
                    materialSaleItemReport.setPurchaseAmountRound(Float.parseFloat(table.getValueAt(i, Header.PURCHASE_AMOUNT_ROUND.getColumn()).toString()));
                    materialSaleItemReport.setPurchaseMeasurementUnitDescription(table.getValueAt(i, Header.PURCHASE_MEASUREMENT_UNIT.getColumn()).toString());
                    materialSaleItemReport.setMaterialInventoryDescription(table.getValueAt(i, Header.MATERIAL_DESCRIPTION.getColumn()).toString());
                    materialSaleItemReport.setProviderId(
                            table.getValueAt(i, Header.PROVIDER_ID.getColumn()).toString()
                    );
                    materialSaleItemReport.setProviderAddress(
                            table.getValueAt(i, Header.PROVIDER_ADDRESS.getColumn()).toString()
                    );
                    materialSaleItemReport.setProviderPhoneNumber(
                            table.getValueAt(i, Header.PROVIDER_TEL.getColumn()).toString()
                    );
                    materialSaleItemReport.setProviderName(
                            table.getValueAt(i, Header.PROVIDER_NAME.getColumn()).toString()
                    );
                    result.add(materialSaleItemReport);
                }
            }
            try {
                reportPDF(result);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e, "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        
    }
    
    private void reportPDF(List<MaterialSaleItemReport> list) throws Exception{
     
        JasperPrint jasperPrint;
        String pathLocation = UtilityCommon.getPathLocation();
        String pathFile = pathLocation+ApplicationConstants.JASPER_REPORT_COLLECTION_MATERIAL;
        System.out.println("Cargando desde: " + pathFile);
        if (pathFile == null || pathFile.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "No se encuentra el Archivo jasper: "+pathFile);
            return;
        }
        JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(pathFile);
        Map params = new HashMap<>();
        params.put("RENT_ID",this.gRentId);
        params.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA);
        params.put("LIST", list);
        params.put("PROVIDER_NAME", list.get(0).getProviderName());
        params.put("PROVIDER_PHONE_NUMBER", list.get(0).getProviderPhoneNumber());
        params.put("PROVIDER_ADDRESS", list.get(0).getProviderAddress());
        params.put("SUBREPORT_DIR", pathLocation+"/");
        JRDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list);
        jasperPrint = JasperFillManager.fillReport(masterReport, params, beanCollectionDataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, pathLocation+"collectionMaterialReport_"+list.get(0).getProviderId()+".pdf");
        File fileReport = new File(pathLocation+"collectionMaterialReport_"+list.get(0).getProviderId()+".pdf");
        Desktop.getDesktop().open(fileReport);
     
     }
    
    private void recharge () {
        init();
    }
    
    private void removeItems () {
        if (table.getSelectedRow() != -1) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int[] rows = table.getSelectedRows();
            for(int i=0;i<rows.length;i++){
              model.removeRow(rows[i]-i);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una o varias filas para quitar del reporte", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void formatTable () {
        Object[][] data = {{"", "", "","","","","","","","","",""}};
        String[] columnNames = {"Cantidad Pedido","Artículo", "Cantidad","U. Medida", "Material","Calculo","Calculo Redondeado","U. Medida","Proveedor","Dirección","Teléfonos","provider_id"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        table.setModel(tableModel);
        
        TableRowSorter<TableModel> order = new TableRowSorter<TableModel>(tableModel); 
        table.setRowSorter(order);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        int[] anchos = {70,180,80,120,180,70,70,120,120,120,120,70};

        for (int inn = 0; inn < table.getColumnCount(); inn++) {
            table.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) table.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        
        table.getColumnModel().getColumn(
                Header.ORDER_AMOUNT.getColumn()
        ).setCellRenderer(right);
        table.getColumnModel().getColumn(Header.AMOUNT.getColumn()).setCellRenderer(right);
        table.getColumnModel().getColumn(Header.PURCHASE_AMOUNT.getColumn()).setCellRenderer(right);
        table.getColumnModel().getColumn(Header.PURCHASE_AMOUNT_ROUND.getColumn()).setCellRenderer(right);
        table.getColumnModel().getColumn(Header.PROVIDER_ID.getColumn()).setMaxWidth(0);
        table.getColumnModel().getColumn(Header.PROVIDER_ID.getColumn()).setMinWidth(0);
    }
    
    private void init () {
        
        try {
            List<MaterialSaleItemReport> list = materialInventoryService.getMaterialSaleItemsByItemsIdReport(this.gRentId);
            formatTable();
            
            for (MaterialSaleItemReport material : list) {
                DefaultTableModel temp = (DefaultTableModel) table.getModel();
                Object row[] = {
                    material.getAmountItem(),
                    material.getDescriptionItem(),
                    material.getAmount(),
                    material.getMeasurementUnitDescription(),
                    material.getMaterialInventoryDescription(),
                    material.getPurchaseAmount(),
                    material.getPurchaseAmountRound(),
                    material.getPurchaseMeasurementUnitDescription(),
                    material.getProviderName(),
                    material.getProviderAddress(),
                    material.getProviderPhoneNumber(),
                    material.getProviderId()
                };
                temp.addRow(row); 
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e, "ERROR", JOptionPane.ERROR_MESSAGE);
           return;
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        btnPDF = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        btnRecharge = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();

        jScrollPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        table.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
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

        btnPDF.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnPDF.setText("Generar PDF");
        btnPDF.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPDFActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnDelete.setText("Quitar del reporte");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnExcel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnExcel.setText("Generar Excel");
        btnExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });

        btnRecharge.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnRecharge.setText("Recargar consulta");
        btnRecharge.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRecharge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRechargeActionPerformed(evt);
            }
        });

        lblInfo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRecharge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPDF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcel))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(27, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPDF)
                            .addComponent(btnDelete)
                            .addComponent(btnExcel)
                            .addComponent(btnRecharge))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        utilityService.exportarExcel(table);
    }//GEN-LAST:event_btnExcelActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        removeItems();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnRechargeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRechargeActionPerformed
        recharge();
    }//GEN-LAST:event_btnRechargeActionPerformed

    private void btnPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPDFActionPerformed
        generatePDF();
    }//GEN-LAST:event_btnPDFActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnPDF;
    private javax.swing.JButton btnRecharge;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
