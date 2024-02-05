package forms.inventario;

import common.form.items.AgregarArticuloDisponibilidadDialog;
import common.form.items.VerDisponibilidadArticulos;
import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.exceptions.InvalidDataException;
import common.services.UtilityService;
import common.utilities.UtilityCommon;
import forms.compras.AgregarCompraFormDialog;
import forms.material.inventory.MaterialSaleItemsView;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import mobiliario.Categoria;
import mobiliario.Colores;
import mobiliario.VerFoliosPorArticulo;
import mobiliario.iniciar_sesion;
import common.model.Articulo;
import common.model.CategoriaDTO;
import common.model.Color;
import common.model.EstadoEvento;
import common.model.Tipo;
import common.services.EstadoEventoService;
import services.CategoryService;
import common.services.ItemService;
import common.services.TipoEventoService;
import common.tables.TableDisponibilidadArticulosShow;
import common.tables.TableItemsByFolio;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import static mobiliario.IndexForm.jDesktopPane1;
import common.model.ItemByFolioResultQuery;
import common.model.SearchItemByFolioParams;
import java.util.Objects;
import java.util.stream.Collectors;
import utilities.Utility;

public class InventarioForm extends javax.swing.JInternalFrame {
    
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(InventarioForm.class.getName());
    public static int g_articuloId;
    public static int g_rentaId;
    public static String g_descripcionArticulo;
    private final String TITLE = "Inventario";
    static Object[][] dtconduc;
    private String id_articulo;
    Object[] datos_combo;
    private String fecha_sistema;
    public static boolean validar_colores, validar_categorias;
    private static final ItemService itemService = ItemService.getInstance();
    private static final CategoryService categoryService = new CategoryService();
    // variable para mandar a la ventana de agregar articulo
    private List<Articulo> items = new ArrayList<>();
    private final UtilityService utilityService = UtilityService.getInstance();
    private static final DecimalFormat decimalFormat = 
            new DecimalFormat( ApplicationConstants.DECIMAL_FORMAT_SHORT );
    private static final DecimalFormat integerFormat = 
            new DecimalFormat( ApplicationConstants.INTEGER_FORMAT );
    private List<Tipo> eventTypes = new ArrayList<>();
    private List<EstadoEvento> eventStatus = new ArrayList<>();
    private final EstadoEventoService estadoEventoService = EstadoEventoService.getInstance();
    private final TipoEventoService tipoEventoService = TipoEventoService.getInstance();
    private final TableItemsByFolio tableItemsByFolio;
    //private final SaleService saleService = SaleService.getInstance();
    private static TableDisponibilidadArticulosShow tablaDisponibilidadArticulos;
    

    public InventarioForm() {

        initComponents();
        formato_tabla_articulos();
        
        new Thread(() -> {
            llenar_combo_colores();
        }).start();
        
        new Thread(() -> {
            llenar_combo_categorias();
        }).start();
        jbtn_guardar.setEnabled(false);        

        if (iniciar_sesion.administrador_global.equals("0")) {
            txt_precio_compra.setEditable(false);
            txt_precio_renta.setEditable(false);
        }

        this.setTitle(TITLE);
        lblInfoConsultarDisponibilidad.setText(ApplicationConstants.EMPTY_STRING);
        
        txtDisponibilidadFechaInicial.getDateEditor().addPropertyChangeListener((PropertyChangeEvent e) -> {
            if ("date".equals(e.getPropertyName())) {
                setLblInfoStatusChange();
            }
        });
        txtDisponibilidadFechaFinal.getDateEditor().addPropertyChangeListener((PropertyChangeEvent e) -> {
            if ("date".equals(e.getPropertyName())) {
                setLblInfoStatusChange();
            }
        });
        
        tableItemsByFolio = new TableItemsByFolio();
        Utility.addJtableToPane(937, 305, panelTableItemsByFolio, tableItemsByFolio);
        
        tablaDisponibilidadArticulos = new TableDisponibilidadArticulosShow();
        Utility.addJtableToPane(937, 305, jPanel6, tablaDisponibilidadArticulos);
        
        eventListener();
        setCmbLimit();
        
        getInitialItems();
        UtilityCommon.setTimeout( () -> txtSearch.requestFocus(), 1000);
    }
    
    private void fillParametersSearchByItemsByFolio () {
        log.info("In fillParametersSearchByItemsByFolio..");
            if (eventTypes.isEmpty()) {
                new Thread(() -> {
                    try {
                        eventTypes = tipoEventoService.get();
                        cmbEventType.removeAllItems();
                        cmbEventType.addItem(
                            new Tipo(0, ApplicationConstants.CMB_SELECCIONE)
                        );
                        eventTypes.stream().forEach(t -> {
                            cmbEventType.addItem(t);
                        });
                    } catch (DataOriginException e) {
                        log.error(e.getMessage(),e);
                        JOptionPane.showMessageDialog(this, e, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);  
                    }
                }).start();
            }

            if (eventStatus.isEmpty()) {
                new Thread(() -> {
                    try {
                        eventStatus = estadoEventoService.get();
                        cmbStatus.removeAllItems();
                        cmbStatus.addItem(
                            new EstadoEvento(0, ApplicationConstants.CMB_SELECCIONE)
                        );
                        eventStatus.stream().forEach(t -> {
                            cmbStatus.addItem(t);
                        });
                    } catch (DataOriginException e) {
                        log.error(e.getMessage(),e);
                        JOptionPane.showMessageDialog(this, e, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);  
                    }
                }).start();
            }        
        
    }
    
    private void setCmbLimit () {
        cmbLimit.removeAllItems();
        cmbLimit.addItem("100");
        cmbLimit.addItem("1000");
        cmbLimit.addItem("5000");
        cmbLimit.addItem("10000");
    }
    
    private void eventListener () {
        tabGeneral.addMouseListener(new MouseAdapter(){
        @Override
        public void mousePressed(MouseEvent e) {
            Component c = tabGeneral.getComponentAt(new Point(e.getX(), e.getY()));
                //TODO Find the right label and print it! :-)
                System.out.println("Selected Index: "+tabGeneral.getSelectedIndex());
                if (tabGeneral.getSelectedIndex() == 2 ) {
                    fillParametersSearchByItemsByFolio();
                }
            }
        });
    }
    
