package utilities;

import common.utilities.RequestFocusListener;
import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.model.EstadoEvento;
import common.model.Tipo;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import mobiliario.iniciar_sesion;
import mobiliario.IndexForm;


public abstract class Utility {
    
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    
    public static boolean validateHour(String hora) {
        boolean b;
        char[] a = hora.toCharArray();
        String[] c = hora.split(":");
        try {
            if ((a[0] == ' ') || (a[1] == ' ') || (a[2] == ' ') || (a[3] == ' ') || (a[4] == ' ') || (Integer.parseInt(c[0]) > 24) || (Integer.parseInt(c[1]) > 59)) {
                b=false;
            }else{
                b=true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            b=false;
        }
        return b;
    }
    
    public static void addJtableToPane (int sizeVertical, int sizeHorizontal, JPanel jPanel,JTable tableToAdd ) {
        
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(tableToAdd);
        
        javax.swing.GroupLayout tabPanelGeneralLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(tabPanelGeneralLayout);
        tabPanelGeneralLayout.setHorizontalGroup(
            tabPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPanelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, sizeVertical, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabPanelGeneralLayout.setVerticalGroup(
            tabPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPanelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, sizeHorizontal, Short.MAX_VALUE))
        );
    }
    
    public static void validateStatusAndTypeEvent (EstadoEvento statusEvent, Tipo typeEvent) throws BusinessException {
        if (typeEvent.getTipoId().toString().equals(ApplicationConstants.TIPO_COTIZACION)
                &&
                !statusEvent.getEstadoId().toString().equals(ApplicationConstants.ESTADO_PENDIENTE))
        {
            throw new BusinessException("Evento tipo 'COTIZACIÓN' debe tener estado 'PENDIENTE' ");
        } else if (typeEvent.getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO)
                &&
                statusEvent.getEstadoId().toString().equals(ApplicationConstants.ESTADO_PENDIENTE)) {
            throw new BusinessException("Evento tipo 'PEDIDO' debe tener estado diferente a 'PENDIENTE' ");
        }
    }
    public static String getPathLocation()throws IOException,URISyntaxException{
   
        File file = new File(Utility.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParentFile();
        
        return file+"";
    
    }
    
    public static void pushNotification(final String notification){
        StringBuilder messages = new StringBuilder();
        
        String date = simpleDateFormat.format(new Timestamp(System.currentTimeMillis()));
        IndexForm.listNotifications.add(date+" >> "+notification);
        IndexForm.listNotifications.stream().forEach(t -> {
            messages.append(t);
            messages.append("\n");
        });
        
       
        IndexForm.txtAreaNotifications.setText(null);
        IndexForm.txtAreaNotifications.setText(messages+"");
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

}
