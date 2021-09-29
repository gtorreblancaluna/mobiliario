package forms.proveedores;

import clases.sqlclass;
import exceptions.BusinessException;
import exceptions.DataOriginException;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import mobiliario.ApplicationConstants;
import mobiliario.consultar_renta;
import mobiliario.iniciar_sesion;
import mobiliario.principal;
import model.Articulo;
import model.DatosGenerales;
import model.DetalleRenta;
import model.Renta;
import model.providers.DetalleOrdenProveedor;
import model.providers.OrdenProveedor;
import model.providers.PagosProveedor;
import model.providers.Proveedor;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import services.SaleService;
import services.SystemService;
import services.providers.OrderProviderService;
import utilities.Utility;

/**
 *
 * @author Gerardo Torreblanca
 */
public class OrderProviderForm extends javax.swing.JInternalFrame {
    
    sqlclass funcion = new sqlclass();
    
    Object[][] dtconduc;      
    private final SaleService saleService;
    private final SystemService systemService;
//    private final ItemService itemService = new ItemService();
    private final OrderProviderService orderProviderService = OrderProviderService.getInstance();
    public static String g_articuloId;
    public static String g_rentaId;
    public static String g_cantidadEnPedido;
    public static Long g_provider_id;
    public static Long g_order_provider_id;
    private String navigation;
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    protected OrdenProveedor ordenProveedor = new OrdenProveedor();
    private PaymentsProvidersForm paymentsProvidersForm;
    
    /** Encabezados de la tabla ARTICULOS ORDEN PROVEEDOR */
    public final static int HD_ORDEN_PROVEEDOR_ID_ORDEN = 0;
    public final static int HD_ORDEN_PROVEEDOR_ID_ARTICULO = 1;
    public final static int HD_ORDEN_PROVEEDOR_DESCRIPCION_ARTICULO = 2;
    public final static int HD_ORDEN_PROVEEDOR_CANTIDAD = 3;
    public final static int HD_ORDEN_PROVEEDOR_PRECIO = 4;
    public final static int HD_ORDEN_PROVEEDOR_IMPORTE = 5;
    public final static int HD_ORDEN_PROVEEDOR_COMENTARIO = 6;
    public final static int HD_ORDEN_PROVEEDOR_TIPO_ORDEN = 7;
    public final static int HD_ORDEN_PROVEEDOR_CREADO = 8;
    public final static int HD_ORDEN_PROVEEDOR_ACTUALIZADO = 9;
    public final static int HD_ORDEN_PROVEEDOR_STATUS = 10;
   
    
    /** Encabezados de la tabla ARTICULOS */
    public final static int HD_ARTICULOS_ID_ARTICULO = 0;
    public final static int HD_ARTICULOS_CANTIDAD_PEDIDO = 1;
    public final static int HD_ARTICULOS_DESCRIPCION_ARTICULO = 2;
    public final static int HD_ARTICULOS_PRECIO_COBRAR = 3;
    
    public final static String UPDATE_ORDER = "update order";
    public final static String NEW_ORDER = "new order";
    private String folio = "";
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(OrderProviderForm.class.getName());
    
   public OrderProviderForm(String folio) {
        initComponents();
        this.folio = folio;
        funcion.conectate();
        systemService = SystemService.getInstance();
        saleService = SaleService.getInstance();
//        this.setLocationRelativeTo(null);
        this.lblQuitarElemento.setText("");
        this.setTitle("Agregar orden al proveedor ");
        resetCmbOrderStatus();
        formato_tabla_orden();
        formato_tabla_articulos();      
        llenar_tabla_articulos();
        resetInputBoxes();
        
        this.setClosable(true);
        txtSubTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtPagos.setHorizontalAlignment(JTextField.RIGHT);

    }
   
