DROP TABLE IF EXISTS Incidencia;
DROP TABLE IF EXISTS Usuarios;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Estados;
DROP TABLE IF EXISTS Tipos;
DROP TABLE IF EXISTS HistorialIncidencias;
DROP TABLE IF EXISTS HistorialIncidencia;


-- Create referenced (master) tables first
CREATE TABLE Tipos (
	id INTEGER PRIMARY KEY NOT NULL,
	nombre VARCHAR(32) NOT NULL
);

CREATE TABLE Roles (id INT PRIMARY KEY NOT NULL, nombre VARCHAR(32) NOT NULL);
CREATE TABLE Estados (id INT PRIMARY KEY NOT NULL, nombre VARCHAR(32) NOT NULL);

CREATE TABLE Usuarios (
    id INT PRIMARY KEY NOT NULL, 
    nombre VARCHAR(32) NOT NULL, 
    email VARCHAR(32) NOT NULL UNIQUE, 
    dni VARCHAR(32) NOT NULL, 
    rol INT REFERENCES Roles(id)
);

CREATE TABLE Incidencia (
    id INT PRIMARY KEY NOT NULL, 
    tipo INT REFERENCES Tipos(id), 
    descripcion VARCHAR(32), 
    localizacion VARCHAR(32), 
    usuario INT REFERENCES Usuarios(id), 
    tecnico INT REFERENCES Usuarios(id), 
    Coste VARCHAR(32), 
    descr_reparación VARCHAR(32), 
    fecha DATE NOT NULL, 
    estado INT REFERENCES Estados(id), 
    validación BOOLEAN NOT NULL
);
CREATE TABLE HistorialIncidencias (
    id_log INTEGER PRIMARY KEY AUTOINCREMENT,
    id_incidencia INT NOT NULL,
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP, 
    descripcion_evento VARCHAR(255) NOT NULL,
    estado_alcanzado INT NOT NULL,
    FOREIGN KEY (id_incidencia) REFERENCES Incidencia(id)
);

CREATE TABLE HistorialIncidencia (
	id INTEGER PRIMARY KEY NOT NULL,
	incidencia INTEGER REFERENCES Incidencia(id),
	fecha DATE NOT NULL,
	accion VARCHAR(64) NOT NULL,
	usuario INTEGER REFERENCES Usuarios(id),
	comentario VARCHAR(256)
);