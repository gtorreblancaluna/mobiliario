package services.tasks.almacen;

import common.constants.ApplicationConstants;
import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import common.model.AttendAlmacenTaskTypeCatalogVO;
import common.model.EstadoEvento;
import common.model.Renta;
import common.model.StatusAlmacenTaskCatalogVO;
import common.model.StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog;
import common.model.TaskAlmacenVO;
import common.model.Tipo;
import common.model.Usuario;
import common.services.UserService;
import dao.task.almacen.TaskAlmacenUpdateDAO;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class TaskAlmacenUpdateService {
    
    private TaskAlmacenUpdateService () {}
    
    private static final TaskAlmacenUpdateService SINGLE_INSTANCE = null;
    private final UserService userService = UserService.getInstance();
    private final TaskAlmacenUpdateDAO taskAlmacenUpdateDAO = TaskAlmacenUpdateDAO.getInstance();
    private static final Logger LOGGER = Logger.getLogger(TaskAlmacenUpdateService.class.getName());
    
    public static TaskAlmacenUpdateService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new TaskAlmacenUpdateService();
        }
        return SINGLE_INSTANCE;
    }
    
    public String saveWhenEventIsUpdated (EstadoEvento eventStatusChange, Tipo eventTypeChange, Renta currentRenta, Boolean updateItems)  throws NoDataFoundException, DataOriginException {
        
        String message;
        if (updateItems && eventTypeChange.getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO)
                && (eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_APARTADO)))
                {
             // hubo cambios en los articulos
                    message = save(Long.parseLong(currentRenta.getRentaId()+""), 
                                StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_ITEMS_FOLIO,
                                StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_ITEMS_FOLIO.getDescription());
        } else if (currentRenta.getTipo().getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO)
                && !eventStatusChange.getEstadoId().toString().equals(currentRenta.getEstado().getEstadoId().toString())
                && (
                        eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_APARTADO) ||
                        eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_CANCELADO)
                )
                ) {
                    // cambio solo el estado del evento a apartado o cancelado
                    message = save(Long.parseLong(currentRenta.getRentaId()+""), 
                                StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_STATUS_FOLIO,
                                StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_STATUS_FOLIO.getDescription() + " [" +
                                   currentRenta.getEstado().getDescripcion() + " a " + eventStatusChange.getDescripcion() + "]"
                    );
                    
        } else if (!eventTypeChange.getTipoId().toString().equals(currentRenta.getTipo().getTipoId().toString())
                    && eventStatusChange.getEstadoId().toString().equals(currentRenta.getEstado().getEstadoId().toString())
                ){
                        // cambio solo el tipo de evento
                    message = save(Long.parseLong(currentRenta.getRentaId()+""), 
                                StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_TYPE_FOLIO,
                                StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_TYPE_FOLIO.getDescription() + " [" +
                                   currentRenta.getTipo().getTipo() + " a " + eventTypeChange.getTipo()+ "]");
        } else if (!eventTypeChange.getTipoId().toString().equals(currentRenta.getTipo().getTipoId().toString())
                    && !eventStatusChange.getEstadoId().toString().equals(currentRenta.getEstado().getEstadoId().toString())
                    && (
                            eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_APARTADO) ||
                            eventStatusChange.getEstadoId().toString().equals(ApplicationConstants.ESTADO_CANCELADO)
                    )
                ){
                    // cambio estado y el tipo de evento
                    message = save(Long.parseLong(currentRenta.getRentaId()+""), 
                                StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_TYPE_AND_STATUS_FOLIO,
                                StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.UPDATE_TYPE_AND_STATUS_FOLIO.getDescription() 
                                        + " Tipo: [" +
                                            currentRenta.getTipo().getTipo() + " a " + eventTypeChange.getTipo()
                                            + "], Estado: [" +
                                            currentRenta.getEstado().getDescripcion() + " a " + eventStatusChange.getDescripcion()+"]")
                            ;
        } else {
           throw new NoDataFoundException("No se generó tarea de almacén, ya que no coincidio con las reglas operativas actuales");
        }
        return message;
    }
    
    private String save (Long rentaId, StatusAlmacenTaskCatalog statusAlmacenTaskCatalog, String systemMessage) throws NoDataFoundException, DataOriginException{
        List<Usuario> usersInCategories =
                userService.getUsersInCategoriesAlmacenAndEvent(Integer.parseInt(rentaId.toString()));
        
        if (usersInCategories == null || usersInCategories.isEmpty()) {
            String message = "No se generó tarea de almacén, por que no se obtuvieron usuarios por categoria";
            LOGGER.info(message);
            throw new NoDataFoundException(message);
        }
        
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        stringBuilder
                .append("[")
                .append(statusAlmacenTaskCatalog.getDescription())
                .append("]")
                .append("\n");
        for (Usuario user : usersInCategories) {
            
            TaskAlmacenVO taskAlmacenVO = new TaskAlmacenVO();
            // renta
            Renta renta = new Renta();
            renta.setRentaId(Integer.parseInt(rentaId.toString()));
            taskAlmacenVO.setRenta(renta);

            //status
            StatusAlmacenTaskCatalogVO statusAlmacenTaskCatalogVO = new StatusAlmacenTaskCatalogVO();
            statusAlmacenTaskCatalogVO.setId(statusAlmacenTaskCatalog.getId());
            taskAlmacenVO.setStatusAlmacenTaskCatalogVO(statusAlmacenTaskCatalogVO);

            taskAlmacenVO.setUser(user);
            
            // attend
            AttendAlmacenTaskTypeCatalogVO attendAlmacenTaskTypeCatalogVO = new AttendAlmacenTaskTypeCatalogVO();
            attendAlmacenTaskTypeCatalogVO.setId(
                    Long.parseLong(AttendAlmacenTaskTypeCatalogVO.AttendAlmacenTaskTypeCatalog.UN_ATTENDED.getId().toString())
            );
            taskAlmacenVO.setAttendAlmacenTaskTypeCatalogVO(attendAlmacenTaskTypeCatalogVO);
            taskAlmacenVO.setSystemMessage(systemMessage);
            

            taskAlmacenVO.setCreatedAt(new Date());
            taskAlmacenVO.setUpdatedAt(new Date());
            
            stringBuilder.append("[")
                    .append(++count)
                    .append("]. ")
                    .append("Tarea almacen generada, para el usuario: ")
                    .append(user.getNombre()).append(" ").append(user.getApellidos());
            stringBuilder.append("\n");
            
            taskAlmacenVO.setFgActive("1");
            taskAlmacenUpdateDAO.save(taskAlmacenVO);
            LOGGER.info(String.format("Se ha generado tarea almacen para el evento id: %s, user id: %s",rentaId,user.getUsuarioId()));
            
        }
        
        return stringBuilder.toString();
    } 
        
    public String saveWhenIsNewEvent (Long rentaId) throws NoDataFoundException, DataOriginException{
        return save(rentaId,StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.NEW_FOLIO,StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.NEW_FOLIO.getDescription());
        
    }
    
}
