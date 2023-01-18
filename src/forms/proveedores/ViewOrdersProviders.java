
package forms.proveedores;


import clases.sqlclass;
import common.constants.ApplicationConstants;
import common.utilities.UtilityCommon;
import common.exceptions.BusinessException;
import common.services.UtilityService;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import mobiliario.IndexForm;
import model.DatosGenerales;
import model.providers.OrdenProveedor;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import parametersVO.ParameterOrderProvider;
import services.SystemService;
import services.providers.OrderProviderService;
import utilities.Utility;

public class ViewOrdersProviders extends javax.swing.JInternalFrame {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ViewOrdersProviders.class.getName());
    private final UtilityService utilityService = UtilityService.getInstance();
    private final OrderProviderService orderService = OrderProviderService.getInstance();
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    private PaymentsProvidersForm paymentsProvidersForm = null;
    public static String g_idRenta=null;
    public static String g_idOrder=null;    
    private OrderProviderForm orderProviderForm = null;  
    private sqlclass funcion = new sqlclass();  
    private SystemService systemService = SystemService.getInstance();

    public ViewOrdersProviders() {
        initComponents();
        this.setTitle("Ordenes al proveedor");
        initComboBox();
        tableFormat();
        funcion.conectate();
    }
    
     public void showProviders() {
        ViewProviderForm win = new ViewProviderForm(null, true);
        win.setVisible(true);
        win.setLocationRelativeTo(null);

    }
    
     public void mostrar_agregar_orden_proveedor() {
         
        String rentaId = this.tableViewOrdersProviders.getValueAt(tableViewOrdersProviders.getSelectedRow(), 8).toString();
        String orderId = this.tableViewOrdersProviders.getValueAt(tableViewOrdersProviders.getSelectedRow(), 0).toString();
        String folio = this.tableViewOrdersProviders.getValueAt(tableViewOrdersProviders.getSelectedRow(), 1).toString();
         
        if (UtilityCommon.verifyIfInternalFormIsOpen(orderProviderForm,IndexForm.jDesktopPane1)) {
            orderProviderForm = new OrderProviderForm(folio, orderId, rentaId);
            IndexForm.jDesktopPane1.add(orderProviderForm);
            orderProviderForm.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }
    }

     public void reportPDF(){
         
         if (this.tableViewOrdersProviders.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar ", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
         
        String idOrder = this.tableViewOrdersProviders.getValueAt(tableViewOrdersProviders.getSelectedRow(), 0).toString();
         
         JasperPrint jasperPrint;
        try {
           
            String pathLocation = Utility.getPathLocation();
            String archivo = pathLocation+ApplicationConstants.RUTA_REPORTE_ORDEN_PROVEEDOR;
            System.out.println("Cargando desde: " + archivo);
            if (archivo == null) {
                JOptionPane.showMessageDialog(rootPane, "No se encuentra el Archivo jasper");
                return;
            }
            JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(archivo);  
           
            DatosGenerales datosGenerales = systemService.getGeneralData();
            
            Map parametros = new HashMap<>();
            parametros.put("ID_ORDEN",idOrder);
            parametros.put("NOMBRE_EMPRESA",datosGenerales.getCompanyName());
            parametros.put("DIRECCION_EMPRESA",datosGenerales.getAddress1());
            parametros.put("TELEFONOS_EMPRESA",datosGenerales.getAddress2());
            parametros.put("EMAIL_EMPRESA",datosGenerales.getAddress3() != null ? datosGenerales.getAddress3() : "");
            //guardamos el parámetro
            parametros.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );
           
            jasperPrint = JasperFillManager.fillReport(masterReport, parametros, funcion.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathLocation+ApplicationConstants.NOMBRE_REPORTE_ORDEN_PROVEEDOR);
            File file2 = new File(pathLocation+ApplicationConstants.NOMBRE_REPORTE_ORDEN_PROVEEDOR);
            Desktop.getDesktop().open(file2);
            
        } catch (Exception e) {
            LOGGER.error(e);
            System.out.println("Mensaje de Error:" + e.toString());
            JOptionPane.showMessageDialog(rootPane, "Error cargando el reporte maestro: " + e.getMessage() + "\n" + e);
        }
     
     }
   public void initComboBox(){
       
        this.cmbLimit.removeAllItems();
        this.cmbStatus.removeAllItems();
        cmbLimit.addItem("100");
        cmbLimit.addItem("500");
        cmbLimit.addItem("1000");
        cmbLimit.addItem("10000");
        cmbStatus.addItem(ApplicationConstants.CMB_SELECCIONE);
        cmbStatus.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER);
        cmbStatus.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_PENDING);
        cmbStatus.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_CANCELLED);     
   
   }
   
   public void tableFormat() {
        Object[][] data = {{"","","","","","","","","","","",""}};
        String[] columnNames = {          
                        "No orden",
                        "Folio renta", 
                        "Usuario",
                        "Proveedor", 
                        "Status",                        
                        "Creado",
                        "Actualizado",
                        "Comentario",
                        "id_renta",
                        "Subtotal",
                        "Pagos",
                        "Total",
                        "Fecha Evento"
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        this.tableViewOrdersProviders.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tableViewOrdersProviders.setRowSorter(ordenarTabla);

        int[] anchos = {20,20,80,40,40, 80,100,100,20,80,60,60,60};

        for (int inn = 0; inn < tableViewOrdersProviders.getColumnCount(); inn++) {
            tableViewOrdersProviders.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tableViewOrdersProviders.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        tableViewOrdersProviders.getColumnModel().getColumn(8).setMaxWidth(0);
        tableViewOrdersProviders.getColumnModel().getColumn(8).setMinWidth(0);
        tableViewOrdersProviders.getColumnModel().getColumn(8).setPreferredWidth(0);
     
        tableViewOrdersProviders.getColumnModel().getColumn(0).setCellRenderer(centrar);
        tableViewOrdersProviders.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tableViewOrdersProviders.getColumnModel().getColumn(9).setCellRenderer(right);
        tableViewOrdersProviders.getColumnModel().getColumn(10).setCellRenderer(right);
        tableViewOrdersProviders.getColumnModel().getColumn(11).setCellRenderer(right);
        
    }
   
   public void showPaymentsProvidersForm() {
       
       if (this.tableViewOrdersProviders.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar ", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
       g_idOrder = this.tableViewOrdersProviders.getValueAt(tableViewOrdersProviders.getSelectedRow(), 0).toString();
       
        if (UtilityCommon.verifyIfInternalFormIsOpen(paymentsProvidersForm,IndexForm.jDesktopPane1)) {
            paymentsProvidersForm = new PaymentsProvidersForm();
            IndexForm.jDesktopPane1.add(paymentsProvidersForm);
            paymentsProvidersForm.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }
    }
   
   public void search(){
     tableFormat();
   
     ParameterOrderProvider parameter = new ParameterOrderProvider();
     
     Integer folioRenta = null;
     Integer orderNumber = null;
     try{
         folioRenta = Integer.parseInt(this.txtSearchFolioRenta.getText());
     }catch(NumberFormatException e){
         System.out.println(e);
     }
     
     try{
         orderNumber = Integer.parseInt(this.txtSearchOrderNumber.getText());
     }catch(NumberFormatException e){
         System.out.println(e);
     }
     
     if(folioRenta != null && !folioRenta.equals("")){
         parameter.setFolioRenta(folioRenta);
     }else if(orderNumber != null && !orderNumber.equals("")){
         parameter.setOrderId(orderNumber);
     }else{
        if(!this.txtSearchByNameProvider.getText().equals("")){
            parameter.setNameProvider(this.txtSearchByNameProvider.getText());
        }
        if(this.txtSearchInitialDate.getDate() != null && this.txtSearchEndDate.getDate() != null){
            parameter.setInitDate(new Timestamp(txtSearchInitialDate.getDate().getTime()));
            parameter.setEndDate(new Timestamp(txtSearchEndDate.getDate().getTime()));
        }
        if(this.txtSearchInitialEventDate.getDate() != null && this.txtSearchEndEventDate.getDate() != null){
            try {
                String formatDate = "dd/MM/yyyy";
                parameter.setInitEventDate(UtilityCommon.getStringFromDate(txtSearchInitialEventDate.getDate(),formatDate));
                parameter.setEndEventDate(UtilityCommon.getStringFromDate(txtSearchEndEventDate.getDate(),formatDate));
            } catch (ParseException e) {
                LOGGER.error(e);
                JOptionPane.showMessageDialog(null, e, "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if(!this.cmbStatus.getModel().getSelectedItem().equals(ApplicationConstants.CMB_SELECCIONE)){
            parameter.setStatus(this.cmbStatus.getSelectedItem().toString());
        }
     }
     parameter.setLimit(Integer.parseInt(this.cmbLimit.getSelectedItem().toString()));
     List<OrdenProveedor> list;
     try{
        list = orderService.getOrdersByParameters(parameter);
     }catch(BusinessException e){
        JOptionPane.showMessageDialog(null, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
     }
     Toolkit.getDefaultToolkit().beep();
     if(!list.isEmpty()){
         this.lblInfoGeneral.setText("Registros obtenidos: "+list.size()+", con un límite de: "+
                 this.cmbLimit.getSelectedItem().toString());
     
        DefaultTableModel tableModel = (DefaultTableModel) this.tableViewOrdersProviders.getModel();

        for(OrdenProveedor orden : list){      

             Object fila[] = {                                          
                 orden.getId(),
                 orden.getRenta().getFolio(),
                 orden.getUsuario().getNombre()+" "+orden.getUsuario().getApellidos(),
                 orden.getProveedor().getNombre()+" "+orden.getProveedor().getApellidos(),
                 orden.getStatusDescription(),
                 orden.getCreado(),
                 orden.getActualizado(),
                 orden.getComentario(),
                 orden.getRenta().getRentaId(),             
                 decimalFormat.format(orden.getTotal()),
                 decimalFormat.format(orden.getAbonos()),
                 decimalFormat.format((orden.getTotal() - orden.getAbonos())),
                 orden.getRenta().getFechaEvento()
               };
               tableModel.addRow(fila);

        }
     } else {
          this.lblInfoGeneral.setText("No se han obtenido resultados :(");
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

        jLabel9 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jMenuItem3 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        txtSearchFolioRenta = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cmbLimit = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        txtSearchInitialDate = new com.toedter.calendar.JDateChooser();
        txtSearchEndDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jbtnSearch = new javax.swing.JButton();
        txtSearchByNameProvider = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtSearchOrderNumber = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtSearchInitialEventDate = new com.toedter.calendar.JDateChooser();
        txtSearchEndEventDate = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableViewOrdersProviders = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        lblInfoGeneral = new javax.swing.JLabel();

        jLabel9.setText("jLabel9");

        jMenuItem3.setText("jMenuItem3");

        setClosable(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Busqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        txtSearchFolioRenta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchFolioRenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchFolioRentaKeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Nombre proveedor:");

        cmbStatus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Por fecha de creación:");

        cmbLimit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbLimit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Status orden:");

        txtSearchInitialDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchInitialDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchInitialDateMouseClicked(evt);
            }
        });
        txtSearchInitialDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchInitialDateKeyPressed(evt);
            }
        });

        txtSearchEndDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchEndDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchEndDateMouseClicked(evt);
            }
        });
        txtSearchEndDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchEndDateKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Limitar resultados a:");

        jbtnSearch.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jbtnSearch.setText("Buscar");
        jbtnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSearchActionPerformed(evt);
            }
        });

        txtSearchByNameProvider.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Folio renta:");

        txtSearchOrderNumber.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchOrderNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchOrderNumberKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Número de orden:");

        jButton1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton1.setText("Pagos");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton2.setText("Exportar EXCEL");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton3.setText("Detalle");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton4.setText("Proveedores");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton5.setText("Exportar PDF");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Por fecha del evento:");

        txtSearchInitialEventDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchInitialEventDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchInitialEventDateMouseClicked(evt);
            }
        });
        txtSearchInitialEventDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchInitialEventDateKeyPressed(evt);
            }
        });

        txtSearchEndEventDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchEndEventDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchEndEventDateMouseClicked(evt);
            }
        });
        txtSearchEndEventDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchEndEventDateKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1)
                        .addGap(3, 3, 3)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnSearch))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(28, 28, 28)
                                .addComponent(jLabel5))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtSearchByNameProvider, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(85, 85, 85)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtSearchOrderNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel2)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtSearchInitialEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtSearchEndEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel7))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbLimit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(34, 34, 34))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSearchByNameProvider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSearchOrderNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearchEndEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchInitialEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnSearch)
                    .addComponent(jButton1)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton2)
                    .addComponent(jButton5)))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 970, 140));

        tableViewOrdersProviders.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        tableViewOrdersProviders.setModel(new javax.swing.table.DefaultTableModel(
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
        tableViewOrdersProviders.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tableViewOrdersProviders.setRowHeight(14);
        tableViewOrdersProviders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableViewOrdersProvidersMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableViewOrdersProviders);

        lblInfoGeneral.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblInfoGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(511, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfoGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 970, 340));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchInitialDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchInitialDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialDateMouseClicked

    private void txtSearchInitialDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchInitialDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialDateKeyPressed

    private void txtSearchEndDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchEndDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndDateMouseClicked

    private void txtSearchEndDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchEndDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndDateKeyPressed

    private void tableViewOrdersProvidersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableViewOrdersProvidersMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            mostrar_agregar_orden_proveedor();
        }
    }//GEN-LAST:event_tableViewOrdersProvidersMouseClicked

    private void jbtnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSearchActionPerformed
        // TODO add your handling code here:
        this.search();
    }//GEN-LAST:event_jbtnSearchActionPerformed

    private void txtSearchFolioRentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchFolioRentaKeyPressed
        // TODO add your handling code here:
         if (evt.getKeyCode() == 10 ) {
            this.search();
        } 
    }//GEN-LAST:event_txtSearchFolioRentaKeyPressed

    private void txtSearchOrderNumberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchOrderNumberKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10 ) {
            this.search();
        } 
    }//GEN-LAST:event_txtSearchOrderNumberKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       showPaymentsProvidersForm();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        utilityService.exportarExcel(tableViewOrdersProviders);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        mostrar_agregar_orden_proveedor();        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        showProviders();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        reportPDF();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void txtSearchInitialEventDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchInitialEventDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialEventDateMouseClicked

    private void txtSearchInitialEventDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchInitialEventDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialEventDateKeyPressed

    private void txtSearchEndEventDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchEndEventDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndEventDateMouseClicked

    private void txtSearchEndEventDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchEndEventDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndEventDateKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbLimit;
    private javax.swing.JComboBox cmbStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnSearch;
    private javax.swing.JLabel lblInfoGeneral;
    private javax.swing.JTable tableViewOrdersProviders;
    private javax.swing.JTextField txtSearchByNameProvider;
    private com.toedter.calendar.JDateChooser txtSearchEndDate;
    private com.toedter.calendar.JDateChooser txtSearchEndEventDate;
    private javax.swing.JTextField txtSearchFolioRenta;
    private com.toedter.calendar.JDateChooser txtSearchInitialDate;
    private com.toedter.calendar.JDateChooser txtSearchInitialEventDate;
    private javax.swing.JTextField txtSearchOrderNumber;
    // End of variables declaration//GEN-END:variables

    private void setLocationRelativeTo(Object object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
