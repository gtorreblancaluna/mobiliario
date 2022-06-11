package services;

import dao.EstadoEventoDao;
import java.util.List;
import common.model.EstadoEvento;

public class EstadoEventoService {
        
    private static EstadoEventoService INSTANCE = null;
    private final EstadoEventoDao estadoEventoDao = EstadoEventoDao.getInstance();

    // Private constructor suppresses 
    private EstadoEventoService(){}

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new EstadoEventoService();
        }
    }

    public static EstadoEventoService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<EstadoEvento> get () {
        return estadoEventoDao.get();
    }
}
