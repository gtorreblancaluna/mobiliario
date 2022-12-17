package forms.rentas;

import forms.tipo.abonos.cuentas.TiposAbonosForm;
import common.services.UserService;
import clases.Mail;
import clases.conectate;
import clases.sqlclass;
import common.constants.ApplicationConstants;
import common.utilities.UtilityCommon;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import mobiliario.iniciar_sesion;
import common.model.Articulo;
import common.model.Cliente;
import model.DatosGenerales;
import common.model.Renta;
import common.model.TipoAbono;
import common.model.Usuario;
import forms.inventario.VerDisponibilidadArticulos;
import common.model.Color;
import common.model.EstadoEvento;
import common.model.Tipo;
import model.querys.AvailabilityItemResult;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import services.CustomerService;
import common.services.ItemService;
import services.SaleService;
import services.SystemService;
import common.services.TaskAlmacenUpdateService;
import common.services.TaskDeliveryChoferUpdateService;
import utilities.OptionPaneService;
import utilities.Utility;
import utilities.dtos.ResultDataShowByDeliveryOrReturnDate;

public class AgregarRenta extends javax.swing.JInternalFrame {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AgregarRenta.class.getName());
    sqlclass funcion = new sqlclass();
    conectate conexion = new conectate();
    Object[][] dtconduc;
    Object[] datos_combo, datos_chofer;
    String id_cliente, id_articulo, descuento, iva, email_cliente = "", itemUtilesGlobal = "0";
    Integer globalFolio = 0;
    public static String fecha_inicial, fecha_final, validar_agregar = "0", id_ultima_renta = "";
    boolean existe, panel = false; 
    Integer xemail = 0;
    String fecha_sistema;
    int seleccion;
    float cant_abono = 0, subTotal = 0;
    public static String chofer = "", cuenta_emisor, pass_emisor, servidor_email, puerto_email, conexion_TLS, autenticacion, rutaPDF_email;
    String fecha_entrega, fecha_devolucion, hora_entrega,fecha_evento, hora_devolucion;
    public static boolean utiliza_conexion_TLS = false, utiliza_autenticacion = false, status,validad_tipo_abonos;
    private final UserService userService = UserService.getInstance();
    private final SaleService saleService;
    private final SystemService systemService = SystemService.getInstance();
    // listado de articulos que se llenaran de manera asincrona, y se utilizara para realizar busquedas por descripcion
    private List<Articulo> articulos = new ArrayList<>();
    private final ItemService itemService;
    private final CustomerService customerService;
    private List<Cliente> customers = new ArrayList<>();
    private static final DecimalFormat decimalFormat = new DecimalFormat( "$#,###,###,##0.00" );
    // valor de la fila a editar
    private Integer rowSelectedToEdit = null;
    private TaskAlmacenUpdateService taskAlmacenUpdateService;
    private TaskDeliveryChoferUpdateService taskDeliveryChoferUpdateService;

    public AgregarRenta() {
        
        funcion.conectate();
        initComponents();
        saleService = SaleService.getInstance();
        customerService = CustomerService.getInstance();
        
        txt_precio_unitario.setEditable(false);
        txt_subtotal.setEditable(false);
        txt_abonos.setEditable(false);
        txt_calculo.setEditable(false);
        txt_total.setEditable(false);
        txt_total_iva.setEditable(false);
        txt_descuento.setEditable(false);
        jbtn_reporte.setEnabled(false);
        jbtn_disponible.setEnabled(false);
        check_mostrar_precios.setSelected(true);
        check_generar_reporte.setSelected(true);
        xemail = funcion.existe_email();
        jbtn_nuevo_evento.setEnabled(false);
        panel_articulos.setVisible(true);
        panel_conceptos.setVisible(false);
        jTabbedPane1.setEnabledAt(1, false);
        lbl_atiende.setText("Atiende: " + iniciar_sesion.nombre_usuario_global.toString() + " " + iniciar_sesion.apellidos_usuario_global.toString());
        jTabbedPane1.setSelectedIndex(0);
        itemService = ItemService.getInstance();
        disableInputsEditItem();
        initData();
    }
    
    private void initData () {
        customerTableFormat();
        getCustomers();
        llenar_combo_estado();
        llenar_combo_tipo();
        llenar_chofer();
        llenar_abonos();
        formato_tabla_detalles();
        formato_tabla_abonos();
        nombre_focus();
        
    }
    
    private void fillTableCustomers (List<Cliente> list) {
        customerTableFormat();
        DefaultTableModel tableModel = (DefaultTableModel) tabla_clientes.getModel();
            
            list.forEach(customer -> {
                Object row[] = {
                    customer.getId(),
                    customer.getNombre() + " " + customer.getApellidos(),
                    customer.getApodo(),
                    customer.getTelMovil(),
                    customer.getTelFijo(),
                    customer.getEmail(),
                    customer.getDireccion(),
                    customer.getLocalidad(),
                    customer.getRfc().toUpperCase()
                };
                
                tableModel.addRow(row);
            });
    }
    
    private void getCustomers() {
        try {
            customers = customerService.obtenerClientesActivos();
            fillTableCustomers(customers);
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener los clientes de la base de datos " + e,"ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void conectar() {
        try {        
            conexion.conectate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog( rootPane,"No se puede establecer la comunicacion con la bd:\n"+e);
        }  catch (Exception e) {
            log.error(e);
            JOptionPane.showMessageDialog( rootPane,"Ocurrio un error inesperado, porfavor intentalo de nuevo, verifica tu conexion a internet\n"+e);
        }
    }

    public void mostrar_disponibilidad() {
        
        if (cmb_fecha_entrega.getDate() == null || cmb_fecha_devolucion.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debes indicar fecha de entrega y fecha de devolucion para poder consultar disponibilidad por articulos");
            return;
        }
        
        if (tabla_detalle.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Debes agregar articulos para poder consultar disponibilidad por articulos");
            return;
        }
        
        OptionPaneService optionPaneService = OptionPaneService.getInstance();
        ResultDataShowByDeliveryOrReturnDate resultDataShowByDeliveryOrReturnDate;
        try {
            resultDataShowByDeliveryOrReturnDate = 
                    optionPaneService.getOptionPaneShowByDeliveryOrReturnDateItemsAvailivity(this);   
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;            
        }
        
        
        String deliveryDateOrder = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_entrega.getDate());
        String returnDateOrder = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_devolucion.getDate());

        List<Long> itemsId = new ArrayList<>();
        List<AvailabilityItemResult> availabilityItemResults = new ArrayList<>();
        String commonDescriptionCurrentOrder = "PEDIDO ACTUAL";
        for (int i = 0; i < tabla_detalle.getRowCount(); i++) {
            String itemId = tabla_detalle.getValueAt(i, 1).toString();
            itemsId.add(Long.parseLong(itemId));
            AvailabilityItemResult availabilityItemResult = new AvailabilityItemResult();
            Articulo item = new Articulo();
            Color color = new Color();
            color.setColor("");
            item.setColor(color);
            item.setArticuloId(Integer.parseInt(itemId));
            item.setUtiles(Float.parseFloat(tabla_detalle.getValueAt(i, 7).toString()));
            item.setDescripcion(tabla_detalle.getValueAt(i, 2).toString());
            
            availabilityItemResult.setItem(item);
            availabilityItemResult.setNumberOfItems(Float.parseFloat(tabla_detalle.getValueAt(i, 0).toString()));
            availabilityItemResult.setEventDateOrder(commonDescriptionCurrentOrder);
            availabilityItemResult.setEventDateElaboration(commonDescriptionCurrentOrder);
            availabilityItemResult.setDeliveryDateOrder(deliveryDateOrder);
            availabilityItemResult.setDeliveryHourOrder(commonDescriptionCurrentOrder);
            availabilityItemResult.setReturnDateOrder(returnDateOrder);
            availabilityItemResult.setReturnHourOrder(commonDescriptionCurrentOrder);
            availabilityItemResult.setCustomerName(commonDescriptionCurrentOrder);
            availabilityItemResult.setFolioOrder(commonDescriptionCurrentOrder);
            availabilityItemResult.setDescriptionOrder(txt_descripcion.getText());
            availabilityItemResult.setTypeOrder(cmb_tipo.getSelectedItem().toString());
            availabilityItemResult.setStatusOrder(cmb_estado.getSelectedItem().toString());
            availabilityItemResults.add(availabilityItemResult);
        }
        
        VerDisponibilidadArticulos ventanaVerDisponibilidad = new VerDisponibilidadArticulos(
                null,
                false,
                deliveryDateOrder,
                returnDateOrder,
                false,
                resultDataShowByDeliveryOrReturnDate.getShowByDeliveryDate(),
                resultDataShowByDeliveryOrReturnDate.getShowByReturnDate(),
                itemsId,
                availabilityItemResults,
                null
        );
        ventanaVerDisponibilidad.setVisible(true);
        ventanaVerDisponibilidad.setLocationRelativeTo(this);
    }

    public void enviar_email() {
        String email = this.txtEmailToSend.getText();
        try{
            UtilityCommon.isEmail(email);
        }catch(MessagingException e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR);
            return;            
        }
        String asunto;
        StringBuilder mensaje = new StringBuilder();
        fecha_sistema();
        DatosGenerales generalData = systemService.getGeneralData();
        asunto = generalData.getCompanyName()+" >>> Evento registrado con éxito " + fecha_sistema;
        
        mensaje.append("<div id='container-message' style='width:700px; text-align:left; margin:16px;'>");
        
        mensaje.append("<font color='black'>Estimad@&nbsp;");
            mensaje.append(lbl_cliente.getText());
            mensaje.append(",&nbsp;");
        mensaje.append(generalData.getCompanyName());
        mensaje.append("&nbsp;le notifica que el alta de su pedido a nuestros sistemas ha quedado guardado exitosamente, ");
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
                mensaje.append(globalFolio);
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Fecha y hora de entrega");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(fecha_entrega);
                mensaje.append(" ");
                mensaje.append(hora_entrega);
                mensaje.append(" horas");
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Fecha y hora devoluci&oacute;n");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(fecha_devolucion);
                mensaje.append(" ");
                mensaje.append(hora_devolucion);
                mensaje.append(" horas");
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Fecha del evento");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(fecha_evento);
            mensaje.append("</td>");
        mensaje.append("</tr>");
        mensaje.append("<tr>");
            mensaje.append("<td style='text-align:left;font-weight:900;'>");
                mensaje.append("Nombre del chofer");
            mensaje.append("</td>");
            mensaje.append("<td style='text-align:left;'>");
                mensaje.append(chofer);
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
        for (int i = 0; i < tabla_detalle.getRowCount(); i++) {
            mensaje.append("<tr>");
            
            // CANTIDAD
            mensaje.append("<td style='text-align:center;'>");
            mensaje.append(tabla_detalle.getValueAt(i, 0).toString());
            mensaje.append("</td>");
            
            // ARTICULO
            mensaje.append("<td>");
            mensaje.append(tabla_detalle.getValueAt(i, 2).toString());
            mensaje.append("</td>");
            
            //P. UNITARIO
            mensaje.append("<td style='text-align:right;'>");
            mensaje.append(tabla_detalle.getValueAt(i, 3).toString());
            mensaje.append("</td>");
            
            //DESCUENTO
            mensaje.append("<td style='text-align:right;'>");
            mensaje.append(tabla_detalle.getValueAt(i, 5).toString());
            mensaje.append("</td>");
            
            //IMPORTE
            mensaje.append("<td style='text-align:right;'>");
            mensaje.append(tabla_detalle.getValueAt(i, 6).toString());
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
        

        System.out.println("MENSAJE: " + mensaje);

        Mail mail = new Mail();
        mail.setTo(email);
        mail.setSubject(asunto);
        mail.setMessage(mensaje+"");
        mail.SEND();
    }

    public void reporte() {

        if (!txt_descuento.getText().equals("")) {
            descuento = EliminaCaracteres(txt_descuento.getText(), "$");
            descuento = descuento.replace(",", ".");
        } else {
            descuento = "0";
        }

        if (txt_total_iva.getText().isEmpty()) {
            iva = "0";
        } else {
            iva = EliminaCaracteres(txt_total_iva.getText(), "$");
            iva = iva.replace(",", ".");
        }

        JasperPrint jasperPrint;
        try {
            String pathLocation = Utility.getPathLocation();
            String archivo = pathLocation+ApplicationConstants.RUTA_REPORTE_NUEVO_PEDIDO;

            System.out.println("Cargando desde: " + archivo);
            if (archivo == null) {
                JOptionPane.showMessageDialog(rootPane, "No se encuentra el Archivo jasper");
            }
            JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(archivo);  

            DatosGenerales datosGenerales = systemService.getGeneralData();
            
            Map parametro = new HashMap<>();
            parametro.put("NOMBRE_EMPRESA",datosGenerales.getCompanyName());
            parametro.put("DIRECCION_1",datosGenerales.getAddress1() != null ? datosGenerales.getAddress1() : "");
            parametro.put("DIRECCION_2",datosGenerales.getAddress2() != null ? datosGenerales.getAddress2() : "");
            parametro.put("DIRECCION_3",datosGenerales.getAddress3() != null ? datosGenerales.getAddress3() : "");
            //guardamos el parámetro
            parametro.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );
            System.out.println("id_renta: " + id_ultima_renta);
            parametro.put("id_renta", id_ultima_renta);
            parametro.put("abonos", cant_abono+"");
            parametro.put("subTotal", subTotal+"");
            parametro.put("chofer", chofer);
            parametro.put("descuento", descuento);
            parametro.put("iva", iva);
            parametro.put("INFO_SUMMARY_FOLIO",datosGenerales.getInfoSummaryFolio());
            
            Float envioRecoleccion = txt_envioRecoleccion.getText().isEmpty() ? 0F : Float.parseFloat(txt_envioRecoleccion.getText());
            Float depositoGarantia = txt_depositoGarantia.getText().isEmpty() ? 0F : Float.parseFloat(txt_depositoGarantia.getText());
            parametro.put("ENVIO_RECOLECCION", String.valueOf(envioRecoleccion));
            parametro.put("DEPOSITO_GARANTIA",String.valueOf(depositoGarantia));
            // new Float(String.valueOf($P{subTotal}))-new Float(String.valueOf($P{abonos}))-new Float(String.valueOf($P{descuento}))+ new Float(String.valueOf($P{iva})) + new Float(String.valueOf($P{ENVIO_RECOLECCION})) + new Float(String.valueOf($P{DEPOSITO_GARANTIA}))
            Float total = subTotal - cant_abono - Float.parseFloat(descuento) + Float.parseFloat(iva) + envioRecoleccion + depositoGarantia;
            parametro.put("TOTAL", decimalFormat.format(total));

            // funcion.conectate();
            jasperPrint = JasperFillManager.fillReport(masterReport, parametro, funcion.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, String.join("",pathLocation,ApplicationConstants.NOMBRE_REPORTE_NUEVO_PEDIDO));
            File file2 = new File(String.join("",pathLocation,ApplicationConstants.NOMBRE_REPORTE_NUEVO_PEDIDO));
            Desktop.getDesktop().open(file2);
        } catch (NoClassDefFoundError j) {
            Logger.getLogger(AgregarRenta.class.getName()).log(Level.SEVERE, null, j);
            JOptionPane.showMessageDialog(rootPane, "No se pudo generar el PDF, muestra este mensaje de error al administrador del sistema: " + j.toString());
        } catch (Exception j) {
            Logger.getLogger(AgregarRenta.class.getName()).log(Level.SEVERE, null, j);
            System.out.println("Mensaje de Error:" + j.toString());
            JOptionPane.showMessageDialog(rootPane, "No se pudo generar el PDF, muestra este mensaje de error al administrador del sistema:" + j.toString() + "\nO bien, revisa que no exista un PDF abierto, cierralo e intenta generar el PDF nuevamente");
        }
    }

    public boolean agregar_cliente() {
        boolean res = false, email = false;
        if (txt_nombre.getText().equals("") || txt_apellidos.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Faltan parametros... ", "Error", JOptionPane.INFORMATION_MESSAGE);

            Toolkit.getDefaultToolkit().beep();
            res = false;
        } else {
            if (!txt_email.getText().equals("") && funcion.isEmail(txt_email.getText().toString())) {
                email = true;
                email_cliente = txt_email.getText().toString();
            } else {
                email_cliente = "";
                email = false;
            }
            if (email == true || txt_email.getText().equals("")) {
                try {
                    String datos[] = {txt_nombre.getText().toString(), txt_apellidos.getText().toString(), txt_apodo.getText().toString(), txt_tel_movil.getText().toString(), txt_tel_casa.getText().toString(), txt_email.getText().toString(), txt_direccion.getText().toString(), txt_localidad.getText().toString(), txt_rfc.getText().toString(), "1"};
                    // funcion.conectate();
                    
                    funcion.InsertarRegistro(datos, "insert into clientes (nombre,apellidos,apodo,tel_movil,tel_fijo,email,direccion,localidad,rfc,activo) values(?,?,?,?,?,?,?,?,?,?)");
                    id_cliente = funcion.ultimoid();
                    // funcion.desconecta();
                    
                    customerTableFormat();
                    res = true;
                } catch (SQLException ex) {
                    Logger.getLogger(AgregarRenta.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Error al insertar registro ", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Email no valido... ", "Error", JOptionPane.INFORMATION_MESSAGE);
                txt_email.requestFocus();
                Toolkit.getDefaultToolkit().beep();

            }

        }
        return res;

    }

    public void nombre_focus() {
        txt_nombre.requestFocus();
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
        
        try {
            
            if(!this.txtPorcentajeDescuento.getText().equals(""))
                fPorcentaejeDescuento = Float.parseFloat(this.txtPorcentajeDescuento.getText()+"".replace(",", "."));
            
            if(!this.txt_subtotal.getText().equals("") )
                fSubtotal = Float.parseFloat(this.txt_subtotal.getText()+"".replace(",", "."));
            
            if(!this.txt_descuento.getText().equals(""))
                fDescuento = Float.parseFloat(this.txt_descuento.getText()+"".replace(",", "."));
            
            if(!this.txt_envioRecoleccion.getText().equals(""))
                fEnvioRecoleccion = Float.parseFloat(this.txt_envioRecoleccion.getText()+"".replace(",", "."));
            
            if(!this.txt_depositoGarantia.getText().equals(""))
                fDepositoGarantia = Float.parseFloat(this.txt_depositoGarantia.getText()+"".replace(",", "."));
            
            if(!this.txt_abonos.getText().equals(""))
                fAbonos = Float.parseFloat(this.txt_abonos.getText()+"".replace(",", "."));        
            
            if(fPorcentaejeDescuento == 0)
                this.txt_descuento.setValue(0);
            
          
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento;
            
            
            if(fPorcentaejeDescuento > 0){
                fDescuento = (fSubtotal * (fPorcentaejeDescuento / 100));
                this.txt_descuento.setValue(fDescuento);
            
            }
              
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento; 
            if(!this.txt_iva.getText().equals(""))
            {              
                fIVA = Float.parseFloat(this.txt_iva.getText()+"".replace(",", "."));
                fTotalIVA = (fCalculo * (fIVA / 100));              
                this.txt_total_iva.setValue(fTotalIVA);
            }            
            
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento; 
            this.txt_calculo.setValue(fCalculo);            
            
             if(!this.txt_total_iva.getText().equals(""))
                fTotalIVA = Float.parseFloat(this.txt_total_iva.getText()+"".replace(",", "."));
            
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento;
            this.txt_calculo.setValue(fCalculo);
            
            
            // TOTALES            
            fTotal = (fCalculo - fAbonos );
           
            if(fTotal < 0)
                fTotal = 0f;
            
            txt_total.setValue(fTotal);
            
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(null, "Error al calcular el total "+e, "SOLO NUMEROS", JOptionPane.INFORMATION_MESSAGE);
             return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado al calcular el total "+e, "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
    }

    public void llenar_combo_estado() {
        int i = 0;
        datos_combo = funcion.GetColumna("estado", "descripcion", "Select descripcion from estado");
        cmb_estado.removeAllItems();

        for (i = 0; i <= datos_combo.length - 1; i++) {
            cmb_estado.addItem(datos_combo[i].toString());
        }
        cmb_estado.addItem("-sel-");
        cmb_estado.setSelectedItem("Apartado");

    }

    public void llenar_combo_tipo() {
        int i = 0;
        // funcion.conectate();
        datos_combo = funcion.GetColumna("tipo", "tipo", "Select tipo from tipo");
        cmb_tipo.removeAllItems();

        for (i = 0; i <= datos_combo.length - 1; i++) {
            System.out.println("COMBO TIPO: " + datos_combo[i].toString());
            cmb_tipo.addItem(datos_combo[i].toString());
        }
        cmb_tipo.addItem("-sel-");
        cmb_tipo.setSelectedItem("Pedido");

    }

    public void llenar_chofer() {

        try {
            List<Usuario> choferes =  userService.getChoferes();
            cmb_chofer.removeAllItems();
            cmb_chofer.addItem(ApplicationConstants.CMB_SELECCIONE);
            
            for(Usuario usuario : choferes){
              cmb_chofer.addItem(usuario.getNombre()+" "+usuario.getApellidos());
            }
            
            cmb_chofer.setSelectedItem(ApplicationConstants.CMB_SELECCIONE);
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
        

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

    public String EliminaCaracteres(String s_cadena, String s_caracteres) {
        String nueva_cadena = "";
        Character caracter = null;
        boolean valido = true;

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

    public void renta() {
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
        } else if (cmb_hora_devolucion.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar hora devolucion ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmb_hora_devolucion_dos.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar segunda hora devolucion", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmb_estado.getSelectedItem().equals("-sel-")) {
            JOptionPane.showMessageDialog(null, "Favor de ingresar un estado ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmb_chofer.getSelectedItem().equals("-sel-")) {
            JOptionPane.showMessageDialog(null, "Favor de seleccionar un chofer ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmb_tipo.getSelectedItem().equals("-sel-")) {
            JOptionPane.showMessageDialog(null, "Favor de seleccionar tipo ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (!txt_descripcion.getText().equals("") && txt_descripcion.getText().length() >= 400) {
            JOptionPane.showMessageDialog(null, "Descripcion a rebasado los caracteres permitidos [400 caracteres] ", "Error", JOptionPane.INFORMATION_MESSAGE);
            txt_descripcion.requestFocus();
        } else if (!this.txt_comentario.getText().equals("") && txt_comentario.getText().length() >= 500) {
            JOptionPane.showMessageDialog(null, "Comentario a rebasado los caracteres permitidos [500 caracteres] ", "Error", JOptionPane.INFORMATION_MESSAGE);
            txt_descripcion.requestFocus();
        } else if (tabla_detalle.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Favor de ingresar detalle de conceptos ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (tabla_abonos.getRowCount() == 0) {
            seleccion = JOptionPane.showOptionDialog(this, "No has registrado abonos,/n ¿Deseas continuar?", "Falta registrar abonos", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            if (seleccion == 0) {//presiono que si
             try { 
                 agregar_renta();
             } catch (SQLNonTransientConnectionException e) {
                log.error(e);
                System.out.println("la conexion se ha cerrado "+e);
                funcion.conectate();
                System.out.println("volvemos abrir la conexion ");
                JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n", "Error", JOptionPane.ERROR_MESSAGE);       
             } catch (Exception e) {
                 log.error(e);
                 JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
             }
            }

        } else {
             try { 
                 agregar_renta();
             } catch (SQLNonTransientConnectionException e) {
                 log.error(e);
                System.out.println("la conexion se ha cerrado, intenta de nuevo\n "+e);
                funcion.conectate();
                 System.out.println("volvemos abrir la conexion ");
                 JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo\n", "Error", JOptionPane.ERROR_MESSAGE);       
             } catch (Exception e) {
                 log.error(e);
                 JOptionPane.showMessageDialog(null, "Ocurrio un error al agregar la renta\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
             }
        }
    }
    

    public void agregar_renta() throws Exception {
       
        if (check_enviar_email.isSelected() == true){
           try{
                UtilityCommon.isEmail(this.txtEmailToSend.getText());
           }catch(MessagingException e){
               JOptionPane.showMessageDialog(null,e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
               return;
           }
        }
        
        subTotal = 0;
        cant_abono = 0;
        String stock = "0";
        
        int aux = 1;
        hora_entrega = cmb_hora.getSelectedItem().toString() +" a "+this.cmb_hora_dos.getSelectedItem()+"";
        hora_devolucion = this.cmb_hora_devolucion.getSelectedItem()+" a "+this.cmb_hora_devolucion_dos.getSelectedItem()+"";
        
//        hora_entrega = cmb_hora.getSelectedItem().toString() + ":" + cmb_minutos.getSelectedItem().toString() + " " + cmb_meridiano.getSelectedItem().toString();
        fecha_entrega = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_entrega.getDate());
        fecha_devolucion = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_devolucion.getDate());
        fecha_evento = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_evento.getDate());

        // funcion.conectate();

        String id_estado = funcion.GetData("id_estado", "Select id_estado from estado where descripcion='" + cmb_estado.getSelectedItem().toString() + "'");
        String id_chofer = funcion.GetData("id_usuarios", "Select id_usuarios from usuarios where CONCAT(nombre,\" \",apellidos)='" + cmb_chofer.getSelectedItem().toString() + "'");
        String id_tipo = funcion.GetData("id_tipo", "Select id_tipo from tipo where tipo='" + cmb_tipo.getSelectedItem().toString() + "'");
        System.out.println("ID TIPO: " + id_tipo);
        
        final EstadoEvento estadoEvento = new EstadoEvento(Integer.parseInt(id_estado));
        final Tipo tipoEvento = new Tipo(Integer.parseInt(id_tipo));
        try {
            Utility.validateStatusAndTypeEvent(estadoEvento,tipoEvento);
        } catch (BusinessException e) {
            log.error(e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String folio = funcion.GetData("folio", "Select folio from datos_generales");
        String folio_cambio = funcion.GetData("folio_cambio", "Select folio_cambio from datos_generales");
        chofer = cmb_chofer.getSelectedItem().toString();

        fecha_sistema();
        String porcentajeDescuentoRenta;
        if (!txt_descuento.getText().equals("") && !txtPorcentajeDescuento.getText().toString().equals("")) {
            descuento = EliminaCaracteres(txt_descuento.getText().toString(), "$");
            descuento = descuento.replace(",", ".");
            porcentajeDescuentoRenta = this.txtPorcentajeDescuento.getText()+"";
        } else {
            porcentajeDescuentoRenta = "0";
            descuento = "0";
        }
        if (!txt_iva.getText().equals("")) {
            iva = EliminaCaracteres(txt_iva.getText().toString(), "$");
            iva = iva.replace(",", ".");
        } else {
            iva = "0";
        }
        
        String mostrarPrecios = check_mostrar_precios.isSelected() == true ? "1" : "0";
        String envioRecoleccion = this.txt_envioRecoleccion.getText().equals("") ? "0" : this.txt_envioRecoleccion.getText()+"";
        String depositoGarantia = this.txt_depositoGarantia.getText().equals("") ? "0" : this.txt_depositoGarantia.getText()+""; 
        
        if (folio_cambio.equals("1")) { //cambio el folio

            if (id_estado.equals("2")) {
                stock = "1";
            }

            String datos[] = {id_estado, id_cliente, iniciar_sesion.id_usuario_global, fecha_sistema, fecha_entrega, hora_entrega, fecha_devolucion, txt_descripcion.getText(),porcentajeDescuentoRenta, descuento, iva, txt_comentarios.getText(), id_chofer, folio, stock, id_tipo,hora_devolucion,fecha_evento,envioRecoleccion,depositoGarantia,mostrarPrecios};
            funcion.InsertarRegistro(datos, "insert into renta (id_estado,id_clientes,id_usuarios,fecha_pedido,fecha_entrega,hora_entrega,fecha_devolucion,descripcion,descuento,cantidad_descuento,iva,comentario,id_usuario_chofer,folio,stock,id_tipo,hora_devolucion,fecha_evento,envio_recoleccion,deposito_garantia,mostrar_precios_pdf) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            id_ultima_renta = funcion.ultimoid_renta();
            
            System.out.println("FOLIO: " + folio);
            aux = Integer.parseInt(folio) + 1;
            System.out.println("aux: " + aux);
            globalFolio = aux;
            String[] datos_2 = {String.valueOf(aux)};
            funcion.UpdateRegistro(datos_2, "UPDATE datos_generales SET folio=?");
            
            

        } else {//No cambio el folio
           
            if (id_estado.equals("2")) {
                stock = "1";
            }
            String datos_4[] = {id_estado, id_cliente, iniciar_sesion.id_usuario_global, fecha_sistema, fecha_entrega, hora_entrega, fecha_devolucion, txt_descripcion.getText(), porcentajeDescuentoRenta,descuento, iva, txt_comentarios.getText(), id_chofer, folio, stock, id_tipo,hora_devolucion,fecha_evento,envioRecoleccion,depositoGarantia,mostrarPrecios};
            funcion.InsertarRegistro(datos_4, "insert into renta (id_estado,id_clientes,id_usuarios,fecha_pedido,fecha_entrega,hora_entrega,fecha_devolucion,descripcion,descuento,cantidad_descuento,iva,comentario,id_usuario_chofer,folio,stock,id_tipo,hora_devolucion,fecha_evento,envio_recoleccion,deposito_garantia,mostrar_precios_pdf) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            id_ultima_renta = funcion.ultimoid_renta();
            
            System.out.println("FOLIO: " + folio);
            aux = Integer.parseInt(folio) + 1;
            System.out.println("aux: " + aux);
            globalFolio = aux;
            String[] datos_3 = {String.valueOf(aux)};
            funcion.UpdateRegistro(datos_3, "update datos_generales set folio=?");

        }
        String porcentajeDescuento = null;
        for (int i = 0; i <= tabla_detalle.getRowCount() - 1; i++) {
            subTotal = subTotal + Float.parseFloat(EliminaCaracteres(tabla_detalle.getValueAt(i, 6).toString(), "$,"));
            porcentajeDescuento = EliminaCaracteres(tabla_detalle.getValueAt(i, 4).toString(), "$,");
            String p_unitario = EliminaCaracteres(tabla_detalle.getValueAt(i, 3).toString(), "$,");
            String datos_detalle[] = {id_ultima_renta, tabla_detalle.getValueAt(i, 0).toString(), tabla_detalle.getValueAt(i, 1).toString(), p_unitario, "0",porcentajeDescuento};
            funcion.InsertarRegistro(datos_detalle, "INSERT INTO detalle_renta (id_renta,cantidad,id_articulo,p_unitario,se_desconto,porcentaje_descuento) VALUES (?,?,?,?,?,?)");

        }
        // ABONOS
        if (tabla_abonos.getRowCount() != 0) {
            String abono;
            for (int i = 0; i <= tabla_abonos.getRowCount() - 1; i++) {
                cant_abono = cant_abono + Float.parseFloat(EliminaCaracteres(tabla_abonos.getValueAt(i, 1).toString(), "$,"));
                abono = EliminaCaracteres(tabla_abonos.getValueAt(i, 1).toString(), "$,");
                String tipoAbonoId = tabla_abonos.getValueAt(i, 3).toString();
                
                String datos_abonos[] = {id_ultima_renta, 
                    iniciar_sesion.id_usuario_global, 
                    fecha_sistema, abono, 
                    tabla_abonos.getValueAt(i, 2).toString(),tipoAbonoId,
                    tabla_abonos.getValueAt(i, 5).toString()
                };
                funcion.InsertarRegistro(datos_abonos, "INSERT INTO abonos "
                        + "(id_renta,id_usuario,fecha,abono,comentario,id_tipo_abono,fecha_pago) "
                        + "VALUES(?,?,?,?,?,?,?) ");

            }
        }
        
        if (check_generar_reporte.isSelected() == true)
            reporte();

        if (check_enviar_email.isSelected() == true){
            enviar_email();               
        }
        JOptionPane.showMessageDialog(null, "Pedido registrado con éxito... =)");
        String messageSuccess = iniciar_sesion.usuarioGlobal.getNombre()+ " registró un evento de tipo "+cmb_tipo.getSelectedItem().toString()
                +" con status "+cmb_estado.getSelectedItem().toString()+ ", id: "+id_ultima_renta;
        Utility.pushNotification(messageSuccess);
        log.info(messageSuccess);
        
        if (id_tipo.equals(ApplicationConstants.TIPO_PEDIDO) || id_tipo.equals(ApplicationConstants.TIPO_FABRICACION)) {
            new Thread(() -> {
                String message;
                try {
                    taskAlmacenUpdateService = TaskAlmacenUpdateService.getInstance();
                    message = taskAlmacenUpdateService.saveWhenIsNewEvent(Long.parseLong(id_ultima_renta), folio, iniciar_sesion.usuarioGlobal.getUsuarioId().toString());
                    log.info(message);
                } catch (NoDataFoundException e) {
                    message = e.getMessage();
                    log.error(message);
                } catch (DataOriginException e) {         
                    message = "Ocurrió un error al generar la tarea a almacén, id: "+id_ultima_renta +", DETALLE: "+e.getMessage();
                    log.error(e.getMessage(),e);
                }
                Utility.pushNotification(message);
            }).start();
            
            new Thread(() -> {
                String message;
                try {
                    taskDeliveryChoferUpdateService = TaskDeliveryChoferUpdateService.getInstance();
                    taskDeliveryChoferUpdateService.saveWhenIsNewEvent(Long.parseLong(id_ultima_renta), folio,id_chofer, iniciar_sesion.usuarioGlobal.getUsuarioId().toString());
                    message = String.format("Tarea 'entrega chofer' generada. Folio: %s, chofer: %s",folio,cmb_chofer.getSelectedItem());
                } catch (DataOriginException | NoDataFoundException e) {
                    message = e.getMessage();
                    log.error(message);
                }
                Utility.pushNotification(message);
            }).start();
        }

        seleccion = JOptionPane.showOptionDialog(this, "¿Deseas agregar otro evento?", "Evento nuevo ", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "No");
        
        
        if (seleccion == 0) {//presiono que si
            nuevo_evento();
            limpiar();
            jTabbedPane1.setEnabledAt(1, false);
            jTabbedPane1.setSelectedIndex(0);
            jbtn_disponible.setEnabled(false);
            jbtn_nuevo_evento.setEnabled(false);
            panel_articulos.setVisible(true);
            panel_conceptos.setVisible(false);
        } else { //presiono que no
            this.dispose();
        }

    }

    public void limpiar() {
        this.txt_nombre.setText("");
        txt_apellidos.setText("");
        txt_direccion.setText("");
        txt_apodo.setText("");
        txt_tel_movil.setText("");
        txt_tel_casa.setText("");
        txt_direccion.setText("");
        txt_localidad.setText("");
        txt_rfc.setText("");
        txt_email.setText("");

    }

    public void nuevo_evento() {
        cmb_chofer.setSelectedItem("-sel-");
        cmb_estado.setSelectedItem("Apartado");
        cmb_tipo.setSelectedItem("Pedido");
        cmb_hora.setSelectedItem("-sel-");
        cmb_hora_devolucion.setSelectedItem("-sel-");
        cmb_fecha_entrega.setDate(null);
        cmb_fecha_devolucion.setDate(null);
        txt_descripcion.setText("");
        txt_descuento.setText("0.0");
        txtPorcentajeDescuento.setText("");
        txt_abono.setText("");
        txt_comentarios.setText("");
        txt_comentario.setText("");
        txt_cantidad.setText("");
        txt_precio_unitario.setText("");
        check_mostrar_precios.setSelected(true);
        check_generar_reporte.setSelected(true);
        txt_envioRecoleccion.setText("");
        txt_depositoGarantia.setText("");
        txt_iva.setText("");
        txt_calculo.setText("");
        formato_tabla_detalles();
        formato_tabla_abonos();
        txt_abonos.setText("");
        abonos();
        subTotal();
        total();
    }

    public void subTotal() {
        String characterToDelete = "$,";
        String aux;
        float subTotal = 0F, total = 0;
        for (int i = 0; i < tabla_detalle.getRowCount(); i++) {
            Float amount = Float.parseFloat(EliminaCaracteres((tabla_detalle.getValueAt(i, 0).toString()), characterToDelete).toString());
            Float unitPrice = Float.parseFloat(EliminaCaracteres((tabla_detalle.getValueAt(i,3).toString()), characterToDelete).toString());
            Float discountRate = Float.parseFloat(EliminaCaracteres((tabla_detalle.getValueAt(i,4).toString()), characterToDelete).toString());
            Float discountTotal = 0F;
            Float importe = amount * unitPrice;
            if (discountRate > 0) {
                discountTotal = importe * (discountRate / 100);
                importe = importe - discountTotal;
            }
            // total descuento
            tabla_detalle.setValueAt(decimalFormat.format(discountTotal), i, 5);
            // importe
            tabla_detalle.setValueAt(decimalFormat.format(importe), i, 6);
            total = total + importe;
        }
        txt_subtotal.setValue(total);

    }

    public void abonos() {
        String aux;
        float abonos = 0;
        for (int i = 0; i < tabla_abonos.getRowCount(); i++) {
            aux = EliminaCaracteres(((String) tabla_abonos.getValueAt(i, 1).toString()), "$,");
            abonos = Float.parseFloat(aux) + abonos;

        }
        txt_abonos.setValue(abonos);

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

    private void customerTableFormat() {
        Object[][] data = {{"","","","","","","", "", ""}};
        String[] columNames = {"Id", "Cliente ", "Apodo", "Tel Cel", "Tel Fijo", "Email ", "Dirección", "Localidad", "RFC"};       

        
        DefaultTableModel tableModel = new DefaultTableModel(data, columNames);
        tabla_clientes.setModel(tableModel);
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tabla_clientes.setRowSorter(ordenarTabla);

        int[] anchos = {10, 190, 100, 80, 80, 200, 100, 80, 80};

        for (int inn = 0; inn < tabla_clientes.getColumnCount(); inn++) {
            tabla_clientes.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        tabla_clientes.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_clientes.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_clientes.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        try {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }

    }

    
    private void searchAndFillTableCustomers () {
        try {
            List<Cliente> filterCustomers =
                    customers.stream()
                            .filter(customer -> Objects.nonNull(customer))
                            .filter(customer -> Objects.nonNull(customer.getNombre()))
                            .filter(customer -> Objects.nonNull(customer.getApellidos()))
                            .filter(customer -> customer.getNombre().toLowerCase().trim().contains(txt_nombre.getText().toLowerCase().trim()))
                            .filter(customer -> customer.getApellidos().toLowerCase().trim().contains(txt_apellidos.getText().toLowerCase().trim()))
                            .collect(Collectors.toList());
            fillTableCustomers(filterCustomers);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.INFORMATION_MESSAGE);
            log.error(e.getMessage(),e);
        }
        
        
    }
    

    public void formato_tabla_detalles() {
        Object[][] data = {{"", "", "", "", "","","",""}};
        String[] columnNames = {"cantidad", "id_articulo", "Articulo", "P.Unitario","Descuento %","Descuento", "Importe","Utiles"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tabla_detalle.setModel(TableModel);

        int[] anchos = {40, 20, 220, 60,60,60, 60,20};

        for (int inn = 0; inn < tabla_detalle.getColumnCount(); inn++) {
            tabla_detalle.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            DefaultTableModel temp = (DefaultTableModel) tabla_detalle.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        tabla_detalle.getColumnModel().getColumn(1).setMaxWidth(0);
        tabla_detalle.getColumnModel().getColumn(1).setMinWidth(0);
        tabla_detalle.getColumnModel().getColumn(1).setPreferredWidth(0);
        tabla_detalle.getColumnModel().getColumn(0).setCellRenderer(centrar);
        tabla_detalle.getColumnModel().getColumn(3).setCellRenderer(centrar);
        tabla_detalle.getColumnModel().getColumn(4).setCellRenderer(centrar);
        tabla_detalle.getColumnModel().getColumn(5).setCellRenderer(centrar);
        tabla_detalle.getColumnModel().getColumn(6).setCellRenderer(centrar);
        tabla_detalle.getColumnModel().getColumn(2).setCellRenderer(centrar);
        tabla_detalle.getColumnModel().getColumn(7).setMaxWidth(0);
        tabla_detalle.getColumnModel().getColumn(7).setMinWidth(0);
        tabla_detalle.getColumnModel().getColumn(7).setPreferredWidth(0);

    }

    public void formato_tabla_abonos() {
        Object[][] data = {{"", "", "","","",""}};
        String[] columnNames = {"Fecha", "Pago", "Comentario","id_tipo_abono","Tipo pago","Fecha pago"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tabla_abonos.setModel(TableModel);

        int[] anchos = {80, 70, 100,60,80,90};

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
        tabla_abonos.getColumnModel().getColumn(3).setMaxWidth(0);
        tabla_abonos.getColumnModel().getColumn(3).setMinWidth(0);
        tabla_abonos.getColumnModel().getColumn(3).setPreferredWidth(0);  
        tabla_abonos.getColumnModel().getColumn(1).setCellRenderer(TablaRenderer);      
        tabla_abonos.getColumnModel().getColumn(0).setCellRenderer(centrar);

    }

    public void agregar_articulos() {
        if (txt_cantidad.getText().equals("") || txt_precio_unitario.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Faltan parametros para agregar al pedido ", "Error", JOptionPane.INFORMATION_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
        } else {
            float porcentajeDescuento = 0f;
            if(!this.txt_porcentaje_descuento.getText().equals("") ){
                try {
                    porcentajeDescuento = Float.parseFloat(this.txt_porcentaje_descuento.getText()+"");
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
                for (int i = 0; i < tabla_detalle.getRowCount(); i++) {
                    if (lbl_eleccion.getText().equals(tabla_detalle.getValueAt(i, 2).toString())) {
                        existe = true;
                        break;
                    }
                }
                if (existe == true) {
                    JOptionPane.showMessageDialog(null, "No se permiten articulos duplicados... ", "Error", JOptionPane.INFORMATION_MESSAGE);
                    Toolkit.getDefaultToolkit().beep();

                } else {
                    DefaultTableModel temp = (DefaultTableModel) tabla_detalle.getModel();
                    float cantidad = Float.parseFloat(txt_cantidad.getText().toString());
                    float precio = Float.parseFloat(txt_precio_unitario.getText().toString());
                    float importe = (cantidad * precio);
                    float totalDescuento = 0f;
                    if(porcentajeDescuento > 0){
                        totalDescuento = importe * (porcentajeDescuento / 100);
                        importe = importe - totalDescuento;
                    }

                    String xprecio = conviertemoneda(Float.toString(precio));
                    String ximporte = conviertemoneda(Float.toString(importe));

                    Object nuevo1[] = {txt_cantidad.getText().toString(), id_articulo, lbl_eleccion.getText(), xprecio,porcentajeDescuento+"", conviertemoneda(totalDescuento+"") , ximporte, itemUtilesGlobal};
                    temp.addRow(nuevo1);
                    subTotal();
                    total();
                    jbtn_disponible.setEnabled(true);
                    panel_conceptos.setVisible(true);
                    panel_articulos.setVisible(false);
                    jbtn_mostrar_articulos.setEnabled(true);
                    panel = true;
                    disableInputsEditItem();

                }

        }

    }

    public void agregar_abonos() {
        StringBuilder message = new StringBuilder();
        if (txt_abono.getText().equals("") || txt_abono.getText().equals("0"))
            message.append("Ingresa un valor para agregar\n");
        if( this.cmbTipoPago.getSelectedIndex() == 0)
            message.append("Selecciona un tipo de pago\n");
        
        if(!message.toString().equals("")){
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.INFORMATION_MESSAGE);
            Toolkit.getDefaultToolkit().beep();
            return;
        }
           
        String tipoAbonoId = funcion.GetData("id_tipo_abono", "SELECT id_tipo_abono FROM tipo_abono "
                + "WHERE descripcion='" + cmbTipoPago.getSelectedItem().toString() + "'");
            //{"cantidad","id_articulo", "P.Unitario", "Importe"};
            DefaultTableModel temp = (DefaultTableModel) tabla_abonos.getModel();
            float abono = 0f;
            try {         
                abono = Float.parseFloat(txt_abono.getText().toString());                          
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ingresa solo numeros ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String fechaPago = "";
            if(cmb_fecha_pago.getDate() != null && !cmb_fecha_pago.getDate().toString().equals(""))
                fechaPago = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_pago.getDate());
            
            String xabono = conviertemoneda(Float.toString(abono));
            fecha_sistema();
            Object nuevo1[] = {fecha_sistema, xabono, txt_comentario.getText().toString(),tipoAbonoId,cmbTipoPago.getSelectedItem().toString(),fechaPago};
            temp.addRow(nuevo1);
            abonos();
            subTotal();
            total();

            txt_abono.setText("0");
            txt_abono.requestFocus();

        

    }
    
    
    private void formatItemsTable () {
        
        tabla_articulos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id","Código", "Categoría", "Descripción", "Color", "P. Unitario","Utiles"};
        Object[][] data = {{"","","","","","",""}};
        

        DefaultTableModel tableModel = new DefaultTableModel(data, columNames);
        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tabla_articulos.setModel(tableModel);

        int[] anchos = {10,40, 120, 250, 100,90,20};

        for (int inn = 0; inn < tabla_articulos.getColumnCount(); inn++) {
            tabla_articulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        tabla_articulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_articulos.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_articulos.getColumnModel().getColumn(0).setPreferredWidth(0);
        tabla_articulos.getColumnModel().getColumn(5).setCellRenderer(centrar);
        tabla_articulos.getColumnModel().getColumn(6).setMaxWidth(0);
        tabla_articulos.getColumnModel().getColumn(6).setMinWidth(0);
        tabla_articulos.getColumnModel().getColumn(6).setPreferredWidth(0);
        
        try {
            tableModel.removeRow(tableModel.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
    }
    
    private void fillTableItems (List<Articulo> list) {
        formatItemsTable();
        DefaultTableModel tableModel = (DefaultTableModel) tabla_articulos.getModel();
            list.forEach(articulo -> {
                Object row[] = {
                    articulo.getArticuloId(),
                    articulo.getCodigo(),
                    articulo.getCategoria().getDescripcion(),
                    articulo.getDescripcion(),
                    articulo.getColor().getColor(),
                    articulo.getPrecioRenta(),
                    articulo.getUtiles()
                };
                tableModel.addRow(row);
            });
    }

    private void tabla_articulos() {
        
        if (!articulos.isEmpty()) {
            return;
        }

        try {
            articulos = itemService.obtenerArticulosActivos();
            fillTableItems(articulos);
        } catch (Exception e) {
            JOptionPane.showMessageDialog( this ,"Ocurrió un error grave al obtener los articulos de la base de datos"+e,"ERROR", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    public void tabla_articulos_like() {
        
        List<Articulo> filterItems =
                articulos.stream()
                    .filter(item -> Objects.nonNull(item))
                    .filter(item -> Objects.nonNull(item.getDescripcion()))
                    .filter(item -> Objects.nonNull(item.getColor()))
                    .filter(item -> 
                        (item.getDescripcion().toLowerCase().trim() + " " + item.getColor().getColor().toLowerCase().trim())
                                .contains(txt_buscar.getText().toLowerCase().trim()))
                .collect(Collectors.toList());
        
        fillTableItems(filterItems);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txt_nombre = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txt_apellidos = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txt_apodo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_tel_movil = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_tel_casa = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txt_email = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_direccion = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt_localidad = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_rfc = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_clientes = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel2 = new javax.swing.JPanel();
        panel_datos_generales = new javax.swing.JPanel();
        lbl_cliente = new javax.swing.JLabel();
        lbl_atiende = new javax.swing.JLabel();
        cmb_fecha_entrega = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cmb_fecha_devolucion = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cmb_estado = new javax.swing.JComboBox();
        txt_subtotal = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txt_descuento = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        txtPorcentajeDescuento = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txt_abonos = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        txt_total = new javax.swing.JFormattedTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        txt_descripcion = new javax.swing.JTextPane();
        txt_total_iva = new javax.swing.JFormattedTextField();
        cmb_chofer = new javax.swing.JComboBox();
        jLabel26 = new javax.swing.JLabel();
        cmb_tipo = new javax.swing.JComboBox();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        cmb_hora_devolucion = new javax.swing.JComboBox();
        cmb_hora_dos = new javax.swing.JComboBox();
        cmb_hora = new javax.swing.JComboBox();
        jLabel29 = new javax.swing.JLabel();
        cmb_hora_devolucion_dos = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        cmb_fecha_evento = new com.toedter.calendar.JDateChooser();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txt_envioRecoleccion = new javax.swing.JFormattedTextField();
        jLabel35 = new javax.swing.JLabel();
        txt_depositoGarantia = new javax.swing.JFormattedTextField();
        jLabel36 = new javax.swing.JLabel();
        txt_calculo = new javax.swing.JFormattedTextField();
        txt_iva = new javax.swing.JFormattedTextField();
        check_generar_reporte = new javax.swing.JCheckBox();
        check_enviar_email = new javax.swing.JCheckBox();
        check_mostrar_precios = new javax.swing.JCheckBox();
        txtEmailToSend = new javax.swing.JTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        panel_articulos = new javax.swing.JPanel();
        txt_cantidad = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txt_precio_unitario = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txt_buscar = new javax.swing.JTextField();
        lbl_eleccion = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabla_articulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        txt_porcentaje_descuento = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        btnGetItemsFromFolio = new javax.swing.JButton();
        panel_conceptos = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabla_detalle = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        lbl_sel = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtAmountEdit = new javax.swing.JTextField();
        txtUnitPriceEdit = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        txtDiscountRateEdit = new javax.swing.JTextField();
        jToolBar3 = new javax.swing.JToolBar();
        jbtn_agregar_articulo = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jbtn_disponible = new javax.swing.JButton();
        jbtn_mostrar_articulos = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabla_abonos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel10 = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        jbtn_agregar_abono = new javax.swing.JButton();
        jbtn_quitar_abono = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        txt_comentario = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txt_abono = new javax.swing.JTextField();
        lblTipoAbono = new javax.swing.JLabel();
        cmbTipoPago = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        cmb_fecha_pago = new com.toedter.calendar.JDateChooser();
        jLabel40 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        txt_comentarios = new javax.swing.JTextPane();
        jToolBar2 = new javax.swing.JToolBar();
        jbtn_agregar_evento = new javax.swing.JButton();
        jbtn_reporte = new javax.swing.JButton();
        jbtn_nuevo_evento = new javax.swing.JButton();

        setClosable(true);
        setResizable(true);
        setTitle("AGREGAR UNA RENTA O PEDIDO....");

        jTabbedPane1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Document-Add-icon.png"))); // NOI18N
        jButton1.setMnemonic('N');
        jButton1.setToolTipText("Agrega el cliente (Alt+N)");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ingresa nuevo cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        txt_nombre.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_nombreKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_nombreKeyReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel1.setText("Nombre:");

        txt_apellidos.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_apellidos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_apellidosKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel2.setText("Apellidos:");

        txt_apodo.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel3.setText("Apodo:");

        txt_tel_movil.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel4.setText("Tel Movil:");

        txt_tel_casa.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel5.setText("Tel Casa:");

        txt_email.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel6.setText("Email:");

        txt_direccion.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel7.setText("Direccion:");

        txt_localidad.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel8.setText("Localidad:");

        jLabel9.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel9.setText("RFC:");

        txt_rfc.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_email)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_apellidos))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txt_apodo, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(txt_tel_movil, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_tel_casa))
                    .addComponent(txt_direccion)
                    .addComponent(txt_localidad)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_rfc, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 12, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_apellidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_apodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_tel_movil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_tel_casa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_localidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_rfc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Clientes en la base de datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        tabla_clientes.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
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
        tabla_clientes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabla_clientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_clientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabla_clientes);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 743, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Elige el cliente", jPanel1);

        panel_datos_generales.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos generales", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N
        panel_datos_generales.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_cliente.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(lbl_cliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 20, 220, 20));

        lbl_atiende.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbl_atiende.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        panel_datos_generales.add(lbl_atiende, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 40, 220, 20));

        cmb_fecha_entrega.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(cmb_fecha_entrega, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 210, 21));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Fecha de entrega:");
        panel_datos_generales.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 100, 21));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Hora entrega:");
        panel_datos_generales.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 80, 20));

        cmb_fecha_devolucion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(cmb_fecha_devolucion, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 210, 21));

        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Fecha del evento:");
        panel_datos_generales.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 100, 20));

        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Direccion del evento:");
        panel_datos_generales.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 230, -1));

        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Estado:");
        panel_datos_generales.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 140, -1, 23));

        cmb_estado.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_estado.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_estado.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(cmb_estado, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 140, 190, -1));

        txt_subtotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_subtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_subtotal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(txt_subtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 20, 120, -1));

        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Subtotal:");
        panel_datos_generales.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 20, 69, 20));

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
        panel_datos_generales.add(txt_descuento, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 40, 70, -1));

        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Descuento % :");
        panel_datos_generales.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 40, 110, 20));

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
        panel_datos_generales.add(txtPorcentajeDescuento, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 40, 40, -1));

        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("IVA % :");
        panel_datos_generales.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 100, 69, 20));

        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Pagos:");
        panel_datos_generales.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 140, 69, 20));

        txt_abonos.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_abonos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_abonos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(txt_abonos, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 140, 120, -1));

        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Total:");
        panel_datos_generales.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 160, 69, 20));

        txt_total.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_total.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_total.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_totalActionPerformed(evt);
            }
        });
        panel_datos_generales.add(txt_total, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 160, 120, -1));

        txt_descripcion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jScrollPane3.setViewportView(txt_descripcion);

        panel_datos_generales.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 40, 230, 90));

        txt_total_iva.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_total_iva.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_total_iva.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(txt_total_iva, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 100, 70, -1));

        cmb_chofer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_chofer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_chofer.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(cmb_chofer, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 210, -1));

        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Chofer:");
        panel_datos_generales.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 110, 20));

        cmb_tipo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_tipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_tipo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(cmb_tipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 170, 190, -1));

        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Tipo:");
        panel_datos_generales.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 170, 40, 20));

        jLabel28.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel28.setText("Hora devolución:");
        panel_datos_generales.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 100, 20));

        cmb_hora_devolucion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_hora_devolucion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-sel-", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
        cmb_hora_devolucion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(cmb_hora_devolucion, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 170, 60, -1));

        cmb_hora_dos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_hora_dos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-sel-", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
        cmb_hora_dos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(cmb_hora_dos, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 140, -1, -1));

        cmb_hora.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_hora.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-sel-", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
        cmb_hora.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(cmb_hora, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, 60, -1));

        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("a");
        panel_datos_generales.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 170, 20, 30));

        cmb_hora_devolucion_dos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmb_hora_devolucion_dos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-sel-", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
        cmb_hora_devolucion_dos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(cmb_hora_devolucion_dos, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 170, -1, -1));

        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("a");
        panel_datos_generales.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, 20, 30));

        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Fecha de devolución:");
        panel_datos_generales.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 120, 20));

        cmb_fecha_evento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        panel_datos_generales.add(cmb_fecha_evento, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, 210, 21));

        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Hrs.");
        panel_datos_generales.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 170, -1, -1));

        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Hrs.");
        panel_datos_generales.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 140, -1, -1));

        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Envío y recolección");
        panel_datos_generales.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 60, 110, 20));

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
        panel_datos_generales.add(txt_envioRecoleccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 60, 120, -1));

        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Depósito en garantía");
        panel_datos_generales.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(829, 80, 110, 20));

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
        panel_datos_generales.add(txt_depositoGarantia, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 80, 120, -1));

        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Calculo:");
        panel_datos_generales.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 120, 69, 20));

        txt_calculo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_calculo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_calculo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_calculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_calculoActionPerformed(evt);
            }
        });
        panel_datos_generales.add(txt_calculo, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 120, 120, -1));

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
        panel_datos_generales.add(txt_iva, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 100, 40, -1));

        check_generar_reporte.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        check_generar_reporte.setText("Generar reporte al guardar");
        check_generar_reporte.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        check_generar_reporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_generar_reporteActionPerformed(evt);
            }
        });
        panel_datos_generales.add(check_generar_reporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 150, 220, 20));

        check_enviar_email.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        check_enviar_email.setText("Enviar email confirmación");
        check_enviar_email.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        check_enviar_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_enviar_emailActionPerformed(evt);
            }
        });
        panel_datos_generales.add(check_enviar_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 110, 220, -1));

        check_mostrar_precios.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        check_mostrar_precios.setText("Mostrar precios en PDF");
        check_mostrar_precios.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(check_mostrar_precios, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 130, 220, 20));

        txtEmailToSend.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txtEmailToSend.setToolTipText("Para enviar multiples correos deberas separarlos por punto y coma [;]");
        txtEmailToSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailToSendActionPerformed(evt);
            }
        });
        panel_datos_generales.add(txtEmailToSend, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 170, 220, -1));

        jTabbedPane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel_articulos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Elije un servicio", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        txt_cantidad.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_cantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_cantidadKeyPressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Cantidad:");

        txt_precio_unitario.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_precio_unitario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_precio_unitarioKeyPressed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Precio:");

        txt_buscar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_buscarActionPerformed(evt);
            }
        });
        txt_buscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_buscarKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_buscarKeyReleased(evt);
            }
        });

        lbl_eleccion.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel23.setText("Elección:");

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
        tabla_articulos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabla_articulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_articulosMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabla_articulos);

        txt_porcentaje_descuento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_porcentaje_descuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_porcentaje_descuentoActionPerformed(evt);
            }
        });
        txt_porcentaje_descuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_porcentaje_descuentoKeyPressed(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Descuento %");

        btnGetItemsFromFolio.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnGetItemsFromFolio.setText("Obtener articulos de un folio");
        btnGetItemsFromFolio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGetItemsFromFolio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetItemsFromFolioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_articulosLayout = new javax.swing.GroupLayout(panel_articulos);
        panel_articulos.setLayout(panel_articulosLayout);
        panel_articulosLayout.setHorizontalGroup(
            panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_articulosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 981, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel_articulosLayout.createSequentialGroup()
                        .addGroup(panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_articulosLayout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbl_eleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel_articulosLayout.createSequentialGroup()
                                .addComponent(txt_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnGetItemsFromFolio)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_precio_unitario, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_porcentaje_descuento, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(113, 113, 113))
        );
        panel_articulosLayout.setVerticalGroup(
            panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_articulosLayout.createSequentialGroup()
                .addGroup(panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panel_articulosLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_porcentaje_descuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_precio_unitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10))
                    .addGroup(panel_articulosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGetItemsFromFolio))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_eleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.add(panel_articulos, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 0, 1010, 380));

        panel_conceptos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Conceptos del evento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        tabla_detalle.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
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
        tabla_detalle.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabla_detalle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_detalleMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabla_detalle);

        lbl_sel.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Cantidad:");

        txtAmountEdit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtAmountEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAmountEditActionPerformed(evt);
            }
        });
        txtAmountEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAmountEditKeyPressed(evt);
            }
        });

        txtUnitPriceEdit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtUnitPriceEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUnitPriceEditKeyPressed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("Precio:");

        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Descuento %:");

        txtDiscountRateEdit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtDiscountRateEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDiscountRateEditKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout panel_conceptosLayout = new javax.swing.GroupLayout(panel_conceptos);
        panel_conceptos.setLayout(panel_conceptosLayout);
        panel_conceptosLayout.setHorizontalGroup(
            panel_conceptosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_conceptosLayout.createSequentialGroup()
                .addGroup(panel_conceptosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 908, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel_conceptosLayout.createSequentialGroup()
                        .addComponent(lbl_sel, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel22)
                        .addGap(15, 15, 15)
                        .addComponent(txtAmountEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jLabel38)
                        .addGap(7, 7, 7)
                        .addComponent(txtUnitPriceEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiscountRateEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 90, Short.MAX_VALUE))
        );
        panel_conceptosLayout.setVerticalGroup(
            panel_conceptosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_conceptosLayout.createSequentialGroup()
                .addGroup(panel_conceptosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_sel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel_conceptosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtAmountEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtUnitPriceEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDiscountRateEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE))
        );

        jPanel6.add(panel_conceptos, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 0, 1010, 387));

        jToolBar3.setFloatable(false);
        jToolBar3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar3.setRollover(true);

        jbtn_agregar_articulo.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_agregar_articulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/shop-cart-down-icon_32.png"))); // NOI18N
        jbtn_agregar_articulo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_agregar_articulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_agregar_articuloActionPerformed(evt);
            }
        });
        jToolBar3.add(jbtn_agregar_articulo);

        jButton2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar_dinero_32.png"))); // NOI18N
        jButton2.setToolTipText("Editar precio");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar3.add(jButton2);

        jButton3.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/folder-remove-icon.png"))); // NOI18N
        jButton3.setToolTipText("Quitar elemento");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
        jbtn_disponible.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
        jbtn_mostrar_articulos.setMnemonic('Q');
        jbtn_mostrar_articulos.setToolTipText("Cambiar panel (Alt+Q)");
        jbtn_mostrar_articulos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_mostrar_articulos.setFocusable(false);
        jbtn_mostrar_articulos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_mostrar_articulos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_mostrar_articulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_mostrar_articulosActionPerformed(evt);
            }
        });
        jToolBar3.add(jbtn_mostrar_articulos);

        jPanel6.add(jToolBar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 39, 370));

        jTabbedPane2.addTab("Detalle conceptos", jPanel6);

        jPanel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel8MouseClicked(evt);
            }
        });

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pagos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

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
        tabla_abonos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_abonosMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tabla_abonos);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ingresa el abono", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        jToolBar4.setFloatable(false);
        jToolBar4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar4.setRollover(true);

        jbtn_agregar_abono.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_agregar_abono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Actions-arrow-right-icon.png"))); // NOI18N
        jbtn_agregar_abono.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_agregar_abono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_agregar_abonoActionPerformed(evt);
            }
        });
        jToolBar4.add(jbtn_agregar_abono);

        jbtn_quitar_abono.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_quitar_abono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/folder-remove-icon.png"))); // NOI18N
        jbtn_quitar_abono.setToolTipText("Quitar elemento");
        jbtn_quitar_abono.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_quitar_abono.setFocusable(false);
        jbtn_quitar_abono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_quitar_abono.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_quitar_abono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_quitar_abonoActionPerformed(evt);
            }
        });
        jToolBar4.add(jbtn_quitar_abono);

        jLabel24.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel24.setText("Pago:");

        txt_comentario.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel25.setText("Comentario:");

        txt_abono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_abonoKeyPressed(evt);
            }
        });

        lblTipoAbono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lblTipoAbono.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblTipoAbono.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTipoAbonoMouseClicked(evt);
            }
        });
        lblTipoAbono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblTipoAbonoKeyPressed(evt);
            }
        });

        cmbTipoPago.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbTipoPago.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbTipoPago.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("Tipo de pago:");

        cmb_fecha_pago.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel40.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel40.setText("Fecha del pago:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txt_abono, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addComponent(jLabel39)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                                    .addComponent(lblTipoAbono))
                                .addComponent(txt_comentario, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cmbTipoPago, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(cmb_fecha_pago, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addGap(10, 10, 10)
                        .addComponent(cmbTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel40)
                        .addGap(7, 7, 7)
                        .addComponent(cmb_fecha_pago, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTipoAbono))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Pagos", jPanel8);

        txt_comentarios.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jScrollPane6.setViewportView(txt_comentarios);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1060, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(193, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Comentarios", jPanel11);

        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar2.setRollover(true);

        jbtn_agregar_evento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Misc-New-Database-icon_32.png"))); // NOI18N
        jbtn_agregar_evento.setMnemonic('G');
        jbtn_agregar_evento.setToolTipText("Guarda en la base de datos (Alt+G)");
        jbtn_agregar_evento.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_agregar_evento.setFocusable(false);
        jbtn_agregar_evento.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_agregar_evento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_agregar_evento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_agregar_eventoActionPerformed(evt);
            }
        });
        jToolBar2.add(jbtn_agregar_evento);

        jbtn_reporte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/blank-catalog-icon.png"))); // NOI18N
        jbtn_reporte.setMnemonic('R');
        jbtn_reporte.setToolTipText("Generar reporte (Alt+R)");
        jbtn_reporte.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_reporte.setFocusable(false);
        jbtn_reporte.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_reporte.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_reporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_reporteActionPerformed(evt);
            }
        });
        jToolBar2.add(jbtn_reporte);

        jbtn_nuevo_evento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Document-Add-icon.png"))); // NOI18N
        jbtn_nuevo_evento.setToolTipText("Nuevo evento");
        jbtn_nuevo_evento.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_nuevo_evento.setFocusable(false);
        jbtn_nuevo_evento.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_nuevo_evento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_nuevo_evento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_nuevo_eventoActionPerformed(evt);
            }
        });
        jbtn_nuevo_evento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtn_nuevo_eventoKeyPressed(evt);
            }
        });
        jToolBar2.add(jbtn_nuevo_evento);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(panel_datos_generales, javax.swing.GroupLayout.PREFERRED_SIZE, 1075, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(panel_datos_generales, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(151, 151, 151))
        );

        jTabbedPane1.addTab("Conceptos", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 677, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        // boolean res = agregar_cliente();
        if (agregar_cliente() == true) {
            jTabbedPane1.setSelectedIndex(1);
            jTabbedPane1.setEnabledAt(1, true);
            this.lbl_cliente.setText("Cliente: "+this.txt_nombre.getText() + " " + this.txt_apellidos.getText());
            limpiar();
            tabla_articulos();
            jbtn_disponible.setEnabled(false);
            jbtn_reporte.setEnabled(false);
            jbtn_nuevo_evento.setEnabled(false);

            if (txt_email.getText().equals("")) {
                check_enviar_email.setSelected(false);
            } else if (xemail != null && xemail > 0) {
                check_enviar_email.setSelected(true);

            }

        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void tabla_clientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_clientesMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            email_cliente = tabla_clientes.getValueAt(tabla_clientes.getSelectedRow(), 5).toString();
            System.out.println("Email es: " + email_cliente.toString());
            this.txtEmailToSend.setText(email_cliente);
            if (email_cliente.equals("")) {
                check_enviar_email.setSelected(false);
//                check_adjuntarPDF.setSelected(false);
            } else if (xemail != null && xemail > 0) {
                this.txtEmailToSend.setText(email_cliente);
                check_enviar_email.setSelected(true);
//                check_adjuntarPDF.setSelected(true);
            }
            jTabbedPane1.setSelectedIndex(1);
            jTabbedPane1.setEnabledAt(1, true);
            jbtn_agregar_evento.setEnabled(true);
            panel_articulos.setVisible(true);
            panel_conceptos.setVisible(false);

            id_cliente = tabla_clientes.getValueAt(tabla_clientes.getSelectedRow(), 0).toString();
            //lbl_aviso.setText("Has elegido al cliente con exito.. Continua con los datos del evento...");
            this.lbl_cliente.setText("Cliente: "+ tabla_clientes.getValueAt(tabla_clientes.getSelectedRow(), 1) + " " + (String) tabla_clientes.getValueAt(tabla_clientes.getSelectedRow(), 2));

            tabla_articulos();
        }
    }//GEN-LAST:event_tabla_clientesMouseClicked

    private void tabla_detalleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_detalleMouseClicked
        // TODO add your handling code here:
        String itemSelected = (tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 2)).toString();
        String characterToDelete = "$,";
        lbl_sel.setText(itemSelected);
        if (evt.getClickCount() == 2) {
            rowSelectedToEdit = tabla_detalle.getSelectedRow();
            Float amount = Float.parseFloat(EliminaCaracteres(tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 0).toString(),characterToDelete));
            Float price = Float.parseFloat(EliminaCaracteres(tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 3).toString(),characterToDelete));
            Float discountRate = Float.parseFloat(EliminaCaracteres(tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 4).toString(),characterToDelete));
            
            txtAmountEdit.setText(amount.toString());
            txtDiscountRateEdit.setText(discountRate.toString());
            txtUnitPriceEdit.setText(price.toString());
            enabledInputsEditItem();
            
        }
    }//GEN-LAST:event_tabla_detalleMouseClicked

    private void jbtn_agregar_articuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_agregar_articuloActionPerformed
        // TODO add your handling code here:
        agregar_articulos();
    }//GEN-LAST:event_jbtn_agregar_articuloActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if (iniciar_sesion.administrador_global.equals("1")) {
            txt_precio_unitario.setEditable(true);
            txt_precio_unitario.requestFocus();
            JOptionPane.showMessageDialog(null, "Puedes modificar el precio...", "Precio", JOptionPane.INFORMATION_MESSAGE);

            Toolkit.getDefaultToolkit().beep();

        } else {
            JOptionPane.showMessageDialog(null, "No cuenta con permisos... ", "Error", JOptionPane.INFORMATION_MESSAGE);

            Toolkit.getDefaultToolkit().beep();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        try {
            if (tabla_detalle.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione un elemento de la tabla para quitar... ", "Error", JOptionPane.INFORMATION_MESSAGE);

                Toolkit.getDefaultToolkit().beep();
            } else {
                DefaultTableModel temp = (DefaultTableModel) tabla_detalle.getModel();
                temp.removeRow(tabla_detalle.getSelectedRow());
                subTotal();
                total();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
    }//GEN-LAST:event_jButton3ActionPerformed

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

    private void txt_descuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_descuentoFocusLost
        // TODO add your handling code here:
        if (txt_descuento.getText().equals("")) {
            txt_descuento.setText("0");
        }
    }//GEN-LAST:event_txt_descuentoFocusLost

    private void txtPorcentajeDescuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoFocusLost
        // TODO add your handling code here:
        if (txtPorcentajeDescuento.getText().equals("")) {
            txtPorcentajeDescuento.setText("0");
        }
    }//GEN-LAST:event_txtPorcentajeDescuentoFocusLost

    private void txt_nombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nombreKeyReleased
        searchAndFillTableCustomers();
    }//GEN-LAST:event_txt_nombreKeyReleased

    private void txt_apellidosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_apellidosKeyReleased
        searchAndFillTableCustomers();
    }//GEN-LAST:event_txt_apellidosKeyReleased

    private void tabla_abonosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_abonosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tabla_abonosMouseClicked

    private void jbtn_agregar_abonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_agregar_abonoActionPerformed
        // TODO add your handling code here:
        agregar_abonos();
    }//GEN-LAST:event_jbtn_agregar_abonoActionPerformed

    private void jbtn_quitar_abonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_quitar_abonoActionPerformed
        // TODO add your handling code here:
        try {
            if (tabla_abonos.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Selecciona un elemento de la tabla para quitar... ", "Error", JOptionPane.INFORMATION_MESSAGE);

                Toolkit.getDefaultToolkit().beep();
            } else {
                DefaultTableModel temp = (DefaultTableModel) tabla_abonos.getModel();
                temp.removeRow(tabla_abonos.getSelectedRow());
                abonos();
                subTotal();
                total();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
    }//GEN-LAST:event_jbtn_quitar_abonoActionPerformed

    private void jbtn_agregar_eventoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_agregar_eventoActionPerformed
        // TODO add your handling code here:
        renta();

    }//GEN-LAST:event_jbtn_agregar_eventoActionPerformed

    private void jbtn_reporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_reporteActionPerformed
        // TODO add your handling code here:
        reporte();
    }//GEN-LAST:event_jbtn_reporteActionPerformed

    private void jbtn_disponibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_disponibleActionPerformed
        // TODO add your handling code here:
        mostrar_disponibilidad();
    }//GEN-LAST:event_jbtn_disponibleActionPerformed

    private void jbtn_nuevo_eventoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_nuevo_eventoActionPerformed
        // TODO add your handling code here:
        nuevo_evento();
        limpiar();
        jTabbedPane1.setEnabledAt(1, false);
        jTabbedPane1.setSelectedIndex(0);
        jbtn_disponible.setEnabled(false);
    }//GEN-LAST:event_jbtn_nuevo_eventoActionPerformed

    public void mostrar_tipos_abonos() {
        TiposAbonosForm ventana_tipos_abonos = new TiposAbonosForm(null, true);
        ventana_tipos_abonos.setVisible(true);
        ventana_tipos_abonos.setLocationRelativeTo(null);
    }
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

    private void txt_totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_totalActionPerformed

    private void txt_abonoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_abonoKeyPressed
        if (evt.getKeyCode() == 10)
         agregar_abonos();
    }//GEN-LAST:event_txt_abonoKeyPressed

    private void jPanel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MouseClicked
        // TODO add your handling code here:
        txt_abono.requestFocus();
    }//GEN-LAST:event_jPanel8MouseClicked

    private void txt_envioRecoleccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_envioRecoleccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_envioRecoleccionActionPerformed

    private void txt_depositoGarantiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_depositoGarantiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_depositoGarantiaActionPerformed

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

    private void txt_calculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_calculoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_calculoActionPerformed

    private void txt_ivaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ivaFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_ivaFocusLost

    private void txt_ivaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ivaKeyPressed
        if (evt.getKeyCode() == 10) {
            if (txt_subtotal.getText().toString().equals("")) {
                JOptionPane.showMessageDialog(null, "Es necesario incluir subtotal para realizar el calculo", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                Toolkit.getDefaultToolkit().beep();

            } else {
                total();               
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_txt_ivaKeyPressed

    private void jbtn_nuevo_eventoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtn_nuevo_eventoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbtn_nuevo_eventoKeyPressed

    private void check_generar_reporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_generar_reporteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_check_generar_reporteActionPerformed

    private void lblTipoAbonoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTipoAbonoMouseClicked
        // TODO add your handling code here:
        if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
            JOptionPane.showMessageDialog(null, "No cuentas con suficientes permisos para esta accion ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        mostrar_tipos_abonos();
        if (validad_tipo_abonos == true) {
            this.llenar_abonos();            
            validad_tipo_abonos = false;
        }
    }//GEN-LAST:event_lblTipoAbonoMouseClicked

    private void lblTipoAbonoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblTipoAbonoKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_lblTipoAbonoKeyPressed

    private void check_enviar_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_enviar_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_check_enviar_emailActionPerformed

    private void txtEmailToSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailToSendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailToSendActionPerformed

    private void txt_porcentaje_descuentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_porcentaje_descuentoKeyPressed
        if (evt.getKeyCode() == 10) {
            agregar_articulos();

        }
    }//GEN-LAST:event_txt_porcentaje_descuentoKeyPressed

    private void txt_porcentaje_descuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_porcentaje_descuentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_porcentaje_descuentoActionPerformed

    private void tabla_articulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_articulosMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

            lbl_eleccion.setText((String) tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 3).toString() + " " + (String) tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 4).toString());
            txt_precio_unitario.setText(EliminaCaracteres((String) tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 5).toString(), "$,"));
            txt_cantidad.setText("");
            txt_cantidad.requestFocus();
            this.txt_porcentaje_descuento.setText("");
            id_articulo = (String) tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString();
            itemUtilesGlobal = tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 6).toString();
            txt_precio_unitario.setEditable(false);

        }
    }//GEN-LAST:event_tabla_articulosMouseClicked

    private void txt_buscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscarKeyReleased
        // TODO add your handling code here:
        tabla_articulos_like();
    }//GEN-LAST:event_txt_buscarKeyReleased

    private void txt_buscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_buscarKeyPressed

    private void txt_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_buscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_buscarActionPerformed

    private void txt_precio_unitarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_precio_unitarioKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            agregar_articulos();

        }
    }//GEN-LAST:event_txt_precio_unitarioKeyPressed

    private void txt_cantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cantidadKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            agregar_articulos();

        }
    }//GEN-LAST:event_txt_cantidadKeyPressed

    private void btnGetItemsFromFolioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetItemsFromFolioActionPerformed
        String folio = JOptionPane.showInputDialog("Ingresa el folio");
        if (folio == null) {
            return;
        }
        
        try {
            Renta renta = saleService.obtenerRentaPorFolio(Integer.parseInt(folio));
            if (renta == null) {
                JOptionPane.showMessageDialog(null, "No se encontró el evento con el folio ingresado ", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (renta.getDetalleRenta().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se encontraron articulos en el evento con el folio ingresado ", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            fillTableFromItemsFolio(renta);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Folio no válido, ingresa un número válido para continuar ", "ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error inesperado "+ e, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGetItemsFromFolioActionPerformed

    private void txtAmountEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmountEditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAmountEditActionPerformed

    private void editItemTable () {
        
        if (rowSelectedToEdit == null) {
            return;
        }
        try {
            
            if (txtAmountEdit.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Cantidad es requerida", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtUnitPriceEdit.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Precio es requerido", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Float amount = Float.parseFloat(txtAmountEdit.getText());
            Float price = Float.parseFloat(txtUnitPriceEdit.getText());
            Float discountRate = Float.parseFloat(txtDiscountRateEdit.getText().isEmpty() ? "0" : txtDiscountRateEdit.getText());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "Ingresa una cantidad mayor a cero.", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (price < 0) {
                JOptionPane.showMessageDialog(null, "Ingresa un precio mayor a cero.", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (discountRate < 0) {
                JOptionPane.showMessageDialog(null, "Ingresa un porcentaje de descuento mayor a cero.", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (discountRate > 100) {
                JOptionPane.showMessageDialog(null, "Ingresa un porcentaje de descuento menor a cien.", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            tabla_detalle.setValueAt(amount, rowSelectedToEdit , 0);
            tabla_detalle.setValueAt(decimalFormat.format(price), rowSelectedToEdit , 3);
            tabla_detalle.setValueAt(discountRate, rowSelectedToEdit , 4);
            
            subTotal();
            total();
            
            rowSelectedToEdit = null;
            
            disableInputsEditItem();
            
        } catch (NumberFormatException e) {
           JOptionPane.showMessageDialog(null, "Número invalido, revisa que las cantidades ingresadas sean solo números y mayores a cero ", "ERROR", JOptionPane.ERROR_MESSAGE); 
        }
    }
    private void disableInputsEditItem () {
        txtAmountEdit.setText("");
        txtUnitPriceEdit.setText("");
        txtDiscountRateEdit.setText("");
            
        txtAmountEdit.setEnabled(false);
        txtUnitPriceEdit.setEnabled(false);
        txtDiscountRateEdit.setEnabled(false);
    }
    private void enabledInputsEditItem () {
      
        txtAmountEdit.setEnabled(true);
        txtUnitPriceEdit.setEnabled(true);
        txtDiscountRateEdit.setEnabled(true);
    }
    private void txtAmountEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAmountEditKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            editItemTable();
        }
    }//GEN-LAST:event_txtAmountEditKeyPressed

    private void txtUnitPriceEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitPriceEditKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            editItemTable();

        }
    }//GEN-LAST:event_txtUnitPriceEditKeyPressed

    private void txtDiscountRateEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiscountRateEditKeyPressed
        if (evt.getKeyCode() == 10) {
            editItemTable();

        }
    }//GEN-LAST:event_txtDiscountRateEditKeyPressed

    private void txt_nombreKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nombreKeyPressed
        
    }//GEN-LAST:event_txt_nombreKeyPressed

    private void fillTableFromItemsFolio (Renta renta) {
        DefaultTableModel tableModel = (DefaultTableModel) tabla_detalle.getModel();
        
        renta.getDetalleRenta().stream().forEach(detail -> {
            
            // verificar duplicados
            for (int i = 0; i < tabla_detalle.getRowCount(); i++) {
                if ( (detail.getArticulo().getArticuloId()+"").equals(tabla_detalle.getValueAt(i, 1).toString())) {
                    return;
                }
            }
            
            Float cantidad = detail.getCantidad();
            Float precio = detail.getPrecioUnitario();
            Float importe = (cantidad * precio);
            Float totalDescuento = 0F;
            
            if (detail.getPorcentajeDescuento() > 0) {
                totalDescuento = importe * (detail.getPorcentajeDescuento() / 100);
                importe = importe - totalDescuento;
            }
            
            Object fila[] = {                                          
                cantidad,
                detail.getArticulo().getArticuloId(),
                detail.getArticulo().getDescripcion() + " " + detail.getArticulo().getColor().getColor(),
                decimalFormat.format(precio),
                detail.getPorcentajeDescuento(),
                decimalFormat.format(totalDescuento),
                decimalFormat.format(importe)
              };
              tableModel.addRow(fila);
        });
        subTotal();
        total();
        jbtn_disponible.setEnabled(true);
        panel_conceptos.setVisible(true);
        panel_articulos.setVisible(false);
        jbtn_mostrar_articulos.setEnabled(true);
        panel = true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGetItemsFromFolio;
    private javax.swing.JCheckBox check_enviar_email;
    private javax.swing.JCheckBox check_generar_reporte;
    private javax.swing.JCheckBox check_mostrar_precios;
    private javax.swing.JComboBox cmbTipoPago;
    private javax.swing.JComboBox cmb_chofer;
    private javax.swing.JComboBox cmb_estado;
    private com.toedter.calendar.JDateChooser cmb_fecha_devolucion;
    private com.toedter.calendar.JDateChooser cmb_fecha_entrega;
    private com.toedter.calendar.JDateChooser cmb_fecha_evento;
    private com.toedter.calendar.JDateChooser cmb_fecha_pago;
    private javax.swing.JComboBox cmb_hora;
    private javax.swing.JComboBox cmb_hora_devolucion;
    private javax.swing.JComboBox cmb_hora_devolucion_dos;
    private javax.swing.JComboBox cmb_hora_dos;
    private javax.swing.JComboBox cmb_tipo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
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
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
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
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JButton jbtn_agregar_abono;
    private javax.swing.JButton jbtn_agregar_articulo;
    private javax.swing.JButton jbtn_agregar_evento;
    private javax.swing.JButton jbtn_disponible;
    private javax.swing.JButton jbtn_mostrar_articulos;
    private javax.swing.JButton jbtn_nuevo_evento;
    private javax.swing.JButton jbtn_quitar_abono;
    private javax.swing.JButton jbtn_reporte;
    private javax.swing.JLabel lblTipoAbono;
    private javax.swing.JLabel lbl_atiende;
    private javax.swing.JLabel lbl_cliente;
    private javax.swing.JLabel lbl_eleccion;
    private javax.swing.JLabel lbl_sel;
    private javax.swing.JPanel panel_articulos;
    private javax.swing.JPanel panel_conceptos;
    private javax.swing.JPanel panel_datos_generales;
    private javax.swing.JTable tabla_abonos;
    public static javax.swing.JTable tabla_articulos;
    private javax.swing.JTable tabla_clientes;
    public static javax.swing.JTable tabla_detalle;
    private javax.swing.JTextField txtAmountEdit;
    private javax.swing.JTextField txtDiscountRateEdit;
    private javax.swing.JTextField txtEmailToSend;
    private javax.swing.JFormattedTextField txtPorcentajeDescuento;
    private javax.swing.JTextField txtUnitPriceEdit;
    private javax.swing.JTextField txt_abono;
    private javax.swing.JFormattedTextField txt_abonos;
    private javax.swing.JTextField txt_apellidos;
    private javax.swing.JTextField txt_apodo;
    private javax.swing.JTextField txt_buscar;
    private javax.swing.JFormattedTextField txt_calculo;
    private javax.swing.JTextField txt_cantidad;
    private javax.swing.JTextField txt_comentario;
    private javax.swing.JTextPane txt_comentarios;
    private javax.swing.JFormattedTextField txt_depositoGarantia;
    public static javax.swing.JTextPane txt_descripcion;
    private javax.swing.JFormattedTextField txt_descuento;
    private javax.swing.JTextField txt_direccion;
    private javax.swing.JTextField txt_email;
    private javax.swing.JFormattedTextField txt_envioRecoleccion;
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
}
