
package model.querys;

import model.Articulo;

public class AvailabilityItemResult {
    
    private Articulo item;
    private Float numberOfItems;
    private String eventDateOrder;
    private String deliveryDateOrder;
    private String returnHourOrder;
    private String deliveryHourOrder;
    private String returnDateOrder;
    private String customerName;
    private String folioOrder;
    private String descriptionOrder;
    private String typeOrder;
    private String statusOrder;

    public Float getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(Float numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
    
    

    public Articulo getItem() {
        return item;
    }

    public void setItem(Articulo item) {
        this.item = item;
    }

    public String getEventDateOrder() {
        return eventDateOrder;
    }

    public void setEventDateOrder(String eventDateOrder) {
        this.eventDateOrder = eventDateOrder;
    }

    public String getDeliveryDateOrder() {
        return deliveryDateOrder;
    }

    public void setDeliveryDateOrder(String deliveryDateOrder) {
        this.deliveryDateOrder = deliveryDateOrder;
    }

    public String getReturnHourOrder() {
        return returnHourOrder;
    }

    public void setReturnHourOrder(String returnHourOrder) {
        this.returnHourOrder = returnHourOrder;
    }

    public String getDeliveryHourOrder() {
        return deliveryHourOrder;
    }

    public void setDeliveryHourOrder(String deliveryHourOrder) {
        this.deliveryHourOrder = deliveryHourOrder;
    }

    public String getReturnDateOrder() {
        return returnDateOrder;
    }

    public void setReturnDateOrder(String returnDateOrder) {
        this.returnDateOrder = returnDateOrder;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFolioOrder() {
        return folioOrder;
    }

    public void setFolioOrder(String folioOrder) {
        this.folioOrder = folioOrder;
    }

    public String getDescriptionOrder() {
        return descriptionOrder;
    }

    public void setDescriptionOrder(String descriptionOrder) {
        this.descriptionOrder = descriptionOrder;
    }

    public String getTypeOrder() {
        return typeOrder;
    }

    public void setTypeOrder(String typeOrder) {
        this.typeOrder = typeOrder;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        this.statusOrder = statusOrder;
    }
    
    
}
