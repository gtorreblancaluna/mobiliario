package mobiliario;

import common.constants.ApplicationConstants;
import exceptions.DataOriginException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import static mobiliario.IndexForm.lbl_logueo;
import static mobiliario.IndexForm.lblPuesto;
import common.model.Usuario;
import org.apache.log4j.Logger;
import org.jvnet.substance.SubstanceLookAndFeel;
import services.UserService;
import utilities.Utility;

public class iniciar_sesion extends javax.swing.JFrame {
//initializing the logger
private static Logger log = Logger.getLogger(iniciar_sesion.class.getName());

    private IndexForm ventana_principal;
    private static final UserService userService = UserService.getInstance();
    private Timer tiempo;
    int cont;
    public final static int TWO_SECOND = 8;

    public static String id_usuario_global; // variable publica global para asignar el id del usuario
    public static String nombre_usuario_global, apellidos_usuario_global, administrador_global, id_puesto_global;
    
    public static Usuario usuarioGlobal;
    
    public iniciar_sesion() {
        
        
//        funcion.conectate();
        initComponents();
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png"));
        setIconImage(icon);
        this.setLocationRelativeTo(null);
        barrita.setVisible(false);
        JFrame.setDefaultLookAndFeelDecorated(true);
        SubstanceLookAndFeel.setSkin("org.jvnet.substance.skin.MistAquaSkin");
        
        
    }


    class TimerListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            cont++;
            barrita.setValue(cont);
            if (cont == 100) {
                tiempo.stop();
                esconder();
                ventana_principal = new IndexForm();
                ventana_principal.setVisible(true);
                setVisible(false);

            }
        }

    }

    public void esconder() {
        this.setVisible(false);
    }

    public void activar() {
        tiempo.start();
    }
    
    public void entrar() {
        log.debug("iniciando sesion ");       
        String pswd = new String(this.txt_contraseña.getPassword());
        //String area = null, priv = null;
        try {
            usuarioGlobal = userService.obtenerUsuarioPorPassword(pswd);
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
            log.error(e);
            return;
        }
        if(usuarioGlobal == null || usuarioGlobal.getNombre().equals("")){
            JOptionPane.showMessageDialog(null, ApplicationConstants.DS_MESSAGE_FAIL_LOGIN, ApplicationConstants.TITLE_MESSAGE_FAIL_LOGIN, JOptionPane.ERROR_MESSAGE);
            this.txt_contraseña.setText("");
            this.txt_contraseña.requestFocus();
            return;
        }                 
        id_usuario_global = usuarioGlobal.getUsuarioId()+"";    
        nombre_usuario_global = usuarioGlobal.getNombre();
        apellidos_usuario_global = usuarioGlobal.getApellidos();
        administrador_global = usuarioGlobal.getAdministrador();
        id_puesto_global = usuarioGlobal.getPuesto().getPuestoId()+""; 
        
        barrita.setVisible(true);
        cont = -1;
        barrita.setValue(0);
        barrita.setStringPainted(true);
        tiempo = new Timer(TWO_SECOND, new TimerListener());
        activar();
        this.txt_contraseña.setText("");
       
    }
    
    public static boolean dataSessionUptade(String password){
        Usuario user;
        try {
            user = userService.obtenerUsuarioPorPassword(password);
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        if(user != null && !user.getNombre().equals("")){
            String msgUpdateSession = "Actualización sesión: "+user.getNombre()+" "+user.getApellidos();
            log.info(msgUpdateSession);
            Utility.pushNotification(msgUpdateSession);
            usuarioGlobal = user;
            id_usuario_global = user.getUsuarioId()+"";    
            nombre_usuario_global = user.getNombre();
            apellidos_usuario_global = user.getApellidos();
            administrador_global = user.getAdministrador();
            id_puesto_global = user.getPuesto().getPuestoId()+"";
            
            lbl_logueo.setText(user.getNombre()+" "+user.getApellidos());
            lblPuesto.setText(user.getPuesto().getDescripcion());
            return true;
        }
        
        return false;
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPasswordField1 = new javax.swing.JPasswordField();
        txt_contraseña = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jbtn_entrar = new javax.swing.JButton();
        barrita = new javax.swing.JProgressBar();

        jPasswordField1.setText("jPasswordField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LOGUEO....");

        txt_contraseña.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 14)); // NOI18N
        txt_contraseña.setToolTipText("Presiona la tecla Enter para ingresar");
        txt_contraseña.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_contraseñaKeyPressed(evt);
            }
        });

        jLabel1.setText("Introduce tu contraseña:");

        jbtn_entrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Actions-arrow-right-icon.png"))); // NOI18N
        jbtn_entrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_entrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(barrita, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txt_contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jbtn_entrar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 47, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtn_entrar, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barrita, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_contraseñaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_contraseñaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            if (txt_contraseña.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Introduce tu contraseña para ingresar ", "Error", JOptionPane.INFORMATION_MESSAGE);
            } else {
                entrar();
            }
        }
    }//GEN-LAST:event_txt_contraseñaKeyPressed

    private void jbtn_entrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_entrarActionPerformed
        // TODO add your handling code here:
        if (txt_contraseña.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Introduce tu contraseña para ingresar ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
            entrar();
        }
    }//GEN-LAST:event_jbtn_entrarActionPerformed

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
            java.util.logging.Logger.getLogger(iniciar_sesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(iniciar_sesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(iniciar_sesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(iniciar_sesion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new iniciar_sesion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barrita;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JButton jbtn_entrar;
    private javax.swing.JPasswordField txt_contraseña;
    // End of variables declaration//GEN-END:variables
}
