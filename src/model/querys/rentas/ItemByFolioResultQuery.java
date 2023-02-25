package model.querys.rentas;

import lombok.Data;

@Data
public class ItemByFolioResultQuery {
    
    private Long eventId;
    private String eventFolio;
    private Float itemAmount;
    private Long itemId;
    private String itemDescription;
    private Float itemUnitPrice;
    private Float itemDiscountRate;
    private Float itemSubTotal;
    private String eventDeliveryDate;
    private String eventCreatedAtDate;
    private String eventType;
    private String eventStatus;
    
}
