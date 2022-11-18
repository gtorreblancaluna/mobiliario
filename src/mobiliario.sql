CREATE TABLE cuenta (
  id int(11) NOT NULL AUTO_INCREMENT,
  descripcion varchar(150) DEFAULT NULL,
  created_at timestamp NULL DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL,
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE tipo_evento (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  descripcion varchar(145) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tipo;
CREATE TABLE tipo (
  id_tipo int(10) unsigned NOT NULL AUTO_INCREMENT,
  tipo varchar(45) NOT NULL,
  PRIMARY KEY (id_tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS email;
CREATE TABLE email (
  id int(11) NOT NULL AUTO_INCREMENT,
  cuenta_correo varchar(45) NOT NULL,
  contrasenia varchar(45) NOT NULL,
  servidor varchar(45) NOT NULL,
  puerto varchar(45) NOT NULL,
  utiliza_conexion_TLS varchar(45) NOT NULL,
  utiliza_autenticacion varchar(45) NOT NULL,
  gmail varchar(45) NOT NULL,
  hotmail varchar(45) NOT NULL,
  personalizada varchar(45) NOT NULL,
  creado timestamp NULL DEFAULT NULL,
  actualizado timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS clientes;
CREATE TABLE clientes (
  id_clientes int(11) NOT NULL AUTO_INCREMENT,
  nombre varchar(45) DEFAULT NULL,
  apellidos varchar(45) DEFAULT NULL,
  apodo varchar(45) DEFAULT NULL,
  tel_movil varchar(45) DEFAULT NULL,
  tel_fijo varchar(45) DEFAULT NULL,
  email varchar(45) DEFAULT NULL,
  direccion varchar(500) DEFAULT NULL,
  localidad varchar(45) DEFAULT NULL,
  rfc varchar(45) DEFAULT NULL,
  activo varchar(5) DEFAULT NULL,
  PRIMARY KEY (id_clientes)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS estado;
CREATE TABLE estado (
  id_estado int(11) NOT NULL AUTO_INCREMENT,
  descripcion varchar(45) DEFAULT NULL,
  PRIMARY KEY (id_estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS proveedores;
CREATE TABLE proveedores (
  id int(11) NOT NULL AUTO_INCREMENT,
  nombre varchar(450) DEFAULT NULL,
  apellidos varchar(250) DEFAULT NULL,
  direccion varchar(250) DEFAULT NULL,
  telefonos varchar(250) DEFAULT NULL,
  email varchar(250) DEFAULT NULL,
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  creado timestamp NULL DEFAULT NULL,
  actualizado timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS puesto;
CREATE TABLE puesto (
  id_puesto int(11) NOT NULL AUTO_INCREMENT,
  descripcion varchar(45) DEFAULT NULL,
  PRIMARY KEY (id_puesto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS categoria;
CREATE TABLE categoria (
  id_categoria int(11) NOT NULL AUTO_INCREMENT,
  descripcion varchar(45) DEFAULT NULL,
  PRIMARY KEY (id_categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS tipo_abono;
CREATE TABLE tipo_abono (
  id_tipo_abono int(11) NOT NULL AUTO_INCREMENT,
  descripcion varchar(150) DEFAULT NULL,
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  fecha_registro timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  cuenta_id int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (id_tipo_abono),
  KEY fk_cuenta_tipo_abono (cuenta_id),
  CONSTRAINT tipo_abono_ibfk_1 FOREIGN KEY (cuenta_id) REFERENCES cuenta (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS categoria_contabilidad;
CREATE TABLE categoria_contabilidad (
  id int(11) NOT NULL AUTO_INCREMENT,
  descripcion varchar(150) DEFAULT NULL,
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  fecha_registro timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS color;
CREATE TABLE color (
  id_color int(11) NOT NULL AUTO_INCREMENT,
  color varchar(45) DEFAULT NULL,
  tono varchar(45) DEFAULT NULL,
  comentario varchar(75) DEFAULT NULL,
  PRIMARY KEY (id_color)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
  id_usuarios int(11) NOT NULL AUTO_INCREMENT,
  nombre varchar(45) DEFAULT NULL,
  apellidos varchar(45) DEFAULT NULL,
  tel_movil varchar(45) DEFAULT NULL,
  tel_fijo varchar(45) DEFAULT NULL,
  direccion varchar(45) DEFAULT NULL,
  administrador varchar(5) DEFAULT NULL,
  nivel1 varchar(5) DEFAULT NULL,
  nivel2 varchar(5) DEFAULT NULL,
  contrasenia varchar(45) DEFAULT NULL,
  activo varchar(5) DEFAULT NULL,
  id_puesto int(11) DEFAULT NULL,
  PRIMARY KEY (id_usuarios),
  KEY fk_usuarios_puesto1_idx (id_puesto),
  CONSTRAINT fk_usuarios_puesto1 FOREIGN KEY (id_puesto) REFERENCES puesto (id_puesto) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS articulo;
CREATE TABLE articulo (
id_articulo int(11) NOT NULL AUTO_INCREMENT,
id_categoria int(11) NOT NULL,
id_usuario int(11) NOT NULL,
cantidad float DEFAULT NULL,
descripcion varchar(250) DEFAULT NULL,
id_color int(11) DEFAULT NULL,
fecha_ingreso varchar(45) DEFAULT NULL,
precio_compra float DEFAULT NULL,
precio_renta float DEFAULT NULL,
activo varchar(5) DEFAULT NULL,
stock float DEFAULT NULL,
codigo varchar(45) DEFAULT NULL,
en_renta float DEFAULT NULL,
fecha_ultima_modificacion timestamp NULL DEFAULT NULL,
PRIMARY KEY (id_articulo),
KEY fk_articulo_usuarios1_idx (id_usuario),
KEY fk_articulo_cateogria1_idx (id_categoria),
KEY fk_articulo_color1_idx (id_color),
CONSTRAINT fk_articulo_cateogria1 FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria) ON DELETE NO ACTION ON UPDATE NO ACTION,
CONSTRAINT fk_articulo_color1 FOREIGN KEY (id_color) REFERENCES color (id_color) ON DELETE NO ACTION ON UPDATE NO ACTION,
CONSTRAINT fk_articulo_usuarios1 FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuarios) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS asigna_categoria;
CREATE TABLE asigna_categoria (
  id_asigna_categoria int(11) NOT NULL AUTO_INCREMENT,
  id_usuarios int(11) NOT NULL,
  id_categoria int(11) NOT NULL,
  fecha_alta timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_asigna_categoria),
  KEY FK_id_usuarios (id_usuarios),
  KEY FK_id_categoria (id_categoria),
  CONSTRAINT FK_id_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_id_usuarios FOREIGN KEY (id_usuarios) REFERENCES usuarios (id_usuarios) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS compras;
CREATE TABLE compras (
  id int(11) NOT NULL AUTO_INCREMENT,
  id_articulo int(11) NOT NULL,
  comentario varchar(250) DEFAULT NULL,
  cantidad float DEFAULT NULL,
  precio_compra float DEFAULT NULL,
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  creado timestamp NULL DEFAULT NULL,
  actualizado timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_compras_articulo_id (id_articulo),
  CONSTRAINT fk_compras_articulo_id FOREIGN KEY (id_articulo) REFERENCES articulo (id_articulo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS configuracion;
CREATE TABLE configuracion (
  id int(11) NOT NULL AUTO_INCREMENT,
  llave varchar(455) NOT NULL,
  valor varchar(455) NOT NULL,
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  creado timestamp NULL DEFAULT NULL,
  actualizado timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS sub_categoria_contabilidad;
CREATE TABLE sub_categoria_contabilidad (
  id int(11) NOT NULL AUTO_INCREMENT,
  id_categoria_contabilidad int(11) NOT NULL,
  descripcion varchar(150) DEFAULT NULL,
  ingreso enum('1','0') NOT NULL DEFAULT '1',
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id),
  KEY fk_categoria_contabilidad (id_categoria_contabilidad),
  CONSTRAINT fk_categoria_contabilidad FOREIGN KEY (id_categoria_contabilidad) REFERENCES categoria_contabilidad (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS contabilidad;
CREATE TABLE contabilidad (
id int(11) NOT NULL AUTO_INCREMENT,
id_sub_categoria_contabilidad int(11) NOT NULL,
id_usuarios int(11) NOT NULL,
cuenta_id int(11) NOT NULL,
fecha_registro timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
fecha_movimiento timestamp NULL DEFAULT NULL,
comentario varchar(350) DEFAULT NULL,
fg_activo enum('1','0') NOT NULL DEFAULT '1',
cantidad float DEFAULT NULL,
PRIMARY KEY (id),
KEY fk_contabilidad_sub_categoria_contablidad (id_sub_categoria_contabilidad),
KEY fk_contabilidad_usuario (id_usuarios),
KEY fk_contabilidad_cuenta (cuenta_id),
CONSTRAINT fk_contabilidad_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuenta (id),
CONSTRAINT fk_contabilidad_sub_categoria_contablidad FOREIGN KEY (id_sub_categoria_contabilidad) REFERENCES sub_categoria_contabilidad (id),
CONSTRAINT fk_contabilidad_usuario FOREIGN KEY (id_usuarios) REFERENCES usuarios (id_usuarios)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS datos_generales;
CREATE TABLE datos_generales (
  id_datos_generales int(10) unsigned NOT NULL AUTO_INCREMENT,
  nombre_empresa varchar(455) DEFAULT NULL,
  direccion1 varchar(900) DEFAULT NULL,
  direccion2 varchar(900) DEFAULT NULL,
  direccion3 varchar(900) DEFAULT NULL,
  folio int(10) unsigned NOT NULL,
  folio_cambio varchar(2) DEFAULT NULL,
   info_summary_folio VARCHAR(9028) DEFAULT NULL,
  PRIMARY KEY (id_datos_generales)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS renta;
CREATE TABLE renta (
id_renta int(11) NOT NULL AUTO_INCREMENT,
id_estado int(11) DEFAULT NULL,
id_clientes int(11) DEFAULT NULL,
id_usuarios int(11) DEFAULT NULL,
fecha_pedido varchar(45) DEFAULT NULL,
fecha_entrega varchar(45) DEFAULT NULL,
hora_entrega varchar(145) DEFAULT NULL,
fecha_devolucion varchar(45) DEFAULT NULL,
descripcion varchar(400) DEFAULT NULL,
descuento varchar(80) DEFAULT NULL,
cantidad_descuento float DEFAULT NULL,
iva float DEFAULT NULL,
comentario varchar(500) DEFAULT NULL,
id_usuario_chofer int(11) DEFAULT NULL,
folio int(10) unsigned DEFAULT NULL,
stock varchar(4) DEFAULT NULL,
id_tipo int(10) unsigned NOT NULL,
hora_devolucion varchar(145) DEFAULT NULL,
fecha_evento varchar(45) DEFAULT NULL,
deposito_garantia float DEFAULT NULL,
envio_recoleccion float DEFAULT NULL,
mostrar_precios_pdf enum('0','1') NOT NULL DEFAULT '1',
PRIMARY KEY (id_renta),
CONSTRAINT FK_chofer FOREIGN KEY (id_usuario_chofer) REFERENCES usuarios (id_usuarios),
CONSTRAINT FK_tipo FOREIGN KEY (id_tipo) REFERENCES tipo (id_tipo),
CONSTRAINT fk_renta_clientes1 FOREIGN KEY (id_clientes) REFERENCES clientes (id_clientes) ON DELETE NO ACTION ON UPDATE NO ACTION,
CONSTRAINT fk_renta_estado FOREIGN KEY (id_estado) REFERENCES estado (id_estado) ON DELETE NO ACTION ON UPDATE NO ACTION,
CONSTRAINT fk_renta_usuarios1 FOREIGN KEY (id_usuarios) REFERENCES usuarios (id_usuarios) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS orden_proveedor;
CREATE TABLE orden_proveedor (
  id int(11) NOT NULL AUTO_INCREMENT,
  id_renta int(11) NOT NULL,
  id_usuario int(11) NOT NULL,
  id_proveedores int(11) NOT NULL,
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  status enum('1','2','3','4') NOT NULL,
  comentario varchar(450) DEFAULT NULL,
  creado timestamp NULL DEFAULT NULL,
  actualizado timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_orden_proveedor_renta_id (id_renta),
  KEY fk_orden_proveedor_usuario_id (id_usuario),
  KEY fk_orden_proveedor_proveedor_id (id_proveedores),
  CONSTRAINT fk_orden_proveedor_proveedor_id FOREIGN KEY (id_proveedores) REFERENCES proveedores (id),
  CONSTRAINT fk_orden_proveedor_renta_id FOREIGN KEY (id_renta) REFERENCES renta (id_renta),
  CONSTRAINT fk_orden_proveedor_usuario_id FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuarios)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS detalle_orden_proveedor;
CREATE TABLE detalle_orden_proveedor (
  id int(11) NOT NULL AUTO_INCREMENT,
  id_orden_proveedor int(11) NOT NULL,
  id_articulo int(11) NOT NULL,
  tipo_orden_detalle_proveedor_id INT(11) NOT NULL,
  cantidad float DEFAULT NULL,
  precio float DEFAULT NULL,
  comentario varchar(450) DEFAULT NULL,
  tipo_orden enum('1','2') NOT NULL DEFAULT '1',
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  creado timestamp NULL DEFAULT NULL,
  actualizado timestamp NULL DEFAULT NULL,
  status enum('1','2') NOT NULL DEFAULT '1',
  PRIMARY KEY (id),
  KEY fk_detalle_orden_proveedor_orden_proveedor_id (id_orden_proveedor),
  KEY fk_orden_proveedor_id_articulo (id_articulo),
  CONSTRAINT fk_tipo_orden_detalle_proveedor_id FOREIGN KEY (tipo_orden_detalle_proveedor_id) REFERENCES tipo_detalle_orden_proveedor (id),
  CONSTRAINT fk_detalle_orden_proveedor_orden_proveedor_id FOREIGN KEY (id_orden_proveedor) REFERENCES orden_proveedor (id),
  CONSTRAINT fk_orden_proveedor_id_articulo FOREIGN KEY (id_articulo) REFERENCES articulo (id_articulo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS detalle_renta;
CREATE TABLE detalle_renta (
  id_detalle_renta int(11) NOT NULL AUTO_INCREMENT,
  id_renta int(11) DEFAULT NULL,
  cantidad float DEFAULT NULL,
  id_articulo int(11) DEFAULT NULL,
  p_unitario float DEFAULT NULL,
  comentario varchar(75) DEFAULT NULL,
  se_desconto varchar(2) DEFAULT NULL,
  porcentaje_descuento float DEFAULT NULL,
  PRIMARY KEY (id_detalle_renta),
  KEY fk_detalle_renta_renta1_idx (id_renta),
  KEY FK_articulo (id_articulo),
  CONSTRAINT FK_articulo FOREIGN KEY (id_articulo) REFERENCES articulo (id_articulo),
  CONSTRAINT fk_detalle_renta_renta1 FOREIGN KEY (id_renta) REFERENCES renta (id_renta) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS faltantes;
CREATE TABLE faltantes (
  id_faltante int(11) NOT NULL AUTO_INCREMENT,
  id_articulo int(11) NOT NULL,
  id_renta int(11) NOT NULL,
  id_usuarios int(11) NOT NULL,
  fecha_registro timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  cantidad float DEFAULT NULL,
  comentario varchar(350) DEFAULT NULL,
  fg_faltante enum('1','0') NOT NULL DEFAULT '1',
  fg_devolucion enum('1','0') NOT NULL DEFAULT '0',
  fg_accidente_trabajo enum('1','0') NOT NULL DEFAULT '0',
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  precio_cobrar float DEFAULT NULL,
  PRIMARY KEY (id_faltante),
  KEY fk_faltantes_articulo (id_articulo),
  KEY fk_faltantes_renta (id_renta),
  KEY fk_faltantes_usuario (id_usuarios),
  CONSTRAINT fk_faltantes_articulo FOREIGN KEY (id_articulo) REFERENCES articulo (id_articulo),
  CONSTRAINT fk_faltantes_renta FOREIGN KEY (id_renta) REFERENCES renta (id_renta),
  CONSTRAINT fk_faltantes_usuario FOREIGN KEY (id_usuarios) REFERENCES usuarios (id_usuarios)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS pagos_proveedor;
CREATE TABLE pagos_proveedor (
  id int(11) NOT NULL AUTO_INCREMENT,
  id_orden_proveedor int(11) NOT NULL,
  id_usuario int(11) NOT NULL,
  id_tipo_abono int(11) NOT NULL,
  cantidad float NOT NULL,
  comentario varchar(450) DEFAULT NULL,
  fg_activo enum('1','0') NOT NULL DEFAULT '1',
  creado timestamp NULL DEFAULT NULL,
  actualizado timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_abono_proveedor_orden_proveedor_id (id_orden_proveedor),
  KEY fk_abono_proveedor_tipo_abono_id (id_tipo_abono),
  KEY fk_abono_proveedor_usuario_id (id_usuario),
  CONSTRAINT fk_abono_proveedor_orden_proveedor_id FOREIGN KEY (id_orden_proveedor) REFERENCES orden_proveedor (id),
  CONSTRAINT fk_abono_proveedor_tipo_abono_id FOREIGN KEY (id_tipo_abono) REFERENCES tipo_abono (id_tipo_abono),
  CONSTRAINT fk_abono_proveedor_usuario_id FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuarios)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE abonos (
  id_abonos int(10) unsigned NOT NULL AUTO_INCREMENT,
  id_renta int(11) NOT NULL,
  id_usuario int(11) NOT NULL,
  fecha varchar(45) NOT NULL,
  abono float NOT NULL,
  comentario varchar(45) DEFAULT NULL,
  id_tipo_abono int(11) NOT NULL DEFAULT '1',
  fecha_pago varchar(45) DEFAULT NULL,
  PRIMARY KEY (id_abonos),
  CONSTRAINT FK_id_renta FOREIGN KEY (id_renta) REFERENCES renta (id_renta),
  CONSTRAINT FK_id_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuarios),
  CONSTRAINT abonos_ibfk_1 FOREIGN KEY (id_tipo_abono) REFERENCES tipo_abono (id_tipo_abono) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE material_area (
  id INT(11) NOT NULL AUTO_INCREMENT,
  description VARCHAR(455) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE measurement_units (
  id INT(11) NOT NULL AUTO_INCREMENT,
  description VARCHAR(455) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE material_inventory (
  id INT(11) NOT NULL AUTO_INCREMENT,
  material_area_id INT(11) NOT NULL,
  stock DECIMAL(10,2) NOT NULL,
  measurement_unit_id INT(11) NOT NULL,
  measurement_unit_purchase_id INT(11) NOT NULL,
  description VARCHAR(455) NOT NULL,
  purchase_amount DECIMAL(10,2) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,   
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id),
  CONSTRAINT FK_material_area_id
    FOREIGN KEY (material_area_id)
    REFERENCES material_area (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT FK_measurement_unit_id
    FOREIGN KEY (measurement_unit_id)
    REFERENCES measurement_units (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT FK_measurement_unit_purchase_id
    FOREIGN KEY (measurement_unit_purchase_id)
    REFERENCES measurement_units (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 2021-09-25 GTL .> configurar material para articulos de venta
CREATE TABLE material_sale_items (
  id INT(11) NOT NULL AUTO_INCREMENT,
  material_inventory_id INT(11) NOT NULL,
  provider_id INT(11) NOT NULL,
  item_id INT(11) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,  
  created_at TIMESTAMP NULL DEFAULT NULL,   
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id),
  CONSTRAINT FK_sale_items_material_inventory_id
    FOREIGN KEY (material_inventory_id)
    REFERENCES material_inventory (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT FK_sale_items_providers_id
    FOREIGN KEY (provider_id)
    REFERENCES proveedores (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT FK_sale_items_item_id
    FOREIGN KEY (item_id)
    REFERENCES articulo (id_articulo)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- almacena la info cuando detecta cambio en el tipo de pedido
CREATE TABLE order_type_changes (
  id INT(11) NOT NULL AUTO_INCREMENT,
  renta_id INT(11) NOT NULL,
  user_id INT(11) NOT NULL,
  current_type_id INT(10) UNSIGNED NOT NULL,
  change_type_id INT(10) UNSIGNED NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id),
  CONSTRAINT fk_order_type_changes_renta
    FOREIGN KEY (renta_id)
    REFERENCES renta (id_renta)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_order_type_changes_users
    FOREIGN KEY (user_id)
    REFERENCES usuarios (id_usuarios)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_order_type_changes_current
    FOREIGN KEY (current_type_id)
    REFERENCES tipo (id_tipo)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_order_type_changes_change
    FOREIGN KEY (change_type_id)
    REFERENCES tipo (id_tipo)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- almacena la info cuando detecta cambio en el estado del pedido
CREATE TABLE order_status_changes (
  id INT(11) NOT NULL AUTO_INCREMENT,
  renta_id INT(11) NOT NULL,
  user_id INT(11) NOT NULL,
  current_status_id INT(11) NOT NULL,
  change_status_id INT(11) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id),
  CONSTRAINT fk_order_status_changes_renta
    FOREIGN KEY (renta_id)
    REFERENCES renta (id_renta)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_order_status_changes_users
    FOREIGN KEY (user_id)
    REFERENCES usuarios (id_usuarios)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_order_status_changes_current
    FOREIGN KEY (current_status_id)
    REFERENCES estado (id_estado)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_order_status_changes_change
    FOREIGN KEY (change_status_id)
    REFERENCES estado (id_estado)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE tipo_detalle_orden_proveedor (
  id INT(10) NOT NULL AUTO_INCREMENT,
  description VARCHAR(145) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE status_almacen_tasks_catalog (
  id INT(11) NOT NULL AUTO_INCREMENT,
  description VARCHAR(145) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE attend_almacen_tasks_type_catalog (
  id INT(11) NOT NULL AUTO_INCREMENT,
  description VARCHAR(145) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE tasks_almacen (
  id INT(11) NOT NULL AUTO_INCREMENT,
  renta_id INT(11) NOT NULL,
  status_almacen_tasks_catalog_id INT(11) NOT NULL,
  attend_almacen_tasks_type_catalog_id INT(11) NOT NULL,
  user_by_category_id INT(11) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  user_id INT(11) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_tasks_almacen_renta
    FOREIGN KEY (renta_id)
    REFERENCES renta (id_renta)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_status_almacen_tasks_catalog
    FOREIGN KEY (status_almacen_tasks_catalog_id)
    REFERENCES status_almacen_tasks_catalog (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_attend_almacen_tasks_type_catalog
    FOREIGN KEY (attend_almacen_tasks_type_catalog_id)
    REFERENCES attend_almacen_tasks_type_catalog (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_tasks_almacen_user_id
    FOREIGN KEY (user_by_category_id)
    REFERENCES usuarios (id_usuarios)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_tasks_almacen_user2_id
    FOREIGN KEY (user_id)
    REFERENCES usuarios (id_usuarios)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE tasks_chofer_delivery (
  id INT(11) NOT NULL AUTO_INCREMENT,
  renta_id INT(11) NOT NULL,
  status_almacen_tasks_catalog_id INT(11) NOT NULL,
  attend_almacen_tasks_type_catalog_id INT(11) NOT NULL,
  chofer_id INT(11) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  user_id INT(11) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_tasks_chofer_delivery_renta
    FOREIGN KEY (renta_id)
    REFERENCES renta (id_renta)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_status_almacen_tasks_chofer_delivery
    FOREIGN KEY (status_almacen_tasks_catalog_id)
    REFERENCES status_almacen_tasks_catalog (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_attend_almacen_tasks_chofer_delivery
    FOREIGN KEY (attend_almacen_tasks_type_catalog_id)
    REFERENCES attend_almacen_tasks_type_catalog (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_tasks_chofer_delivery_chofer_id
    FOREIGN KEY (chofer_id)
    REFERENCES usuarios (id_usuarios)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_tasks_chofer_delivery_user_id
    FOREIGN KEY (user_id)
    REFERENCES usuarios (id_usuarios)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- tabla para el estatus de la ordenes para proveedor de un folio

CREATE TABLE status_orders_provider_by_renta_catalog (
  id INT(11) NOT NULL AUTO_INCREMENT,
  description VARCHAR(145) NOT NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE status_orders_provider_by_renta (
  id INT(11) NOT NULL AUTO_INCREMENT,
  renta_id INT(11) NOT NULL,
  status_provider_orders_by_renta_catalog_id INT(11) NOT NULL,
  user_id INT(11) NOT NULL,
  comment VARCHAR(700) NULL,
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  fg_active ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (id),
  CONSTRAINT fk_status_provider_orders_by_renta_renta_id
    FOREIGN KEY (renta_id)
    REFERENCES renta (id_renta)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_status_provider_orders_by_renta_catalog_id
    FOREIGN KEY (status_provider_orders_by_renta_catalog_id)
    REFERENCES status_provider_orders_by_renta_catalog (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_status_provider_orders_by_renta_user_id
    FOREIGN KEY (user_id)
    REFERENCES usuarios (id_usuarios)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

