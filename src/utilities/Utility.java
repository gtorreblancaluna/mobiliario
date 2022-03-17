package utilities;

import java.awt.Component;
import java.awt.Dialog;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.MessagingException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import mobiliario.ApplicationConstants;
import mobiliario.iniciar_sesion;
import mobiliario.principal;
import static mobiliario.principal.jDesktopPane1;


public abstract class Utility {
    
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
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
    
     public static boolean verifyIfInternalFormIsOpen(Object obj) {
        JInternalFrame[] activos = jDesktopPane1.getAllFrames();
        boolean cerrado = true;
        int i = 0;
        while (i < activos.length && cerrado) {
            if (activos[i] == obj) {
                cerrado = false;
            }
            i++;
        }
        return cerrado;
    }
    
    public static void pushNotification(final String notification){
        StringBuilder messages = new StringBuilder();
        
        String date = simpleDateFormat.format(new Timestamp(System.currentTimeMillis()));
        principal.listNotifications.add(date+" >> "+notification);
        principal.listNotifications.stream().forEach(t -> {
            messages.append(t);
            messages.append("\n");
        });
        
       
        principal.txtAreaNotifications.setText(null);
        principal.txtAreaNotifications.setText(messages+"");
    }
    
     public static String formatMoney(String valor) {
        
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
    
    public static JDialog showDialog(final String title, final String content, Component componentLocation){
        
        JDialog dialog = new JDialog(new JFrame());
        dialog.setModalityType(Dialog.ModalityType.MODELESS);
        dialog.setBounds(0,0,100, 100);
        dialog.setSize(300,100);
        dialog.add(new JLabel(content));
        dialog.setTitle(title);
        dialog.setLocationRelativeTo(componentLocation);
        dialog.setVisible(true);
        dialog.setModal(true);
            
        return dialog;
    }
    
    // obtiene la fecha del sistema con un caracter delimitador
    public static String getSystemDate(String delimiterCharacter) {
        String systemDate = null;
        Calendar fecha = Calendar.getInstance();
        String mes = Integer.toString(fecha.get(Calendar.MONTH) + 1);
        String dia = Integer.toString(fecha.get(Calendar.DATE));
        String auxMes = null, auxDia = null;
        

        if (mes.length() == 1) {
            auxMes = "0" + mes;
            systemDate = fecha.get(Calendar.DATE) + delimiterCharacter + auxMes + delimiterCharacter + fecha.get(Calendar.YEAR);

            if (dia.length() == 1) {
                auxDia = "0" + dia;
                systemDate = auxDia + delimiterCharacter + auxMes + delimiterCharacter + fecha.get(Calendar.YEAR);

            }

        } else {
            systemDate = fecha.get(Calendar.DATE) + delimiterCharacter + (fecha.get(Calendar.MONTH) + 1) + delimiterCharacter + fecha.get(Calendar.YEAR);
        }
        
        return systemDate;
    }
    
    public static String deleteCharacters(String s_cadena, String s_caracteres) {
        String nueva_cadena = "";
        Character caracter = null;
        boolean valido = true;

        /* Va recorriendo la cadena s_cadena y copia a la cadena que va a regresar,
         sólo los caracteres que no estén en la cadena s_caracteres */
        for (int i = 0; i < s_cadena.length(); i++) {
            valido = true;
            for (int j = 0; j < s_caracteres.length(); j++) {
                caracter = s_caracteres.charAt(j);

                if (s_cadena.charAt(i) == caracter) {
                    valido = false;
                    break;
                }
            }
            if (valido) {
                nueva_cadena += s_cadena.charAt(i);
            }
        }

        return nueva_cadena;
    }
  
    
     public static boolean validateAmount(String value) { 
        boolean valid = false;
        Float amount = null;
        
        if(value == null || value.isEmpty() || value.equals(""))
            return false;
        
         try {
             amount = new Float(value);
         } catch (NumberFormatException e) {
             valid = false;
         }
         
         if(amount == null  || amount <= 0)
            valid = false;
        else
            valid = true;         
        
        return valid;
    }
     
    public static boolean validateComboBoxDataValue(String value){
                
        if(value == null 
                || value.isEmpty()
                || value.equals(ApplicationConstants.CMB_SELECCIONE))
            return false;
        else
            return true;
    
    }
    
   
    public static boolean showWindowDataUpdateSession(){
        
        JPasswordField pf = new JPasswordField(); 
        pf.addAncestorListener(new RequestFocusListener());
        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Introduce tu contrase\u00F1a", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); 
       
        
        if (okCxl == JOptionPane.OK_OPTION) {
            String password = new String(pf.getPassword()); 
            System.out.println("You entered: " + password); 
            if(!iniciar_sesion.dataSessionUptade(password)){
                JOptionPane.showMessageDialog(null, ApplicationConstants.DS_MESSAGE_FAIL_LOGIN, ApplicationConstants.TITLE_MESSAGE_FAIL_LOGIN, JOptionPane.ERROR_MESSAGE);
                return false;
            }else{
              return true;
            }
        }else{
            return false;
        }
    }
    
     public static void isEmail(String email) throws MessagingException{ //validar correo electronico
         
        Pattern pat;
        Matcher mat;
        pat = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
        
        if(email == null || email.equals("")){
            throw new MessagingException("Email vacio");
        }else{
            String[] array = email.split(";");
            for(String e : array){
                mat = pat.matcher(e);
                if(!mat.find()){
                    throw new MessagingException("Email no v\u00E1lido: "+e);
                }
            }
        }
        
    }
     
    public static String getPathLocation()throws IOException,URISyntaxException{
   
        File file = new File(Utility.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParentFile();
        
        return file+"";
    
    }
    
}
