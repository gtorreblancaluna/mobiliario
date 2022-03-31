/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobiliario;

import forms.rentas.ConsultarRentas;
import services.SaleService;
import clases.sqlclass;
import java.awt.Toolkit;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.Articulo;
import model.DetalleRenta;
import model.Faltante;
import model.Renta;
import services.ItemService;
import services.SystemService;

public class VerFaltantes extends java.awt.Dialog {

    private final sqlclass funcion = new sqlclass();
    
    Object[][] dtconduc;      
    private final SaleService saleService;
    private final SystemService systemService = SystemService.getInstance();
    ItemService itemService = ItemService.getInstance();
    public static String g_articuloId;
    public static String g_rentaId;
    public static String g_cantidadEnPedido;
    
    /** Encabezados de la tabla FALTANTES */
    public static int HD_FALTANTES_ID_FALTANTE = 0;
    public static int HD_FALTANTES_ID_ARTICULO = 1;
    public static int HD_FALTANTES_CANTIDAD = 2;
    public static int HD_FALTANTES_PRECIO_COBRAR = 3;
    public static int HD_FALTANTES_DESCRIPCION_FALTANTE = 4;
    public static int HD_FALTANTES_ARTICULO = 5;
    public static int HD_FALTANTES_USUARIO = 6;
    public static int HD_FALTANTES_FECHA_REGISTRO = 7;
    public static int HD_FALTANTES_COMENTARIO = 8;
    
    /** Encabezados de la tabla FALTANTES */
    public static int HD_ARTICULOS_ID_ARTICULO = 0;
    public static int HD_ARTICULOS_CANTIDAD_PEDIDO = 1;
    public static int HD_ARTICULOS_DESCRIPCION_ARTICULO = 2;
    public static int HD_ARTICULOS_PRECIO_COBRAR = 3;
   
    
   
    
    public String conviertemoneda(String valor) {

        DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');

        float entero = Float.parseFloat(valor);
        DecimalFormat formateador = new DecimalFormat("###,###.##", simbolo);
        String entero2 = formateador.format(entero);

        if (entero2.contains(".")) {
            entero2 = "$" + entero2;

        } else {
            entero2 = "$" + entero2 + ".00";
        }

        return entero2;

    }
    public VerFaltantes(java.awt.Frame parent, boolean modal) {
        super(parent, modal);        
        initComponents();
        saleService = SaleService.getInstance();
        funcion.conectate();
        this.setLocationRelativeTo(null);
        this.lblQuitarElemento.setText("");
        this.setTitle("Ver faltantes/reparacion/devoluciones ");
        formato_tabla_faltantes();
        formato_tabla_articulos();      
        llenar_tabla_articulos();
        llenar_tabla_faltantes();
        this.txtPrecioCobrar.setEnabled(false);
        this.txtCantidad.setEnabled(false);
        this.txtComentario.setEnabled(false);
    }
    
