/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobiliario;

import forms.inventario.InventarioForm;
import clases.JCMail_enviar_prueba;
import clases.conectate;
import clases.sqlclass;
import exceptions.BusinessException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import model.DatosGenerales;
import services.SystemService;

/**
 *
 * @author Carlos Alberto
 */
public class utilerias extends java.awt.Dialog {

    sqlclass funcion = new sqlclass();
    conectate conexion = new conectate();

    Object[][] dtconduc;
    boolean existe, editar = false;
    String id_categoria;
    public static boolean utiliza_conexion_TLS = false, utiliza_autenticacion = false, status;
    private final SystemService systemService = SystemService.getInstance();

    /**
     * Creates new form Colores
     */
    public utilerias(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(parent);
       
        editarDatosEmail_no();
        traer_datos_email();
        datosGenerales();
        getDataConfiguration();
        initFormDataConfiguration();

        txt_folio.setEditable(false);

        funcion.conectate();
        txt_folio.setText((String) funcion.GetData("folio", "select folio from datos_generales").toString());
        funcion.desconecta();
        
        

        jbtn_guardar.setEnabled(false);
        this.btnSaveConfiguration.setEnabled(false);
        
        // informacion de lanzamientos de version
        this.info.setText("VERSION RELEASE [1.3] 2019.01.20 GTL\n"
                + "1. se agrega fecha del evento\n"
                +"2. consultar por fecha del evento\n"
                +"3. se realiza el reporte por categorias\n"
                +"4. permitir agregar mas articulos al pedido de los que se tienen en inventario\n"
                +"\n"
                + "VERSION RELEASE [1.4] 2019.01.28 GTL \n"
                + "1. aplicar descuentos por articulo vista y PDF\n"
                +"2. aplicar descuento al subtotal, vista y PDF\n"
                +"3. aplicar deposito en garantia, vista y PDF\n"
                +"4. aplicar envio y recoleccion, vista y PDF\n"
                +"5. corregir modificar cliente a la nota\n"
                +"\n"
                + "VERSION RELEASE [1.4.2] 2019.01.31 GTL \n"
                +"1. filtrar en disponibilidad de articulos solo por PEDIDO\n"
                +"2. en varias vistas aplicar la seguridad por nivel de administrador, bloquearemos acciones como eliminar, nuevo, modificar a usuarios nivel 1\n"
                +"3. en el PDF, quitar los ceros y dejarlo vacio en DESCUENTO\n"
                +"4. las tablas podran ordenarse cuando clickeas el encabezado\n"
                +"5. poder actualizar la cantidad y descuento de articulos en el detalle del evento, al momento de editar un evento\n"
                +"\n"
                + "VERSION RELEASE [1.4.2.1] 2019.02.10 GTL \n"
                +"1. PDFS incluir POLITICAS Y REGLAS DE  NEGOCIO\n"
                +"2. corregir que permita agregar descuento hasta 100% en la parte de consultar renta\n"
                +"3. agregar leyenda al CONSULTAR PEDIDOS, (PAGADO, NO PAGADO, PARCIALMENTE)\n"
                +"4. titulo en la ventana\n"
                +"5. ocultar en PDF leyenda de DESCUENTO, ETC\n"
                +"6. si se cierra una conexion a la bd, vamos a reintentar conectar a bd\n"
                +"\n"
                + "VERSION RELEASE [1.4.2.2] 2019.02.15 GTL \n"
                +"1. ingresar el tipo de pago\n"
                +"2. corregir la busqueda desde CONSULTAR RENTA, en el nombre de cliente\n"
                +"3. quitar encabezados del PDF cuando el pedido este con , no mostrar precios\n"
                +"4. corregir el tema de articulos en renta\n"
                 +"\n"
                + "VERSION RELEASE [1.4.2.3] 2019.03.01 GTL \n"
                +"1. ajustar y corregir la consulta de disponibilidad\n"
                +"2. corregir al agregar abono desde CONSULTA PEDIDO\n"
                +"3. corregir el calculo de total desde CONSULTA PEDIDO\n"
                +"4. corregir la duplicacion de articulos en la consulta del inventario\n"
                 +"\n"
                + "VERSION RELEASE [1.4.2.4] 2019.03.07 GTL \n"
                +"1. se agrega fecha del evento en el PDF de entregas\n"
                +"2. se reemplaza la leyenda 'Fecha del pedido' por 'Fecha registro' en todos los PDF\n"
                +"3. el reporte de pagos, se optimiza y ademas se agrega fecha del pago y tipo de pago\n"
                +"4. se optimiza y se agrega en la consulta de disponibilidad nuevos parametros para realizar el reporte\n"
                 +"\n"
                + "VERSION RELEASE [1.4.3] 2019.03.12 GTL \n"
                +"1. modulo completo de faltantes/reparacion/devoluciones\n"
                
        );
        // fin informacion de version
        this.info.setEditable(false);
    }
    
