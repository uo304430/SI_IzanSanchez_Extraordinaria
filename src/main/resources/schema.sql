-- =====================================================================
-- Sistema de transporte de paquetes - Esquema de base de datos
-- Asignatura: Sistemas de Información - Convocatoria de Junio 2026
-- Autor: Izan Sánchez Vázquez
-- =====================================================================
-- Notas:
--   * SQLite no aplica integridad referencial sin PRAGMA foreign_keys=ON
--     (ya activado en giis.demo.util.DbUtil.getConnection()).
--   * Los CHECK constraints se aplican como guardarraíles adicionales
--     a las validaciones de la lógica de negocio.
-- =====================================================================

-- ----- DROPS (en orden inverso a las dependencias) -----
DROP TABLE IF EXISTS AvisoCliente;
DROP TABLE IF EXISTS Incidencia;
DROP TABLE IF EXISTS HistorialEvento;
DROP TABLE IF EXISTS IntentoEntrega;
DROP TABLE IF EXISTS TramoRuta;
DROP TABLE IF EXISTS Ruta;
DROP TABLE IF EXISTS Paquete;
DROP TABLE IF EXISTS Envio;
DROP TABLE IF EXISTS Tarifa;
DROP TABLE IF EXISTS TipoServicio;
DROP TABLE IF EXISTS Vehiculo;
DROP TABLE IF EXISTS PuntoLogistico;
DROP TABLE IF EXISTS Zona;
DROP TABLE IF EXISTS EmpresaExterna;
DROP TABLE IF EXISTS Administrador;
DROP TABLE IF EXISTS Transportista;
DROP TABLE IF EXISTS Operario;
DROP TABLE IF EXISTS Empleado;
DROP TABLE IF EXISTS Cliente;
DROP TABLE IF EXISTS Usuario;
DROP TABLE IF EXISTS Rol;


-- =====================================================================
-- BLOQUE A - Catálogos y maestros
-- =====================================================================

CREATE TABLE Rol (
    id      INTEGER PRIMARY KEY,
    nombre  VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE Usuario (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    email           VARCHAR(128) NOT NULL UNIQUE,
    dni             VARCHAR(16)  UNIQUE,
    nombre          VARCHAR(128) NOT NULL,
    password        VARCHAR(256) NOT NULL,
    idRol           INTEGER      NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT 1,
    fechaCreacion   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idRol) REFERENCES Rol(id)
);

CREATE TABLE Zona (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo       VARCHAR(16) NOT NULL UNIQUE,
    descripcion  VARCHAR(64) NOT NULL
);

CREATE TABLE PuntoLogistico (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo           VARCHAR(16) NOT NULL UNIQUE,
    tipo             VARCHAR(16) NOT NULL CHECK (tipo IN ('OFICINA','ALMACEN')),
    direccion        VARCHAR(256) NOT NULL,
    ciudad           VARCHAR(64)  NOT NULL,
    codigoPostal     VARCHAR(8)   NOT NULL,
    idZona           INTEGER      NOT NULL,
    horarioAtencion  VARCHAR(128),
    activo           BOOLEAN      NOT NULL DEFAULT 1,
    FOREIGN KEY (idZona) REFERENCES Zona(id)
);

CREATE TABLE Vehiculo (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    matricula           VARCHAR(16) NOT NULL UNIQUE,
    tipo                VARCHAR(32) NOT NULL CHECK (tipo IN ('FURGONETA','CAMION_RIGIDO','TRAILER')),
    capacidadPesoKg     INTEGER     NOT NULL,
    capacidadVolumenM3  NUMERIC     NOT NULL,
    idBaseOperativa     INTEGER     NOT NULL,
    estado              VARCHAR(16) NOT NULL DEFAULT 'ACTIVO'
                          CHECK (estado IN ('ACTIVO','MANTENIMIENTO','BAJA')),
    FOREIGN KEY (idBaseOperativa) REFERENCES PuntoLogistico(id)
);

CREATE TABLE Cliente (
    idUsuario          INTEGER PRIMARY KEY,
    telefono           VARCHAR(20) NOT NULL,
    direccionHabitual  VARCHAR(256),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id)
);

