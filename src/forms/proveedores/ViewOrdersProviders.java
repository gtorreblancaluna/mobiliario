
package forms.proveedores;


import clases.sqlclass;
import exceptions.BusinessException;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import mobiliario.ApplicationConstants;
import static mobiliario.consultar_renta.g_mensajeFaltantes;
import static mobiliario.consultar_renta.g_totalFaltantes;
import mobiliario.principal;
import model.DatosGenerales;
import model.providers.OrdenProveedor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import parametersVO.ParameterOrderProvider;
import services.SystemService;
import services.providers.OrderProviderService;
import utilities.Utility;

/**
 *
 * @author Gerardo Torreblanca
 */
public class ViewOrdersProviders extends javax.swing.JInternalFrame {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ViewOrdersProviders.class.getName());
    private final SystemService systemService = SystemService.getInstance();
    private final OrderProviderService orderService = OrderProviderService.getInstance();
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    private PaymentsProvidersForm paymentsProvidersForm = null;
    public static String g_idRenta=null;
    public static String g_idOrder=null;    
    private OrderProviderForm orderProviderForm = null;  
    private sqlclass funcion = new sqlclass();  

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
         
        g_idRenta = this.tableViewOrdersProviders.getValueAt(tableViewOrdersProviders.getSelectedRow(), 8).toString();
        g_idOrder = this.tableViewOrdersProviders.getValueAt(tableViewOrdersProviders.getSelectedRow(), 0).toString();
         
        if (Utility.verifyIfInternalFormIsOpen(orderProviderForm)) {
            orderProviderForm = new OrderProviderForm();
            principal.jDesktopPane1.add(orderProviderForm);
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
            JasperReport masterReport = null;
            
            masterReport = (JasperReport) JRLoader.loadObject(archivo);
           
            DatosGenerales datosGenerales = systemService.getGeneralData();
            
            Map parametros = new HashMap<>();
            parametros.put("ID_ORDEN",idOrder);
            parametros.put("NOMBRE_EMPRESA",datosGenerales.getCompanyName());
            parametros.put("DIRECCION_EMPRESA",datosGenerales.getAddress1());
            parametros.put("TELEFONOS_EMPRESA",datosGenerales.getAddress2());
            parametros.put("EMAIL_EMPRESA",datosGenerales.getAddress3());
            //guardamos el parámetro
            parametros.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );
           
            jasperPrint = JasperFillManager.fillReport(masterReport, parametros, funcion.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathLocation+ApplicationConstants.NOMBRE_REPORTE_ORDEN_PROVEEDOR);
            File file2 = new File(pathLocation+ApplicationConstants.NOMBRE_REPORTE_ORDEN_PROVEEDOR);
            Desktop.getDesktop().open(file2);

        } catch (IOException e) {
            LOGGER.error(e);
            JOptionPane.showMessageDialog(rootPane, "Error cargando el reporte maestro: " + e.getMessage());
        } catch (JRException e) {
            LOGGER.error(e);
            JOptionPane.showMessageDialog(rootPane, "Error cargando el reporte maestro: " + e.getMessage());            
        } catch (Exception e) {
            LOGGER.error(e);
            System.out.println("Mensaje de Error:" + e.toString());
            JOptionPane.showMessageDialog(rootPane, "Mensaje de Error :" + e.toString() + "\n Existe un PDF abierto, cierralo e intenta generar el PDF nuevamente");
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
                        "Total"
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        this.tableViewOrdersProviders.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tableViewOrdersProviders.setRowSorter(ordenarTabla);

        int[] anchos = {20,20,80,40,40, 80,100,100,20,80,60,60};

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
       
        if (Utility.verifyIfInternalFormIsOpen(paymentsProvidersForm)) {
            paymentsProvidersForm = new PaymentsProvidersForm();
//            orderProviderForm.setLocation(this.getWidth() / 2 - orderProviderForm.getWidth() / 2, this.getHeight() / 2 - orderProviderForm.getHeight() / 2 - 20);
            principal.jDesktopPane1.add(paymentsProvidersForm);
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
         folioRenta = new Integer(this.txtSearchFolioRenta.getText());
     }catch(NumberFormatException e){
         System.out.println(e);
     }
     
     try{
         orderNumber = new Integer(this.txtSearchOrderNumber.getText());
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
        if(!this.cmbStatus.equals(ApplicationConstants.CMB_SELECCIONE)){
            parameter.setStatus(this.cmbStatus.getSelectedItem().toString());
        }
     }
     parameter.setLimit(new Integer(this.cmbLimit.getSelectedItem().toString()));
     List<OrdenProveedor> list;
     try{
        list = orderService.getOrdersByParameters(parameter);
     }catch(BusinessException e){
        JOptionPane.showMessageDialog(null, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
        return;
     }
     Toolkit.getDefaultToolkit().beep();
     if(list == null || list.size()<=0){
         this.lblInfoGeneral.setText("No se han obtenido resultados :(");
         return;
     }
     if(list.size() == 1){
        this.lblInfoGeneral.setText("Se obtuvo "+list.size()+" resultado");
     }else{
        this.lblInfoGeneral.setText("Se obtuvieron "+list.size()+" resultados");
     }
     
     DefaultTableModel tableModel = (DefaultTableModel) this.tableViewOrdersProviders.getModel();
     
     for(OrdenProveedor orden : list){      
         
          Object fila[] = {                                          
              orden.getId(),
              orden.getRenta().getFolio(),
              orden.getUsuario().getNombre()+" "+orden.getUsuario().getApellidos(),
              orden.getProveedor().getNombre()+" "+orden.getProveedor().getApellidos(),
              orden.getStatusDescription(),
//              orden.getStatus(),
              orden.getCreado(),
              orden.getActualizado(),
              orden.getComentario(),
              orden.getRenta().getRentaId(),             
              decimalFormat.format(orden.getTotal()),
              decimalFormat.format(orden.getAbonos()),
              decimalFormat.format((orden.getTotal() - orden.getAbonos()))
            };
            tableModel.addRow(fila);
     
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
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableViewOrdersProviders = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        lblInfoGeneral = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

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
        jbtnSearch.setText("Enviar");
        jbtnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel3)
                        .addGap(117, 117, 117)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70)
                        .addComponent(jbtnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(txtSearchByNameProvider, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(75, 75, 75)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSearchOrderNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(234, 234, 234))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchByNameProvider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchOrderNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel3))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnSearch)))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 970, 120));

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
        tableViewOrdersProviders.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuBar1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenu1.setText("Archivo");
        jMenu1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jMenuItem2.setText("Pagos a proveedor");
        jMenuItem2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jMenuItem5.setText("Ver detalle orden");
        jMenuItem5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jMenuItem6.setText("Ver proveedores");
        jMenuItem6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar1.add(jMenu1);

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

        jMenuItem1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jMenuItem1.setText("Generar orden PDF");
        jMenuItem1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

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

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        systemService.exportarExcel(tableViewOrdersProviders);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        if (this.tableViewOrdersProviders.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar ", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mostrar_agregar_orden_proveedor();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        showPaymentsProvidersForm();
        
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        
        showProviders();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        
        reportPDF();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cmbLimit;
    private javax.swing.JComboBox cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnSearch;
    private javax.swing.JLabel lblInfoGeneral;
    private javax.swing.JTable tableViewOrdersProviders;
    private javax.swing.JTextField txtSearchByNameProvider;
    private com.toedter.calendar.JDateChooser txtSearchEndDate;
    private javax.swing.JTextField txtSearchFolioRenta;
    private com.toedter.calendar.JDateChooser txtSearchInitialDate;
    private javax.swing.JTextField txtSearchOrderNumber;
    // End of variables declaration//GEN-END:variables

    private void setLocationRelativeTo(Object object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
