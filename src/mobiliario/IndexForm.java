package mobiliario;

import common.utilities.UtilityCommon;
import forms.abonos.PaymentsForm;
import forms.inventario.InventarioForm;
import forms.rentas.AgregarRenta;
import forms.rentas.ConsultarRentas;
import forms.contabilidad.ContabilidadForm;
import forms.material.inventory.MaterialInventoryView;
import forms.proveedores.ViewOrdersProviders;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import static mobiliario.iniciar_sesion.usuarioGlobal;
import model.DatosGenerales;
import services.SystemService;
import utilities.Utility;

public class IndexForm extends javax.swing.JFrame {

    private ViewOrdersProviders viewOrdersProviders;
    private MaterialInventoryView materialInventoryView;
    private clientes ventana_clientes;
    private iniciar_sesion v_iniciar_sesion;
    private usuarios ventana_usuarios;
    private ContabilidadForm ventana_contabilidad;
    private InventarioForm ventana_inventario;
    private PaymentsForm ventana_abonos;
    private AgregarRenta ventana_agregar_renta;
    private ConsultarRentas v_consultar_renta;
    private final SystemService systemService = SystemService.getInstance();
    private final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(IndexForm.class.getName());
    public static List<String> listNotifications = new ArrayList<>();
    public static DatosGenerales generalDataGlobal;

    public IndexForm() {

        initComponents();
        this.setExtendedState(this.MAXIMIZED_BOTH);
        lbl_logueo.setText(iniciar_sesion.usuarioGlobal.getNombre()+" "+iniciar_sesion.usuarioGlobal.getApellidos());
        lblPuesto.setText(iniciar_sesion.usuarioGlobal.getPuesto().getDescripcion());
        
        Utility.pushNotification("NOTIFICACIONES");
        Utility.pushNotification("Inicio sesión: " + usuarioGlobal.getNombre() + " " + usuarioGlobal.getApellidos() );
        
        generalDataGlobal = systemService.getGeneralData();
        LOGGER.info(">>> datos generales obtenidos: "+generalDataGlobal);
        this.setTitle(generalDataGlobal.getCompanyName().toUpperCase());        
        
    }

    public void abrir_ventana(JInternalFrame internalFrame) {
        int x = (jDesktopPane1.getWidth() / 2) - internalFrame.getWidth() / 2;
        int y = (jDesktopPane1.getHeight() / 2) - internalFrame.getHeight() / 2;
        if (internalFrame.isShowing()) {
            internalFrame.setLocation(x, y);
        } else {
            jDesktopPane1.add(internalFrame);
            internalFrame.setLocation(x, y);
            internalFrame.show();
        }

    }

    public void abrir_clientes() {
        if (UtilityCommon.verifyIfInternalFormIsOpen(ventana_clientes,IndexForm.jDesktopPane1)) {
            ventana_clientes = new clientes();
            ventana_clientes.setLocation(this.getWidth() / 2 - ventana_clientes.getWidth() / 2, this.getHeight() / 2 - ventana_clientes.getHeight() / 2 - 20);
            jDesktopPane1.add(ventana_clientes);
            ventana_clientes.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }

    }
    
    public void openMaterialInventoryView() {
        if (UtilityCommon.verifyIfInternalFormIsOpen(materialInventoryView,IndexForm.jDesktopPane1)) {
             if(!Utility.showWindowDataUpdateSession()){
                return;
            }
            materialInventoryView = new MaterialInventoryView();
            materialInventoryView.setLocation(this.getWidth() / 2 - materialInventoryView.getWidth() / 2, this.getHeight() / 2 - materialInventoryView.getHeight() / 2 - 20);
            jDesktopPane1.add(materialInventoryView);
            materialInventoryView.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }

    }
    
       
    public void abrir_orden_proveedor() {
        if (UtilityCommon.verifyIfInternalFormIsOpen(viewOrdersProviders,IndexForm.jDesktopPane1)) {
             if(!Utility.showWindowDataUpdateSession()){
                return;
            }
            viewOrdersProviders = new ViewOrdersProviders();
            viewOrdersProviders.setLocation(this.getWidth() / 2 - viewOrdersProviders.getWidth() / 2, this.getHeight() / 2 - viewOrdersProviders.getHeight() / 2 - 20);
            jDesktopPane1.add(viewOrdersProviders);
            viewOrdersProviders.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }

    }

