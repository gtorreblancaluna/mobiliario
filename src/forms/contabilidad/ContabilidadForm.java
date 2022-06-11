package forms.contabilidad;

import common.constants.ApplicationConstants;
import common.utilities.UtilityCommon;
import common.exceptions.DataOriginException;
import common.services.UtilityService;
import forms.tipo.abonos.cuentas.CuentasBancariasForm;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import mobiliario.iniciar_sesion;
import model.Abono;
import model.CategoriaContabilidad;
import model.Contabilidad;
import model.Cuenta;
import model.SubCategoriaContabilidad;
import services.AbonosService;
import services.AccountService;
import services.ContabilidadServices;



public final class ContabilidadForm extends javax.swing.JInternalFrame {
    private ContabilidadServices contabilidadServices = new ContabilidadServices();
    private final AbonosService abonosService = AbonosService.getInstance();
    public static AccountService accountService = new AccountService();
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
     public static boolean isContabilidadForm = false;
     private final UtilityService utilityService = UtilityService.getInstance();
    /**
     * Creates new form ContabilidadForm
     */
    public ContabilidadForm() {
        
        initComponents();
        llenar_categorias();
        formato_tabla_contabilidad();
        formato_tabla_pagos();
        formato_tabla_resumen();
        this.cmbSubCategoria.setEnabled(false);
        actionListener();
        llenar_combo_cuentas();

        
    }
    public void actionListener(){
        cmbCategoria.addActionListener((ActionEvent arg0) -> {
            llenar_sub_categorias();
        });
    }

    public void mostrar_cuentas_form(){
        isContabilidadForm = true;
        CuentasBancariasForm ventana = new CuentasBancariasForm(null, true);
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
    
    }
    
        public static void llenar_combo_cuentas(){
        
        List<Cuenta> list = accountService.getAccounts();
        
        cmbCuenta.removeAllItems();
        cmbCuenta.addItem(ApplicationConstants.CMB_SELECCIONE);
        if(list == null)
            return;
        for(Cuenta cuenta : list){
            cmbCuenta.addItem(cuenta.getDescripcion());
        }
        isContabilidadForm = false;
    }
    public void llenar_tabla_resumen(){
       this.formato_tabla_resumen();
       String initDate = new SimpleDateFormat("dd/MM/yyyy").format(txt_fecha_inicial.getDate());
       String endDate = new SimpleDateFormat("dd/MM/yyyy").format(txt_fecha_final.getDate());
       Timestamp tsInitDate = new Timestamp(txt_fecha_inicial.getDate().getTime()); 
        Timestamp tsEndDate = new Timestamp(txt_fecha_final.getDate().getTime()); 
       List<Cuenta> accountsList = accountService.getAccounts();
       
       List<Contabilidad> contabilidadList = new ArrayList<>();
       contabilidadList =  contabilidadServices.getAllContabilidadByDatesGroupByBankAccounts(tsInitDate,tsEndDate);
       List<Abono> abonosList;
       try {
        abonosList = abonosService.getAbonosByDatesGroupByBankAccounts(initDate, endDate);
       } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
         
        DefaultTableModel temp = (DefaultTableModel) tabla_resumen.getModel();
        for(Cuenta cuenta : accountsList){

            Object fila[] = {
               cuenta.getDescripcion(),
                "0",
                "0",
                "0"
             };

             temp.addRow(fila); 
        } // end for
        
        
        // ingresamos los abonos
    for(int j=0 ; j < tabla_resumen.getRowCount() ; j++){
        
        for(Abono abono : abonosList){
            if(abono.getTipoAbono().getCuenta().getDescripcion()
                    .equals(tabla_resumen.getValueAt(j, 0).toString()))
            {
                tabla_resumen.setValueAt(decimalFormat.format(abono.getTotalAbonos()), j, 3);
               
            }
                
        } // end for each
    }// end for
        
  // ingresos y egresos al resumen
        
    for(int j=0 ; j < tabla_resumen.getRowCount() ; j++){
        
        for(Contabilidad contabilidad : contabilidadList){
            if(contabilidad.getCuenta().getDescripcion()
                    .equals(tabla_resumen.getValueAt(j, 0).toString()))
            {
                tabla_resumen.setValueAt(decimalFormat.format(contabilidad.getTotalIngresos()), j, 1);
                tabla_resumen.setValueAt(decimalFormat.format(contabilidad.getTotalEgresos()), j, 2);
            }
                
        } // end for each
    }// end for
    
    // totales
    
        for(int j=0 ; j < tabla_resumen.getRowCount() ; j++){
            float ingresos = 0f;
            float egresos = 0f;
            float abonos = 0f;
            try {
                 ingresos = Float.parseFloat(tabla_resumen.getValueAt(j, 1).toString().replaceAll(",",""));
                 egresos = Float.parseFloat(tabla_resumen.getValueAt(j, 2).toString().replaceAll(",","")); 
                 abonos = Float.parseFloat(tabla_resumen.getValueAt(j, 3).toString().replaceAll(",","")); 
            } catch (Exception e) {
                System.out.println(e);
            }
            Float total = (ingresos+abonos) - egresos;
            tabla_resumen.setValueAt( decimalFormat.format(total) , j, 4);

        }
    
    }
    public void llenar_categorias(){
        List<CategoriaContabilidad> list = contabilidadServices.getAllCategoriasContabilidad();
        this.cmbCategoria.removeAllItems();
        this.cmbCategoria.addItem(ApplicationConstants.CMB_SELECCIONE);
        for(CategoriaContabilidad categoria : list){
            this.cmbCategoria.addItem(categoria.getDescripcion());
        }
    }
    
