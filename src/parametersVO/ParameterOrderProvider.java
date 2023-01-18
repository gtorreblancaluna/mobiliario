package parametersVO;

import java.sql.Timestamp;
import java.util.Calendar;



public class ParameterOrderProvider {
    
    private Integer orderId;
    private Integer limit;
    private Timestamp initDate;
    private Timestamp endDate;
    private String initEventDate;
    private String endEventDate;
    private String nameProvider;
    private String status;
    private Integer folioRenta;

    public String getInitEventDate() {
        return initEventDate;
    }

    public void setInitEventDate(String initEventDate) {
        this.initEventDate = initEventDate;
    }

    public String getEndEventDate() {
        return endEventDate;
    }

    public void setEndEventDate(String endEventDate) {
        this.endEventDate = endEventDate;
    }

   

    
    

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    
    

    public Integer getFolioRenta() {
        return folioRenta;
    }

    public void setFolioRenta(Integer folioRenta) {
        this.folioRenta = folioRenta;
    }
    
    

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

   

    public Timestamp getInitDate() {
        return initDate;
    }

    public void setInitDate(Timestamp initDate) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(initDate.getTime());
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 1);
        initDate.setTime(now.getTimeInMillis());
        this.initDate = initDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(endDate.getTime());
        now.set(Calendar.HOUR_OF_DAY, 23);
        now.set(Calendar.MINUTE, 23);
        now.set(Calendar.SECOND, 59);
        now.set(Calendar.MILLISECOND, 59);
        endDate.setTime(now.getTimeInMillis());
        this.endDate = endDate;
    }

   
    public String getNameProvider() {
        return nameProvider;
    }

    public void setNameProvider(String nameProvider) {
        this.nameProvider = nameProvider.toUpperCase().trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        String array[] = status.split("-");
        this.status = array[0].trim();
    }
    
    
    
}
