package parametersVO;

import java.sql.Timestamp;

public class ParameterOrderProvider {
    
    private Integer orderId;
    private Integer limit;
    private Timestamp initDate;
    private Timestamp endDate;
    private Timestamp initEventDate;
    private Timestamp endEventDate;
    private String nameProvider;
    private String status;
    private Integer folioRenta;

    public Timestamp getInitEventDate() {
        return initEventDate;
    }

    public void setInitEventDate(Timestamp initEventDate) {
        this.initEventDate = initEventDate;
    }

    public Timestamp getEndEventDate() {
        return endEventDate;
    }

    public void setEndEventDate(Timestamp endEventDate) {
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
        this.initDate = initDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

   
    public String getNameProvider() {
        return nameProvider;
    }

    public void setNameProvider(String nameProvider) {
        this.nameProvider = nameProvider;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        String array[] = status.split("-");
        this.status = array[0].trim();
    }
    
    
    
}
