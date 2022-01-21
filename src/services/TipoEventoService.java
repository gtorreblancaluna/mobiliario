package services;

import dao.TipoEventoDao;
import java.util.List;
import model.Tipo;

public class TipoEventoService {
        
    private static TipoEventoService INSTANCE = null;
    private final TipoEventoDao tipoEventoDao = TipoEventoDao.getInstance();

    // Private constructor suppresses 
    private TipoEventoService(){}

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TipoEventoService();
        }
    }

    public static TipoEventoService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<Tipo> get () {
        return tipoEventoDao.get();
    }
}
