/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parametersVO;

/**
 *
 * @author idscomercial
 */
public class ModelTableItem {
    private String quantity;
    private String item;
    private String unitPrice;
    private String discount;
    private String amount;

    public ModelTableItem() {
    }
    
    

    public ModelTableItem(String quantity, String item, String unitPrice, String discount, String amount) {
        this.quantity = quantity;
        this.item = item;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.amount = amount;
    }
    
    

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
    
    
}
