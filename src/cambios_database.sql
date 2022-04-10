CREATE TABLE tipo_abono (
  id_tipo_abono INT(11) NOT NULL AUTO_INCREMENT,
  descripcion VARCHAR(150) NULL,
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',  
  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY(id_tipo_abono)
)
ENGINE = InnoDB;

INSERT INTO tipo_abono (descripcion) VALUES ('Efectivo');
INSERT INTO tipo_abono (descripcion) VALUES ('Transferencia bancaria');
INSERT INTO tipo_abono (descripcion) VALUES ('Cheque');

-- agregar columna 
ALTER TABLE abonos ADD COLUMN id_tipo_abono INT(11) NOT NULL DEFAULT 1 AFTER comentario,
ADD FOREIGN KEY fk_id_tipo_abono(id_tipo_abono) REFERENCES tipo_abono(id_tipo_abono) ON DELETE CASCADE;

-- faltantes 2019.03.11 GTL

CREATE TABLE faltantes (
  id_faltante INT(11) NOT NULL AUTO_INCREMENT,
  id_articulo INT(11) NOT NULL,
  id_renta INT(11) NOT NULL,
  id_usuarios INT(11) NOT NULL,
  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  cantidad FLOAT NULL,
  comentario VARCHAR(350) NULL,
  fg_faltante ENUM('1','0') NOT NULL DEFAULT '1',
  fg_devolucion ENUM('1','0') NOT NULL DEFAULT '0',
  fg_accidente_trabajo ENUM('1','0') NOT NULL DEFAULT '0',
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',
  precio_cobrar FLOAT NULL,
  PRIMARY KEY(id_faltante),
  CONSTRAINT fk_faltantes_articulo FOREIGN KEY (id_articulo) REFERENCES articulo (id_articulo),
  CONSTRAINT fk_faltantes_renta FOREIGN KEY (id_renta) REFERENCES renta (id_renta),
  CONSTRAINT fk_faltantes_usuario FOREIGN KEY (id_usuarios) REFERENCES usuarios (id_usuarios)
)
ENGINE = InnoDB;

-- agregar columna 
ALTER TABLE faltantes ADD COLUMN fg_accidente_trabajo ENUM('1','0') NOT NULL DEFAULT '0' AFTER fg_devolucion;
ALTER TABLE faltantes ADD COLUMN precio_cobrar FLOAT NULL;

-- agregar columna 
ALTER TABLE articulo ADD COLUMN fecha_ultima_modificacion TIMESTAMP NULL;

-- 2019.04.19 GTL, tabla para almacenar informacion de ingresos y egresos

CREATE TABLE contabilidad (
  id INT(11) NOT NULL AUTO_INCREMENT,
  id_sub_categoria_contabilidad INT(11) NOT NULL,
  id_usuarios INT(11) NOT NULL,
  cuenta_id INT(11) NOT NULL,
  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_movimiento TIMESTAMP NULL,
  comentario VARCHAR(350) NULL,
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',
  cantidad FLOAT NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_contabilidad_sub_categoria_contablidad 
    FOREIGN KEY (id_sub_categoria_contabilidad) REFERENCES sub_categoria_contabilidad (id),
  CONSTRAINT fk_contabilidad_usuario FOREIGN KEY (id_usuarios) REFERENCES usuarios (id_usuarios),
  CONSTRAINT fk_contabilidad_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuenta (id)
)
ENGINE = InnoDB;

-- tipo de contabilidad