    public void initFormDataConfiguration(){
        this.btnSave.setEnabled(false);
        this.txtEmailCompras.setEnabled(false);
    }
    
    public void getDataConfiguration(){
        String emailCompras=null;
        try{
            emailCompras = 
                    systemService.getDataConfigurationByKey(ApplicationConstants.SYSTEM_EMAIL_COMPRAS);
        }catch(BusinessException e){
            JOptionPane.showMessageDialog( this,e.getMessage());
        }
        this.txtEmailCompras.setText(emailCompras);
        
    }

    public void conectar() {
        // conexion.setIp(IpServer);
          try {        
            conexion.conectate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog( null,"No se puede establecer la comunicacion con la bd:\n"+e);
        }  catch (Exception e) {
             JOptionPane.showMessageDialog( null,"Ocurrio un error inesperado, porfavor intentalo de nuevo, verifica tu conexion a internet\n"+e);
        }
    }
    
    public void datosGenerales(){
        DatosGenerales datosGenerales = systemService.getGeneralData();
        this.txtCompanyName.setText(datosGenerales.getCompanyName());
        this.txtAdress.setText(datosGenerales.getAddress1());
        this.txtTels.setText(datosGenerales.getAddress2());
        
        this.txtCompanyName.setEnabled(false);
        this.txtAdress.setEnabled(false);
        this.txtTels.setEnabled(false);
        this.btnSave.setEnabled(false);
        this.btnEdit.setEnabled(true);
    }
    
    public void editDatosGenerales(){
    
        this.txtCompanyName.setEnabled(true);
        this.txtAdress.setEnabled(true);
        this.txtTels.setEnabled(true);
        this.btnSave.setEnabled(true);
        this.btnEdit.setEnabled(false);
    }
    
    public void saveDatosGenerales(){
        
        DatosGenerales datosGenerales = new DatosGenerales();
        datosGenerales.setCompanyName(this.txtCompanyName.getText());
        datosGenerales.setAddress1(this.txtAdress.getText());
        datosGenerales.setAddress2(this.txtTels.getText());
        systemService.saveDatosGenerales(datosGenerales);
        
        this.txtCompanyName.setEnabled(false);
        this.txtAdress.setEnabled(false);
        this.txtTels.setEnabled(false);
        this.btnSave.setEnabled(false);
        this.btnEdit.setEnabled(true);
    }

    public void editarDatosEmail() {
        this.txt_cuenta_email.setEditable(true);
        this.txt_contraseña_email.setEditable(true);
        this.txt_verificar_contraseña_email.setEditable(true);
        this.txt_servidor_email.setEditable(true);
        this.txt_puerto_email.setEditable(true);
        this.check_gmail.setEnabled(true);
        this.check_hotmail.setEnabled(true);
        this.check_personalizada.setEnabled(true);
        

        this.check_conexion_tls.setEnabled(true);
        this.check_autenticacion.setEnabled(true);
    }

    public void editarDatosEmail_no() {
        this.txt_cuenta_email.setEditable(false);
        this.txt_contraseña_email.setEditable(false);
        this.txt_verificar_contraseña_email.setEditable(false);
        this.txt_servidor_email.setEditable(false);
        this.txt_puerto_email.setEditable(false);
        this.check_gmail.setEnabled(false);
        this.check_hotmail.setEnabled(false);
        this.check_personalizada.setEnabled(false);

        this.check_conexion_tls.setEnabled(false);
        this.check_autenticacion.setEnabled(false);

    }