    public void abrir_inventario() {
        if (UtilityCommon.verifyIfInternalFormIsOpen(ventana_inventario,IndexForm.jDesktopPane1)) {
            ventana_inventario = new InventarioForm();
            ventana_inventario.setLocation(this.getWidth() / 2 - ventana_inventario.getWidth() / 2, this.getHeight() / 2 - ventana_inventario.getHeight() / 2 - 20);
            jDesktopPane1.add(ventana_inventario);
            ventana_inventario.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }

    }

    public void abrir_abonos() {
        if (UtilityCommon.verifyIfInternalFormIsOpen(ventana_abonos,IndexForm.jDesktopPane1)) {
            ventana_abonos = new PaymentsForm();
            ventana_abonos.setLocation(this.getWidth() / 2 - ventana_abonos.getWidth() / 2, this.getHeight() / 2 - ventana_abonos.getHeight() / 2 - 20);
            jDesktopPane1.add(ventana_abonos);
            ventana_abonos.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }

    }

    public void abrir_iniciar_sesion() {
        if (UtilityCommon.verifyIfInternalFormIsOpen(v_iniciar_sesion,IndexForm.jDesktopPane1)) {
            v_iniciar_sesion = new iniciar_sesion();
            v_iniciar_sesion.setLocation(this.getWidth() / 2 - v_iniciar_sesion.getWidth() / 2, this.getHeight() / 2 - v_iniciar_sesion.getHeight() / 2 - 20);
            //jDesktopPane1.add(v_iniciar_sesion);
            v_iniciar_sesion.show();

        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }

    }

    public void mostrar_utilerias() {
        utilerias v_utilerias = new utilerias(null, true);
        v_utilerias.setVisible(true);
        v_utilerias.setLocationRelativeTo(null);
    }
    
   

    public void abrir_usuarios() {
        if (UtilityCommon.verifyIfInternalFormIsOpen(ventana_usuarios,IndexForm.jDesktopPane1)) {
            ventana_usuarios = new usuarios();
            ventana_usuarios.setLocation(this.getWidth() / 2 - ventana_usuarios.getWidth() / 2, this.getHeight() / 2 - ventana_usuarios.getHeight() / 2 - 20);
            jDesktopPane1.add(ventana_usuarios);
            ventana_usuarios.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }

    }
    
    public void abrir_contabilidad() {
        if (UtilityCommon.verifyIfInternalFormIsOpen(ventana_contabilidad,IndexForm.jDesktopPane1)) {
            ventana_contabilidad = new ContabilidadForm();
            ventana_contabilidad.setLocation(this.getWidth() / 2 - ventana_contabilidad.getWidth() / 2, this.getHeight() / 2 - ventana_contabilidad.getHeight() / 2 - 20);
            jDesktopPane1.add(ventana_contabilidad);
            ventana_contabilidad.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }

    }

    public void abrir_nueva_renta() throws PropertyVetoException{
        if (UtilityCommon.verifyIfInternalFormIsOpen(ventana_agregar_renta,IndexForm.jDesktopPane1)) {
            if(!Utility.showWindowDataUpdateSession()){
                return;
            }
                ventana_agregar_renta = new AgregarRenta();
                ventana_agregar_renta.setLocation(this.getWidth() / 2 - ventana_agregar_renta.getWidth() / 2, this.getHeight() / 2 - ventana_agregar_renta.getHeight() / 2 - 20);
//                ventana_agregar_renta.setMaximum(true);
                jDesktopPane1.add(ventana_agregar_renta);
                ventana_agregar_renta.show();
            
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }

    }

