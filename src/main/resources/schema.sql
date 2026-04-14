DROP TABLE IF EXISTS Incidencia;
DROP TABLE IF EXISTS Usuarios;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Estados;
DROP TABLE IF EXISTS Tipos;
DROP TABLE IF EXISTS Zonas;
DROP TABLE IF EXISTS HistorialIncidencia;
DROP TABLE IF EXISTS TipoTecnico;
DROP TABLE IF EXISTS Presupuestos;

DROP TABLE IF EXISTS Incidencia_Tecnico;

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
    rol INT NOT NULL,
    FOREIGN KEY (rol) REFERENCES Roles(id)
);

CREATE TABLE Incidencia (
    id INT PRIMARY KEY NOT NULL,
    tipo INT NOT NULL,
    descripcion VARCHAR(32),
    localizacion INT NOT NULL,
    usuario INT NOT NULL,
    tecnico INT,
    Coste VARCHAR(32),
    descr_reparación VARCHAR(32),
    fecha DATE NOT NULL,
    estado INT NOT NULL,
    validación BOOLEAN NOT NULL,
    FOREIGN KEY (tipo) REFERENCES Tipos(id),
    FOREIGN KEY (localizacion) REFERENCES Zonas(id),
    FOREIGN KEY (usuario) REFERENCES Usuarios(id),
    FOREIGN KEY (tecnico) REFERENCES Usuarios(id),
    FOREIGN KEY (estado) REFERENCES Estados(id)
);

CREATE TABLE HistorialIncidencia (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    incidencia INT NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    accion VARCHAR(64) NOT NULL,
    usuario INT NOT NULL,
    comentario VARCHAR(256),
    estado INT NOT NULL,
    FOREIGN KEY (incidencia) REFERENCES Incidencia(id),
    FOREIGN KEY (usuario) REFERENCES Usuarios(id),
    FOREIGN KEY (estado) REFERENCES Estados(id)
);

CREATE TABLE TipoTecnico (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario INT NOT NULL,
    tipo INT NOT NULL,
    FOREIGN KEY (usuario) REFERENCES Usuarios(id),
    FOREIGN KEY (tipo) REFERENCES Tipos(id)
);

-- Tabla de presupuestos por tipo: periodo de vigencia y consumo acumulado
CREATE TABLE Presupuestos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo INT NOT NULL,
    presupuesto NUMERIC NOT NULL,
    consumido NUMERIC DEFAULT 0,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    FOREIGN KEY (tipo) REFERENCES Tipos(id)
-- Table to allow assigning multiple technicians to the same incidencia
CREATE TABLE Incidencia_Tecnico (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    incidencia INT NOT NULL,
    tecnico INT NOT NULL,
    FOREIGN KEY (incidencia) REFERENCES Incidencia(id),
    FOREIGN KEY (tecnico) REFERENCES Usuarios(id)
);