   public void reportPDF(String orderProviderId) throws Exception{
     
        if (orderProviderId == null || orderProviderId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se obtuvo la orden del proveedor");
            return;
        }
        JasperPrint jasperPrint;
       
           
            String pathLocation = Utility.getPathLocation();
            String archivo = pathLocation+ApplicationConstants.RUTA_REPORTE_ORDEN_PROVEEDOR;
            System.out.println("Cargando desde: " + archivo);
            if (archivo == null) {
                JOptionPane.showMessageDialog(rootPane, "No se encuentra el Archivo jasper");
                return;
            }
            JasperReport masterReport = null;
            
            masterReport = (JasperReport) JRLoader.loadObject(archivo);
           
            DatosGenerales datosGenerales = systemService.getGeneralData();
            
            Map parametros = new HashMap<>();
            parametros.put("ID_ORDEN",orderProviderId);
            parametros.put("NOMBRE_EMPRESA",datosGenerales.getCompanyName());
            parametros.put("DIRECCION_EMPRESA",datosGenerales.getAddress1());
            parametros.put("TELEFONOS_EMPRESA",datosGenerales.getAddress2());
            parametros.put("EMAIL_EMPRESA",datosGenerales.getAddress3());
            //guardamos el parámetro
            parametros.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );
           
            jasperPrint = JasperFillManager.fillReport(masterReport, parametros, funcion.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathLocation+ApplicationConstants.NOMBRE_REPORTE_ORDEN_PROVEEDOR);
            File file2 = new File(pathLocation+ApplicationConstants.NOMBRE_REPORTE_ORDEN_PROVEEDOR);
            Desktop.getDesktop().open(file2);
     
     }
    
   public void saveOrderProvider(){
       
       StringBuilder message = new StringBuilder();
       if(g_provider_id == null ){
           message.append("Falta proveedor, puedes elejir proveedor provisional\n");
       }
       if(g_rentaId == null || g_rentaId.equals("")){
           message.append("No se obtuvo el folio de la renta, recarga la ventana nuevamente :(\n");
       }
       if(jTableOrderProvider.getRowCount() == 0){
           message.append("No existen articulos para guardar\n");
       }
       if(this.cmbStatusOrder.getSelectedItem().toString()
               .equals(ApplicationConstants.CMB_SELECCIONE)){
           message.append("Seleccione un status válido\n");
       }
       
       if(!message.toString().equals("")){
           JOptionPane.showMessageDialog(this, message+"", "Error", JOptionPane.INFORMATION_MESSAGE);
           return;
       }
       
      List<DetalleOrdenProveedor> list = new ArrayList<>();
      OrdenProveedor orden = new OrdenProveedor();
      
      Articulo articulo;
      DetalleOrdenProveedor detail;
      for (int i = 0; i < jTableOrderProvider.getRowCount(); i++) {        
           detail = new DetalleOrdenProveedor();
           articulo = new Articulo();
           
           if(!jTableOrderProvider.getValueAt(i, 0).toString().equals("0")){
               // viene de actualizar lo ignoramos
               continue;
           }
           String articuloId = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_ID_ARTICULO).toString();
           String cantidad = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_CANTIDAD).toString();
           String precio = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_PRECIO).toString();
           
           String comentario = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_COMENTARIO).toString();
           
            String tipo = 
                   jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_TIPO_ORDEN).toString();
            String[] arrayTipo = tipo.split("-");
            String tipoFinal = arrayTipo[0].trim();
           
           articulo.setArticuloId(new Integer(articuloId));
           
           detail.setArticulo(articulo);
           detail.setCantidad(new Float(cantidad));
           detail.setPrecio(new Float(precio));
           detail.setTipoOrden(tipoFinal);
           detail.setComentario(comentario);
           
           list.add(detail);
           
      } // end for
      
      orden.setDetalleOrdenProveedorList(list);
      Renta renta = new Renta();
      renta.setRentaId(new Integer(g_rentaId));
      orden.setRenta(renta);
      orden.setUsuario(iniciar_sesion.usuarioGlobal);
      Proveedor proveedor = new Proveedor();
      proveedor.setId(g_provider_id);
      orden.setProveedor(proveedor);
      orden.setStatus(ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER);
      orden.setComentario(txtCommentOrder.getText());
      
      String confirmationMessage=null;
      if(navigation.equals(UPDATE_ORDER)){
          orden.setId(g_order_provider_id);
          try{
            orden.setStatus(orden.getStatusFromDescription(cmbStatusOrder.getSelectedItem().toString()));
            orderProviderService.updateOrder(orden);
          }catch(BusinessException e){
                JOptionPane.showMessageDialog(this, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
          }
          confirmationMessage = ApplicationConstants.MESSAGE_UPDATE_SUCCESSFUL;
          Utility.pushNotification("Se a actualizado orden al proveedor, orden número: "+g_order_provider_id);
      }else{
        try{
          orderProviderService.saveOrder(orden);
        }catch(BusinessException e){
            JOptionPane.showMessageDialog(this, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        confirmationMessage = ApplicationConstants.MESSAGE_SAVE_SUCCESSFUL;
        Utility.pushNotification("Se a agregado orden al proveedor con éxito");
      } 
      
      JOptionPane.showMessageDialog(this, confirmationMessage, "EXITO", JOptionPane.INFORMATION_MESSAGE);
      this.dispose();
      
   }
    
    public void showProviders() {
        SelectProviderToOrder win = new SelectProviderToOrder(null, true, "ORDER_PROVIDER");
        win.setLocationRelativeTo(null);
        win.setVisible(true);
    }
    
     public void showPaymentsProvidersForm() {
         
        if (Utility.verifyIfInternalFormIsOpen(paymentsProvidersForm)) {
            paymentsProvidersForm = new PaymentsProvidersForm();
//            orderProviderForm.setLocation(this.getWidth() / 2 - orderProviderForm.getWidth() / 2, this.getHeight() / 2 - orderProviderForm.getHeight() / 2 - 20);
            principal.jDesktopPane1.add(paymentsProvidersForm);
            paymentsProvidersForm.show();
        } else {
            JOptionPane.showMessageDialog(this, "Ahi ta la ventana =)");
        }
    }
 
    
    public void resetInputBoxes(){
        this.txtPrecioCobrar.setEnabled(false);
        this.txtCantidad.setEnabled(false);
        this.txtComentario.setEnabled(false);
        this.txtArticulo.setEnabled(false);
        
        this.txtPrecioCobrar.setText("");
        this.txtCantidad.setText("");
        this.txtComentario.setText("");
        this.txtArticulo.setText("");
        resetComboOrderType();
    }
    
    public void enabledInputBoxes(){
        this.txtPrecioCobrar.setEnabled(true);
        this.txtCantidad.setEnabled(true);
        this.txtComentario.setEnabled(true);
    }
    
    public void llenar_tabla_articulos(){
        
        List<OrdenProveedor> ordenProveedorList = new ArrayList<>();
                
        navigation = NEW_ORDER;
        if(consultar_renta.g_idRenta == null || 
                consultar_renta.g_idRenta.equals("") ||
                consultar_renta.g_idRenta.isEmpty()){
            // viene de ver ordenes proveedores
            navigation = UPDATE_ORDER;
            g_rentaId = ViewOrdersProviders.g_idRenta;
          g_order_provider_id = new Long(ViewOrdersProviders.g_idOrder);
          try{
            ordenProveedor = orderProviderService.getOrderById(new Long(ViewOrdersProviders.g_idOrder));
          }catch(BusinessException e){
                JOptionPane.showMessageDialog(this, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
          }
          
          if(ordenProveedor == null){
              // que paso aqui???
              JOptionPane.showMessageDialog(this, "Ocurrio un error inesperado, porfavor recarga el sistema", "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
              return;
          }else{
              this.lblInformacionInicial.setText("ORDEN: "+ordenProveedor.getId()+"  ");
          }
           
            
        }else{
            // viene desde consutar renta
            cmbStatusOrder.setEnabled(false);
            cmbStatusOrder.setSelectedItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER);
            g_rentaId = consultar_renta.g_idRenta;
            try{
                ordenProveedorList = orderProviderService.getOrdersByRentaId(new Integer(g_rentaId));
            }catch(BusinessException e){
                JOptionPane.showMessageDialog(this, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            
            if(ordenProveedorList != null && ordenProveedorList.size()==1 ){
                Integer option =
                        JOptionPane.showOptionDialog(this, "Existe una orden, Elija SI para continuar con esta orden o eliga NO para agregar nueva orden " ,"Confirme", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
                if(option == 1){
                 // elijio que NO, agregar nueva orden
                    jMenuItem5.setEnabled(false);
                }else{
                    // elijio que SI, continuar con esta orden
                    ordenProveedor = ordenProveedorList.get(0);
                    this.lblInformacionInicial.setText("ORDEN: "+ordenProveedor.getId()+"  ");
                    this.setTitle("Editar orden al proveedor");
                    navigation = UPDATE_ORDER;
                    g_order_provider_id = ordenProveedor.getId();
                    
                }  
            }else if(ordenProveedorList != null && ordenProveedorList.size()>1 ){
               JOptionPane.showMessageDialog(this, "ATENCI\u00D3N, existen varias ordenes para este folio, al continuar agregar\u00E1s una nueva orden ...", "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
            }else{
                // no existe orden para este folio, inhabilitaremos el boton para ver los pagos al proveedor
                jMenuItem5.setEnabled(false);
            }
            
           
        }
        List<DetalleRenta> detail;
        try {
            detail = saleService.getDetailByRentId(g_rentaId);
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.WARNING_MESSAGE);            
            Toolkit.getDefaultToolkit().beep();
            return;
        }
         DefaultTableModel tablaDetalle = (DefaultTableModel) tablaArticulos.getModel();
         this.lblInformacionInicial.setText(this.lblInformacionInicial.getText()+" FOLIO: "+folio);
          
            for(DetalleRenta detalle : detail){
                    Object fila[] = {                                          
                        detalle.getArticulo().getArticuloId()+"",   
                        detalle.getCantidad()+"",
                        detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor(), 
                        detalle.getArticulo().getPrecioCompra()
                    };
                    tablaDetalle.addRow(fila);
            }
            
        if(navigation.equals(UPDATE_ORDER)){
            cmbStatusOrder.setSelectedItem(ordenProveedor.getStatusDescription());
            txtProviderName.setText(ordenProveedor.getProveedor().getNombre() + " "+ordenProveedor.getProveedor().getApellidos() );
            g_provider_id = ordenProveedor.getProveedor().getId();
            txtCommentOrder.setText(ordenProveedor.getComentario());
            
            // llenamos los articulos del proveedor
             DefaultTableModel tabla = (DefaultTableModel) jTableOrderProvider.getModel();
             
             for(DetalleOrdenProveedor detalle : ordenProveedor.getDetalleOrdenProveedorList()){
                Object fila[] = {                                          
                        detalle.getId(),
                        detalle.getArticulo().getArticuloId(),
                        detalle.getArticulo().getDescripcion()+" "+detalle.getArticulo().getColor().getColor(),
                        detalle.getCantidad(),
                        detalle.getPrecio(),
                        decimalFormat.format(detalle.getCantidad()*detalle.getPrecio()),
                        detalle.getComentario(),
                        detalle.getTipoOrdenDescripcion(),
                        detalle.getCreado(),
                        detalle.getActualizado(),
                        detalle.getStatusDescription()
                    };
                tabla.addRow(fila);
             }
            
        }
        
        this.total();
        ViewOrdersProviders.g_idRenta = null;
        consultar_renta.g_idRenta = null;
         
    }
    
  
    
    public void resetComboOrderType() {
       
        comboOrderType.removeAllItems();
        comboOrderType.addItem(ApplicationConstants.CMB_SELECCIONE);
        comboOrderType.addItem(ApplicationConstants.DS_TYPE_DETAIL_ORDER_SHOPPING);
        comboOrderType.addItem(ApplicationConstants.DS_TYPE_DETAIL_ORDER_RENTAL);
        
        
    }
    
    public void resetCmbOrderStatus(){
        cmbStatusOrder.removeAllItems();
        cmbStatusOrder.addItem(ApplicationConstants.CMB_SELECCIONE);
        cmbStatusOrder.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER);
        cmbStatusOrder.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_PENDING);
        cmbStatusOrder.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_CANCELLED);
        cmbStatusOrder.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_FINISH);
    }
    
    @Deprecated
    public void finishOrder(){
        if(ordenProveedor == null){
            JOptionPane.showMessageDialog(this, "Requerimos de una orden existente para finalizar", "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
            return;
        }
        
        if(!ordenProveedor.getStatus().equals(ApplicationConstants.STATUS_ORDER_PROVIDER_ORDER)){
            JOptionPane.showMessageDialog(this, "Para finalizar la orden se requiere status "+ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER, "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
            return;
        }
        
        StringBuilder stringBuilder = new StringBuilder();
        int cont = 0;
        
                stringBuilder.append("Estos articulos se agregarán a compras\n");
                stringBuilder.append("¿Deseas continuar?\n");
                stringBuilder.append("\n");
        try{
            for (int i = 0; i < jTableOrderProvider.getRowCount(); i++) {
                String status = (String) jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_STATUS);
                String type = (String) jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_TIPO_ORDEN);
                    if(!status.equals(ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED)){
                       throw new BusinessException("El articulo ["+(String) jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_DESCRIPCION_ARTICULO)+
                               "] tiene status diferente a >>> "+ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED
                       );
                    }

                    if(type.equals(ApplicationConstants.DS_TYPE_DETAIL_ORDER_SHOPPING) &&
                            status.equals(ApplicationConstants.DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED)){
                        stringBuilder.append(++cont);
                        stringBuilder.append(". ");
                        stringBuilder.append("Cantidad [");
                        stringBuilder.append((Float) jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_CANTIDAD));
                        stringBuilder.append("] ");
                        stringBuilder.append(
                                (String) jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_DESCRIPCION_ARTICULO));
                        stringBuilder.append("\n");
                    }
            }
        }catch(BusinessException e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
            return;
        }
        Integer optionWithoutItems= null;
        Integer finishOrder= null;
        
        if(cont <= 0){
            optionWithoutItems = 
                    JOptionPane.showOptionDialog(this, "No se encontraron articulos para agregar a COMPRAS, ¿Deseas continuar? " ,"Confirme", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
              
        }else{
            finishOrder =
                     JOptionPane.showOptionDialog(this,  stringBuilder.toString() ,"Confirme", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
        }
        
        if(optionWithoutItems != null 
                && optionWithoutItems == 0){
            // Eligio que si
           JOptionPane.showMessageDialog(this, "FINALIZANDO ORDEN...", "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);            
        }else if(finishOrder != null && finishOrder == 0 ){
            JOptionPane.showMessageDialog(this, "FINALIZANDO ORDEN...", "ATENCI\u00D3N", JOptionPane.WARNING_MESSAGE);    
        }
    }
    
    
    
    public void total(){
        
        float cantidad=0f;
        float precio=0f;
        float subTotal=0f;
        float pagos=0f;
        
                
        for (int i = 0; i < jTableOrderProvider.getRowCount(); i++) {
           cantidad = new Float(jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_CANTIDAD).toString());
           precio = new Float(jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_PRECIO).toString());
           subTotal += (cantidad * precio);
        }
        
        if(ordenProveedor.getPagosProveedor() != null && ordenProveedor.getPagosProveedor().size()>0){
            for(PagosProveedor p : ordenProveedor.getPagosProveedor()){
                pagos += p.getCantidad();
            }
        }
        
        txtPagos.setText(decimalFormat.format(pagos));
        txtSubTotal.setText(decimalFormat.format(subTotal));
        txtTotal.setText(decimalFormat.format(subTotal - pagos));
    }
    
     public void formato_tabla_orden() {
        Object[][] data = {{"","","","","","","","","","",""}};
        String[] columnNames = {
                        "Id_detalle_orden",
                        "Id articulo", 
                        "Articulo",
                        "Cantidad", 
                        "Precio u.",   
                        "Importe",
                        "Comentario",
                        "Tipo Orden",
                        "Creado",
                        "Actualizado",
                        "Status"
                         };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        jTableOrderProvider.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        jTableOrderProvider.setRowSorter(ordenarTabla);

        int[] anchos = {20,20,80,40,40, 80,100,80,80,80,80};

        for (int inn = 0; inn < jTableOrderProvider.getColumnCount(); inn++) {
            jTableOrderProvider.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) jTableOrderProvider.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ORDEN).setMaxWidth(0);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ORDEN).setMinWidth(0);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ORDEN).setPreferredWidth(0);
        
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ARTICULO).setMaxWidth(1);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ARTICULO).setMinWidth(1);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_ID_ARTICULO).setPreferredWidth(1);
        
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_CANTIDAD).setCellRenderer(right);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_PRECIO).setCellRenderer(right);
        jTableOrderProvider.getColumnModel().getColumn(HD_ORDEN_PROVEEDOR_IMPORTE).setCellRenderer(right);
        
    }
     
     public void formato_tabla_articulos() {
        Object[][] data = {{"", "","",""}};
        String[] columnNames = {"id articulo","Cantidad", "Descripci"+ApplicationConstants.ACENTO_O_MINUSCULA+"n","Precio compra"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tablaArticulos.setModel(tableModel);
        
        // Instanciamos el TableRowSorter y lo añadimos al JTable
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tablaArticulos.setRowSorter(ordenarTabla);

        int[] anchos = {20, 40,120,40};

        for (int inn = 0; inn < tablaArticulos.getColumnCount(); inn++) {
            tablaArticulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaArticulos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tablaArticulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaArticulos.getColumnModel().getColumn(0).setPreferredWidth(0);
        tablaArticulos.getColumnModel().getColumn(1).setCellRenderer(centrar);
       

    }

    /**
     * Creates new form OrderProviderForm
     */
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        lblInformacionInicial = new javax.swing.JLabel();
        txtCommentOrder = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtProviderName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cmbStatusOrder = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtArticulo = new javax.swing.JTextField();
        txtPrecioCobrar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtComentario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JButton();
        txtCantidad = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        comboOrderType = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaArticulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableOrderProvider = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jButton1 = new javax.swing.JButton();
        lblQuitarElemento = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        txtTotal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtSubTotal = new javax.swing.JTextField();
        txtPagos = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información general", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblInformacionInicial.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jPanel8.add(lblInformacionInicial, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 260, 20));

        txtCommentOrder.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jPanel8.add(txtCommentOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 260, -1));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Proveedor:");
        jPanel8.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, 20));

        txtProviderName.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtProviderName.setEnabled(false);
        jPanel8.add(txtProviderName, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 260, -1));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Estatus orden:");
        jPanel8.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(461, 80, 80, 20));

        cmbStatusOrder.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbStatusOrder.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel8.add(cmbStatusOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 100, 190, 20));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Comentario:");
        jPanel8.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 43, -1, 20));

        getContentPane().add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 670, 140));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Agregar articulo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Articulo:");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 60, 20));

        txtArticulo.setEditable(false);
        txtArticulo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jPanel4.add(txtArticulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 170, -1));

        txtPrecioCobrar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtPrecioCobrar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPrecioCobrarKeyPressed(evt);
            }
        });
        jPanel4.add(txtPrecioCobrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, 80, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Costo:");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 90, 20));

        txtComentario.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtComentario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtComentarioKeyPressed(evt);
            }
        });
        jPanel4.add(txtComentario, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 40, 340, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Comentario:");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, 60, 20));

        btnAgregar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnAgregar.setText("(+) agregar");
        btnAgregar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        jPanel4.add(btnAgregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 40, 140, 24));

        txtCantidad.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCantidadKeyPressed(evt);
            }
        });
        jPanel4.add(txtCantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 40, 70, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Cantidad:");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 60, 20));

        comboOrderType.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        comboOrderType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel4.add(comboOrderType, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 40, 190, -1));

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 1140, 70));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Articulos del pedido", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        tablaArticulos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tablaArticulos.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaArticulos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tablaArticulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaArticulosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaArticulos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 370, 350));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pedidos al proveedor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jTableOrderProvider.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jTableOrderProvider.setModel(new javax.swing.table.DefaultTableModel(
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
        jTableOrderProvider.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTableOrderProvider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableOrderProviderMouseClicked(evt);
            }
        });
        jTableOrderProvider.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTableOrderProviderKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTableOrderProvider);

        jButton1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton1.setText("(-) quitar elemento");
        jButton1.setToolTipText("Elimina el elemento de la bd");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblQuitarElemento.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblQuitarElemento.setToolTipText("");

        jButton2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton2.setText("Cambiar status");
        jButton2.setToolTipText("Elimina el elemento de la bd");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 111, Short.MAX_VALUE)
                        .addComponent(lblQuitarElemento, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(127, 127, 127)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblQuitarElemento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 240, 770, 370));

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Totales", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtTotal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtTotal.setEnabled(false);
        jPanel9.add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, 110, -1));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Sub total:");
        jPanel9.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 60, 20));

        txtSubTotal.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSubTotal.setEnabled(false);
        jPanel9.add(txtSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 110, -1));

        txtPagos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtPagos.setEnabled(false);
        jPanel9.add(txtPagos, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 50, 110, -1));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total:");
        jPanel9.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 60, 20));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Pagos:");
        jPanel9.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, 60, 20));

        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 10, 460, 140));

        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jMenu2.setText("Archivo");
        jMenu2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem3.setText("Guardar");
        jMenuItem3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem4.setText("Proveedores");
        jMenuItem4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem5.setText("Ver pagos");
        jMenuItem5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuBar1.add(jMenu2);

        jMenu1.setText("Exportar");
        jMenu1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenu1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jMenuItem2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem2.setText("Exportar tabla pedidos");
        jMenuItem2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem1.setText("Exportar tabla articulos");
        jMenuItem1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jMenuItem6.setText("Generar PDF");
        jMenuItem6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPrecioCobrarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioCobrarKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtPrecioCobrarKeyPressed

    private void txtComentarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtComentarioKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtComentarioKeyPressed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // TODO add your handling code here:

        this.agregar_articulo_a_orden();
    }//GEN-LAST:event_btnAgregarActionPerformed

    
    private void txtCantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadKeyPressed

    private void tablaArticulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaArticulosMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

            this.enabledInputBoxes();

            String artId = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_ID_ARTICULO).toString();
            String descripcion = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_DESCRIPCION_ARTICULO).toString();
            String precioCobrar = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_PRECIO_COBRAR).toString();
            this.g_cantidadEnPedido = tablaArticulos.getValueAt(tablaArticulos.getSelectedRow(), HD_ARTICULOS_CANTIDAD_PEDIDO).toString();
            if(this.g_cantidadEnPedido == null || this.g_cantidadEnPedido.equals(""))
            this.g_cantidadEnPedido = "0";
            this.g_articuloId = artId;
            this.txtArticulo.setText(descripcion);
            this.txtCantidad.requestFocus();
            this.txtPrecioCobrar.setText(precioCobrar);

        }
    }//GEN-LAST:event_tablaArticulosMouseClicked

    public void changeStatus(){
        
        if(JOptionPane.showOptionDialog(this, "\u00BFCambiar status? " ,"Confirme para continuar...", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si") != 0){
                    return;
        }else{
             String idDetailOrder = 
                    jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_ID_ORDEN).toString();

             String statusChange;
             try{
                statusChange = this.orderProviderService.changeStatusDetailOrderById(new Long(idDetailOrder));
                jTableOrderProvider.setValueAt(statusChange,jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_STATUS);
             }catch(BusinessException e){
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);            
                Toolkit.getDefaultToolkit().beep();
                return;
             }
        }
    }
    private void jTableOrderProviderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableOrderProviderMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
