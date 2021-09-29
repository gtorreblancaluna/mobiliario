package mobiliario;

import services.ItemService;
import services.SaleService;
import services.SystemService;
import services.UserService;
import clases.FormatoTabla;
import clases.Mail;
import clases.sqlclass;
import com.mysql.jdbc.MysqlDataTruncation;
import exceptions.BusinessException;
import forms.proveedores.OrderProviderForm;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.Abono;
import model.Articulo;
import model.AsignaCategoria;
import model.DatosGenerales;
import model.DetalleRenta;
import model.Faltante;
import model.Renta;
import model.TipoAbono;
import model.Usuario;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import parametersVO.ModelTableItem;
import parametersVO.DataEmailTemplate;
import services.CategoryService;
import utilities.BuildEmailTemplate;
import utilities.Utility;

public class consultar_renta extends javax.swing.JInternalFrame {
    private OrderProviderForm orderProviderForm;
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(consultar_renta.class.getName());
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    // variable global para almacenar el id detalle de renta
    public static String g_idDetalleRenta;
    // variable global para almacenar el tipo de estado de la renta
    public static String g_idTipoEvento;
    public static String g_idRenta = null;
    public static String g_mensajeFaltantes = "";
    public static float g_totalFaltantes = 0f;
    public static String messageSucessfullyResults = "";
    // bandera para seleccionar si quiere generar el reporte pdf desde la tabla consulta
    private static boolean fgConsultaTabla=false;
    String fecha_sistema, sql, id_articulo, id_abonos, id_cliente, cant_abono = "0", subTotal = "0", chofer, id_tipo = "1", descuento, desc_rep, iva_rep, id_estado;
    public static String fecha_inicial, fecha_final, validar_consultar = "0", id_renta;
    sqlclass funcion = new sqlclass();    
    private final ItemService itemService;
    private final SaleService saleService;
    UserService userService = new UserService();
    private final SystemService systemService = SystemService.getInstance();
    CategoryService categoryService = new CategoryService();
    Object[][] dtconduc, datos_cliente;
    Object[] datos_combo;
    boolean cambios = false, existe = false, editar_abonos = false, panel = true;
    public static String cant, precio;
    public static boolean v_mod_precio = false;
    float canti = 0;

    /**
     * Creates new form consultar_renta
     */
    public consultar_renta() throws PropertyVetoException {
        funcion.conectate();
       
        initComponents();
        saleService = SaleService.getInstance();
        itemService = ItemService.getInstance();
        fecha_sistema();
        
        new Thread(() -> {
            llenar_combo_estado();
        }).start();
        new Thread(() -> {
            llenar_combo_tipo();
        }).start();
        new Thread(() -> {
            llenar_abonos();
        }).start();
        new Thread(() -> {
            llenar_combo_chofer_buscar();
        }).start();
        formato_tabla_detalles();
        new Thread(() -> {
            llenar_combo_limite();
        }).start();
        

        // lbl_aviso_resultados.setVisible(false);
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setEnabledAt(2, false);
        jbtn_guardar.setEnabled(false);
        jbtn_guardar_abonos.setEnabled(false);
        txt_subtotal.setEditable(false);
        txt_faltantes.setEditable(false);
        txt_abonos.setEditable(false);
        txt_descuento.setEditable(false);
        txt_total_iva.setEditable(false);
        txt_total.setEditable(false);
        txt_calculo.setEditable(false);
        jbtn_guardar_cliente.setEnabled(false);
        new Thread(() -> {
            tabla_clientes();
        }).start();
       
            buscar();
        panel_articulos.setVisible(false);
        jbtn_disponible.setEnabled(false);
        txt_buscar_folio.requestFocus();
        panel_conceptos.setVisible(true);
        panel_articulos.setVisible(false);
        check_nombre.setSelected(true);
        this.txt_editar_cantidad.setEnabled(false);
        this.txt_editar_precio_unitario.setEnabled(false);
        this.txt_editar_porcentaje_descuento.setEnabled(false);
        
    }
    
    private void disableButtonsActions () {
        
        jbtn_buscar.setEnabled(false);
        
        jbtn_generar_reporte.setEnabled(false);
        jbtn_generar_reporte1.setEnabled(false);
        
        jbtnGenerarReporteEntregas.setEnabled(false);
        jtbtnGenerateExcel.setEnabled(false);
        
        jButton2.setEnabled(false);
        jButton6.setEnabled(false);
        jbtn_refrescar.setEnabled(false);
    }
    
    private void enabledButtonsActions () {
        
        jbtn_buscar.setEnabled(true);
        
        jbtn_generar_reporte.setEnabled(true);
        jbtn_generar_reporte1.setEnabled(true);
        
        jbtnGenerarReporteEntregas.setEnabled(true);
        jtbtnGenerateExcel.setEnabled(true);
        
        jButton2.setEnabled(true);
        jButton6.setEnabled(true);
        
        jbtn_refrescar.setEnabled(true);
    }
    
    
    public void limpiar() {
        txt_nombre.setText("");
        txt_apellidos.setText("");
        txt_apodo.setText("");
        txt_tel_movil.setText("");
        txt_tel_casa.setText("");
        txt_email.setText("");
        txt_direccion.setText("");
        txt_localidad.setText("");
        txt_rfc.setText("");
        txt_nombre.requestFocus();
        
    }
    
    public void mostrar_disponibilidad() {
        disponibilidad_articulos ventana_disp = new disponibilidad_articulos(null, true);
        ventana_disp.setVisible(true);
        ventana_disp.setLocationRelativeTo(null);
    }
    
    public void mostrar_faltantes() {
        VerFaltantes ventana_faltantes = new VerFaltantes(null, true);
        ventana_faltantes.setVisible(true);
        ventana_faltantes.setLocationRelativeTo(null);
    }
    
     public void mostrar_agregar_orden_proveedor(String orderId) {
       if (Utility.verifyIfInternalFormIsOpen(orderProviderForm)) {
            orderProviderForm = new OrderProviderForm(orderId);
            orderProviderForm.setLocation(this.getWidth() / 2 - orderProviderForm.getWidth() / 2, this.getHeight() / 2 - orderProviderForm.getHeight() / 2 - 20);
            principal.jDesktopPane1.add(orderProviderForm);
            orderProviderForm.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }
    }
    
