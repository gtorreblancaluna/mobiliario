package services.material.inventory;

import dao.material.inventory.MaterialInventoryDAO;
import exceptions.BusinessException;
import exceptions.DataOriginException;
import java.util.List;
import java.util.Map;
import model.material.inventory.MaterialArea;
import model.material.inventory.MaterialInventory;
import model.material.inventory.MeasurementUnit;

public class MaterialInventoryService {
    
    // singlenton instance
    private static final MaterialInventoryService SINGLE_INSTANCE = null;
    
    private MaterialInventoryService () {}
    
    public static MaterialInventoryService getInstance() {
        if (SINGLE_INSTANCE == null) {
            return new MaterialInventoryService();
        }
        return SINGLE_INSTANCE;
    }
    
    private final MaterialInventoryDAO materialInventoryDAO = MaterialInventoryDAO.getInstance();
    
    public List<MaterialInventory> get (Map<String,Object> filter) throws BusinessException{
        try {
            return materialInventoryDAO.get(filter);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public void save (MaterialInventory materialInventory) throws BusinessException {
        try {
            materialInventoryDAO.save(materialInventory);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public List<MeasurementUnit> getMeasurementUnits () throws BusinessException {
        try {
            return materialInventoryDAO.getMeasurementUnits();
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public List<MaterialArea> getMaterialAreas () throws BusinessException {
        try {
            return materialInventoryDAO.getMaterialAreaList();
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
}