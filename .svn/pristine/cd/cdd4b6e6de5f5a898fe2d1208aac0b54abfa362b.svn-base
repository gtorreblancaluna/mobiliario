/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import dao.ContabilidadDAO;
import java.sql.Timestamp;
import java.util.List;
import model.CategoriaContabilidad;
import model.Contabilidad;
import model.SubCategoriaContabilidad;

/**
 *
 * @author idscomercial
 */
public class ContabilidadServices {
    
    private ContabilidadDAO contabilidadDAO = new ContabilidadDAO();
    
    public void deleteContabilidadById(Integer id){
        contabilidadDAO.deleteContabilidadById(id);
    }
    
    public Contabilidad getContabilidadById(Integer id){
        return contabilidadDAO.getContabilidadById(id);
    }
    
    public List<Contabilidad> getAllContabilidad(){
        List<Contabilidad> listContabilidad;
            listContabilidad = contabilidadDAO.getAllContabilidad();
        return listContabilidad;
    }
    
    public List<Contabilidad> getAllContabilidadByDates(Timestamp initDate, Timestamp endDate){
        List<Contabilidad> listContabilidad;
            listContabilidad = contabilidadDAO.getAllContabilidadByDates(initDate, endDate);
        return listContabilidad;
    }
     public List<Contabilidad> getAllContabilidadByDatesGroupByBankAccounts(Timestamp initDate, Timestamp endDate){
        List<Contabilidad> listContabilidad;
            listContabilidad = contabilidadDAO.getAllContabilidadByDatesGroupByBankAccounts(initDate, endDate);
        return listContabilidad;
    }
    public List<SubCategoriaContabilidad> getAllSubCategoriasContabilidadByCategoriaId(Integer categoriaId){
        
        List<SubCategoriaContabilidad> listSubCategoriaContabilidad;
            listSubCategoriaContabilidad = contabilidadDAO.getAllSubCategoriasContabilidadByCategoriaId(categoriaId);
        return listSubCategoriaContabilidad;
    }
    
    public List<CategoriaContabilidad> getAllCategoriasContabilidad(){
       
        List<CategoriaContabilidad> listCategoriaContabilidad;
            listCategoriaContabilidad = contabilidadDAO.getAllCategoriasContabilidad();
        return listCategoriaContabilidad;
    }
    
    public CategoriaContabilidad getCategoryByName(String category){
        return contabilidadDAO.getCategoryByName(category);
    }
    
     public SubCategoriaContabilidad getSubCategoryByName(String subCategory){
        return contabilidadDAO.getSubCategoryByName(subCategory);
    }
    
    public void save(Contabilidad contabilidad){
        contabilidadDAO.save(contabilidad);
    }
    
    public void saveCategory(CategoriaContabilidad category){
        contabilidadDAO.saveCategory(category);
    }
    public void saveSubCategory(SubCategoriaContabilidad subCategory){
        contabilidadDAO.saveSubCategory(subCategory);
    }
    
    public void updateCategory(CategoriaContabilidad category){
        contabilidadDAO.updateCategory(category);
    }
    
    public void deleteSubCategoryById(Integer id){
        contabilidadDAO.deleteSubCategoryById(id);
    }
    
     public void updateSubCategory(SubCategoriaContabilidad category){
        contabilidadDAO.updateSubCategory(category);
    }
    
    
}
