package clases;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
import forms.settings.SystemSettingsForm;
/**
 *
 * @author SOPORTE KONESH
 */
public class JCMail_enviar_prueba {
    
    private String from = "";//tu_correo@gmail.com
    private String password = "";//tu password: 123456  
    // destinatario1@hotmail.com,destinatario2@hotmail.com, destinatario_n@hotmail.com
    private InternetAddress[] addressTo;
    private String Subject = "";//titulo del mensaje
    private String MessageMail = "";//contenido del mensaje
    private String Adjunto ="";
    sqlclass funcion = new sqlclass();
    public JCMail_enviar_prueba(){}
    
    public void SEND()
    {
        try {
            System.out.println("datos envio de email...");
            System.out.println("SERVIDOR: "+SystemSettingsForm.txt_servidor_email.getText());
            System.out.println("CONEXION TLS: "+SystemSettingsForm.utiliza_conexion_TLS);
             System.out.println("AUTENTICACION: "+SystemSettingsForm.utiliza_conexion_TLS);
             System.out.println("USUARIO: usuario");
             System.out.println("PUERTO: "+SystemSettingsForm.txt_puerto_email.getText());
            Properties props = new Properties();
            props.put("mail.smtp.host", SystemSettingsForm.txt_servidor_email.getText());
            props.put("mail.smtp.starttls.enable", SystemSettingsForm.utiliza_conexion_TLS);
            props.put("mail.smtp.auth", SystemSettingsForm.utiliza_autenticacion);
            props.put("mail.smtp.user", "gtorreblancaluna");
            props.put("mail.smtp.port", SystemSettingsForm.txt_puerto_email.getText());
            //
            SMTPAuthenticator auth = new SMTPAuthenticator( getFrom(), getPassword() );
            Session session = Session.getDefaultInstance(props, auth);
            session.setDebug(false);
            //Se crea destino y origen del mensaje
            MimeMessage mimemessage = new MimeMessage(session);
            InternetAddress addressFrom = new InternetAddress( getFrom() );
            mimemessage.setFrom(addressFrom);
            mimemessage.setRecipients(Message.RecipientType.TO, addressTo);
            mimemessage.setSubject( getSubject() );
            // Se crea el contenido del mensaje
            BodyPart texto = new MimeBodyPart();
            String m="";
            BodyPart archivo = new MimeBodyPart();
            if(Adjunto!=""){
//                            archivo.setDataHandler(new DataHandler(new FileDataSource(Adjunto)));
                            String[] tmp =Adjunto.split("/");
                            int j=0;
                            while( j < tmp.length){
                            System.out.println(tmp[j]);
                            j++;
                            }
                            Adjunto=tmp[1];
                            archivo.setFileName(Adjunto);
                            MimeBodyPart mimebodypart = new MimeBodyPart();
                            mimebodypart.setText( getMessage() );
                            mimebodypart.setContent( getMessage() , "text/html" );
                            Multipart multipart = new MimeMultipart();
                            multipart.addBodyPart(mimebodypart);
                            multipart.addBodyPart(archivo);
                            mimemessage.setContent(multipart);            
                            mimemessage.setSentDate(new Date());
                            Transport.send(mimemessage);
                            JOptionPane.showMessageDialog(null, "Correo enviado","Correo",JOptionPane.INFORMATION_MESSAGE);
            } else{ 
                            MimeBodyPart mimebodypart = new MimeBodyPart();
                            mimebodypart.setText( getMessage() );
                            mimebodypart.setContent( getMessage() , "text/html" );
                            Multipart multipart = new MimeMultipart();
                            multipart.addBodyPart(mimebodypart);
                //            multipart.addBodyPart(archivo);
                            mimemessage.setContent(multipart);            
                            mimemessage.setSentDate(new Date());
                            Transport.send(mimemessage);
                            JOptionPane.showMessageDialog(null, "Correo enviado","Correo",JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (MessagingException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null,"No se pudo enviar correo,error:"+ ex,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    //remitente
    public void setFrom(String mail){ this.from = mail; }
    public String getFrom(){ return this.from; }
    //Contraseña
    public void setPassword(char[] value){
        this.password = new String(value);
    }
    public String getPassword(){ return this.password; }
    //destinatarios
    public void setTo(String mails){
        String[] tmp =mails.split(",");
        addressTo = new InternetAddress[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            try {
                addressTo[i] = new InternetAddress(tmp[i]);
            } catch (AddressException ex) {
                System.out.println(ex);
            }
        }
    }
    public InternetAddress[] getTo(){ return this.addressTo; }
    //titulo correo
    public void setSubject(String value){ this.Subject = value; }
    public String getSubject(){ return this.Subject; }
    //contenido del mensaje
    public void setMessage(String value){ this.MessageMail = value; }
    public String getMessage(){ return this.MessageMail; }
    public void setArchive(String value){this.Adjunto = value;}
    public String getArchive(){return this.Adjunto; }
}
