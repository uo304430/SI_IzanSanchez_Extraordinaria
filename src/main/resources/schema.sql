DROP TABLE IF EXISTS Incidencia;
DROP TABLE IF EXISTS Usuarios;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Estados;
DROP TABLE IF EXISTS Tipos;
DROP TABLE IF EXISTS Zonas;
DROP TABLE IF EXISTS HistorialIncidencia;


-- Create referenced (master) tables first
CREATE TABLE Tipos (
	id INTEGER PRIMARY KEY NOT NULL,
	nombre VARCHAR(32) NOT NULL
);

CREATE TABLE Zonas (
    id INTEGER PRIMARY KEY NOT NULL,
    descripcion VARCHAR(32) NOT NULL
);

CREATE TABLE Roles (id INT PRIMARY KEY NOT NULL, nombre VARCHAR(32) NOT NULL);
CREATE TABLE Estados (id INT PRIMARY KEY NOT NULL, nombre VARCHAR(32) NOT NULL);

CREATE TABLE Usuarios (
    id INT PRIMARY KEY NOT NULL, 
    nombre VARCHAR(32) NOT NULL, 
    email VARCHAR(32) NOT NULL UNIQUE, 
    dni VARCHAR(32) NOT NULL, 
    FOREIGN KEY (rol) REFERENCES Roles(id)
);

CREATE TABLE Incidencia (
    id INT PRIMARY KEY NOT NULL, 
    FOREIGN KEY (tipo) REFERENCES Tipos(id), 
    descripcion VARCHAR(32), 
    FOREIGN KEY (localizacion) REFERENCES Zonas(id), 
    FOREIGN KEY (usuario) REFERENCES Usuarios(id), 
    FOREIGN KEY (tecnico) REFERENCES Usuarios(id), 
    Coste VARCHAR(32), 
    descr_reparación VARCHAR(32), 
    fecha DATE NOT NULL, 
    FOREIGN KEY (estado) REFERENCES Estados(id), 
    validación BOOLEAN NOT NULL
);

CREATE TABLE HistorialIncidencia (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    FOREIGN KEY (incidencia) REFERENCES Incidencia(id),
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    accion VARCHAR(64) NOT NULL,
    FOREIGN KEY (usuario) REFERENCES Usuarios(id),
    comentario VARCHAR(256),
    FOREIGN KEY (estado) REFERENCES Estados(id)
);

CREATE TABLE TipoTecnico (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    FOREIGN KEY (usuario) REFERENCES Usuarios(id),
    FOREIGN KEY (tipo) REFERENCES Tipos(id)
);