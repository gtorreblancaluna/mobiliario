
package parametersVO;

import java.util.List;

public class DataEmailTemplate {


    public DataEmailTemplate(String urlLogoSiteCompany, String urlIconCkeck, String siteCompany, String orderNumber, String returnDate, String eventDate, String driversName, String adressEvent, String registerType, String nameUser, String subTotal, String sendAndCollection, String guaranteeDeposit, String iva, String payments, String total, String deliveryDate, String discount,List<ModelTableItem> items) {
        
        this.urlLogoSiteCompany = urlLogoSiteCompany;
        this.urlIconCkeck = urlIconCkeck;
        this.siteCompany = siteCompany;
        this.orderNumber = orderNumber;
        this.returnDate = returnDate;
        this.eventDate = eventDate;
        this.driversName = driversName;
        this.adressEvent = adressEvent;
        this.registerType = registerType;
        this.nameUser = nameUser;
        this.subTotal = subTotal;
        this.sendAndCollection = sendAndCollection;
        this.guaranteeDeposit = guaranteeDeposit;
        this.iva = iva;
        this.payments = payments;
        this.total = total;
        this.deliveryDate = deliveryDate;
        this.discount = discount;
        this.items = items;
    }
       
    private String urlLogoSiteCompany;
    private String urlIconCkeck;
    private String siteCompany;
    private String orderNumber;
    private String returnDate;
    private String eventDate;
    private String driversName;
    private String adressEvent;
    private String registerType;
    private String nameUser;
    private String subTotal;
    private String sendAndCollection;
    private String guaranteeDeposit;
    private String iva;
    private String payments;
    private String total;
    private List<ModelTableItem> items;
    private String deliveryDate;
    private String discount;

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
    
    

    public String getDiscount() {
        return "$"+discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
    
    

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    

    public String getUrlLogoSiteCompany() {
        return urlLogoSiteCompany;
    }

    public void setUrlLogoSiteCompany(String urlLogoSiteCompany) {
        this.urlLogoSiteCompany = urlLogoSiteCompany;
    }

    public String getUrlIconCkeck() {
        return urlIconCkeck;
    }

    public void setUrlIconCkeck(String urlIconCkeck) {
        this.urlIconCkeck = urlIconCkeck;
    }

    public String getSiteCompany() {
        return siteCompany;
    }

    public void setSiteCompany(String siteCompany) {
        this.siteCompany = siteCompany;
    }
    
    

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

   
   

    public String getDriversName() {
        return driversName;
    }

    public void setDriversName(String driversName) {
        this.driversName = driversName;
    }

    public String getAdressEvent() {
        return adressEvent;
    }

    public void setAdressEvent(String adressEvent) {
        this.adressEvent = adressEvent;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getSubTotal() {
        return "$"+subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getSendAndCollection() {
        return "$"+sendAndCollection;
    }

    public void setSendAndCollection(String sendAndCollection) {
        this.sendAndCollection = sendAndCollection;
    }

    public String getGuaranteeDeposit() {
        return "$"+guaranteeDeposit;
    }

    public void setGuaranteeDeposit(String guaranteeDeposit) {
        this.guaranteeDeposit = guaranteeDeposit;
    }

    public String getIva() {
        return "$"+iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getPayments() {
        return "$"+payments;
    }

    public void setPayments(String payments) {
        this.payments = payments;
    }

    public String getTotal() {
        return "$"+total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<ModelTableItem> getItems() {
        return items;
    }

    public void setItems(List<ModelTableItem> items) {
        this.items = items;
    }
    
    
    
}