    public boolean agregar_cliente() {
        boolean res = false;
        if (txt_nombre.getText().equals("") || txt_apellidos.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Faltan parametros... Debes de agregar o elegir un cliente de la tabla para continuar...", "Error", JOptionPane.INFORMATION_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
            res = false;
        } else {
            try {
                String datos[] = {txt_nombre.getText().toString(), txt_apellidos.getText().toString(), txt_apodo.getText().toString(), txt_tel_movil.getText().toString(), txt_tel_casa.getText().toString(), txt_email.getText().toString(), txt_direccion.getText().toString(), txt_localidad.getText().toString(), txt_rfc.getText().toString(), "1"};
//            funcion.conectate();

                funcion.InsertarRegistro(datos, "insert into clientes (nombre,apellidos,apodo,tel_movil,tel_fijo,email,direccion,localidad,rfc,activo) values(?,?,?,?,?,?,?,?,?,?)");
                String id_cliente = funcion.ultimoid();
                String datos1[] = {id_cliente, id_renta};

                funcion.UpdateRegistro(datos1, "update renta set id_clientes=? where id_renta=?");

                // funcion.desconecta();

                tabla_clientes();
                res = true;

//jbtn_agregar.setEnabled(false);
            } catch (SQLException ex) {
                Logger.getLogger(consultar_renta.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Error al insertar registro ", "Error", JOptionPane.ERROR);
            }
        }
        return res;
        
    }
    
    public void reporte() throws RuntimeException {
        
        String xiva;
        String rentaId="";
        g_totalFaltantes = 0f;
        float yiva, itotal;
        if (fgConsultaTabla && tabla_prox_rentas.getSelectedRow() != - 1 ) {
            // a dado clic desde la tabla de consultar renta
            rentaId = tabla_prox_rentas.getValueAt(tabla_prox_rentas.getSelectedRow(), 0).toString();
        }else if(fgConsultaTabla && tabla_prox_rentas.getSelectedRow() == - 1){
            JOptionPane.showMessageDialog(null, "Selecciona una fila para generar el reporte ", "ERROR", JOptionPane.INFORMATION_MESSAGE);
            return;
        }else{
            rentaId = id_renta;
        }
            
            Renta renta = saleService.obtenerRentaPorId(new Integer(rentaId), funcion);
            if(renta == null){
                JOptionPane.showMessageDialog(null, "No se obtuvieron resultados, porfavor cierra y abre la aplicacion ", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            float fAbonos = 0f;
            float fSubTotal = 0f;
            float fCalculo = 0f;
            
            for(DetalleRenta detalle : renta.getDetalleRenta()){
                float fDescuento = 0f;
                if(detalle.getPorcentajeDescuento() > 0)
                    fDescuento += ( detalle.getCantidad() * detalle.getPrecioUnitario() ) * (detalle.getPorcentajeDescuento()/100);                
                    
                fSubTotal += ( detalle.getCantidad() * detalle.getPrecioUnitario() ) - fDescuento;
            }
            fCalculo = (fSubTotal+renta.getEnvioRecoleccion()+renta.getDepositoGarantia()) - renta.getCantidadDescuento(); 
            
             subTotal = fSubTotal+"";             
  
             if(renta.getAbonos() != null && renta.getAbonos().size()>0){               
                for(Abono abono : renta.getAbonos()){
                    fAbonos += abono.getAbono();
                }
             }
             
             float fSubtTotalFaltantes = 0f;
             float fTotalFaltantes = 0f;
             float totalFaltantes = 0f;
             
             List<Faltante> faltantes = itemService.obtenerFaltantesPorRentaId(funcion,renta.getRentaId());
             
             if(faltantes != null && faltantes.size() > 0){
                 for(Faltante faltante : faltantes){
                     if(faltante.getPrecioCobrar() == null){
                         totalFaltantes += (faltante.getCantidad() * faltante.getArticulo().getPrecioCompra());
                     } else {
                        totalFaltantes += (faltante.getCantidad() * faltante.getPrecioCobrar());
                     }
                 }
             }
             
            
            try {
                fSubtTotalFaltantes = totalFaltantes;
            } catch (Exception e) {
            }
            g_mensajeFaltantes = "";
            if(fSubtTotalFaltantes > 0){
                // el pedido tiene pago pendiente por faltante
                if(renta.getDepositoGarantia()>0){
                    // a dejado deposito en garantia
                    fTotalFaltantes = fSubtTotalFaltantes - renta.getDepositoGarantia();
                    if(fTotalFaltantes < 0)
                        this.g_mensajeFaltantes = "Dep\u00F3sito en garant\u00EDa es: $ "+renta.getDepositoGarantia()+", concepto faltantes es: $ "+fSubtTotalFaltantes+", cantidad a devolver al cliente: $ "+ (fTotalFaltantes * -1);
                    else if(fTotalFaltantes > 0)
                        this.g_mensajeFaltantes = "Dep\u00F3sito en garant\u00EDa es: $ "+renta.getDepositoGarantia()+", concepto faltantes es: $ "+fSubtTotalFaltantes+", resta: $ "+ (fTotalFaltantes);
                }else{
                    // se asgina el total                   
                    this.g_mensajeFaltantes = "Total a pagar por concepto de faltantes es: $ "+fSubtTotalFaltantes;
                    g_totalFaltantes = fSubtTotalFaltantes;
                }                
                
            }
            if(fTotalFaltantes>0)
                g_totalFaltantes = fTotalFaltantes;
            
             cant_abono = fAbonos+"";
             desc_rep  = renta.getCantidadDescuento()+"";
             xiva = renta.getIva()+"";
            
            if (cant_abono == null)
                cant_abono = "0";
            
            if (desc_rep == null)
                desc_rep = "0";
            
            if (xiva == null) {
                iva_rep = "0";
            } else {
                yiva = Float.parseFloat(xiva.toString());
                itotal = (fCalculo * (yiva / 100));
                iva_rep = String.valueOf(itotal);                
            }
            System.out.println("SUBTOTAL: " + subTotal);
            System.out.println("ABONO: " + cant_abono);
            chofer = renta.getChofer().getNombre() + " " + renta.getChofer().getApellidos();
        
        
        JasperPrint jasperPrint;
        try {
            fgConsultaTabla= false;
            String pathLocation = Utility.getPathLocation();
            String archivo = pathLocation+ApplicationConstants.RUTA_REPORTE_CONSULTA;
            System.out.println("Cargando desde: " + archivo);
            if (archivo == null) {
                JOptionPane.showMessageDialog(rootPane, "No se encuentra el Archivo jasper");
                return;
            }
            JasperReport masterReport = null;
            
            masterReport = (JasperReport) JRLoader.loadObject(archivo);
           
            DatosGenerales datosGenerales = systemService.getGeneralData();
            
            Map parametro = new HashMap<>();
            parametro.put("NOMBRE_EMPRESA",datosGenerales.getCompanyName());
            parametro.put("DIRECCION_1",datosGenerales.getAddress1());
            parametro.put("DIRECCION_2",datosGenerales.getAddress2());
            parametro.put("DIRECCION_3",datosGenerales.getAddress3());
            //guardamos el parámetro
            parametro.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );
            parametro.put("id_renta", rentaId);
            parametro.put("abonos", cant_abono);
            parametro.put("subTotal", subTotal);
            parametro.put("chofer", chofer);
            parametro.put("descuento", desc_rep);
            parametro.put("iva", iva_rep);
            parametro.put("total_faltantes", g_totalFaltantes);
            parametro.put("mensaje_faltantes", g_mensajeFaltantes);  
            parametro.put("URL_SUB_REPORT_CONSULTA", pathLocation+ApplicationConstants.URL_SUB_REPORT_CONSULTA);
         
            jasperPrint = JasperFillManager.fillReport(masterReport, parametro, funcion.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathLocation+ApplicationConstants.NOMBRE_REPORTE_CONSULTA);
            File file2 = new File(pathLocation+ApplicationConstants.NOMBRE_REPORTE_CONSULTA);
                
            Desktop.getDesktop().open(file2);
            
        } catch (Exception e) {
            log.error(e);
            System.out.println("Mensaje de Error:" + e.toString());
            JOptionPane.showMessageDialog(rootPane, "Error cargando el reporte maestro: " + e.getMessage());
        }
        
    }
    
    public void guardar_cliente() {
        
        // funcion.conectate();
        String nombre, ap, apodo, tel1, tel2, email, dir, loc, rfc;
        nombre = txt_nombre.getText().toString();
        ap = txt_apellidos.getText().toString();
        apodo = txt_apodo.getText().toString();
        tel1 = txt_tel_movil.getText().toString();
        tel2 = txt_tel_casa.getText();
        email = txt_email.getText().toString();
        dir = txt_direccion.getText().toString();
        loc = txt_localidad.getText().toString();
        rfc = txt_rfc.getText().toString();
        String datos[] = {nombre, ap, apodo, tel1, tel2, email, dir, loc, rfc, id_cliente};
        try {      
            funcion.UpdateRegistro(datos, "update clientes set nombre=?,apellidos=?,apodo=?,tel_movil=?,tel_fijo=?,email=?,direccion=?,localidad=?,rfc=? where id_clientes=? ");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        lbl_cliente.setText(nombre + " " + ap);
        
        // // funcion.desconecta();
        tabla_clientes();
        
    }
    
    public void tabla_clientes() {
        // funcion.conectate();
        tabla_clientes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Nombre", "Apellidos", "Apodo", "Tel Cel", "Tel Fijo", "Email ", "Direccion", "Localidad", "RFC"};
        String[] colName = {"id_clientes", "nombre", "apellidos", "apodo", "tel_movil", "tel_fijo", "email", "direccion", "localidad", "rfc"};
        //nombre de columnas, tabla, instruccion sql      
        try {       
            dtconduc = funcion.GetTabla(colName, "clientes", "SELECT * FROM clientes where activo = 1 ORDER BY apellidos");
        } catch (SQLNonTransientConnectionException e) {
            funcion.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        
        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        tabla_clientes.setModel(datos);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(datos); 
        tabla_clientes.setRowSorter(ordenarTabla);
        
        int[] anchos = {10, 100, 190, 100, 80, 80, 200, 100, 80, 80};
        
        for (int inn = 0; inn < tabla_clientes.getColumnCount(); inn++) {
            tabla_clientes.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        
        tabla_clientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_clientes.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_clientes.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // // funcion.desconecta();
    }
    
    public void tabla_clientes_like() {
        // funcion.conectate();
        tabla_clientes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Nombre", "Apellidos", "Apodo", "Tel Cel", "Tel Fijo", "Email", "Direccion", "Localidad", "RFC"};
        String[] colName = {"id_clientes", "nombre", "apellidos", "apodo", "tel_movil", "tel_fijo", "email", "direccion", "localidad", "rfc"};
        //nombre de columnas, tabla, instruccion sql 
        if (check_nombre.isSelected()) {
            try {       
                dtconduc = funcion.GetTabla(colName, "clientes", "SELECT * FROM clientes where nombre like '%" + txt_buscar1.getText() + "%' and activo = 1 ORDER BY nombre");
            } catch (SQLNonTransientConnectionException e) {
                funcion.conectate();
                JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            }
            
            System.out.println("Entra a nombre " + txt_nombre.getText());
        }
        if (check_apellidos.isSelected()) {
            try {       
                dtconduc = funcion.GetTabla(colName, "clientes", "SELECT * FROM clientes where apellidos like '%" + txt_buscar1.getText() + "%' and activo = 1 ORDER BY apellidos");
            } catch (SQLNonTransientConnectionException e) {
                funcion.conectate();
                JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            }
            
            System.out.println("Entra a apellidos " + txt_apellidos.getText());
        }
        if (check_apodo.isSelected()) {
             try {       
                dtconduc = funcion.GetTabla(colName, "clientes", "SELECT * FROM clientes where apodo like '%" + txt_buscar1.getText() + "%'  and activo = 1 ORDER BY apodo");
            } catch (SQLNonTransientConnectionException e) {
                funcion.conectate();
                JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
            }
            
            System.out.println("Entra a apodo " + txt_apodo.getText());
        }
        
        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        tabla_clientes.setModel(datos);
        
        int[] anchos = {10, 100, 190, 100, 80, 80, 200, 100, 80, 80};
        
        for (int inn = 0; inn < tabla_clientes.getColumnCount(); inn++) {
            tabla_clientes.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        
        tabla_clientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_clientes.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_clientes.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // // funcion.desconecta();
    }
    
    public void datos_cliente(int clienteId) {
        // funcion.conectate();
        String colName[] = {"id_clientes", "nombre", "apellidos", "apodo", "tel_movil", "tel_fijo", "email", "direccion", "localidad", "rfc"};
         try {       
            datos_cliente = funcion.GetTabla(colName, "clientes", "Select * from clientes where id_clientes='" + clienteId + "'");
        } catch (SQLNonTransientConnectionException e) {
            funcion.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        
       
        
        String nombre, ap, apodo, tel1, tel2, email, dir, loc, rfc;
        nombre = txt_nombre.getText().toString();
        ap = txt_apellidos.getText().toString();
        apodo = txt_apodo.getText().toString();
        tel1 = txt_tel_movil.getText().toString();
        tel2 = txt_tel_casa.getText();
        email = txt_email.getText().toString();
        dir = txt_direccion.getText().toString();
        loc = txt_localidad.getText().toString();
        rfc = txt_rfc.getText().toString();
        
        nombre = String.valueOf(datos_cliente[0][1].toString());
        ap = String.valueOf(datos_cliente[0][2].toString());
        apodo = String.valueOf(datos_cliente[0][3].toString());
        tel1 = String.valueOf(datos_cliente[0][4].toString());
        tel2 = String.valueOf(datos_cliente[0][5].toString());
        email = String.valueOf(datos_cliente[0][6].toString());
        dir = String.valueOf(datos_cliente[0][7].toString());
        loc = String.valueOf(datos_cliente[0][8].toString());
        rfc = String.valueOf(datos_cliente[0][9].toString());
        
        if (nombre.equals(null)) {
            nombre = "";
        }
        if (ap.equals(null)) {
            ap = "";
        }
        if (apodo.equals(null)) {
            apodo = "";
        }
        if (tel1.equals(null)) {
            tel1 = "";
        }
        if (tel2.equals(null)) {
            tel2 = "";
        }
        if (email.equals(null)) {
            email = "";
        }
        if (dir.equals(null)) {
            dir = "";
        }
        if (loc.equals(null)) {
            loc = "";
        }
        if (rfc.equals(null)) {
            rfc = "";
        }
        
        txt_nombre.setText(nombre);
        txt_apellidos.setText(ap);
        txt_apodo.setText(apodo);
        txt_tel_movil.setText(tel1);
        txt_tel_casa.setText(tel2);
        txt_email.setText(email);
        txt_direccion.setText(dir);
        txt_localidad.setText(loc);
        txt_rfc.setText(rfc);
        
        // // funcion.desconecta();
        
    }
    
    public void tabla_clientes_nombre() {
        // funcion.conectate();
        tabla_clientes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Nombre", "Apellidos", "Apodo", "Tel Cel", "Tel Fijo", "Email ", "Direccion", "Localidad", "RFC"};
        String[] colName = {"id_clientes", "nombre", "apellidos", "apodo", "tel_movil", "tel_fijo", "email", "direccion", "localidad", "rfc"};
        //nombre de columnas, tabla, instruccion sql
        try {       
            dtconduc = funcion.GetTabla(colName, "clientes", "SELECT * FROM clientes where activo = 1 AND nombre like '%" + txt_nombre.getText().toString() + "%' ORDER BY id_clientes DESC");
        } catch (SQLNonTransientConnectionException e) {
            funcion.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }        
        
        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        tabla_clientes.setModel(datos);
        
        int[] anchos = {10, 100, 190, 100, 80, 80, 200, 100, 80, 80};
        
        for (int inn = 0; inn < tabla_clientes.getColumnCount(); inn++) {
            tabla_clientes.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        
        tabla_clientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_clientes.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_clientes.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // funcion.desconecta();
    }
    
    public void tabla_clientes_apellidos() {
        // funcion.conectate();
        tabla_clientes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Nombre", "Apellidos", "Apodo", "Tel Cel", "Tel Fijo", "Email ", "Direccion", "Localidad", "RFC"};
        String[] colName = {"id_clientes", "nombre", "apellidos", "apodo", "tel_movil", "tel_fijo", "email", "direccion", "localidad", "rfc"};
        //nombre de columnas, tabla, instruccion sql        
         try {       
            dtconduc = funcion.GetTabla(colName, "clientes", "SELECT * FROM clientes where activo = 1 AND apellidos like '%" + txt_apellidos.getText().toString() + "%' ORDER BY id_clientes DESC");
        } catch (SQLNonTransientConnectionException e) {
            funcion.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        
        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        tabla_clientes.setModel(datos);
        
        int[] anchos = {10, 100, 190, 100, 80, 80, 200, 100, 80, 80};
        
        for (int inn = 0; inn < tabla_clientes.getColumnCount(); inn++) {
            tabla_clientes.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        
        tabla_clientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_clientes.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_clientes.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // funcion.desconecta();
    }
    
    public void subTotal() {
        String aux;
        float subTotal = 0, total = 0;
        for (int i = 0; i < tabla_detalle.getRowCount(); i++) {
            aux = EliminaCaracteres(((String) tabla_detalle.getValueAt(i, 7).toString()), "$,");
            subTotal = Float.parseFloat(aux);
            total = subTotal + total;
        }
//        txt_subtotal.setValue(total);
        txt_subtotal.setText(decimalFormat.format(total));
        
    }
    
    public void agregar_articulos() {
        existe = false;
        if (txt_cantidad.getText().equals("") || txt_precio_unitario.getText().equals("")) {
            
            JOptionPane.showMessageDialog(null, "Favor de completar los parametros para agregar articulos...", "Error", JOptionPane.INFORMATION_MESSAGE);
            
            Toolkit.getDefaultToolkit().beep();
        } else {
             float porcentajeDescuento = 0f;
            if(!this.txt_porcentaje_descuento.getText().equals("") ){
                try {
                    porcentajeDescuento = new Float(this.txt_porcentaje_descuento.getText()+"");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Ingresa un número valido para porcentaje descuento "+e, "Error", JOptionPane.INFORMATION_MESSAGE);
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            
            if(porcentajeDescuento < 0 || porcentajeDescuento > 100)
            {
                    JOptionPane.showMessageDialog(null, "Ingresa un rango de 0 al 100 para el porcentaje de descuento ", "Error", JOptionPane.INFORMATION_MESSAGE);
                    Toolkit.getDefaultToolkit().beep();
                    return;
            }
            
            
            existe = false;
//            if (Float.parseFloat(txt_cantidad.getText().toString()) <= canti) {
                for (int i = 0; i < tabla_detalle.getRowCount(); i++) {
                    System.out.println("lbl: " + lbl_eleccion.getText() + "  tabla: " + tabla_detalle.getValueAt(i, 3).toString());
                    if (lbl_eleccion.getText().toString().equals(tabla_detalle.getValueAt(i, 3).toString())) {
                        existe = true;
                        break;
                    }
                }
                if (existe == true) {
                    JOptionPane.showMessageDialog(null, "No se permiten duplicados...", "Error", JOptionPane.INFORMATION_MESSAGE);
                    
                    Toolkit.getDefaultToolkit().beep();
                    
                } else {
                    
                    
                
                 String estadoActualPedido = funcion.GetData("id_estado", "SELECT id_estado FROM renta WHERE id_renta=" + id_renta + "");
                  Articulo articulo = itemService.obtenerArticuloPorId(funcion, new Integer(id_articulo)); 
                 if( (ApplicationConstants.ESTADO_EN_RENTA.equals(estadoActualPedido )
                  ) ){         
                      //2018.11.16
                      // si el estado actual del evento es igual a EN RENTA, 
                      // procedemos a aumentar los articulos del contador en inventario "en_renta"                         
                    float cantidad = new Float(txt_cantidad.getText().toString());
                    String[] datos3 = { (articulo.getEnRenta()+cantidad+""), articulo.getArticuloId()+""};
                    
                    try {
                        funcion.UpdateRegistro(datos3, "UPDATE articulo SET en_renta=? WHERE id_articulo=?");
                     } catch (Exception e) {
                         JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                     }
                 }
                    
                    String datos[] = {id_renta, txt_cantidad.getText().toString(), id_articulo, txt_precio_unitario.getText().toString(),porcentajeDescuento+""};
//                    funcion.InsertarRegistro(datos, "INSERT INTO detalle_renta (id_renta,cantidad,id_articulo,p_unitario) values (?,?,?,?)");
                   int lastId = saleService.insertarDetalleRenta(datos, funcion);
                    // funcion.desconecta();
//                    tabla_detalle();
                    //String[] columnNames = {"id_detalle_renta", "cantidad", "id_articulo", "descripcion","precio u.", "importe","esNuevo"};
                    DefaultTableModel temp = (DefaultTableModel) tabla_detalle.getModel();              
                     float cantidad = Float.parseFloat(txt_cantidad.getText().toString());
                    float precio = Float.parseFloat(txt_precio_unitario.getText().toString());
                    float importe = (cantidad * precio);
                    float totalDescuento = 0f;
                    if(porcentajeDescuento > 0){
                        totalDescuento = importe * (porcentajeDescuento / 100);
                        importe = importe - totalDescuento;
                    }
                    Object nuevo[] = {
                                  lastId+"", 
                                  txt_cantidad.getText().toString(),
                                  articulo.getArticuloId()+"",
                                  articulo.getDescripcion()+" "+articulo.getColor().getColor(),
                                  conviertemoneda(txt_precio_unitario.getText()),
                                  porcentajeDescuento+"",
                                  totalDescuento+"",
                                  conviertemoneda(importe+""),
                                  "1"
                    };
                    temp.addRow(nuevo);
                    subTotal();
                    total();

                    //JOptionPane.showMessageDialog(null, "Agrega otra cantidad menor a la del inventario...", "Actualizacion", JOptionPane.INFORMATION_MESSAGE);
                    Toolkit.getDefaultToolkit().beep();
                    
                    panel_conceptos.setVisible(true);
                    panel_articulos.setVisible(false);
                    jbtn_mostrar_articulos.setEnabled(true);
                    panel = true;
                    
                }
//            } else {
//                JOptionPane.showMessageDialog(null, "Agrega otra cantidad menor a la del inventario...", "Actualizacion", JOptionPane.INFORMATION_MESSAGE);
//            }
        }
        
    }
    
    public void guardar_abonos() {
        System.out.println("Abono es: " + txt_abono.getText());
        if (txt_abono.getText().equals("") || 
                txt_abono.getText().equals("0") || 
                txt_abono.getText().equals("0.00") ||  
                this.cmbTipoPago.getSelectedIndex() == 0
                ) {
            JOptionPane.showMessageDialog(null, "Faltan datos ", "Actualizacion", JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            
//            try {
                String fechaPago = "";
                float cantidadAbono = 0f;
                
                if (cmb_fecha_pago.getDate() != null )
                    fechaPago = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_pago.getDate());              
                
                TipoAbono tipoAbono = saleService.obtenerTipoAbonoPorDescripcion(cmbTipoPago.getSelectedItem().toString());
                if(tipoAbono == null){
                 JOptionPane.showMessageDialog(null, "No se encontro el tipo de pago: "+cmbTipoPago.getSelectedItem().toString()+" en la bd\nContacta con el administrador del sistema", "Error", JOptionPane.INFORMATION_MESSAGE);
                 return;
                }
                
                try {
                    cantidadAbono = new Float(txt_abono.getText().trim());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error con la cantidad de pago\n"+e, "Error", JOptionPane.ERROR);
                    return;
                }
                jbtn_guardar_abonos.setEnabled(false);
                // funcion.conectate();
//                String datos[] = {txt_abono.getText().toString(), txt_comentario.getText().toString(), fechaPago,tipoAbono.getTipoAbonoId()+"",id_abonos};
                //array de datos , instruccion sql
                Abono abono = new Abono();
                abono.setAbono(cantidadAbono);
                abono.setComentario(txt_comentario.getText().toString());
                abono.setFechaPago(fechaPago);
                abono.setTipoAbono(tipoAbono);
                abono.setAbonoId(new Integer(id_abonos));
                saleService.actualizarAbonoPorId(abono);
                
//                funcion.UpdateRegistro(datos, "UPDATE abonos set abono=?,comentario=?,fecha_pago=? where id_abonos=?");
                log.debug("el usuario: "+iniciar_sesion.usuarioGlobal.getNombre()+" "
                        +iniciar_sesion.usuarioGlobal.getApellidos()+" a modificado el abono a: "+txt_abono.getText().toString()+
                        " id_renta: "+this.id_renta);
                
                // funcion.desconecta();
                JOptionPane.showMessageDialog(null, "Se actualizo con exito el abono...", "Actualizacion", JOptionPane.INFORMATION_MESSAGE);
                txt_abono.setText("0");
                txt_comentario.setText("");
                cmb_fecha_pago.setDate(null);
                this.cmb_fecha_pago.setDate(null);
                this.cmbTipoPago.setSelectedIndex(0);
                tabla_abonos(id_renta);
                abonos();
                
                total();
                editar_abonos = false;
//            } catch (SQLException ex) {
//                Logger.getLogger(consultar_renta.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        
    }
    
    public void editar_abonos() {
        if (iniciar_sesion.administrador_global.equals("1")) {
            try {
              
                editar_abonos = true;
                txt_abono.requestFocus();
                id_abonos = tabla_abonos.getValueAt(tabla_abonos.getSelectedRow(), 0).toString();
                
                jbtn_guardar_abonos.setEnabled(true);
                
                txt_abono.setText(EliminaCaracteres(tabla_abonos.getValueAt(tabla_abonos.getSelectedRow(), 3).toString(), "$,"));
                txt_comentario.setText((String) tabla_abonos.getValueAt(tabla_abonos.getSelectedRow(), 4).toString());
                this.cmbTipoPago.setSelectedItem(tabla_abonos.getValueAt(tabla_abonos.getSelectedRow(), 5).toString());
                
                
                SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
                String fechaPago = (String) (tabla_abonos.getValueAt(tabla_abonos.getSelectedRow(), 7));
                if(fechaPago != null && !fechaPago.equals(""))
                    cmb_fecha_pago.setDate((Date) formatDate.parse(tabla_abonos.getValueAt(tabla_abonos.getSelectedRow(), 7).toString()));
                
                JOptionPane.showMessageDialog(null, "Puedes modificar el abono...", "Abonos", JOptionPane.INFORMATION_MESSAGE);
                
                Toolkit.getDefaultToolkit().beep();
                log.debug("el usuario: "+iniciar_sesion.usuarioGlobal.getNombre()+" "
                      +iniciar_sesion.usuarioGlobal.getApellidos()+" intenta modificar el abono: "+txt_abono.getText().toString()+
                      " id_renta: "+this.id_renta);
            } catch (ParseException ex) {
                Logger.getLogger(consultar_renta.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } else {
            JOptionPane.showMessageDialog(null, "No cuenta con permisos suficientos :(", "Error", JOptionPane.INFORMATION_MESSAGE);
            
        }
        
    }
    
    public void agregar_abonos() {
        StringBuilder mensaje = new StringBuilder();
        if (txt_abono.getText().equals("") || txt_abono.getText().equals("0"))
            mensaje.append("Ingresa un abono para continuar\n");
        else{
            try {
                float f = new Float(txt_abono.getText()+"");
            } catch (NumberFormatException e) {
                mensaje.append("Ingresa solo números con formato válido\n");
            } catch (Exception e) {
                mensaje.append("Ocurrio un error inesperado al obtener la cantidad de abono\n");
            }
        }
        
        int tipoId = this.cmbTipoPago.getSelectedIndex();
        if(tipoId == 0)
            mensaje.append("Ingresa un tipo de pago\n");
        
        if(!mensaje.toString().equals("")){
            JOptionPane.showMessageDialog(null, mensaje+"", "Error", JOptionPane.INFORMATION_MESSAGE);            
            Toolkit.getDefaultToolkit().beep();
            return;
        }
                String fechaPago = "";
                if (cmb_fecha_pago.getDate() != null )
                    fechaPago = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_pago.getDate());            
                fecha_sistema();
                // funcion.conectate();
                String tipoAbonoId = funcion.GetData("id_tipo_abono", "SELECT id_tipo_abono FROM tipo_abono "
                    + "WHERE descripcion='" + cmbTipoPago.getSelectedItem().toString() + "'");
                String datos[] = {id_renta, iniciar_sesion.id_usuario_global, fecha_sistema, txt_abono.getText().toString(),
                    txt_comentario.getText(),fechaPago,tipoAbonoId};
                try {
                     funcion.InsertarRegistro(datos, "INSERT INTO abonos (id_renta,id_usuario,fecha,abono,comentario,fecha_pago,id_tipo_abono) VALUES (?,?,?,?,?,?,?)");
                } catch (SQLException ex) {                
                    JOptionPane.showMessageDialog(null, "Error al insertar registro "+ex, "Error", JOptionPane.ERROR);                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR);
                }
               
                // funcion.desconecta();
                tabla_abonos(id_renta);
                abonos();
                
                subTotal();
                total();
                editar_abonos = false;
                txt_abono.setText("0");
           
            
        
        
    }
    
    public void quitar_articulo() {

        int seleccion = JOptionPane.showOptionDialog(this, "¿Eliminar registro?  " + tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 3).toString(), "Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
        
        if (seleccion != -1) {
            if ((seleccion + 1) == 1) {
//                 String estadoId = funcion.GetData("id_estado", "Select id_estado from estado where descripcion='" + cmb_estado1.getSelectedItem().toString() + "'");
                 String estadoActualPedido = funcion.GetData("id_estado", "SELECT id_estado FROM renta WHERE id_renta=" + id_renta + "");
                                 
                 // si el estado actual del evento es igual a EN RENTA, procedemos a descontar los articulos del contador en inventario "en_renta"              
                 if( (ApplicationConstants.ESTADO_EN_RENTA.equals(estadoActualPedido )
                  ) ){

                    float cantidadDetalleVenta = Float.parseFloat((String) tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 1).toString());
                    String en_renta = funcion.GetData("en_renta", "SELECT en_renta FROM articulo WHERE id_articulo ='" + ((String) tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 2).toString()) + "'");
                    float resta = Float.parseFloat(en_renta.toString()) - cantidadDetalleVenta;// restamos la cantidad restada

                    String id = ((String) tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 2).toString());
                    String[] datos3 = {String.valueOf(resta), id};
                     try {
                         funcion.UpdateRegistro(datos3, "UPDATE articulo SET en_renta=? WHERE id_articulo=?");
                     } catch (Exception e) {
                         JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                     }
                    
                }
                 try {
                        funcion.DeleteRegistro("detalle_renta", "id_detalle_renta", tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 0).toString());
                     } catch (Exception e) {
                         JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                     }
                
                // funcion.desconecta();
                tabla_detalle();
                
               this.llenarTablaDetalle();
                
                
                subTotal();
                total();
                tabla_articulos();
                this.txt_editar_porcentaje_descuento.setText("");
                this.txt_editar_cantidad.setText("");
                this.txt_editar_precio_unitario.setText("");
            }
        }
        
    }
    
    public void quitar_abono() {
        if (iniciar_sesion.administrador_global.equals("1")) {
            int seleccion = JOptionPane.showOptionDialog(this, "¿Eliminar registro?  " + tabla_abonos.getValueAt(tabla_abonos.getSelectedRow(), 3).toString(), "Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            
            if (seleccion == 0) {
                try {
                    // funcion.conectate();
                    funcion.DeleteRegistro("abonos", "id_abonos", tabla_abonos.getValueAt(tabla_abonos.getSelectedRow(), 0).toString());
                    // funcion.desconecta();
                    tabla_abonos(id_renta);
                    abonos();
                    subTotal();
                    total();
                } catch (SQLException ex) {
                    Logger.getLogger(consultar_renta.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            
        }
        
    }
    
    public void actualizar_renta() {
        if (cmb_fecha_entrega.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Falta fecha de entrega ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmb_fecha_devolucion.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Falta fecha de devolucion ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmb_fecha_evento.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Falta fecha del evento ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmb_hora.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar hora ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmb_hora_dos.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar segunda hora ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (this.cmb_hora_devolucion.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar hora devolucion ", "Error", JOptionPane.INFORMATION_MESSAGE);
          } else if (this.cmb_hora_devolucion_dos.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar segunda hora devolucion ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmb_hora.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar meridiano (am)(pm)", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (txt_descripcion.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Favor de ingresar una descripcion ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (tabla_detalle.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Favor de ingresar detalle de conceptos ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (tabla_abonos.getRowCount() == 0) {
            int seleccion = JOptionPane.showOptionDialog(this, "No has registrado abonos,\n ¿Deseas continuar?", "Falta registrar abonos", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            if (seleccion == 0) {//presiono que si
                try { 
                 actualizar();
                } catch (SQLNonTransientConnectionException e) {
                    System.out.println("la conexion se ha cerrado "+e);
                    funcion.conectate();
                    JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n"+e, "Error", JOptionPane.ERROR_MESSAGE); 
                } catch (MysqlDataTruncation e) {
                    JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);             
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } else {
             try { 
                 actualizar();
             } catch (SQLNonTransientConnectionException e) {
                System.out.println("la conexion se ha cerrado "+e);
                funcion.conectate();
                JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n"+e, "Error", JOptionPane.ERROR_MESSAGE); 
             } catch (MysqlDataTruncation e) {
                 JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
             } catch (SQLException e) {
                 JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);             
             } catch (Exception e) {
                 JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
             }
        }
    }
    
    public void actualizar() throws Exception{
        
        if (check_enviar_email.isSelected() == true){
           try{
                Utility.isEmail(this.txtEmailToSend.getText());
           }catch(MessagingException e){
               JOptionPane.showMessageDialog(null,e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
               return;
           }
        }
        
        String aviso = "";
        // funcion.conectate();
        //String descuento, iva;
        String hora_entrega = cmb_hora.getSelectedItem().toString()+" a "+cmb_hora_dos.getSelectedItem().toString();
//        String hora_entrega = cmb_hora.getSelectedItem().toString() + ":" + cmb_minutos.getSelectedItem().toString() + " " + cmb_meridiano.getSelectedItem().toString();
        String fecha_entrega = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_entrega.getDate());
        String fecha_devolucion = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_devolucion.getDate());
        String fecha_evento = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_evento.getDate());
       
        String id_estado = funcion.GetData("id_estado", "Select id_estado from estado where descripcion='" + cmb_estado1.getSelectedItem().toString() + "'");
        String id_chofer = funcion.GetData("id_usuarios", "Select id_usuarios from usuarios where CONCAT(nombre,\" \",apellidos)='" + cmb_chofer.getSelectedItem().toString() + "'");
        String id_tipo = funcion.GetData("id_tipo", "Select id_tipo from tipo where tipo='" + cmb_tipo.getSelectedItem().toString() + "'");
        System.out.println("id_estado: "+id_estado);
//        String estadoActualPedido = funcion.GetData("id_estado", "SELECT id_estado FROM renta WHERE id_renta=" + id_renta + "");
//        System.out.println("estadoActualPedido: "+estadoActualPedido); 

        /*
            // si el pedido actual es diferente a EN RENTA y eligieron EN RENTA
            if( (!ApplicationConstants.ESTADO_EN_RENTA.equals(estadoActualPedido )
                    && id_estado.equals(ApplicationConstants.ESTADO_EN_RENTA)
            ) ){
            for (int i = 0; i <= tabla_detalle.getRowCount() - 1; i++) {                



                    // 2018.11.14
                // vamos a incrementar el campo "en renta" para no modificar el stock
                
                String esNuevo = tabla_detalle.getValueAt(i, 6).toString();
                if(esNuevo.equals("0")){
                    // solo aumentaremos los articulos que vengan desde un inicio
                    String cant = EliminaCaracteres(tabla_detalle.getValueAt(i, 1).toString(), "$,");
                    String id_art = tabla_detalle.getValueAt(i, 2).toString();
                    String cantEnRenta = funcion.GetData("en_renta", "SELECT en_renta FROM articulo WHERE id_articulo=" + id_art + "");
                    System.out.println("EN RENTA: "+cantEnRenta);
                    if(cantEnRenta == null || cantEnRenta.equals(""))
                        cantEnRenta = "0";                
                    float aumentar = Float.parseFloat(cantEnRenta) + Float.parseFloat(cant);
                    System.out.println("AUMENTAR: "+aumentar);
                    String[] datos_en_renta= {String.valueOf(aumentar), id_art};                
                    funcion.UpdateRegistro(datos_en_renta, "UPDATE articulo SET en_renta=? WHERE id_articulo=?");
                }
                
            }
            aviso = " Se resto del inventario los articulos de renta";
        }
        */
            /*

            if( ApplicationConstants.ESTADO_EN_RENTA.equals(estadoActualPedido) && !id_estado.equals(ApplicationConstants.ESTADO_EN_RENTA) ){
                // aumentar los articulos al inventario
                for (int i = 0; i <= tabla_detalle.getRowCount() - 1; i++) {
                // 2018.11.14
                // vamos a incrementar el campo "en renta" para no modificar el stock
                
                String cant = EliminaCaracteres(tabla_detalle.getValueAt(i, 1).toString(), "$,");
                String id_art = tabla_detalle.getValueAt(i, 2).toString();
                String cantEnRenta = funcion.GetData("en_renta", "SELECT en_renta FROM articulo WHERE id_articulo=" + id_art + "");
                System.out.println("EN RENTA: "+cantEnRenta);
                if(cantEnRenta == null || cantEnRenta.equals(""))
                    cantEnRenta = "0";                
                float restar = Float.parseFloat(cantEnRenta) - Float.parseFloat(cant);
                System.out.println("RESTAR: "+restar);
                String[] datos_en_renta= {String.valueOf(restar), id_art};                
                funcion.UpdateRegistro(datos_en_renta, "UPDATE articulo SET en_renta=? WHERE id_articulo=?");
                    
                }
                aviso = "Se aumento los articulos de renta al inventario";

            
        }
            
            */
        String porcentajeDescuentoRenta;
        String cantidadDescuento;
        if (!txt_descuento.getText().toString().equals("") && !txtPorcentajeDescuento.getText().toString().equals("")) {
            cantidadDescuento = EliminaCaracteres(txt_descuento.getText().toString(), "$");
            cantidadDescuento = cantidadDescuento.replaceAll(",", "");
            porcentajeDescuentoRenta = this.txtPorcentajeDescuento.getText()+"";
        } else {
            porcentajeDescuentoRenta = "0";
            cantidadDescuento = "0";
        }
        String iva;
        if (!txt_iva.getText().toString().equals("")) {
            iva = EliminaCaracteres(txt_iva.getText().toString(), "$");
            iva = iva.replaceAll(",", "");
        } else {
            iva = "0";
        }
        
        String mostrarPrecios = check_mostrar_precios.isSelected() == true ? "1" : "0";
        String envioRecoleccion = this.txt_envioRecoleccion.getText().toString().equals("") ? "0" : this.txt_envioRecoleccion.getText().toString().replaceAll(",", "");
        String depositoGarantia = this.txt_depositoGarantia.getText().toString().equals("") ? "0" : this.txt_depositoGarantia.getText().toString().replaceAll(",", "");; 
        String hora_devolucion = this.cmb_hora_devolucion.getSelectedItem()+" a "+this.cmb_hora_devolucion_dos.getSelectedItem();
        String datos[] = {id_estado, fecha_entrega, hora_entrega, fecha_devolucion, txt_descripcion.getText().toString(),porcentajeDescuentoRenta,cantidadDescuento, txt_comentarios.getText().toString(), id_chofer, id_tipo, hora_devolucion,fecha_evento,depositoGarantia,envioRecoleccion,iva,mostrarPrecios,id_renta};
        //array de datos , instruccion sql
        funcion.UpdateRegistro(datos, "update renta set id_estado=?,fecha_entrega=?,hora_entrega=?,fecha_devolucion=?,descripcion=?,descuento=?,cantidad_descuento=?,comentario=?,id_usuario_chofer=?,id_tipo=?,hora_devolucion=?,fecha_evento=?,deposito_garantia=?,envio_recoleccion=?,iva=?,mostrar_precios_pdf=? where id_renta=?");
//        JOptionPane.showMessageDialog(null, "Se actualizo con exito...\n" + aviso, "Actualizar datos de renta", JOptionPane.INFORMATION_MESSAGE);
        Utility.pushNotification(iniciar_sesion.usuarioGlobal.getNombre() + " actualizó con éxito el folio "+this.lbl_folio.getText());
        // funcion.desconecta();
        jbtn_guardar.setEnabled(false);
        funcion.setEnableContainer(panel_datos_generales, false);
        tabla_articulos();
        descuento = txt_descuento.getText().toString();
        
        if (check_enviar_email.isSelected() == true){
          
                enviar_email();      
            
                     
        }
    }

    //ActionListener es la clase encargada de escuchar eventos de acción
    ActionListener al = new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            System.out.println("Se ha hecho click en el botón");
        }
    };
    
    public void abonos() {
        String aux;
        float abonos = 0;
        for (int i = 0; i < tabla_abonos.getRowCount(); i++) {
            aux = EliminaCaracteres(((String) tabla_abonos.getValueAt(i, 3).toString()), "$,");
            abonos = Float.parseFloat(aux) + abonos;
            System.out.println("Abono es: " + abonos);
            
        }
//        txt_abonos.setValue(abonos);
        txt_abonos.setText(decimalFormat.format(abonos));
        
    }
    
    public String conviertemoneda(String valor) {
        
        DecimalFormatSymbols simbolo = new DecimalFormatSymbols();
        simbolo.setDecimalSeparator('.');
        simbolo.setGroupingSeparator(',');
        
        float entero = Float.parseFloat(valor);
        DecimalFormat formateador = new DecimalFormat("###,###.##", simbolo);
        String entero2 = formateador.format(entero);
        
        if (entero2.contains(".")) {
            entero2 = "$" + entero2;
            
        } else {
            entero2 = "$" + entero2 + ".00";
        }
        
        return entero2;
        
    }
    
    public void modificar_detalle() {
        cant = ((String) tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 1).toString());
        precio = EliminaCaracteres(((String) tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 3).toString()), "$,");
        
    }
    
    public void llenarTablaDetalle(){
         DefaultTableModel tablaDetalle = (DefaultTableModel) tabla_detalle.getModel();
                Renta renta = saleService.obtenerRentaPorId(new Integer(id_renta), funcion);
                
                for(DetalleRenta detalle : renta.getDetalleRenta()){
                float descuento = 0f;
                float importe = detalle.getCantidad()*detalle.getPrecioUnitario();
                if(detalle.getPorcentajeDescuento() > 0)
                    descuento = (importe * (detalle.getPorcentajeDescuento() / 100));
                
                importe = importe - descuento;
                
                    Object fila[] = {
                        detalle.getDetalleRentaId()+"",                       
                        detalle.getCantidad()+"",
                        detalle.getArticulo().getArticuloId()+"",                                                      
                        detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor(),
                        conviertemoneda(detalle.getPrecioUnitario()+""),
                        detalle.getPorcentajeDescuento()+"",
                        descuento+"",                        
                        conviertemoneda(importe+""),
                        "0"
                    
                    };
                    tablaDetalle.addRow(fila);
            }
    
    }
    
    public void formato_tabla_detalles() {
        Object[][] data = {{"", "", "", "", "","","","",""}};
        String[] columnNames = {"id_detalle_renta", "cantidad", "id_articulo", "descripcion","precio u.", "descuento %","descuento","importe","esNuevo"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tabla_detalle.setModel(TableModel);
        
        int[] anchos = {70, 100, 100, 300,70,70 ,70,70,60};
        
        for (int inn = 0; inn < tabla_detalle.getColumnCount(); inn++) {
            tabla_detalle.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        DefaultTableCellRenderer alignCenter = new DefaultTableCellRenderer();
        alignCenter.setHorizontalAlignment(SwingConstants.RIGHT);
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_detalle.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        tabla_detalle.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_detalle.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_detalle.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        tabla_detalle.getColumnModel().getColumn(2).setMaxWidth(0);
        tabla_detalle.getColumnModel().getColumn(2).setMinWidth(0);
        tabla_detalle.getColumnModel().getColumn(2).setPreferredWidth(0);
        
        tabla_detalle.getColumnModel().getColumn(8).setMaxWidth(0);
        tabla_detalle.getColumnModel().getColumn(8).setMinWidth(0);
        tabla_detalle.getColumnModel().getColumn(8).setPreferredWidth(0);
     
        tabla_detalle.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tabla_detalle.getColumnModel().getColumn(3).setCellRenderer(centrar);
        tabla_detalle.getColumnModel().getColumn(4).setCellRenderer(alignCenter);
        tabla_detalle.getColumnModel().getColumn(5).setCellRenderer(alignCenter);
        tabla_detalle.getColumnModel().getColumn(6).setCellRenderer(alignCenter);
        tabla_detalle.getColumnModel().getColumn(7).setCellRenderer(alignCenter);
        
    }
    
    public void tabla_detalle() {
        formato_tabla_detalles();       
        // funcion.conectate();
//        tabla_detalle.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        String[] columNames = {"Id", "Cant", "Id art", "Articulo", "Precio", "Imp."};
//        String[] colName = {"id_detalle_renta", "cantidad", "id_articulo", "Articulo", "p_unitario", "Importe"};
//        //nombre de columnas, tabla, instruccion sql        
//        dtconduc = funcion.GetTabla(colName, "detalle_cotizacion", "SELECT d.`id_detalle_renta`, d.`cantidad`,d.`id_articulo`, CONCAT(a.`descripcion`,\" \",c.`color`)As Articulo, d.`p_unitario`,(d.`cantidad`*d.`p_unitario`)As Importe FROM detalle_renta d, articulo a, color c\n"
//                + "WHERE d.id_articulo=a.id_articulo AND a.id_color=c.id_color AND d.`id_renta` ='" + id_renta + "'");
//         DefaultTableModel temp = (DefaultTableModel) tabla_detalle.getModel();        
//         
//         for (int j = 0; j < dtconduc.length; j++) {             
//              
//              Object nuevo[] = {
//                            dtconduc[j][0].toString(), 
//                            dtconduc[j][1].toString(),
//                            dtconduc[j][2].toString(),
//                            dtconduc[j][3].toString(),
//                            conviertemoneda(dtconduc[j][4].toString()),
//                            conviertemoneda(dtconduc[j][5].toString()),
//                            "0"
//                        };
//                        temp.addRow(nuevo);
//         }        

        
    }
    
    public void formato_tabla_abonos() {
        Object[][] data = {{"","", "", "","","","",""}};
        String[] columnNames = {"id_abonos","Usuario", "Fecha", "Pago","Comentario","Tipo pago","id_tipo_abono","fecha_pago"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tabla_abonos.setModel(TableModel);
        
        int[] anchos = {60, 80, 80,60,90,90,60,90};
        
        for (int inn = 0; inn < tabla_abonos.getColumnCount(); inn++) {
            tabla_abonos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_abonos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        tabla_abonos.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_abonos.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_abonos.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        tabla_abonos.getColumnModel().getColumn(6).setMaxWidth(0);
        tabla_abonos.getColumnModel().getColumn(6).setMinWidth(0);
        tabla_abonos.getColumnModel().getColumn(6).setPreferredWidth(0);
        
        tabla_abonos.getColumnModel().getColumn(1).setCellRenderer(TablaRenderer);
        tabla_abonos.getColumnModel().getColumn(0).setCellRenderer(centrar);
        
    }
    
    public void fecha_sistema() {
        Calendar fecha = Calendar.getInstance();
        String mes = Integer.toString(fecha.get(Calendar.MONTH) + 1);
        String dia = Integer.toString(fecha.get(Calendar.DATE));
        String auxMes = null, auxDia = null;
        
        if (mes.length() == 1) {
            auxMes = "0" + mes;
            fecha_sistema = fecha.get(Calendar.DATE) + "/" + auxMes + "/" + fecha.get(Calendar.YEAR);
            
            if (dia.length() == 1) {
                auxDia = "0" + dia;
                fecha_sistema = auxDia + "/" + auxMes + "/" + fecha.get(Calendar.YEAR);
                
            }
            
        } else {
            fecha_sistema = fecha.get(Calendar.DATE) + "/" + (fecha.get(Calendar.MONTH) + 1) + "/" + fecha.get(Calendar.YEAR);
        }
    }
    
    public void total() {
        float fSubtotal = 0f;
        float fDescuento = 0f;
        float fEnvioRecoleccion = 0f;
        float fDepositoGarantia = 0f;
        float fAbonos = 0f;
        float fIVA = 0f;
        float fTotalIVA = 0f; 
        float fTotal = 0f;
        float fCalculo = 0F;
        float fPorcentaejeDescuento = 0f;
        float fFaltantes = 0f;
        
        try {
            
            if(!this.txtPorcentajeDescuento.getText().equals(""))
                fPorcentaejeDescuento = new Float(this.txtPorcentajeDescuento.getText().toString().replaceAll(",", ""));
            
            if(!this.txt_subtotal.getText().equals("") )
                fSubtotal = new Float(this.txt_subtotal.getText().toString().replaceAll(",", ""));
            
            if(!this.txt_descuento.getText().equals(""))
                fDescuento = new Float(this.txt_descuento.getText().toString().replaceAll(",", ""));
            
            if(!this.txt_envioRecoleccion.getText().equals(""))
                fEnvioRecoleccion = new Float(this.txt_envioRecoleccion.getText().toString().replaceAll(",", ""));
            
            if(!this.txt_depositoGarantia.getText().equals(""))
                fDepositoGarantia = new Float (this.txt_depositoGarantia.getText().toString().replaceAll(",", ""));
            
            if(!this.txt_abonos.getText().equals(""))
                fAbonos = new Float (this.txt_abonos.getText().toString().replaceAll(",", ""));      
            
            if(fPorcentaejeDescuento == 0)
                this.txt_descuento.setValue(0);
            if(!this.txt_faltantes.equals(""))
                fFaltantes = new Float(this.txt_faltantes.getText().toString().replaceAll(",", ""));
            
          
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento;
            
            
            if(fPorcentaejeDescuento > 0){
                fDescuento = (fSubtotal * (fPorcentaejeDescuento / 100));
//                this.txt_descuento.setValue(fDescuento);
                this.txt_descuento.setText(decimalFormat.format(fDescuento));
            
            }            
                
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento; 
            if(!this.txt_iva.getText().equals(""))
            {              
                fIVA = new Float(this.txt_iva.getText().toString().replaceAll(",", ""));
                fTotalIVA = (fCalculo * (fIVA / 100));              
//                this.txt_total_iva.setValue(fTotalIVA);
                this.txt_total_iva.setText(decimalFormat.format(fTotalIVA));
            }
            
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento; 
            this.txt_calculo.setValue(fCalculo);            
            
             if(!this.txt_total_iva.getText().equals(""))
                fTotalIVA = new Float(this.txt_total_iva.getText().toString().replaceAll(",", ""));
            
            
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento;
//            this.txt_calculo.setValue((fCalculo));
            this.txt_calculo.setText(decimalFormat.format(fCalculo));
            
            
            // TOTALES            
            fTotal = (fCalculo - fAbonos )+fFaltantes;
           
            if(fTotal < 0)
                fTotal = 0f;
        
//            txt_total.setValue((fTotal));
            this.txt_total.setText(decimalFormat.format(fTotal));
            
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(null, "Error al calcular el total "+e, "SOLO NUMEROS", JOptionPane.INFORMATION_MESSAGE);
             return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado al calcular el total "+e, "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
    }
    
    public void llenar_combo_limite(){
        this.cmb_limit.removeAllItems();
        this.cmb_limit.addItem("100");
        this.cmb_limit.addItem("1000");
        this.cmb_limit.addItem("5000");
        this.cmb_limit.addItem("todos");
    }
    
    public void llenar_combo_estado() {
        //
        // funcion.conectate();
        datos_combo = funcion.GetColumna("estado", "descripcion", "Select descripcion from estado");
        cmb_estado.removeAllItems();
        
        for (int i = 0; i <= datos_combo.length - 1; i++) {
            cmb_estado.addItem(datos_combo[i].toString());
            
        }
        // funcion.desconecta();
        
    }
    
    public void llenar_combo_tipo() {
        //
        // funcion.conectate();
        datos_combo = funcion.GetColumna("tipo", "tipo", "Select tipo from tipo");
        cmb_tipo.removeAllItems();
        
        for (int i = 0; i <= datos_combo.length - 1; i++) {
            cmb_tipo.addItem(datos_combo[i].toString());
            
        }
        // funcion.desconecta();
        
    }
    
    public void llenar_abonos() {

        List<TipoAbono> tiposAbonos =  saleService.obtenerTiposAbono(funcion);
        this.cmbTipoPago.removeAllItems();
        cmbTipoPago.addItem("-sel-");
        if(tiposAbonos != null && tiposAbonos.size()>0){
            for(TipoAbono tipo : tiposAbonos){               
                   cmbTipoPago.addItem(tipo.getDescripcion());
            }
        }        
        
        cmbTipoPago.setSelectedItem("-sel-");
    }
    public void llenar_combo_chofer() {
        
        datos_combo = funcion.GetColumna("usuarios", "nombre", "SELECT CONCAT(u.`nombre`,\" \", u.`apellidos`)AS nombre FROM usuarios u");
        cmb_chofer.removeAllItems();
     
        
        for (int i = 0; i <= datos_combo.length - 1; i++) {
            cmb_chofer.addItem(datos_combo[i].toString());
                      
        }
       
        
    }
    
    
    
    public void llenar_combo_chofer_buscar() {
        //
        // funcion.conectate();
        datos_combo = funcion.GetColumna("usuarios", "nombre", "SELECT CONCAT(u.`nombre`,\" \", u.`apellidos`)AS nombre FROM usuarios u");
        cmb_chofer_buscar.removeAllItems();
           this.cmbUsuarios.removeAllItems();
           this.cmbUsuarios.addItem("- seleccione -");
        for (int i = 0; i <= datos_combo.length - 1; i++) {
            cmb_chofer_buscar.addItem(datos_combo[i].toString());
             this.cmbUsuarios.addItem(datos_combo[i].toString()); 
            
        }
        // funcion.desconecta();
        
    }
    
    public void llenar_combo_estado2() {
        //
        // funcion.conectate();
        datos_combo = funcion.GetColumna("estado", "descripcion", "Select descripcion from estado");
        cmb_estado1.removeAllItems();
        
        for (int i = 0; i <= datos_combo.length - 1; i++) {
            cmb_estado1.addItem(datos_combo[i].toString());
            
        }
        // funcion.desconecta();
        
    }
    
    public String EliminaCaracteres(String s_cadena, String s_caracteres) {
        String nueva_cadena = "";
        Character caracter = null;
        boolean valido = true;

        /* Va recorriendo la cadena s_cadena y copia a la cadena que va a regresar,
         sólo los caracteres que no estén en la cadena s_caracteres */
        for (int i = 0; i < s_cadena.length(); i++) {
            valido = true;
            for (int j = 0; j < s_caracteres.length(); j++) {
                caracter = s_caracteres.charAt(j);
                
                if (s_cadena.charAt(i) == caracter) {
                    valido = false;
                    break;
                }
            }
            if (valido) {
                nueva_cadena += s_cadena.charAt(i);
            }
        }
        
        return nueva_cadena;
    }
    
    public void buscar() {
        // sql = "SELECT r.`id_renta`,r.`folio`,CONCAT(c.`nombre`,\" \", c.`apellidos`)AS cliente,e.`descripcion` as estado, r.`fecha_entrega`, r.`hora_entrega`, r.`descripcion`, CONCAT(u.`nombre`,\" \",u.`apellidos`)AS Chofer FROM renta r, estado e, clientes c, usuarios u WHERE r.id_estado=e.id_estado AND r.id_clientes=c.id_clientes AND r.id_usuario_chofer=u.id_usuarios ";
        //id_estado = "2"; //en renta
        String tipoId = null;
        
//        id_tipo = "1"; // pedido

        if (check_cotizacion.isSelected() == true)
            tipoId = ApplicationConstants.TIPO_COTIZACION;
        if(this.check_pedido.isSelected() == true)
            tipoId = ApplicationConstants.TIPO_PEDIDO;
        
        String limit = "";
        try {
            limit = this.cmb_limit.getSelectedItem().toString();
        } catch (Exception e) {
        
        }
        
        if (check_cliente.isSelected() == false && check_estado.isSelected() == false && 
                check_fechas.isSelected() == false && check_chofer.isSelected() == false && 
                check_fechas_evento.isSelected() == false
                && check_cotizacion.isSelected() == false
                && check_pedido.isSelected() == false                
                ) {
            sql = "SELECT r.id_renta, r.folio, c.nombre, c.apellidos, estado.id_estado, estado.descripcion, "
                    + "r.fecha_evento, r.fecha_entrega, r.hora_entrega, r.descripcion, chofer.nombre, chofer.apellidos, "
                    + "tipo.id_tipo, tipo.tipo, r.cantidad_descuento, r.iva, r.deposito_garantia, r.envio_recoleccion ,u.nombre AS nombre_usuario, u.apellidos AS apellidos_usuario "
                    + "FROM renta r "
                    + "INNER JOIN clientes c ON (r.id_clientes = c.id_clientes) "
                    +" INNER JOIN usuarios u ON (u.id_usuarios = r.id_usuarios) "
                    + "INNER JOIN tipo tipo ON(tipo.id_tipo = r.id_tipo) "
                    + "INNER JOIN estado estado ON (estado.id_estado = r.id_estado) "
                    + "INNER JOIN usuarios chofer ON (chofer.id_usuarios = r.id_usuario_chofer) "
                    + "WHERE STR_TO_DATE(r.fecha_entrega, '%d/%m/%Y') >= STR_TO_DATE(' "+ fecha_sistema +" ', '%d/%m/%Y' ) AND r.id_estado<> '"+ApplicationConstants.ESTADO_CANCELADO+"' "
                    + "AND tipo.id_tipo = '"+ApplicationConstants.TIPO_PEDIDO+"' "
                    + "ORDER BY STR_TO_DATE(r.fecha_entrega, '%d/%m/%Y') ";
//            sql = "SELECT r.`id_renta`,r.`folio`,CONCAT(c.`nombre`,\" \", c.`apellidos`)AS cliente,e.`descripcion` as estado, r.fecha_evento,r.`fecha_entrega`, r.`hora_entrega`, r.`descripcion`, CONCAT(u.`nombre`,\" \",u.`apellidos`)AS Chofer FROM renta r, estado e, clientes c, usuarios u WHERE r.id_estado=e.id_estado AND r.id_clientes=c.id_clientes AND r.id_usuario_chofer=u.id_usuarios AND STR_TO_DATE(r.`fecha_entrega`, '%d/%m/%Y') >= STR_TO_DATE('" + fecha_sistema + "', '%d/%m/%Y' ) AND r.`id_tipo`='" + id_tipo + "' AND r.`id_estado`<> '4' ORDER BY r.`folio`";
            /*sql = "SELECT r.`id_renta`,CONCAT(c.`nombre`,\" \", c.`apellidos`)AS cliente,e.`descripcion` as estado, r.`fecha_entrega`, r.`hora_entrega`, r.`descripcion` FROM renta r, estado e, clientes c\n"
             + "WHERE r.id_estado=e.id_estado AND r.id_clientes=c.id_clientes AND STR_TO_DATE(r.`fecha_entrega`, '%d/%m/%Y') >= STR_TO_DATE('" + fecha_sistema + "', '%d/%m/%Y')";
             */
        } else {
//            sql = "SELECT r.`id_renta`,r.`folio`,CONCAT(c.`nombre`,\" \", c.`apellidos`)AS cliente,e.`descripcion` as estado, r.fecha_evento, r.`fecha_entrega`, r.`hora_entrega`, r.`descripcion`, CONCAT(u.`nombre`,\" \",u.`apellidos`)AS Chofer FROM renta r, estado e, clientes c, usuarios u WHERE r.id_estado=e.id_estado AND r.id_clientes=c.id_clientes AND r.id_usuario_chofer=u.id_usuarios ";
           sql = "SELECT r.id_renta, r.folio, c.nombre, c.apellidos, estado.id_estado, estado.descripcion, "
                    + "r.fecha_evento, r.fecha_entrega, r.hora_entrega, r.descripcion, chofer.nombre, chofer.apellidos, "
                    + "tipo.id_tipo, tipo.tipo, r.cantidad_descuento, r.iva, r.deposito_garantia, r.envio_recoleccion,u.nombre AS nombre_usuario, u.apellidos AS apellidos_usuario "
                    + "FROM renta r "
                    + "INNER JOIN clientes c ON (r.id_clientes = c.id_clientes) "
                    +" INNER JOIN usuarios u ON (u.id_usuarios = r.id_usuarios) "
                    + "INNER JOIN tipo tipo ON(tipo.id_tipo = r.id_tipo) "
                    + "INNER JOIN estado estado ON (estado.id_estado = r.id_estado) "
                    + "INNER JOIN usuarios chofer ON (chofer.id_usuarios = r.id_usuario_chofer) "
                       + "";
            if(tipoId != null)
                sql = sql + " AND r.id_tipo = '"+tipoId+"' ";
            if (check_cliente.isSelected() == true)
                sql = sql + " AND CONCAT(c.nombre,\" \",c.apellidos) LIKE '%" + txt_cliente.getText().toString() + "%' ";
            
            if (check_fechas.isSelected() == true) {
                
                fecha_inicial = new SimpleDateFormat("dd/MM/yyyy").format(txt_fecha_inicial.getDate());
                fecha_final = new SimpleDateFormat("dd/MM/yyyy").format(this.txt_fecha_final.getDate());

                /*fecha_inicial = new SimpleDateFormat("dd/MM/yyyy").format(txt_fecha_inicial.getDateFormat());
                 fecha_final = new SimpleDateFormat("dd/MM/yyyy").format(txt_fecha_final.getDateFormat());*/
                System.out.println("Fecha inicial: " + fecha_inicial);
                System.out.println("Fecha Final: " + fecha_final);
                
                sql = sql + " AND STR_TO_DATE(r.fecha_entrega,'%d/%m/%Y') BETWEEN STR_TO_DATE('" + fecha_inicial + "','%d/%m/%Y') AND STR_TO_DATE('" + fecha_final + "','%d/%m/%Y')  ";
            }
            
            if (check_fechas_evento.isSelected() == true) {
                // por fecha de evento
                String fecha_inicial_evento = new SimpleDateFormat("dd/MM/yyyy").format(dateFechaEventoInicial.getDate());
                String fecha_final_evento = new SimpleDateFormat("dd/MM/yyyy").format(dateFechaEventoFinal.getDate());
                sql = sql + " AND STR_TO_DATE(r.fecha_evento,'%d/%m/%Y') BETWEEN STR_TO_DATE('" + fecha_inicial_evento + "','%d/%m/%Y') AND STR_TO_DATE('" + fecha_final_evento + "','%d/%m/%Y')  ";
            }
            if (check_estado.isSelected() == true) {
                // funcion.conectate();
                id_estado = funcion.GetData("id_estado", "Select id_estado from estado where descripcion='" + cmb_estado.getSelectedItem().toString() + "'");
                
                // funcion.desconecta();
                sql = sql + " AND r.id_estado=" + id_estado + "  AND r.id_tipo='" + id_tipo + "' ";
                
            }
            if (check_chofer.isSelected() == true) {
                // funcion.conectate();
                String id_chofer = funcion.GetData("id_usuarios", "Select id_usuarios from usuario where CONCAT(nombre,\" \",apellidos)='" + cmb_chofer_buscar.getSelectedItem().toString() + "'");
                
                // funcion.desconecta();
                sql = sql + " AND r.id_usuario_chofer='" + id_chofer + "'  ";
                
            }
            if(limit.isEmpty() || limit.equals("todos")){
                sql = sql + " ORDER BY STR_TO_DATE(r.fecha_entrega, '%d/%m/%Y') ";
            }else{
                sql = sql + " ORDER BY STR_TO_DATE(r.fecha_entrega, '%d/%m/%Y') LIMIT "+limit;
            }
            
            
        }
        tabla_consultar_renta();
    }
    
    public String dia_semana(String fecha) {
        //String fecha1[];  
        String[] fecha1 = fecha.split("/");
        
        if (fecha1.length != 3) {
            return null;
        }
        //Vector para calcular día de la semana de un año regular.  
        int[] regular = {0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5};
        //Vector para calcular día de la semana de un año bisiesto.  
        int[] bisiesto = {0, 3, 4, 0, 2, 5, 0, 3, 6, 1, 4, 6};
        //Vector para hacer la traducción de resultado en día de la semana.  
        String[] semana = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        //Día especificado en la fecha recibida por parametro.  
        int d = Integer.parseInt(fecha1[0]);
        //Módulo acumulado del mes especificado en la fecha recibida por parametro.  
        int m = Integer.parseInt(fecha1[1]) - 1;
        //Año especificado por la fecha recibida por parametros.  
        int a = Integer.parseInt(fecha1[2]);
        //Comparación para saber si el año recibido es bisiesto.  
        int dia = (int) d;
        int mes = (int) m;
        int anno = (int) a;
        
        if ((anno % 4 == 0) && !(anno % 100 == 0 && anno % 400 != 0)) {
            mes = bisiesto[mes];
        } else {
            mes = regular[mes];
        }
        //Se retorna el resultado del calculo del día de la semana. 
        int dd = (int) Math.ceil(Math.ceil(Math.ceil((anno - 1) % 7) + Math.ceil((Math.floor((anno - 1) / 4) - Math.floor((3 * (Math.floor((anno - 1) / 100) + 1)) / 4)) % 7) + mes + dia % 7) % 7);
        
        String DD = semana[dd].toString();
        String MM = meses[m].toString();
        String fechafinal = DD + " " + d + " de " + MM + " " + a;
        return fechafinal;
    }
    
    public void tabla_consultar_renta() {   // funcion para llenar al abrir la ventana   
        Object[][] data = {{"","","","","","", "", "", "", "", "","","","","","","",""}};
        String[] columNames = {
            "Id", 
            "Folio", 
            "Cliente", 
            "Estado", 
            "Fecha evento",
            "Fecha Entrega", 
//            "Hora Entrega", 
//            "Descripcion", 
//            "Chofer",
            "Tipo",
            "Estado Pagado",
            "Atendió",
            "Subtotal",
            "Dep. Garantía",
            "Envio Rec.",
            "IVA",
            "faltantes",
            "faltantes por cubrir",
            "Pagos",
            "Saldo",
            "SubTotal/Descuentos"
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columNames);
        tabla_prox_rentas.setModel(tableModel);

            TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
            tabla_prox_rentas.setRowSorter(ordenarTabla);
            
            DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
            centrar.setHorizontalAlignment(SwingConstants.CENTER);
            
            DefaultTableCellRenderer right = new DefaultTableCellRenderer();
            right.setHorizontalAlignment(SwingConstants.RIGHT);
            
            int[] anchos = {40,30,120,60,70,70,70,70,70,60,60,60,60,60,60,60,60,70};
            
            for (int inn = 0; inn < tabla_prox_rentas.getColumnCount(); inn++){
                tabla_prox_rentas.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
            }
                      
            tabla_prox_rentas.getColumnModel().getColumn(0).setMaxWidth(0);
            tabla_prox_rentas.getColumnModel().getColumn(0).setMinWidth(0);
            tabla_prox_rentas.getColumnModel().getColumn(0).setPreferredWidth(0);
            
            tabla_prox_rentas.getColumnModel().getColumn(1).setCellRenderer(centrar);
            tabla_prox_rentas.getColumnModel().getColumn(11).setCellRenderer(right);
            tabla_prox_rentas.getColumnModel().getColumn(12).setCellRenderer(right);
            tabla_prox_rentas.getColumnModel().getColumn(13).setCellRenderer(right);
            tabla_prox_rentas.getColumnModel().getColumn(14).setCellRenderer(right);
            tabla_prox_rentas.getColumnModel().getColumn(15).setCellRenderer(right);
            tabla_prox_rentas.getColumnModel().getColumn(16).setCellRenderer(right);
            tabla_prox_rentas.getColumnModel().getColumn(17).setCellRenderer(right);
            
             FormatoTabla ft = new FormatoTabla(3);
            tabla_prox_rentas.setDefaultRenderer(Object.class, ft);
            
        
            /* tabla_prox_rentas.getColumnModel().getColumn(2).setCellRenderer(centrar);
             tabla_prox_rentas.getColumnModel().getColumn(3).setCellRenderer(centrar);
             tabla_prox_rentas.getColumnModel().getColumn(4).setCellRenderer(centrar);
             tabla_prox_rentas.getColumnModel().getColumn(0).setCellRenderer(centrar);*/

            //tabla_prox_rentas.setDefaultRenderer(Object.class, new TableCellRendererColor());
            // funcion.desconecta();
            jPanel2.setVisible(true);
            //lbl_aviso_resultados.setVisible(false);

//        } else {
//            
//            jPanel2.setVisible(false);
//            lbl_aviso_resultados.setVisible(true);
//            lbl_aviso_resultados.setText("< No hay resultados para mostrar >");
//        }
        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_prox_rentas.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }

        this.lblInformation.setText("Obteniendo resultados, porfavor espere ... "); 
        this.disableButtonsActions();
        new Thread(() -> {
            List<Renta> rentas = saleService.obtenerPedidosPorConsultaSqlSinDetalle(sql, funcion);
            fillTable(rentas);
            enabledButtonsActions();
        }).start();
        
    }
    
    
    private void fillTable (List<Renta> rentas) {
        if(rentas == null || rentas.size()<=0)
    {
//        JOptionPane.showMessageDialog(null, "no se obtuvieron resultados :( ", "Error", JOptionPane.ERROR_MESSAGE);
        this.lblInformation.setText("No se obtuvieron resultados :( ");
        return;
    }else{
        Toolkit.getDefaultToolkit().beep();
        String limit = this.cmb_limit.getSelectedItem().toString();
        
        if(limit.equals("todos") || limit.toLowerCase().equals("item 1")){
            if(rentas.size()>1){
                this.lblInformation.setText("Se han obtenido "+rentas.size()+" resultados");
            }else{
                this.lblInformation.setText("Se a obtenido "+rentas.size()+" resultado");
            }
            
//            JOptionPane.showMessageDialog(null, "Se han obtenido "+rentas.size()+" resultados", "Resultados", JOptionPane.INFORMATION_MESSAGE);
        }else{
            if(rentas.size()>1){
               this.lblInformation.setText("Se han obtenido "+rentas.size()+" resultados, con un limite de "+limit+" registros");  
            }else{
              this.lblInformation.setText("Se a obtenido "+rentas.size()+" resultado, con un limite de "+limit+" registros");
            }
            
//            JOptionPane.showMessageDialog(null, "Se han obtenido "+rentas.size()+" resultados, con un limite de "+limit+" registros", "Resultados", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }
        
        DefaultTableModel tableModel = (DefaultTableModel) tabla_prox_rentas.getModel();
        for(Renta renta : rentas){
            float abonos = 0f;
            if(renta.getAbonos() != null && renta.getAbonos().size()>0){
                for(Abono abono : renta.getAbonos()){
                    abonos += abono.getAbono();
                }
            }
                 Object fila[] = {
                    renta.getRentaId()+"",
                    renta.getFolio()+"",
                    renta.getCliente().getNombre()+" "+renta.getCliente().getApellidos(),              
                    renta.getEstado().getDescripcion(),
                    renta.getFechaEvento(),
                    renta.getFechaEntrega(),
//                    renta.getHoraEntrega(),
//                    renta.getDescripcion(),
//                    renta.getChofer().getNombre()+" "+renta.getChofer().getApellidos(),
                    renta.getTipo().getTipo(),
                    renta.getDescripcionCobranza(),
                    renta.getUsuario().getNombre()+" "+renta.getUsuario().getApellidos(),
                    decimalFormat.format(renta.getSubTotal()),
                    decimalFormat.format(renta.getDepositoGarantia()),
                    decimalFormat.format(renta.getEnvioRecoleccion()),
                    decimalFormat.format(renta.getIva()),
                    decimalFormat.format(renta.getTotalFaltantes()),
                    decimalFormat.format(renta.getTotalFaltantesPorCubrir()),
                    decimalFormat.format(abonos),
                    decimalFormat.format(renta.getTotal()),
                    decimalFormat.format(renta.getSubTotal() - renta.getCantidadDescuento())
                         
                };
                tableModel.addRow(fila);
        }
    }
    
    public void tabla_articulos() {
        Object[][] data = {{"", "", "", "", "", "", "",""}};       
        String[] columNames = {"Id", "Categoria", "Descripcion", "Color", "P.Unitario", "Stock"};
 
        DefaultTableModel tableModel = new DefaultTableModel(data, columNames);
        tabla_articulos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla_articulos.setModel(tableModel);     
        
    
        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        int[] anchos = {10, 120, 250, 100, 90, 90};
        
        for (int inn = 0; inn < tabla_articulos.getColumnCount(); inn++) {
            tabla_articulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        
        tabla_articulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_articulos.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_articulos.getColumnModel().getColumn(0).setPreferredWidth(0);      
        tabla_articulos.getColumnModel().getColumn(5).setCellRenderer(centrar);
        tabla_articulos.getColumnModel().getColumn(4).setCellRenderer(centrar);
    }
    
    public void tabla_articulos_like() {
        // funcion.conectate();
        tabla_articulos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Categoria", "Descripcion", "Color", "P.Unitario", "Stock"};
        String[] colName = {"id_articulo", "categoria", "descripcion", "color", "precio_renta", "cantidad"};
        //nombre de columnas, tabla, instruccion sql   
        try {       
            dtconduc = funcion.GetTabla(colName, "articulo", "SELECT a.`id_articulo`, ca.`descripcion` as categoria, a.`descripcion`, c.`color`, a.`precio_renta`,a.`cantidad` FROM articulo a, color c, categoria ca\n"
                + "WHERE a.id_color=c.id_color and activo =1 AND a.id_categoria=ca.id_categoria AND a.`descripcion` like '%" + txt_buscar.getText().toString() + "%' ");
        
        } catch (SQLNonTransientConnectionException e) {
            funcion.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        
        for (int i = 0; i < dtconduc.length; i++) {
            String valor = dtconduc[i][4].toString();
            dtconduc[i][4] = conviertemoneda(valor).toString();
            System.out.println(conviertemoneda(valor));
            
        }
        
        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        tabla_articulos.setModel(datos);
        
        int[] anchos = {10, 120, 250, 100, 90, 90};
        
        for (int inn = 0; inn < tabla_articulos.getColumnCount(); inn++) {
            tabla_articulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        
        tabla_articulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_articulos.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_articulos.getColumnModel().getColumn(0).setPreferredWidth(0);

        //tabla_articulos.getColumnModel().getColumn(4).setCellRenderer(TablaRenderer);
        tabla_articulos.getColumnModel().getColumn(5).setCellRenderer(centrar);
        tabla_articulos.getColumnModel().getColumn(4).setCellRenderer(centrar);
        
        // funcion.desconecta();
    }
    
    public void tabla_abonos(String rentaId) {
        String fecha, fecha2;
        // funcion.conectate();
        tabla_abonos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id", "Usuario", "Fecha", "Abono", "Comentario","Tipo pago","id_tipo_abono","fecha_pago"};
        String[] colName = {"id_abonos", "usuario", "fecha","abono","comentario","tipo_abono","id_tipo_abono","fecha_pago"};
        //nombre de columnas, tabla, instruccion sql        
        try {       
        dtconduc = funcion.GetTabla(colName, "abonos", 
                "SELECT a.id_abonos, CONCAT(u.nombre,\" \",u.apellidos)AS usuario, a.fecha, "
                + "a.abono, a.comentario,tipo.descripcion AS tipo_abono,a.id_tipo_abono,a.fecha_pago "
                + "FROM abonos a "
                + "INNER JOIN usuarios u ON (u.id_usuarios = a.id_usuario) "
                + "INNER JOIN tipo_abono tipo ON (tipo.id_tipo_abono = a.id_tipo_abono) "
                + "WHERE a.id_renta='" + rentaId + "' "
                + "ORDER BY a.id_abonos DESC ");
        } catch (SQLNonTransientConnectionException e) {
            funcion.conectate();
            JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        
        
        for (int i = 0; i < dtconduc.length; i++) {
            String valor = dtconduc[i][3].toString();
            dtconduc[i][3] = conviertemoneda(valor).toString();
            
        }
        for (int i = 0; i < dtconduc.length; i++) {
            fecha = dtconduc[i][2].toString();
            fecha2 = dia_semana(fecha);
            dtconduc[i][2] = fecha2;
        }
        
                
        DefaultTableModel datos = new DefaultTableModel(dtconduc, columNames);
        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        tabla_abonos.setModel(datos);
        
        int[] anchos = {10, 100, 160, 80, 160,160,10,90};
        
        for (int inn = 0; inn < tabla_abonos.getColumnCount(); inn++) {
            tabla_abonos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        
        tabla_abonos.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_abonos.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_abonos.getColumnModel().getColumn(0).setPreferredWidth(0);
        
         tabla_abonos.getColumnModel().getColumn(6).setMaxWidth(0);
        tabla_abonos.getColumnModel().getColumn(6).setMinWidth(0);
        tabla_abonos.getColumnModel().getColumn(6).setPreferredWidth(0);
        
        tabla_abonos.getColumnModel().getColumn(3).setCellRenderer(TablaRenderer);
        
        // funcion.desconecta();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_prox_rentas = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        lblInformation = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txt_cliente = new javax.swing.JTextField();
        cmb_estado = new javax.swing.JComboBox();
        check_cliente = new javax.swing.JCheckBox();
        check_fechas = new javax.swing.JCheckBox();
        check_estado = new javax.swing.JCheckBox();
        jToolBar1 = new javax.swing.JToolBar();
        jbtn_buscar = new javax.swing.JButton();
        jbtn_refrescar = new javax.swing.JButton();
        jbtn_generar_reporte1 = new javax.swing.JButton();
        jbtnGenerarReporteEntregas = new javax.swing.JButton();
        jtbtnGenerateExcel = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        txt_buscar_folio = new javax.swing.JFormattedTextField();
        check_chofer = new javax.swing.JCheckBox();
        cmb_chofer_buscar = new javax.swing.JComboBox();
        check_pedido = new javax.swing.JCheckBox();
        check_cotizacion = new javax.swing.JCheckBox();
        txt_fecha_inicial = new com.toedter.calendar.JDateChooser();
        txt_fecha_final = new com.toedter.calendar.JDateChooser();
        check_fechas_evento = new javax.swing.JCheckBox();
        dateFechaEventoInicial = new com.toedter.calendar.JDateChooser();
        dateFechaEventoFinal = new com.toedter.calendar.JDateChooser();
        cmbUsuarios = new javax.swing.JComboBox<>();
        cmb_limit = new javax.swing.JComboBox();
        jLabel49 = new javax.swing.JLabel();
        lbl_aviso_resultados = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        panel_datos_generales = new javax.swing.JPanel();
        lbl_cliente = new javax.swing.JLabel();
        lbl_atiende = new javax.swing.JLabel();
        cmb_fecha_entrega = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        cmb_fecha_devolucion = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cmb_estado1 = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        txt_descripcion = new javax.swing.JTextPane();
        cmb_chofer = new javax.swing.JComboBox();
        jLabel31 = new javax.swing.JLabel();
        lbl_folio = new javax.swing.JLabel();
        cmb_tipo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        cmb_hora_devolucion = new javax.swing.JComboBox();
        cmb_hora = new javax.swing.JComboBox();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        cmb_hora_devolucion_dos = new javax.swing.JComboBox();
        cmb_hora_dos = new javax.swing.JComboBox();
        jLabel33 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        cmb_fecha_evento = new com.toedter.calendar.JDateChooser();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txt_total = new javax.swing.JFormattedTextField();
        txt_abonos = new javax.swing.JFormattedTextField();
        txt_calculo = new javax.swing.JFormattedTextField();
        txt_total_iva = new javax.swing.JFormattedTextField();
        txt_iva = new javax.swing.JFormattedTextField();
        txt_depositoGarantia = new javax.swing.JFormattedTextField();
        txt_envioRecoleccion = new javax.swing.JFormattedTextField();
        txt_descuento = new javax.swing.JFormattedTextField();
        txt_subtotal = new javax.swing.JFormattedTextField();
        txtPorcentajeDescuento = new javax.swing.JFormattedTextField();
        check_mostrar_precios = new javax.swing.JCheckBox();
        jLabel48 = new javax.swing.JLabel();
        txt_faltantes = new javax.swing.JFormattedTextField();
        check_enviar_email = new javax.swing.JCheckBox();
        txtEmailToSend = new javax.swing.JTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        panel_articulos = new javax.swing.JPanel();
        txt_cantidad = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txt_precio_unitario = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txt_buscar = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        lbl_eleccion = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabla_articulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jLabel39 = new javax.swing.JLabel();
        txt_porcentaje_descuento = new javax.swing.JTextField();
        panel_conceptos = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabla_detalle = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        lbl_sel = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        txt_editar_cantidad = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        txt_editar_precio_unitario = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        txt_editar_porcentaje_descuento = new javax.swing.JTextField();
        jToolBar3 = new javax.swing.JToolBar();
        jbtn_agregar_articulo = new javax.swing.JButton();
        jbtn_editar_dinero = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jbtn_disponible = new javax.swing.JButton();
        jbtn_mostrar_articulos = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jBtnAddOrderProvider = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        panel_abonos = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabla_abonos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel10 = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        jbtn_agregar_abono = new javax.swing.JButton();
        jbtn_quitar_abono = new javax.swing.JButton();
        jbtn_editar_abonos = new javax.swing.JButton();
        jbtn_guardar_abonos = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        txt_comentario = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txt_abono = new javax.swing.JTextField();
        cmbTipoPago = new javax.swing.JComboBox();
        jLabel46 = new javax.swing.JLabel();
        cmb_fecha_pago = new com.toedter.calendar.JDateChooser();
        jLabel47 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        txt_comentarios = new javax.swing.JTextPane();
        jToolBar5 = new javax.swing.JToolBar();
        jbtn_editar = new javax.swing.JButton();
        jbtn_guardar = new javax.swing.JButton();
        jbtn_agregar_articulos = new javax.swing.JButton();
        jbtn_generar_reporte = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jbtn_nuevo_cliente = new javax.swing.JButton();
        jbtn_agregar_cliente = new javax.swing.JButton();
        jbtn_guardar_cliente = new javax.swing.JButton();
        jbtn_editar_cliente = new javax.swing.JButton();
        panel_datos_cliente = new javax.swing.JPanel();
        txt_nombre = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txt_apellidos = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_apodo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt_tel_movil = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txt_tel_casa = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txt_email = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txt_direccion = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        txt_localidad = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txt_rfc = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_clientes = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        txt_buscar1 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        check_nombre = new javax.swing.JCheckBox();
        check_apellidos = new javax.swing.JCheckBox();
        check_apodo = new javax.swing.JCheckBox();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("CONSULTAR RENTA O PEDIDO....");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jTabbedPane1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Proximas rentas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabla_prox_rentas.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        tabla_prox_rentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabla_prox_rentas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabla_prox_rentas.setRowHeight(14);
        tabla_prox_rentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_prox_rentasMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabla_prox_rentas);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 1150, 450));

        lblInformation.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jPanel2.add(lblInformation, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 370, 20));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 1190, 500));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Parametros"));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_cliente.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jPanel3.add(txt_cliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, 181, 20));

        cmb_estado.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_estado.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_estado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(cmb_estado, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 20, 140, -1));

        check_cliente.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        check_cliente.setText("Cliente:");
        check_cliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(check_cliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 70, -1));

        check_fechas.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        check_fechas.setText("Por fecha entrega: ");
        check_fechas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        check_fechas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_fechasActionPerformed(evt);
            }
        });
        jPanel3.add(check_fechas, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 150, 20));

        check_estado.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        check_estado.setText("Estado:");
        check_estado.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(check_estado, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 80, -1));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jbtn_buscar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search-icon_32.png"))); // NOI18N
        jbtn_buscar.setMnemonic('B');
        jbtn_buscar.setToolTipText("Realizar busqueda (Alt+B)");
        jbtn_buscar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_buscar.setFocusable(false);
        jbtn_buscar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_buscar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_buscarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_buscar);

        jbtn_refrescar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_refrescar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/arrow-refresh-3-icon_32.png"))); // NOI18N
        jbtn_refrescar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_refrescar.setFocusable(false);
        jbtn_refrescar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_refrescar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_refrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_refrescarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_refrescar);

        jbtn_generar_reporte1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/blank-catalog-icon.png"))); // NOI18N
        jbtn_generar_reporte1.setMnemonic('R');
        jbtn_generar_reporte1.setToolTipText("Generar reporte (Alt+R)");
        jbtn_generar_reporte1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_generar_reporte1.setFocusable(false);
        jbtn_generar_reporte1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_generar_reporte1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_generar_reporte1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbtn_generar_reporte1MouseClicked(evt);
            }
        });
        jbtn_generar_reporte1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_generar_reporte1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_generar_reporte1);

        jbtnGenerarReporteEntregas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/truck.png"))); // NOI18N
        jbtnGenerarReporteEntregas.setToolTipText("reporte para entregas");
        jbtnGenerarReporteEntregas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnGenerarReporteEntregas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnGenerarReporteEntregasActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtnGenerarReporteEntregas);

        jtbtnGenerateExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/excel-icon.png"))); // NOI18N
        jtbtnGenerateExcel.setToolTipText("Exportar a Excel");
        jtbtnGenerateExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtbtnGenerateExcel.setFocusable(false);
        jtbtnGenerateExcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jtbtnGenerateExcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jtbtnGenerateExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbtnGenerateExcelActionPerformed(evt);
            }
        });
        jToolBar1.add(jtbtnGenerateExcel);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/archive-icon.png"))); // NOI18N
        jButton2.setToolTipText("Reporte articulos por categoria");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/faltantes_32x.png"))); // NOI18N
        jButton6.setToolTipText("Ver faltantes");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton6);

        jPanel3.add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 20, 310, -1));

        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Limitar los resultados a:");
        jPanel3.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 60, 140, 20));

        txt_buscar_folio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        txt_buscar_folio.setToolTipText("Busca por el folio del pedido (Enter)");
        txt_buscar_folio.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_buscar_folio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_buscar_folioKeyPressed(evt);
            }
        });
        jPanel3.add(txt_buscar_folio, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 80, 54, -1));

        check_chofer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        check_chofer.setText("Chofer:");
        check_chofer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(check_chofer, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 80, -1));

        cmb_chofer_buscar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_chofer_buscar.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_chofer_buscar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(cmb_chofer_buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, 150, -1));

        buttonGroup1.add(check_pedido);
        check_pedido.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        check_pedido.setText("Pedido");
        check_pedido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(check_pedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 50, -1, -1));

        buttonGroup1.add(check_cotizacion);
        check_cotizacion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        check_cotizacion.setText("Cotizacion");
        check_cotizacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(check_cotizacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 50, -1, -1));

        txt_fecha_inicial.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_fecha_inicial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txt_fecha_inicialMouseClicked(evt);
            }
        });
        txt_fecha_inicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_fecha_inicialKeyPressed(evt);
            }
        });
        jPanel3.add(txt_fecha_inicial, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 160, 21));

        txt_fecha_final.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_fecha_final.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txt_fecha_finalMouseClicked(evt);
            }
        });
        txt_fecha_final.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_fecha_finalKeyPressed(evt);
            }
        });
        jPanel3.add(txt_fecha_final, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, 170, 21));

        check_fechas_evento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        check_fechas_evento.setText("Por fecha evento: ");
        check_fechas_evento.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        check_fechas_evento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_fechas_eventoActionPerformed(evt);
            }
        });
        jPanel3.add(check_fechas_evento, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 150, -1));

        dateFechaEventoInicial.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        dateFechaEventoInicial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dateFechaEventoInicialMouseClicked(evt);
            }
        });
        dateFechaEventoInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dateFechaEventoInicialKeyPressed(evt);
            }
        });
        jPanel3.add(dateFechaEventoInicial, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, 160, 21));

        dateFechaEventoFinal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        dateFechaEventoFinal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dateFechaEventoFinalMouseClicked(evt);
            }
        });
        dateFechaEventoFinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dateFechaEventoFinalKeyPressed(evt);
            }
        });
        jPanel3.add(dateFechaEventoFinal, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 170, 21));

        cmbUsuarios.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbUsuarios.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbUsuarios.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(cmbUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 70, 170, -1));

        cmb_limit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_limit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_limit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanel3.add(cmb_limit, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 80, 140, -1));

        jLabel49.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel49.setText("Buscar por folio:");
        jPanel3.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 80, 100, 20));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 1180, 120));

        lbl_aviso_resultados.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jPanel1.add(lbl_aviso_resultados, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 160, 510, 30));

        jTabbedPane1.addTab("General", jPanel1);

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel_datos_generales.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos generales", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N
        panel_datos_generales.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_cliente.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 14)); // NOI18N
        panel_datos_generales.add(lbl_cliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 10, 210, 20));

        lbl_atiende.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12)); // NOI18N
        panel_datos_generales.add(lbl_atiende, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 20, 180, 20));

        cmb_fecha_entrega.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_fecha_entrega.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmb_fecha_entregaMouseClicked(evt);
            }
        });
        cmb_fecha_entrega.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmb_fecha_entregaKeyPressed(evt);
            }
        });
        panel_datos_generales.add(cmb_fecha_entrega, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 210, 21));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Fecha de entrega:");
        panel_datos_generales.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 110, 20));

        cmb_fecha_devolucion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(cmb_fecha_devolucion, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 210, 21));

        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Fecha de devolución:");
        panel_datos_generales.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 110, 20));

        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Dirección del evento:");
        panel_datos_generales.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 40, 340, 10));

        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Estado:");
        panel_datos_generales.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 140, 50, 20));

        cmb_estado1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_estado1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_estado1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panel_datos_generales.add(cmb_estado1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 140, 160, -1));

        txt_descripcion.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jScrollPane3.setViewportView(txt_descripcion);

        panel_datos_generales.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 60, 250, 70));

        cmb_chofer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_chofer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_chofer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panel_datos_generales.add(cmb_chofer, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 210, -1));

        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Chofer:");
        panel_datos_generales.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 110, 20));

        lbl_folio.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 18)); // NOI18N
        panel_datos_generales.add(lbl_folio, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 80, 20));

        cmb_tipo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_tipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_tipo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panel_datos_generales.add(cmb_tipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 170, 160, -1));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Tipo:");
        panel_datos_generales.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 170, 50, 20));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Hora entrega:");
        panel_datos_generales.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 90, 20));

        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Hora devolución:");
        panel_datos_generales.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 110, 20));

        cmb_hora_devolucion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_hora_devolucion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-sel-", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
        cmb_hora_devolucion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panel_datos_generales.add(cmb_hora_devolucion, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 170, 60, -1));

        cmb_hora.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_hora.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-sel-", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
        cmb_hora.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panel_datos_generales.add(cmb_hora, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, 60, -1));

        jLabel35.setText("a");
        panel_datos_generales.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, 20, 30));

        jLabel36.setText("a");
        panel_datos_generales.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 170, 20, 30));

        cmb_hora_devolucion_dos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_hora_devolucion_dos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-sel-", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
        cmb_hora_devolucion_dos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panel_datos_generales.add(cmb_hora_devolucion_dos, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 170, -1, -1));

        cmb_hora_dos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_hora_dos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-sel-", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
        cmb_hora_dos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panel_datos_generales.add(cmb_hora_dos, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 140, -1, -1));

        jLabel33.setText("Hrs.");
        panel_datos_generales.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 140, -1, -1));

        jLabel37.setText("Hrs.");
        panel_datos_generales.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 170, -1, -1));

        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("Fecha del evento:");
        panel_datos_generales.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 110, 20));

        cmb_fecha_evento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(cmb_fecha_evento, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, 210, 21));

        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Subtotal:");
        panel_datos_generales.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 20, 69, 20));

        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Descuento % :");
        panel_datos_generales.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 40, 100, 20));

        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Envío y recolección");
        panel_datos_generales.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 60, 110, 20));

        jLabel42.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel42.setText("Depósito en garantía");
        panel_datos_generales.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 80, 110, 20));

        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("IVA % :");
        panel_datos_generales.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 100, 69, 20));

        jLabel43.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel43.setText("Calculo:");
        panel_datos_generales.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 120, 69, 20));

        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Pagos:");
        panel_datos_generales.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 140, 69, 20));

        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Total:");
        panel_datos_generales.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 180, 69, 20));

        txt_total.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_total.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_total.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_totalActionPerformed(evt);
            }
        });
        panel_datos_generales.add(txt_total, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 180, 120, -1));

        txt_abonos.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_abonos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_abonos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(txt_abonos, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 140, 120, -1));

        txt_calculo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_calculo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_calculo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_calculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_calculoActionPerformed(evt);
            }
        });
        panel_datos_generales.add(txt_calculo, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 120, 120, -1));

        txt_total_iva.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_total_iva.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_total_iva.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(txt_total_iva, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 100, 70, -1));

        txt_iva.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_iva.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_iva.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_iva.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_ivaFocusLost(evt);
            }
        });
        txt_iva.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_ivaKeyPressed(evt);
            }
        });
        panel_datos_generales.add(txt_iva, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 100, 40, -1));

        txt_depositoGarantia.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_depositoGarantia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_depositoGarantia.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_depositoGarantia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_depositoGarantiaActionPerformed(evt);
            }
        });
        txt_depositoGarantia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_depositoGarantiaKeyPressed(evt);
            }
        });
        panel_datos_generales.add(txt_depositoGarantia, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 80, 120, -1));

        txt_envioRecoleccion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_envioRecoleccion.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_envioRecoleccion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_envioRecoleccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_envioRecoleccionActionPerformed(evt);
            }
        });
        txt_envioRecoleccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_envioRecoleccionKeyPressed(evt);
            }
        });
        panel_datos_generales.add(txt_envioRecoleccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 60, 120, -1));

        txt_descuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_descuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_descuento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_descuento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_descuentoFocusLost(evt);
            }
        });
        txt_descuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_descuentoKeyPressed(evt);
            }
        });
        panel_datos_generales.add(txt_descuento, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 40, 70, -1));

        txt_subtotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_subtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_subtotal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(txt_subtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 20, 120, -1));

        txtPorcentajeDescuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtPorcentajeDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPorcentajeDescuento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtPorcentajeDescuento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPorcentajeDescuentoFocusLost(evt);
            }
        });
        txtPorcentajeDescuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPorcentajeDescuentoKeyPressed(evt);
            }
        });
        panel_datos_generales.add(txtPorcentajeDescuento, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 40, 40, -1));

        check_mostrar_precios.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        check_mostrar_precios.setText("Mostrar precios en PDF");
        check_mostrar_precios.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panel_datos_generales.add(check_mostrar_precios, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 50, 170, 30));

        jLabel48.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel48.setText("Faltantes:");
        panel_datos_generales.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 160, 69, 20));

        txt_faltantes.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_faltantes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_faltantes.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_faltantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_faltantesActionPerformed(evt);
            }
        });
        panel_datos_generales.add(txt_faltantes, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 160, 120, -1));

        check_enviar_email.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        check_enviar_email.setText("Enviar email confirmación");
        check_enviar_email.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        check_enviar_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_enviar_emailActionPerformed(evt);
            }
        });
        panel_datos_generales.add(check_enviar_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 80, 180, -1));

        txtEmailToSend.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txtEmailToSend.setToolTipText("Para enviar multiples correos deberas separarlos por punto y coma [;]");
        txtEmailToSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailToSendActionPerformed(evt);
            }
        });
        panel_datos_generales.add(txtEmailToSend, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 140, 240, -1));

        jPanel4.add(panel_datos_generales, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 1120, 210));

        jTabbedPane2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jTabbedPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane2MouseClicked(evt);
            }
        });

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel_articulos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Elije un servicio", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N
        panel_articulos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_cantidad.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_cantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cantidadActionPerformed(evt);
            }
        });
        txt_cantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_cantidadKeyPressed(evt);
            }
        });
        panel_articulos.add(txt_cantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 50, 60, -1));

        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Cantidad:");
        panel_articulos.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 50, -1, 20));

        txt_precio_unitario.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_precio_unitario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_precio_unitarioKeyPressed(evt);
            }
        });
        panel_articulos.add(txt_precio_unitario, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 50, 70, -1));

        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Precio:");
        panel_articulos.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 50, -1, 20));

        txt_buscar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_buscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_buscarKeyReleased(evt);
            }
        });
        panel_articulos.add(txt_buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 17, 247, -1));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search-icon.png"))); // NOI18N
        panel_articulos.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 17, -1, -1));

        lbl_eleccion.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12)); // NOI18N
        panel_articulos.add(lbl_eleccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 220, 21));

        jLabel23.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel23.setText("Eleccion:");
        panel_articulos.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 45, -1, 30));

        tabla_articulos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabla_articulos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabla_articulos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabla_articulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_articulosMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabla_articulos);

        panel_articulos.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 77, 1000, 260));

        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("Descuento %:");
        panel_articulos.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 50, 70, 20));

        txt_porcentaje_descuento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_porcentaje_descuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_porcentaje_descuentoKeyPressed(evt);
            }
        });
        panel_articulos.add(txt_porcentaje_descuento, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 50, 70, -1));

        jPanel6.add(panel_articulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 11, 1080, 350));

        panel_conceptos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Conceptos del evento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N
        panel_conceptos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabla_detalle.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabla_detalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabla_detalle.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabla_detalle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_detalleMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tabla_detalle);

        panel_conceptos.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 1040, 230));

        lbl_sel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        panel_conceptos.add(lbl_sel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 250, 20));

        jLabel40.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel40.setText("Cantidad:");
        panel_conceptos.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 20, -1, 20));

        txt_editar_cantidad.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_editar_cantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_editar_cantidadActionPerformed(evt);
            }
        });
        txt_editar_cantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_editar_cantidadKeyPressed(evt);
            }
        });
        panel_conceptos.add(txt_editar_cantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 20, 60, -1));

        jLabel44.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel44.setText("Precio:");
        panel_conceptos.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 20, -1, 20));

        txt_editar_precio_unitario.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_editar_precio_unitario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_editar_precio_unitarioKeyPressed(evt);
            }
        });
        panel_conceptos.add(txt_editar_precio_unitario, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 20, 70, -1));

        jLabel45.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel45.setText("Descuento %:");
        panel_conceptos.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 20, 70, 20));

        txt_editar_porcentaje_descuento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_editar_porcentaje_descuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_editar_porcentaje_descuentoKeyPressed(evt);
            }
        });
        panel_conceptos.add(txt_editar_porcentaje_descuento, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 20, 70, -1));

        jPanel6.add(panel_conceptos, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 1080, 310));

        jToolBar3.setFloatable(false);
        jToolBar3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar3.setRollover(true);

        jbtn_agregar_articulo.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_agregar_articulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/shop-cart-down-icon_32.png"))); // NOI18N
        jbtn_agregar_articulo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_agregar_articulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_agregar_articuloActionPerformed(evt);
            }
        });
        jToolBar3.add(jbtn_agregar_articulo);

        jbtn_editar_dinero.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_editar_dinero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar_dinero_32.png"))); // NOI18N
        jbtn_editar_dinero.setToolTipText("Editar precio");
        jbtn_editar_dinero.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_editar_dinero.setFocusable(false);
        jbtn_editar_dinero.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_editar_dinero.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_editar_dinero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_editar_dineroActionPerformed(evt);
            }
        });
        jToolBar3.add(jbtn_editar_dinero);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Actions-edit-icon.png"))); // NOI18N
        jButton4.setToolTipText("Editar datos articulo");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar3.add(jButton4);

        jButton3.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/folder-remove-icon.png"))); // NOI18N
        jButton3.setToolTipText("Quitar elemento");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar3.add(jButton3);

        jbtn_disponible.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/application-check-icon_32.png"))); // NOI18N
        jbtn_disponible.setToolTipText("Revisa la disponibilidad en inventario para la fecha indicada");
        jbtn_disponible.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_disponible.setFocusable(false);
        jbtn_disponible.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_disponible.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_disponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_disponibleActionPerformed(evt);
            }
        });
        jToolBar3.add(jbtn_disponible);

        jbtn_mostrar_articulos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Left-align-icon.png"))); // NOI18N
        jbtn_mostrar_articulos.setMnemonic('X');
        jbtn_mostrar_articulos.setToolTipText("Cambiar panel (Alt+X)");
        jbtn_mostrar_articulos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_mostrar_articulos.setFocusable(false);
        jbtn_mostrar_articulos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_mostrar_articulos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_mostrar_articulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_mostrar_articulosActionPerformed(evt);
            }
        });
        jToolBar3.add(jbtn_mostrar_articulos);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/faltantes_32x.png"))); // NOI18N
        jButton5.setToolTipText("Ver faltantes");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar3.add(jButton5);

        jBtnAddOrderProvider.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/customer-service-icon-32.png"))); // NOI18N
        jBtnAddOrderProvider.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jBtnAddOrderProvider.setFocusable(false);
        jBtnAddOrderProvider.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBtnAddOrderProvider.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBtnAddOrderProvider.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAddOrderProviderActionPerformed(evt);
            }
        });
        jToolBar3.add(jBtnAddOrderProvider);

        jPanel6.add(jToolBar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 39, 350));

        jTabbedPane2.addTab("Detalle conceptos", jPanel6);

        panel_abonos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pagos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        tabla_abonos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        tabla_abonos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabla_abonos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabla_abonos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_abonosMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tabla_abonos);

        javax.swing.GroupLayout panel_abonosLayout = new javax.swing.GroupLayout(panel_abonos);
        panel_abonos.setLayout(panel_abonosLayout);
        panel_abonosLayout.setHorizontalGroup(
            panel_abonosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_abonosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
                .addContainerGap())
        );
        panel_abonosLayout.setVerticalGroup(
            panel_abonosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_abonosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(149, 149, 149))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ingresa el abono", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        jToolBar4.setFloatable(false);
        jToolBar4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar4.setRollover(true);

        jbtn_agregar_abono.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_agregar_abono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Actions-arrow-right-icon.png"))); // NOI18N
        jbtn_agregar_abono.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_agregar_abono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_agregar_abonoActionPerformed(evt);
            }
        });
        jToolBar4.add(jbtn_agregar_abono);

        jbtn_quitar_abono.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_quitar_abono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/folder-remove-icon.png"))); // NOI18N
        jbtn_quitar_abono.setToolTipText("Quitar elemento");
        jbtn_quitar_abono.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_quitar_abono.setFocusable(false);
        jbtn_quitar_abono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_quitar_abono.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_quitar_abono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_quitar_abonoActionPerformed(evt);
            }
        });
        jToolBar4.add(jbtn_quitar_abono);

        jbtn_editar_abonos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar_32.png"))); // NOI18N
        jbtn_editar_abonos.setToolTipText("Editar abonos");
        jbtn_editar_abonos.setFocusable(false);
        jbtn_editar_abonos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_editar_abonos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_editar_abonos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbtn_editar_abonosMouseClicked(evt);
            }
        });
        jbtn_editar_abonos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_editar_abonosActionPerformed(evt);
            }
        });
        jToolBar4.add(jbtn_editar_abonos);

        jbtn_guardar_abonos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/floppy-disk-icon.png"))); // NOI18N
        jbtn_guardar_abonos.setToolTipText("guardar_abonos");
        jbtn_guardar_abonos.setFocusable(false);
        jbtn_guardar_abonos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_guardar_abonos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_guardar_abonos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_guardar_abonosActionPerformed(evt);
            }
        });
        jToolBar4.add(jbtn_guardar_abonos);

        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Pago");

        txt_comentario.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Comentario:");

        txt_abono.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_abono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_abonoKeyPressed(evt);
            }
        });

        cmbTipoPago.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbTipoPago.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTipoPago.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel46.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel46.setText("Fecha pago:");

        cmb_fecha_pago.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel47.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel47.setText("Tipo de pago:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel25)
                        .addComponent(jLabel46)
                        .addComponent(cmbTipoPago, 0, 210, Short.MAX_VALUE)
                        .addComponent(txt_comentario)
                        .addComponent(txt_abono))
                    .addComponent(cmb_fecha_pago, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 102, Short.MAX_VALUE)
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_abono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_comentario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmb_fecha_pago, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_abonos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel_abonos, javax.swing.GroupLayout.PREFERRED_SIZE, 315, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(73, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Pagos", jPanel8);

        txt_comentarios.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jScrollPane7.setViewportView(txt_comentarios);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 661, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(511, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Comentarios", jPanel11);

        jPanel4.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 220, 1180, 400));

        jToolBar5.setFloatable(false);
        jToolBar5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar5.setRollover(true);
        jPanel4.add(jToolBar5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1360, 10, -1, 210));

        jbtn_editar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar_32.png"))); // NOI18N
        jbtn_editar.setMnemonic('E');
        jbtn_editar.setToolTipText("Editar (Alt+E)");
        jbtn_editar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_editar.setFocusable(false);
        jbtn_editar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_editar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_editarActionPerformed(evt);
            }
        });
        jPanel4.add(jbtn_editar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 20, 40, 40));

        jbtn_guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/floppy-disk-icon.png"))); // NOI18N
        jbtn_guardar.setMnemonic('G');
        jbtn_guardar.setToolTipText("Guardar (Alt+G)");
        jbtn_guardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_guardar.setFocusable(false);
        jbtn_guardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_guardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_guardarActionPerformed(evt);
            }
        });
        jPanel4.add(jbtn_guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 60, 40, 40));

        jbtn_agregar_articulos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/shop-cart-down-icon_32.png"))); // NOI18N
        jbtn_agregar_articulos.setToolTipText("Agregar mas articulos");
        jbtn_agregar_articulos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_agregar_articulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_agregar_articulosActionPerformed(evt);
            }
        });
        jPanel4.add(jbtn_agregar_articulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 100, 40, 40));

        jbtn_generar_reporte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/blank-catalog-icon.png"))); // NOI18N
        jbtn_generar_reporte.setMnemonic('R');
        jbtn_generar_reporte.setToolTipText("Generar reporte (Alt+R)");
        jbtn_generar_reporte.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_generar_reporte.setFocusable(false);
        jbtn_generar_reporte.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_generar_reporte.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_generar_reporte.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbtn_generar_reporteMouseClicked(evt);
            }
        });
        jbtn_generar_reporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_generar_reporteActionPerformed(evt);
            }
        });
        jPanel4.add(jbtn_generar_reporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 140, 40, 40));

        jTabbedPane1.addTab("Detalle...", jPanel4);

        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar2.setRollover(true);

        jbtn_nuevo_cliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Folder-New-Folder-icon.png"))); // NOI18N
        jbtn_nuevo_cliente.setToolTipText("Agregar cliente");
        jbtn_nuevo_cliente.setFocusable(false);
        jbtn_nuevo_cliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_nuevo_cliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_nuevo_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_nuevo_clienteActionPerformed(evt);
            }
        });
        jToolBar2.add(jbtn_nuevo_cliente);

        jbtn_agregar_cliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Document-Add-icon.png"))); // NOI18N
        jbtn_agregar_cliente.setToolTipText("Agregar cliente");
        jbtn_agregar_cliente.setFocusable(false);
        jbtn_agregar_cliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_agregar_cliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_agregar_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_agregar_clienteActionPerformed(evt);
            }
        });
        jToolBar2.add(jbtn_agregar_cliente);

        jbtn_guardar_cliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/floppy-disk-icon.png"))); // NOI18N
        jbtn_guardar_cliente.setToolTipText("Guardar datos cliente");
        jbtn_guardar_cliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtn_guardar_cliente.setFocusable(false);
        jbtn_guardar_cliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_guardar_cliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_guardar_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_guardar_clienteActionPerformed(evt);
            }
        });
        jToolBar2.add(jbtn_guardar_cliente);

        jbtn_editar_cliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Actions-edit-icon.png"))); // NOI18N
        jbtn_editar_cliente.setToolTipText("Editar datos cliente");
        jbtn_editar_cliente.setFocusable(false);
        jbtn_editar_cliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_editar_cliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_editar_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_editar_clienteActionPerformed(evt);
            }
        });
        jToolBar2.add(jbtn_editar_cliente);

        panel_datos_cliente.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        txt_nombre.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_nombreKeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel5.setText("Nombre:");

        txt_apellidos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_apellidos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_apellidosKeyReleased(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel6.setText("Apellidos:");

        txt_apodo.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel7.setText("Apodo:");

        txt_tel_movil.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel8.setText("Tel Movil:");

        txt_tel_casa.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel9.setText("Tel Casa:");

        txt_email.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel26.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel26.setText("Email:");

        txt_direccion.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel27.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel27.setText("Direccion:");

        txt_localidad.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel28.setText("Localidad:");

        jLabel29.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel29.setText("RFC:");

        txt_rfc.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        javax.swing.GroupLayout panel_datos_clienteLayout = new javax.swing.GroupLayout(panel_datos_cliente);
        panel_datos_cliente.setLayout(panel_datos_clienteLayout);
        panel_datos_clienteLayout.setHorizontalGroup(
            panel_datos_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_datos_clienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_datos_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_email)
                    .addGroup(panel_datos_clienteLayout.createSequentialGroup()
                        .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_apellidos))
                    .addGroup(panel_datos_clienteLayout.createSequentialGroup()
                        .addComponent(txt_apodo, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(txt_tel_movil, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_tel_casa))
                    .addComponent(txt_direccion)
                    .addComponent(txt_localidad)
                    .addGroup(panel_datos_clienteLayout.createSequentialGroup()
                        .addGroup(panel_datos_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_datos_clienteLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel_datos_clienteLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_rfc, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 12, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panel_datos_clienteLayout.setVerticalGroup(
            panel_datos_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_datos_clienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_datos_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_datos_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_datos_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_datos_clienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_apodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_tel_movil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_tel_casa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_localidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_rfc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(188, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Clientes en la base de datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        tabla_clientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabla_clientes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tabla_clientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_clientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabla_clientes);

        txt_buscar1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_buscar1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_buscar1KeyReleased(evt);
            }
        });

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search-icon.png"))); // NOI18N

        buttonGroup3.add(check_nombre);
        check_nombre.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        check_nombre.setText("Nombre");

        buttonGroup3.add(check_apellidos);
        check_apellidos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        check_apellidos.setText("Apellidos");

        buttonGroup3.add(check_apodo);
        check_apodo.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        check_apodo.setText("Apodo");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(18, 18, 18)
                        .addComponent(txt_buscar1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(check_nombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(check_apellidos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(check_apodo)
                        .addGap(0, 324, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_buscar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32)
                    .addComponent(check_nombre)
                    .addComponent(check_apellidos)
                    .addComponent(check_apodo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_datos_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panel_datos_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(128, 128, 128))
        );

        jTabbedPane1.addTab("Datos cliente", jPanel9);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1215, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 664, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabla_prox_rentasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_prox_rentasMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            this.txt_editar_cantidad.setEnabled(false);
            this.txt_editar_precio_unitario.setEnabled(false);
            this.txt_editar_porcentaje_descuento.setEnabled(false);
            
            funcion.setEnableContainer(panel_datos_cliente, false);
          
            funcion.setEnableContainer(panel_datos_generales, false);
            funcion.setEnableContainer(jTabbedPane2, false);
            funcion.setEnableContainer(panel_abonos, false);
            
            llenar_combo_estado2();
            llenar_combo_chofer();
            tabla_articulos();
            tabla_detalle();
                       
            String rentaId = tabla_prox_rentas.getValueAt(tabla_prox_rentas.getSelectedRow(), 0).toString();
            Renta renta = saleService.obtenerRentaPorId(new Integer(rentaId), funcion);
            if(renta.getMostrarPreciosPdf().equals("1"))
                this.check_mostrar_precios.setSelected(true);
            else
                this.check_mostrar_precios.setSelected(false);
            
            if(renta.getCliente().getEmail() != null && !renta.getCliente().getEmail().equals("")){
                this.txtEmailToSend.setText(renta.getCliente().getEmail());
            }else{
                this.txtEmailToSend.setText("");
            }
            
            this.g_idTipoEvento = renta.getTipo().getTipoId()+""; // variable global
            id_renta = renta.getRentaId()+""; // variable global
            id_cliente = renta.getCliente().getClienteId()+""; // variable global
            tabla_abonos(id_renta);

                      
            SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
            jTabbedPane1.setEnabledAt(1, true);
            jTabbedPane1.setSelectedIndex(1);
            jTabbedPane1.setEnabledAt(2, true);
            
            lbl_cliente.setText(renta.getCliente().getNombre()+" "+renta.getCliente().getApellidos());
            lbl_folio.setText(renta.getFolio()+"");            
          
            try {
                cmb_fecha_entrega.setDate((Date) formatoDelTexto.parse((String) renta.getFechaEntrega()));
                cmb_fecha_devolucion.setDate((Date) formatoDelTexto.parse((String) renta.getFechaDevolucion()));
                cmb_fecha_evento.setDate((Date) formatoDelTexto.parse((String) renta.getFechaEvento()));
            } catch (ParseException ex) {
                Logger.getLogger(consultar_renta.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            cmb_estado1.setSelectedItem(renta.getEstado().getDescripcion());            
            cmb_chofer.setSelectedItem(renta.getChofer().getNombre()+" "+renta.getChofer().getApellidos());          
            txt_descripcion.setText(renta.getDescripcion());            
            txt_comentarios.setText(renta.getComentario());            

            cmb_tipo.setSelectedItem(renta.getTipo().getTipo());
            this.txtPorcentajeDescuento.setValue(renta.getDescuento());
            txt_descuento.setText(decimalFormat.format(renta.getCantidadDescuento()));
            txt_iva.setValue(renta.getIva());
            txt_iva.setValue(renta.getIva());
            txt_total_iva.setValue(0);            
            String[] horaSplit = renta.getHoraEntrega().split("a");            

            String hora_devolucion = renta.getHoraDevolucion();
            String[] horaDevolucionSplit = hora_devolucion.split("a");
            this.cmb_hora_devolucion.setSelectedItem(horaDevolucionSplit[0].replaceAll(" ", ""));
            this.cmb_hora_devolucion_dos.setSelectedItem(horaDevolucionSplit[1].replaceAll(" ", ""));
            cmb_hora.setSelectedItem(horaSplit[0].replaceAll(" ", ""));
            cmb_hora_dos.setSelectedItem(horaSplit[1].replaceAll(" ", ""));
            
            lbl_atiende.setText("Atendio: " + renta.getUsuario().getNombre()+" "+renta.getUsuario().getApellidos());
            
            this.txt_envioRecoleccion.setText(decimalFormat.format(renta.getEnvioRecoleccion()));
            this.txt_depositoGarantia.setText(decimalFormat.format(renta.getDepositoGarantia()));
            
            // agregamos los articulos de esta renta
            DefaultTableModel tablaDetalle = (DefaultTableModel) tabla_detalle.getModel();
            for(DetalleRenta detalle : renta.getDetalleRenta()){
                float descuento = 0f;
                float importe = detalle.getCantidad()*detalle.getPrecioUnitario();
                if(detalle.getPorcentajeDescuento() > 0)
                    descuento = (importe * (detalle.getPorcentajeDescuento() / 100));
                
                importe = importe - descuento;
                
                    Object fila[] = {
                        detalle.getDetalleRentaId()+"",                       
                        detalle.getCantidad()+"",
                        detalle.getArticulo().getArticuloId()+"",
                        detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor(),
                        conviertemoneda(detalle.getPrecioUnitario()+""),
                        detalle.getPorcentajeDescuento()+"",
                        descuento+"",
                        conviertemoneda(importe+""),
                        "0"
                    };
                    tablaDetalle.addRow(fila);
            }
            this.txt_faltantes.setText(decimalFormat.format(renta.getTotalFaltantes()));
            subTotal();
            abonos();
            total();
            
            datos_cliente(renta.getCliente().getClienteId());
            jbtn_agregar_cliente.setEnabled(false);
            jbtn_guardar_cliente.setEnabled(false);
            
        }
    }//GEN-LAST:event_tabla_prox_rentasMouseClicked

    private void jbtn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_buscarActionPerformed
        // TODO add your handling code here:
        buscar();

    }//GEN-LAST:event_jbtn_buscarActionPerformed

    private void jbtn_refrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_refrescarActionPerformed
        // TODO add your handling code here:

        sql = "SELECT r.`id_renta`,r.`folio`,CONCAT(c.`nombre`,\" \", c.`apellidos`)AS cliente,e.`descripcion` as estado, r.`fecha_entrega`, r.`hora_entrega`, r.`descripcion`, CONCAT(u.`nombre`,\" \",u.`apellidos`)AS Chofer "
                + "FROM renta r, estado e, clientes c, usuarios u "
                + "WHERE r.id_estado=e.id_estado AND r.id_clientes=c.id_clientes AND r.id_usuario_chofer=u.id_usuarios AND STR_TO_DATE(r.`fecha_entrega`, '%d/%m/%Y') >= STR_TO_DATE('" + fecha_sistema + "', '%d/%m/%Y' ) AND r.`id_tipo`='" + id_tipo + "' AND r.`id_estado`<> '4' ORDER BY r.`folio`";
        
        tabla_consultar_renta();
    }//GEN-LAST:event_jbtn_refrescarActionPerformed

    private void txt_cantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cantidadKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            agregar_articulos();
        }

    }//GEN-LAST:event_txt_cantidadKeyPressed

    private void txt_buscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscarKeyReleased
        // TODO add your handling code here:
        tabla_articulos_like();
    }//GEN-LAST:event_txt_buscarKeyReleased

    private void tabla_articulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_articulosMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            canti = Float.parseFloat(tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 5).toString());
            lbl_eleccion.setText((String) tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 2).toString() + " " + (String) tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 3).toString());
            txt_precio_unitario.setText(EliminaCaracteres((String) tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 4).toString(), "$,"));
            txt_porcentaje_descuento.setText("");
            txt_cantidad.requestFocus();
            id_articulo = (String) tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString();
            txt_precio_unitario.setEditable(false);
            
        }

    }//GEN-LAST:event_tabla_articulosMouseClicked

    private void tabla_detalleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_detalleMouseClicked
        // TODO add your handling code here:
        
        if (evt.getClickCount() == 2) {
            lbl_sel.setText((String) tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 3));
            this.editarDetalleRenta();
        }
    }//GEN-LAST:event_tabla_detalleMouseClicked

    private void jbtn_agregar_articuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_agregar_articuloActionPerformed
        // TODO add your handling code here:
        agregar_articulos();
    }//GEN-LAST:event_jbtn_agregar_articuloActionPerformed

    private void jbtn_editar_dineroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editar_dineroActionPerformed
        // TODO add your handling code here:
        if (iniciar_sesion.administrador_global.equals("1")) {
            txt_precio_unitario.setEditable(true);
            txt_precio_unitario.requestFocus();
            JOptionPane.showMessageDialog(null, "Puedes modificar el precio...", "Precio", JOptionPane.INFORMATION_MESSAGE);
            
            Toolkit.getDefaultToolkit().beep();
            
        } else {
            JOptionPane.showMessageDialog(null, "No cuentas con permisos para modificar el precio...", "Error", JOptionPane.INFORMATION_MESSAGE);
            
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_jbtn_editar_dineroActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        quitar_articulo();

    }//GEN-LAST:event_jButton3ActionPerformed

    private void tabla_abonosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_abonosMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            editar_abonos();
            
        }
    }//GEN-LAST:event_tabla_abonosMouseClicked

    private void jbtn_agregar_abonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_agregar_abonoActionPerformed
        // TODO add your handling code here:
        agregar_abonos();
    }//GEN-LAST:event_jbtn_agregar_abonoActionPerformed

    private void jbtn_quitar_abonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_quitar_abonoActionPerformed
        // TODO add your handling code here:
        quitar_abono();
    }//GEN-LAST:event_jbtn_quitar_abonoActionPerformed

    private void cmb_fecha_entregaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmb_fecha_entregaMouseClicked
        // TODO add your handling code here:


    }//GEN-LAST:event_cmb_fecha_entregaMouseClicked

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formInternalFrameClosing

    private void cmb_fecha_entregaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmb_fecha_entregaKeyPressed
        // TODO add your handling code here:
        jbtn_guardar.setEnabled(true);
    }//GEN-LAST:event_cmb_fecha_entregaKeyPressed

    private void jbtn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_guardarActionPerformed
        // TODO add your handling code here:
        actualizar_renta();
