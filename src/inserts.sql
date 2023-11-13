-- inserts iniciales
INSERT INTO catalog_social_media_contact (description,created_at,fg_active) VALUES ('Carga inicial','2023-11-08','0');
INSERT INTO catalog_social_media_contact (description,created_at,fg_active) VALUES ('Facebook','2023-11-08','1');
INSERT INTO catalog_social_media_contact (description,created_at,fg_active) VALUES ('Google','2023-11-08','1');
INSERT INTO catalog_social_media_contact (description,created_at,fg_active) VALUES ('Twitter','2023-11-08','1');

INSERT INTO datos_generales (nombre_empresa,direccion1,folio,folio_cambio) VALUES ('generico','generico','1','0');

INSERT INTO puesto (descripcion) values ('Chofer');
INSERT INTO puesto (descripcion) values ('Repartidor');
INSERT INTO puesto (descripcion) values ('Administrador');
INSERT INTO puesto (descripcion) values ('Mostrador');

INSERT INTO usuarios (nombre,apellidos,administrador,nivel1,nivel2,contrasenia,activo,id_puesto) VALUES ('admin','admin','1','0','0','admin','1',3);

INSERT INTO tipo_detalle_orden_proveedor (description,created_at,updated_at) VALUES ('Compra','2022-04-22','2022-04-22');
INSERT INTO tipo_detalle_orden_proveedor (description,created_at,updated_at) VALUES ('Renta','2022-04-22','2022-04-22');

INSERT INTO cuenta (descripcion,fg_activo) values ('EFECTIVO','1');

INSERT INTO tipo_abono (descripcion,cuenta_id) VALUES ('Efectivo',1);

INSERT INTO categoria_contabilidad (descripcion) VALUES ("operativos");
INSERT INTO categoria_contabilidad (descripcion) VALUES ("administrativos");

INSERT INTO tipo_evento (descripcion) VALUES ('Alquiler');
INSERT INTO tipo_evento (descripcion) VALUES ('Compra');

INSERT INTO material_area (description) VALUES ('Carpinteria');
INSERT INTO material_area (description) VALUES ('Herrería');
INSERT INTO measurement_units (description) VALUES ('METRO');
INSERT INTO measurement_units (description) VALUES ('KILO');
INSERT INTO measurement_units (description) VALUES ('TRAMO');

INSERT INTO color (color) VALUES ('blanco');

INSERT INTO status_almacen_tasks_catalog (description,fg_active) VALUES ('Nuevo folio','1');
INSERT INTO status_almacen_tasks_catalog (description,fg_active) VALUES ('Cambio estado folio','1');
INSERT INTO status_almacen_tasks_catalog (description,fg_active) VALUES ('Cambio tipo folio','1');
INSERT INTO status_almacen_tasks_catalog (description,fg_active) VALUES ('Cambio tipo y estado folio','1');
INSERT INTO status_almacen_tasks_catalog (description,fg_active) VALUES ('Cambio en articulos del folio','1');
INSERT INTO status_almacen_tasks_catalog (description,fg_active) VALUES ('Cambio en datos generales del folio','1');

INSERT INTO attend_almacen_tasks_type_catalog (description,fg_active) VALUES ('Sin atender','1');
INSERT INTO attend_almacen_tasks_type_catalog (description,fg_active) VALUES ('Atendido','1');

INSERT INTO estado (descripcion) VALUES ('Apartado');
INSERT INTO estado (descripcion) VALUES ('En Renta');
INSERT INTO estado (descripcion) VALUES ('Pendiente');
INSERT INTO estado (descripcion) VALUES ('Cancelado');
INSERT INTO estado (descripcion) VALUES ('Finalizado');

INSERT INTO tipo (tipo) VALUES ('Pedido');
INSERT INTO tipo (tipo) VALUES ('Cotización');

INSERT INTO catalog_status_provider (description) values ('Iniciado');
INSERT INTO catalog_status_provider (description) values ('Finalizado');
INSERT INTO catalog_status_provider (description) values ('Pendiente');
INSERT INTO catalog_status_provider (description) values ('Cancelado');