    public void traer_datos_email() {
        byte[] img = null;
        conectar();
        try {
            String conexion_TLS, autenticacion, gmail, hotmail, personalizada;
            Connection con = conexion.getConnection();
            Statement s = con.createStatement();
            ResultSet res = s.executeQuery("SELECT * FROM email");

            res.next();
            this.txt_cuenta_email.setText(res.getString("cuenta_correo"));
            this.txt_contraseña_email.setText(res.getString("contrasenia"));
            this.txt_verificar_contraseña_email.setText(res.getString("contrasenia"));
            this.txt_servidor_email.setText(res.getString("servidor"));
            this.txt_puerto_email.setText(res.getString("puerto"));
            conexion_TLS = (res.getString("utiliza_conexion_TLS"));
            autenticacion = (res.getString("utiliza_autenticacion"));
            gmail = (res.getString("gmail"));
            hotmail = (res.getString("hotmail"));
            personalizada = (res.getString("personalizada"));

            if (conexion_TLS.equals("1")) {
                this.check_conexion_tls.setSelected(true);
            }
            if (autenticacion.equals("1")) {
                this.check_autenticacion.setSelected(true);
            }

            if (gmail.equals("1")) {
                this.check_gmail.setSelected(true);
            }
            if (hotmail.equals("1")) {
                this.check_hotmail.setSelected(true);
            }
            if (personalizada.equals("1")) {
                this.check_personalizada.setSelected(true);
            }

            res.close();

        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public void guardar() {
        int folio = Integer.parseInt(txt_folio.getText().toString());
        if (txt_folio.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "No puede ir vacio...", "Error...", JOptionPane.INFORMATION_MESSAGE);

        } else {

            if (folio > 0 && folio < 99999) {
                int seleccion = JOptionPane.showOptionDialog(this, "Esta accion reestablecera el folio, no se podra deshacer, confirma para continuar?", "Reestablecer folio..???", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
                if (seleccion == 0) {//presiono que si

                    funcion.conectate();
                    String datos[] = {txt_folio.getText().toString(), "1"};
                    try {
                        funcion.UpdateRegistro(datos, "update datos_generales set folio=?, folio_cambio=?");
                    } catch (SQLNonTransientConnectionException e) {
                        funcion.conectate();
                        JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                    }
                    

                    funcion.desconecta();

                    jbtn_guardar.setEnabled(false);
                    jbtn_editar.setEnabled(true);
                    txt_folio.setEditable(false);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Fuera de rango ¡  ops  !.", "Error...", JOptionPane.INFORMATION_MESSAGE);
            }
        }

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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        txt_folio = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jbtn_editar = new javax.swing.JButton();
        jbtn_guardar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        subPanel_configuracion_email = new javax.swing.JPanel();
        check_personalizada = new javax.swing.JCheckBox();
        check_hotmail = new javax.swing.JCheckBox();
        check_gmail = new javax.swing.JCheckBox();
        txt_cuenta_email = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        txt_servidor_email = new javax.swing.JTextField();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        txt_puerto_email = new javax.swing.JTextField();
        check_conexion_tls = new javax.swing.JCheckBox();
        check_autenticacion = new javax.swing.JCheckBox();
        txt_contraseña_email = new javax.swing.JPasswordField();
        txt_verificar_contraseña_email = new javax.swing.JPasswordField();
        panel_enviar_prueba = new javax.swing.JPanel();
        txt_enviar_a = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        txt_asunto = new javax.swing.JTextField();
        jScrollPane21 = new javax.swing.JScrollPane();
        txt_mensaje = new javax.swing.JTextPane();
        jLabel101 = new javax.swing.JLabel();
        Jbnt_enviar_prueba = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        Jbtn_editar_email = new javax.swing.JButton();
        Jbtn_cancelar_email = new javax.swing.JButton();
        Jbtn_guardar_email = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        info = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        lblCompanyName = new javax.swing.JLabel();
        txtCompanyName = new javax.swing.JTextField();
        lblAdress = new javax.swing.JLabel();
        txtAdress = new javax.swing.JTextField();
        lblTels = new javax.swing.JLabel();
        txtTels = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtEmailCompras = new javax.swing.JTextField();
        btnSaveConfiguration = new javax.swing.JButton();
        btnEditConfiguration = new javax.swing.JButton();

        setLocationRelativeTo(jLabel1);
        setTitle("Utilerias");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setToolTipText("Folio");
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_folio.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_folio.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_folio.setToolTipText("");
        txt_folio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_folioKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_folioKeyTyped(evt);
            }
        });
        jPanel1.add(txt_folio, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, 109, -1));