CREATE TABLE categoria_contabilidad (
  id INT(11) NOT NULL AUTO_INCREMENT,
  descripcion VARCHAR(150) NULL,
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',  
  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

CREATE TABLE sub_categoria_contabilidad (
  id INT(11) NOT NULL AUTO_INCREMENT,
  id_categoria_contabilidad INT(11) NOT NULL,
  descripcion VARCHAR(150) NULL,
  ingreso ENUM('1','0') NOT NULL DEFAULT '1',
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY(id),
  CONSTRAINT fk_categoria_contabilidad 
    FOREIGN KEY (id_categoria_contabilidad) REFERENCES categoria_contabilidad (id)
)
ENGINE = InnoDB;

INSERT INTO categoria_contabilidad (descripcion) VALUES ("operativos");
INSERT INTO categoria_contabilidad (descripcion) VALUES ("administrativos");

INSERT INTO sub_categoria_contabilidad (id_categoria_contabilidad,descripcion,ingreso)
VALUES (1,"sueldos",'0');

INSERT INTO sub_categoria_contabilidad (id_categoria_contabilidad,descripcion,ingreso)
VALUES (1,"gasolina",'0');

INSERT INTO sub_categoria_contabilidad (id_categoria_contabilidad,descripcion,ingreso)
VALUES (2,"facturacion",'0');

-- 2019.05.18 GTL
CREATE TABLE cuenta (
  id INT(11) NOT NULL AUTO_INCREMENT,
  descripcion VARCHAR(150) NULL,
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL,
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

INSERT INTO cuenta (descripcion) VALUES ('EFECTIVO');
INSERT INTO cuenta (descripcion) VALUES ('BANORTE');
INSERT INTO cuenta (descripcion) VALUES ('BBVA');

-- agregar columna 
ALTER TABLE tipo_abono ADD COLUMN cuenta_id INT(11) NOT NULL DEFAULT 1 AFTER fecha_registro,
ADD FOREIGN KEY fk_cuenta_tipo_abono(cuenta_id) REFERENCES cuenta(id) ON DELETE CASCADE;

ALTER TABLE orden_proveedor ADD COLUMN comentario VARCHAR(450) NULL;

CREATE TABLE compras (
  id INT(11) NOT NULL AUTO_INCREMENT,
  id_articulo INT(11) NOT NULL,
  comentario VARCHAR(250) NULL,
  cantidad FLOAT NULL,
  precio_compra FLOAT NULL,
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',  
  creado TIMESTAMP NULL,
  actualizado TIMESTAMP NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_compras_articulo_id 
   FOREIGN KEY (id_articulo) REFERENCES articulo (id_articulo)
)
ENGINE = InnoDB;


CREATE TABLE proveedores (
  id INT(11) NOT NULL AUTO_INCREMENT,
  nombre VARCHAR (450) NULL,
  apellidos VARCHAR(250) NULL,
  direccion VARCHAR(250) NULL,
  telefonos VARCHAR(250) NULL,
  email VARCHAR(250) NULL,
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',  
  creado TIMESTAMP NULL,
  actualizado TIMESTAMP NULL,
  PRIMARY KEY(id)
)
ENGINE = InnoDB;


CREATE TABLE orden_proveedor (
  id INT(11) NOT NULL AUTO_INCREMENT,
  id_renta INT(11) NOT NULL,
  id_usuario INT(11) NOT NULL,
  id_proveedores INT(11) NOT NULL,
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',
  status ENUM('1','2','3') NOT NULL DEFAULT '1',
  comentario VARCHAR(450) NULL,
  creado TIMESTAMP NULL,
  actualizado TIMESTAMP NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_orden_proveedor_renta_id 
   FOREIGN KEY (id_renta) REFERENCES renta (id_renta),
  CONSTRAINT fk_orden_proveedor_usuario_id 
   FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuarios),
  CONSTRAINT fk_orden_proveedor_proveedor_id
   FOREIGN KEY (id_proveedores) REFERENCES proveedores (id)
)
ENGINE = InnoDB;


CREATE TABLE detalle_orden_proveedor (
  id INT(11) NOT NULL AUTO_INCREMENT,
  id_orden_proveedor INT(11) NOT NULL,
  id_articulo INT(11) NOT NULL,
  cantidad FLOAT NULL,
  precio FLOAT NULL,
  comentario VARCHAR(450) NULL,
  status ENUM('1','2') NOT NULL DEFAULT '1', 
  -- tipo sera compra o renta
  tipo_orden ENUM('1','2') NOT NULL DEFAULT '1', 
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',  
  creado TIMESTAMP NULL,
  actualizado TIMESTAMP NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_detalle_orden_proveedor_orden_proveedor_id
   FOREIGN KEY (id_orden_proveedor) REFERENCES orden_proveedor (id),
  CONSTRAINT fk_orden_proveedor_id_articulo
   FOREIGN KEY (id_articulo) REFERENCES articulo (id_articulo)
)
ENGINE = InnoDB;


CREATE TABLE pagos_proveedor (
  id INT(11) NOT NULL AUTO_INCREMENT,
  id_orden_proveedor INT(11) NOT NULL,
  id_usuario INT(11) NOT NULL,
  id_tipo_abono INT(11) NOT NULL,
  cantidad FLOAT NOT NULL,
  comentario VARCHAR(450) NULL,
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',  
  creado TIMESTAMP NULL,
  actualizado TIMESTAMP NULL,
  PRIMARY KEY(id),
  CONSTRAINT fk_abono_proveedor_orden_proveedor_id
   FOREIGN KEY (id_orden_proveedor) REFERENCES orden_proveedor (id),
  CONSTRAINT fk_abono_proveedor_tipo_abono_id
   FOREIGN KEY (id_tipo_abono) REFERENCES tipo_abono (id_tipo_abono),
  CONSTRAINT fk_abono_proveedor_usuario_id
   FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuarios)
)
ENGINE = InnoDB;

