
package model.querys;

import common.model.Articulo;
import lombok.Data;

@Data
public class AvailabilityItemResult {
    
    private Articulo item;
    private Float numberOfItems;
    private String eventDateOrder;
    private String eventDateElaboration;
    private String deliveryDateOrder;
    private String returnHourOrder;
    private String deliveryHourOrder;
    private String returnDateOrder;
    private String customerName;
    private String folioOrder;
    private String descriptionOrder;
    private String typeOrder;
    private String statusOrder;
    
}
