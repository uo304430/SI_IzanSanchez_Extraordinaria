--Datos para carga inicial de la base de datos

-- Datos de ejemplo para giis.demo.tkrun (Incidencias y usuarios)

insert into Roles(id,nombre) values 
	(1,'Admin'),
	(2,'Operador'),
	(3,'Ciudadano');

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

insert into Usuarios(id,nombre,email,dni,rol) values
	(1,'Ana López','ana.lopez@example.com','12345678A',3),
	(2,'Carlos Ruiz','carlos.ruiz@example.com','87654321B',2),
	(3,'María Pérez','maria.perez@example.com','11223344C',1);

insert into Incidencia(id,tipo,descripcion,localizacion,usuario,tecnico,Coste,descr_reparación,fecha,estado,validación) values
	(1,1,'Fallo en el servidor','Sala 1',1,2,'150','Reemplazo fuente','2016-12-01',1,false),
	(2,2,'Error en la aplicación','Puesto 23',3,2,'0','Aplicado parche','2016-12-02',2,true);
