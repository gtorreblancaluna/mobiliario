package model.querys.rentas;

import lombok.Data;

@Data
public class SearchItemByFolioParams {
    
    private String initCreatedAtEvent;
    private String endCreatedAtEvent;
    private String initialEventDate;
    private String endEventDate;
    private Integer eventStatusId;
    private Integer eventTypeId;
    private Integer limit;
    private Long folio;
    private String likeItemDescription;
    
}
