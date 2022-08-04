package mobiliario;

import clases.conectate;
import clases.sqlclass;
import common.exceptions.DataOriginException;
import common.services.UtilityService;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.AsignaCategoria;
import common.model.CategoriaDTO;
import services.CategoryService;
import common.services.UserService;

/**
 *
 * @author Carlos Alberto
 */
public class usuarios extends javax.swing.JInternalFrame {
private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(usuarios.class.getName());
    sqlclass funcion = new sqlclass();
    conectate conexion = new conectate();
    private final UserService userService = UserService.getInstance();
    CategoryService categoryService = new CategoryService();
    private final UtilityService utilityService = UtilityService.getInstance();
    Object[][] dtconduc;
    Object[] datos_combo;
    String id_usuario;
    public static boolean validar_puesto = false;

    /**
     * Creates new form clientes
     */
    public usuarios() {
        funcion.conectate();
        initComponents();       
        jbtn_guardar.setEnabled(false);
        jbtn_cambiar_contraseña.setEnabled(false);
        tabla_usuarios();
        llenar_combo_puesto();
        limpiar();
        formato_categorias_por_usuario();
        llenarComboCategorias();
        this.txt_buscar.requestFocus();
        check_nombre.setSelected(true);
        // this.setLocationRelativeTo(null);
    }
    
     public void llenarComboCategorias(){
        this.comboCategorias.removeAllItems();
        List<CategoriaDTO> categorias = categoryService.obtenerCategorias(funcion);
        
        if(categorias == null || categorias.size()<=0)
            return;
        
        this.comboCategorias.addItem("- seleccione -");
        for(CategoriaDTO categoria : categorias)
            this.comboCategorias.addItem(categoria.getDescripcion());        
        
    }

    public void limpiar() {
        this.txt_nombre.setText("");
        txt_apellidos.setText("");

        txt_tel_movil.setText("");
        txt_tel_casa.setText("");
        txt_contraseña.setText("");
        txt_verificar_contraseña.setText("");
        txt_direccion.setText("");

        check_administrador.setSelected(false);
        check_nivel_1.setSelected(false);

        txt_contraseña.setEnabled(true);
        txt_verificar_contraseña.setEnabled(true);

    }

    public void tabla_usuarios() {
        // funcion.conectate();
        tabla_usuarios.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Nombre", "Apellidos", "Tel Cel", "Tel fijo", "Direccion", "Puesto", "admin", "nivel1"};
        String[] colName = {"id_usuarios", "nombre", "apellidos", "tel_movil", "tel_fijo", "direccion", "descripcion", "administrador", "nivel1"};
        //nombre de columnas, tabla, instruccion sql   
        try {       
            dtconduc = funcion.GetTabla(colName, "usuarios", "SELECT u.`id_usuarios`,u.`nombre`, u.`apellidos`, u.`tel_movil`, u.`tel_fijo`, u.`direccion`, p.`descripcion`,u.`administrador`,u.`nivel1` FROM usuarios u, puesto p\n"
              + "WHERE u.id_puesto=p.id_puesto Order by u.`nombre`");
          } catch (SQLNonTransientConnectionException e) {
              funcion.conectate();
              JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo "+e, "Error", JOptionPane.ERROR_MESSAGE); 
          } catch (SQLException e) {
              JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
          } catch (Exception e) {
              JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
          }
       
        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        tabla_usuarios.setModel(datos);

        int[] anchos = {10, 130, 190, 80, 80, 100, 80, 10, 10};

        for (int inn = 0; inn < tabla_usuarios.getColumnCount(); inn++)
            tabla_usuarios.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);

        tabla_usuarios.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_usuarios.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_usuarios.getColumnModel().getColumn(0).setPreferredWidth(0);

        tabla_usuarios.getColumnModel().getColumn(7).setMaxWidth(0);
        tabla_usuarios.getColumnModel().getColumn(7).setMinWidth(0);
        tabla_usuarios.getColumnModel().getColumn(7).setPreferredWidth(0);

        tabla_usuarios.getColumnModel().getColumn(8).setMaxWidth(0);
        tabla_usuarios.getColumnModel().getColumn(8).setMinWidth(0);
        tabla_usuarios.getColumnModel().getColumn(8).setPreferredWidth(0);

