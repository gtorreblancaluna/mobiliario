package services.material.inventory;

import dao.material.inventory.MaterialInventoryDAO;
import exceptions.BusinessException;
import exceptions.DataOriginException;
import java.util.List;
import java.util.Map;
import model.material.inventory.MaterialArea;
import model.material.inventory.MaterialInventory;
import model.material.inventory.MaterialSaleItem;
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
    
    public void save (MaterialSaleItem materialSaleItem) throws BusinessException {
        try {
            materialInventoryDAO.save(materialSaleItem);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public MaterialInventory getById (Long id) throws BusinessException {
        try {
            return materialInventoryDAO.getById(id);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public List<MaterialSaleItem> getMaterialSaleItemsByItemId (Long id) throws BusinessException{
        try {
            return materialInventoryDAO.getMaterialSaleItemsByItemId(id);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public List<MaterialSaleItem> getMaterialSaleItemsByItemsId (String itemsId) throws BusinessException{
        try {
            return materialInventoryDAO.getMaterialSaleItemsByItemsId(itemsId);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
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
    
    public void save (MeasurementUnit measurementUnit) throws BusinessException {
        try {
            measurementUnit.setDescription(measurementUnit.getDescription().trim().toUpperCase());
            materialInventoryDAO.save(measurementUnit);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public void delete (MeasurementUnit measurementUnit) throws BusinessException {
        try {
            materialInventoryDAO.delete(measurementUnit);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public void delete (MaterialInventory materialInventory) throws BusinessException {
        try {
            materialInventoryDAO.delete(materialInventory);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public void delete (MaterialSaleItem materialSaleItem) throws BusinessException {
        try {
            materialInventoryDAO.delete(materialSaleItem);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public void save (MaterialArea materialArea) throws BusinessException {
        try {
            materialArea.setDescription(materialArea.getDescription().trim().toUpperCase());
            materialInventoryDAO.save(materialArea);
        } catch (DataOriginException e) {
            throw new BusinessException(e.getMessage(),e);
        }
    }
    
    public void delete (MaterialArea materialArea) throws BusinessException {
        try {
            materialInventoryDAO.delete(materialArea);
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