/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author idscomercial
 */
public class DatosGenerales {
    
    private Integer id;
    private String companyName;
    private String address1;
    private String address2;
    private String address3;
    private Integer folio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public Integer getFolio() {
        return folio;
    }

    public void setFolio(Integer folio) {
        this.folio = folio;
    }

    @Override
    public String toString() {
        return "DatosGenerales{" + "id=" + id + ", companyName=" + companyName + ", address1=" + address1 + ", address2=" + address2 + ", address3=" + address3 + ", folio=" + folio + '}';
    }
    
    
    
    
    
}
