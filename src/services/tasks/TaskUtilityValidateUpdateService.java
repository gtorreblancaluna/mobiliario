package services.tasks;

import common.constants.ApplicationConstants;
import common.exceptions.NoDataFoundException;
import common.model.EstadoEvento;
import common.model.Renta;
import common.model.StatusAlmacenTaskCatalogVO;
import common.model.Tipo;
import model.tasks.TaskCatalogVO;

public class TaskUtilityValidateUpdateService {
    
    private TaskUtilityValidateUpdateService () {}
    private static final TaskUtilityValidateUpdateService SINGLE_INSTANCE = null;
        
    public static TaskUtilityValidateUpdateService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new TaskUtilityValidateUpdateService();
        }
        return SINGLE_INSTANCE;
    }
    
    public TaskCatalogVO validateAndBuild (EstadoEvento eventStatusChange, Tipo eventTypeChange, Renta currentRenta, Boolean updateItems) throws NoDataFoundException {
        
        TaskCatalogVO taskCatalogVO = new TaskCatalogVO();
        taskCatalogVO.setRentaId(currentRenta.getRentaId()+"");
        
        if (updateItems && eventTypeChange.getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO)
                && (eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_APARTADO)))
                {
                    // hubo cambios en los articulos
                    taskCatalogVO.setStatusAlmacenTaskCatalog(StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_ITEMS_FOLIO);
                    taskCatalogVO.setSystemMessage(StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_ITEMS_FOLIO.getDescription());
                    
        } else if (currentRenta.getTipo().getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO)
                && !eventStatusChange.getEstadoId().toString().equals(currentRenta.getEstado().getEstadoId().toString())
                && (
                        eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_APARTADO) ||
                        eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_CANCELADO)
                )
                ) {
                    // cambio solo el estado del evento a apartado o cancelado
                    taskCatalogVO.setStatusAlmacenTaskCatalog(StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_STATUS_FOLIO);
                    taskCatalogVO.setSystemMessage(
                            StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_STATUS_FOLIO.getDescription() + " [" +
                                   currentRenta.getEstado().getDescripcion() + " a " + eventStatusChange.getDescripcion() + "]"
                    );
                                        
        } else if (!eventTypeChange.getTipoId().toString().equals(currentRenta.getTipo().getTipoId().toString())
                    && eventStatusChange.getEstadoId().toString().equals(currentRenta.getEstado().getEstadoId().toString())
                ){
                        // cambio solo el tipo de evento
                    
                    taskCatalogVO.setStatusAlmacenTaskCatalog(StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_TYPE_FOLIO);
                    taskCatalogVO.setSystemMessage(
                            StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_TYPE_FOLIO.getDescription() + " [" +
                                   currentRenta.getTipo().getTipo() + " a " + eventTypeChange.getTipo()+ "]"
                    );
                    
        } else if (!eventTypeChange.getTipoId().toString().equals(currentRenta.getTipo().getTipoId().toString())
                    && !eventStatusChange.getEstadoId().toString().equals(currentRenta.getEstado().getEstadoId().toString())
                    && (
                            eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_APARTADO) ||
                            eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_CANCELADO)
                    )
                ){
                    // cambio estado y el tipo de evento
                    
                    taskCatalogVO.setStatusAlmacenTaskCatalog(StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_TYPE_AND_STATUS_FOLIO);
                    taskCatalogVO.setSystemMessage(
                            StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_TYPE_AND_STATUS_FOLIO.getDescription() 
                                        + " Tipo: [" +
                                            currentRenta.getTipo().getTipo() + " a " + eventTypeChange.getTipo()
                                            + "], Estado: [" +
                                            currentRenta.getEstado().getDescripcion() + " a " + eventStatusChange.getDescripcion()+"]"
                    );
                    
        } else {
           throw new NoDataFoundException("No se generaron tareas, ya que no coincidio con las reglas operativas actuales");
        }
        
        return taskCatalogVO;
        
    }
}
