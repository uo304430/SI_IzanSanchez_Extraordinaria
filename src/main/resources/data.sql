--Datos para carga inicial de la base de datos

-- Datos de ejemplo para giis.demo.tkrun (Incidencias y usuarios)

insert into Roles(id,nombre) values 
	(1,'Técnico'),
	(2,'Operador'),
	(3,'Ciudadano'),
	(4,'Gestor Económico');

insert into Estados(id,nombre) values 
	(1,'Nueva'),
	(2,'Validada'),
	(3,'Asignada'),
	(4,'En proceso'),
	(5,'Resuelta'),
	(6,'Cerrada');

insert into Tipos(id,nombre) values 
	(1,'alumbrado'),
	(2,'limpieza'),
	(3,'mobiliario urbano'),
	(4,'zonas verdes'),
	(5,'señalización'),
	(6,'calzada');

insert into Zonas(id,descripcion) values
	(1,'Norte'),
	(2,'Noreste'),
	(3,'Este'),
	(4,'Sureste'),
	(5,'Sur'),
	(6,'Suroeste'),
	(7,'Oeste'),
	(8,'Noroeste');

insert into Usuarios(id,nombre,email,dni,rol) values
	(1,'Ana López','ana.lopez@example.com','12345678A',3),
	(2,'Carlos Ruiz','carlos.ruiz@example.com','87654321B',2),
	(3,'María Pérez','maria.perez@example.com','11223344C',1),
	(4,'Luis Gómez','luis.gomez@example.com','55667788D',1),
	(5,'Sofía Martínez','sofia.martinez@example.com','99887766E',1);

insert into Incidencia(id,tipo,descripcion,localizacion,usuario,tecnico,Coste,descr_reparación,fecha,estado,validación) values
	(1,1,'Fallo en el servidor',1,1,2,'150','Reemplazo fuente','2016-12-01T10:00:00',1,false),
	(2,2,'Error en la aplicación',3,3,2,'0','Aplicado parche','2016-12-02T15:30:00',2,true),
	(3,3,'Fallo en la red',4,1,2,'200','Reemplazo cable','2016-12-03T09:00:00',4,false),
	(4,4,'Problema en el sistema',5,1,2,'300','Reemplazo componente','2016-12-04T14:00:00',3,false),
	(5,2,'Error en la base de datos',3,3,2,'0','Aplicado parche','2016-12-02T15:30:00',2,true),
	(6,2,'Fallo en el hardware',2,1,2,'250','Reemplazo disco','2016-12-05T11:00:00',2,true),
	(7,2,'Problema de rendimiento',1,3,2,'0','Optimización realizada','2016-12-06T16:30:00',2,true);

	
INSERT INTO HistorialIncidencia (incidencia, fecha, accion, usuario, comentario, estado)
VALUES (1, '2016-12-01 09:00:00', 'Registro inicial', 2, 'Registro inicial de la incidencia', 1);

INSERT INTO HistorialIncidencia (incidencia, fecha, accion, usuario, comentario, estado)
VALUES (1, '2016-12-01 10:30:00', 'Asignada', 2, 'Asignada al técnico Carlos Ruiz', 3);

INSERT INTO HistorialIncidencia (incidencia, fecha, accion, usuario, comentario, estado)
VALUES (2, '2016-12-02 08:15:00', 'Registro inicial', 2, 'Registro inicial de la incidencia', 1);

INSERT INTO HistorialIncidencia (incidencia, fecha, accion, usuario, comentario, estado)
VALUES (2, '2016-12-02 11:00:00', 'Validada', 2, 'Validada por operador', 2);

INSERT INTO HistorialIncidencia (incidencia, fecha, accion, usuario, comentario, estado)
VALUES (5, '2026-04-01 10:05:00', 'Registro inicial', 1, 'Incidencia creada para pruebas de presupuesto', 5);

INSERT INTO TipoTecnico (tipo, usuario) 
VALUES (2, 3),
	   (2, 4),
	   (2, 5);
     -- Presupuestos de ejemplo: un presupuesto anual vigente para cada tipo (consumido inicial 0)
-- Usamos fechas amplias que incluyan la fecha actual
insert into Presupuestos(tipo,presupuesto,consumido,fecha_inicio,fecha_fin) values
(1, 1000, 0, '2026-01-01', '2026-12-31'),
(2, 800, 0, '2026-01-01', '2026-12-31'),
(3, 1200, 0, '2026-01-01', '2026-12-31'),
(4, 1500, 0, '2026-01-01', '2026-12-31'),
(5, 900, 0, '2026-01-01', '2026-12-31'),
(6, 2000, 0, '2026-01-01', '2026-12-31');
