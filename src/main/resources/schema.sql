--Primero se deben borrar todas las tablas (de detalle a maestro) y lugo anyadirlas (de maestro a detalle)
--(en este caso en cada aplicacion se usa solo una tabla, por lo que no hace falta)

--Para giis.demo.tkrun:
drop table Roles;
drop table Incidencia;
drop table Usuarios;


create table Incidencia (id int primary key not null, tipo int foreign key references Tipos(id), descripcion varchar(32), localizacion varchar(32), usuario int foreign key references Usuarios(id), tecnico int foreign key references Usuarios(id), Coste varchar(32), descr_reparación varchar(32), fecha date not null, estado int foreign key references Estados(id), validación boolean not null);

create table Usuarios (id int primary key not null, nombre varchar(32) not null, email varchar(32) not null unique, dni varchar(32) not null, rol int foreign key references Roles(id));

create table Roles (id int primary key not null, nombre varchar(32) not null);

create table Estados (id int primary key not null, nombre varchar(32) not null);

create table Tipos (id int primary key not null, nombre varchar(32) not null);