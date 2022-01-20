/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import clases.sqlclass;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import javax.swing.JOptionPane;
import model.Cliente;

/**
 *
 * @author jerry
 */
public class CustomerService {
    
    public Cliente obtenerClientePorId(sqlclass sql, int clienteId){
         String[] colName = {"id_clientes", 
            "nombre", "apellidos", "apodo", "tel_movil", "tel_fijo", 
            "email", "direccion", "localidad", "rfc","activo"
            };
         
        
         Object[][] dtconduc = null;
         try {
           dtconduc = sql.GetTabla(colName, "clientes", "SELECT * "
                + "FROM clientes "              
                + "WHERE id_clientes="+clienteId);
        } catch (SQLNonTransientConnectionException e) {
            sql.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        if(dtconduc == null || dtconduc.equals(""))
            return null;
         
        Cliente cliente = new Cliente();
        
        if(dtconduc[0][0] != null)
            cliente.setClienteId(new Integer(dtconduc[0][0].toString()));
        if(dtconduc[0][1] != null)
            cliente.setNombre(dtconduc[0][1].toString());
        if(dtconduc[0][2] != null)
            cliente.setApellidos(dtconduc[0][2].toString());
        if(dtconduc[0][3] != null)
            cliente.setApodo(dtconduc[0][3].toString());
        if(dtconduc[0][4] != null)
             cliente.setTelMovil(dtconduc[0][4].toString());
        if(dtconduc[0][5] != null)
             cliente.setTelFijo(dtconduc[0][5].toString());
        if(dtconduc[0][6] != null)
            cliente.setEmail(dtconduc[0][6].toString());
        if(dtconduc[0][7] != null)
            cliente.setDireccion(dtconduc[0][7].toString());
        if(dtconduc[0][8] != null)
            cliente.setLocalidad(dtconduc[0][8].toString());
        if(dtconduc[0][9] != null)
            cliente.setRfc(dtconduc[0][9].toString());
        if(dtconduc[0][10] != null)
            cliente.setActivo(dtconduc[0][10].toString());
        
        return cliente;
    }
    
}