        jLabel1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel1.setText("Folio:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, -1, -1));
        jLabel1.getAccessibleContext().setAccessibleName("Folio:asd");

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        jbtn_editar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Actions-edit-icon.png"))); // NOI18N
        jbtn_editar.setToolTipText("Editar");
        jbtn_editar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_editar.setFocusable(false);
        jbtn_editar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_editar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_editarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_editar);

        jbtn_guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/floppy-disk-icon.png"))); // NOI18N
        jbtn_guardar.setToolTipText("Guardar");
        jbtn_guardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_guardar.setFocusable(false);
        jbtn_guardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_guardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_guardarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_guardar);

        jPanel1.add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 330));

        jTabbedPane1.addTab("Folio", jPanel1);

        subPanel_configuracion_email.setBorder(javax.swing.BorderFactory.createTitledBorder("Configurar email"));
        subPanel_configuracion_email.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonGroup1.add(check_personalizada);
        check_personalizada.setText("Personalizada");
        check_personalizada.setToolTipText("Indica si es otra tipo de cuenta");
        check_personalizada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_personalizadaActionPerformed(evt);
            }
        });
        subPanel_configuracion_email.add(check_personalizada, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, -1, -1));

        buttonGroup1.add(check_hotmail);
        check_hotmail.setText("Hotmail");
        check_hotmail.setToolTipText("Indica si es hotmail");
        check_hotmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_hotmailActionPerformed(evt);
            }
        });
        subPanel_configuracion_email.add(check_hotmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        buttonGroup1.add(check_gmail);
        check_gmail.setText("Gmail");
        check_gmail.setToolTipText("Indica si es gmail");
        check_gmail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                check_gmailMouseClicked(evt);
            }
        });
        check_gmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_gmailActionPerformed(evt);
            }
        });
        subPanel_configuracion_email.add(check_gmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        txt_cuenta_email.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_cuenta_emailFocusGained(evt);
            }
        });
        subPanel_configuracion_email.add(txt_cuenta_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 317, -1));

        jLabel93.setText("Cuenta de correo:");
        subPanel_configuracion_email.add(jLabel93, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        jLabel94.setText("Contraseña:");
        subPanel_configuracion_email.add(jLabel94, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 60, -1, -1));

        jLabel95.setText("Confirmar contraseña:");
        subPanel_configuracion_email.add(jLabel95, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 60, -1, -1));

        txt_servidor_email.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_servidor_emailFocusGained(evt);
            }
        });
        subPanel_configuracion_email.add(txt_servidor_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 254, -1));

        jLabel96.setText("Servidor:");
        subPanel_configuracion_email.add(jLabel96, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        jLabel97.setText("Puerto:");
        subPanel_configuracion_email.add(jLabel97, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 110, -1, -1));

        txt_puerto_email.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_puerto_emailFocusGained(evt);
            }
        });
        subPanel_configuracion_email.add(txt_puerto_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, 166, -1));

        check_conexion_tls.setText("Utiliza conexion TLS");
        subPanel_configuracion_email.add(check_conexion_tls, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, -1));

        check_autenticacion.setText("Utiliza Autenticación");
        subPanel_configuracion_email.add(check_autenticacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 170, -1, -1));

        txt_contraseña_email.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_contraseña_emailFocusGained(evt);
            }
        });
        subPanel_configuracion_email.add(txt_contraseña_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, 126, -1));

        txt_verificar_contraseña_email.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_verificar_contraseña_emailFocusGained(evt);
            }
        });
        subPanel_configuracion_email.add(txt_verificar_contraseña_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 151, -1));

        panel_enviar_prueba.setBorder(javax.swing.BorderFactory.createTitledBorder("Enviar prueba"));
        panel_enviar_prueba.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_enviar_a.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_enviar_aActionPerformed(evt);
            }
        });
        panel_enviar_prueba.add(txt_enviar_a, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 613, -1));

        jLabel99.setText("Enviar a:");
        panel_enviar_prueba.add(jLabel99, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel100.setText("Asunto:");
        panel_enviar_prueba.add(jLabel100, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));
        panel_enviar_prueba.add(txt_asunto, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 613, -1));

        jScrollPane21.setViewportView(txt_mensaje);

        panel_enviar_prueba.add(jScrollPane21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 613, 66));

        jLabel101.setText("Mensaje:");
        panel_enviar_prueba.add(jLabel101, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        Jbnt_enviar_prueba.setText("Enviar prueba");
        Jbnt_enviar_prueba.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Jbnt_enviar_pruebaActionPerformed(evt);
            }
        });
        panel_enviar_prueba.add(Jbnt_enviar_prueba, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 210, 100, 25));

        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar2.setRollover(true);

        Jbtn_editar_email.setText("Editar");
        Jbtn_editar_email.setToolTipText("Editar");
        Jbtn_editar_email.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Jbtn_editar_email.setName(""); // NOI18N
        Jbtn_editar_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Jbtn_editar_emailActionPerformed(evt);
            }
        });
        jToolBar2.add(Jbtn_editar_email);

        Jbtn_cancelar_email.setText("Cancelar");
        Jbtn_cancelar_email.setToolTipText("cancelar");
        Jbtn_cancelar_email.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Jbtn_cancelar_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Jbtn_cancelar_emailActionPerformed(evt);
            }
        });
        jToolBar2.add(Jbtn_cancelar_email);

        Jbtn_guardar_email.setText("Guardar");
        Jbtn_guardar_email.setToolTipText("Guardar");
        Jbtn_guardar_email.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Jbtn_guardar_email.setFocusable(false);
        Jbtn_guardar_email.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Jbtn_guardar_email.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Jbtn_guardar_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Jbtn_guardar_emailActionPerformed(evt);
            }
        });
        jToolBar2.add(Jbtn_guardar_email);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_enviar_prueba, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subPanel_configuracion_email, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(subPanel_configuracion_email, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel_enviar_prueba, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Email", jPanel3);

        info.setColumns(20);
        info.setRows(5);
        jScrollPane1.setViewportView(info);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("version release", jPanel4);

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos de la empresa"));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblCompanyName.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblCompanyName.setText("Nombre empresa:");
        jPanel6.add(lblCompanyName, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, -1, -1));

        txtCompanyName.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtCompanyName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCompanyNameKeyPressed(evt);
            }
        });
        jPanel6.add(txtCompanyName, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 500, -1));

        lblAdress.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblAdress.setText("Dirección:");
        jPanel6.add(lblAdress, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 220, -1));

        txtAdress.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtAdress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAdressKeyPressed(evt);
            }
        });
        jPanel6.add(txtAdress, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 500, -1));

        lblTels.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblTels.setText("Telefónos:");
        jPanel6.add(lblTels, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        txtTels.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtTels.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTelsKeyPressed(evt);
            }
        });
        jPanel6.add(txtTels, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 500, -1));

        btnSave.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnSave.setText("Guardar");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel6.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 220, -1, -1));

        btnEdit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnEdit.setText("Editar");
        btnEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jPanel6.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, -1, -1));

        jPanel5.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 8, 710, 440));

        jTabbedPane1.addTab("Datos generales", jPanel5);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Email compras:");

        txtEmailCompras.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        btnSaveConfiguration.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnSaveConfiguration.setText("Guardar");
        btnSaveConfiguration.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnEditConfiguration.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnEditConfiguration.setText("Editar");
        btnEditConfiguration.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditConfigurationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmailCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(btnSaveConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditConfiguration, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(237, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtEmailCompras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 228, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveConfiguration)
                    .addComponent(btnEditConfiguration))
                .addGap(142, 142, 142))
        );

        jTabbedPane1.addTab("Configuración", jPanel7);

        jPanel2.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 750, 500));
        jTabbedPane1.getAccessibleContext().setAccessibleName("Folio");
        jTabbedPane1.getAccessibleContext().setAccessibleDescription("Folio");

        add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        InventarioForm.validar_categorias = true;
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void jbtn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_guardarActionPerformed
        // TODO add your handling code here:
        guardar();


    }//GEN-LAST:event_jbtn_guardarActionPerformed

    private void jbtn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editarActionPerformed
        // TODO add your handling code here:
        txt_folio.setEditable(true);
        jbtn_guardar.setEnabled(true);
        jbtn_editar.setEnabled(false);

    }//GEN-LAST:event_jbtn_editarActionPerformed

    private void txt_folioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_folioKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {

            guardar();
        }
    }//GEN-LAST:event_txt_folioKeyPressed

    private void txt_folioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_folioKeyTyped
        char car = evt.getKeyChar();
        if ((car < '0' || car > '9')) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_folioKeyTyped

    private void check_personalizadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_personalizadaActionPerformed
        // TODO add your handling code here:
        this.txt_servidor_email.setText("");
        this.txt_puerto_email.setText("");
        this.txt_servidor_email.setText("");
        this.txt_puerto_email.setText("");
        this.check_conexion_tls.setSelected(false);
        this.check_autenticacion.setSelected(false);

        this.check_conexion_tls.setEnabled(true);
        this.check_autenticacion.setEnabled(true);
        this.txt_servidor_email.setEnabled(true);
        this.txt_puerto_email.setEnabled(true);
        this.txt_servidor_email.requestFocus();
    }//GEN-LAST:event_check_personalizadaActionPerformed

    private void check_hotmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_hotmailActionPerformed
        // TODO add your handling code here:
        this.txt_servidor_email.setText("");
        this.txt_puerto_email.setText("");
        this.txt_servidor_email.setText("smtp.live.com");
        this.txt_puerto_email.setText("587");
        this.check_conexion_tls.setSelected(true);
        this.check_autenticacion.setSelected(true);

        this.check_conexion_tls.setEnabled(false);
        this.check_autenticacion.setEnabled(false);
        this.txt_servidor_email.setEnabled(false);
        this.txt_puerto_email.setEnabled(false);
    }//GEN-LAST:event_check_hotmailActionPerformed

    private void check_gmailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_check_gmailMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_check_gmailMouseClicked

    private void check_gmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_gmailActionPerformed
        // TODO add your handling code here:
        this.txt_servidor_email.setText("");
        this.txt_puerto_email.setText("");
        this.txt_servidor_email.setText("smtp.gmail.com");
        this.txt_puerto_email.setText("587");
        this.check_conexion_tls.setSelected(true);
        this.check_autenticacion.setSelected(true);

        this.check_conexion_tls.setEnabled(false);
        this.check_autenticacion.setEnabled(false);

        this.txt_servidor_email.setEnabled(false);
        this.txt_puerto_email.setEnabled(false);
    }//GEN-LAST:event_check_gmailActionPerformed

    private void txt_cuenta_emailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_cuenta_emailFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_cuenta_emailFocusGained

    private void txt_servidor_emailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_servidor_emailFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_servidor_emailFocusGained

    private void txt_puerto_emailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_puerto_emailFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_puerto_emailFocusGained

    private void txt_contraseña_emailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_contraseña_emailFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_contraseña_emailFocusGained

    private void txt_verificar_contraseña_emailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_verificar_contraseña_emailFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_verificar_contraseña_emailFocusGained

    private void Jbnt_enviar_pruebaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Jbnt_enviar_pruebaActionPerformed
        // TODO add your handling code here:
        if (this.txt_enviar_a.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Faltan parametros..");
        } else if (!funcion.isEmail(txt_enviar_a.getText())) {
            JOptionPane.showMessageDialog(null, "Correo destino es incorrecto....");
            txt_enviar_a.requestFocus();
        } else {

            if (this.check_conexion_tls.isSelected()) {
                utiliza_conexion_TLS = true;
            } else {
                utiliza_conexion_TLS = false;
            }

            if (this.check_autenticacion.isSelected()) {
                utiliza_autenticacion = true;
            } else {
                utiliza_autenticacion = false;
            }

            JCMail_enviar_prueba mail_prueba = new JCMail_enviar_prueba();
            mail_prueba.setFrom(this.txt_cuenta_email.getText());
            mail_prueba.setPassword(this.txt_contraseña_email.getPassword());
            mail_prueba.setTo(this.txt_enviar_a.getText());
            mail_prueba.setSubject(this.txt_asunto.getText());
            mail_prueba.setMessage(this.txt_mensaje.getText());
            //mail.setArchive(this.txta.getText());
            mail_prueba.SEND();
//            this.txt_enviar_a.setText("");
//            this.txt_asunto.setText("");
//            this.txt_mensaje.setText("");
        }

        // panel_enviar_prueba.setVisible(false);

    }//GEN-LAST:event_Jbnt_enviar_pruebaActionPerformed

    private void Jbtn_editar_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Jbtn_editar_emailActionPerformed
        // TODO add your handling code here:
        editarDatosEmail();
        this.Jbtn_guardar_email.setVisible(true);
        this.Jbtn_editar_email.setVisible(false);
        Jbtn_cancelar_email.setVisible(true);
    }//GEN-LAST:event_Jbtn_editar_emailActionPerformed

    private void Jbtn_cancelar_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Jbtn_cancelar_emailActionPerformed
        // TODO add your handling code here:
        traer_datos_email();
        editarDatosEmail_no();
        this.Jbtn_cancelar_email.setVisible(false);
        this.Jbtn_editar_email.setVisible(true);
        this.Jbtn_guardar_email.setVisible(false);
    }//GEN-LAST:event_Jbtn_cancelar_emailActionPerformed

    private void Jbtn_guardar_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Jbtn_guardar_emailActionPerformed
        // TODO add your handling code here:
        String conexion_tls = "0", autenticacion = "0", gmail = "0", hotmail = "0", personalizada = "0";
        if (this.check_conexion_tls.isSelected()) {
            conexion_tls = "1";
        }
        if (this.check_autenticacion.isSelected()) {
            autenticacion = "1";
        }
        if (this.check_gmail.isSelected()) {
            gmail = "1";
        }
        if (this.check_hotmail.isSelected()) {
            hotmail = "1";
        }
        if (this.check_personalizada.isSelected()) {
            personalizada = "1";
        }

        if (!this.txt_cuenta_email.getText().equals("") && !this.txt_contraseña_email.getText().equals("") && !this.txt_servidor_email.getText().equals("") && !this.txt_puerto_email.getText().equals("")) {

            if (funcion.isEmail(txt_cuenta_email.getText())) {

                if (this.txt_contraseña_email.getText().equals(this.txt_verificar_contraseña_email.getText())) {

                    funcion.conectate();
                    Integer existe = funcion.existe_email();
                    System.out.println("existe: " + existe);
                    if (existe != null && existe > 0) {
                        String datos[] = {this.txt_cuenta_email.getText(), this.txt_contraseña_email.getText(), this.txt_servidor_email.getText(), this.txt_puerto_email.getText(), conexion_tls, autenticacion, gmail, hotmail, personalizada, existe+""};
                        
                        try {
                            funcion.UpdateRegistro(datos, "UPDATE email set cuenta_correo=?,contrasenia=?,servidor=?,puerto=?,utiliza_conexion_TLS=?,utiliza_autenticacion=?,gmail=?,hotmail=?,personalizada=? where id=? ");
                        } catch (SQLNonTransientConnectionException e) {
                            funcion.conectate();
                            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                        }
                    } else {
                        try {
                            String datos2[] = {this.txt_cuenta_email.getText(), this.txt_contraseña_email.getText(), this.txt_servidor_email.getText(), this.txt_puerto_email.getText(), conexion_tls, autenticacion, gmail, hotmail, personalizada};
                            funcion.InsertarRegistro(datos2, "INSERT INTO email (cuenta_correo,contrasenia,servidor,puerto,utiliza_conexion_TLS,utiliza_autenticacion,gmail,hotmail,personalizada) values (?,?,?,?,?,?,?,?,?) ");
                        } catch (SQLException ex) {
                            Logger.getLogger(utilerias.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "Error al insertar registro ", "Error", JOptionPane.ERROR);
                        }
                    }
                    this.Jbtn_guardar_email.setVisible(false);
                    this.Jbtn_editar_email.setVisible(true);
                    Jbtn_cancelar_email.setVisible(false);
                    editarDatosEmail_no();

                    funcion.desconecta();
                    JOptionPane.showMessageDialog(null, "Se guardo con exito. =) ");

                } else {
                    JOptionPane.showMessageDialog(null, "No coincide la contraseña ");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Formato del email es incorrecto... ");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Faltan parametros..");
        }
    }//GEN-LAST:event_Jbtn_guardar_emailActionPerformed

    private void txt_enviar_aActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_enviar_aActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_enviar_aActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        this.saveDatosGenerales();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        this.editDatosGenerales();
    }//GEN-LAST:event_btnEditActionPerformed

    private void txtCompanyNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCompanyNameKeyPressed
        
         if (evt.getKeyCode() == 10) {
             this.saveDatosGenerales();
         }
    }//GEN-LAST:event_txtCompanyNameKeyPressed

    private void txtAdressKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAdressKeyPressed
        
         if (evt.getKeyCode() == 10) {
             this.saveDatosGenerales();
         }
    }//GEN-LAST:event_txtAdressKeyPressed

    private void txtTelsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelsKeyPressed
        // TODO add your handling code here:
         if (evt.getKeyCode() == 10) {
             this.saveDatosGenerales();
         }
    }//GEN-LAST:event_txtTelsKeyPressed

    private void btnEditConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditConfigurationActionPerformed
        // TODO add your handling code here:
        
        this.btnSaveConfiguration.setEnabled(true);
        this.txtEmailCompras.setEnabled(true);
    }//GEN-LAST:event_btnEditConfigurationActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                utilerias dialog = new utilerias(new java.awt.Frame(), true);
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
    private javax.swing.JButton Jbnt_enviar_prueba;
    private javax.swing.JButton Jbtn_cancelar_email;
    private javax.swing.JButton Jbtn_editar_email;
    private javax.swing.JButton Jbtn_guardar_email;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEditConfiguration;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveConfiguration;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox check_autenticacion;
    private javax.swing.JCheckBox check_conexion_tls;
    private javax.swing.JCheckBox check_gmail;
    private javax.swing.JCheckBox check_hotmail;
    private javax.swing.JCheckBox check_personalizada;
    private javax.swing.JTextArea info;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JButton jbtn_editar;
    private javax.swing.JButton jbtn_guardar;
    private javax.swing.JLabel lblAdress;
    private javax.swing.JLabel lblCompanyName;
    private javax.swing.JLabel lblTels;
    private javax.swing.JPanel panel_enviar_prueba;
    private javax.swing.JPanel subPanel_configuracion_email;
    private javax.swing.JTextField txtAdress;
    private javax.swing.JTextField txtCompanyName;
    private javax.swing.JTextField txtEmailCompras;
    private javax.swing.JTextField txtTels;
    public static javax.swing.JTextField txt_asunto;
    public static javax.swing.JPasswordField txt_contraseña_email;
    public static javax.swing.JTextField txt_cuenta_email;
    public static javax.swing.JTextField txt_enviar_a;
    private javax.swing.JTextField txt_folio;
    public static javax.swing.JTextPane txt_mensaje;
    public static javax.swing.JTextField txt_puerto_email;
    public static javax.swing.JTextField txt_servidor_email;
    public static javax.swing.JPasswordField txt_verificar_contraseña_email;
    // End of variables declaration//GEN-END:variables
}