//        buscar();
        jbtn_disponible.setEnabled(true);
    }//GEN-LAST:event_jbtn_guardarActionPerformed

    private void jbtn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editarActionPerformed
        // TODO add your handling code here:

        if (iniciar_sesion.administrador_global.equals("1")) {
            funcion.setEnableContainer(panel_datos_generales, true);
            funcion.setEnableContainer(jTabbedPane2, true);
            
//            JOptionPane.showMessageDialog(null, "Puedes modificar...=)", "Modificar pedido", JOptionPane.INFORMATION_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
            jbtn_guardar.setEnabled(true);
            
            this.txt_editar_cantidad.setEnabled(false);
            this.txt_editar_precio_unitario.setEnabled(false);
            this.txt_editar_porcentaje_descuento.setEnabled(false);
            
        } else {
            JOptionPane.showMessageDialog(null, "No cuenta con permisos suficientos :(", "Error", JOptionPane.INFORMATION_MESSAGE);
            
        }
    }//GEN-LAST:event_jbtn_editarActionPerformed

    private void jbtn_editar_abonosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editar_abonosActionPerformed
        // TODO add your handling code here:
        editar_abonos();
    }//GEN-LAST:event_jbtn_editar_abonosActionPerformed

    private void jbtn_guardar_abonosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_guardar_abonosActionPerformed
        // TODO add your handling code here:
        guardar_abonos();
        

    }//GEN-LAST:event_jbtn_guardar_abonosActionPerformed

    private void jbtn_guardar_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_guardar_clienteActionPerformed
        // TODO add your handling code here:
        guardar_cliente();
        jTabbedPane1.setSelectedIndex(1);
        JOptionPane.showMessageDialog(null, "Se actualizo los datos del cliente...", "Datos del cliente", JOptionPane.INFORMATION_MESSAGE);
        
        jbtn_guardar_cliente.setEnabled(false);
        funcion.setEnableContainer(panel_datos_cliente, false);
        //limpiar();
        tabla_clientes();
        tabla_detalle();
        

    }//GEN-LAST:event_jbtn_guardar_clienteActionPerformed

    private void txt_nombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nombreKeyReleased
        // TODO add your handling code here:
        tabla_clientes_nombre();
    }//GEN-LAST:event_txt_nombreKeyReleased

    private void txt_apellidosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_apellidosKeyReleased
        // TODO add your handling code here:
        tabla_clientes_apellidos();
    }//GEN-LAST:event_txt_apellidosKeyReleased

    private void tabla_clientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_clientesMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            if (id_cliente.equals(tabla_clientes.getValueAt(tabla_clientes.getSelectedRow(), 0))) {
                JOptionPane.showMessageDialog(null, "Este cliente ya esta asignado a la renta, elije uno diferente", "Error", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int seleccion = JOptionPane.showOptionDialog(this, "Se cambiara el cliente de la renta ¿Deseas continuar?", "cambiar cliente de la renta", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
                if (seleccion == 0) {//presiono que si

                    jTabbedPane1.setSelectedIndex(1);
                    jTabbedPane1.setEnabledAt(1, true);
                    id_cliente = (String) tabla_clientes.getValueAt(tabla_clientes.getSelectedRow(), 0);
                    JOptionPane.showMessageDialog(null, "Se a cambiado el cliente con exito...", "Cambio cliente", JOptionPane.INFORMATION_MESSAGE);
                    
                    this.lbl_cliente.setText("Socio: " + (String) tabla_clientes.getValueAt(tabla_clientes.getSelectedRow(), 1) + " " + (String) tabla_clientes.getValueAt(tabla_clientes.getSelectedRow(), 2));
                    // funcion.conectate();
                    
                    System.out.println("ID RENTA ES: " + id_renta);
                    System.out.println("ID CLIENTE ES: " + id_cliente);
                    
                    String datos[] = {id_cliente, id_renta};
                    try {                   
                        funcion.UpdateRegistro(datos, "UPDATE renta SET id_clientes = ? WHERE id_renta = ? ");
                        
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
                    }
                    // funcion.desconecta();
                    //tabla_detalle();
                    tabla_consultar_renta();
                }
            }
        }
    }//GEN-LAST:event_tabla_clientesMouseClicked

    private void jbtn_nuevo_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_nuevo_clienteActionPerformed
        // TODO add your handling code here:
        limpiar();
        funcion.setEnableContainer(panel_datos_cliente, true);
        jbtn_agregar_cliente.setEnabled(true);
        jbtn_guardar_cliente.setEnabled(false);
        jbtn_editar_cliente.setEnabled(false);
    }//GEN-LAST:event_jbtn_nuevo_clienteActionPerformed

    private void jbtn_agregar_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_agregar_clienteActionPerformed
        // TODO add your handling code here:
        int seleccion = JOptionPane.showOptionDialog(this, "Se cambiara el cliente de la renta ¿Deseas continuar?", "cambiar cliente de la renta", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
        if (seleccion == 0) {//presiono que si
            if (agregar_cliente() == true) {
                jTabbedPane1.setSelectedIndex(1);
                
                JOptionPane.showMessageDialog(null, "Se agrego el cliente a la db y se actualizo...", "Cliente bd", JOptionPane.INFORMATION_MESSAGE);
                
                this.lbl_cliente.setText("Socio: " + this.txt_nombre.getText() + " " + this.txt_apellidos.getText());
                //limpiar();
                funcion.setEnableContainer(panel_datos_cliente, false);
                jbtn_agregar_cliente.setEnabled(false);
                jbtn_editar_cliente.setEnabled(true);
                
            }
        }
    }//GEN-LAST:event_jbtn_agregar_clienteActionPerformed

    private void jbtn_editar_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editar_clienteActionPerformed
        // TODO add your handling code here:
        jbtn_guardar_cliente.setEnabled(true);
        funcion.setEnableContainer(panel_datos_cliente, true);
    }//GEN-LAST:event_jbtn_editar_clienteActionPerformed

    private void txt_buscar_folioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscar_folioKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            /* sql = "SELECT r.`id_renta`,r.`folio`,CONCAT(c.`nombre`,\" \", c.`apellidos`)AS cliente,e.`descripcion` as estado, r.`fecha_entrega`, r.`hora_entrega`, r.`descripcion` FROM renta r, estado e, clientes c\n"
             + "WHERE r.id_estado=e.id_estado AND r.id_clientes=c.id_clientes AND r.`folio`='" + txt_buscar_folio.getText().toString() + "'";
             */
              sql = "SELECT r.id_renta, r.folio, c.nombre, c.apellidos, estado.id_estado, estado.descripcion, "
                    + "r.fecha_evento, r.fecha_entrega, r.hora_entrega, r.descripcion, chofer.nombre, chofer.apellidos, "
                    + "tipo.id_tipo, tipo.tipo, r.cantidad_descuento, r.iva, r.deposito_garantia, r.envio_recoleccion ,u.nombre AS nombre_usuario, u.apellidos AS apellidos_usuario "
                    + "FROM renta r "
                    + "INNER JOIN clientes c ON (r.id_clientes = c.id_clientes) "
                    +" INNER JOIN usuarios u ON (u.id_usuarios = r.id_usuarios) "
                    + "INNER JOIN tipo tipo ON(tipo.id_tipo = r.id_tipo) "
                    + "INNER JOIN estado estado ON (estado.id_estado = r.id_estado) "
                    + "INNER JOIN usuarios chofer ON (chofer.id_usuarios = r.id_usuario_chofer) "
                    + "WHERE r.folio = ' "+this.txt_buscar_folio.getText().toString() +"' "
                    + "";
              
            tabla_consultar_renta();
        }
    }//GEN-LAST:event_txt_buscar_folioKeyPressed

    private void jTabbedPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane2MouseClicked
        // TODO add your handling code here:
        txt_abono.requestFocus();
    }//GEN-LAST:event_jTabbedPane2MouseClicked

    private void jbtn_agregar_articulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_agregar_articulosActionPerformed
        // TODO add your handling code here:
        panel_articulos.setVisible(true);
    }//GEN-LAST:event_jbtn_agregar_articulosActionPerformed

    private void jbtn_generar_reporte1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_generar_reporte1ActionPerformed
        // TODO add your handling code here:
