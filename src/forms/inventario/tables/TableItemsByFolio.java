package forms.inventario.tables;

import java.awt.Font;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class TableItemsByFolio extends JTable {

    public TableItemsByFolio() {
       
        this.setFont(new Font( "Arial" , Font.PLAIN, 11 ));
        format();
        
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    public void format () {
        setModel(
                new DefaultTableModel(columnNames,0)
        );
        
        for (int inn = 0; inn < this.getColumnCount(); inn++) {
            this.getColumnModel().getColumn(inn).setPreferredWidth(sizes[inn]);
        }
        
        try {
            DefaultTableModel temp = (DefaultTableModel) this.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(this.dataModel); 
        this.setRowSorter(ordenarTabla);
        
         this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setPreferredWidth(0);
     
        this.getColumnModel().getColumn(Column.AMOUNT.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.PRICE.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.SUBTOTAL.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.DISCOUNT.getNumber()).setCellRenderer(right);
        
        
        this.getColumnModel().getColumn(Column.EVENT_DATE.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.EVENT_STATUS.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.EVENT_TYPE.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.DELIVERY_DATE.getNumber()).setCellRenderer(centrar);
    }
    
    private final String[] columnNames = {       
            Column.RENTA_ID.getDescription(),
            Column.FOLIO.getDescription(),
            Column.AMOUNT.getDescription(),
            Column.ITEM.getDescription(),
            Column.PRICE.getDescription(),
            Column.DISCOUNT.getDescription(),
            Column.SUBTOTAL.getDescription(),
            Column.DELIVERY_DATE.getDescription(),
            Column.EVENT_DATE.getDescription(),
            Column.EVENT_TYPE.getDescription(),
            Column.EVENT_STATUS.getDescription()
        };
    
    private final int[] sizes = {
            Column.RENTA_ID.getSize(),
            Column.FOLIO.getSize(),
            Column.AMOUNT.getSize(),
            Column.ITEM.getSize(),
            Column.PRICE.getSize(),
            Column.DISCOUNT.getSize(),
            Column.SUBTOTAL.getSize(),
            Column.DELIVERY_DATE.getSize(),
            Column.EVENT_DATE.getSize(),
            Column.EVENT_TYPE.getSize(),
            Column.EVENT_STATUS.getSize()
        };
    
    
    public enum Column{
       RENTA_ID(0,"renta_id",20), 
       FOLIO(1,"Folio",20),
       AMOUNT(2,"Cantidad",20),
       ITEM(3,"Articulo",80),
       PRICE(4,"Precio",40),
       DISCOUNT(5,"Descuento %",40),
       SUBTOTAL(6,"Importe",40),
       DELIVERY_DATE(7,"Fecha entrega",80),
       EVENT_DATE(8,"Fecha creacion evento",80),
       EVENT_TYPE(9,"Tipo Evento",80),
       EVENT_STATUS(10,"Estado Evento",80);
       
       private final int number;
       private final String description;
       private final int size;

        Column(int number, String description, int size) {
            this.number = number;
            this.description = description;
            this.size = size;
        }
        public int getSize () {
            return this.size;
        }
        public int getNumber () {
            return this.number;
        }
        
        public String getDescription () {
            return this.description;
        }
        
    }
    
}