    public void abrir_consultar_renta() throws PropertyVetoException {
        if (UtilityCommon.verifyIfInternalFormIsOpen(v_consultar_renta,IndexForm.jDesktopPane1)) {
            if(!Utility.showWindowDataUpdateSession()){
                return;
            }
            v_consultar_renta = new ConsultarRentas();
            v_consultar_renta.setLocation(this.getWidth() / 2 - v_consultar_renta.getWidth() / 2, this.getHeight() / 2 - v_consultar_renta.getHeight() / 2 - 20);
//            v_consultar_renta.setMaximum(true);
            jDesktopPane1.add(v_consultar_renta);
            v_consultar_renta.show();
         
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
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

        jButton9 = new javax.swing.JButton();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lbl_logueo = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        lblPuesto = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaNotifications = new javax.swing.JTextArea();
        jToolBar1 = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnContabilidad = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jbtn_usuarios = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButton7 = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jbtn_cerrar_sesion = new javax.swing.JButton();
        jBtnViewOrderProviders = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();

        jButton9.setText("jButton9");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Casa Gaby Eventos");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Admin-icon_48.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lbl_logueo.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jPanel1.add(lbl_logueo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 328, 22));

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, -1, 64));
        jPanel1.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 420, 10));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Apps-preferences-system-windows-actions-icon_48.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));
        jPanel1.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 0, 760, 10));

        jLabel3.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel3.setText("gtorreblancaluna@gmail.com");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 30, 300, 20));

        lblPuesto.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12)); // NOI18N
        jPanel1.add(lblPuesto, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 14, 330, 20));

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 0, 10, 60));

        jLabel5.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel5.setText("Versión 1.4.3");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 24, 150, 20));

        jLabel6.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel6.setText("Contacto: L.I. Gerardo Torreblanca Luna");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, 300, 20));

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 0, 10, 60));

        txtAreaNotifications.setEditable(false);
        txtAreaNotifications.setBackground(new java.awt.Color(226, 224, 224));
        txtAreaNotifications.setColumns(20);
        txtAreaNotifications.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAreaNotifications.setRows(5);
        txtAreaNotifications.setBorder(null);
        txtAreaNotifications.setEnabled(false);
        jScrollPane1.setViewportView(txtAreaNotifications);

        jDesktopPane1.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1380, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 978, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);
        jToolBar1.add(jSeparator1);

        jButton3.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/rentas_48.png"))); // NOI18N
        jButton3.setMnemonic('A');
        jButton3.setToolTipText("Agregar evento (Alt+A)");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton4.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/consultar_renta_48.png"))); // NOI18N
        jButton4.setMnemonic('C');
        jButton4.setToolTipText("Consultar eventos (Alt+C)");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);
        jToolBar1.add(jSeparator2);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/inventario_48.png"))); // NOI18N
        jButton2.setMnemonic('I');
        jButton2.setToolTipText("Inventario (Alt+I)");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/coins-icon_48.png"))); // NOI18N
        jButton5.setMnemonic('Z');
        jButton5.setToolTipText("Consultar abonos (Alt+Z)");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton5);
        jToolBar1.add(jSeparator4);

        btnContabilidad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cash-register-icon_48.png"))); // NOI18N
        btnContabilidad.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnContabilidad.setFocusable(false);
        btnContabilidad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContabilidad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnContabilidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContabilidadActionPerformed(evt);
            }
        });
        jToolBar1.add(btnContabilidad);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Utilities-icon_48.png"))); // NOI18N
        jButton6.setToolTipText("Utilerias");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton6);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Apps-system-users-icon_48.png"))); // NOI18N
        jButton1.setToolTipText("Clientes");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jbtn_usuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/User-Files-icon_48.png"))); // NOI18N
        jbtn_usuarios.setToolTipText("Usuarios");
        jbtn_usuarios.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_usuarios.setFocusable(false);
        jbtn_usuarios.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_usuarios.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_usuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_usuariosActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_usuarios);
        jToolBar1.add(jSeparator3);

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/database-arrow-down-icon_48.png"))); // NOI18N
        jButton7.setToolTipText("Respaldar base de datos");
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton7);
        jToolBar1.add(jSeparator5);

        jbtn_cerrar_sesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar_ventana_48.png"))); // NOI18N
        jbtn_cerrar_sesion.setMnemonic('C');
        jbtn_cerrar_sesion.setToolTipText("Cerrar sesion (Alt+C)");
        jbtn_cerrar_sesion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_cerrar_sesion.setFocusable(false);
        jbtn_cerrar_sesion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_cerrar_sesion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_cerrar_sesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_cerrar_sesionActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_cerrar_sesion);

        jBtnViewOrderProviders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/customer-service-icon-48.png"))); // NOI18N
        jBtnViewOrderProviders.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jBtnViewOrderProviders.setFocusable(false);
        jBtnViewOrderProviders.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBtnViewOrderProviders.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBtnViewOrderProviders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnViewOrderProvidersActionPerformed(evt);
            }
        });
        jToolBar1.add(jBtnViewOrderProviders);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/inventory-maintenance-icon.png"))); // NOI18N
        jButton8.setToolTipText("Inventario de material");
        jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton8);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDesktopPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jDesktopPane1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtn_usuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_usuariosActionPerformed
        // TODO add your handling code here:
        abrir_usuarios();
    }//GEN-LAST:event_jbtn_usuariosActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        abrir_clientes();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        abrir_inventario();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            abrir_nueva_renta();
        } catch (PropertyVetoException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR);
            Logger.getLogger(IndexForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            // TODO add your handling code here:
            abrir_consultar_renta();
        } catch (PropertyVetoException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR);
            Logger.getLogger(IndexForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Solo el administrador puede visualizar abonos ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
        }
        if(!Utility.showWindowDataUpdateSession()){
            return;
        }
        abrir_abonos();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
            JOptionPane.showMessageDialog(null, "Solo el administrador puede abrir las preferencias del sistema :( ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if(!Utility.showWindowDataUpdateSession()){
            return;
        }
        mostrar_utilerias();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jbtn_cerrar_sesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_cerrar_sesionActionPerformed
        // TODO add your handling code here:
        int seleccion = JOptionPane.showOptionDialog(this, "Confirme para salir del sistema \nNo se guardaran los cambios en ningun formulario abierto.", "Cerrar sesion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
        if (seleccion == 0) {//presiono que si
            abrir_iniciar_sesion();
            this.dispose();
        }

    }//GEN-LAST:event_jbtn_cerrar_sesionActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Solo el administrador puede realizar esta accion :( ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        bd r = new bd(this, true);
        r.setVisible(true);
        r.setLocationRelativeTo(this);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea cerrar la ventana actual?", "Salir del sistema ", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
//            funcion.desconecta();
            System.exit(0);
        } else {
            
        }
    }//GEN-LAST:event_formWindowClosing

    private void btnContabilidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContabilidadActionPerformed
        // TODO add your handling code here:
         if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Permiso denegado :( ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        abrir_contabilidad();
    }//GEN-LAST:event_btnContabilidadActionPerformed

    private void jBtnViewOrderProvidersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnViewOrderProvidersActionPerformed
        // TODO add your handling code here:
        abrir_orden_proveedor();
    }//GEN-LAST:event_jBtnViewOrderProvidersActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        openMaterialInventoryView();
    }//GEN-LAST:event_jButton8ActionPerformed

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
            java.util.logging.Logger.getLogger(IndexForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IndexForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IndexForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IndexForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IndexForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnContabilidad;
    private javax.swing.JButton jBtnViewOrderProviders;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    public static javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbtn_cerrar_sesion;
    private javax.swing.JButton jbtn_usuarios;
    public static javax.swing.JLabel lblPuesto;
    public static javax.swing.JLabel lbl_logueo;
    public static javax.swing.JTextArea txtAreaNotifications;
    // End of variables declaration//GEN-END:variables
}
