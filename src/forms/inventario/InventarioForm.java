package forms.inventario;

import common.constants.ApplicationConstants;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import mobiliario.AsignarFaltante;
import mobiliario.Categoria;
import mobiliario.Colores;
import mobiliario.VerFoliosPorArticulo;
import mobiliario.iniciar_sesion;
import common.model.Articulo;
import common.model.CategoriaDTO;
import common.model.Color;
import services.CategoryService;
import common.services.ItemService;
import java.beans.PropertyChangeEvent;
import static mobiliario.IndexForm.jDesktopPane1;

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
    private List<Articulo> articulos = new ArrayList<>();
    private final UtilityService utilityService = UtilityService.getInstance();
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0" );

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
        formato_tabla();
        this.setTitle(TITLE);
        lblInfoConsultarDisponibilidad.setText("");
        
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
        
    }
    
    private static void setLblInfoStatusChange () {
        
        final String FORMAT_DATE = "dd/MM/yy"; 
        int rowCount = tablaDisponibilidadArticulos.getRowCount();
        String initDate = txtDisponibilidadFechaInicial.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDisponibilidadFechaInicial.getDate()) : null;
        String endDate = txtDisponibilidadFechaFinal.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDisponibilidadFechaFinal.getDate()) : null;
        
        String message = "";
        
        if (initDate != null && endDate != null) {
            if (rowCount > 0) {
                message = String.format("Articulos a mostrar %s entre el dia %s y %s",rowCount,initDate,endDate);
            } else {
                message = String.format("Mostrar todos los articulos entre el dia %s y %s", initDate,endDate);
            }
        }
        
        lblInfoConsultarDisponibilidad.setText(message);
        
    }
    private enum Column{
        
        BOOLEAN(0,"",Boolean.class, true),
        ID(1,"id",String.class, false),
        CODE(2,"Codigo",String.class,false),
        CATEGORY(3,"Categoria",String.class, false),
        DESCRIPTION(4,"Descripcion",String.class, false),
        COLOR(5,"Color",String.class, false),
        PRICE(6,"P.Unitario",String.class, false),
        STOCK(7,"Stock",String.class, false)
        ;
        
        Column (Integer number, String description, Class clazz, Boolean isEditable) {
            this.number = number;
            this.description = description;
            this.clazz = clazz;
            this.isEditable = isEditable;
        }
        private final Integer number;
        private final String description;
        private final Class clazz;
        private final Boolean isEditable;
        
        public Boolean getIsEditable() {
            return isEditable;
        }
        
        public Class getClazz () {
            return clazz;
        }

        public Integer getNumber() {
            return number;
        }
        
        public String getDescription () {
            return description;
        }
        
    }
    
    public void formato_tabla() {
        
        String[] columnNames = {
            
            Column.BOOLEAN.getDescription(),
            Column.ID.getDescription(),
            Column.CODE.getDescription(), 
            Column.CATEGORY.getDescription(),
            Column.DESCRIPTION.getDescription(), 
            Column.COLOR.getDescription(),                        
            Column.PRICE.getDescription(),
            Column.STOCK.getDescription()
            
        };
        Class[] types = {
            Column.BOOLEAN.getClazz(),
            Column.ID.getClazz(),
            Column.CODE.getClazz(), 
            Column.CATEGORY.getClazz(),
            Column.DESCRIPTION.getClazz(), 
            Column.COLOR.getClazz(),                        
            Column.PRICE.getClazz(),
            Column.STOCK.getClazz()            
        };
        
        boolean[] editable = {
            
            Column.BOOLEAN.getIsEditable(),
            Column.ID.getIsEditable(),
            Column.CODE.getIsEditable(), 
            Column.CATEGORY.getIsEditable(),
            Column.DESCRIPTION.getIsEditable(),
            Column.COLOR.getIsEditable(),                        
            Column.PRICE.getIsEditable(),
            Column.STOCK.getIsEditable()
        };
        
  
        // customize column types
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public Class getColumnClass(int column) {
                return types[column];
            }
            
            @Override
            public boolean isCellEditable (int row, int column) {
                return editable[column];
            }
        };
        tablaDisponibilidadArticulos.setModel(tableModel);
        int[] anchos = {10,20, 120, 250, 100, 40, 40,40};

        for (int inn = 0; inn < tablaDisponibilidadArticulos.getColumnCount(); inn++) {
            tablaDisponibilidadArticulos.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tablaDisponibilidadArticulos.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        tablaDisponibilidadArticulos.getColumnModel().getColumn(Column.ID.getNumber()).setMaxWidth(0);
        tablaDisponibilidadArticulos.getColumnModel().getColumn(Column.ID.getNumber()).setMinWidth(0);
        tablaDisponibilidadArticulos.getColumnModel().getColumn(Column.ID.getNumber()).setPreferredWidth(0);
        
        tablaDisponibilidadArticulos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // int row = table.rowAtPoint(evt.getPoint());
                int col = tablaDisponibilidadArticulos.columnAtPoint(evt.getPoint());
                String name = tablaDisponibilidadArticulos.getColumnName(col);
                System.out.println("Column index selected " + col + " " + name);
                if (col == Column.BOOLEAN.getNumber()) {
                    System.out.println("BOOLEAN");
                }
            }
        });
    }
    
    public static boolean agregarArticulo (String id) {
        
        Articulo articulo = itemService.obtenerArticuloPorId(Integer.parseInt(id));
        
        if(articulo == null)
            return false;

        String dato = null;
         
         // verificamos que el elemento no se encuentre en la lista
        for (int i = 0; i < tablaDisponibilidadArticulos.getRowCount(); i++) {
            dato = tablaDisponibilidadArticulos.getValueAt(i, Column.ID.getNumber()).toString();
            System.out.println("dato seleccionado" + " " + " - " + dato + " - ");
            if (dato.equals(String.valueOf(articulo.getArticuloId()))) {
                 JOptionPane.showMessageDialog(null, "Ya se encuentra el elemento en la lista  ", "Error", JOptionPane.INFORMATION_MESSAGE);
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
        cmb_categoria2.removeAllItems();
        
        
        cmb_categoria.addItem(
                new CategoriaDTO(0, ApplicationConstants.CMB_SELECCIONE)
        );
        
        cmb_categoria2.addItem(
                new CategoriaDTO(0, ApplicationConstants.CMB_SELECCIONE)
        );
        
        for (CategoriaDTO categoria : categorias){
            cmb_categoria.addItem(categoria);
            cmb_categoria2.addItem(categoria);
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

    public void buscar() {
              
       JDialog dialog = UtilityCommon.showDialog("Espere porfavor...","Espere porfavor...", this);
         
        this.formato_tabla_articulos();
        Map<String,Object> map = new HashMap<>();
        
        
               
        CategoriaDTO categoria = (CategoriaDTO) cmb_categoria2.getModel().getSelectedItem();
        Color color = (Color) cmb_color2.getModel().getSelectedItem();
       
        map.put("categoria", categoria);
        map.put("color", color);
        map.put("descripcion", txt_descripcion2.getText());
        
        List<Articulo> articulos = null;
        try {
            articulos = itemService.obtenerArticulosBusquedaInventario(map);
        } catch (Exception e) {
            Logger.getLogger(InventarioForm.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE); 
        } finally {
            Toolkit.getDefaultToolkit().beep();
            dialog.dispose();
        }

        if(articulos == null || articulos.size()<=0){
            JOptionPane.showMessageDialog(null, "Sin resultados obtenidos ", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            return ;
        }
       
        
        for(Articulo articulo : articulos){
            
            DefaultTableModel temp = (DefaultTableModel) tabla_articulos.getModel();
            Object fila[] = {
                  articulo.getArticuloId()+"",
                  articulo.getCodigo(),
                  articulo.getCantidad() != 0 ? decimalFormat.format(articulo.getCantidad()) : "",
                  articulo.getRentados() != 0 ? decimalFormat.format(articulo.getRentados()) : "",
                  
                  articulo.getFaltantes() != 0 ? decimalFormat.format(articulo.getFaltantes()) : "",
                  articulo.getReparacion() != 0 ? decimalFormat.format(articulo.getReparacion()) : "",
                  articulo.getAccidenteTrabajo() != 0 ? decimalFormat.format(articulo.getAccidenteTrabajo()) : "",
                  articulo.getDevolucion() != 0 ? decimalFormat.format(articulo.getDevolucion()) : "",
                  articulo.getTotalCompras() != 0 ? decimalFormat.format(articulo.getTotalCompras()) : "",
                  articulo.getUtiles() != 0 ? decimalFormat.format(articulo.getUtiles()) : "",
                  
                 
                  articulo.getCategoria().getDescripcion(),
                  articulo.getDescripcion(),
                  articulo.getColor().getColor(),
                  articulo.getFechaIngreso(),
                  articulo.getPrecioCompra()+"",
                  articulo.getPrecioRenta()+"",
                  articulo.getFechaUltimaModificacion()
               };
               temp.addRow(fila);
        }

    }

    public void llenar_combo_colores() {
 
        List<Color> colores = itemService.obtenerColores();
        cmb_color.removeAllItems();
        cmb_color2.removeAllItems();
        
        cmb_color2.addItem(
                new Color(0, ApplicationConstants.CMB_SELECCIONE)
        );
        cmb_color.addItem(
                new Color(0, ApplicationConstants.CMB_SELECCIONE)
        );
        for(Color color : colores){
            cmb_color.addItem(color);
            cmb_color2.addItem(color);
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
//        ventana.setLocationRelativeTo(null);
    }
    
    public void mostrar_agregar_articulo() {
        if (articulos.isEmpty()) {
            articulos = itemService.obtenerArticulosActivos();
        }
        AgregarArticuloDisponibilidad ventanaAgregarArticuloDisponibilidad = new AgregarArticuloDisponibilidad(null, true, articulos);
        ventanaAgregarArticuloDisponibilidad.setVisible(true);
        ventanaAgregarArticuloDisponibilidad.setLocationRelativeTo(null);
    }
    
     public void mostrar_ver_disponibilidad_articulos() {
         // mostrara la ventana de disponibilidad de articulos
        String initDate = new SimpleDateFormat("dd/MM/yyyy").format(txtDisponibilidadFechaInicial.getDate());
        String endDate = new SimpleDateFormat("dd/MM/yyyy").format(txtDisponibilidadFechaFinal.getDate());
        List<Long> itemsId = new ArrayList<>();
        for (int i = 0; i < InventarioForm.tablaDisponibilidadArticulos.getRowCount(); i++) {
            itemsId.add(Long.parseLong(tablaDisponibilidadArticulos.getValueAt(i, Column.ID.getNumber()).toString()));
        }
        VerDisponibilidadArticulos ventanaVerDisponibilidad = new VerDisponibilidadArticulos(null, true,initDate,endDate,check_solo_negativos.isSelected(),radioBtnFechaEntrega.isSelected(),radioBtnFechaDevolucion.isSelected(), itemsId, null);
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
        txt_cantidad.setText("");
        txt_descripcion.setText("");
        txt_precio_compra.setText("");
        txt_precio_renta.setText("");
        this.txtCodigo.setText("");
        txt_cantidad.requestFocus();
        cmb_categoria.setSelectedIndex(0);
        cmb_color.setSelectedIndex(0);

    }

    public void agregar() {
        int cont = 0;
        StringBuilder message = new StringBuilder();       
        
        if (txt_cantidad.getText().equals("") 
                || txt_descripcion.getText().equals("") 
                || txt_precio_renta.getText().equals("") 
                || txtCodigo.getText().equals("")
                || cmb_categoria.getSelectedIndex() == 0 
                || cmb_color.getSelectedIndex() == 0) {
            
            JOptionPane.showMessageDialog(null, "Faltan parametros", "Error", JOptionPane.INFORMATION_MESSAGE);
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
            if(!txt_descripcion.getText().equals("") && txt_descripcion.getText().length()>250)
                 message.append(++cont + "La descripcion sobrepasa la longitud permitida, 250 caracteres\n");
            
            if(!message.toString().equals("")){
                JOptionPane.showMessageDialog(null, message.toString(), "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "se a insertado con \u00E9xito el articulo: "+articulo.getDescripcion(), "Error", JOptionPane.INFORMATION_MESSAGE);

            this.formato_tabla_articulos();
            limpiar();

        }
    }

    public void guardar() {
        if (txt_cantidad.getText().equals("") || txt_descripcion.getText().equals("") || txt_precio_compra.getText().equals("") || txt_precio_renta.getText().equals("") || cmb_categoria.getSelectedIndex() == 0 || cmb_color.getSelectedIndex() == 0 ) {
            JOptionPane.showMessageDialog(null, "Faltan parametros", "Error", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "se a actualizado con \u00E9xito el articulo: "+articulo.getDescripcion(), "Error", JOptionPane.INFORMATION_MESSAGE);
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

    private void formato_tabla_articulos(){
        Object[][] data = {{"","","","", "","","", "", "", "", "", "","","",""}};
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
        Object[][] data = {{"", "", "", "", "", "", ""}};
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
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
        cmb_categoria2 = new javax.swing.JComboBox<>();
        txt_descripcion2 = new javax.swing.JTextField();
        cmb_color2 = new javax.swing.JComboBox<>();
        lbl_buscar = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
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
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnAddItem = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        check_solo_negativos = new javax.swing.JCheckBox();
        btnShowAvailivity = new javax.swing.JButton();
        lblInfoConsultarDisponibilidad = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaDisponibilidadArticulos = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        txtDisponibilidadFechaInicial = new com.toedter.calendar.JDateChooser();
        txtDisponibilidadFechaFinal = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        radioBtnTodos = new javax.swing.JRadioButton();
        radioBtnFechaDevolucion = new javax.swing.JRadioButton();
        radioBtnFechaEntrega = new javax.swing.JRadioButton();

        setClosable(true);
        setResizable(true);
        setTitle("Inventario");
        setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12)); // NOI18N
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Inventory-icon.png"))); // NOI18N

        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Panel de busqueda"));

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
        jScrollPane1.setViewportView(tabla_articulos);

        cmb_categoria2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        txt_descripcion2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txt_descripcion2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_descripcion2ActionPerformed(evt);
            }
        });
        txt_descripcion2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_descripcion2KeyPressed(evt);
            }
        });

        cmb_color2.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N

        lbl_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search-icon.png"))); // NOI18N
        lbl_buscar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lbl_buscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_buscarMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Categoría:");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Descripción:");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Color:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_categoria2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_descripcion2, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cmb_color2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbl_buscar))
                            .addComponent(jLabel12))
                        .addGap(0, 355, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel12))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmb_categoria2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_descripcion2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmb_color2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_buscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("agregar, editar articulos", jPanel3);

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

        tablaDisponibilidadArticulos.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tablaDisponibilidadArticulos.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaDisponibilidadArticulos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tablaDisponibilidadArticulosKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(tablaDisponibilidadArticulos);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                .addContainerGap())
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
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

        jTabbedPane1.addTab("consultar disponibilidad", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1219, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void txt_descripcion2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_descripcion2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            buscar();
        }
    }//GEN-LAST:event_txt_descripcion2KeyPressed

    private void txt_descripcion2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_descripcion2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_descripcion2ActionPerformed

    private void lbl_colorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_colorMouseClicked
        // TODO add your handling code here:
        mostrar_colores();
        if (validar_colores == true) {
            llenar_combo_colores();
//            tabla_articulos();
//            buscar();
        }
    }//GEN-LAST:event_lbl_colorMouseClicked

    private void lbl_buscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_buscarMouseClicked
        // TODO add your handling code here:
        buscar();
    }//GEN-LAST:event_lbl_buscarMouseClicked

    private void jbtn_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtn_editarActionPerformed
        // TODO add your handling code here:
        if (tabla_articulos.getSelectedRow() != - 1) {
             if(iniciar_sesion.usuarioGlobal.getAdministrador().equals("0")){
                JOptionPane.showMessageDialog(null, "Solo el administrador puede editar articulos :( ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
             }
             
            String itemId = tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString();
            
            Articulo articulo = itemService.obtenerArticuloPorId(Integer.parseInt(itemId));
             
            if (articulo == null)
            {
             JOptionPane.showMessageDialog(null, "Ocurrio un error al obtener datos del articulo, intenta de nuevo o reinicia la aplicaci\u00F3n", "Error", JOptionPane.INFORMATION_MESSAGE);
             return;
            }
            id_articulo = articulo.getArticuloId()+"";
            jbtn_guardar.setEnabled(true);
            jbtn_agregar.setEnabled(false);
            
            this.txt_cantidad.setText(articulo.getCantidad()+"");
            
            
            this.txtCodigo.setText(articulo.getCodigo());
            this.txt_descripcion.setText(articulo.getDescripcion());
            txt_precio_compra.setText(articulo.getPrecioCompra()+"");
            this.txt_precio_renta.setText(articulo.getPrecioRenta()+"");
            
            cmb_categoria.getModel().setSelectedItem(articulo.getCategoria());
            cmb_color.getModel().setSelectedItem(articulo.getColor());

        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para editar", "Error", JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.showMessageDialog(null, "Solo el administrador puede eliminar articulos :( ", "Error", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int seleccion = JOptionPane.showOptionDialog(this, "¿Eliminar registro: " + (String.valueOf(tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 9))) + "?", "Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "No");
            if ((seleccion + 1) == 1) {
                
                Articulo articulo = new Articulo();
                String artId = tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString();
                articulo.setArticuloId(Integer.parseInt(artId));
                articulo.setActivo("0");
                itemService.actualizarArticulo(articulo);
//                String datos[] = {"0", tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString()};
//                try {
//                    funcion.UpdateRegistro(datos, "UPDATE articulo SET activo=? WHERE id_articulo=?");
//                } catch (SQLNonTransientConnectionException e) {
//                    funcion.conectate();
//                    JOptionPane.showMessageDialog(null, "la conexion se ha cerrado, intenta de nuevo "+e, "Error", JOptionPane.ERROR_MESSAGE); 
//                } catch (SQLException e) {
//                    JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
//                } catch (Exception e) {
//                    JOptionPane.showMessageDialog(null, "ocurrio un error inesperado "+e, "Error", JOptionPane.ERROR_MESSAGE); 
//                }
               
               
                limpiar();
//                tabla_articulos();
//                    buscar();
                this.formato_tabla_articulos();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila para eliminar ", "Error", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Seleciona un articulo para continuar", "Error", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(null, "Seleciona un articulo para continuar", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        this.g_articuloId = Integer.parseInt(tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString());
        Articulo articulo = itemService.obtenerArticuloPorId(g_articuloId);
        if(articulo == null ){
            JOptionPane.showMessageDialog(null, "Ocurrio un error al obtener el articulo, intenta de nuevo porfavor ", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        this.g_articuloId = articulo.getArticuloId();
        this.g_descripcionArticulo = articulo.getDescripcion()+" "+articulo.getColor().getColor();
        this.mostrar_asignar_faltante();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btnMostrarAgregarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarAgregarCompraActionPerformed
        if (tabla_articulos.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Seleciona un articulo para continuar", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
         
        Integer idArticulo = Integer.parseInt(tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString());
         
        Articulo articulo = itemService.obtenerArticuloPorId(idArticulo);
        AgregarCompraFormDialog.articulo = articulo;
         
        this.mostrar_agregar_compra();
         
    }//GEN-LAST:event_btnMostrarAgregarCompraActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        
        if (tabla_articulos.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(null, "Seleciona un articulo para continuar", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
         
        String id = (tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0).toString());
        
        MaterialSaleItemsView win = new MaterialSaleItemsView(id);
        win.setLocation(this.getWidth() / 2 - win.getWidth() / 2, this.getHeight() / 2 - win.getHeight() / 2 - 20);
        jDesktopPane1.add(win);
        win.show();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void tablaDisponibilidadArticulosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaDisponibilidadArticulosKeyPressed
        UtilityCommon.selectCheckBoxWhenKeyPressedIsSpace(evt,tablaDisponibilidadArticulos,Column.BOOLEAN.getNumber());
    }//GEN-LAST:event_tablaDisponibilidadArticulosKeyPressed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
       DefaultTableModel temp = (DefaultTableModel) tablaDisponibilidadArticulos.getModel();
        for( int i = temp.getRowCount() - 1; i >= 0; i-- ){
            if (Boolean.parseBoolean(tablaDisponibilidadArticulos.getValueAt(i, Column.BOOLEAN.getNumber()).toString())) {
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
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
        
        if(!mensaje.isEmpty())
              JOptionPane.showMessageDialog(null, mensaje.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddItem;
    private javax.swing.JButton btnMostrarAgregarCompra;
    private javax.swing.JButton btnShowAvailivity;
    private javax.swing.ButtonGroup buttonGroup1;
    public static javax.swing.JCheckBox check_solo_negativos;
    private javax.swing.JComboBox<CategoriaDTO> cmb_categoria;
    private javax.swing.JComboBox<CategoriaDTO> cmb_categoria2;
    public static javax.swing.JComboBox<Color> cmb_color;
    private javax.swing.JComboBox<Color> cmb_color2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jbtn_agregar;
    private javax.swing.JButton jbtn_editar;
    private javax.swing.JButton jbtn_guardar;
    private javax.swing.JButton jbtn_nuevo;
    private static javax.swing.JLabel lblInfoConsultarDisponibilidad;
    private javax.swing.JLabel lbl_buscar;
    private javax.swing.JLabel lbl_categoria;
    private javax.swing.JLabel lbl_color;
    private javax.swing.JRadioButton radioBtnFechaDevolucion;
    private javax.swing.JRadioButton radioBtnFechaEntrega;
    private javax.swing.JRadioButton radioBtnTodos;
    public static javax.swing.JTable tablaDisponibilidadArticulos;
    private javax.swing.JTable tabla_articulos;
    private javax.swing.JTextField txtCodigo;
    private static com.toedter.calendar.JDateChooser txtDisponibilidadFechaFinal;
    private static com.toedter.calendar.JDateChooser txtDisponibilidadFechaInicial;
    private javax.swing.JTextField txt_cantidad;
    private javax.swing.JTextField txt_descripcion;
    private javax.swing.JTextField txt_descripcion2;
    private javax.swing.JTextField txt_precio_compra;
    private javax.swing.JTextField txt_precio_renta;
    // End of variables declaration//GEN-END:variables
}