     public void mostrar_categorias_contabilidad() {
        CategoriaContabilidadForm ventana = new CategoriaContabilidadForm(null, true);
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
    }
     
      public void mostrar_sub_categorias_contabilidad() {
        SubCategoriaContabilidadForm ventana = new SubCategoriaContabilidadForm(null, true);
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
    }
      
  
    
    public void llenar_sub_categorias(){
        String categoria = this.cmbCategoria.getSelectedItem().toString();
        if(categoria == null 
                || categoria.isEmpty()
                || categoria.equals("")
                )
        {
            JOptionPane.showMessageDialog(null, "No se recibio el parametro correctamente :( ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if(categoria.equals(ApplicationConstants.CMB_SELECCIONE)){
            this.cmbSubCategoria.removeAllItems();
            this.cmbSubCategoria.addItem(ApplicationConstants.CMB_SELECCIONE);
            this.cmbSubCategoria.setEnabled(false);
        }
        CategoriaContabilidad category = contabilidadServices.getCategoryByName(categoria);
        if(category == null)
            return;
        List<SubCategoriaContabilidad> list = 
                contabilidadServices.getAllSubCategoriasContabilidadByCategoriaId(category.getCategoriaContabilidadId());
        
        if(list == null || list.isEmpty() || list.size()<=0 )
            return;
        
        this.cmbSubCategoria.removeAllItems();
        this.cmbSubCategoria.addItem(ApplicationConstants.CMB_SELECCIONE);
        for(SubCategoriaContabilidad sub : list){
            String dsIngreso = null;
            if(sub.getIngreso().equals("1"))
                dsIngreso = " (+)";
            else
                dsIngreso = " (-)";
            this.cmbSubCategoria.addItem(sub.getDescripcion()+dsIngreso);
        }
        this.cmbSubCategoria.setEnabled(true);
        
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
        jLabel2 = new javax.swing.JLabel();
        cmbSubCategoria = new javax.swing.JComboBox();
        cmbCategoria = new javax.swing.JComboBox<>();
        txtComentario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnRegistrar = new javax.swing.JButton();
        lblAgregarCategoria = new javax.swing.JLabel();
        lblAgregarSubCategoria = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        cmbCuenta = new javax.swing.JComboBox();
        lblDescripcion = new javax.swing.JLabel();
        lblAgregarCuenta = new javax.swing.JLabel();
        txt_fecha_movimiento = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txt_fecha_inicial = new com.toedter.calendar.JDateChooser();
        txt_fecha_final = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        btnBusqueda = new javax.swing.JButton();
        lblSummaryIngresos = new javax.swing.JLabel();
        btnExportExcel = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_pagos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_contabilidad = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabla_resumen = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};

        setClosable(true);
        setTitle("Contabilidad");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registra ingreso o egreso", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Categoria:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 24, -1, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Sub categoria:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(184, 24, -1, -1));

        cmbSubCategoria.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbSubCategoria.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSubCategoria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbSubCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbSubCategoriaMouseClicked(evt);
            }
        });
        jPanel1.add(cmbSubCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 40, 166, 20));

        cmbCategoria.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCategoria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbCategoria.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCategoriaItemStateChanged(evt);
            }
        });
        cmbCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoriaActionPerformed(evt);
            }
        });
        jPanel1.add(cmbCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 166, 20));

        txtComentario.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtComentario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtComentarioActionPerformed(evt);
            }
        });
        jPanel1.add(txtComentario, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 40, 260, 20));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Fecha movimiento:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 20, 160, -1));

        btnRegistrar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnRegistrar.setText("Registrar");
        btnRegistrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegistrar.setDefaultCapable(false);
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });
        jPanel1.add(btnRegistrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 40, -1, 20));

        lblAgregarCategoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lblAgregarCategoria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAgregarCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAgregarCategoriaMouseClicked(evt);
            }
        });
        lblAgregarCategoria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblAgregarCategoriaKeyPressed(evt);
            }
        });
        jPanel1.add(lblAgregarCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, -1, -1));

        lblAgregarSubCategoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lblAgregarSubCategoria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAgregarSubCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAgregarSubCategoriaMouseClicked(evt);
            }
        });
        lblAgregarSubCategoria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblAgregarSubCategoriaKeyPressed(evt);
            }
        });
        jPanel1.add(lblAgregarSubCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 20, -1, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Cantidad:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 20, -1, -1));

        txtCantidad.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadActionPerformed(evt);
            }
        });
        jPanel1.add(txtCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 40, 70, 20));

        cmbCuenta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbCuenta.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCuenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbCuenta.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCuentaItemStateChanged(evt);
            }
        });
        cmbCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCuentaActionPerformed(evt);
            }
        });
        jPanel1.add(cmbCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 40, 170, 20));

        lblDescripcion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblDescripcion.setText("Cuenta:");
        jPanel1.add(lblDescripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 20, 70, -1));

        lblAgregarCuenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lblAgregarCuenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAgregarCuenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAgregarCuentaMouseClicked(evt);
            }
        });
        lblAgregarCuenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblAgregarCuentaKeyPressed(evt);
            }
        });
        jPanel1.add(lblAgregarCuenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 20, -1, -1));

        txt_fecha_movimiento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_fecha_movimiento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txt_fecha_movimientoMouseClicked(evt);
            }
        });
        txt_fecha_movimiento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_fecha_movimientoKeyPressed(evt);
            }
        });
        jPanel1.add(txt_fecha_movimiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 40, 160, 21));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Comentario:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, -1, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Busqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_fecha_inicial.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_fecha_inicial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txt_fecha_inicialMouseClicked(evt);
            }
        });
        txt_fecha_inicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_fecha_inicialKeyPressed(evt);
            }
        });
        jPanel2.add(txt_fecha_inicial, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 37, 169, 21));

        txt_fecha_final.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_fecha_final.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txt_fecha_finalMouseClicked(evt);
            }
        });
        txt_fecha_final.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_fecha_finalKeyPressed(evt);
            }
        });
        jPanel2.add(txt_fecha_final, new org.netbeans.lib.awtextra.AbsoluteConstraints(193, 37, 160, 21));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Fecha inicial y final:");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 18, 111, -1));

        btnBusqueda.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnBusqueda.setText("Realizar busqueda");
        btnBusqueda.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBusqueda.setDefaultCapable(false);
        btnBusqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBusquedaActionPerformed(evt);
            }
        });
        jPanel2.add(btnBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(365, 38, -1, 20));

        lblSummaryIngresos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jPanel2.add(lblSummaryIngresos, new org.netbeans.lib.awtextra.AbsoluteConstraints(462, 15, 605, 16));

        btnExportExcel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnExportExcel.setText("Exportar a Excel");
        btnExportExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportExcel.setDefaultCapable(false);
        btnExportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportExcelActionPerformed(evt);
            }
        });
        jPanel2.add(btnExportExcel, new org.netbeans.lib.awtextra.AbsoluteConstraints(506, 38, -1, 20));

        btnEliminar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnEliminar.setText("Eliminar registro");
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.setDefaultCapable(false);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        jPanel2.add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(637, 38, -1, 20));

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabla_pagos.setFont(new java.awt.Font("Arial", 0, 9)); // NOI18N
        tabla_pagos.setModel(new javax.swing.table.DefaultTableModel(
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
        tabla_pagos.setToolTipText("");
        jScrollPane2.setViewportView(tabla_pagos);

        jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 5, 1130, 330));

        jTabbedPane1.addTab("Pagos", jPanel4);

        tabla_contabilidad.setFont(new java.awt.Font("Arial", 0, 9)); // NOI18N
        tabla_contabilidad.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tabla_contabilidad);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Ingresos/Egresos", jPanel5);

        jPanel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        tabla_resumen.setFont(new java.awt.Font("Arial", 0, 9)); // NOI18N
        tabla_resumen.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tabla_resumen);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Resumen", jPanel3);

        jPanel2.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 1160, 400));
        jTabbedPane1.getAccessibleContext().setAccessibleName("Cuentas");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1178, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtComentarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtComentarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtComentarioActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        // TODO add your handling code here:
        String cuenta = this.cmbCuenta.getSelectedItem().toString();
        // insertar
        if( (cuenta == null || cuenta.equals(ApplicationConstants.CMB_SELECCIONE)))
        {
            JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_MISSING_PARAMETERS, "Error", JOptionPane.INFORMATION_MESSAGE);
            return ;
        }
        String subCategoria = this.cmbSubCategoria.getSelectedItem().toString().replace(" (+)", "").replace(" (-)", "");
        StringBuilder message = new StringBuilder();
        
        if(!UtilityCommon.validateComboBoxDataValue(subCategoria))
            message.append("Falta dato de sub categoria\n");
             
        if(!UtilityCommon.validateAmount(this.txtCantidad.getText().toString()))
           message.append("Error en cantidad, porfavor verifica el dato\n");
        
        Cuenta account = accountService.getAccountByDescription(cuenta);
        if(account == null)
        {
         JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_NOT_PARAMETER_RECEIVED, "Error", JOptionPane.INFORMATION_MESSAGE);
         return;
        }
        
        if(!message.toString().equals("")){
            JOptionPane.showMessageDialog(null, message.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
         Timestamp movementDate = new Timestamp(txt_fecha_movimiento.getDate().getTime());
        
         Float cantidad = Float.parseFloat(txtCantidad.getText().toString());
        
         SubCategoriaContabilidad subCategoriaContabilidad = 
                contabilidadServices.getSubCategoryByName(subCategoria);
        
        Contabilidad contabilidad = new Contabilidad();
        contabilidad.setUsuario(iniciar_sesion.usuarioGlobal);
        contabilidad.setSubCategoriaContabilidad(subCategoriaContabilidad);
        contabilidad.setComentario(txtComentario.getText().toString());
        contabilidad.setCantidad(cantidad);
        contabilidad.setFechaMovimiento(movementDate);
        contabilidad.setCuenta(account);
        
        contabilidadServices.save(contabilidad);
        
        JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_SAVE_SUCCESSFUL, "Successful", JOptionPane.INFORMATION_MESSAGE);
        clean_save_form();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    public void clean_save_form(){
        this.txtCantidad.setText("");
        this.txtComentario.setText("");
        this.cmbSubCategoria.removeAllItems();
        this.cmbSubCategoria.addItem(ApplicationConstants.CMB_SELECCIONE);
        this.cmbSubCategoria.setEnabled(false);
        
    }
    private void lblAgregarCategoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAgregarCategoriaMouseClicked
        // TODO add your handling code here:
        this.mostrar_categorias_contabilidad();
    }//GEN-LAST:event_lblAgregarCategoriaMouseClicked

    private void lblAgregarCategoriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblAgregarCategoriaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblAgregarCategoriaKeyPressed

    private void lblAgregarSubCategoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAgregarSubCategoriaMouseClicked
        // TODO add your handling code here:
         if(this.cmbCategoria.getSelectedItem().equals(ApplicationConstants.CMB_SELECCIONE))
        {
             JOptionPane.showMessageDialog(null, "Debe seleccionar una categoria para agregar una sub categoria", "Error", JOptionPane.INFORMATION_MESSAGE);
             return;
        }
        this.cmbSubCategoria.setEnabled(false);
        this.mostrar_sub_categorias_contabilidad();
       
    }//GEN-LAST:event_lblAgregarSubCategoriaMouseClicked

    private void lblAgregarSubCategoriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblAgregarSubCategoriaKeyPressed
        // TODO add your handling code here:
       
        
    }//GEN-LAST:event_lblAgregarSubCategoriaKeyPressed

    private void cmbCategoriaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCategoriaItemStateChanged
        // TODO add your handling code here:
        
    }//GEN-LAST:event_cmbCategoriaItemStateChanged

    private void cmbSubCategoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbSubCategoriaMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_cmbSubCategoriaMouseClicked

    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoriaActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_cmbCategoriaActionPerformed

    private void txtCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadActionPerformed

    private void txt_fecha_inicialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_fecha_inicialMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fecha_inicialMouseClicked

    private void txt_fecha_inicialKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_fecha_inicialKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fecha_inicialKeyPressed

    private void txt_fecha_finalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_fecha_finalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fecha_finalMouseClicked

    private void txt_fecha_finalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_fecha_finalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fecha_finalKeyPressed

    public void formato_tabla_contabilidad(){
    Object[][] data = {{"", "", "", "", "","","","","","",""}};
        String[] columnNames = {"Id", "id sub categoria", "Categoria", "Sub Categoria","Ingreso/Egreso", "Usuario", "Fecha registro","Fecha movimiento","Comentario","Cantidad","Cuenta"};       
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tabla_contabilidad.setModel(tableModel);
        tabla_contabilidad.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 10));
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tabla_contabilidad.setRowSorter(ordenarTabla);
        int[] anchos = {10, 10, 100, 100,100, 100, 120,120,140,100,400};

        for (int inn = 0; inn < tabla_contabilidad.getColumnCount(); inn++) {
            tabla_contabilidad.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }        

        DefaultTableCellRenderer cellRight = new DefaultTableCellRenderer();
        cellRight.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_contabilidad.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        tabla_contabilidad.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_contabilidad.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_contabilidad.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        tabla_contabilidad.getColumnModel().getColumn(1).setMaxWidth(0);
        tabla_contabilidad.getColumnModel().getColumn(1).setMinWidth(0);
        tabla_contabilidad.getColumnModel().getColumn(1).setPreferredWidth(0);
        
        tabla_contabilidad.getColumnModel().getColumn(5).setCellRenderer(centrar);
        tabla_contabilidad.getColumnModel().getColumn(4).setCellRenderer(centrar);
         tabla_contabilidad.getColumnModel().getColumn(9).setCellRenderer(cellRight);
    
    }
    
    public void formato_tabla_pagos(){
    Object[][] data = {{"", "", "", "", "","","","",""}};
        String[] columnNames = {"id_abonos", "id_renta", "Folio", "Cantidad","Usuario", "Fecha", "Fecha pago","Tipo","Cuenta"};       
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tabla_pagos.setModel(tableModel);
       
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tabla_pagos.setRowSorter(ordenarTabla);
        int[] anchos = {10, 10, 50, 100, 300, 150,150,400,400};

            tabla_pagos.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 10));
        for (int inn = 0; inn < tabla_pagos.getColumnCount(); inn++) {
            tabla_pagos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }        

      DefaultTableCellRenderer cellRight = new DefaultTableCellRenderer();
      cellRight.setHorizontalAlignment(SwingConstants.RIGHT);
