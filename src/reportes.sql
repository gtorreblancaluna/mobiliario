-- Obtener el numero de articulos apartados de una fecha determinada

SELECT
r.fecha_pedido,
r.fecha_entrega,
SUM(d.cantidad) AS total,
a.codigo,
a.descripcion,
c.color,
ca.descripcion
FROM renta r
INNER JOIN detalle_renta d ON (r.id_renta = d.id_renta)
INNER JOIN articulo a ON (a.id_articulo = d.id_articulo)
INNER JOIN color c ON (c.id_color = a.id_color)
INNER JOIN categoria ca ON (ca.id_categoria = a.id_categoria)
WHERE r.id_tipo = 1
AND r.id_estado = 1
AND a.id_categoria = 13
AND STR_TO_DATE(r.fecha_entrega,'%d/%m/%Y') >= STR_TO_DATE('07/09/2021','%d/%m/%Y')
AND STR_TO_DATE(r.fecha_entrega,'%d/%m/%Y') <= STR_TO_DATE('31/12/2021','%d/%m/%Y')
GROUP BY a.id_articulo
ORDER BY a.descripcion
