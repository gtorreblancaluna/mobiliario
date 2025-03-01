package clases;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import javax.activation.DataHandler;  //para enviar imagen adjunta
//import javax.activation.FileDataSource; //para enviar imagen adjunta
import java.util.Date;
import javax.mail.Message;
import javax.mail.Session;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.swing.JOptionPane;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import utilities.Utility;

/**
 *
 * @author SOPORTE KONESH
 */
public class Mail {

    private String from = "";//tu_correo@gmail.com
    private String password = "";//tu password: 123456  
    // destinatario1@hotmail.com,destinatario2@hotmail.com, destinatario_n@hotmail.com
    private InternetAddress[] addressTo;
    private String Subject = "";//titulo del mensaje
    private String MessageMail = "";//contenido del mensaje
    private String Adjunto = "";
    
    private static String SERVIDOR_EMAIL = "";
    private static Boolean UTILIZA_CONEXION_TLS = false;
    private static Boolean UTILIZA_AUTENTICACION = false;
    private static String USUARIO = "usuario";
    private static String PUERTO_EMAIL;
    private static String CUENTA_EMISOR = "";
    private static String PASS_EMISOR = "";
    private static String mailToSend = "";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Mail.class.getName());

    
    public void getPropertiesConection(){
        conectate conectate = new conectate();
         try {        
            conectate.conectate();
        
         
            Connection con = conectate.getConnection();
            Statement s = con.createStatement();
            ResultSet res = s.executeQuery("SELECT * FROM email");

            res.next();
            CUENTA_EMISOR = (res.getString("cuenta_correo"));
            PASS_EMISOR = (res.getString("contrasenia"));
            System.out.println("Pass: " + PASS_EMISOR);

            SERVIDOR_EMAIL = (res.getString("servidor"));
            PUERTO_EMAIL = (res.getString("puerto"));
            String utiliza_conexion_TLS = (res.getString("utiliza_conexion_TLS"));
            String utiliza_autenticacion = (res.getString("utiliza_autenticacion"));
            
            if(utiliza_autenticacion.equals("1")){
                UTILIZA_AUTENTICACION = true;
            }
            if(utiliza_conexion_TLS.equals("1")){
                UTILIZA_CONEXION_TLS = true;
            }
            
            this.setPassword(PASS_EMISOR);
            this.setFrom(CUENTA_EMISOR);
            
            res.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog( null,"No se puede establecer la comunicacion con la bd:\n"+e);
        }  catch (Exception e) {
             JOptionPane.showMessageDialog( null,"Ocurrio un error inesperado, porfavor intentalo de nuevo, verifica tu conexion a internet\n"+e);
        }
         
         
    }
    
    
    public void SEND() {
        new Thread(() -> {
            this.getPropertiesConection();

            try {

                Properties props = new Properties();
                props.put("mail.smtp.host", SERVIDOR_EMAIL);
                props.put("mail.smtp.starttls.enable", UTILIZA_CONEXION_TLS);
                props.put("mail.smtp.auth", UTILIZA_AUTENTICACION);
                props.put("mail.smtp.user", USUARIO);
                props.put("mail.smtp.port", PUERTO_EMAIL);
                //
                SMTPAuthenticator auth = new SMTPAuthenticator(getFrom(), getPassword());
                Session session = Session.getDefaultInstance(props, auth);
                session.setDebug(false);
                //Se crea destino y origen del mensaje
                MimeMessage mimemessage = new MimeMessage(session);
                InternetAddress addressFrom = new InternetAddress(getFrom());
                mimemessage.setFrom(addressFrom);
                mimemessage.setRecipients(Message.RecipientType.TO, addressTo);
                mimemessage.setSubject(getSubject());
                // Se crea el contenido del mensaje
                BodyPart texto = new MimeBodyPart();
                String m = "";
                BodyPart archivo = new MimeBodyPart();
                System.out.println("ADJUNTO ES: " + Adjunto);

                if (Adjunto != "") {
//                    archivo.setDataHandler(new DataHandler(new FileDataSource(Adjunto)));
                   // archivo.setDataHandler(new DataHandler(new FileDataSource("C:/reportes_mobiliario/reporte_consulta.pdf")));
                    //C:\reporte_mobiliario\reporte.pdf
                    String[] tmp = Adjunto.split("/");

                    int j = 0;
                    while (j < tmp.length) {
                        System.out.println("dentro while: " + tmp[j]);
                        j++;
                    }

                    Adjunto = tmp[j-1];
                    System.out.println("Adjunto: " + Adjunto);

                    archivo.setFileName(Adjunto);
                    //archivo.setFileName("reporte_consulta.pdf");

                    MimeBodyPart mimebodypart = new MimeBodyPart();
                    mimebodypart.setText(getMessage());
                    mimebodypart.setContent(getMessage(), "text/html");
                    Multipart multipart = new MimeMultipart();
                    multipart.addBodyPart(mimebodypart);
                    multipart.addBodyPart(archivo);
                    mimemessage.setContent(multipart);
                    mimemessage.setSentDate(new Date());
                    Transport.send(mimemessage);
                    Utility.pushNotification("Correo enviado con éxito a "+mailToSend);
                     LOG.info("Correo a sido enviado con exito a "+mailToSend);
                } else {
                    MimeBodyPart mimebodypart = new MimeBodyPart();
                    mimebodypart.setText(getMessage());
                    mimebodypart.setContent(getMessage(), "text/html");
                    Multipart multipart = new MimeMultipart();
                    multipart.addBodyPart(mimebodypart);
                    //            multipart.addBodyPart(archivo);
                    mimemessage.setContent(multipart);
                    mimemessage.setSentDate(new Date());
                    Transport.send(mimemessage);
    //                JOptionPane.showMessageDialog(null, "Correo enviado", "Correo", JOptionPane.INFORMATION_MESSAGE);


                     Utility.pushNotification("Correo enviado con éxito a "+mailToSend);
                     LOG.info("Correo a sido enviado con exito a "+mailToSend);
                }
            } catch (MessagingException ex) {
//                System.out.println(ex);
//                JOptionPane.showMessageDialog(null, "No se pudo enviar correo,error:" + ex, "Error", JOptionPane.ERROR_MESSAGE);
                LOG.error("Fallo envio de correo \n"+ex);
                 Utility.pushNotification("Fallo el envio de correo a "+mailToSend);
                 Utility.pushNotification(ex.toString());
            }

        }).start();
    }

    //remitente
    public void setFrom(String mail) {
        this.from = mail;
    }

    public String getFrom() {
        return this.from;
    }

    //Contraseña
    /*public void setPassword(char[] value) {
     this.password = new String(value);
     }*/
    public void setPassword(String value) {
        this.password = new String(value);
    }

    public String getPassword() {
        return this.password;
    }

    //destinatarios
    public void setTo(String mails) {
        mailToSend = mails;
        String[] tmp = mails.split(";");
        addressTo = new InternetAddress[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            try {
                addressTo[i] = new InternetAddress(tmp[i].trim());
            } catch (AddressException ex) {
                System.out.println(ex);
            }
        }
    }

    public InternetAddress[] getTo() {
        return this.addressTo;
    }

    //titulo correo
    public void setSubject(String value) {
        this.Subject = value;
    }

    public String getSubject() {
        return this.Subject;
    }

    //contenido del mensaje
    public void setMessage(String value) {
        this.MessageMail = value;
    }

    public String getMessage() {
        return this.MessageMail;
    }

    public void setArchive(String value) {
        this.Adjunto = value;
    }

    public String getArchive() {
        return this.Adjunto;
    }
}