//
//        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
//        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_pagos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        tabla_pagos.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_pagos.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_pagos.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        tabla_pagos.getColumnModel().getColumn(1).setMaxWidth(0);
        tabla_pagos.getColumnModel().getColumn(1).setMinWidth(0);
        tabla_pagos.getColumnModel().getColumn(1).setPreferredWidth(0);
        
        tabla_pagos.getColumnModel().getColumn(3).setCellRenderer(cellRight);
//        tabla_pagos.getColumnModel().getColumn(4).setCellRenderer(centrar);
    
    }
    
     public void formato_tabla_resumen(){
        Object[][] data = {{"", "", "", "",""}};
        String[] columnNames = {"Cuenta", "Ingresos", "Egresos", "Pagos","Total"};       
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tabla_resumen.setModel(tableModel);
        
         DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
       
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tabla_resumen.setRowSorter(ordenarTabla);
        
        int[] anchos = {400, 100, 100, 100, 100,100};

            tabla_resumen.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 10));
        for (int inn = 0; inn < tabla_resumen.getColumnCount(); inn++) {
            tabla_resumen.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }        


        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_resumen.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        tabla_resumen.getColumnModel().getColumn(1).setCellRenderer(right);
        tabla_resumen.getColumnModel().getColumn(2).setCellRenderer(right);
        tabla_resumen.getColumnModel().getColumn(3).setCellRenderer(right);
        tabla_resumen.getColumnModel().getColumn(4).setCellRenderer(right);
    
    }
    
    private void btnBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBusquedaActionPerformed
        // TODO add your handling code here:
        this.llenar_tabla_general();
        this.llenar_tabla_pagos();
        this.llenar_tabla_resumen();
        
    }//GEN-LAST:event_btnBusquedaActionPerformed

    public boolean validate_date_search(){
        String initDate = new SimpleDateFormat("dd/MM/yyyy").format(txt_fecha_inicial.getDate());
        String endDate = new SimpleDateFormat("dd/MM/yyyy").format(txt_fecha_final.getDate());
       
       
        if(initDate == null || initDate.isEmpty() || initDate.equals("")
                || endDate == null || endDate.isEmpty() || endDate.equals("")
                )
        {
             return false;
             
        }
        return true;

    }
    public void llenar_tabla_general(){
       this.formato_tabla_contabilidad();
        if(!this.validate_date_search()){
             JOptionPane.showMessageDialog(null, "Error al obtener las fechas\nporfavor verifique fecha inicial y final esten de manera correcta", "Error", JOptionPane.INFORMATION_MESSAGE);
             return;
        }
        Timestamp tsInitDate = new Timestamp(txt_fecha_inicial.getDate().getTime()); 
        Timestamp tsEndDate = new Timestamp(txt_fecha_final.getDate().getTime()); 
        
        List<Contabilidad> list = new ArrayList<>();
        list =  contabilidadServices.getAllContabilidadByDates(tsInitDate,tsEndDate);
        
        if(list == null || list.isEmpty() || list.size()<=0)
            return;
        
        
        
        DefaultTableModel temp = (DefaultTableModel) tabla_contabilidad.getModel();
        for(Contabilidad contabilidad : list){
         String dsIngreso = null;
         if(contabilidad.getSubCategoriaContabilidad().getIngreso().equals("1"))
         {
             dsIngreso = "(+) Ingreso";
         }
         else
         {
             dsIngreso = "(-) Egreso";
         }
        Object fila[] = {
             contabilidad.getContabilidadId(),
             contabilidad.getSubCategoriaContabilidad().getSubCategoriaContabilidadId(),
             contabilidad.getSubCategoriaContabilidad().getCategoriaContabilidad().getDescripcion(),
             contabilidad.getSubCategoriaContabilidad().getDescripcion(),
             dsIngreso,
             contabilidad.getUsuario().getNombre()+" "+contabilidad.getUsuario().getApellidos(),
             contabilidad.getFechaRegistro(),
             contabilidad.getFechaMovimiento(),
             contabilidad.getComentario(),
             decimalFormat.format(contabilidad.getCantidad()),
             contabilidad.getCuenta().getDescripcion()
         };
        
         temp.addRow(fila); 
        } // end for
        
    }
    
    public void llenar_tabla_pagos(){
        this.formato_tabla_pagos();
        if(!this.validate_date_search()){
             JOptionPane.showMessageDialog(null, "Error al obtener las fechas\nporfavor verifique fecha inicial y final esten de manera correcta", "Error", JOptionPane.INFORMATION_MESSAGE);
             return;
        }
        String initDate = new SimpleDateFormat("dd/MM/yyyy").format(txt_fecha_inicial.getDate());
        String endDate = new SimpleDateFormat("dd/MM/yyyy").format(txt_fecha_final.getDate());
       
        
        List<Abono> list;
        try {
            list =  abonosService.getAbonosByDates(initDate,endDate);
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if(list == null || list.isEmpty() || list.size()<=0)
            return;
        
        
        DefaultTableModel temp = (DefaultTableModel) tabla_pagos.getModel();
        for(Abono abono : list){

            Object fila[] = {
                abono.getAbonoId(),
                abono.getRenta().getRentaId(),
                abono.getRenta().getRentaId(),
                decimalFormat.format(abono.getAbono()),
                abono.getUsuario().getNombre()+" "+abono.getUsuario().getApellidos(),
                abono.getFecha(),
                abono.getFechaPago(),
                abono.getTipoAbono().getDescripcion(),
                abono.getTipoAbono().getCuenta().getDescripcion()
             };

             temp.addRow(fila); 
        } // end for
        
    }
    private void btnExportExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportExcelActionPerformed
        // TODO add your handling code here:
        utilityService.exportarExcel(this.tabla_contabilidad);
    }//GEN-LAST:event_btnExportExcelActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        if (!iniciar_sesion.usuarioGlobal.getAdministrador().equals("1")) {
             JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_NOT_PERMISIONS_ADMIN, "Error", JOptionPane.INFORMATION_MESSAGE);
             return;
        }
        if (tabla_contabilidad.getSelectedRow() == -1) {
             JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar", "Error", JOptionPane.INFORMATION_MESSAGE);
             return;
        }
        
        int seleccion = JOptionPane.showOptionDialog(this, "¿Eliminar registro: " + 
                (String.valueOf(tabla_contabilidad.getValueAt(tabla_contabilidad.getSelectedRow(), 8))) + "?", "Confirme eliminacion", 
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "No");
        if ((seleccion + 1) == 1) {
            int row = tabla_contabilidad.getSelectedRow();
            String contabilidadId  = tabla_contabilidad.getValueAt(row, 0).toString();
            Contabilidad contabilidad = 
                    contabilidadServices.getContabilidadById(Integer.parseInt(contabilidadId));
            
            if(contabilidad == null){
                JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_NOT_PARAMETER_RECEIVED, "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            contabilidadServices.deleteContabilidadById(contabilidad.getContabilidadId());
            
            JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_DELETE_SUCCESSFUL, "Error", JOptionPane.INFORMATION_MESSAGE);
            this.formato_tabla_contabilidad();
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void cmbCuentaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbCuentaItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCuentaItemStateChanged

    private void cmbCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCuentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCuentaActionPerformed

    private void lblAgregarCuentaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAgregarCuentaMouseClicked
        // TODO add your handling code here:
        this.mostrar_cuentas_form();
    }//GEN-LAST:event_lblAgregarCuentaMouseClicked

    private void lblAgregarCuentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblAgregarCuentaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblAgregarCuentaKeyPressed

    private void txt_fecha_movimientoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_fecha_movimientoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fecha_movimientoMouseClicked

    private void txt_fecha_movimientoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_fecha_movimientoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fecha_movimientoKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBusqueda;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnExportExcel;
    private javax.swing.JButton btnRegistrar;
    public static javax.swing.JComboBox<String> cmbCategoria;
    public static javax.swing.JComboBox cmbCuenta;
    private javax.swing.JComboBox cmbSubCategoria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblAgregarCategoria;
    private javax.swing.JLabel lblAgregarCuenta;
    private javax.swing.JLabel lblAgregarSubCategoria;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblSummaryIngresos;
    private javax.swing.JTable tabla_contabilidad;
    private javax.swing.JTable tabla_pagos;
    private javax.swing.JTable tabla_resumen;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtComentario;
    private com.toedter.calendar.JDateChooser txt_fecha_final;
    private com.toedter.calendar.JDateChooser txt_fecha_inicial;
    private com.toedter.calendar.JDateChooser txt_fecha_movimiento;
    // End of variables declaration//GEN-END:variables
}
