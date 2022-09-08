package model.tasks;

import common.model.StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog;

public class TaskCatalogVO {
    
    private String rentaId;
    private String eventFolio;
    private StatusAlmacenTaskCatalog statusAlmacenTaskCatalog;
    private String choferId;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    

    public String getEventFolio() {
        return eventFolio;
    }

    public void setEventFolio(String eventFolio) {
        this.eventFolio = eventFolio;
    }
    
    public String getChoferId() {
        return choferId;
    }

    public void setChoferId(String choferId) {
        this.choferId = choferId;
    }
    
    public String getRentaId() {
        return rentaId;
    }

    public void setRentaId(String rentaId) {
        this.rentaId = rentaId;
    }

    public StatusAlmacenTaskCatalog getStatusAlmacenTaskCatalog() {
        return statusAlmacenTaskCatalog;
    }

    public void setStatusAlmacenTaskCatalog(StatusAlmacenTaskCatalog statusAlmacenTaskCatalog) {
        this.statusAlmacenTaskCatalog = statusAlmacenTaskCatalog;
    }
    
}
