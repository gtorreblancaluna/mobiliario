package services.tasks.deliveryChofer;

import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import common.model.AttendAlmacenTaskTypeCatalogVO;
import common.model.EstadoEvento;
import common.model.Renta;
import common.model.StatusAlmacenTaskCatalogVO;
import common.model.TaskChoferDeliveryVO;
import common.model.Tipo;
import common.model.Usuario;
import dao.task.deliveryChofer.TaskDeliveryChoferUpdateDAO;
import java.util.Date;
import model.tasks.TaskCatalogVO;
import org.apache.log4j.Logger;
import services.tasks.TaskUtilityValidateUpdateService;

public class TaskDeliveryChoferUpdateService {
    
    private TaskDeliveryChoferUpdateService () {}
    private static final TaskDeliveryChoferUpdateService SINGLE_INSTANCE = null;
    private final TaskUtilityValidateUpdateService taskUtilityValidateUpdateService = TaskUtilityValidateUpdateService.getInstance();
    private static final Logger LOGGER = Logger.getLogger(TaskDeliveryChoferUpdateService.class.getName());
    private final TaskDeliveryChoferUpdateDAO taskDeliveryChoferUpdateDAO = TaskDeliveryChoferUpdateDAO.getInstance();
        
    public static TaskDeliveryChoferUpdateService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new TaskDeliveryChoferUpdateService();
        }
        return SINGLE_INSTANCE;
    }
    
    public String saveWhenEventIsUpdated (final EstadoEvento eventStatusChange,
            final Tipo eventTypeChange,
            final Renta currentRenta,
            final Boolean updateItems,
            final String choferId,
            final Boolean generalDataUpdated,
            final String userId)  throws NoDataFoundException, DataOriginException {
        
        TaskCatalogVO taskCatalogVO = taskUtilityValidateUpdateService.validateAndBuild(
                eventStatusChange,
                eventTypeChange,
                currentRenta,
                updateItems,
                generalDataUpdated
        );
        taskCatalogVO.setChoferId(choferId);
        taskCatalogVO.setEventFolio(currentRenta.getFolio()+"");
        taskCatalogVO.setUserId(userId);
        
        return save (taskCatalogVO); 
    }
    
    private String save (TaskCatalogVO taskCatalogVO) throws NoDataFoundException, DataOriginException {
        
        TaskChoferDeliveryVO taskChoferDeliveryVO = new TaskChoferDeliveryVO();
        
        //renta
        Renta renta = new Renta();
        renta.setRentaId(Integer.parseInt(taskCatalogVO.getRentaId()));
        taskChoferDeliveryVO.setRenta(renta);
        
        //status
        StatusAlmacenTaskCatalogVO statusAlmacenTaskCatalogVO = new StatusAlmacenTaskCatalogVO();
        statusAlmacenTaskCatalogVO.setId(taskCatalogVO.getStatusAlmacenTaskCatalog().getId());
        taskChoferDeliveryVO.setStatusAlmacenTaskCatalogVO(statusAlmacenTaskCatalogVO);
        
        //user chofer
        Usuario chofer = new Usuario();
        chofer.setUsuarioId(Integer.parseInt(taskCatalogVO.getChoferId()));
        taskChoferDeliveryVO.setChofer(chofer);
        
        //user
        Usuario user = new Usuario();
        user.setUsuarioId(Integer.parseInt(taskCatalogVO.getUserId()));
        taskChoferDeliveryVO.setUser(user);
        
        // attend
        AttendAlmacenTaskTypeCatalogVO attendAlmacenTaskTypeCatalogVO = new AttendAlmacenTaskTypeCatalogVO();
        attendAlmacenTaskTypeCatalogVO.setId(
                Long.parseLong(AttendAlmacenTaskTypeCatalogVO.AttendAlmacenTaskTypeCatalog.UN_ATTENDED.getId().toString())
        );
        taskChoferDeliveryVO.setAttendAlmacenTaskTypeCatalogVO(attendAlmacenTaskTypeCatalogVO);
        
        taskChoferDeliveryVO.setCreatedAt(new Date());
        taskChoferDeliveryVO.setUpdatedAt(new Date());
        
        taskChoferDeliveryVO.setFgActive("1");
        
        String message = String.format("Tarea 'entrega chofer' generada. Folio: %s, chofer: %s",taskCatalogVO.getEventFolio(),chofer.getUsuarioId());
        LOGGER.info(message);
        taskDeliveryChoferUpdateDAO.save(taskChoferDeliveryVO);
        
        return message;
    }
    
     public String saveWhenIsNewEvent (final Long rentaId,
             final String eventFolio,
             final String choferId,
             final String userId) throws NoDataFoundException, DataOriginException{
        TaskCatalogVO taskCatalogVO = new TaskCatalogVO();
        taskCatalogVO.setRentaId(rentaId+"");
        taskCatalogVO.setStatusAlmacenTaskCatalog(StatusAlmacenTaskCatalogVO.StatusAlmacenTaskCatalog.NEW_FOLIO);
        taskCatalogVO.setEventFolio(eventFolio);
        taskCatalogVO.setChoferId(choferId);
        taskCatalogVO.setUserId(userId);
        return save(taskCatalogVO);
    }
    
}