CREATE TABLE Empleado (
    idUsuario         INTEGER PRIMARY KEY,
    idPuntoLogistico  INTEGER NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id),
    FOREIGN KEY (idPuntoLogistico) REFERENCES PuntoLogistico(id)
);

CREATE TABLE Operario (
    idUsuario         INTEGER PRIMARY KEY,
    idPuntoLogistico  INTEGER NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id),
    FOREIGN KEY (idPuntoLogistico) REFERENCES PuntoLogistico(id)
);

CREATE TABLE Transportista (
    idUsuario           INTEGER PRIMARY KEY,
    idVehiculoHabitual  INTEGER,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id),
    FOREIGN KEY (idVehiculoHabitual) REFERENCES Vehiculo(id)
);

CREATE TABLE Administrador (
    idUsuario  INTEGER PRIMARY KEY,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(id)
);

CREATE TABLE EmpresaExterna (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    cif             VARCHAR(16)  NOT NULL UNIQUE,
    nombreComercial VARCHAR(128) NOT NULL,
    apiKey          VARCHAR(64)  NOT NULL UNIQUE,
    emailContacto   VARCHAR(128) NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT 1,
    fechaAlta       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TipoServicio (
    id                    INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo                VARCHAR(16) NOT NULL UNIQUE,
    descripcion           VARCHAR(64) NOT NULL,
    diasEstimadosEntrega  INTEGER     NOT NULL
);

CREATE TABLE Tarifa (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    idTipoServicio      INTEGER NOT NULL,
    pesoDesdeKg         NUMERIC NOT NULL,
    pesoHastaKg         NUMERIC NOT NULL,
    idZonaOrigen        INTEGER NOT NULL,
    idZonaDestino       INTEGER NOT NULL,
    precio              NUMERIC NOT NULL,
    fechaInicioVigencia DATE    NOT NULL,
    fechaFinVigencia    DATE,
    activa              BOOLEAN NOT NULL DEFAULT 1,
    FOREIGN KEY (idTipoServicio) REFERENCES TipoServicio(id),
    FOREIGN KEY (idZonaOrigen)   REFERENCES Zona(id),
    FOREIGN KEY (idZonaDestino)  REFERENCES Zona(id)
);


-- =====================================================================
-- BLOQUE B - Operación del envío
-- =====================================================================

CREATE TABLE Envio (
    id                       INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo                   VARCHAR(32)  NOT NULL UNIQUE,
    idCliente                INTEGER,
    idEmpresaExterna         INTEGER,
    canalOrigen              VARCHAR(16)  NOT NULL
                                CHECK (canalOrigen IN ('OFICINA','WEB','API','PDA')),
    idTipoServicio           INTEGER      NOT NULL,
    -- Remitente
    remitenteNombre          VARCHAR(128) NOT NULL,
    remitenteDni             VARCHAR(16)  NOT NULL,
    remitenteTelefono        VARCHAR(20)  NOT NULL,
    remitenteDireccion       VARCHAR(256),
    remitenteCiudad          VARCHAR(64),
    remitenteCodigoPostal    VARCHAR(8),
    idZonaOrigen             INTEGER      NOT NULL,
    -- Destinatario
    destinatarioNombre       VARCHAR(128) NOT NULL,
    destinatarioTelefono     VARCHAR(20)  NOT NULL,
    destinatarioDireccion    VARCHAR(256) NOT NULL,
    destinatarioCiudad       VARCHAR(64)  NOT NULL,
    destinatarioCodigoPostal VARCHAR(8)   NOT NULL,
    idZonaDestino            INTEGER      NOT NULL,
    -- Modalidades
    modalidadRecogida        VARCHAR(16)  NOT NULL
                                CHECK (modalidadRecogida IN ('OFICINA','DOMICILIO')),
    modalidadEntrega         VARCHAR(16)  NOT NULL
                                CHECK (modalidadEntrega  IN ('OFICINA','DOMICILIO')),
    idPuntoOrigen            INTEGER      NOT NULL,
    idPuntoDestino           INTEGER      NOT NULL,
    -- Estado y económico
    estado                   VARCHAR(32)  NOT NULL DEFAULT 'REGISTRADO'
                                CHECK (estado IN ('REGISTRADO','PENDIENTE_ASIGNACION',
                                                  'PENDIENTE_RECOGIDA','EN_RUTA','RECOGIDO','EN_TRANSITO',
                                                  'EN_REPARTO','PENDIENTE_REENTREGA',
                                                  'ENTREGADO','DEPOSITADO_EN_PUNTO',
                                                  'EN_DEVOLUCION','CANCELADO')),
    costeCalculado           NUMERIC      NOT NULL,
    valorDeclarado           NUMERIC      NOT NULL DEFAULT 0,
    formaPago                VARCHAR(16)  NOT NULL DEFAULT 'EFECTIVO'
                               CHECK (formaPago IN ('EFECTIVO','TARJETA','PENDIENTE')),
    pagado                   BOOLEAN      NOT NULL DEFAULT 0,
    -- Fechas
    fechaCreacion            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fechaEstimadaEntrega     DATE,
    fechaEntregaReal         DATETIME,
    -- Auditoría
    idUsuarioCreador         INTEGER,
    modificacionesEntrega    INTEGER      NOT NULL DEFAULT 0,
    FOREIGN KEY (idCliente)        REFERENCES Cliente(idUsuario),
    FOREIGN KEY (idEmpresaExterna) REFERENCES EmpresaExterna(id),
    FOREIGN KEY (idTipoServicio)   REFERENCES TipoServicio(id),
    FOREIGN KEY (idZonaOrigen)     REFERENCES Zona(id),
    FOREIGN KEY (idZonaDestino)    REFERENCES Zona(id),
    FOREIGN KEY (idPuntoOrigen)    REFERENCES PuntoLogistico(id),
    FOREIGN KEY (idPuntoDestino)   REFERENCES PuntoLogistico(id),
    FOREIGN KEY (idUsuarioCreador) REFERENCES Usuario(id)
);

CREATE TABLE Paquete (
    id                        INTEGER PRIMARY KEY AUTOINCREMENT,
    idEnvio                   INTEGER NOT NULL UNIQUE,
    codigoBarras              VARCHAR(64) NOT NULL UNIQUE,
    descripcion               VARCHAR(256) NOT NULL,
    pesoDeclaradoKg           NUMERIC NOT NULL,
    pesoUltimaVerificacionKg  NUMERIC,
    largoCm                   INTEGER NOT NULL,
    anchoCm                   INTEGER NOT NULL,
    altoCm                    INTEGER NOT NULL,
    estadoFisico              VARCHAR(16) NOT NULL DEFAULT 'CORRECTO'
                                CHECK (estadoFisico IN ('CORRECTO','DANO_LEVE','DANO_GRAVE')),
    FOREIGN KEY (idEnvio) REFERENCES Envio(id)
);

CREATE TABLE Ruta (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    idEnvio            INTEGER NOT NULL UNIQUE,
    fechaPlanificacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado             VARCHAR(16) NOT NULL DEFAULT 'PLANIFICADA'
                         CHECK (estado IN ('PLANIFICADA','EN_CURSO','COMPLETADA','INCOMPLETA')),
    FOREIGN KEY (idEnvio) REFERENCES Envio(id)
);

CREATE TABLE TramoRuta (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    idRuta           INTEGER NOT NULL,
    ordenSecuencia   INTEGER NOT NULL,
    tipo             VARCHAR(16) NOT NULL
                       CHECK (tipo IN ('RECOGIDA','INTERMEDIO','ENTREGA')),
    idPuntoOrigen    INTEGER,
    idPuntoDestino   INTEGER,
    direccionOrigen  VARCHAR(256),
    direccionDestino VARCHAR(256),
    idVehiculo       INTEGER,
    fechaPrevista    DATETIME NOT NULL,
    fechaReal        DATETIME,
    estado           VARCHAR(16) NOT NULL DEFAULT 'PLANIFICADO'
                       CHECK (estado IN ('PLANIFICADO','EN_TRANSITO','ALMACENADO','COMPLETADO','FALLIDO')),
    FOREIGN KEY (idRuta)         REFERENCES Ruta(id),
    FOREIGN KEY (idPuntoOrigen)  REFERENCES PuntoLogistico(id),
    FOREIGN KEY (idPuntoDestino) REFERENCES PuntoLogistico(id),
    FOREIGN KEY (idVehiculo)     REFERENCES Vehiculo(id),
    UNIQUE (idRuta, ordenSecuencia)
);

CREATE TABLE IntentoEntrega (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    idEnvio         INTEGER NOT NULL,
    idTramoEntrega  INTEGER NOT NULL,
    numeroIntento   INTEGER NOT NULL CHECK (numeroIntento BETWEEN 1 AND 4),
    idTransportista INTEGER NOT NULL,
    fechaIntento    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resultado       VARCHAR(16) NOT NULL CHECK (resultado IN ('COMPLETADO','FALLIDO')),
    motivoFallo     VARCHAR(32) CHECK (motivoFallo IN ('AUSENTE','DIRECCION_INCORRECTA','RECHAZADO','OTROS')),
    comentario      VARCHAR(256),
    UNIQUE (idEnvio, numeroIntento),
    FOREIGN KEY (idEnvio)         REFERENCES Envio(id),
    FOREIGN KEY (idTramoEntrega)  REFERENCES TramoRuta(id),
    FOREIGN KEY (idTransportista) REFERENCES Usuario(id)
);


-- =====================================================================
-- BLOQUE C - Trazabilidad e incidencias
-- =====================================================================

CREATE TABLE HistorialEvento (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    idEnvio              INTEGER NOT NULL,
    fechaEvento          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accion               VARCHAR(64) NOT NULL,
    idUsuarioResponsable INTEGER,
    idPuntoLogistico     INTEGER,
    estadoResultante     VARCHAR(32),
    comentario           VARCHAR(512),
    datosAdicionales     VARCHAR(512),
    FOREIGN KEY (idEnvio)              REFERENCES Envio(id),
    FOREIGN KEY (idUsuarioResponsable) REFERENCES Usuario(id),
    FOREIGN KEY (idPuntoLogistico)     REFERENCES PuntoLogistico(id)
);

CREATE TABLE Incidencia (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    idEnvio              INTEGER NOT NULL,
    tipo                 VARCHAR(32) NOT NULL
                           CHECK (tipo IN ('DANO_GRAVE','DISCREPANCIA_PESO','SIN_RUTA_ASIGNABLE','CODIGO_BARRAS_INCORRECTO','OTRO')),
    descripcion          VARCHAR(512) NOT NULL,
    idUsuarioGenerador   INTEGER,
    idPuntoLogistico     INTEGER,
    fechaApertura        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado               VARCHAR(16) NOT NULL DEFAULT 'ABIERTA'
                           CHECK (estado IN ('ABIERTA','EN_REVISION','RESUELTA')),
    idUsuarioAsignado    INTEGER,
    fechaResolucion      DATETIME,
    comentarioResolucion VARCHAR(512),
    FOREIGN KEY (idEnvio)            REFERENCES Envio(id),
    FOREIGN KEY (idUsuarioGenerador) REFERENCES Usuario(id),
    FOREIGN KEY (idPuntoLogistico)   REFERENCES PuntoLogistico(id),
    FOREIGN KEY (idUsuarioAsignado)  REFERENCES Usuario(id)
);

CREATE TABLE AvisoCliente (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    idEnvio              INTEGER NOT NULL,
    tipoEvento           VARCHAR(32) NOT NULL,
    canal                VARCHAR(16) NOT NULL CHECK (canal IN ('EMAIL','SMS')),
    destinatarioEmail    VARCHAR(128),
    destinatarioTelefono VARCHAR(20),
    asunto               VARCHAR(128) NOT NULL,
    cuerpo               VARCHAR(2048) NOT NULL,
    fechaEnvio           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado               VARCHAR(16) NOT NULL DEFAULT 'ENVIADO'
                           CHECK (estado IN ('ENVIADO','FALLIDO','REINTENTANDO')),
    intentosEnvio        INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (idEnvio) REFERENCES Envio(id)
);
