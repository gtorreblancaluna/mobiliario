package utilities;

import common.exceptions.BusinessException;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import utilities.dtos.ResultDataShowByDeliveryOrReturnDate;

public class OptionPaneService {
    
    // singlenton instance
    private static final OptionPaneService SINGLE_INSTANCE = null;
  
    
    private OptionPaneService () {}
    
    public static OptionPaneService getInstance() {
        if (SINGLE_INSTANCE == null) {
            return new OptionPaneService();
        }
        return SINGLE_INSTANCE;
    }
    
    public ResultDataShowByDeliveryOrReturnDate getOptionPaneShowByDeliveryOrReturnDateItemsAvailivity (JInternalFrame jInternalFrame) throws BusinessException {
        ResultDataShowByDeliveryOrReturnDate result = new ResultDataShowByDeliveryOrReturnDate();
        
        boolean showByDeliveryDate = false;
        boolean showByReturnDate = false;
        final String[] values = {"Mostrar por fecha de entrega y fecha de devolución", "Fecha de entrega","Fecha de devolución"};
        
        Object selected = JOptionPane.showInputDialog(jInternalFrame, "Indica el parametro a consultar", "Parametro a consultar", JOptionPane.QUESTION_MESSAGE, null, values, values[0]);
        if ( selected != null ){//null if the user cancels. 
            String selectedString = selected.toString();
            switch (selectedString) {
                case "Fecha de entrega":
                    showByDeliveryDate = true;
                    break;
                case "Fecha de devolución":
                    showByReturnDate = true;
                    break;
            }
        }else{
            throw new BusinessException("Falta indicar el parametro");
        }
        
        result.setShowByDeliveryDate(showByDeliveryDate);
        result.setShowByReturnDate(showByReturnDate);
        
        return result;
    }
    
}