//        String xiva;
//        float yiva, itotal;
//        if (tabla_prox_rentas.getSelectedRow() != - 1) {
//            id_renta = tabla_prox_rentas.getValueAt(tabla_prox_rentas.getSelectedRow(), 0).toString();
//            // funcion.conectate();
//            
//            Renta renta = saleService.obtenerRentaPorId(new Integer(id_renta), funcion);
//            if(renta ==null){
//                JOptionPane.showMessageDialog(null, "No se obtuvieron resultados, porfavor cierra y abre la aplicacion ", "ERROR", JOptionPane.INFORMATION_MESSAGE);
//                return;
//            }
//            
//            float fAbonos = 0f;
//            float fSubTotal = 0f;
//            
//            for(DetalleRenta detalle : renta.getDetalleRenta()){
//                float fDescuento = 0f;
//                if(detalle.getPorcentajeDescuento() > 0)
//                    fDescuento += ( detalle.getCantidad() * detalle.getPrecioUnitario() ) * (detalle.getPorcentajeDescuento()/100);                
//                    
//                fSubTotal += ( detalle.getCantidad() * detalle.getPrecioUnitario() ) - fDescuento;
//            }
//             subTotal = fSubTotal+"";
//             
//             if(renta.getAbonos() != null && renta.getAbonos().size()>0){
//                for(Abono abono : renta.getAbonos()){
//                    fAbonos += abono.getAbono();
//                }
//             }
//             
//             float fSubtTotalFaltantes = 0f;
//             float fTotalFaltantes = 0f;
//             
//             String totalFaltantes = funcion.GetData("total",
//                      "SELECT SUM(IF( ( f.fg_devolucion = '0' AND f.fg_accidente_trabajo = '0'),(f.cantidad * a.precio_compra),0) )AS total "
//                    + "FROM faltantes f "
//                    + "INNER JOIN articulo a ON (f.id_articulo = a.id_articulo) "
//                    + "WHERE f.id_renta=" + id_renta + " "
//                    + "AND f.fg_activo = '1' "
//                    );
//            if(totalFaltantes == null || totalFaltantes.equals(""))
//                totalFaltantes = "0";
//            
//            try {
//                fSubtTotalFaltantes = new Float(totalFaltantes);
//            } catch (Exception e) {
//            }
//            g_mensajeFaltantes = "";
//            if(fSubtTotalFaltantes > 0){
//                // el pedido tiene pago pendiente por faltante
//                if(renta.getDepositoGarantia()>0){
//                    // a dejado deposito en garantia
//                    fTotalFaltantes = fSubtTotalFaltantes - renta.getDepositoGarantia();
//                    if(fTotalFaltantes < 0)
//                        this.g_mensajeFaltantes = "deposito en garantia es: $ "+renta.getDepositoGarantia()+", concepto faltantes es: $ "+fSubtTotalFaltantes+", cantidad a devolver al cliente: $ "+ (fTotalFaltantes * -1);
//                    else if(fTotalFaltantes > 0)
//                        this.g_mensajeFaltantes = "deposito en garantia es: $ "+renta.getDepositoGarantia()+", concepto faltantes es: $ "+fSubtTotalFaltantes+", resta: $ "+ (fTotalFaltantes);
//                }else{
//                    // se asgina el total                   
//                    this.g_mensajeFaltantes = "total a pagar por concepto de faltantes es: $ "+fSubtTotalFaltantes;
//                }                
//                
//            }
//            if(fTotalFaltantes>0)
//                g_totalFaltantes = fTotalFaltantes;
//             else
//                g_totalFaltantes = 0f;
//             cant_abono = fAbonos+"";
//             desc_rep  = renta.getCantidadDescuento()+"";
//             xiva = renta.getIva()+"";
//            
//            if (cant_abono == null) {
//                cant_abono = "0";
//            }
//            if (desc_rep == null) {
//                desc_rep = "0";
//            }
//            if (xiva == null) {
//                iva_rep = "0";
//            } else {
//                yiva = Float.parseFloat(xiva.toString());
//                itotal = (Float.parseFloat(subTotal) * (yiva / 100));
//                iva_rep = String.valueOf(itotal);
//
//                //((Float.parseFloat(auxSubtotal) * (xiva / 100)));
//            }
//            System.out.println("SUBTOTAL: " + subTotal);
//            System.out.println("ABONO: " + cant_abono);
//            chofer = renta.getChofer().getNombre() + " " + renta.getChofer().getApellidos();
            fgConsultaTabla= true;
            try{
                reporte();
            }catch(Exception e){
                log.error(e);
            }
            