//            String comentario = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_COMENTARIO).toString();
            String descripcionArticulo = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_DESCRIPCION_ARTICULO).toString();
            this.lblQuitarElemento.setText(descripcionArticulo);
            
            if(descripcionArticulo!=null){
                changeStatus();
            }
        }
    }//GEN-LAST:event_jTableOrderProviderMouseClicked

    private void jTableOrderProviderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTableOrderProviderKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_jTableOrderProviderKeyPressed

     public void agregar_articulo_a_orden(){
        
        StringBuilder mensaje = new StringBuilder();
        int cont = 0;
        
        if(txtArticulo.getText().equals(""))
            mensaje.append(++cont + ". Debes elegir un articulo para agregar el faltante\n");

        float cantidad = 0f;
        float precio = 0f;
        String tipoOrden = this.comboOrderType.getSelectedItem().toString();
        
        
        try {
            cantidad = new Float(this.txtCantidad.getText());
           
            
        } catch (NumberFormatException e) {
            mensaje.append(++cont + ". Error al ingresar la cantidad\n");
        } catch (Exception e) {
            mensaje.append(++cont + ". Error al ingresar la cantidad\n");
        }
        
         try {
           
            precio = new Float(this.txtPrecioCobrar.getText());
            
        } catch (NumberFormatException e) {
            mensaje.append(++cont + ". Error al ingresar el precio\n");
        } catch (Exception e) {
            mensaje.append(++cont + ". Error al ingresar el precio\n");
        }
        
        if(cantidad <= 0 ){
            mensaje.append(++cont + ". La cantidad debe ser mayor a cero\n");
        }
        
        
        if(tipoOrden.equals(ApplicationConstants.CMB_SELECCIONE)){
            mensaje.append(++cont + ". Seleccione tipo de orden\n");
        }
        if(!mensaje.toString().equals("")){
             Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, mensaje+"", "Error", JOptionPane.INFORMATION_MESSAGE);            
            return;
        }
        
        // verificamos si existe para lanzar una advertencia
        boolean existe = false;
        for (int i = 0; i < jTableOrderProvider.getRowCount(); i++) {            
            if (this.g_articuloId.equals(jTableOrderProvider.getValueAt(i, HD_ORDEN_PROVEEDOR_ID_ARTICULO).toString() )
                    ) {
                existe = true;
                break;
            }
        }
        
        if (existe){
            if(JOptionPane.showOptionDialog(this, "Ya existe ese articulo.  \u00BFContinuar? " ,"Confirme", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si") != 0)
                return;                
        }

        String comentario = this.txtComentario.getText();

        String datos[] = {
            "0",
            g_articuloId,
            txtArticulo.getText(),
            cantidad+"",
            precio+"",
            decimalFormat.format(cantidad*precio),
            comentario,
            tipoOrden};       
                
      DefaultTableModel tabla = (DefaultTableModel) jTableOrderProvider.getModel();
      tabla.addRow(datos);
       this.resetInputBoxes();
       this.total();
       
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_NOT_PERMISIONS_ADMIN, "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (jTableOrderProvider.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para continuar ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = jTableOrderProvider.getValueAt(jTableOrderProvider.getSelectedRow(), HD_ORDEN_PROVEEDOR_ID_ORDEN).toString();

        if(id == null || id.equals("")){
            JOptionPane.showMessageDialog(this, "Ocurrio un error, intenta de nuevo o reinicia la aplicacion ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if(id.equals("0")){
            // entonces es primera vez solo eliminamos de la tabla
            DefaultTableModel temp = (DefaultTableModel) this.jTableOrderProvider.getModel();
            temp.removeRow(jTableOrderProvider.getSelectedRow());

        }else{
            if(JOptionPane.showOptionDialog(this, "Se eliminara de la bd,  \u00BFContinuar? " ,"Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si") != 0)
            return;

            String datos[] = {"0",id};
            try {
                funcion.UpdateRegistro(datos, "UPDATE detalle_orden_proveedor SET fg_activo=? WHERE id=?");
                DefaultTableModel temp = (DefaultTableModel) this.jTableOrderProvider.getModel();
                temp.removeRow(jTableOrderProvider.getSelectedRow());
            } catch (Exception e) {
                LOGGER.error(String.format(" ocurrio un error al actualizar los datos [%s] ", e));
                JOptionPane.showMessageDialog(this, "Ocurrio un error actualizar la orden\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        systemService.exportarExcel(tablaArticulos);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        systemService.exportarExcel(jTableOrderProvider);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        this.saveOrderProvider();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        this.showProviders();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        showPaymentsProvidersForm();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if (jTableOrderProvider.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para continuar ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        this.changeStatus();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        try {
            reportPDF(g_order_provider_id+"");
        } catch (Exception e) {
            LOGGER.error(e);
            System.out.println("Mensaje de Error:" + e.toString());
            JOptionPane.showMessageDialog(rootPane, "Error cargando el reporte maestro: " + e.getMessage() + "\n" + e);
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JComboBox cmbStatusOrder;
    private javax.swing.JComboBox comboOrderType;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableOrderProvider;
    private javax.swing.JLabel lblInformacionInicial;
    private javax.swing.JLabel lblQuitarElemento;
    private javax.swing.JTable tablaArticulos;
    private javax.swing.JTextField txtArticulo;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtComentario;
    public static javax.swing.JTextField txtCommentOrder;
    public static javax.swing.JTextField txtPagos;
    private javax.swing.JTextField txtPrecioCobrar;
    public static javax.swing.JTextField txtProviderName;
    public static javax.swing.JTextField txtSubTotal;
    public static javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
