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
    
    public TaskCatalogVO validateAndBuild (EstadoEvento eventStatusChange, Tipo eventTypeChange, Renta currentRenta, Boolean updateItems, Boolean generalDataUpdated) throws NoDataFoundException {
        
        TaskCatalogVO taskCatalogVO = new TaskCatalogVO();
        taskCatalogVO.setRentaId(currentRenta.getRentaId()+"");
        
        if (eventTypeChange.getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO) 
                && eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_PENDIENTE)) {
            throw new NoDataFoundException("No se generaron tareas.. Tipo PEDIDO debe tener un Estado diferente a PENDIENTE");
        }
        
        if (eventTypeChange.getTipoId().toString().equals(ApplicationConstants.TIPO_COTIZACION) 
                && !eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_PENDIENTE)) {
            throw new NoDataFoundException("No se generaron tareas.. Tipo COTIZACIÓN debe tener un Estado igual a PENDIENTE");
        }
        
        if (generalDataUpdated && eventTypeChange.getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO)
                && (eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_APARTADO)))
                {
                    // hubo cambios en los datos generales
                    taskCatalogVO.setStatusAlmacenTaskCatalog(StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.GENERAL_DATA_UPDATED);
                    taskCatalogVO.setSystemMessage(StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.GENERAL_DATA_UPDATED.getDescription());
                    
        } else if (updateItems && eventTypeChange.getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO)
                && (eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_APARTADO)))
                {
                    // hubo cambios en los articulos
                    taskCatalogVO.setStatusAlmacenTaskCatalog(StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_ITEMS_FOLIO);
                    taskCatalogVO.setSystemMessage(StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_ITEMS_FOLIO.getDescription());
                    
        } else if (currentRenta.getTipo().getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO)
                && !eventStatusChange.getEstadoId().toString().equals(currentRenta.getEstado().getEstadoId().toString())
                && (
                        !eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_PENDIENTE)
                )
                ) {
                    // cambio solo el estado del evento diferente a PENDIENTE
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
           throw new NoDataFoundException(""
                   + "No se generaron tareas, ya que no coincidio con las reglas operativas actuales\n"
//                   + "1. [Modf. datos generales]-[Tipo=Pedido]-[Estado=apartado]\n"
//                   + "2. [Modf. articulos]-[Tipo=Pedido]-[Estado=apartado]\n"
//                   + "3. [Tipo=Pedido]-[Estado diff PENDIENTE]\n"
//                   + "4. [Tipo=CAMBIO]-[Estado=NO CAMBIO]\n"
//                   + "5. [Tipo=CAMBIO]-[Estado=CAMBIO]"
                   + "");
        }
        
        return taskCatalogVO;
        
    }
}