//        } else {
//            JOptionPane.showMessageDialog(null, "Selecciona una fila para generar el reporte...", "Reporte", JOptionPane.INFORMATION_MESSAGE);
//            
//        }
    }//GEN-LAST:event_jbtn_generar_reporte1ActionPerformed

    private void jbtn_generar_reporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_generar_reporteActionPerformed
        // TODO add your handling code here:
        float a = 0, b = 0;
        String aux = "";
        
        for (int i = 0; i < tabla_detalle.getRowCount(); i++) {
            aux = EliminaCaracteres((String) tabla_detalle.getValueAt(i, 7).toString(), "$,");
            a = a + Float.parseFloat(aux);
        }
        subTotal = String.valueOf(a);
        
        for (int i = 0; i < tabla_abonos.getRowCount(); i++) {
            aux = EliminaCaracteres((String) tabla_abonos.getValueAt(i, 3).toString(), "$,");
            b = b + Float.parseFloat(aux);
        }
        cant_abono = String.valueOf(b);
        chofer = cmb_chofer.getSelectedItem().toString();
        desc_rep = EliminaCaracteres(txt_descuento.getText().toString(), "$");
        desc_rep = desc_rep.replaceAll(",", "");
        iva_rep = EliminaCaracteres(txt_total_iva.getText().toString(), "$");
        iva_rep=iva_rep.replaceAll(",", "");
        reporte();

    }//GEN-LAST:event_jbtn_generar_reporteActionPerformed

    private void txt_fecha_inicialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_fecha_inicialMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fecha_inicialMouseClicked

    private void txt_fecha_inicialKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_fecha_inicialKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fecha_inicialKeyPressed

    private void txt_fecha_finalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_fecha_finalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fecha_finalMouseClicked

    private void txt_fecha_finalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_fecha_finalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_fecha_finalKeyPressed

    private void check_fechasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_fechasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_check_fechasActionPerformed

    private void jbtn_disponibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_disponibleActionPerformed
        // TODO add your handling code here:
        if (tabla_detalle.getRowCount() < 0 && cmb_fecha_entrega.getDate() == null && cmb_fecha_devolucion.getDate() == null) {
            JOptionPane.showMessageDialog(rootPane, "Debe de haber por lo menos un articulo en la tabla \npara revisar la disponibilidad en la base de datos \nFecha de entrega o fecha de devolucion estan vacias.");
        } else {
            fecha_inicial = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_entrega.getDate());
            fecha_final = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_devolucion.getDate());
            validar_consultar = "1";
            mostrar_disponibilidad();
        }
    }//GEN-LAST:event_jbtn_disponibleActionPerformed

    private void jbtn_mostrar_articulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_mostrar_articulosActionPerformed
        // TODO add your handling code here:
        System.out.println("Panel es: " + panel);
        
        if (panel == false) {
            panel_conceptos.setVisible(true);
            panel_articulos.setVisible(false);
            panel = true;
        } else {
            panel = false;
            panel_conceptos.setVisible(false);
            panel_articulos.setVisible(true);
        }
        //jbtn_mostrar_articulos.setEnabled(false);
    }//GEN-LAST:event_jbtn_mostrar_articulosActionPerformed

    private void txt_precio_unitarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_precio_unitarioKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            agregar_articulos();
            
        }
    }//GEN-LAST:event_txt_precio_unitarioKeyPressed

    private void txt_buscar1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscar1KeyReleased
        // TODO add your handling code here:
        tabla_clientes_like();
    }//GEN-LAST:event_txt_buscar1KeyReleased

    private void txt_cantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cantidadActionPerformed

    private void jbtnGenerarReporteEntregasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGenerarReporteEntregasActionPerformed
        
            
        if (tabla_prox_rentas.getSelectedRow() == - 1){
            JOptionPane.showMessageDialog(null, "Selecciona una fila para generar el reporte...", "Reporte", JOptionPane.INFORMATION_MESSAGE);
            return;
         }
             
        String rentaId = tabla_prox_rentas.getValueAt(tabla_prox_rentas.getSelectedRow(), 0).toString();

        if(rentaId == null || rentaId.equals("") || rentaId.equals("0")){
            JOptionPane.showMessageDialog(rootPane, "No se recibio correctamente el id de la renta, porfavor reincia el sistema :P");
            return;
        }            

        Renta renta = saleService.obtenerRentaPorId(new Integer(rentaId), funcion);

        if(renta == null ){
           JOptionPane.showMessageDialog(rootPane, "No se obtuvo los datos de manuera correcta, intentalo de nuevo o reinicia el sistema ");
           return;
        }

        if( renta.getTipo().getTipoId() != new Integer(ApplicationConstants.TIPO_PEDIDO)){
            JOptionPane.showMessageDialog(rootPane, "ERROR. el tipo de pedido actualmente es: "+renta.getTipo().getTipo()+"\nPara continuar debera ser de tipo PEDIDO ");
           return;
        }

        Usuario chofer = userService.obtenerUsuarioPorId(funcion,renta.getUsuarioChoferId() );  

        // mandamos a generar el reporte PDF
        JasperPrint jasperPrint;
        
        try {               
                String pathLocation = Utility.getPathLocation();
                JasperReport masterReport = null;
            try {
                masterReport = (JasperReport) JRLoader.loadObject(pathLocation+ApplicationConstants.RUTA_REPORTE_ENTREGAS);
            } catch (JRException e) {                
                JOptionPane.showMessageDialog(rootPane, "Error cargando el reporte maestro: " + e.getMessage());
            }
            
           
            
            // enviamos los parametros
            Map map = new HashMap<>();
            // generamos el QR
//            map.put("QR",systemService.generarQR());            
            map.put("id_renta", renta.getRentaId());                
            map.put("chofer", chofer.getNombre()+" "+chofer.getApellidos());    
            map.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );

            jasperPrint = JasperFillManager.fillReport(masterReport, map, funcion.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathLocation+ApplicationConstants.NOMBRE_REPORTE_ENTREGAS);
            File file2 = new File(pathLocation+ApplicationConstants.NOMBRE_REPORTE_ENTREGAS);
            try {
                Desktop.getDesktop().open(file2);
            } catch (IOException ex) {
                Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception j) {
            System.out.println("Mensaje de Error:" + j.toString());
            JOptionPane.showMessageDialog(rootPane, "Mensaje de Error :" + j.toString() + "\n Existe un PDF abierto, cierralo e intenta generar el PDF nuevamente");
        }
            
         
    }//GEN-LAST:event_jbtnGenerarReporteEntregasActionPerformed

    private void jtbtnGenerateExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbtnGenerateExcelActionPerformed
        systemService.exportarExcel(tabla_prox_rentas);
    }//GEN-LAST:event_jtbtnGenerateExcelActionPerformed

    private void check_fechas_eventoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_fechas_eventoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_check_fechas_eventoActionPerformed

    private void dateFechaEventoInicialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dateFechaEventoInicialMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_dateFechaEventoInicialMouseClicked

    private void dateFechaEventoInicialKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateFechaEventoInicialKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateFechaEventoInicialKeyPressed

    private void dateFechaEventoFinalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dateFechaEventoFinalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_dateFechaEventoFinalMouseClicked

    private void dateFechaEventoFinalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateFechaEventoFinalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_dateFechaEventoFinalKeyPressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       
       
        if (tabla_prox_rentas.getSelectedRow() == - 1){
            JOptionPane.showMessageDialog(null, "Selecciona una fila para generar el reporte...", "Reporte", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (this.cmbUsuarios.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Selecciona un usuario para generar el reporte ", "Reporte", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
         String usuarioId = funcion.GetData("id_usuarios", "SELECT id_usuarios FROM usuarios WHERE CONCAT(nombre,\" \",apellidos)='" + this.cmbUsuarios.getSelectedItem().toString() + "'");
         String rentaId = tabla_prox_rentas.getValueAt(tabla_prox_rentas.getSelectedRow(), 0).toString();
         List<AsignaCategoria> categoriasPorUsuario = categoryService.obtenerCategoriasAsignadasPorUsuarioId(funcion, new Integer(usuarioId));
           
         if(categoriasPorUsuario == null || categoriasPorUsuario.size()<=0){
            JOptionPane.showMessageDialog(null, "Agrega por lo menos una categoria al usuario seleccionado ", "Reporte", JOptionPane.INFORMATION_MESSAGE);
            return;
         }
         
//         String query = "SELECT * " +
//                    "FROM renta renta\n" +
//                    "INNER JOIN detalle_renta detalle ON (detalle.id_renta = renta.id_renta)\n" +
//                    "INNER JOIN articulo articulo ON (articulo.id_articulo = detalle.id_articulo)\n" +
//                    "INNER JOIN categoria categoria ON (categoria.id_categoria = articulo.id_categoria)\n" +
//                    "INNER JOIN color color ON (color.id_color = articulo.id_color)\n" +
//                    "INNER JOIN usuarios usuarios ON (usuarios.id_usuarios = renta.id_usuarios)\n" +
//                    "INNER JOIN asigna_categoria asigna ON (asigna.id_usuarios = usuarios.id_usuarios)\n" +
//                    "WHERE renta.id_renta = "+rentaId+" AND articulo.id_categoria IN (SELECT asigna.id_categoria FROM asigna_categoria asigna WHERE asigna.id_usuarios = "+usuarioId+")";
         
        String query = "SELECT * FROM renta renta\n" +
        "INNER JOIN detalle_renta detalle ON (detalle.id_renta = renta.id_renta)\n" +
        "INNER JOIN articulo articulo ON (articulo.id_articulo = detalle.id_articulo)\n" +
        "WHERE renta.id_renta = "+rentaId+" AND articulo.id_categoria IN (SELECT asigna.id_categoria FROM asigna_categoria asigna WHERE asigna.id_usuarios = "+usuarioId+" )";
         
         
         List<Renta> rentas = saleService.obtenerPedidosPorConsultaSql(query, funcion);
         if(rentas == null || rentas.size()<=0){
            JOptionPane.showMessageDialog(null, "No se obtuvieron registros :( ", "Reporte", JOptionPane.INFORMATION_MESSAGE);
            return;
         }
            JasperPrint jasperPrint;
        try {
            String archivo = ApplicationConstants.RUTA_REPORTE_CATEGORIAS;
            String pathLocation = Utility.getPathLocation();
            System.out.println("Cargando desde: " + archivo);
            if (archivo == null) {
                JOptionPane.showMessageDialog(rootPane, "No se encuentra el Archivo jasper");
              
            }
            JasperReport masterReport = null;
            try {
                masterReport = (JasperReport) JRLoader.loadObject(pathLocation+archivo);
            } catch (JRException e) {               
                JOptionPane.showMessageDialog(rootPane, "Error cargando el reporte maestro: " + e.getMessage());              
            }
            Map parametro = new HashMap<>();
            //guardamos el parametro

            parametro.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );
            parametro.put("ID_RENTA",rentaId);
            parametro.put("ID_USUARIO",usuarioId);
            parametro.put("NOMBRE_ENCARGADO_AREA",this.cmbUsuarios.getSelectedItem()+"");
            
            // funcion.conectate();
            jasperPrint = JasperFillManager.fillReport(masterReport, parametro, funcion.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathLocation+ApplicationConstants.NOMBRE_REPORTE_CATEGORIAS);
            File file2 = new File(pathLocation+ApplicationConstants.NOMBRE_REPORTE_CATEGORIAS);
            try {
                Desktop.getDesktop().open(file2);
            } catch (IOException ex) {
                Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
            }
            // funcion.desconecta();
        } catch (Exception j) {
            System.out.println("Mensaje de Error:" + j.toString());
            JOptionPane.showMessageDialog(rootPane, "Mensaje de Error :" + j.toString() + "\n Existe un PDF abierto, cierralo e intenta generar el PDF nuevamente");
        }
            
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txt_abonoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_abonoKeyPressed
        if (evt.getKeyCode() == 10)
        agregar_abonos();
    }//GEN-LAST:event_txt_abonoKeyPressed

    private void txt_totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_totalActionPerformed

    private void txt_calculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_calculoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_calculoActionPerformed

    private void txt_ivaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ivaFocusLost
        // TODO add your handling code here:
        if (txt_iva.getText().equals("")) {
            txt_iva.setText("0");
        }
    }//GEN-LAST:event_txt_ivaFocusLost

    private void txt_ivaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ivaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            // presiono enter
            total();
            // lbl_aviso.setText("Se aplico con exito el IVA ...");
            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_txt_ivaKeyPressed

    private void txt_depositoGarantiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_depositoGarantiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_depositoGarantiaActionPerformed

    private void txt_depositoGarantiaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_depositoGarantiaKeyPressed
        if (evt.getKeyCode() == 10) {
            if (txt_subtotal.getText().toString().equals("")) {
                JOptionPane.showMessageDialog(null, "No hay cantidad en subtotal para agregar el descuento", "Deposito en garantia", JOptionPane.INFORMATION_MESSAGE);
                Toolkit.getDefaultToolkit().beep();

            } else {
                total();
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_txt_depositoGarantiaKeyPressed

    private void txt_envioRecoleccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_envioRecoleccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_envioRecoleccionActionPerformed

    private void txt_envioRecoleccionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_envioRecoleccionKeyPressed
        if (evt.getKeyCode() == 10) {
            if (txt_subtotal.getText().toString().equals("")) {
                JOptionPane.showMessageDialog(null, "No hay cantidad en subtotal para agregar el descuento", "Envio y recoleccion", JOptionPane.INFORMATION_MESSAGE);
                Toolkit.getDefaultToolkit().beep();

            } else {
                total();
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_txt_envioRecoleccionKeyPressed

    private void txt_descuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_descuentoFocusLost
        // TODO add your handling code here:
        if (txt_descuento.getText().equals("")) {
            txt_descuento.setText("0");
        }
    }//GEN-LAST:event_txt_descuentoFocusLost

    private void txt_descuentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_descuentoKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            if (txt_subtotal.getText().toString().equals("")) {
                JOptionPane.showMessageDialog(null, "No hay cantidad en subtotal para agregar el descuento", "Descuento", JOptionPane.INFORMATION_MESSAGE);
                Toolkit.getDefaultToolkit().beep();

            } else {

                total();
                //lbl_aviso.setText("Se aplico con exito el descuento...");
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_txt_descuentoKeyPressed

    private void txt_porcentaje_descuentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_porcentaje_descuentoKeyPressed
        if (evt.getKeyCode() == 10) {
            agregar_articulos();
            
        }
    }//GEN-LAST:event_txt_porcentaje_descuentoKeyPressed

    private void txtPorcentajeDescuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoFocusLost
        // TODO add your handling code here:
        if (txtPorcentajeDescuento.getText().equals("")) {
            txtPorcentajeDescuento.setText("0");
        }
    }//GEN-LAST:event_txtPorcentajeDescuentoFocusLost

    private void txtPorcentajeDescuentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoKeyPressed
        if (evt.getKeyCode() == 10) {
            if (txt_subtotal.getText().toString().equals("")) {
                JOptionPane.showMessageDialog(null, "Es necesario incluir subtotal para realizar el calculo", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                Toolkit.getDefaultToolkit().beep();

            } else {
                total();
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_txtPorcentajeDescuentoKeyPressed

    private void jbtn_generar_reporteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtn_generar_reporteMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jbtn_generar_reporteMouseClicked

    private void txt_editar_cantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_editar_cantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_editar_cantidadActionPerformed

    private void txt_editar_cantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_editar_cantidadKeyPressed
        if (evt.getKeyCode() == 10) {
            aplicarEditarArticuloPedido();
        }
    }//GEN-LAST:event_txt_editar_cantidadKeyPressed

    private void txt_editar_precio_unitarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_editar_precio_unitarioKeyPressed
       if (evt.getKeyCode() == 10) {
            aplicarEditarArticuloPedido();
        }
    }//GEN-LAST:event_txt_editar_precio_unitarioKeyPressed

    // aplicar modificar el articulo
    public void aplicarEditarArticuloPedido(){
        
        float cantidad=0f;
        float precio=0f;
        float descuento=0f;
        
        String sCantidad = this.txt_editar_cantidad.getText().toString().equals("") ? "0" : this.txt_editar_cantidad.getText().toString();
        String sPrecioU = this.txt_editar_precio_unitario.getText().toString().equals("") ? "0" : this.txt_editar_precio_unitario.getText().toString();
        String sDescuento = this.txt_editar_porcentaje_descuento.getText().toString().equals("") ? "0" : this.txt_editar_porcentaje_descuento.getText().toString();
        try {
            cantidad = new Float(sCantidad);
            precio = new Float(sPrecioU);
            descuento = new Float(sDescuento);
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(null, "Ingresa solo numeros "+e, "ERROR", JOptionPane.INFORMATION_MESSAGE);
             Toolkit.getDefaultToolkit().beep();
             return;
        }        
        if(cantidad <= 0f){
            JOptionPane.showMessageDialog(null, "Ingresa una cantidad mayor a 0 ", "ERROR", JOptionPane.INFORMATION_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
            return;
        }        
        if(precio <= 0f){
            JOptionPane.showMessageDialog(null, "Ingresa un precio mayor a 0 ", "ERROR", JOptionPane.INFORMATION_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        if(descuento > 100f){
            JOptionPane.showMessageDialog(null, "El porcentaje descuento debe ser menor a 100 ", "ERROR", JOptionPane.INFORMATION_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        
        String datos1[] = {cantidad+"",precio+"",descuento+"",this.g_idDetalleRenta};
        
        try {                   
           funcion.UpdateRegistro(datos1, "UPDATE detalle_renta SET cantidad=?,p_unitario=?,porcentaje_descuento=? WHERE id_detalle_renta=?");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        }
        limpiarBotonesEdicion();               
        this.formato_tabla_detalles();
        this.llenarTablaDetalle();
        
    }
    
    public void limpiarBotonesEdicion(){
        this.txt_editar_cantidad.setText("");
        this.txt_editar_precio_unitario.setText("");
        this.txt_editar_porcentaje_descuento.setText("");
        this.txt_editar_cantidad.setEnabled(false);
        this.txt_editar_precio_unitario.setEnabled(false);
        this.txt_editar_porcentaje_descuento.setEnabled(false);
    }
    
    public void editarDetalleRenta(){
        if(!this.g_idTipoEvento.equals(ApplicationConstants.TIPO_COTIZACION)){
             JOptionPane.showMessageDialog(null, "Solo puedes editar el detalle a una COTIZACION ", "ERROR", JOptionPane.ERROR_MESSAGE);
             return;
        }
            
         this.g_idDetalleRenta = tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 0).toString();
         String porcentajeDescuento = EliminaCaracteres(tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 5).toString(), "$,");
         String cantidad = EliminaCaracteres(tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 1).toString(), "$,");
         String precioUnitario = EliminaCaracteres(tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 4).toString(), "$,");
         
         this.txt_editar_cantidad.setText(cantidad);
         this.txt_editar_porcentaje_descuento.setText(porcentajeDescuento);
         this.txt_editar_precio_unitario.setText(precioUnitario);
         
         this.txt_editar_cantidad.setEnabled(true);
        this.txt_editar_precio_unitario.setEnabled(true);
        this.txt_editar_porcentaje_descuento.setEnabled(true);
         
    }
    
    public void enviar_email() {
        String email = this.txtEmailToSend.getText();
        
        
        try{
            Utility.isEmail(email);
        }catch(MessagingException e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR);
            return;            
        }
        
        DatosGenerales generalData = systemService.getGeneralData();
        
        String mensaje = "";
        String asunto = generalData.getCompanyName()+": Evento actualizado #"+this.lbl_folio.getText();
           
        try{
            DataEmailTemplate emailTemplate = new DataEmailTemplate(
                    systemService.getDataConfigurationByKey("url_logo_empresa"),
                    systemService.getDataConfigurationByKey("icon_check_ok"),
                    systemService.getDataConfigurationByKey("sitio_empresa"),
                    lbl_folio.getText(),
                    new SimpleDateFormat("EEEEE dd 'de' MMMM 'del' yyyy").format(cmb_fecha_devolucion.getDate())+" "+this.cmb_hora_devolucion.getSelectedItem()+" a "+this.cmb_hora_devolucion_dos.getSelectedItem()+" horas",
                    new SimpleDateFormat("EEEEE dd 'de' MMMM 'del' yyyy").format(cmb_fecha_evento.getDate()),
                    cmb_chofer.getSelectedItem()+"",
                    txt_descripcion.getText(),
                    ApplicationConstants.DS_ESTADO_APARTADO,
                    iniciar_sesion.usuarioGlobal.getNombre()+" "+iniciar_sesion.usuarioGlobal.getApellidos(),
                    txt_subtotal.getText(),
                    txt_envioRecoleccion.getText(),
                    txt_depositoGarantia.getText(),
                    txt_total_iva.getText(),
                    txt_abonos.getText(),
                    txt_total.getText(),
                    new SimpleDateFormat("EEEEE dd 'de' MMMM 'del' yyyy").format(cmb_fecha_entrega.getDate())+" "+cmb_hora.getSelectedItem().toString() +" a "+this.cmb_hora_dos.getSelectedItem()+" horas",
                    txt_descuento.getText(),
                    new ArrayList<>()                
            );
        
         
            for (int i = 0; i < tabla_detalle.getRowCount() ; i++) {
                emailTemplate.getItems().add(new ModelTableItem(
                        tabla_detalle.getValueAt(i, 1).toString(),
                        tabla_detalle.getValueAt(i, 3).toString(),
                        tabla_detalle.getValueAt(i, 4).toString(),
                        tabla_detalle.getValueAt(i, 6).toString(),
                        tabla_detalle.getValueAt(i, 7).toString()
                ));
            }
            
            BuildEmailTemplate emailBuilded = new BuildEmailTemplate(emailTemplate);

            mensaje = emailBuilded.buildEmail();
        
        }catch(BusinessException e){
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR);
            return;    
        }
      
      
        
        
        /*
        String asunto;
        StringBuilder mensaje = new StringBuilder();
        fecha_sistema();
        DatosGenerales generalData = systemService.getGeneralData();
        asunto = generalData.getCompanyName()+" >>> Evento actualizado con éxito " + fecha_sistema;
       
        String fechaEntrega = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_entrega.getDate());
        String fechaDevolucion = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_devolucion.getDate());
        String fechaEvento = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_evento.getDate());
        String horaEntrega = cmb_hora.getSelectedItem().toString() +" a "+this.cmb_hora_dos.getSelectedItem()+"";
        String horaDevolucion = this.cmb_hora_devolucion.getSelectedItem()+" a "+this.cmb_hora_devolucion_dos.getSelectedItem()+" horas";
        
        mensaje.append("<div id='container-message' style='width:700px; text-align:left; margin:16px;'>");
        
        mensaje.append("<font color='black'>Estimad@&nbsp;");
            mensaje.append(lbl_cliente.getText());
            mensaje.append(",&nbsp;");
        mensaje.append(generalData.getCompanyName());
        mensaje.append("&nbsp;le notifica que la actualizaci&oacute;n de su pedido a nuestros sistemas ha quedado guardado exitosamente, ");
        mensaje.append("le recordamos que verifique todos los datos contenidos en el folio tanto datos personales y de env&iacute;o ");
        mensaje.append("as&iacute; como los servicios contratados para su entera satisfacci&oacute;n </font>");
        mensaje.append("<br/>");
        mensaje.append("<br/>");
        
        mensaje.append("<table style='border-spacing: 4px;border: 1px solid #afadad; border-radius: 2px;'><caption>Datos del pedido</caption>");
        mensaje.append("<thead><tr><th></th><th></th></tr></thead>");
        
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Folio");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(lbl_folio.getText());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Fecha y hora de entrega");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(fechaEntrega);
                mensaje.append(" ");
                mensaje.append(horaEntrega);
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Fecha y hora devoluci&oacute;n");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(fechaDevolucion);
                mensaje.append(" ");
                mensaje.append(horaDevolucion);
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Fecha del evento");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(fechaEvento);
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Nombre del chofer");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(cmb_chofer.getSelectedItem().toString());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Direcci&oacute;n del evento");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(txt_descripcion.getText());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Tipo registro");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(cmb_tipo.getSelectedItem().toString());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Atendi&oacute;");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(iniciar_sesion.usuarioGlobal.getNombre());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Registrado a");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(this.lbl_cliente.getText());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append("");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:right;'>");
                mensaje.append("");
            mensaje.append("</td>");
        mensaje.append("</tr>");
        
        mensaje.append("<tr>");
            mensaje.append("<td colspan=2 style='text-align:center;font-weight:900;padding-top: 16px; border-top: 1px solid #afadad; '>");
                mensaje.append("DATOS DE FACTURACI&Oacute;N");
            mensaje.append("</td>");
        mensaje.append("</tr>");
        
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Subtotal");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:right;'>");
                mensaje.append(txt_subtotal.getText());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Descuento");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:right;'>");
                mensaje.append(txt_descuento.getText());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Envio y recolecci&oacute;n");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:right;'>");
                mensaje.append(txt_envioRecoleccion.getText());
            mensaje.append("</td>");
        mensaje.append("</tr>");
         mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Dep&oacute;sito en garant&iacute;a");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:right;'>");
                mensaje.append(txt_depositoGarantia.getText());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("IVA");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:right;'>");
                mensaje.append(txt_total_iva.getText());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Pagos");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:right;'>");
                mensaje.append(txt_abonos.getText());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Total");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:right;'>");
                mensaje.append(txt_total.getText());
            mensaje.append("</td>");
        mensaje.append("</tr>");
        
        mensaje.append("</tbody></table>");
        
        mensaje.append("<br/>");

        
            
        mensaje.append("<table style='border-spacing: 4px;border: 1px solid #afadad; border-radius: 2px;'><caption>Detalle pedido</caption><thead><tr><th>cantidad</th><th>articulo</th><th>p. unitario</th><th>descuento</th><th>importe</th></tr></thead>");
        mensaje.append("<tbody>");
        
        for (int i = 0; i < tabla_detalle.getRowCount() ; i++) {
            items.add(new ModelTableItem(
                    tabla_detalle.getValueAt(i, 1).toString(),
                    tabla_detalle.getValueAt(i, 3).toString(),
                    tabla_detalle.getValueAt(i, 4).toString(),
                    tabla_detalle.getValueAt(i, 6).toString(),
                    tabla_detalle.getValueAt(i, 7).toString()
            ));
            mensaje.append("<tr>");
            
            // CANTIDAD
            mensaje.append("<td style='text-align:center;'>");
            mensaje.append(tabla_detalle.getValueAt(i, 1).toString());
            mensaje.append("</td>");
            
            // ARTICULO
            mensaje.append("<td>");
            mensaje.append(tabla_detalle.getValueAt(i, 3).toString());
            mensaje.append("</td>");
            
            //P. UNITARIO
            mensaje.append("<td style='text-align:right;'>");
            mensaje.append(tabla_detalle.getValueAt(i, 4).toString());
            mensaje.append("</td>");
            
            //DESCUENTO
            mensaje.append("<td style='text-align:right;'>");
            mensaje.append(tabla_detalle.getValueAt(i, 6).toString());
            mensaje.append("</td>");
            
            //IMPORTE
            mensaje.append("<td style='text-align:right;'>");
            mensaje.append(tabla_detalle.getValueAt(i, 7).toString());
            mensaje.append("</td>");
            
            mensaje.append("</tr>");
        }
        
        mensaje.append("</tbody></table>");
        
        mensaje.append("<br/>");
        mensaje.append("<br/>");
        mensaje.append("<font color='black'>");
                mensaje.append(generalData.getCompanyName());
                mensaje.append("&nbsp;agradece tu preferencia...");
                mensaje.append("</font>");
        
                mensaje.append("</div>");
                
                */
       
        
//        mensaje = "<div id='container-message' style='width:700px; margin:16px;'>"
//                + "<font color='black'>Estimad@ "+this.lbl_cliente.getText()+ ","
//                + generalData.getCompanyName()+" le notifica que la actualizaci&oacute;n de su pedido a nuestros sistemas ha quedado guardado exitosamente, "
//                + "le recordamos que verifique todos los datos contenidos en el folio tanto datos personales y de env&iacute;o "
//                + "as&iacute; como los servicios contratados para su entera satisfacci&oacute;n </font>"
//                + "<br/>"
//                + "<br/>"
//                + "<font color='black'><span style='font-weight:900;'>LOS DATOS SON: </span></font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Folio:  </span>" + this.lbl_folio.getText() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Fecha y hora de entrega:  </span>" + fechaEntrega + " " + horaEntrega + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Fecha y hora devolucion:  </span>" + fechaDevolucion +" "+ horaDevolucion+ " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Fecha del evento:  </span>" + fechaEvento + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Nombre del chofer:  </span>" + cmb_chofer.getSelectedItem().toString() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Direcci&oacute;n del evento:  </span>" + txt_descripcion.getText() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Tipo registro: </span>" + cmb_tipo.getSelectedItem().toString() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Atendi&oacute;:  </span>" + iniciar_sesion.usuarioGlobal.getNombre() + " " + iniciar_sesion.usuarioGlobal.getApellidos() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Registrado a nombre de:  </span>" + this.lbl_cliente.getText() + " </font>"
//                + "<br/>"
//                + "<br/>"
//                + detalles
//                + "<br/>"
//                + "<br/>"
//                + "<font color='black'><span style='font-weight:900;'>Subtotal: </span>" + txt_subtotal.getText() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Descuento: </span>" + txt_descuento.getText() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Envio y recolecci&oacute;n: </span>" + this.txt_envioRecoleccion.getText() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Dep&oacute;sito en garant&iacute;a: </span>" + this.txt_depositoGarantia.getText() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>IVA: </span>" + txt_total_iva.getText() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Pagos: </span>" + txt_abonos.getText() + " </font><br>"
//                + "<font color='black'><span style='font-weight:900;'>Total: </span>" + txt_total.getText() + " </font><br>"
//                + "<br/>"
//                + "<br/>"
//                + "<font color='black'>Agradecemos su preferencia... </font>"
//                + "</div>";
        Mail mail = new Mail();
//        mail.setFrom(cuenta_emisor); //cuenta emisor
//        mail.setPassword(pass_emisor); //cuenta contraseña
        mail.setTo(email);
        mail.setSubject(asunto);
        mail.setMessage(mensaje+"");
        
//        if (check_adjuntarPDF.isSelected() && check_generar_reporte.isSelected()) {
//            rutaPDF_email = "C:/reportes_mobiliario/reporte.pdf";
//            mail.setArchive(rutaPDF_email);
//        }

        // mail.setArchive(principal.rutaXML_email.toString());
        mail.SEND();
    }
    
    
    private void txt_editar_porcentaje_descuentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_editar_porcentaje_descuentoKeyPressed
        if (evt.getKeyCode() == 10) {
            aplicarEditarArticuloPedido();
        }
    }//GEN-LAST:event_txt_editar_porcentaje_descuentoKeyPressed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
         if (tabla_detalle.getSelectedRow() == - 1){
             JOptionPane.showMessageDialog(null, "Selecciona una fila para editar el articulo ...", "ERROR", JOptionPane.INFORMATION_MESSAGE);
             return;
         }
            editarDetalleRenta();
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if(id_renta == null || id_renta.equals("")){
             JOptionPane.showMessageDialog(null, "Ocurrio un error, vuelve a cerrar todo y consultar de nuevo porfavor ...", "ERROR", JOptionPane.INFORMATION_MESSAGE);
             return;
        }
        this.g_idRenta = id_renta;
        mostrar_faltantes();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jbtn_editar_abonosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtn_editar_abonosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jbtn_editar_abonosMouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
       if (tabla_prox_rentas.getSelectedRow() == - 1) {
           JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar ", "ERROR", JOptionPane.INFORMATION_MESSAGE);
           return;
       }
        this.g_idRenta = tabla_prox_rentas.getValueAt(tabla_prox_rentas.getSelectedRow(), 0).toString();
        mostrar_faltantes();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void txt_faltantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_faltantesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_faltantesActionPerformed

    private void txtEmailToSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailToSendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailToSendActionPerformed

    private void check_enviar_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_enviar_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_check_enviar_emailActionPerformed

    private void jBtnAddOrderProviderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAddOrderProviderActionPerformed
        if(id_renta == null || id_renta.equals("")){
             JOptionPane.showMessageDialog(null, "Ocurrio un error, vuelve a cerrar todo y consultar de nuevo porfavor ...", "ERROR", JOptionPane.INFORMATION_MESSAGE);
             return;
        }
        this.g_idRenta = id_renta;
        mostrar_agregar_orden_proveedor(id_renta);
    }//GEN-LAST:event_jBtnAddOrderProviderActionPerformed

    private void jbtn_generar_reporte1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtn_generar_reporte1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jbtn_generar_reporte1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JCheckBox check_apellidos;
    private javax.swing.JCheckBox check_apodo;
    private javax.swing.JCheckBox check_chofer;
    private javax.swing.JCheckBox check_cliente;
    private javax.swing.JCheckBox check_cotizacion;
    private javax.swing.JCheckBox check_enviar_email;
    private javax.swing.JCheckBox check_estado;
    private javax.swing.JCheckBox check_fechas;
    private javax.swing.JCheckBox check_fechas_evento;
    private javax.swing.JCheckBox check_mostrar_precios;
    private javax.swing.JCheckBox check_nombre;
    private javax.swing.JCheckBox check_pedido;
    private javax.swing.JComboBox cmbTipoPago;
    private javax.swing.JComboBox<String> cmbUsuarios;
    private javax.swing.JComboBox cmb_chofer;
    private javax.swing.JComboBox cmb_chofer_buscar;
    private javax.swing.JComboBox cmb_estado;
    private javax.swing.JComboBox cmb_estado1;
    private com.toedter.calendar.JDateChooser cmb_fecha_devolucion;
    private com.toedter.calendar.JDateChooser cmb_fecha_entrega;
    private com.toedter.calendar.JDateChooser cmb_fecha_evento;
    private com.toedter.calendar.JDateChooser cmb_fecha_pago;
    private javax.swing.JComboBox cmb_hora;
    private javax.swing.JComboBox cmb_hora_devolucion;
    private javax.swing.JComboBox cmb_hora_devolucion_dos;
    private javax.swing.JComboBox cmb_hora_dos;
    private javax.swing.JComboBox cmb_limit;
    private javax.swing.JComboBox cmb_tipo;
    private com.toedter.calendar.JDateChooser dateFechaEventoFinal;
    private com.toedter.calendar.JDateChooser dateFechaEventoInicial;
    private javax.swing.JButton jBtnAddOrderProvider;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JButton jbtnGenerarReporteEntregas;
    private javax.swing.JButton jbtn_agregar_abono;
    private javax.swing.JButton jbtn_agregar_articulo;
    private javax.swing.JButton jbtn_agregar_articulos;
    private javax.swing.JButton jbtn_agregar_cliente;
    private javax.swing.JButton jbtn_buscar;
    private javax.swing.JButton jbtn_disponible;
    private javax.swing.JButton jbtn_editar;
    private javax.swing.JButton jbtn_editar_abonos;
    private javax.swing.JButton jbtn_editar_cliente;
    private javax.swing.JButton jbtn_editar_dinero;
    private javax.swing.JButton jbtn_generar_reporte;
    private javax.swing.JButton jbtn_generar_reporte1;
    private javax.swing.JButton jbtn_guardar;
    private javax.swing.JButton jbtn_guardar_abonos;
    private javax.swing.JButton jbtn_guardar_cliente;
    private javax.swing.JButton jbtn_mostrar_articulos;
    private javax.swing.JButton jbtn_nuevo_cliente;
    private javax.swing.JButton jbtn_quitar_abono;
    private javax.swing.JButton jbtn_refrescar;
    private javax.swing.JButton jtbtnGenerateExcel;
    private javax.swing.JLabel lblInformation;
    private javax.swing.JLabel lbl_atiende;
    private javax.swing.JLabel lbl_aviso_resultados;
    private javax.swing.JLabel lbl_cliente;
    private javax.swing.JLabel lbl_eleccion;
    private javax.swing.JLabel lbl_folio;
    public static javax.swing.JLabel lbl_sel;
    private javax.swing.JPanel panel_abonos;
    private javax.swing.JPanel panel_articulos;
    private javax.swing.JPanel panel_conceptos;
    private javax.swing.JPanel panel_datos_cliente;
    private javax.swing.JPanel panel_datos_generales;
    private javax.swing.JTable tabla_abonos;
    public static javax.swing.JTable tabla_articulos;
    private javax.swing.JTable tabla_clientes;
    public static javax.swing.JTable tabla_detalle;
    private javax.swing.JTable tabla_prox_rentas;
    private javax.swing.JTextField txtEmailToSend;
    private javax.swing.JFormattedTextField txtPorcentajeDescuento;
    private javax.swing.JTextField txt_abono;
    private javax.swing.JFormattedTextField txt_abonos;
    private javax.swing.JTextField txt_apellidos;
    private javax.swing.JTextField txt_apodo;
    private javax.swing.JTextField txt_buscar;
    private javax.swing.JTextField txt_buscar1;
    private javax.swing.JFormattedTextField txt_buscar_folio;
    private javax.swing.JFormattedTextField txt_calculo;
    private javax.swing.JTextField txt_cantidad;
    private javax.swing.JTextField txt_cliente;
    private javax.swing.JTextField txt_comentario;
    private javax.swing.JTextPane txt_comentarios;
    private javax.swing.JFormattedTextField txt_depositoGarantia;
    private javax.swing.JTextPane txt_descripcion;
    private javax.swing.JFormattedTextField txt_descuento;
    private javax.swing.JTextField txt_direccion;
    private javax.swing.JTextField txt_editar_cantidad;
    private javax.swing.JTextField txt_editar_porcentaje_descuento;
    private javax.swing.JTextField txt_editar_precio_unitario;
    private javax.swing.JTextField txt_email;
    private javax.swing.JFormattedTextField txt_envioRecoleccion;
    private javax.swing.JFormattedTextField txt_faltantes;
    private com.toedter.calendar.JDateChooser txt_fecha_final;
    private com.toedter.calendar.JDateChooser txt_fecha_inicial;
    private javax.swing.JFormattedTextField txt_iva;
    private javax.swing.JTextField txt_localidad;
    private javax.swing.JTextField txt_nombre;
    private javax.swing.JTextField txt_porcentaje_descuento;
    private javax.swing.JTextField txt_precio_unitario;
    private javax.swing.JTextField txt_rfc;
    private javax.swing.JFormattedTextField txt_subtotal;
    private javax.swing.JTextField txt_tel_casa;
    private javax.swing.JTextField txt_tel_movil;
    private javax.swing.JFormattedTextField txt_total;
    private javax.swing.JFormattedTextField txt_total_iva;
    // End of variables declaration//GEN-END:variables

    private void setExtendedState(int MAXIMIZED_BOTH) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
