-- De los folios que dicen PEDIDO considerando desde 1-Enero-2022 a 31-Dic-2022, cuántas piezas de rentadas tuvimos este año, De cada producto registrado en Inventsrio

SELECT
SUM(d.cantidad) AS total,
a.codigo,
a.descripcion,
c.color,
ca.descripcion AS categoria,
r.fecha_pedido,
r.fecha_entrega,
r.folio,
tipo.tipo,
estado.descripcion
FROM renta r
INNER JOIN detalle_renta d ON (r.id_renta = d.id_renta)
INNER JOIN articulo a ON (a.id_articulo = d.id_articulo)
INNER JOIN color c ON (c.id_color = a.id_color)
INNER JOIN categoria ca ON (ca.id_categoria = a.id_categoria)
INNER JOIN tipo tipo ON (r.id_tipo = tipo.id_tipo)
INNER JOIN estado estado ON (r.id_estado = estado.id_estado)
WHERE r.id_tipo = 1
AND r.id_estado <> 4
AND STR_TO_DATE(r.fecha_entrega,'%d/%m/%Y') >= STR_TO_DATE('01/01/2022','%d/%m/%Y')
AND STR_TO_DATE(r.fecha_entrega,'%d/%m/%Y') <= STR_TO_DATE('31/12/2022','%d/%m/%Y')
GROUP BY a.id_articulo
ORDER BY SUM(d.cantidad) DESC


-- muestra todos los articulos
SELECT 
d.cantidad AS cantidad,
a.codigo AS codigo,
CONCAT(a.descripcion," ",c.color) AS articulo,
d.p_unitario AS precio_unitario,
d.porcentaje_descuento,
(d.cantidad*d.p_unitario) AS importe,
r.folio AS folio_pedido,
r.fecha_pedido,
r.fecha_entrega,
r.fecha_devolucion,
tipo.tipo,
estado.descripcion AS estado
FROM detalle_renta d
INNER JOIN renta r ON (r.id_renta = d.id_renta)
INNER JOIN articulo a ON (a.id_articulo = d.id_articulo)
INNER JOIN color c ON (c.id_color = a.id_color)
INNER JOIN categoria ca ON (ca.id_categoria = a.id_categoria)
INNER JOIN tipo tipo ON (r.id_tipo = tipo.id_tipo)
INNER JOIN estado estado ON (r.id_estado = estado.id_estado)
LIMIT 100000000