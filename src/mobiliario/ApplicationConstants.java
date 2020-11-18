
package mobiliario;

public interface ApplicationConstants {
    
    public static final String SECRET_KEY = "#yuosSDCG&6729.3";
    
    /** llaves para la tabla configuracion **/
    public static final String SYSTEM_EMAIL_COMPRAS = "email_compras";
    
    /** acentos unicode **/
    
    public static final String ACENTO_A_MINUSCULA = "\u00E1";
    public static final String ACENTO_A_MAYUSCULA = "\u00C1";
    public static final String ACENTO_E_MINUSCULA = "\u00E9";
    public static final String ACENTO_E_MAYUSCULA = "\u00C9";
    public static final String ACENTO_I_MINUSCULA = "\u00ED";
    public static final String ACENTO_I_MAYUSCULA = "\u00CD";
    public static final String ACENTO_O_MINUSCULA = "\u00F3";
    public static final String ACENTO_O_MAYUSCULA = "\u00D3";
    public static final String ACENTO_U_MINUSCULA = "\u00FA";
    public static final String ACENTO_U_MAYUSCULA = "\u00DA";
    
    /** 
     * Tip de orden en el detalle de orden
     */
    public static final String TYPE_DETAIL_ORDER_SHOPPING = "1";
    public static final String TYPE_DETAIL_ORDER_RENTAL = "2";
    
    public static final String DS_TYPE_DETAIL_ORDER_SHOPPING = "1 - compra";
    public static final String DS_TYPE_DETAIL_ORDER_RENTAL = "2 - renta";
    
    /** status detail provider order **/
    public static final String STATUS_ORDER_DETAIL_PROVIDER_PENDING = "1";
    public static final String STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED = "2";
    
    public static final String DS_STATUS_ORDER_DETAIL_PROVIDER_PENDING = "1 - Pendiente";
    public static final String DS_STATUS_ORDER_DETAIL_PROVIDER_ACCEPTED = "2 - Recibido";
    
    /** status provider order **/
    public static final String STATUS_ORDER_PROVIDER_ORDER = "1";
    public static final String STATUS_ORDER_PROVIDER_PENDING = "2";
    public static final String STATUS_ORDER_PROVIDER_CANCELLED = "3";
    public static final String STATUS_ORDER_PROVIDER_FINISH = "4";
    
    public static final String DS_STATUS_ORDER_PROVIDER_ORDER = "1 - Orden";
    public static final String DS_STATUS_ORDER_PROVIDER_PENDING = "2 - Pendiente";
    public static final String DS_STATUS_ORDER_PROVIDER_CANCELLED = "3 - Cancelado";
    public static final String DS_STATUS_ORDER_PROVIDER_FINISH = "4 - Finalizado";
    
    /** Catalogo estados de renta */
    public static final String ESTADO_APARTADO = "1";
    public static final String ESTADO_EN_RENTA = "2";
    public static final String ESTADO_PENDIENTE = "3";
    public static final String ESTADO_CANCELADO = "4";
    public static final String ESTADO_FINALIZADO = "5";
    
    /** Descripcion estados de renta **/
    public static final String DS_ESTADO_APARTADO = "Apartado";
    public static final String DS_ESTADO_EN_RENTA = "En renta";
    public static final String DS_ESTADO_PENDIENTE = "Pendiente";
    public static final String DS_ESTADO_CANCELADO = "Cancelado";
    public static final String DS_ESTADO_FINALIZADO = "Finalizado";
    
    public static final int PUESTO_CHOFER = 1;
    public static final int PUESTO_REPARTIDOR = 2;
    public static final int PUESTO_ADMINISTRADOR = 3;
    public static final int PUESTO_MOSTRADOR = 4;
    
    /** Catalogo tipo de evento */
    public static final String TIPO_PEDIDO = "1";
    public static final String TIPO_COTIZACION = "2";
    
    public static final String LOGO_EMPRESA = "/logo_empresa.jpg";
    
    // Descripcion para la columna de cobranza en CONSULTAR RENTA
    
    public static final String COBRANZA_PAGADO = "Pagado";
    public static final String COBRANZA_PARCIAL_PAGADO = "Parcialmente pagado";
    public static final String COBRANZA_NO_PAGADO = "No pagado";
    
    /* Descripcion para mostrar en la tabla de faltantes */
    public static final String DS_FALTANTES_FALTANTE = "Faltante";
    public static final String DS_FALTANTES_DEVOLUCION = "Devoluci\u00F3n";
    public static final String DS_FALTANTES_REPARACION = "Reparaci\u00F3n";
    public static final String DS_FALTANTES_ACCIDENTE = "Accidente de trabajo";
    
    /* dato inicial para un combo box */
    public static final String CMB_SELECCIONE = "-seleccione-";
    
    /* mensajes para mostrar en los ventanas de avisos */
    public static final String MESSAGE_SAVE_SUCCESSFUL = "Se ha registrado con \u00E9xito";
    public static final String MESSAGE_UPDATE_SUCCESSFUL = "Se actualiz\u00F3 con \u00E9xito";
    public static final String MESSAGE_DELETE_SUCCESSFUL = "Se elimin\u00F3 con \u00E9xito";
    public static final String MESSAGE_NOT_PARAMETER_RECEIVED = "No se recibi\u00F3 parametro";
    public static final String MESSAGE_NOT_PERMISIONS_ADMIN = "No cuentas con permisos de administrador";
    public static final String MESSAGE_MISSING_PARAMETERS = "Faltan parametros";
    
    /* mensaje generico */
    public static final String DS_MESSAGE_FAIL_LOGIN = "Contrase\u00F1a incorrecta o usuario no encontrado";
    public static final String TITLE_MESSAGE_FAIL_LOGIN = "Error al inciar sesion";
    
    // ****************************************************************************************
    // nombres de reportes jasper
    public static final String RUTA_REPORTE_ENTREGAS = "/reporteEntrega.jasper";
    public static final String NOMBRE_REPORTE_ENTREGAS = "/reporteEntrega.pdf";
    public static final String RUTA_REPORTE_CONSULTA = "/renta_consulta.jasper";
    public static final String NOMBRE_REPORTE_CONSULTA = "/reporte_consulta.pdf";
    public static final String RUTA_REPORTE_CATEGORIAS = "/reporte_por_categorias.jasper";
    public static final String NOMBRE_REPORTE_CATEGORIAS = "/reporte_por_categorias.pdf";
    public static final String RUTA_REPORTE_NUEVO_PEDIDO = "/renta.jasper";
    public static final String NOMBRE_REPORTE_NUEVO_PEDIDO = "/reporte.pdf";
    public static final String RUTA_LOGO_EMPRESA = "/";
    public static final String URL_SUB_REPORT_CONSULTA = "/";
     public static final String RUTA_REPORTE_ORDEN_PROVEEDOR = "/reporte_proveedor.jasper";
    public static final String NOMBRE_REPORTE_ORDEN_PROVEEDOR = "/reporte_proveedor.pdf";
    

}
