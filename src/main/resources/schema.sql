--Primero se deben borrar todas las tablas (de detalle a maestro) y lugo anyadirlas (de maestro a detalle)
--(en este caso en cada aplicacion se usa solo una tabla, por lo que no hace falta)

--Para giis.demo.tkrun:
--Para giis.demo.tkrun:
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Incidencia;
DROP TABLE IF EXISTS Usuarios;
DROP TABLE IF EXISTS Estados;
DROP TABLE IF EXISTS Tipos;

-- Create referenced (master) tables first
CREATE TABLE Tipos (
	id INTEGER PRIMARY KEY NOT NULL,
	nombre VARCHAR(32) NOT NULL
);

CREATE TABLE Roles (
	id INTEGER PRIMARY KEY NOT NULL,
	nombre VARCHAR(32) NOT NULL
);

CREATE TABLE Estados (
	id INTEGER PRIMARY KEY NOT NULL,
	nombre VARCHAR(32) NOT NULL
);

-- Then tables that reference them
CREATE TABLE Usuarios (
	id INTEGER PRIMARY KEY NOT NULL,
	nombre VARCHAR(32) NOT NULL,
	email VARCHAR(32) NOT NULL UNIQUE,
	dni VARCHAR(32) NOT NULL,
	rol INTEGER REFERENCES Roles(id)
);

CREATE TABLE Incidencia (
	id INTEGER PRIMARY KEY NOT NULL,
	tipo INTEGER REFERENCES Tipos(id),
	descripcion VARCHAR(32),
	localizacion VARCHAR(32),
	usuario INTEGER REFERENCES Usuarios(id),
	tecnico INTEGER REFERENCES Usuarios(id),
	Coste VARCHAR(32),
	descr_reparación VARCHAR(32),
	fecha DATE NOT NULL,
	estado INTEGER REFERENCES Estados(id),
	validación BOOLEAN NOT NULL
);