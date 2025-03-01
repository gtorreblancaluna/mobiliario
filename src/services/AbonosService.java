package services;

import common.constants.ApplicationConstants;
import dao.AbonosDAO;
import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import java.util.List;
import java.util.Map;
import common.model.Abono;
import common.utilities.UtilityCommon;
import java.time.LocalDate;
import java.util.ArrayList;
import model.abonos.CustomizePayment;


public class AbonosService {
    
    private AbonosService(){}
    private static final AbonosService SINGLE_INSTANCE = null;
    
    private AbonosDAO abonosDAO = AbonosDAO.getInstance();
    
    public static AbonosService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new AbonosService();
        }
        return SINGLE_INSTANCE;
    }
    
    /**
     * Obtener los abonos y calcular si es pago anticipado o pago tipo cobro; la regla es:
     * pago >= fecha_evento = "COBRO"
     * pago < fecha_evento = "ANTICIPO
     * @param parameters
     * @return
     * @throws DataOriginException 
     */
    public List<CustomizePayment> getByParametersAndCalculcatePrepaidAndCurrentPay(Map<String,Object> parameters) throws NoDataFoundException, DataOriginException {
        List<Abono> payments = abonosDAO.getByParameters(parameters);
        if (payments.isEmpty()) {
            throw new NoDataFoundException(ApplicationConstants.NO_DATA_FOUND_EXCEPTION);
        }
        final String PATTERN_DATE = "dd/MM/yyyy";
        List<CustomizePayment> customizePaymentses = new ArrayList<>();
        final String PREPAID_DESCRIPTION = "Anticipo";
        final String CURRENT_PAY_DESCRIPTION = "Cobro";
        
        payments.stream()
                .filter(payment -> 
                        payment.getRenta() != null 
                        && payment.getRenta().getFechaEvento() != null
                        && !payment.getRenta().getFechaEvento().isBlank()
                        && payment.getFechaPago() != null
                        && !payment.getFechaPago().isBlank()
                )
                .forEach(payment -> {
                    
                    LocalDate eventDate = 
                            UtilityCommon.getLocalDateFromString(payment.getRenta().getFechaEvento(),PATTERN_DATE);
                    LocalDate paymentDate = 
                            UtilityCommon.getLocalDateFromString(payment.getFechaPago(),PATTERN_DATE);
                    
                    CustomizePayment customizePayment = new CustomizePayment(payment);
                    if (paymentDate.isBefore(eventDate)) {
                        customizePayment.setTypePaymentDescription(PREPAID_DESCRIPTION);
                    } else {
                        customizePayment.setTypePaymentDescription(CURRENT_PAY_DESCRIPTION);
                    }
                    customizePaymentses.add(customizePayment);
                });
        
        return customizePaymentses;
    }
    
    public List<Abono> getByParameters(Map<String,Object> parameters) throws DataOriginException {
       
        return abonosDAO.getByParameters(parameters);
    }
    
    public List<Abono> getAbonosByDates(String initDate,String endDate) throws DataOriginException {
       
        return abonosDAO.getAbonosByDates(initDate, endDate);
    }
    
    public List<Abono> getAbonosByDatesGroupByBankAccounts(String initDate,String endDate) throws DataOriginException {
       
        return abonosDAO.getAbonosByDatesGroupByBankAccounts(initDate, endDate);
    }
    
}
