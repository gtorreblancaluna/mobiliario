package services.tasks.almacen;

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
import model.tasks.TaskCatalogVO;
import org.apache.log4j.Logger;
import services.tasks.TaskUtilityValidateUpdateService;

public class TaskAlmacenUpdateService {
    
    private TaskAlmacenUpdateService () {}
    
    private static final TaskAlmacenUpdateService SINGLE_INSTANCE = null;
    private final UserService userService = UserService.getInstance();
    private final TaskAlmacenUpdateDAO taskAlmacenUpdateDAO = TaskAlmacenUpdateDAO.getInstance();
    private static final Logger LOGGER = Logger.getLogger(TaskAlmacenUpdateService.class.getName());
    private final TaskUtilityValidateUpdateService taskUtilityValidateUpdateService = TaskUtilityValidateUpdateService.getInstance();
    
    public static TaskAlmacenUpdateService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new TaskAlmacenUpdateService();
        }
        return SINGLE_INSTANCE;
    }
    
    public String saveWhenEventIsUpdated (EstadoEvento eventStatusChange, Tipo eventTypeChange, Renta currentRenta, Boolean updateItems, Boolean generalDataUpdated)  throws NoDataFoundException, DataOriginException {
        
        TaskCatalogVO taskCatalogVO = taskUtilityValidateUpdateService.validateAndBuild(
                eventStatusChange,
                eventTypeChange,
                currentRenta,
                updateItems,
                generalDataUpdated
        );
        taskCatalogVO.setEventFolio(String.valueOf(currentRenta.getFolio()));
        return save (taskCatalogVO); 
    }
    
    private String save (TaskCatalogVO taskCatalogVO) throws NoDataFoundException, DataOriginException {
        List<Usuario> usersInCategories =
                userService.getUsersInCategoriesAlmacenAndEvent(Integer.parseInt(taskCatalogVO.getRentaId()));
        
        if (usersInCategories == null || usersInCategories.isEmpty()) {
            String message = "No se generó tarea de almacén, por que no se obtuvieron usuarios por categoria";
            LOGGER.info(message);
            throw new NoDataFoundException(message);
        }
        
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        stringBuilder
                .append("[")
                .append(taskCatalogVO.getStatusAlmacenTaskCatalog().getDescription())
                .append(". # ")
                .append(taskCatalogVO.getEventFolio())
                .append("]")
                .append("\n");
        for (Usuario user : usersInCategories) {
            
            TaskAlmacenVO taskAlmacenVO = new TaskAlmacenVO();
            // renta
            Renta renta = new Renta();
            renta.setRentaId(Integer.parseInt(taskCatalogVO.getRentaId()));
            taskAlmacenVO.setRenta(renta);

            //status
            StatusAlmacenTaskCatalogVO statusAlmacenTaskCatalogVO = new StatusAlmacenTaskCatalogVO();
            statusAlmacenTaskCatalogVO.setId(taskCatalogVO.getStatusAlmacenTaskCatalog().getId());
            taskAlmacenVO.setStatusAlmacenTaskCatalogVO(statusAlmacenTaskCatalogVO);

            taskAlmacenVO.setUser(user);
            
            // attend
            AttendAlmacenTaskTypeCatalogVO attendAlmacenTaskTypeCatalogVO = new AttendAlmacenTaskTypeCatalogVO();
            attendAlmacenTaskTypeCatalogVO.setId(
                    Long.parseLong(AttendAlmacenTaskTypeCatalogVO.AttendAlmacenTaskTypeCatalog.UN_ATTENDED.getId().toString())
            );
            taskAlmacenVO.setAttendAlmacenTaskTypeCatalogVO(attendAlmacenTaskTypeCatalogVO);
            

            taskAlmacenVO.setCreatedAt(new Date());
            taskAlmacenVO.setUpdatedAt(new Date());
            
            stringBuilder.append("[")
                    .append(++count)
                    .append("]. ")
                    .append("Tarea almacen generada. para el usuario: ")
                    .append(user.getNombre()).append(" ").append(user.getApellidos())
                    ;
            stringBuilder.append("\n");
            
            taskAlmacenVO.setFgActive("1");
            taskAlmacenUpdateDAO.save(taskAlmacenVO);
            LOGGER.info(String.format("Se ha generado tarea almacen para el evento id: %s, user id: %s",taskCatalogVO.getRentaId(),user.getUsuarioId()));
            
        }
        
        return stringBuilder.toString();
    }
    
    public String saveWhenIsNewEvent (Long rentaId, String folio) throws NoDataFoundException, DataOriginException{
        TaskCatalogVO taskCatalogVO = new TaskCatalogVO();
        taskCatalogVO.setRentaId(rentaId+"");
        taskCatalogVO.setStatusAlmacenTaskCatalog(StatusAlmacenTaskCatalog.NEW_FOLIO);
        taskCatalogVO.setEventFolio(folio);
        return save(taskCatalogVO);
    }
    
}