    public void llenar_tabla_articulos(){
        
        if(ConsultarRentas.g_idRenta != null && !ConsultarRentas.g_idRenta.equals("")){
             g_rentaId = ConsultarRentas.g_idRenta;
        }
        else if (VerFoliosPorArticulo.g_rentaId !=null && !VerFoliosPorArticulo.g_rentaId.equals("")){
             g_rentaId = VerFoliosPorArticulo.g_rentaId;
        }
        else{
             g_rentaId = AsignarFaltante.g_rentaId+"";
        }
        Renta renta = null;
        try{
            renta = saleService.obtenerRentaPorIdSinSumas(Integer.parseInt(g_rentaId));
        } catch (Exception e) {
           Logger.getLogger(VerFaltantes.class.getName()).log(Level.SEVERE, null, e);
           JOptionPane.showMessageDialog(null, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
           return;
       }
         DefaultTableModel tablaDetalle = (DefaultTableModel) tablaArticulos.getModel();
         this.lblInformacionInicial.setText("FOLIO: "+renta.getFolio());
         int itemId = 0;
         if(AsignarFaltante.g_articuloId > 0)
             itemId = AsignarFaltante.g_articuloId;
//         else if(VerFoliosPorArticulo.g_articuloId > 0)
            else
             itemId = VerFoliosPorArticulo.g_articuloId;
         if(renta.getRentaId() == 0 && itemId > 0 )
         {
             Articulo articulo = null;
             
             try {
                articulo = itemService.obtenerArticuloPorId( funcion , itemId);
           } catch (Exception e) {
               Logger.getLogger(VerFaltantes.class.getName()).log(Level.SEVERE, null, e);
               JOptionPane.showMessageDialog(null, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
               return;
           }
              Object fila[] = {                                          
                        articulo.getArticuloId()+"",   
                        "",
                        articulo.getDescripcion()+" "+articulo.getColor().getColor(),
                        articulo.getPrecioCompra()+""
                    };
                    tablaDetalle.addRow(fila);
         }else{
            for(DetalleRenta detalle : renta.getDetalleRenta()){
                    Object fila[] = {                                          
                        detalle.getArticulo().getArticuloId()+"",   
                        detalle.getCantidad()+"",
                        detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor(), 
                        detalle.getArticulo().getPrecioCompra()
                    };
                    tablaDetalle.addRow(fila);
            }
         }
    }
    
    public void llenar_tabla_faltantes(){        
            
        List<Faltante> faltantes = null;
        
        
        try {
             faltantes = itemService.obtenerFaltantesPorRentaId(funcion, Integer.parseInt(g_rentaId));
        } catch (Exception e) {
            Logger.getLogger(VerFaltantes.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            return;
        }
        
        if(faltantes == null || faltantes.size()<=0)
            return;
        
        DefaultTableModel tablaDetalle = (DefaultTableModel) tablaFaltantes.getModel();
        for(Faltante faltante : faltantes){
            
            String descripcionFaltante = null;
            
            if(faltante.getFgFaltante() == 1  && faltante.getFgAccidenteTrabajo() == 0 && faltante.getFgDevolucion() == 0)
                descripcionFaltante = ApplicationConstants.DS_FALTANTES_FALTANTE;
            else if(faltante.getFgFaltante() == 0 && faltante.getFgDevolucion() == 0 && faltante.getFgAccidenteTrabajo() == 0)
                descripcionFaltante = ApplicationConstants.DS_FALTANTES_REPARACION;
            else if(faltante.getFgFaltante() == 0 && faltante.getFgDevolucion() == 1 && faltante.getFgAccidenteTrabajo() == 0)
                descripcionFaltante = ApplicationConstants.DS_FALTANTES_DEVOLUCION;
            else if(faltante.getFgFaltante() == 0 && faltante.getFgDevolucion() == 0 && faltante.getFgAccidenteTrabajo() == 1)
                descripcionFaltante = ApplicationConstants.DS_FALTANTES_ACCIDENTE;
            
            
            Object fila[] = {
                faltante.getFaltanteId()+"",
                faltante.getArticulo().getArticuloId(),
                faltante.getCantidad()+"",
                faltante.getPrecioCobrar()+"",
                descripcionFaltante,
                faltante.getArticulo().getDescripcion()+" "+faltante.getArticulo().getColor().getColor(),
                faltante.getUsuario().getNombre()+" "+faltante.getUsuario().getApellidos(),
                faltante.getFechaRegistro(),
                faltante.getComentario()
                
            };
            tablaDetalle.addRow(fila);
            
        }
    
    }
    
     public void formato_tabla_faltantes() {
        Object[][] data = {{"","","","", "", "", "","","" }};
        String[] columnNames = {"id_faltante","id_articulo", "cantidad", "precio cobrar","faltante/devolucion","articulo","usuario","fecha registro","comentario"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaFaltantes.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablaFaltantes.setRowSorter(ordenarTabla);

        int[] anchos = {20,20, 40,40, 80, 80,80, 80,80};

        for (int inn = 0; inn < tablaFaltantes.getColumnCount(); inn++) {
            tablaFaltantes.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaFaltantes.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tablaFaltantes.getColumnModel().getColumn(HD_FALTANTES_ID_FALTANTE).setMaxWidth(0);
        tablaFaltantes.getColumnModel().getColumn(HD_FALTANTES_ID_FALTANTE).setMinWidth(0);
        tablaFaltantes.getColumnModel().getColumn(HD_FALTANTES_ID_FALTANTE).setPreferredWidth(0);
        
        tablaFaltantes.getColumnModel().getColumn(HD_FALTANTES_ID_ARTICULO).setMaxWidth(0);
        tablaFaltantes.getColumnModel().getColumn(HD_FALTANTES_ID_ARTICULO).setMinWidth(0);
        tablaFaltantes.getColumnModel().getColumn(HD_FALTANTES_ID_ARTICULO).setPreferredWidth(0);

        tablaFaltantes.getColumnModel().getColumn(HD_FALTANTES_ARTICULO).setCellRenderer(centrar);
        tablaFaltantes.getColumnModel().getColumn(HD_FALTANTES_CANTIDAD).setCellRenderer(centrar);
        tablaFaltantes.getColumnModel().getColumn(HD_FALTANTES_PRECIO_COBRAR).setCellRenderer(centrar);

    }
     
     public void formato_tabla_articulos() {
        Object[][] data = {{"", "","",""}};
        String[] columnNames = {"id_articulo","cantidad", "descripcion","precio compra"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaArticulos.setModel(tableModel);
        
        // Instanciamos el TableRowSorter y lo añadimos al JTable
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablaArticulos.setRowSorter(ordenarTabla);

        int[] anchos = {20, 40,120,40};

        for (int inn = 0; inn < tablaArticulos.getColumnCount(); inn++) {
            tablaArticulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tablaArticulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setPreferredWidth(0);
        tablaArticulos.getColumnModel().getColumn(1).setCellRenderer(centrar);
       

    }
    
    public void agregar() {
        
    }

    public void guardar() {
      
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaFaltantes = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jButton1 = new javax.swing.JButton();
        lblQuitarElemento = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaArticulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtArticulo = new javax.swing.JTextField();
        txtPrecioCobrar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        radioReparacion = new javax.swing.JRadioButton();
        radioDevolucion = new javax.swing.JRadioButton();
        txtComentario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        radioFaltante = new javax.swing.JRadioButton();
        btnAgregar = new javax.swing.JButton();
        radioAccidenteTrabajo = new javax.swing.JRadioButton();
        txtCantidad = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lblEncontrados = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lblInformacionInicial = new javax.swing.JLabel();

        setLocationRelativeTo(lblQuitarElemento);
        setTitle("Faltantes/Devolucion");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Faltantes / Devoluciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        tablaFaltantes.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tablaFaltantes.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaFaltantes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaFaltantes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaFaltantesMouseClicked(evt);
            }
        });
        tablaFaltantes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tablaFaltantesKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaFaltantes);

        jButton1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton1.setText("(-) quitar elemento");
        jButton1.setToolTipText("Elimina el elemento de la bd");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblQuitarElemento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblQuitarElemento.setToolTipText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 596, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblQuitarElemento, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(172, 172, 172)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQuitarElemento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 150, 620, 460));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Articulos del pedido", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        tablaArticulos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tablaArticulos.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaArticulos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaArticulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaArticulosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaArticulos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 596, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 620, 430));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Agregar faltante / devolucion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Articulo:");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 60, -1));
        jLabel1.getAccessibleContext().setAccessibleName("asd");

        txtArticulo.setEditable(false);
        txtArticulo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jPanel4.add(txtArticulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 170, -1));

        txtPrecioCobrar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtPrecioCobrar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPrecioCobrarKeyPressed(evt);
            }
        });
        jPanel4.add(txtPrecioCobrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, 80, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Precio a cobrar:");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 90, -1));

        buttonGroup1.add(radioReparacion);
        radioReparacion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        radioReparacion.setText("Reparación");
        radioReparacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        radioReparacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioReparacionActionPerformed(evt);
            }
        });
        jPanel4.add(radioReparacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, 120, 20));

        buttonGroup1.add(radioDevolucion);
        radioDevolucion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        radioDevolucion.setText("Devolución");
        radioDevolucion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel4.add(radioDevolucion, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 70, 90, 20));

        txtComentario.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtComentario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtComentarioKeyPressed(evt);
            }
        });
        jPanel4.add(txtComentario, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 40, 340, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Comentario:");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, 60, -1));

        buttonGroup1.add(radioFaltante);
        radioFaltante.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        radioFaltante.setSelected(true);
        radioFaltante.setText("Faltante");
        radioFaltante.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        radioFaltante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioFaltanteActionPerformed(evt);
            }
        });
        jPanel4.add(radioFaltante, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 110, 20));

        btnAgregar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnAgregar.setText("(+) agregar");
        btnAgregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        jPanel4.add(btnAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 40, 140, 24));

        buttonGroup1.add(radioAccidenteTrabajo);
        radioAccidenteTrabajo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        radioAccidenteTrabajo.setText("Accidente de trabajo");
        radioAccidenteTrabajo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel4.add(radioAccidenteTrabajo, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 70, 200, 20));

        txtCantidad.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCantidadKeyPressed(evt);
            }
        });
        jPanel4.add(txtCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 40, 70, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Cantidad:");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 60, -1));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 1250, 100));

        lblEncontrados.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jPanel2.add(lblEncontrados, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 580, 530, 20));

        lblInformacionInicial.setToolTipText("");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInformacionInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(518, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(lblInformacionInicial, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1100, 40));

        add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