        // funcion.desconecta();
    }

    public void tabla_usuarios_like() {
        // funcion.conectate();
        tabla_usuarios.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Nombre", "Apellidos", "Tel Cel", "Tel Fijo", "Direccion", "Admin", "Nivel1"};
        String[] colName = {"id_usuarios", "nombre", "apellidos", "tel_movil", "tel_fijo", "direccion", "administrador", "nivel1"};
        //nombre de columnas, tabla, instruccion sql 
        if (check_nombre.isSelected()) {
            try {       
             dtconduc = funcion.GetTabla(colName, "usuarios", "SELECT u.`id_usuarios`,u.`nombre`, u.`apellidos`, u.`tel_movil`, u.`tel_fijo`, u.`direccion`, p.`descripcion`,u.`administrador`,u.`nivel1` FROM usuarios u, puesto p\n"
                    + "WHERE u.id_puesto=p.id_puesto AND u.`nombre` like '%" + txt_buscar.getText() + "%' Order by u.`nombre`");
            } catch (SQLNonTransientConnectionException e) {
                funcion.conectate();
                JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            }
           
            System.out.println("Entra a nombre " + txt_nombre.getText());
        }
        if (check_apellidos.isSelected()) {
            try {       
             dtconduc = funcion.GetTabla(colName, "usuarios", "SELECT u.`id_usuarios`,u.`nombre`, u.`apellidos`, u.`tel_movil`, u.`tel_fijo`, u.`direccion`, p.`descripcion`,u.`administrador`,u.`nivel1` FROM usuarios u, puesto p\n"
                    + "WHERE u.id_puesto=p.id_puesto AND u.`apellidos` like '%" + txt_buscar.getText() + "%' Order by u.`apellidos`");
            } catch (SQLNonTransientConnectionException e) {
                funcion.conectate();
                JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            }
            
            System.out.println("Entra a apellidos " + txt_apellidos.getText());
        }

        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        tabla_usuarios.setModel(datos);

        int[] anchos = {10, 130, 190, 80, 80, 100, 80, 10, 10};

        for (int inn = 0; inn < tabla_usuarios.getColumnCount(); inn++) {
            tabla_usuarios.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        tabla_usuarios.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_usuarios.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_usuarios.getColumnModel().getColumn(0).setPreferredWidth(0);

        tabla_usuarios.getColumnModel().getColumn(7).setMaxWidth(0);
        tabla_usuarios.getColumnModel().getColumn(7).setMinWidth(0);
        tabla_usuarios.getColumnModel().getColumn(7).setPreferredWidth(0);

        tabla_usuarios.getColumnModel().getColumn(8).setMaxWidth(0);
        tabla_usuarios.getColumnModel().getColumn(8).setMinWidth(0);
        tabla_usuarios.getColumnModel().getColumn(8).setPreferredWidth(0);

        // funcion.desconecta();
    }

    public void mostrar_puesto() {
        Puesto ventana_puesto = new Puesto(null, true);
        ventana_puesto.setVisible(true);
        ventana_puesto.setLocationRelativeTo(null);
    }

    public void llenar_combo_puesto() {
        //
        // funcion.conectate();
        datos_combo = funcion.GetColumna("puesto", "descripcion", "Select descripcion from puesto");
        cmb_puesto.removeAllItems();

        for (int i = 0; i <= datos_combo.length - 1; i++) {
            cmb_puesto.addItem(datos_combo[i].toString());
        }
        // funcion.desconecta();

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
        buttonGroup2 = new javax.swing.ButtonGroup();
        panel_botones = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_usuarios = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jLabel3 = new javax.swing.JLabel();
        txt_buscar = new javax.swing.JTextField();
        check_apellidos = new javax.swing.JCheckBox();
        check_nombre = new javax.swing.JCheckBox();
        panel_datos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_nombre = new javax.swing.JTextField();
        txt_apellidos = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txt_tel_movil = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_tel_casa = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        check_administrador = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_contraseña = new javax.swing.JPasswordField();
        txt_verificar_contraseña = new javax.swing.JPasswordField();
        cmb_puesto = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        lbl_puesto = new javax.swing.JLabel();
        txt_direccion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        check_nivel_1 = new javax.swing.JCheckBox();
        jToolBar1 = new javax.swing.JToolBar();
        jbtn_nuevo = new javax.swing.JButton();
        jbtn_agregar = new javax.swing.JButton();
        jbtn_editar = new javax.swing.JButton();
        jbtn_guardar = new javax.swing.JButton();
        jbtn_eliminar = new javax.swing.JButton();
        jbtn_cambiar_contraseña = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        panelCategoriasPorUsuarios = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCategoriasPorUsuario = new javax.swing.JTable();
        lblEncontrados = new javax.swing.JLabel();
        lblNombreUsuario = new javax.swing.JLabel();
        comboCategorias = new javax.swing.JComboBox<>();
        lblInfoAsingaCategorias = new javax.swing.JLabel();
        lblEliminaAsignaCategoria = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        jLabel9.setText("jLabel9");

        setClosable(true);
        setTitle("USUARIOS");
        setToolTipText("");

        panel_botones.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tabla de usuarios", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        tabla_usuarios.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tabla_usuarios.setModel(new javax.swing.table.DefaultTableModel(
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
        tabla_usuarios.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabla_usuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_usuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabla_usuarios);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search-icon.png"))); // NOI18N

        txt_buscar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_buscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_buscarKeyReleased(evt);
            }
        });

        buttonGroup1.add(check_apellidos);
        check_apellidos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        check_apellidos.setText("Apellidos");

        buttonGroup1.add(check_nombre);
        check_nombre.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        check_nombre.setText("Nombre");

        javax.swing.GroupLayout panel_botonesLayout = new javax.swing.GroupLayout(panel_botones);
        panel_botones.setLayout(panel_botonesLayout);
        panel_botonesLayout.setHorizontalGroup(
            panel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_botonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_botonesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(6, 6, 6)
                        .addComponent(txt_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(check_apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(check_nombre)))
                .addContainerGap())
        );
        panel_botonesLayout.setVerticalGroup(
            panel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_botonesLayout.createSequentialGroup()
                .addGroup(panel_botonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txt_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(check_apellidos)
                    .addComponent(check_nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_datos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N
        panel_datos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel1.setText("Nombre:");
        panel_datos.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        txt_nombre.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        panel_datos.add(txt_nombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 104, -1));

        txt_apellidos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        panel_datos.add(txt_apellidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 240, -1));

        jLabel2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel2.setText("Apellidos:");
        panel_datos.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));

        txt_tel_movil.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        panel_datos.add(txt_tel_movil, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 40, 104, -1));

        jLabel4.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel4.setText("Tel Movil:");
        panel_datos.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, -1, -1));

        txt_tel_casa.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        panel_datos.add(txt_tel_casa, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 40, 94, -1));

        jLabel5.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel5.setText("Tel Casa:");
        panel_datos.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, -1, -1));

        buttonGroup2.add(check_administrador);
        check_administrador.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        check_administrador.setText("Administrador");
        panel_datos.add(check_administrador, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 90, 110, -1));

        jLabel6.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel6.setText("Contraseña:");
        panel_datos.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, -1, -1));

        jLabel7.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel7.setText("Verificar contraseña:");
        panel_datos.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 20, -1, -1));

        txt_contraseña.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        panel_datos.add(txt_contraseña, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 40, 114, -1));

        txt_verificar_contraseña.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_verificar_contraseña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_verificar_contraseñaActionPerformed(evt);
            }
        });
        panel_datos.add(txt_verificar_contraseña, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 40, 125, -1));

        cmb_puesto.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        cmb_puesto.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        panel_datos.add(cmb_puesto, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 90, 140, -1));

        jLabel8.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel8.setText("Puesto:");
        panel_datos.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 70, 50, 20));

        lbl_puesto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lbl_puesto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_puesto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_puestoMouseClicked(evt);
            }
        });
        panel_datos.add(lbl_puesto, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 80, 30, 40));

        txt_direccion.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        panel_datos.add(txt_direccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 430, -1));

        jLabel10.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel10.setText("Direccion:");
        panel_datos.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 170, -1));

        buttonGroup2.add(check_nivel_1);
        check_nivel_1.setText("Nivel 1");
        panel_datos.add(check_nivel_1, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 90, -1, -1));

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        jbtn_nuevo.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jbtn_nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Folder-New-Folder-icon.png"))); // NOI18N
        jbtn_nuevo.setMnemonic('N');
        jbtn_nuevo.setToolTipText("Nuevo");
        jbtn_nuevo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_nuevoActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_nuevo);

        jbtn_agregar.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jbtn_agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Document-Add-icon.png"))); // NOI18N
        jbtn_agregar.setMnemonic('A');
        jbtn_agregar.setToolTipText("Agregar");
        jbtn_agregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_agregarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_agregar);

        jbtn_editar.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jbtn_editar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Actions-edit-icon.png"))); // NOI18N
        jbtn_editar.setMnemonic('d');
        jbtn_editar.setToolTipText("Editar");
        jbtn_editar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_editarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_editar);

        jbtn_guardar.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jbtn_guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/floppy-disk-icon.png"))); // NOI18N
        jbtn_guardar.setMnemonic('G');
        jbtn_guardar.setToolTipText("Guardar");
        jbtn_guardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_guardarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_guardar);

        jbtn_eliminar.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jbtn_eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/folder-remove-icon.png"))); // NOI18N
        jbtn_eliminar.setMnemonic('E');
        jbtn_eliminar.setToolTipText("Eliminar");
        jbtn_eliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_eliminarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_eliminar);

        jbtn_cambiar_contraseña.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jbtn_cambiar_contraseña.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/secrecy-icon.png"))); // NOI18N
        jbtn_cambiar_contraseña.setMnemonic('C');
        jbtn_cambiar_contraseña.setToolTipText("Cambiar pass");
        jbtn_cambiar_contraseña.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_cambiar_contraseña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_cambiar_contraseñaActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_cambiar_contraseña);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/excel-icon.png"))); // NOI18N
        jButton1.setToolTipText("Exportar a Excel");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);
        jToolBar1.add(jSeparator1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/archive-icon.png"))); // NOI18N
        jButton2.setToolTipText("Asignar categoria a un usuario seleccionado");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        tablaCategoriasPorUsuario.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaCategoriasPorUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jScrollPane2.setViewportView(tablaCategoriasPorUsuario);

        comboCategorias.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblInfoAsingaCategorias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/info.png"))); // NOI18N
        lblInfoAsingaCategorias.setToolTipText("Asigna categorias a un usuario seleccionado, para poder emitir el reporte correspondiente a las categorias por usuario de un determinado FOLIO");
        lblInfoAsingaCategorias.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblInfoAsingaCategorias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblInfoAsingaCategoriasMouseClicked(evt);
            }
        });

        lblEliminaAsignaCategoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/delete.png"))); // NOI18N
        lblEliminaAsignaCategoria.setToolTipText("Eliminar categoria");
        lblEliminaAsignaCategoria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblEliminaAsignaCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblEliminaAsignaCategoriaMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelCategoriasPorUsuariosLayout = new javax.swing.GroupLayout(panelCategoriasPorUsuarios);
        panelCategoriasPorUsuarios.setLayout(panelCategoriasPorUsuariosLayout);
        panelCategoriasPorUsuariosLayout.setHorizontalGroup(
            panelCategoriasPorUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCategoriasPorUsuariosLayout.createSequentialGroup()
                .addComponent(comboCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(panelCategoriasPorUsuariosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCategoriasPorUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelCategoriasPorUsuariosLayout.createSequentialGroup()
                        .addComponent(lblInfoAsingaCategorias)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblEliminaAsignaCategoria))
                    .addGroup(panelCategoriasPorUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblEncontrados, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelCategoriasPorUsuariosLayout.setVerticalGroup(
            panelCategoriasPorUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCategoriasPorUsuariosLayout.createSequentialGroup()
                .addComponent(comboCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCategoriasPorUsuariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfoAsingaCategorias)
                    .addComponent(lblEliminaAsignaCategoria))
                .addGap(31, 31, 31)
                .addComponent(lblNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblEncontrados, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        jLabel11.setText("Categorias:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel_botones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_datos, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCategoriasPorUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_datos, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_botones, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 53, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelCategoriasPorUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtn_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_agregarActionPerformed
        try {
            // TODO add your handling code here:
            
             if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Solo el administrador puede realizar esta accion ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (txt_nombre.getText().equals("") || txt_apellidos.getText().equals("") || txt_contraseña.getText().equals("") || cmb_puesto.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(null, "Faltan parametros", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (!txt_contraseña.getText().equals(txt_verificar_contraseña.getText())) {
                JOptionPane.showMessageDialog(null, "No coincide la contraseña", "Error", JOptionPane.INFORMATION_MESSAGE);
                txt_contraseña.setText("");
                txt_verificar_contraseña.setText("");
                txt_contraseña.requestFocus();
                return;
            }
            if (check_administrador.isSelected() == false && check_nivel_1.isSelected() == false) {
                JOptionPane.showMessageDialog(null, "Elige un nivel para el usuario", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String admin = "0";
            if (check_administrador.isSelected())
                admin = "1";
            
            String nivel_1 = "0";
            if (check_nivel_1.isSelected() == true)
                nivel_1 = "1";
            
            try {
                if(userService.checkAlReadyPassword(String.valueOf(txt_contraseña.getPassword()))){
                    JOptionPane.showMessageDialog(null, "Esta contraseña ya esta asignada a un usuario, porfavor introduce una diferente", "Error", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            } catch (DataOriginException e) {
                log.error(e);
                JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR);
            }
            String id_puesto = funcion.GetData("id_puesto", "select id_puesto from puesto where descripcion = '" + cmb_puesto.getSelectedItem().toString() + "' ");
            
            
             String datos[] = {txt_nombre.getText().toString(), txt_apellidos.getText().toString(), txt_tel_movil.getText().toString(), txt_tel_casa.getText().toString(), txt_direccion.getText().toString(), admin, nivel_1, this.txt_contraseña.getText().toString(), "1", id_puesto};
            
            funcion.InsertarRegistro(datos, "insert into usuarios (nombre,apellidos,tel_movil,tel_fijo,direccion,administrador,nivel1,contrasenia,activo,id_puesto) values(?,?,?,?,?,?,?,?,?,?)");
            limpiar();
            tabla_usuarios();
        } catch (SQLException ex) {
            log.error(ex);
            JOptionPane.showMessageDialog(null, "Error al insertar registro ", "Error", JOptionPane.ERROR);
        }
            
        
    }//GEN-LAST:event_jbtn_agregarActionPerformed

    private void jbtn_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_nuevoActionPerformed
        // TODO add your handling code here:
        limpiar();
        jbtn_agregar.setEnabled(true);
        txt_nombre.requestFocus();
        jbtn_guardar.setEnabled(false);
    }//GEN-LAST:event_jbtn_nuevoActionPerformed

    private void jbtn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editarActionPerformed
        // TODO add your handling code here:
        if (tabla_usuarios.getSelectedRow() != - 1) {
             if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Solo el administrador puede realizar esta accion ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            txt_contraseña.setEnabled(false);
            txt_verificar_contraseña.setEnabled(false);
            jbtn_cambiar_contraseña.setEnabled(true);
            jbtn_agregar.setEnabled(false);
            jbtn_guardar.setEnabled(true);
            id_usuario = tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 0).toString();
            txt_contraseña.setEnabled(true);
            txt_verificar_contraseña.setEnabled(true);
            check_administrador.setSelected(false);
            check_nivel_1.setSelected(false);

            for (int i = 0; i < cmb_puesto.getItemCount(); i++) {
                if (cmb_puesto.getItemAt(i).equals(String.valueOf(tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 6)))) {

                    cmb_puesto.setSelectedIndex(i);
                    break;
                }

            }
            this.txt_nombre.setText(String.valueOf(tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 1)));
            this.txt_apellidos.setText(String.valueOf(tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 2)));
            this.txt_tel_movil.setText(String.valueOf(tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 3)));
            this.txt_tel_casa.setText(String.valueOf(tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 4)));
            this.txt_direccion.setText(String.valueOf(tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 5)));

            if (tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 7).equals("1")) {
                check_administrador.setSelected(true);
            } else {
                check_administrador.setSelected(false);
            }

            if (tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 8).equals("1")) {
                check_nivel_1.setSelected(true);
            } else {
                check_nivel_1.setSelected(false);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para editar", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jbtn_editarActionPerformed

    
    private void jbtn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_guardarActionPerformed
        // TODO add your handling code here:
        String nivel1 = "0", admin = "0";
        if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Solo el administrador puede realizar esta accion ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        if (txt_nombre.getText().equals("") || txt_apellidos.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Faltan parametros", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (check_administrador.isSelected())
            admin = "1";
        if (check_nivel_1.isSelected())
            nivel1 = "1";
                
      
        String id_puesto = funcion.GetData("id_puesto", "select id_puesto from puesto where descripcion = '" + cmb_puesto.getSelectedItem().toString() + "' ");

        String datos[] = {txt_nombre.getText().toString(), txt_apellidos.getText().toString(), txt_tel_movil.getText().toString(), txt_tel_casa.getText().toString(), txt_direccion.getText().toString(), admin, nivel1, id_puesto, id_usuario};
        
        try {        
            funcion.UpdateRegistro(datos, "update usuarios set nombre=?, apellidos=?,tel_movil=?,tel_fijo=?,direccion=?,administrador=?,nivel1=?,id_puesto=? where id_usuarios=?");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error \n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        tabla_usuarios();
        txt_contraseña.setEnabled(true);
        txt_verificar_contraseña.setEnabled(true);
        jbtn_guardar.setEnabled(false);
        jbtn_agregar.setEnabled(true);
        limpiar();
            
        
    }//GEN-LAST:event_jbtn_guardarActionPerformed

    private void jbtn_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_eliminarActionPerformed
        // TODO add your handling code here:
        if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Solo el administrador puede realizar esta accion ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (tabla_usuarios.getRowCount() > 1) {
                System.out.println("prueba: " + tabla_usuarios.getRowCount());
                if (tabla_usuarios.getSelectedRow() != -1) {

                    int seleccion = JOptionPane.showOptionDialog(this, "¿Eliminar registro: " + (String.valueOf(tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 1))) + " " + String.valueOf(tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 2)) + "?", "Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "No");
                    if ((seleccion + 1) == 1) {
                        // funcion.conectate();
                    try {        
                           funcion.DeleteRegistro("usuarios", "id_usuarios", tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 0).toString()); //tabla,columna,id
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Ocurrio un error \n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                    }
                        
                        // funcion.desconecta();
                        tabla_usuarios();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecciona una fila para eliminar ", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se puede eliminar el usuario... ", "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        
    }//GEN-LAST:event_jbtn_eliminarActionPerformed

    private void txt_buscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscarKeyReleased
        // TODO add your handling code here:
        tabla_usuarios_like();
    }//GEN-LAST:event_txt_buscarKeyReleased

    private void txt_verificar_contraseñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_verificar_contraseñaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_verificar_contraseñaActionPerformed

    private void jbtn_cambiar_contraseñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_cambiar_contraseñaActionPerformed
        // TODO add your handling code here:
        id_usuario = tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 0).toString();
        if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Solo el administrador puede realizar esta accion ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        if (tabla_usuarios.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!txt_contraseña.getText().equals(txt_verificar_contraseña.getText())) {
            JOptionPane.showMessageDialog(null, "No coincide la contraseña ", "Error", JOptionPane.INFORMATION_MESSAGE);
            txt_contraseña.setText("");
            txt_verificar_contraseña.setText("");
            txt_contraseña.requestFocus();
            return;
        } 
        String contraseña = this.txt_contraseña.getText().toString();
        if (contraseña.equals("")) {
            JOptionPane.showMessageDialog(null, "No puede ir vacio ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
                if(userService.checkAlReadyPassword(String.valueOf(txt_contraseña.getPassword()))){
                    JOptionPane.showMessageDialog(null, "Esta contraseña ya esta asignada a un usuario, porfavor introduce una diferente", "Error", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            } catch (DataOriginException e) {
                log.error(e);
                JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR);
            }
            
        String datos[] = {txt_contraseña.getText().toString(), id_usuario};
         try {        
             funcion.UpdateRegistro(datos, "update usuarios set contrasenia=? WHERE id_usuarios=? ");
         } catch (Exception e) {
             JOptionPane.showMessageDialog(null, "Ocurrio un error \n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
         }
        
        // funcion.desconecta();
        jbtn_cambiar_contraseña.setEnabled(false);
        JOptionPane.showMessageDialog(null, "Se actualizo la contraseña correctamente...", "Error", JOptionPane.INFORMATION_MESSAGE);
        limpiar();            
        
    }//GEN-LAST:event_jbtn_cambiar_contraseñaActionPerformed

    public void formato_categorias_por_usuario(){
        tablaCategoriasPorUsuario.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        Object[][] data = {{"", "", ""}};
        String[] columnNames = {"id_asigna_categoria","id_categoria", "Categoria"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tablaCategoriasPorUsuario.setModel(TableModel);
        
        int[] anchos = {70, 70,100};
        
        for (int inn = 0; inn < tablaCategoriasPorUsuario.getColumnCount(); inn++) {
            tablaCategoriasPorUsuario.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            DefaultTableModel temp = (DefaultTableModel) tablaCategoriasPorUsuario.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        tablaCategoriasPorUsuario.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaCategoriasPorUsuario.getColumnModel().getColumn(0).setMinWidth(0);
        tablaCategoriasPorUsuario.getColumnModel().getColumn(0).setPreferredWidth(0); 
        
        tablaCategoriasPorUsuario.getColumnModel().getColumn(1).setMaxWidth(0);
        tablaCategoriasPorUsuario.getColumnModel().getColumn(1).setMinWidth(0);
        tablaCategoriasPorUsuario.getColumnModel().getColumn(1).setPreferredWidth(0); 
    
    }
    private void lbl_puestoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_puestoMouseClicked
        // TODO add your handling code here:
        mostrar_puesto();
        if (validar_puesto == true) {
            llenar_combo_puesto();
            tabla_usuarios();
            validar_puesto = false;
        }
    }//GEN-LAST:event_lbl_puestoMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        utilityService.exportarExcel(tabla_usuarios);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tabla_usuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_usuariosMouseClicked
        // TODO add your handling code here:
         if (evt.getClickCount() == 2) {
             mostrarCategoriasPorUsuario();
              
         }
    }//GEN-LAST:event_tabla_usuariosMouseClicked

    public void mostrarCategoriasPorUsuario(){
        this.formato_categorias_por_usuario();
//              temp.setRowCount(0);
              String nombre = (tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 1).toString()) + " " + (tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 2).toString()) ;
              lblNombreUsuario.setText(nombre);
              int usuarioId = Integer.parseInt(tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 0).toString());
              List<AsignaCategoria> categorias = categoryService.obtenerCategoriasAsignadasPorUsuarioId(funcion, usuarioId);
              if(categorias == null || categorias.size()<=0){
                  this.lblEncontrados.setText("No se encontraron categorias asignadas a este usuario :( ");
                  return;
              } 
              DefaultTableModel temp = (DefaultTableModel) tablaCategoriasPorUsuario.getModel();
              this.lblEncontrados.setText("Se encontraron : "+categorias.size()+" categorias... :) ");         
              for(AsignaCategoria asignaCategoria : categorias){                  
                     Object nuevo[] = {
                         asignaCategoria.getAsignaCategoriaId()+"",
                         asignaCategoria.getCategoria().getCategoriaId()+"",
                         asignaCategoria.getCategoria().getDescripcion()
                     };
                     temp.addRow(nuevo);
              } // end for
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        StringBuilder mensaje = new StringBuilder();
        if (tabla_usuarios.getSelectedRow() == - 1)
            mensaje.append("Selecciona un usuario para continuar ");
        if(this.comboCategorias.getSelectedIndex() == 0)
            mensaje.append("\nSelecciona una categoria para continuar");
                    
        if(!mensaje.toString().equals("")){
            JOptionPane.showMessageDialog(null, mensaje.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id_categoria = funcion.GetData("id_categoria", "SELECT id_categoria FROM categoria WHERE descripcion='" + comboCategorias.getSelectedItem().toString() + "'");
        String id_usuario = (tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 0).toString());
        String datos[] = {id_usuario,id_categoria};
        
        categoryService.insertarCategoriaEnAsignaCategoria(funcion, datos);
        String nombreUsuario = (tabla_usuarios.getValueAt(tabla_usuarios.getSelectedRow(), 1).toString());
        
        JOptionPane.showMessageDialog(null, "Se asigno con exito la categoria "+comboCategorias.getSelectedItem().toString()+" al usuario "+nombreUsuario, "EXITO", JOptionPane.INFORMATION_MESSAGE);
        
        this.mostrarCategoriasPorUsuario();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void lblInfoAsingaCategoriasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblInfoAsingaCategoriasMouseClicked
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(null, "Podras asignar una o varias categorias a un usuario seleccionado en la tabla\nPara posteriormente emitir el reporte de un folio y saber que categorias le corresponde a los usuarios asignados a dichas categorias", "INFO ASIGNA CATEGORIAS", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_lblInfoAsingaCategoriasMouseClicked

    private void lblEliminaAsignaCategoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEliminaAsignaCategoriaMouseClicked
        // TODO add your handling code here:
         StringBuilder mensaje = new StringBuilder();
        if (tablaCategoriasPorUsuario.getSelectedRow() == - 1)
            mensaje.append("Selecciona fila para continuar ");
        
        if(!mensaje.toString().equals("")){
             JOptionPane.showMessageDialog(null, mensaje.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int seleccion = JOptionPane.showOptionDialog(this, "¿Eliminar registro: " + (String.valueOf(tablaCategoriasPorUsuario.getValueAt(tablaCategoriasPorUsuario.getSelectedRow(), 2))) + "?", "Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "No");
        if ((seleccion + 1) == 1) {     
             try {        
                funcion.DeleteRegistro("asigna_categoria", "id_asigna_categoria", tablaCategoriasPorUsuario.getValueAt(tablaCategoriasPorUsuario.getSelectedRow(), 0).toString()); //tabla,columna,id
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Ocurrio un error \n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            }
            
             this.mostrarCategoriasPorUsuario();
        }
        
    }//GEN-LAST:event_lblEliminaAsignaCategoriaMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JCheckBox check_administrador;
    private javax.swing.JCheckBox check_apellidos;
    private javax.swing.JCheckBox check_nivel_1;
    private javax.swing.JCheckBox check_nombre;
    private javax.swing.JComboBox cmb_puesto;
    private javax.swing.JComboBox<String> comboCategorias;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbtn_agregar;
    private javax.swing.JButton jbtn_cambiar_contraseña;
    private javax.swing.JButton jbtn_editar;
    private javax.swing.JButton jbtn_eliminar;
    private javax.swing.JButton jbtn_guardar;
    private javax.swing.JButton jbtn_nuevo;
    private javax.swing.JLabel lblEliminaAsignaCategoria;
    private javax.swing.JLabel lblEncontrados;
    private javax.swing.JLabel lblInfoAsingaCategorias;
    private javax.swing.JLabel lblNombreUsuario;
    private javax.swing.JLabel lbl_puesto;
    private javax.swing.JPanel panelCategoriasPorUsuarios;
    private javax.swing.JPanel panel_botones;
    private javax.swing.JPanel panel_datos;
    private javax.swing.JTable tablaCategoriasPorUsuario;
    private javax.swing.JTable tabla_usuarios;
    private javax.swing.JTextField txt_apellidos;
    private javax.swing.JTextField txt_buscar;
    private javax.swing.JPasswordField txt_contraseña;
    private javax.swing.JTextField txt_direccion;
    private javax.swing.JTextField txt_nombre;
    private javax.swing.JTextField txt_tel_casa;
    private javax.swing.JTextField txt_tel_movil;
    private javax.swing.JPasswordField txt_verificar_contraseña;
    // End of variables declaration//GEN-END:variables

    private void setLocationRelativeTo(Object object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