INSERT proveedores (nombre,apellidos,direccion,telefonos) VALUES
    ("por definir","provisional","","");

INSERT proveedores (nombre,apellidos,direccion,telefonos) VALUES
    ("por definir","provisional","","");

CREATE TABLE configuracion (
  id INT(11) NOT NULL AUTO_INCREMENT,
  llave VARCHAR(455) NOT NULL,
  valor VARCHAR(455) NOT NULL,
  fg_activo ENUM('1','0') NOT NULL DEFAULT '1',  
  creado TIMESTAMP NULL,
  actualizado TIMESTAMP NULL,
  PRIMARY KEY(id)
)
ENGINE = InnoDB;

INSERT INTO configuracion (llave,valor) VALUES ("email_compras","email@email.com");

ALTER TABLE detalle_orden_proveedor 
    ADD COLUMN status ENUM('1','2') NOT NULL DEFAULT '1' NOT NULL DEFAULT '1';


ALTER TABLE
    orden_proveedor
MODIFY COLUMN
    status ENUM(
        '1',
        '2',
        '3',
        '4'
    )
NOT NULL;


INSERT INTO configuracion (llave,valor) VALUES ("url_logo_empresa","https://storage.googleapis.com/windy-container-237418.appspot.com/mobiliario-gaby/72063929_2897909780233817_7146560142975172608_o.jpg");
INSERT INTO configuracion (llave,valor) VALUES ("icon_check_ok","https://storage.googleapis.com/windy-container-237418.appspot.com/mobiliario-gaby/icon-check.png");
INSERT INTO configuracion (llave,valor) VALUES ("sitio_empresa","https://www.casagaby.com");

INSERT INTO tipo_evento (descripcion) VALUES ('Alquiler');
INSERT INTO tipo_evento (descripcion) VALUES ('Compra');

INSERT INTO material_area (description) VALUES ('Carpinteria');
INSERT INTO material_area (description) VALUES ('Herrería');
INSERT INTO measurement_units (description) VALUES ('METRO');
INSERT INTO measurement_units (description) VALUES ('KILO');
INSERT INTO measurement_units (description) VALUES ('TRAMO');

INSERT INTO tipo_detalle_orden_proveedor (description,created_at,updated_at) VALUES ('Compra','2022-04-22','2022-04-22');
INSERT INTO tipo_detalle_orden_proveedor (description,created_at,updated_at) VALUES ('Renta','2022-04-22','2022-04-22');

-- modificar id tipo de orden
ALTER TABLE detalle_orden_proveedor CHANGE COLUMN tipo_orden tipo_orden_detalle_proveedor_id INT(11) NOT NULL;;

ALTER TABLE detalle_orden_proveedor
ADD CONSTRAINT FK_detalle_orden_proveedor_id FOREIGN KEY (tipo_orden_detalle_proveedor_id)
    REFERENCES detalle_orden_proveedor(id);

-- FIN agregar id tipo de orden