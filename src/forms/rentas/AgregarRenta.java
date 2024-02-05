package forms.rentas;

import forms.socialMediaContact.AddCatalogSocialMediaFormDialog;
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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import mobiliario.iniciar_sesion;
import common.model.Articulo;
import common.model.Cliente;
import common.model.DatosGenerales;
import common.model.Renta;
import common.model.TipoAbono;
import common.model.Usuario;
import common.form.items.VerDisponibilidadArticulos;
import common.model.Color;
import common.model.EstadoEvento;
import common.model.Tipo;
import common.model.AvailabilityItemResult;
import common.model.CatalogSocialMediaContactModel;
import common.services.CatalogSocialMediaContactService;
import common.services.EstadoEventoService;
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
import common.services.TipoEventoService;
import common.tables.TableCustomer;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.JTextField;
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
    private final CatalogSocialMediaContactService catalogSocialMediaContactService 
            = CatalogSocialMediaContactService.getInstance();
    private final SystemService systemService = SystemService.getInstance();
    // listado de articulos que se llenaran de manera asincrona, y se utilizara para realizar busquedas por descripcion
    private List<Articulo> articulos = new ArrayList<>();
    private final ItemService itemService;
    private final CustomerService customerService;
    private List<Cliente> customers = new ArrayList<>();
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    // valor de la fila a editar
    private Integer rowSelectedToEdit = null;
    private TaskAlmacenUpdateService taskAlmacenUpdateService;
    private TaskDeliveryChoferUpdateService taskDeliveryChoferUpdateService;
    private final TableCustomer tableCustomer;
    // variables gloables para reutilizar en los filtros y combos
    private List<Usuario> choferes = new ArrayList<>();
    private List<Tipo> typesGlobal = new ArrayList<>();
    private List<EstadoEvento> statusListGlobal = new ArrayList<>();
    private List<TipoAbono> tiposAbonosGlobal = new ArrayList<>();
    private final EstadoEventoService estadoEventoService = EstadoEventoService.getInstance();
    private final TipoEventoService tipoEventoService = TipoEventoService.getInstance();

    public AgregarRenta() {
        
        funcion.conectate();
        initComponents();
        tableCustomer = new TableCustomer();
        Utility.addJtableToPane(937, 305, panelCustomer, tableCustomer);
        saleService = SaleService.getInstance();
        customerService = CustomerService.getInstance();
        
        txt_precio_unitario.setEditable(false);
        txt_subtotal.setEditable(false);
        txt_subtotal.setHorizontalAlignment(SwingConstants.RIGHT);
        txt_calculo.setEditable(false);
        txt_calculo.setHorizontalAlignment(SwingConstants.RIGHT);
        txt_abonos.setEditable(false);
        txt_abonos.setHorizontalAlignment(SwingConstants.RIGHT);
        txt_total.setEditable(false);
        txt_total.setHorizontalAlignment(SwingConstants.RIGHT);
        txt_total_iva.setEditable(false);
        txt_total_iva.setHorizontalAlignment(SwingConstants.RIGHT);
        txt_descuento.setEditable(false);
        txt_descuento.setHorizontalAlignment(SwingConstants.RIGHT);
        jbtn_reporte.setEnabled(false);
        jbtn_disponible.setEnabled(false);
        check_mostrar_precios.setSelected(true);
        check_generar_reporte.setSelected(true);
        xemail = funcion.existe_email();
        jbtn_nuevo_evento.setEnabled(false);
        panel_articulos.setVisible(true);
        panel_conceptos.setVisible(false);
        jTabbedPane1.setEnabledAt(1, false);
        lbl_atiende.setText("Atiende: " + iniciar_sesion.nombre_usuario_global+ " " + iniciar_sesion.apellidos_usuario_global);
        jTabbedPane1.setSelectedIndex(0);
        itemService = ItemService.getInstance();
        disableInputsEditItem();
        initData();
        eventListenerTextHourFields();
        addEventListenerTableCustomers();
        addEventListenerCmbDate();
    }
    
    private void addEventListenerCmbDate () {
    
        cmb_fecha_evento.getDateEditor().addPropertyChangeListener((PropertyChangeEvent e) -> {
            if ("date".equals(e.getPropertyName()) && cmb_fecha_evento.getDate() != null) {
                txtInitDeliveryHour.requestFocus();
            }
        });
        
        cmb_fecha_entrega.getDateEditor().addPropertyChangeListener((PropertyChangeEvent e) -> {
            if ("date".equals(e.getPropertyName()) && cmb_fecha_devolucion.getDate() == null 
                    && cmb_fecha_entrega.getDate() != null) {
                cmb_fecha_devolucion.setDate(cmb_fecha_entrega.getDate());
            }
            if ("date".equals(e.getPropertyName()) && cmb_fecha_evento.getDate() == null 
                    && cmb_fecha_entrega.getDate() != null) {
                cmb_fecha_evento.setDate(cmb_fecha_entrega.getDate());
            }
        });
    }
    
    private void addEventListenerTableCustomers(){
        
        tableCustomer.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    id_cliente = tableCustomer.getValueAt(
                    tableCustomer.getSelectedRow(), TableCustomer.Column.ID.getNumber()).toString();
                    jTabbedPane1.setSelectedIndex(1);
                    jTabbedPane1.setEnabledAt(1, true);
                    
                    String customerName = tableCustomer.getValueAt(tableCustomer.getSelectedRow(), 
                            TableCustomer.Column.NAME.getNumber()).toString();
                    String customerLastName = tableCustomer.getValueAt(tableCustomer.getSelectedRow(), 
                            TableCustomer.Column.LAST_NAME.getNumber()).toString();
                    lbl_cliente.setText("Cliente: "+customerName+" "+customerLastName);
                    
                    fillDataCmbs();
                }
            }
        });
    
    }
    
    private void eventListenerTextHourFields () {
        
        txtEndReturnHour.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();                
                if (Utility.validateHour(textField.getText()) 
                        && Utility.validateHour(txtInitDeliveryHour.getText())
                        && Utility.validateHour(txtEndDeliveryHour.getText())
                        && Utility.validateHour(txtInitReturnHour.getText())
                        ) {
                    txt_descripcion.requestFocus();
                }
            }
        });
        
        txtInitDeliveryHour.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();                
                if (Utility.validateHour(textField.getText()) 
                        && !Utility.validateHour(txtEndDeliveryHour.getText())) {
                    txtEndDeliveryHour.requestFocus();
                }
            }
        });
        
        txtInitReturnHour.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();                
                if (Utility.validateHour(textField.getText()) 
                        && !Utility.validateHour(txtEndReturnHour.getText())) {
                    txtEndReturnHour.requestFocus();
                }
            }
        });
        
        txtEndDeliveryHour.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();                
                if (Utility.validateHour(textField.getText())
                        && !Utility.validateHour(txtInitReturnHour.getText())) {
                    txtInitReturnHour.requestFocus();
                }
            }
        });
        
    }
    
    private void initData () {
        
        new Thread(() -> {
            disableFormCustomer();
            getCustomers();
            enableFormCustomer();
            UtilityCommon.setTimeout(() -> txt_nombre.requestFocus(), 1000);
        }).start();        
        formato_tabla_detalles();
        formato_tabla_abonos();
        fillCmbCatalogSocialMediaContact();
        cmb_fecha_pago.setDate(new Date());
          
        
    }
    
    private void fillCmbCatalogSocialMediaContact () {
        
        cmbSocialMedialContact.setEnabled(false);
        cmbSocialMedialContact.removeAllItems();
        new Thread(() -> {
            try {
                List<CatalogSocialMediaContactModel> catalogs = 
                        catalogSocialMediaContactService.getAll();

                catalogs.stream().forEach(t -> 
                        cmbSocialMedialContact.addItem(t));

            
            } catch (DataOriginException e) {
               JOptionPane.showMessageDialog(this, e, 
                       ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE); 
            } finally {
                cmbSocialMedialContact.setEnabled(true);
            }
        }).start();
    }
    
    private void fillTableCustomers (List<Cliente> list) {
        tableCustomer.format();
        DefaultTableModel tableModel = (DefaultTableModel) tableCustomer.getModel();
            
            list.forEach(customer -> {
                Object row[] = {
                    false,
                    customer.getId(),
                    customer.getNombre(),
                    customer.getApellidos(),
                    customer.getApodo(),
                    customer.getTelMovil(),
                    customer.getTelFijo(),
                    customer.getEmail(),
                    customer.getDireccion(),
                    customer.getLocalidad(),
                    customer.getRfc(),
                    customer.getBirthday(),
                    customer.getSocialMedia().getDescription()
                };                
                tableModel.addRow(row);
            });
    }
    
    private void getCustomers() {
        try {
            customers = customerService.getAll();
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
                mensaje.append(txt_abonos.getText());
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
    
    private void fillDataCmbs () {
        new Thread(() -> {
            txt_buscar.setEnabled(false);
            tabla_articulos();
            txt_buscar.setEnabled(true);            
        }).start();
        
        new Thread(() -> {
            cmb_estado.setEnabled(false);
            llenar_combo_estado();
            cmb_estado.setEnabled(true);                     
        }).start();
        
        new Thread(() -> {
            cmb_tipo.setEnabled(false);
            llenar_combo_tipo();
            cmb_tipo.setEnabled(true);           
        }).start();
        
        new Thread(() -> {
            cmb_chofer.setEnabled(false);
            llenar_chofer();
            cmb_chofer.setEnabled(true);         
        }).start();
        
        new Thread(() -> {
            cmbTipoPago.setEnabled(false);
            llenar_abonos();
            cmbTipoPago.setEnabled(true);
        }).start();
    
    }

    private boolean agregar_cliente() {
        
        boolean res = true;
        
        Cliente cliente = new Cliente();
        
        CatalogSocialMediaContactModel catalog = 
                (CatalogSocialMediaContactModel) cmbSocialMedialContact.getModel().getSelectedItem();
        
        cliente.setSocialMedia(catalog);
        
        if (cmbBirthday.getDate() != null) {        
            cliente.setBirthday(
                    new Timestamp(cmbBirthday.getDate().getTime())
            );
        }
        
        cliente.setNombre(txt_nombre.getText());
        cliente.setApellidos(txt_apellidos.getText());
        cliente.setApodo(txt_apodo.getText());
        cliente.setEmail(txt_email.getText());
        cliente.setTelMovil(txt_tel_movil.getText());
        cliente.setTelFijo(txt_tel_casa.getText());
        cliente.setDireccion(txt_direccion.getText());
        cliente.setLocalidad(txt_localidad.getText());
        cliente.setRfc(txt_rfc.getText());
        cliente.setActivo("1");
        
        try {
            customerService.saveOrUpdate(cliente);
            
            id_cliente = cliente.getId().toString();
            lbl_cliente.setText("Cliente: "+cliente.getNombre()+" "+cliente.getApellidos());
            
            jTabbedPane1.setSelectedIndex(1);
            jTabbedPane1.setEnabledAt(1, true);            
            limpiar();
            fillDataCmbs();
            jbtn_disponible.setEnabled(false);
            jbtn_reporte.setEnabled(false);
            jbtn_nuevo_evento.setEnabled(false);

            if (txt_email.getText().equals("")) {
                check_enviar_email.setSelected(false);
            } else if (xemail != null && xemail > 0) {
                check_enviar_email.setSelected(true);
            }
            
        } catch (BusinessException businessException) {
            JOptionPane.showMessageDialog(this, businessException.getMessage(), 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            res = false;
        }

        
        return res;

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
                fPorcentaejeDescuento = Float.parseFloat(this.txtPorcentajeDescuento.getText().replace(",", ""));
            
            if(!this.txt_subtotal.getText().isEmpty() )
                fSubtotal = Float.parseFloat(this.txt_subtotal.getText().replace(",", ""));
            
            if(!this.txt_descuento.getText().isEmpty())
                fDescuento = Float.parseFloat(this.txt_descuento.getText().replace(",", ""));
            
            if(!this.txt_envioRecoleccion.getText().isEmpty())
                fEnvioRecoleccion = Float.parseFloat(this.txt_envioRecoleccion.getText().replace(",", ""));
            
            if(!this.txt_depositoGarantia.getText().isEmpty())
                fDepositoGarantia = Float.parseFloat(this.txt_depositoGarantia.getText().replace(",", ""));
            
            if(!this.txt_abonos.getText().isEmpty())
                fAbonos = Float.parseFloat(this.txt_abonos.getText().replace(",", ""));        
            
            if(fPorcentaejeDescuento == 0)
                this.txt_descuento.setText("0");
            
          
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento;
            
            
            if(fPorcentaejeDescuento > 0){
                fDescuento = (fSubtotal * (fPorcentaejeDescuento / 100));
                this.txt_descuento.setText(decimalFormat.format(fDescuento));
            
            }
              
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento; 
            if(!this.txt_iva.getText().equals(""))
            {              
                fIVA = Float.parseFloat(this.txt_iva.getText().replace(",", ""));
                fTotalIVA = (fCalculo * (fIVA / 100));              
                this.txt_total_iva.setText(decimalFormat.format(fTotalIVA));
            }            
            
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento; 
            this.txt_calculo.setText(decimalFormat.format(fCalculo));        
            
             if(!this.txt_total_iva.getText().isEmpty())
                fTotalIVA = Float.parseFloat(this.txt_total_iva.getText().replace(",", ""));
            
            fCalculo = (fSubtotal+fEnvioRecoleccion+fDepositoGarantia+fTotalIVA) - fDescuento;
            this.txt_calculo.setText(decimalFormat.format(fCalculo));
            
            
            // TOTALES            
            fTotal = (fCalculo - fAbonos );
           
            if(fTotal < 0)
                fTotal = 0f;
            
         txt_total.setText(decimalFormat.format(fTotal));
            
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(null, "Error al calcular el total. "+e, "SOLO NUMEROS", JOptionPane.INFORMATION_MESSAGE);
             return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado al calcular el total "+e, "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
    }

    public void llenar_combo_estado() {
        
        if (!statusListGlobal.isEmpty()) {
            return;
        }
        
        try {
            statusListGlobal = estadoEventoService.get();
            cmb_estado.removeAllItems();
            statusListGlobal.stream().forEach(t -> cmb_estado.addItem(t));
        } catch (DataOriginException dataOriginException) {
            JOptionPane.showMessageDialog(this, dataOriginException, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }


    }

    public void llenar_combo_tipo() {
        
        if (!typesGlobal.isEmpty()) {
            return;
        }
        
        try {
            cmb_tipo.removeAllItems();
            typesGlobal = tipoEventoService.get();
            typesGlobal.stream().forEach(t -> cmb_tipo.addItem(t));
        } catch (DataOriginException dataOriginException) {
            JOptionPane.showMessageDialog(this, dataOriginException, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }

    }

    public void llenar_chofer() {
        
        if (!choferes.isEmpty()) {
            return;
        }

        try {
            choferes =  userService.getChoferes();
            cmb_chofer.removeAllItems();
            cmb_chofer.addItem(
                    new Usuario(0,ApplicationConstants.CMB_SELECCIONE));

            for(Usuario usuario : choferes){
              cmb_chofer.addItem(usuario);
            }

            cmb_chofer.setSelectedIndex(0);
            
        } catch (DataOriginException dataOriginException) {
            JOptionPane.showMessageDialog(this, dataOriginException, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
        

    }
    
    public void llenar_abonos() {
        
        if (!tiposAbonosGlobal.isEmpty()) {
            return;
        }

        tiposAbonosGlobal =  saleService.obtenerTiposAbono(funcion);
        this.cmbTipoPago.removeAllItems();
        
        cmbTipoPago.addItem(new TipoAbono(0,ApplicationConstants.CMB_SELECCIONE));
        
        tiposAbonosGlobal.stream().forEach(t -> cmbTipoPago.addItem(t));
        
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
        } else if (!Utility.validateHour(txtInitDeliveryHour.getText())) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar hora ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (!Utility.validateHour(txtEndDeliveryHour.getText())) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar segunda hora ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (!Utility.validateHour(txtInitReturnHour.getText())) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar hora devolución ", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (!Utility.validateHour(txtEndReturnHour.getText())) {
            JOptionPane.showMessageDialog(null, "Falta seleccionar segunda hora devolución", "Error", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmb_chofer.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(null, "Favor de seleccionar un chofer ", "Error", JOptionPane.INFORMATION_MESSAGE);
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
        hora_entrega = txtInitDeliveryHour.getText() +" a "+this.txtEndDeliveryHour.getText();
        hora_devolucion = this.txtInitReturnHour.getText()+" a "+txtEndReturnHour.getText();
        
        fecha_entrega = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_entrega.getDate());
        fecha_devolucion = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_devolucion.getDate());
        fecha_evento = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_evento.getDate());
        
        EstadoEvento estadoEventoSelected =(EstadoEvento) cmb_estado.getModel().getSelectedItem();
        Usuario choferSelected = (Usuario) cmb_chofer.getModel().getSelectedItem();
        Tipo tipoSelected = (Tipo) cmb_tipo.getModel().getSelectedItem();

        String id_estado = estadoEventoSelected.getEstadoId().toString();
        String id_chofer = choferSelected.getUsuarioId().toString();
        String id_tipo = tipoSelected.getTipoId().toString();
        System.out.println("ID TIPO: " + id_tipo);
        

        try {
            Utility.validateStatusAndTypeEvent(estadoEventoSelected,tipoSelected);
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
        if (!txt_descuento.getText().equals("") && !txtPorcentajeDescuento.getText().equals("")) {
            descuento = UtilityCommon.onlyNumbers(txt_descuento.getText());            
            porcentajeDescuentoRenta = UtilityCommon.onlyNumbers(this.txtPorcentajeDescuento.getText());
        } else {
            porcentajeDescuentoRenta = "0";
            descuento = "0";
        }
        if (!txt_iva.getText().equals("")) {
            iva = EliminaCaracteres(txt_iva.getText(), "$");
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
                    message = taskAlmacenUpdateService.saveWhenIsNewEvent(
                            Long.parseLong(id_ultima_renta), 
                            folio, 
                            iniciar_sesion.usuarioGlobal.getUsuarioId().toString()
                    );
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
                    taskDeliveryChoferUpdateService.saveWhenIsNewEvent(
                            Long.parseLong(id_ultima_renta), 
                            folio,id_chofer, 
                            iniciar_sesion.usuarioGlobal.getUsuarioId().toString()
                    );
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
    
    private void disableFormCustomer() {
        txt_nombre.setEnabled(false);
        txt_apellidos.setEnabled(false);
        txt_direccion.setEnabled(false);
        txt_apodo.setEnabled(false);
        txt_tel_movil.setEnabled(false);
        txt_tel_casa.setEnabled(false);
        txt_direccion.setEnabled(false);
        txt_localidad.setEnabled(false);
        txt_rfc.setEnabled(false);
        txt_email.setEnabled(false);

    }
    
    private void enableFormCustomer() {
        txt_nombre.setEnabled(true);
        txt_apellidos.setEnabled(true);
        txt_direccion.setEnabled(true);
        txt_apodo.setEnabled(true);
        txt_tel_movil.setEnabled(true);
        txt_tel_casa.setEnabled(true);
        txt_direccion.setEnabled(true);
        txt_localidad.setEnabled(true);
        txt_rfc.setEnabled(true);
        txt_email.setEnabled(true);

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
        cmb_chofer.setSelectedItem(ApplicationConstants.CMB_SELECCIONE);
        cmb_estado.setSelectedItem("Apartado");
        cmb_tipo.setSelectedItem("Pedido");
        txtInitDeliveryHour.setText("");
        txtEndDeliveryHour.setText("");
        txtInitReturnHour.setText("");
        txtEndReturnHour.setText("");
        cmb_fecha_entrega.setDate(null);
        cmb_fecha_devolucion.setDate(null);
        cmb_fecha_pago.setDate(new Date());
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
        txt_subtotal.setText(decimalFormat.format(total));
        jTabbedPaneItems.setTitleAt(1, "Artículos("+tabla_detalle.getRowCount()+")");

    }

    public void abonos() {
        String aux;
        float abonos = 0;
        for (int i = 0; i < tabla_abonos.getRowCount(); i++) {
            aux = EliminaCaracteres(((String) tabla_abonos.getValueAt(i, 1).toString()), "$,");
            abonos = Float.parseFloat(aux) + abonos;

        }
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

    
    private void searchAndFillTableCustomers () {
        try {
            List<Cliente> filterCustomers =
                    customers.stream()
                            .filter(customer -> Objects.nonNull(customer))
                            .filter(customer -> Objects.nonNull(customer.getNombre()))
                            .filter(customer -> Objects.nonNull(customer.getApellidos()))
                            .filter(customer -> UtilityCommon.removeAccents(customer.getNombre().toLowerCase().trim()).contains(txt_nombre.getText().toLowerCase().trim()))
                            .filter(customer -> UtilityCommon.removeAccents(customer.getApellidos().toLowerCase().trim()).contains(txt_apellidos.getText().toLowerCase().trim()))
                            .collect(Collectors.toList());
            fillTableCustomers(filterCustomers);
        } catch (Exception e){
                        JOptionPane.showMessageDialog(this, e, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            log.error(e.getMessage(),e);
        }
        
        
    }
    

    public void formato_tabla_detalles() {
        Object[][] data = {{"", "", "", "", "","","",""}};
        String[] columnNames = {"Cantidad", "id_articulo", "Articulo", "P.Unitario","Descuento %","Descuento", "Importe","Utiles"};
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
                    float cantidad = Float.parseFloat(txt_cantidad.getText());
                    float precio = Float.parseFloat(txt_precio_unitario.getText());
                    float importe = (cantidad * precio);
                    float totalDescuento = 0f;
                    if(porcentajeDescuento > 0){
                        totalDescuento = importe * (porcentajeDescuento / 100);
                        importe = importe - totalDescuento;
                    }

                    String xprecio = conviertemoneda(Float.toString(precio));
                    String ximporte = conviertemoneda(Float.toString(importe));

                    Object nuevo1[] = {txt_cantidad.getText(), id_articulo, lbl_eleccion.getText(), xprecio,porcentajeDescuento+"", conviertemoneda(totalDescuento+"") , ximporte, itemUtilesGlobal};
                    temp.addRow(nuevo1);
                    subTotal();
                    total();
                    jbtn_disponible.setEnabled(true);
                    jbtn_mostrar_articulos.setEnabled(true);
                    panel = true;
                    disableInputsEditItem();
                    
                    String itemSelected = lbl_eleccion.getText();
                    lbl_eleccion.setText("Artículo se agregó al evento.");
                    UtilityCommon.setTimeout(() -> lbl_eleccion.setText(itemSelected), 2000);
                    
                    Toolkit.getDefaultToolkit().beep();
                    txt_buscar.requestFocus();
                    txt_buscar.selectAll();
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
        
        TipoAbono tipoAbonoSelected = (TipoAbono) cmbTipoPago.getModel().getSelectedItem();           

        DefaultTableModel temp = (DefaultTableModel) tabla_abonos.getModel();
        float abono = 0f;
        try {         
            abono = Float.parseFloat(txt_abono.getText());                          
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingresa solo numeros ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String fechaPago = "";
        if(cmb_fecha_pago.getDate() != null && !cmb_fecha_pago.getDate().toString().equals(""))
            fechaPago = new SimpleDateFormat("dd/MM/yyyy").format(cmb_fecha_pago.getDate());

        String xabono = conviertemoneda(Float.toString(abono));
        fecha_sistema();
        Object nuevo1[] = {fecha_sistema, xabono, txt_comentario.getText(),tipoAbonoSelected.getTipoAbonoId(),cmbTipoPago.getSelectedItem().toString(),fechaPago};
        temp.addRow(nuevo1);
        abonos();
        subTotal();
        total();

        txt_abono.setText("0");
        txt_abono.requestFocus();

        

    }
    
    
    private void formatItemsTable () {
        
        //tabla_articulos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        String[] columNames = {"Id","Código", "Categoría", "Descripción", "Color", "P. Unitario","Utiles"};
        Object[][] data = {{"","","","","","",""}};
        

        DefaultTableModel tableModel = new DefaultTableModel(data, columNames);
        DefaultTableCellRenderer TablaRenderer = new DefaultTableCellRenderer();
        TablaRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tabla_articulos.setModel(tableModel);

        int[] anchos = {10,120, 120, 250, 100,90,20};

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
        
        List<Articulo> itemsFiltered = 
                UtilityCommon.applyFilterToItems(articulos,txt_buscar.getText());
        
        fillTableItems(itemsFiltered);
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
        cmbBirthday = new com.toedter.calendar.JDateChooser();
        jLabel23 = new javax.swing.JLabel();
        cmbSocialMedialContact = new javax.swing.JComboBox<>();
        jLabel42 = new javax.swing.JLabel();
        lblAddSocialMediaContact = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        panelCustomer = new javax.swing.JPanel();
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
        cmb_estado = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtPorcentajeDescuento = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txt_descripcion = new javax.swing.JTextPane();
        cmb_chofer = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        cmb_tipo = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
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
        txt_iva = new javax.swing.JFormattedTextField();
        check_generar_reporte = new javax.swing.JCheckBox();
        check_enviar_email = new javax.swing.JCheckBox();
        check_mostrar_precios = new javax.swing.JCheckBox();
        txtEmailToSend = new javax.swing.JTextField();
        txtInitReturnHour = new javax.swing.JFormattedTextField();
        txtInitDeliveryHour = new javax.swing.JFormattedTextField();
        txtEndDeliveryHour = new javax.swing.JFormattedTextField();
        txtEndReturnHour = new javax.swing.JFormattedTextField();
        txt_abonos = new javax.swing.JTextField();
        txt_subtotal = new javax.swing.JTextField();
        txt_calculo = new javax.swing.JTextField();
        txt_descuento = new javax.swing.JTextField();
        txt_total_iva = new javax.swing.JTextField();
        txt_total = new javax.swing.JTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jbtn_agregar_articulo = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jbtn_disponible = new javax.swing.JButton();
        jbtn_mostrar_articulos = new javax.swing.JButton();
        jTabbedPaneItems = new javax.swing.JTabbedPane();
        panel_articulos = new javax.swing.JPanel();
        txt_cantidad = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txt_precio_unitario = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txt_buscar = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabla_articulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        txt_porcentaje_descuento = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        btnGetItemsFromFolio = new javax.swing.JButton();
        lbl_eleccion = new javax.swing.JLabel();
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
        cmbTipoPago = new javax.swing.JComboBox<>();
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

        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

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

        txt_nombre.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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

        txt_apellidos.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_apellidos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_apellidosKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel2.setText("Apellidos:");

        txt_apodo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel3.setText("Apodo:");

        txt_tel_movil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel4.setText("Tel Movil:");

        txt_tel_casa.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel5.setText("Tel Casa:");

        txt_email.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel6.setText("Email:");

        txt_direccion.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel7.setText("Direccion:");

        txt_localidad.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel8.setText("Localidad:");

        jLabel9.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel9.setText("RFC:");

        txt_rfc.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        cmbBirthday.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel23.setText("Fecha de cumpleaños:");

        cmbSocialMedialContact.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbSocialMedialContact.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel42.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jLabel42.setText("Medio de contacto:");

        lblAddSocialMediaContact.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lblAddSocialMediaContact.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAddSocialMediaContact.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAddSocialMediaContactMouseClicked(evt);
            }
        });
        lblAddSocialMediaContact.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lblAddSocialMediaContactKeyPressed(evt);
            }
        });

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
                    .addComponent(cmbBirthday, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbSocialMedialContact, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblAddSocialMediaContact)))
                        .addGap(0, 12, Short.MAX_VALUE))
                    .addComponent(txt_rfc))
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
                .addGap(2, 2, 2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbBirthday, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel42))
                    .addComponent(lblAddSocialMediaContact))
                .addGap(4, 4, 4)
                .addComponent(cmbSocialMedialContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(188, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Clientes en la base de datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12))); // NOI18N

        javax.swing.GroupLayout panelCustomerLayout = new javax.swing.GroupLayout(panelCustomer);
        panelCustomer.setLayout(panelCustomerLayout);
        panelCustomerLayout.setHorizontalGroup(
            panelCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelCustomerLayout.setVerticalGroup(
            panelCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelCustomer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        panel_datos_generales.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panel_datos_generales.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_cliente.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(lbl_cliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 20, 200, 20));

        lbl_atiende.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lbl_atiende.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        panel_datos_generales.add(lbl_atiende, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 40, 200, 20));

        cmb_fecha_entrega.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(cmb_fecha_entrega, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 210, 21));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Fecha de entrega:");
        panel_datos_generales.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 110, 21));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Hora entrega:");
        panel_datos_generales.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 110, 20));

        cmb_fecha_devolucion.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(cmb_fecha_devolucion, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, 210, 21));

        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Fecha del evento:");
        panel_datos_generales.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 110, 20));

        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Dirección del evento:");
        panel_datos_generales.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 230, -1));

        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Estado:");
        panel_datos_generales.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 140, 70, 23));

        cmb_estado.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmb_estado.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(cmb_estado, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 140, 170, -1));

        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText("Subtotal:");
        panel_datos_generales.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(809, 20, 90, 20));

        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Descuento % :");
        panel_datos_generales.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 40, 130, 20));

        txtPorcentajeDescuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtPorcentajeDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPorcentajeDescuento.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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

        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("IVA % :");
        panel_datos_generales.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 100, 120, 20));

        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Pagos:");
        panel_datos_generales.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 140, 120, 20));

        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Total:");
        panel_datos_generales.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 160, 120, 20));

        txt_descripcion.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jScrollPane3.setViewportView(txt_descripcion);

        panel_datos_generales.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 40, 230, 90));

        cmb_chofer.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmb_chofer.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(cmb_chofer, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 210, -1));

        jLabel26.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel26.setText("Chofer:");
        panel_datos_generales.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 110, 20));

        cmb_tipo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmb_tipo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(cmb_tipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 170, 170, -1));

        jLabel27.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel27.setText("Tipo:");
        panel_datos_generales.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 170, 70, 20));

        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText("Hora devolución:");
        panel_datos_generales.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 110, 20));

        jLabel29.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel29.setText("a");
        panel_datos_generales.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 170, 20, 20));

        jLabel30.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel30.setText("a");
        panel_datos_generales.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 140, 20, 20));

        jLabel31.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel31.setText("Fecha de devolución:");
        panel_datos_generales.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 120, 20));

        cmb_fecha_evento.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(cmb_fecha_evento, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, 210, 21));

        jLabel32.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel32.setText("Hrs.");
        panel_datos_generales.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 170, 40, 20));

        jLabel33.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel33.setText("Hrs.");
        panel_datos_generales.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 140, 50, 20));

        jLabel34.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel34.setText("Envío y recolección");
        panel_datos_generales.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 60, 130, 20));

        txt_envioRecoleccion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_envioRecoleccion.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_envioRecoleccion.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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

        jLabel35.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel35.setText("Depósito en garantía");
        panel_datos_generales.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(809, 80, 130, 20));

        txt_depositoGarantia.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_depositoGarantia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_depositoGarantia.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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

        jLabel36.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel36.setText("Calculo:");
        panel_datos_generales.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 120, 120, 20));

        txt_iva.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txt_iva.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt_iva.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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

        check_generar_reporte.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        check_generar_reporte.setText("Generar reporte al guardar");
        check_generar_reporte.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        check_generar_reporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_generar_reporteActionPerformed(evt);
            }
        });
        panel_datos_generales.add(check_generar_reporte, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 150, 190, 20));

        check_enviar_email.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        check_enviar_email.setText("Enviar email confirmación");
        check_enviar_email.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        check_enviar_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_enviar_emailActionPerformed(evt);
            }
        });
        panel_datos_generales.add(check_enviar_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 110, 180, -1));

        check_mostrar_precios.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        check_mostrar_precios.setText("Mostrar precios en PDF");
        check_mostrar_precios.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panel_datos_generales.add(check_mostrar_precios, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 130, 180, 20));

        txtEmailToSend.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txtEmailToSend.setToolTipText("Para enviar multiples correos deberas separarlos por punto y coma [;]");
        txtEmailToSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailToSendActionPerformed(evt);
            }
        });
        panel_datos_generales.add(txtEmailToSend, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 170, 180, -1));

        try {
            txtInitReturnHour.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtInitReturnHour.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(txtInitReturnHour, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 170, 60, -1));

        try {
            txtInitDeliveryHour.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtInitDeliveryHour.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(txtInitDeliveryHour, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, 60, -1));

        try {
            txtEndDeliveryHour.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtEndDeliveryHour.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(txtEndDeliveryHour, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 140, 60, -1));

        try {
            txtEndReturnHour.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtEndReturnHour.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(txtEndReturnHour, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 170, 60, -1));

        txt_abonos.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(txt_abonos, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 140, 120, -1));

        txt_subtotal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(txt_subtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 20, 120, 20));

        txt_calculo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(txt_calculo, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 120, 120, 20));

        txt_descuento.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(txt_descuento, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 40, 80, 20));

        txt_total_iva.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(txt_total_iva, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 100, 80, 20));

        txt_total.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        panel_datos_generales.add(txt_total, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 160, 120, -1));

        jTabbedPane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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

        jTabbedPaneItems.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_cantidad.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_cantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_cantidadKeyPressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Cantidad:");

        txt_precio_unitario.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_precio_unitario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_precio_unitarioKeyPressed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Precio:");

        txt_buscar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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

        tabla_articulos.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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
        tabla_articulos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabla_articulosKeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(tabla_articulos);

        txt_porcentaje_descuento.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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

        jLabel37.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel37.setText("Descuento %");

        btnGetItemsFromFolio.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnGetItemsFromFolio.setText("Obtener artículos de un folio");
        btnGetItemsFromFolio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGetItemsFromFolio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetItemsFromFolioActionPerformed(evt);
            }
        });

        lbl_eleccion.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout panel_articulosLayout = new javax.swing.GroupLayout(panel_articulos);
        panel_articulos.setLayout(panel_articulosLayout);
        panel_articulosLayout.setHorizontalGroup(
            panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_articulosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1055, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel_articulosLayout.createSequentialGroup()
                        .addComponent(txt_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGetItemsFromFolio)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_eleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_precio_unitario, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_porcentaje_descuento, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_articulosLayout.setVerticalGroup(
            panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_articulosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_porcentaje_descuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_precio_unitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_articulosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panel_articulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_buscar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGetItemsFromFolio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lbl_eleccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
        );

        jTabbedPaneItems.addTab("Inventario", panel_articulos);

        tabla_detalle.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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

        lbl_sel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Cantidad:");

        txtAmountEdit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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

        txtUnitPriceEdit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtUnitPriceEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUnitPriceEditKeyPressed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel38.setText("Precio:");

        jLabel41.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel41.setText("Descuento %:");

        txtDiscountRateEdit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtDiscountRateEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDiscountRateEditKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout panel_conceptosLayout = new javax.swing.GroupLayout(panel_conceptos);
        panel_conceptos.setLayout(panel_conceptosLayout);
        panel_conceptosLayout.setHorizontalGroup(
            panel_conceptosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_conceptosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_conceptosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel_conceptosLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane2))
                    .addGroup(panel_conceptosLayout.createSequentialGroup()
                        .addComponent(lbl_sel, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 362, Short.MAX_VALUE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAmountEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUnitPriceEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiscountRateEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panel_conceptosLayout.setVerticalGroup(
            panel_conceptosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_conceptosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_conceptosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_conceptosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDiscountRateEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtUnitPriceEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtAmountEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_sel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneItems.addTab("Artículos (0)", panel_conceptos);

        jPanel6.add(jTabbedPaneItems, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 1070, 370));

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

        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Pago:");

        txt_comentario.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel25.setText("Comentario:");

        txt_abono.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_abono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_abonoKeyPressed(evt);
            }
        });

        lblTipoAbono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lblTipoAbono.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

        cmbTipoPago.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbTipoPago.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel39.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel39.setText("Tipo de pago:");

        cmb_fecha_pago.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel40.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel40.setText("Fecha del pago:");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_abono)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblTipoAbono))
                            .addComponent(txt_comentario, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbTipoPago, javax.swing.GroupLayout.Alignment.LEADING, 0, 220, Short.MAX_VALUE))
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_fecha_pago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(jLabel40))
                    .addComponent(lblTipoAbono))
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
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
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

        txt_comentarios.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jScrollPane6.setViewportView(txt_comentarios);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1060, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
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
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(panel_datos_generales, javax.swing.GroupLayout.PREFERRED_SIZE, 1075, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(151, 151, 151))
        );

        jTabbedPane1.addTab("Conceptos", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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

        agregar_cliente();


    }//GEN-LAST:event_jButton1ActionPerformed

    private void tabla_detalleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_detalleMouseClicked
        // TODO add your handling code here:
        
        if (evt.getClickCount() == 2) {
            String itemSelected = (tabla_detalle.getValueAt(tabla_detalle.getSelectedRow(), 2)).toString();
            String characterToDelete = "$,";
            lbl_sel.setText(itemSelected);
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
        
        if(!iniciar_sesion.usuarioGlobal.getAdministrador().equals("1")){
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_NOT_PERMISIONS_ADMIN, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            Toolkit.getDefaultToolkit().beep();            
            return;
        }        

        txt_precio_unitario.setEditable(true);
        txt_precio_unitario.requestFocus();
        JOptionPane.showMessageDialog(this, "Puedes modificar el precio...", "Precio", JOptionPane.INFORMATION_MESSAGE);

        

        
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

    private void txtPorcentajeDescuentoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPorcentajeDescuentoKeyPressed
         if (evt.getKeyCode() == 10) {
            if (txt_subtotal.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Es necesario incluir subtotal para realizar el calculo", "ERROR", JOptionPane.INFORMATION_MESSAGE);
                Toolkit.getDefaultToolkit().beep();

            } else {              
                total();               
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_txtPorcentajeDescuentoKeyPressed

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
        
        jTabbedPaneItems.getSelectedIndex();
        jTabbedPaneItems.setSelectedIndex(
                jTabbedPaneItems.getSelectedIndex() == 0 ? 1 : 0
        );
       
        //jbtn_mostrar_articulos.setEnabled(false);
    }//GEN-LAST:event_jbtn_mostrar_articulosActionPerformed

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
            if (txt_subtotal.getText().equals("")) {
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
            if (txt_subtotal.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "No hay cantidad en subtotal para agregar el descuento", "Deposito en garantia", JOptionPane.INFORMATION_MESSAGE);
                Toolkit.getDefaultToolkit().beep();

            } else {
                total();                
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }//GEN-LAST:event_txt_depositoGarantiaKeyPressed

    private void txt_ivaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_ivaFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_ivaFocusLost

    private void txt_ivaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_ivaKeyPressed
        if (evt.getKeyCode() == 10) {
            if (txt_subtotal.getText().equals("")) {
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
        if(!iniciar_sesion.usuarioGlobal.getAdministrador().equals("1")){
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_NOT_PERMISIONS_ADMIN, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            Toolkit.getDefaultToolkit().beep();   
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

    private void addItemToEventFromInventory (int selectedRow) {
        lbl_eleccion.setText((String) tabla_articulos.getValueAt(selectedRow, 3).toString() + " " + (String) tabla_articulos.getValueAt(selectedRow, 4).toString());
        txt_precio_unitario.setText(EliminaCaracteres((String) tabla_articulos.getValueAt(selectedRow, 5).toString(), "$,"));
        txt_cantidad.setText("");
        txt_cantidad.requestFocus();
        txt_cantidad.selectAll();
        this.txt_porcentaje_descuento.setText("");
        id_articulo = (String) tabla_articulos.getValueAt(selectedRow, 0).toString();
        itemUtilesGlobal = tabla_articulos.getValueAt(selectedRow, 6).toString();
        txt_precio_unitario.setEditable(false);
    }
    
    private void tabla_articulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_articulosMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            addItemToEventFromInventory(tabla_articulos.getSelectedRow());
        }
    }//GEN-LAST:event_tabla_articulosMouseClicked

    private void txt_buscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscarKeyReleased
        
    }//GEN-LAST:event_txt_buscarKeyReleased

    private void txt_buscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_buscarKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER && tabla_articulos.getRowCount() > 0) {
            addItemToEventFromInventory(0);
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN && tabla_articulos.getRowCount() > 0) {
            tabla_articulos.requestFocus();
            tabla_articulos.changeSelection(0,0,false, false);
        } else {
           tabla_articulos_like();
        }
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

    private void lblAddSocialMediaContactMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddSocialMediaContactMouseClicked
        
        if(!iniciar_sesion.usuarioGlobal.getAdministrador().equals("1")){
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_NOT_PERMISIONS_ADMIN, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            Toolkit.getDefaultToolkit().beep();   
            return;
        }
        
        Frame frame = JOptionPane.getFrameForComponent(this);
        
        AddCatalogSocialMediaFormDialog dialog = 
                new AddCatalogSocialMediaFormDialog(frame, true);
        
        Boolean successfulChangesDetected = dialog.showDialog();
        
        if (successfulChangesDetected) {
            fillCmbCatalogSocialMediaContact();
        }
        
    }//GEN-LAST:event_lblAddSocialMediaContactMouseClicked

    private void lblAddSocialMediaContactKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblAddSocialMediaContactKeyPressed
        

        
    }//GEN-LAST:event_lblAddSocialMediaContactKeyPressed

    private void tabla_articulosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabla_articulosKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER && tabla_articulos.getRowCount() > 0) {
            addItemToEventFromInventory(tabla_articulos.getSelectedRow());
        } else if (evt.getKeyCode() == KeyEvent.VK_UP 
                && tabla_articulos.getSelectedRow() == 0) {
            txt_buscar.requestFocus();
            txt_buscar.selectAll();
        } 
    }//GEN-LAST:event_tabla_articulosKeyPressed

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
    private com.toedter.calendar.JDateChooser cmbBirthday;
    private javax.swing.JComboBox<CatalogSocialMediaContactModel> cmbSocialMedialContact;
    private javax.swing.JComboBox<TipoAbono> cmbTipoPago;
    private javax.swing.JComboBox<Usuario> cmb_chofer;
    private javax.swing.JComboBox<EstadoEvento> cmb_estado;
    private com.toedter.calendar.JDateChooser cmb_fecha_devolucion;
    private com.toedter.calendar.JDateChooser cmb_fecha_entrega;
    private com.toedter.calendar.JDateChooser cmb_fecha_evento;
    private com.toedter.calendar.JDateChooser cmb_fecha_pago;
    private javax.swing.JComboBox<Tipo> cmb_tipo;
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
    private javax.swing.JLabel jLabel42;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPaneItems;
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
    private javax.swing.JLabel lblAddSocialMediaContact;
    private javax.swing.JLabel lblTipoAbono;
    private javax.swing.JLabel lbl_atiende;
    private javax.swing.JLabel lbl_cliente;
    private javax.swing.JLabel lbl_eleccion;
    private javax.swing.JLabel lbl_sel;
    private javax.swing.JPanel panelCustomer;
    private javax.swing.JPanel panel_articulos;
    private javax.swing.JPanel panel_conceptos;
    private javax.swing.JPanel panel_datos_generales;
    private javax.swing.JTable tabla_abonos;
    public static javax.swing.JTable tabla_articulos;
    public static javax.swing.JTable tabla_detalle;
    private javax.swing.JTextField txtAmountEdit;
    private javax.swing.JTextField txtDiscountRateEdit;
    private javax.swing.JTextField txtEmailToSend;
    private javax.swing.JFormattedTextField txtEndDeliveryHour;
    private javax.swing.JFormattedTextField txtEndReturnHour;
    private javax.swing.JFormattedTextField txtInitDeliveryHour;
    private javax.swing.JFormattedTextField txtInitReturnHour;
    private javax.swing.JFormattedTextField txtPorcentajeDescuento;
    private javax.swing.JTextField txtUnitPriceEdit;
    private javax.swing.JTextField txt_abono;
    private javax.swing.JTextField txt_abonos;
    private javax.swing.JTextField txt_apellidos;
    private javax.swing.JTextField txt_apodo;
    private javax.swing.JTextField txt_buscar;
    private javax.swing.JTextField txt_calculo;
    private javax.swing.JTextField txt_cantidad;
    private javax.swing.JTextField txt_comentario;
    private javax.swing.JTextPane txt_comentarios;
    private javax.swing.JFormattedTextField txt_depositoGarantia;
    public static javax.swing.JTextPane txt_descripcion;
    private javax.swing.JTextField txt_descuento;
    private javax.swing.JTextField txt_direccion;
    private javax.swing.JTextField txt_email;
    private javax.swing.JFormattedTextField txt_envioRecoleccion;
    private javax.swing.JFormattedTextField txt_iva;
    private javax.swing.JTextField txt_localidad;
    private javax.swing.JTextField txt_nombre;
    private javax.swing.JTextField txt_porcentaje_descuento;
    private javax.swing.JTextField txt_precio_unitario;
    private javax.swing.JTextField txt_rfc;
    private javax.swing.JTextField txt_subtotal;
    private javax.swing.JTextField txt_tel_casa;
    private javax.swing.JTextField txt_tel_movil;
    private javax.swing.JTextField txt_total;
    private javax.swing.JTextField txt_total_iva;
    // End of variables declaration//GEN-END:variables
}