//        inventario.validar_colores = true;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void radioReparacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioReparacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioReparacionActionPerformed

    private void tablaArticulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaArticulosMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            this.txtPrecioCobrar.setEnabled(true);
            this.txtCantidad.setEnabled(true);
            this.txtComentario.setEnabled(true);
            String artId = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_ID_ARTICULO).toString();
            String descripcion = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_DESCRIPCION_ARTICULO).toString();
            String precioCobrar = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_PRECIO_COBRAR).toString();
            this.g_cantidadEnPedido = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_CANTIDAD_PEDIDO).toString();
            if(this.g_cantidadEnPedido == null || this.g_cantidadEnPedido.equals(""))
                this.g_cantidadEnPedido = "0";
            this.g_articuloId = artId;
            this.txtArticulo.setText(descripcion);
            this.txtCantidad.requestFocus(); 
            this.txtPrecioCobrar.setText(precioCobrar);
            
        }
    }//GEN-LAST:event_tablaArticulosMouseClicked

    private void radioFaltanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioFaltanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioFaltanteActionPerformed

    public void agregar_faltante(){
        
        StringBuilder mensaje = new StringBuilder();
        int cont = 0;
        
        if(txtArticulo.getText().equals(""))
            mensaje.append(++cont + ". Debes elegir un articulo para agregar el faltante\n");

        float cantidad = 0f;
        float cantidadPedido = 0f;
        float precioCobrar = 0f;
        
        try {
            cantidad = new Float(this.txtCantidad.getText());
            cantidadPedido = new Float(g_cantidadEnPedido);
            precioCobrar = new Float(this.txtPrecioCobrar.getText());
        } catch (NumberFormatException e) {
            mensaje.append(++cont + ". Error al ingresar la cantidad\n");
        } catch (Exception e) {
            mensaje.append(++cont + ". Error al ingresar la cantidad\n");
        }
        
        if(!this.g_rentaId.equals("0") && (cantidad <= 0 || cantidad > cantidadPedido))
            mensaje.append(++cont + ". La cantidad debe ser mayor a cero y menor a la cantidad del pedido\n");
        
        if(!mensaje.toString().equals("")){
            JOptionPane.showMessageDialog(null, mensaje+"", "Error", JOptionPane.INFORMATION_MESSAGE);            
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        // verificamos si existe para lanzar una advertencia
        
        String descripcionFaltante = "";
        if(radioFaltante.isSelected())
            descripcionFaltante = ApplicationConstants.DS_FALTANTES_FALTANTE;
        else if(radioReparacion.isSelected())
            descripcionFaltante  = ApplicationConstants.DS_FALTANTES_REPARACION;
        else if(radioDevolucion.isSelected())
            descripcionFaltante = ApplicationConstants.DS_FALTANTES_DEVOLUCION;
        else if(radioAccidenteTrabajo.isSelected())
            descripcionFaltante = ApplicationConstants.DS_FALTANTES_ACCIDENTE;
        
        boolean existe = false;
        for (int i = 0; i < tablaFaltantes.getRowCount(); i++) {            
            if (this.g_articuloId.equals(tablaFaltantes.getValueAt(i, HD_FALTANTES_ID_ARTICULO).toString() ) &&
                    descripcionFaltante.equals(tablaFaltantes.getValueAt(i, HD_FALTANTES_DESCRIPCION_FALTANTE).toString())
                    ) {
                existe = true;
                break;
            }
        }
        
        if (existe){
            if(JOptionPane.showOptionDialog(this, "Existe un registro similar,  \u00BFContinuar? " ,"Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si") != 0)
                return;                
        }

        String comentario = this.txtComentario.getText();
        String fgFaltante = "0";
        String fgDevolucion = "0";
        String fgAccidenteTrabajo = "0";
     
        
        if(radioFaltante.isSelected())
            fgFaltante = "1";
        if(radioDevolucion.isSelected())
            fgDevolucion = "1";
        if(radioAccidenteTrabajo.isSelected())
            fgAccidenteTrabajo = "1"; 
        
        String datos[] = {this.g_articuloId,this.g_rentaId,iniciar_sesion.usuarioGlobal.getUsuarioId()+"",cantidad+"",comentario,fgFaltante,fgDevolucion,fgAccidenteTrabajo,precioCobrar+""};       
         try {
            funcion.InsertarRegistro(datos, "INSERT INTO faltantes (id_articulo,id_renta,id_usuarios,cantidad,comentario,fg_faltante,fg_devolucion,fg_accidente_trabajo,precio_cobrar) VALUES (?,?,?,?,?,?,?,?,?)");
        } catch (SQLException ex) {                
            JOptionPane.showMessageDialog(null, "Error al insertar registro "+ex, "Error", JOptionPane.ERROR_MESSAGE);                    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE);
        }
         
        this.formato_tabla_faltantes();
        this.llenar_tabla_faltantes();
        
        this.txtPrecioCobrar.setText("");
        this.txtCantidad.setText("");
        this.txtComentario.setText("");
        this.txtPrecioCobrar.setEnabled(false);
        this.txtCantidad.setEnabled(false);
        this.txtComentario.setEnabled(false);
        this.radioFaltante.setSelected(true);
    }
    
    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // TODO add your handling code here:
        
        agregar_faltante();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void txtPrecioCobrarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioCobrarKeyPressed
        // TODO add your handling code here:
         if (evt.getKeyCode() == 10)
             agregar_faltante();
    }//GEN-LAST:event_txtPrecioCobrarKeyPressed

    private void txtComentarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtComentarioKeyPressed
        // TODO add your handling code here:
         if (evt.getKeyCode() == 10)
             agregar_faltante();
    }//GEN-LAST:event_txtComentarioKeyPressed

    private void tablaFaltantesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaFaltantesKeyPressed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_tablaFaltantesKeyPressed

    private void tablaFaltantesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaFaltantesMouseClicked
        // TODO add your handling code here:
         if (evt.getClickCount() == 2) {
            String descripcionFalt = tablaFaltantes.getValueAt(tablaFaltantes.getSelectedRow(), HD_FALTANTES_DESCRIPCION_FALTANTE).toString();
            String descripcionArticulo = tablaFaltantes.getValueAt(tablaFaltantes.getSelectedRow(), HD_FALTANTES_ARTICULO).toString();
            this.lblQuitarElemento.setText(descripcionFalt.toUpperCase() + " : " +descripcionArticulo);
        }
    }//GEN-LAST:event_tablaFaltantesMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
            JOptionPane.showMessageDialog(null, ApplicationConstants.MESSAGE_NOT_PERMISIONS_ADMIN, "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (tablaFaltantes.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String faltanteId = tablaFaltantes.getValueAt(tablaFaltantes.getSelectedRow(), HD_FALTANTES_ID_FALTANTE).toString();
        
        if(faltanteId == null || faltanteId.equals("")){
            JOptionPane.showMessageDialog(null, "Ocurrio un error, intenta de nuevo o reinicia la aplicacion ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if(JOptionPane.showOptionDialog(this, "Se eliminara de la bd,  \u00BFContinuar? " ,"Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si") != 0)
                return;
        
        String datos[] = {"0",faltanteId};
        try {                   
           funcion.UpdateRegistro(datos, "UPDATE faltantes SET fg_activo=? WHERE id_faltante=?");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } 
        
        this.formato_tabla_faltantes();
        this.llenar_tabla_faltantes();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtCantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VerFaltantes dialog = new VerFaltantes(new java.awt.Frame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblEncontrados;
    private javax.swing.JLabel lblInformacionInicial;
    private javax.swing.JLabel lblQuitarElemento;
    private javax.swing.JRadioButton radioAccidenteTrabajo;
    private javax.swing.JRadioButton radioDevolucion;
    private javax.swing.JRadioButton radioFaltante;
    private javax.swing.JRadioButton radioReparacion;
    private javax.swing.JTable tablaArticulos;
    private javax.swing.JTable tablaFaltantes;
    private javax.swing.JTextField txtArticulo;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtComentario;
    private javax.swing.JTextField txtPrecioCobrar;
    // End of variables declaration//GEN-END:variables
}