    private SearchItemByFolioParams getParametersToSearchItemsByFolio () throws InvalidDataException{
        
        SearchItemByFolioParams searchItemByFolioParams = new SearchItemByFolioParams();
         
        final String FORMAT_DATE = ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT; 
         
         searchItemByFolioParams.setInitCreatedAtEvent(
                 txtSearchInitialDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtSearchInitialDate.getDate()) : null
         );
         searchItemByFolioParams.setEndCreatedAtEvent(
                 txtSearchEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtSearchEndDate.getDate()) : null
         );
         searchItemByFolioParams.setInitialEventDate(
                txtSearchInitialEventDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtSearchInitialEventDate.getDate()) : null
         );
         searchItemByFolioParams.setEndEventDate(
                 txtSearchEndEventDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtSearchEndEventDate.getDate()) : null
         );
         
         EstadoEvento estadoEvento = (EstadoEvento) cmbStatus.getModel().getSelectedItem();
         Tipo eventType = (Tipo) cmbEventType.getModel().getSelectedItem();
         
         searchItemByFolioParams.setEventStatusId(estadoEvento.getEstadoId());
         searchItemByFolioParams.setEventTypeId(eventType.getTipoId());
         searchItemByFolioParams.setLikeItemDescription(
                 UtilityCommon.removeAccents(txtSearchLikeItemDescription.getText().toLowerCase().trim()));
         
         searchItemByFolioParams.setLimit(Integer.parseInt(cmbLimit.getSelectedItem().toString()));
         try {
             if (!txtSearchFolioRenta.getText().isEmpty()){
                searchItemByFolioParams.setFolio(Long.parseLong(txtSearchFolioRenta.getText()));
             }
         } catch (NumberFormatException e) {
           throw new InvalidDataException("Folio no valido.");
         }
         
         return searchItemByFolioParams;
    
    }
    
    private static void setLblInfoStatusChange () {
        
        final String FORMAT_DATE = "dd/MM/yy"; 
        int rowCount = tablaDisponibilidadArticulos.getRowCount();
        String initDate = txtDisponibilidadFechaInicial.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDisponibilidadFechaInicial.getDate()) : null;
        String endDate = txtDisponibilidadFechaFinal.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDisponibilidadFechaFinal.getDate()) : null;
        
        String message = ApplicationConstants.EMPTY_STRING;
        
        if (initDate != null && endDate != null) {
            if (rowCount > 0) {
                message = String.format("Articulos a mostrar %s entre el dia %s y %s",rowCount,initDate,endDate);
            } else {
                message = String.format("Mostrar todos los articulos entre el dia %s y %s", initDate,endDate);
            }
        }
        
        lblInfoConsultarDisponibilidad.setText(message);
        
    }
    
    public static boolean agregarArticulo (String id) {
        
        Articulo articulo = itemService.obtenerArticuloPorId(Integer.parseInt(id));
        
        if(articulo == null)
            return false;

        String dato = null;
         
         // verificamos que el elemento no se encuentre en la lista
        for (int i = 0; i < tablaDisponibilidadArticulos.getRowCount(); i++) {
            dato = tablaDisponibilidadArticulos.getValueAt(i, TableDisponibilidadArticulosShow.Column.ID.getNumber()).toString();
            System.out.println("dato seleccionado" + " " + " - " + dato + " - ");
            if (dato.equals(String.valueOf(articulo.getArticuloId()))) {
                 JOptionPane.showMessageDialog(null, "Ya se encuentra el elemento en la lista  ", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
                 return false;
            }
        }
        
         DefaultTableModel temp = (DefaultTableModel) tablaDisponibilidadArticulos.getModel();
         Object fila[] = {
               false,
               articulo.getArticuloId(),
               articulo.getCodigo(),
               articulo.getCategoria().getDescripcion(),
               articulo.getDescripcion(),
               articulo.getColor().getColor(),
               articulo.getPrecioRenta(),
               articulo.getCantidad()
         };
         temp.addRow(fila);
         setLblInfoStatusChange();
        return true;
    }

    public void llenar_combo_categorias() {

        List<CategoriaDTO> categorias = categoryService.obtenerCategorias();
        cmb_categoria.removeAllItems();        
        
        cmb_categoria.addItem(
                new CategoriaDTO(0, ApplicationConstants.CMB_SELECCIONE)
        );
        for (CategoriaDTO categoria : categorias){
            cmb_categoria.addItem(categoria);
        }

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
    
    private void getInitialItems () {
       
        items = itemService.obtenerArticulosActivos();
        fillItemTable(items);

    }
    
    private void fillItemTable (List<Articulo> items) {
        
        for(Articulo item : items){
            
            DefaultTableModel temp = (DefaultTableModel) tabla_articulos.getModel();
            Object fila[] = {
                  item.getArticuloId()+ApplicationConstants.EMPTY_STRING,
                  item.getCodigo(),
                  item.getCantidad() != 0 ? integerFormat.format(item.getCantidad()) : ApplicationConstants.EMPTY_STRING,
                  
                  item.getRentados().equals(0F) ? ApplicationConstants.EMPTY_STRING : integerFormat.format(item.getRentados()),
                  
                  item.getFaltantes() != 0 ? integerFormat.format(item.getFaltantes()) : ApplicationConstants.EMPTY_STRING,
                  item.getReparacion() != 0 ? integerFormat.format(item.getReparacion()) : ApplicationConstants.EMPTY_STRING,
                  item.getAccidenteTrabajo() != 0 ? integerFormat.format(item.getAccidenteTrabajo()) : ApplicationConstants.EMPTY_STRING,
                  item.getDevolucion() != 0 ? integerFormat.format(item.getDevolucion()) : ApplicationConstants.EMPTY_STRING,
                  item.getTotalCompras() != 0 ? integerFormat.format(item.getTotalCompras()) : ApplicationConstants.EMPTY_STRING,
                  item.getUtiles() != 0 ? integerFormat.format(item.getUtiles()) : ApplicationConstants.EMPTY_STRING,
                  
                  item.getCategoria().getDescripcion(),
                  item.getDescripcion(),
                  item.getColor().getColor(),
                  item.getFechaIngreso(),
                  decimalFormat.format(item.getPrecioCompra()),
                  decimalFormat.format(item.getPrecioRenta()),
                  item.getFechaUltimaModificacion()
               };
               temp.addRow(fila);
        }
    
    }

    public void llenar_combo_colores() { 
        List<Color> colores = itemService.obtenerColores();
        cmb_color.removeAllItems();
        cmb_color.addItem(
                new Color(0, ApplicationConstants.CMB_SELECCIONE)
        );
        for(Color color : colores){
            cmb_color.addItem(color);
        }
    }

    public void mostrar_colores() {
        Colores ventana_colores = new Colores(null, true);
        ventana_colores.setVisible(true);
        ventana_colores.setLocationRelativeTo(null);
    }
    
    public void mostrar_asignar_faltante() {
        AsignarFaltante ventana = new AsignarFaltante(null, true);
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
    }
    
    public void mostrar_agregar_compra() {
        AgregarCompraFormDialog ventana = new AgregarCompraFormDialog(null, true);
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        
        final int x = (screenSize.width - ventana.getWidth()) / 2;
        final int y = (screenSize.height - ventana.getHeight()) / 2;
        ventana.setLocation(x, y);
        
        ventana.setVisible(true);
    }
    
    public void mostrar_agregar_articulo() {
        if (items.isEmpty()) {
            items = itemService.obtenerArticulosActivos();
        }
        AgregarArticuloDisponibilidadDialog dialog = new AgregarArticuloDisponibilidadDialog(null, true, items);
        String itemId = dialog.showDialog();
        agregarArticulo(itemId);
    }
    
     public void mostrar_ver_disponibilidad_articulos() {
         // mostrara la ventana de disponibilidad de items
        String initDate = new SimpleDateFormat(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT).format(txtDisponibilidadFechaInicial.getDate());
        String endDate = new SimpleDateFormat(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT).format(txtDisponibilidadFechaFinal.getDate());
        List<Long> itemsId = new ArrayList<>();
        for (int i = 0; i < InventarioForm.tablaDisponibilidadArticulos.getRowCount(); i++) {
            itemsId.add(Long.parseLong(tablaDisponibilidadArticulos.getValueAt(i, TableDisponibilidadArticulosShow.Column.ID.getNumber()).toString()));
        }
        VerDisponibilidadArticulos ventanaVerDisponibilidad = new VerDisponibilidadArticulos(
                null,
                true,
                initDate,
                endDate,
                check_solo_negativos.isSelected(),
                radioBtnFechaEntrega.isSelected(),
                radioBtnFechaDevolucion.isSelected(),
                itemsId,
                null,
                null
        );
        ventanaVerDisponibilidad.setVisible(true);
        ventanaVerDisponibilidad.setLocationRelativeTo(null);
    }

    public void mostrar_categorias() {
        Categoria ventana_categoria = new Categoria(null, true);
        ventana_categoria.setVisible(true);
        ventana_categoria.setLocationRelativeTo(null);
    }
    
     public void mostrarVentanaFoliosPorArticulos() {
        g_articuloId = Integer.parseInt(tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString());
        VerFoliosPorArticulo ventana = new VerFoliosPorArticulo(null, true);
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
    }

    public void limpiar() {
        txt_cantidad.setText(ApplicationConstants.EMPTY_STRING);
        txt_descripcion.setText(ApplicationConstants.EMPTY_STRING);
        txt_precio_compra.setText(ApplicationConstants.EMPTY_STRING);
        txt_precio_renta.setText(ApplicationConstants.EMPTY_STRING);
        this.txtCodigo.setText(ApplicationConstants.EMPTY_STRING);
        txt_cantidad.requestFocus();
        cmb_categoria.setSelectedIndex(0);
        cmb_color.setSelectedIndex(0);

    }

    public void agregar() {
        int cont = 0;
        StringBuilder message = new StringBuilder();       
        
        if (txt_cantidad.getText().equals(ApplicationConstants.EMPTY_STRING) 
                || txt_descripcion.getText().equals(ApplicationConstants.EMPTY_STRING) 
                || txt_precio_renta.getText().equals(ApplicationConstants.EMPTY_STRING) 
                || txtCodigo.getText().equals(ApplicationConstants.EMPTY_STRING)
                || cmb_categoria.getSelectedIndex() == 0 
                || cmb_color.getSelectedIndex() == 0) {
            
            JOptionPane.showMessageDialog(null, "Faltan parametros", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
        } else {            
            
            float cant = 0f;
            float precioRenta = 0f;
            try {
                cant = Float.parseFloat(txt_cantidad.getText());
                precioRenta = Float.parseFloat(txt_precio_renta.getText());
            } catch (NumberFormatException e) {
                message.append(++cont + "Error al formatear numero, porfavor verifica que cantidades numericas esten correctas\n");
            }
            
            if(cant < 0)
                message.append(++cont + "Cantidad no puede ser menor a cero\n");
            if(precioRenta < 0)
                message.append(++cont + "Precio de renta no puede ser menor a cero\n");
            if(!txt_descripcion.getText().equals(ApplicationConstants.EMPTY_STRING) && txt_descripcion.getText().length()>250)
                 message.append(++cont + "La descripcion sobrepasa la longitud permitida, 250 caracteres\n");
            
            if(!message.toString().equals(ApplicationConstants.EMPTY_STRING)){
                JOptionPane.showMessageDialog(null, message.toString(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.debug("validaci\u00F3n exitosa para agregar articulo ");
                
            Articulo articulo = new Articulo();
            CategoriaDTO categoria = (CategoriaDTO) cmb_categoria.getModel().getSelectedItem();
            Color color = (Color) cmb_color.getModel().getSelectedItem();
            articulo.setColor(color);
            articulo.setCategoria(categoria);
            articulo.setUsuarioId(iniciar_sesion.usuarioGlobal.getUsuarioId());
            articulo.setCantidad(Float.parseFloat(txt_cantidad.getText()));
            articulo.setDescripcion(txt_descripcion.getText());
            articulo.setPrecioCompra(Float.parseFloat(txt_precio_compra.getText()));
            articulo.setPrecioRenta(Float.parseFloat(txt_precio_renta.getText()));
            articulo.setCodigo(txtCodigo.getText().trim());
            fecha_sistema();
            articulo.setFechaIngreso(fecha_sistema);
            articulo.setActivo("1");
            itemService.insertarArticulo(articulo);
            log.debug("se a insertado con \u00E9xito el articulo: "+articulo.getDescripcion());
            JOptionPane.showMessageDialog(null, "se a insertado con \u00E9xito el articulo: "+articulo.getDescripcion(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);

            this.formato_tabla_articulos();
            limpiar();

        }
    }

    public void guardar() {
        if (txt_cantidad.getText().equals(ApplicationConstants.EMPTY_STRING) || txt_descripcion.getText().equals(ApplicationConstants.EMPTY_STRING) || txt_precio_compra.getText().equals(ApplicationConstants.EMPTY_STRING) || txt_precio_renta.getText().equals(ApplicationConstants.EMPTY_STRING) || cmb_categoria.getSelectedIndex() == 0 || cmb_color.getSelectedIndex() == 0 ) {
            JOptionPane.showMessageDialog(null, "Faltan parametros", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
        } else {
            Articulo articuloAnterior = itemService.obtenerArticuloPorId(Integer.parseInt(id_articulo));
            log.debug("articulo antes de editar es: "+articuloAnterior.toString());

            Articulo articulo = new Articulo();
            CategoriaDTO categoria = (CategoriaDTO) cmb_categoria.getModel().getSelectedItem();
            Color color = (Color) cmb_color.getModel().getSelectedItem();
            articulo.setArticuloId(Integer.parseInt(id_articulo));
            articulo.setColor(color);
            articulo.setCategoria(categoria);
            articulo.setCantidad(Float.parseFloat(txt_cantidad.getText()));
            articulo.setDescripcion(txt_descripcion.getText());
            articulo.setPrecioCompra(Float.parseFloat(txt_precio_compra.getText()));
            articulo.setPrecioRenta(Float.parseFloat(txt_precio_renta.getText()));
            articulo.setCodigo(txtCodigo.getText());
            articulo.setFechaUltimaModificacion(new Timestamp(System.currentTimeMillis()));
            itemService.actualizarArticulo(articulo);
            log.debug("articulo despues de actualizar: "+articulo.toString());
            JOptionPane.showMessageDialog(null, "se a actualizado con \u00E9xito el articulo: "+articulo.getDescripcion(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
            log.debug("el usuario: "+iniciar_sesion.usuarioGlobal.getNombre()+" "+iniciar_sesion.usuarioGlobal.getApellidos()+" a modificado el articulo: "+articulo.getDescripcion()+" con id: "+articulo.getArticuloId());

            this.formato_tabla_articulos();
            limpiar();
        }
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

    public String EliminaCaracteres(String s_cadena, String s_caracteres) {
        String nueva_cadena = ApplicationConstants.EMPTY_STRING;
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

    private void formato_tabla_articulos(){
        Object[][] data = {{ApplicationConstants.EMPTY_STRING,ApplicationConstants.EMPTY_STRING,ApplicationConstants.EMPTY_STRING,ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING,ApplicationConstants.EMPTY_STRING,ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING,ApplicationConstants.EMPTY_STRING,ApplicationConstants.EMPTY_STRING,ApplicationConstants.EMPTY_STRING}};
        String[] columNames = {"Id","Codigo", "Stock","En renta","faltantes","reparacion","accidente trabajo","devolucion","compras","utiles", "Categoria", "Descripcion", "Color", "Fecha", "P Compra", "P Renta","ult. modifiacion"};
        DefaultTableModel tableModel = new DefaultTableModel(data, columNames);
        tabla_articulos.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tabla_articulos.setRowSorter(ordenarTabla);
        
        int[] anchos = {10,60, 30,30,30,30,30,30,30,40, 90, 140, 110, 80, 80, 80,80};

        for (int inn = 0; inn < tabla_articulos.getColumnCount(); inn++) {
            tabla_articulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }
        
         try {
            DefaultTableModel temp = (DefaultTableModel) tabla_articulos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }      
     
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
      
        tabla_articulos.getColumnModel().getColumn(2).setCellRenderer(centrar);
        tabla_articulos.getColumnModel().getColumn(3).setCellRenderer(centrar);
        tabla_articulos.getColumnModel().getColumn(4).setCellRenderer(centrar);
        tabla_articulos.getColumnModel().getColumn(5).setCellRenderer(centrar);
        tabla_articulos.getColumnModel().getColumn(6).setCellRenderer(centrar);
        tabla_articulos.getColumnModel().getColumn(7).setCellRenderer(centrar);
        tabla_articulos.getColumnModel().getColumn(8).setCellRenderer(centrar);
        tabla_articulos.getColumnModel().getColumn(9).setCellRenderer(centrar);
       

        tabla_articulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla_articulos.getColumnModel().getColumn(0).setMinWidth(0);
        tabla_articulos.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    public void formato_tabla_disponibilidad() {
        Object[][] data = {{ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING, ApplicationConstants.EMPTY_STRING}};
        String[] columnNames = {"id_articulo", "cantidad_pedido", "cantidad_inventario", "articulo", "fecha_entrega", "fecha_devolucion", "cliente"};
        DefaultTableModel TableModel = new DefaultTableModel(data, columnNames);
        tablaDisponibilidadArticulos.setModel(TableModel);

        int[] anchos = {20, 100, 100, 180, 120, 120, 240};

        for (int inn = 0; inn < tablaDisponibilidadArticulos.getColumnCount(); inn++) {
            tablaDisponibilidadArticulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaDisponibilidadArticulos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tablaDisponibilidadArticulos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaDisponibilidadArticulos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaDisponibilidadArticulos.getColumnModel().getColumn(0).setPreferredWidth(0);

        tablaDisponibilidadArticulos.getColumnModel().getColumn(1).setCellRenderer(centrar);
        tablaDisponibilidadArticulos.getColumnModel().getColumn(2).setCellRenderer(centrar);

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
        tabGeneral = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        txt_precio_renta = new javax.swing.JTextField();
        txt_precio_compra = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cmb_color = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txt_descripcion = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cmb_categoria = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_cantidad = new javax.swing.JTextField();
        lbl_color = new javax.swing.JLabel();
        lbl_categoria = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla_articulos = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        txtSearch = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jbtn_nuevo = new javax.swing.JButton();
        jbtn_agregar = new javax.swing.JButton();
        jbtn_editar = new javax.swing.JButton();
        jbtn_guardar = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btnMostrarAgregarCompra = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnAddItem = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        check_solo_negativos = new javax.swing.JCheckBox();
        btnShowAvailivity = new javax.swing.JButton();
        lblInfoConsultarDisponibilidad = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        txtDisponibilidadFechaInicial = new com.toedter.calendar.JDateChooser();
        txtDisponibilidadFechaFinal = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        radioBtnTodos = new javax.swing.JRadioButton();
        radioBtnFechaDevolucion = new javax.swing.JRadioButton();
        radioBtnFechaEntrega = new javax.swing.JRadioButton();
        panelItemsByFolio = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jbtnSearch = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtSearchInitialDate = new com.toedter.calendar.JDateChooser();
        txtSearchEndDate = new com.toedter.calendar.JDateChooser();
        txtSearchInitialEventDate = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        txtSearchEndEventDate = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        txtSearchFolioRenta = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        cmbLimit = new javax.swing.JComboBox();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        cmbEventType = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        txtSearchLikeItemDescription = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        lblInfoGeneral = new javax.swing.JLabel();
        panelTableItemsByFolio = new javax.swing.JPanel();

        setClosable(true);
        setResizable(true);
        setTitle("Inventario");
        setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12)); // NOI18N
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Inventory-icon.png"))); // NOI18N

        tabGeneral.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Panel de seleccion"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_precio_renta.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_precio_renta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_precio_rentaKeyPressed(evt);
            }
        });
        jPanel1.add(txt_precio_renta, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 70, -1));

        txt_precio_compra.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jPanel1.add(txt_precio_compra, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 70, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Precio compra:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 110, -1));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Precio renta:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 90, -1));

        cmb_color.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jPanel1.add(cmb_color, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 160, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Color:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 120, 20));

        txt_descripcion.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jPanel1.add(txt_descripcion, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 160, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Descripción:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 120, -1));

        cmb_categoria.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jPanel1.add(cmb_categoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 160, -1));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Categoria:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 67, 20));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Cantidad:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        txt_cantidad.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_cantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cantidadActionPerformed(evt);
            }
        });
        jPanel1.add(txt_cantidad, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 160, -1));

        lbl_color.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lbl_color.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lbl_color.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_colorMouseClicked(evt);
            }
        });
        jPanel1.add(lbl_color, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 230, -1, -1));

        lbl_categoria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Add-icon.png"))); // NOI18N
        lbl_categoria.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lbl_categoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_categoriaMouseClicked(evt);
            }
        });
        lbl_categoria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lbl_categoriaKeyPressed(evt);
            }
        });
        jPanel1.add(lbl_categoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, -1, -1));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Código:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 170, -1));

        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });
        jPanel1.add(txtCodigo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 160, -1));

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
        jScrollPane1.setViewportView(tabla_articulos);

        txtSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Buscar por descripción, color, código o categoría:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 928, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE))
        );

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);
        jToolBar1.setToolTipText("Menu de opciones");

        jbtn_nuevo.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Folder-New-Folder-icon.png"))); // NOI18N
        jbtn_nuevo.setMnemonic('N');
        jbtn_nuevo.setToolTipText("Nuevo");
        jbtn_nuevo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_nuevo.setFocusable(false);
        jbtn_nuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_nuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_nuevoActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_nuevo);

        jbtn_agregar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_agregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Document-Add-icon.png"))); // NOI18N
        jbtn_agregar.setToolTipText("Agregar");
        jbtn_agregar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_agregar.setPreferredSize(new java.awt.Dimension(47, 45));
        jbtn_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_agregarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_agregar);

        jbtn_editar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_editar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Actions-edit-icon.png"))); // NOI18N
        jbtn_editar.setToolTipText("Editar");
        jbtn_editar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_editar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_editar.setPreferredSize(new java.awt.Dimension(47, 45));
        jbtn_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_editarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_editar);

        jbtn_guardar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jbtn_guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/floppy-disk-icon.png"))); // NOI18N
        jbtn_guardar.setToolTipText("Guardar");
        jbtn_guardar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbtn_guardar.setFocusable(false);
        jbtn_guardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtn_guardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtn_guardarActionPerformed(evt);
            }
        });
        jToolBar1.add(jbtn_guardar);

        jButton1.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/folder-remove-icon.png"))); // NOI18N
        jButton1.setMnemonic('E');
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

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/excel-icon.png"))); // NOI18N
        jButton2.setToolTipText("Exportar a Excel");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search-icon_32.png"))); // NOI18N
        jButton3.setToolTipText("Mostrar folios por articulo");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/faltantes_32x.png"))); // NOI18N
        jButton4.setToolTipText("Asignar faltante");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        btnMostrarAgregarCompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/shop-cart-down-icon_32.png"))); // NOI18N
        btnMostrarAgregarCompra.setToolTipText("Mostrar agregar compra");
        btnMostrarAgregarCompra.setFocusable(false);
        btnMostrarAgregarCompra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMostrarAgregarCompra.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMostrarAgregarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMostrarAgregarCompraActionPerformed(evt);
            }
        });
        jToolBar1.add(btnMostrarAgregarCompra);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Inventory-maintenance-icon-32px.png"))); // NOI18N
        jButton5.setToolTipText("Configurar material  para venta de articulo");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton5);

        jButton8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/archive-icon.png"))); // NOI18N
        jButton8.setToolTipText("Desglose de almacen por artículo");
        jButton8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton8);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabGeneral.addTab("Agregar o editar articulos", jPanel3);

        btnAddItem.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnAddItem.setText("Agregar");
        btnAddItem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton6.setText("Quitar");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        check_solo_negativos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        check_solo_negativos.setText("Mostrar solo faltantes");
        check_solo_negativos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_solo_negativosActionPerformed(evt);
            }
        });

        btnShowAvailivity.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnShowAvailivity.setText("Mostrar disponibilidad");
        btnShowAvailivity.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnShowAvailivity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowAvailivityActionPerformed(evt);
            }
        });

        lblInfoConsultarDisponibilidad.setFont(new java.awt.Font("Arial", 3, 12)); // NOI18N
        lblInfoConsultarDisponibilidad.setForeground(new java.awt.Color(204, 0, 51));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(check_solo_negativos, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAddItem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnShowAvailivity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInfoConsultarDisponibilidad, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAddItem)
                            .addComponent(jButton6)
                            .addComponent(check_solo_negativos)
                            .addComponent(btnShowAvailivity)))
                    .addComponent(lblInfoConsultarDisponibilidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 504, Short.MAX_VALUE)
        );

        txtDisponibilidadFechaInicial.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txtDisponibilidadFechaInicial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDisponibilidadFechaInicialMouseClicked(evt);
            }
        });
        txtDisponibilidadFechaInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDisponibilidadFechaInicialKeyPressed(evt);
            }
        });

        txtDisponibilidadFechaFinal.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txtDisponibilidadFechaFinal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDisponibilidadFechaFinalMouseClicked(evt);
            }
        });
        txtDisponibilidadFechaFinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDisponibilidadFechaFinalKeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Fecha inicial");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Fecha Final");

        buttonGroup1.add(radioBtnTodos);
        radioBtnTodos.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        radioBtnTodos.setSelected(true);
        radioBtnTodos.setText("Ver todos los traslapes");
        radioBtnTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnTodosActionPerformed(evt);
            }
        });

        buttonGroup1.add(radioBtnFechaDevolucion);
        radioBtnFechaDevolucion.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        radioBtnFechaDevolucion.setText("Ver por fecha de devolución");
        radioBtnFechaDevolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnFechaDevolucionActionPerformed(evt);
            }
        });

        buttonGroup1.add(radioBtnFechaEntrega);
        radioBtnFechaEntrega.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        radioBtnFechaEntrega.setText("Ver por fecha de entrega");
        radioBtnFechaEntrega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnFechaEntregaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDisponibilidadFechaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(txtDisponibilidadFechaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioBtnTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioBtnFechaEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioBtnFechaDevolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addGap(1, 1, 1)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDisponibilidadFechaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDisponibilidadFechaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(radioBtnTodos)
                        .addComponent(radioBtnFechaDevolucion)
                        .addComponent(radioBtnFechaEntrega))))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabGeneral.addTab("Disponibilidad", jPanel4);

        jbtnSearch.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jbtnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons24/search-24.png"))); // NOI18N
        jbtnSearch.setToolTipText("Buscar");
        jbtnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSearchActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Fecha de creación:");

        txtSearchInitialDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchInitialDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchInitialDateMouseClicked(evt);
            }
        });
        txtSearchInitialDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchInitialDateKeyPressed(evt);
            }
        });

        txtSearchEndDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchEndDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchEndDateMouseClicked(evt);
            }
        });
        txtSearchEndDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchEndDateKeyPressed(evt);
            }
        });

        txtSearchInitialEventDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchInitialEventDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchInitialEventDateMouseClicked(evt);
            }
        });
        txtSearchInitialEventDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchInitialEventDateKeyPressed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Fecha del evento:");

        txtSearchEndEventDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchEndEventDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchEndEventDateMouseClicked(evt);
            }
        });
        txtSearchEndEventDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchEndEventDateKeyPressed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Folio:");

        txtSearchFolioRenta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchFolioRenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchFolioRentaKeyPressed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Limitar resultados a:");

        cmbLimit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbLimit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbStatus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Estado del evento:");

        cmbEventType.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbEventType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Tipo de evento:");

        jButton7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons24/excel-24.png"))); // NOI18N
        jButton7.setToolTipText("Exportar Excel");
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        txtSearchLikeItemDescription.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchLikeItemDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchLikeItemDescriptionKeyPressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel20.setText("Articulo:");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(144, 144, 144)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(cmbEventType, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(txtSearchLikeItemDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(txtSearchInitialEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addComponent(txtSearchEndEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jbtnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(473, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20))))
                .addGap(7, 7, 7)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEventType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtSearchLikeItemDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(7, 7, 7)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSearchInitialEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearchEndEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(7, 7, 7)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(7, 7, 7)
                        .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(7, 7, 7)
                        .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtnSearch)
                            .addComponent(jButton7)))))
        );

        lblInfoGeneral.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        javax.swing.GroupLayout panelTableItemsByFolioLayout = new javax.swing.GroupLayout(panelTableItemsByFolio);
        panelTableItemsByFolio.setLayout(panelTableItemsByFolioLayout);
        panelTableItemsByFolioLayout.setHorizontalGroup(
            panelTableItemsByFolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelTableItemsByFolioLayout.setVerticalGroup(
            panelTableItemsByFolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 443, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTableItemsByFolio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblInfoGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfoGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTableItemsByFolio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelItemsByFolioLayout = new javax.swing.GroupLayout(panelItemsByFolio);
        panelItemsByFolio.setLayout(panelItemsByFolioLayout);
        panelItemsByFolioLayout.setHorizontalGroup(
            panelItemsByFolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelItemsByFolioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelItemsByFolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelItemsByFolioLayout.setVerticalGroup(
            panelItemsByFolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelItemsByFolioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabGeneral.addTab("Artículos por folio", panelItemsByFolio);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 1219, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabGeneral)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_cantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cantidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cantidadActionPerformed

    private void jbtn_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_agregarActionPerformed
        // TODO add your handling code here:
        agregar();
    }//GEN-LAST:event_jbtn_agregarActionPerformed

    private void txt_precio_rentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_precio_rentaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            agregar();
        }
    }//GEN-LAST:event_txt_precio_rentaKeyPressed

    private void lbl_colorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_colorMouseClicked
        // TODO add your handling code here:
        mostrar_colores();
        if (validar_colores == true) {
            llenar_combo_colores();
//            tabla_articulos();
//            buscar();
        }
    }//GEN-LAST:event_lbl_colorMouseClicked

    private void jbtn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editarActionPerformed
        // TODO add your handling code here:
        if (tabla_articulos.getSelectedRow() != - 1) {
             if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Solo el administrador puede editar articulos :( ", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
                return;
             }
             
            String itemId = tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString();
            
            Articulo articulo = itemService.obtenerArticuloPorId(Integer.parseInt(itemId));
             
            if (articulo == null)
            {
             JOptionPane.showMessageDialog(null, "Ocurrio un error al obtener datos del articulo, intenta de nuevo o reinicia la aplicaci\u00F3n", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
             return;
            }
            id_articulo = articulo.getArticuloId()+ApplicationConstants.EMPTY_STRING;
            jbtn_guardar.setEnabled(true);
            jbtn_agregar.setEnabled(false);
            
            this.txt_cantidad.setText(articulo.getCantidad()+ApplicationConstants.EMPTY_STRING);
            
            
            this.txtCodigo.setText(articulo.getCodigo());
            this.txt_descripcion.setText(articulo.getDescripcion());
            txt_precio_compra.setText(articulo.getPrecioCompra()+ApplicationConstants.EMPTY_STRING);
            this.txt_precio_renta.setText(articulo.getPrecioRenta()+ApplicationConstants.EMPTY_STRING);
            
            cmb_categoria.getModel().setSelectedItem(articulo.getCategoria());
            cmb_color.getModel().setSelectedItem(articulo.getColor());

        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para editar", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jbtn_editarActionPerformed

    private void jbtn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_guardarActionPerformed
        // TODO add your handling code here:
        guardar();
        jbtn_guardar.setEnabled(false);
        jbtn_agregar.setEnabled(true);


    }//GEN-LAST:event_jbtn_guardarActionPerformed

    private void jbtn_nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_nuevoActionPerformed
        // TODO add your handling code here:
        limpiar();
        jbtn_agregar.setEnabled(true);
        txt_cantidad.requestFocus();
        jbtn_guardar.setEnabled(false);
    }//GEN-LAST:event_jbtn_nuevoActionPerformed

    private void lbl_categoriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lbl_categoriaKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_lbl_categoriaKeyPressed

    private void lbl_categoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_categoriaMouseClicked
        // TODO add your handling code here:
        mostrar_categorias();
        if (validar_categorias == true) {
            llenar_combo_categorias();
//            tabla_articulos();
//               buscar();
            validar_categorias = false;
        }
    }//GEN-LAST:event_lbl_categoriaMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         
         if (tabla_articulos.getSelectedRow() != -1) {
            if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Solo el administrador puede eliminar articulos :( ", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int seleccion = JOptionPane.showOptionDialog(this, "¿Eliminar registro: " + (String.valueOf(tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 9))) + "?", "Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "No");
            if ((seleccion + 1) == 1) {
                
                Articulo articulo = new Articulo();
                String artId = tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString();
                articulo.setArticuloId(Integer.parseInt(artId));
                articulo.setActivo("0");
                itemService.actualizarArticulo(articulo);
                limpiar();
                this.formato_tabla_articulos();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para eliminar ", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
        }        
      
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       utilityService.exportarExcel(tabla_articulos);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (tabla_articulos.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Seleciona un articulo para continuar", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        this.mostrarVentanaFoliosPorArticulos();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void tabla_articulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_articulosMouseClicked
        if (evt.getClickCount() == 2)
            this.mostrarVentanaFoliosPorArticulos();
    }//GEN-LAST:event_tabla_articulosMouseClicked

    private void check_solo_negativosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_solo_negativosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_check_solo_negativosActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        if (tabla_articulos.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Seleciona un articulo para continuar", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        this.g_articuloId = Integer.parseInt(tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString());
        Articulo articulo = itemService.obtenerArticuloPorId(g_articuloId);
        if(articulo == null ){
            JOptionPane.showMessageDialog(null, "Ocurrio un error al obtener el articulo, intenta de nuevo porfavor ", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        this.g_articuloId = articulo.getArticuloId();
        this.g_descripcionArticulo = articulo.getDescripcion()+" "+articulo.getColor().getColor();
        this.mostrar_asignar_faltante();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnMostrarAgregarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarAgregarCompraActionPerformed
        if (tabla_articulos.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Seleciona un articulo para continuar", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
         
        Integer idArticulo = Integer.parseInt(tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString());
         
        Articulo articulo = itemService.obtenerArticuloPorId(idArticulo);
        AgregarCompraFormDialog.articulo = articulo;
         
        this.mostrar_agregar_compra();
         
    }//GEN-LAST:event_btnMostrarAgregarCompraActionPerformed

    private void exportToExcelItemsByFolio () {
        utilityService.exportarExcel(tableItemsByFolio);
    }
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        
        if (tabla_articulos.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Seleciona un articulo para continuar", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
         
        String id = (tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString());
        
        MaterialSaleItemsView win = new MaterialSaleItemsView(id);
        win.setLocation(this.getWidth() / 2 - win.getWidth() / 2, this.getHeight() / 2 - win.getHeight() / 2 - 20);
        jDesktopPane1.add(win);
        win.show();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void searchItemsByFolio () {
        try {
            SearchItemByFolioParams searchItemByFolioParams
                    = getParametersToSearchItemsByFolio();
            List<ItemByFolioResultQuery> itemByFolioResultQuerys = itemService.getItemsByFolio(searchItemByFolioParams);
            tableItemsByFolio.format();
            if (itemByFolioResultQuerys.isEmpty()) {
                lblInfoGeneral.setText(ApplicationConstants.NO_DATA_FOUND_EXCEPTION);
            } else {
                lblInfoGeneral.setText("Total: "+itemByFolioResultQuerys.size()+", Límite de resultados: "+cmbLimit.getSelectedItem());
                DefaultTableModel tableModel = (DefaultTableModel) tableItemsByFolio.getModel();
                
                    for(ItemByFolioResultQuery item : itemByFolioResultQuerys){
                        Object fila[] = {
                            item.getEventId(),
                            item.getEventFolio(),
                            integerFormat.format(item.getItemAmount()),
                            item.getItemDescription(),
                            decimalFormat.format(item.getItemUnitPrice()),
                            item.getItemDiscountRate() > 0 ? 
                                integerFormat.format(item.getItemDiscountRate()) : ApplicationConstants.EMPTY_STRING,
                            item.getItemSubTotal() > 0 ? 
                                decimalFormat.format(item.getItemSubTotal()) : ApplicationConstants.EMPTY_STRING,
                            item.getEventDeliveryDate(),
                            item.getEventCreatedAtDate(),
                            item.getEventType(),
                            item.getEventStatus()
                        };
                    tableModel.addRow(fila);
                }
            }
        } catch (BusinessException | DataOriginException e) {
            log.error(e.getMessage(),e);
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }finally{
           Toolkit.getDefaultToolkit().beep();
        }
    }
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
       DefaultTableModel temp = (DefaultTableModel) tablaDisponibilidadArticulos.getModel();
        for( int i = temp.getRowCount() - 1; i >= 0; i-- ){
            if (Boolean.parseBoolean(tablaDisponibilidadArticulos.getValueAt(i, TableDisponibilidadArticulosShow.Column.BOOLEAN.getNumber()).toString())) {
                temp.removeRow(i);
            }
        }
        setLblInfoStatusChange();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemActionPerformed
        mostrar_agregar_articulo();
    }//GEN-LAST:event_btnAddItemActionPerformed

    private void btnShowAvailivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAvailivityActionPerformed
        StringBuilder mensaje = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT);
        int contador = 0;
        if ((txtDisponibilidadFechaInicial.getDate() == null 
                || txtDisponibilidadFechaFinal.getDate() == null)) {
            mensaje.append(++contador).append(". Fecha inicial y final son requeridos.\n");
        }else{          
            // 2018-12-04 verificamos que la fecha inicial sea menor a la fecha final
             LocalDate initDate = LocalDate.parse(sdf.format(txtDisponibilidadFechaInicial.getDate()),formatter);
             LocalDate endDate = LocalDate.parse(sdf.format(txtDisponibilidadFechaFinal.getDate()),formatter);
             
             if(initDate.isAfter(endDate))
                  mensaje.append(++contador).append(". Fecha inicial debe ser menor a fecha final.\n");
        }
        
        if(!mensaje.toString().isEmpty())
              JOptionPane.showMessageDialog(this, mensaje.toString(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
        else
            this.mostrar_ver_disponibilidad_articulos();
    }//GEN-LAST:event_btnShowAvailivityActionPerformed

    private void radioBtnFechaEntregaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnFechaEntregaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioBtnFechaEntregaActionPerformed

    private void radioBtnFechaDevolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnFechaDevolucionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioBtnFechaDevolucionActionPerformed

    private void radioBtnTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnTodosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioBtnTodosActionPerformed

    private void txtDisponibilidadFechaFinalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDisponibilidadFechaFinalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDisponibilidadFechaFinalKeyPressed

    private void txtDisponibilidadFechaFinalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDisponibilidadFechaFinalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDisponibilidadFechaFinalMouseClicked

    private void txtDisponibilidadFechaInicialKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDisponibilidadFechaInicialKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDisponibilidadFechaInicialKeyPressed

    private void txtDisponibilidadFechaInicialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDisponibilidadFechaInicialMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDisponibilidadFechaInicialMouseClicked

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        exportToExcelItemsByFolio();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void txtSearchFolioRentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchFolioRentaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10 ) {
            this.searchItemsByFolio();
        }
    }//GEN-LAST:event_txtSearchFolioRentaKeyPressed

    private void txtSearchEndEventDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchEndEventDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndEventDateKeyPressed

    private void txtSearchEndEventDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchEndEventDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndEventDateMouseClicked

    private void txtSearchInitialEventDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchInitialEventDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialEventDateKeyPressed

    private void txtSearchInitialEventDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchInitialEventDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialEventDateMouseClicked

    private void txtSearchEndDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchEndDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndDateKeyPressed

    private void txtSearchEndDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchEndDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndDateMouseClicked

    private void txtSearchInitialDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchInitialDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialDateKeyPressed

    private void txtSearchInitialDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchInitialDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialDateMouseClicked

    private void jbtnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSearchActionPerformed
        // TODO add your handling code here:
        this.searchItemsByFolio();
    }//GEN-LAST:event_jbtnSearchActionPerformed

    private void txtSearchLikeItemDescriptionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchLikeItemDescriptionKeyPressed
        if (evt.getKeyCode() == 10 ) {
            this.searchItemsByFolio();
        }
    }//GEN-LAST:event_txtSearchLikeItemDescriptionKeyPressed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        
        if (tabla_articulos.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(this, ApplicationConstants.SELECT_A_ROW_NECCESSARY, 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
         
        String id = (tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString());
        Articulo item = itemService.obtenerArticuloPorId(Integer.parseInt(id));
        
        DesgloseAlmacenForm form = new DesgloseAlmacenForm(item, items);
        jDesktopPane1.add(form);
        form.show();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyPressed

    }//GEN-LAST:event_txtSearchKeyPressed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        formato_tabla_articulos();        
        List<Articulo> itemsFiltered = 
                UtilityCommon.applyFilterToItems(items,txtSearch.getText());      
        fillItemTable(itemsFiltered);
    }//GEN-LAST:event_txtSearchKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddItem;
    private javax.swing.JButton btnMostrarAgregarCompra;
    private javax.swing.JButton btnShowAvailivity;
    private javax.swing.ButtonGroup buttonGroup1;
    public static javax.swing.JCheckBox check_solo_negativos;
    private javax.swing.JComboBox<Tipo> cmbEventType;
    private javax.swing.JComboBox cmbLimit;
    private javax.swing.JComboBox<EstadoEvento> cmbStatus;
    private javax.swing.JComboBox<CategoriaDTO> cmb_categoria;
    public static javax.swing.JComboBox<Color> cmb_color;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbtnSearch;
    private javax.swing.JButton jbtn_agregar;
    private javax.swing.JButton jbtn_editar;
    private javax.swing.JButton jbtn_guardar;
    private javax.swing.JButton jbtn_nuevo;
    private static javax.swing.JLabel lblInfoConsultarDisponibilidad;
    private javax.swing.JLabel lblInfoGeneral;
    private javax.swing.JLabel lbl_categoria;
    private javax.swing.JLabel lbl_color;
    private javax.swing.JPanel panelItemsByFolio;
    private javax.swing.JPanel panelTableItemsByFolio;
    public static javax.swing.JRadioButton radioBtnFechaDevolucion;
    public static javax.swing.JRadioButton radioBtnFechaEntrega;
    public static javax.swing.JRadioButton radioBtnTodos;
    private javax.swing.JTabbedPane tabGeneral;
    private javax.swing.JTable tabla_articulos;
    private javax.swing.JTextField txtCodigo;
    private static com.toedter.calendar.JDateChooser txtDisponibilidadFechaFinal;
    private static com.toedter.calendar.JDateChooser txtDisponibilidadFechaInicial;
    private javax.swing.JTextField txtSearch;
    private com.toedter.calendar.JDateChooser txtSearchEndDate;
    private com.toedter.calendar.JDateChooser txtSearchEndEventDate;
    private javax.swing.JTextField txtSearchFolioRenta;
    private com.toedter.calendar.JDateChooser txtSearchInitialDate;
    private com.toedter.calendar.JDateChooser txtSearchInitialEventDate;
    private javax.swing.JTextField txtSearchLikeItemDescription;
    private javax.swing.JTextField txt_cantidad;
    private javax.swing.JTextField txt_descripcion;
    private javax.swing.JTextField txt_precio_compra;
    private javax.swing.JTextField txt_precio_renta;
    // End of variables declaration//GEN-END:variables
}
