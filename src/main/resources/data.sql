-- =====================================================================
-- Datos iniciales para pruebas
-- Asignatura: Sistemas de Información - Convocatoria de Junio 2026
-- =====================================================================
-- Objetivo: poblar el sistema con datos mínimos pero coherentes que
-- permitan probar las 6 historias de usuario implementadas sin tener
-- que crear datos a mano.
-- =====================================================================

-- --------------------------------------------------------------------
-- BLOQUE A - Catálogos y maestros
-- --------------------------------------------------------------------

INSERT INTO Rol (id, nombre) VALUES
    (1, 'CLIENTE'),
    (2, 'EMPLEADO'),
    (3, 'OPERARIO'),
    (4, 'TRANSPORTISTA'),
    (5, 'ADMINISTRADOR');

-- Contraseña en claro '1234' para todos los usuarios de prueba.
-- En producción se usaría BCrypt o similar.
INSERT INTO Usuario (id, email, dni, nombre, password, idRol, activo) VALUES
    (1, 'admin@paqueteria.es',       '00000000A', 'Administrador Sistema', '1234', 5, 1),
    (2, 'empleado.gijon@paqueteria.es','11111111B', 'Ana López',           '1234', 2, 1),
    (3, 'operario.madrid@paqueteria.es','22222222C', 'Luis Gómez',         '1234', 3, 1),
    (4, 'transportista1@paqueteria.es','33333333D', 'Carlos Ruiz',         '1234', 4, 1),
    (5, 'cliente1@example.com',      '44444444E', 'María Pérez',          '1234', 1, 1),
    (6, 'cliente2@example.com',      '55555555F', 'Pedro Sánchez',        '1234', 1, 1),
    (7, 'transportista2@paqueteria.es','66666666G', 'Javier Martín',      '1234', 4, 1);

INSERT INTO Zona (id, codigo, descripcion) VALUES
    (1, 'NORTE',  'Zona Norte (Asturias, Cantabria, País Vasco)'),
    (2, 'CENTRO', 'Zona Centro (Madrid, Castilla-La Mancha)'),
    (3, 'ESTE',   'Zona Este (Cataluña, Comunidad Valenciana)'),
    (4, 'SUR',    'Zona Sur (Andalucía, Murcia)'),
    (5, 'OESTE',  'Zona Oeste (Galicia, Castilla y León, Extremadura)');

INSERT INTO PuntoLogistico (id, codigo, tipo, direccion, ciudad, codigoPostal, idZona, horarioAtencion, activo) VALUES
    (1, 'OF-GIJ-01', 'OFICINA', 'Calle Corrida 12',        'Gijón',    '33201', 1, 'L-V 9:00-19:00', 1),
    (2, 'OF-MAD-01', 'OFICINA', 'Gran Vía 50',             'Madrid',   '28013', 2, 'L-V 9:00-20:00', 1),
    (3, 'OF-BCN-01', 'OFICINA', 'Passeig de Gràcia 80',    'Barcelona','08008', 3, 'L-V 9:00-20:00', 1),
    (4, 'OF-SEV-01', 'OFICINA', 'Avenida de la Constitución 5','Sevilla','41001', 4, 'L-V 9:00-19:00', 1),
    (5, 'AL-CTRO-01','ALMACEN', 'Polígono Centro km 12',   'Madrid',   '28038', 2, NULL,             1),
    (6, 'AL-NOR-01', 'ALMACEN', 'Polígono Asturias',       'Oviedo',   '33010', 1, NULL,             1),
    (7, 'AL-EST-01', 'ALMACEN', 'Polígono Llobregat',      'Barcelona','08820', 3, NULL,             1);

INSERT INTO Vehiculo (id, matricula, tipo, capacidadPesoKg, capacidadVolumenM3, idBaseOperativa, estado) VALUES
    (1, '1234-AAA', 'FURGONETA',     1500,  8.0, 1, 'ACTIVO'),
    (2, '5678-BBB', 'CAMION_RIGIDO', 8000, 30.0, 5, 'ACTIVO'),
    (3, '9012-CCC', 'CAMION_RIGIDO', 8000, 30.0, 6, 'ACTIVO'),
    (4, '3456-DDD', 'TRAILER',      24000, 80.0, 5, 'ACTIVO'),
    (5, '7890-EEE', 'FURGONETA',     1500,  8.0, 2, 'ACTIVO'),
    (6, '1122-FFF', 'FURGONETA',     1500,  8.0, 3, 'ACTIVO');

INSERT INTO Cliente (idUsuario, telefono, direccionHabitual) VALUES
    (5, '600111222', 'Calle Uría 25, Oviedo, 33003'),
    (6, '600333444', 'Calle Mayor 10, Madrid, 28013');

INSERT INTO Empleado (idUsuario, idPuntoLogistico) VALUES
    (2, 1);  -- Ana López, empleada en oficina Gijón

INSERT INTO Operario (idUsuario, idPuntoLogistico) VALUES
    (3, 5);  -- Luis Gómez, operario en almacén centro Madrid

INSERT INTO Transportista (idUsuario, idVehiculoHabitual) VALUES
    (4, 1),  -- Carlos Ruiz, conduce furgoneta Gijón
    (7, 5);  -- Javier Martín, conduce furgoneta Madrid

INSERT INTO Administrador (idUsuario) VALUES
    (1);

INSERT INTO EmpresaExterna (id, cif, nombreComercial, apiKey, emailContacto, activo) VALUES
    (1, 'B12345678', 'TiendaOnline SL', 'sk_test_abc123def456', 'pedidos@tiendaonline.es', 1);

INSERT INTO TipoServicio (id, codigo, descripcion, diasEstimadosEntrega) VALUES
    (1, 'ESTANDAR',    'Servicio estándar (3-5 días)',     4),
    (2, 'URGENTE_24H', 'Servicio urgente 24 horas',        1),
    (3, 'URGENTE_48H', 'Servicio urgente 48 horas',        2);

-- Tarifas: combinación de tipo, peso y zonas (modelo similar a SEUR/MRW)
-- Estándar
INSERT INTO Tarifa (idTipoServicio, pesoDesdeKg, pesoHastaKg, idZonaOrigen, idZonaDestino, precio, fechaInicioVigencia, fechaFinVigencia, activa) VALUES
    -- Estándar entre zonas iguales (más barato)
    (1, 0.0,  2.0,  1, 1,  5.50, '2025-01-01', NULL, 1),
    (1, 2.01, 5.0,  1, 1,  8.50, '2025-01-01', NULL, 1),
    (1, 5.01, 10.0, 1, 1, 12.00, '2025-01-01', NULL, 1),
    (1, 0.0,  2.0,  2, 2,  5.50, '2025-01-01', NULL, 1),
    (1, 2.01, 5.0,  2, 2,  8.50, '2025-01-01', NULL, 1),
    (1, 5.01, 10.0, 2, 2, 12.00, '2025-01-01', NULL, 1),
    -- Estándar entre zonas distintas (más caro)
    (1, 0.0,  2.0,  1, 2,  7.50, '2025-01-01', NULL, 1),
    (1, 2.01, 5.0,  1, 2, 11.50, '2025-01-01', NULL, 1),
    (1, 5.01, 10.0, 1, 2, 16.00, '2025-01-01', NULL, 1),
    (1, 0.0,  2.0,  2, 1,  7.50, '2025-01-01', NULL, 1),
    (1, 2.01, 5.0,  2, 1, 11.50, '2025-01-01', NULL, 1),
    (1, 5.01, 10.0, 2, 1, 16.00, '2025-01-01', NULL, 1),
    -- Urgente 24H (un 80% más caro que estándar)
    (2, 0.0,  2.0,  1, 1, 10.00, '2025-01-01', NULL, 1),
    (2, 2.01, 5.0,  1, 1, 15.00, '2025-01-01', NULL, 1),
    (2, 0.0,  2.0,  1, 2, 14.00, '2025-01-01', NULL, 1),
    (2, 2.01, 5.0,  1, 2, 21.00, '2025-01-01', NULL, 1),
    -- Urgente 48H (intermedio)
    (3, 0.0,  2.0,  1, 1,  7.50, '2025-01-01', NULL, 1),
    (3, 2.01, 5.0,  1, 1, 11.50, '2025-01-01', NULL, 1),
    (3, 0.0,  2.0,  1, 2, 10.50, '2025-01-01', NULL, 1),
    (3, 2.01, 5.0,  1, 2, 15.50, '2025-01-01', NULL, 1);


-- --------------------------------------------------------------------
-- BLOQUE B - Envíos de prueba
-- --------------------------------------------------------------------

-- Envío 1: Recién registrado, ruta asignada, todavía en oficina origen.
--          Sirve para probar HU-04 (seguimiento) y HU-05 (modificar entrega).
INSERT INTO Envio (id, codigo, idCliente, canalOrigen, idTipoServicio,
                   remitenteNombre, remitenteDni, remitenteTelefono,
                   remitenteDireccion, remitenteCiudad, remitenteCodigoPostal, idZonaOrigen,
                   destinatarioNombre, destinatarioTelefono,
                   destinatarioDireccion, destinatarioCiudad, destinatarioCodigoPostal, idZonaDestino,
                   modalidadRecogida, modalidadEntrega,
                   idPuntoOrigen, idPuntoDestino,
                   estado, costeCalculado, valorDeclarado, formaPago, pagado,
                   fechaCreacion, fechaEstimadaEntrega,
                   idUsuarioCreador) VALUES
    (1, 'ENV-20260601-0001', 5, 'OFICINA', 1,
     'María Pérez',        '44444444E', '600111222',
     NULL, NULL, NULL, 1,
     'Juan García',        '600999888',
     'Calle Mayor 25', 'Madrid', '28013', 2,
     'OFICINA', 'DOMICILIO',
     1, 2,
     'EN_RUTA', 11.50, 30.00, 'EFECTIVO', 1,
     '2026-06-01 10:30:00', '2026-06-05',
     2);

INSERT INTO Paquete (id, idEnvio, codigoBarras, descripcion,
                     pesoDeclaradoKg, largoCm, anchoCm, altoCm, estadoFisico) VALUES
    (1, 1, 'BC-20260601-0001-X', 'Caja de libros', 3.5, 40, 30, 20, 'CORRECTO');

INSERT INTO Ruta (id, idEnvio, fechaPlanificacion, estado) VALUES
    (1, 1, '2026-06-01 10:31:00', 'PLANIFICADA');

INSERT INTO TramoRuta (id, idRuta, ordenSecuencia, tipo,
                        idPuntoOrigen, idPuntoDestino, idVehiculo,
                        fechaPrevista, estado) VALUES
    (1, 1, 1, 'INTERMEDIO', 1, 6,  3, '2026-06-02 08:00:00', 'PLANIFICADO'),
    (2, 1, 2, 'INTERMEDIO', 6, 5,  4, '2026-06-03 08:00:00', 'PLANIFICADO'),
    (3, 1, 3, 'INTERMEDIO', 5, 2,  2, '2026-06-04 08:00:00', 'PLANIFICADO');

-- El tramo de ENTREGA final (a domicilio) lo añadimos por separado:
INSERT INTO TramoRuta (id, idRuta, ordenSecuencia, tipo,
                        idPuntoOrigen, idPuntoDestino, direccionDestino,
                        idVehiculo, fechaPrevista, estado) VALUES
    (4, 1, 4, 'ENTREGA', 2, NULL,
       'Calle Mayor 25, Madrid, 28013',
       5, '2026-06-05 11:00:00', 'PLANIFICADO');


-- Envío 2: En tránsito, con un paso por almacén ya realizado.
--          Sirve para probar HU-03 (carga/descarga).
INSERT INTO Envio (id, codigo, idCliente, canalOrigen, idTipoServicio,
                   remitenteNombre, remitenteDni, remitenteTelefono,
                   idZonaOrigen,
                   destinatarioNombre, destinatarioTelefono,
                   destinatarioDireccion, destinatarioCiudad, destinatarioCodigoPostal, idZonaDestino,
                   modalidadRecogida, modalidadEntrega,
                   idPuntoOrigen, idPuntoDestino,
                   estado, costeCalculado, valorDeclarado, formaPago, pagado,
                   fechaCreacion, fechaEstimadaEntrega,
                   idUsuarioCreador) VALUES
    (2, 'ENV-20260530-0002', 6, 'OFICINA', 2,
     'Pedro Sánchez',      '55555555F', '600333444',
     2,
     'Lucía Vázquez',      '600777666',
     'Calle del Sol 10', 'Gijón', '33203', 1,
     'OFICINA', 'DOMICILIO',
     2, 1,
     'EN_TRANSITO', 14.00, 0, 'TARJETA', 1,
     '2026-05-30 14:00:00', '2026-06-01',
     2);

INSERT INTO Paquete (id, idEnvio, codigoBarras, descripcion,
                     pesoDeclaradoKg, pesoUltimaVerificacionKg,
                     largoCm, anchoCm, altoCm, estadoFisico) VALUES
    (2, 2, 'BC-20260530-0002-Y', 'Caja de ropa', 1.8, 1.8, 35, 25, 15, 'CORRECTO');

INSERT INTO Ruta (id, idEnvio, fechaPlanificacion, estado) VALUES
    (2, 2, '2026-05-30 14:01:00', 'EN_CURSO');

INSERT INTO TramoRuta (id, idRuta, ordenSecuencia, tipo,
                        idPuntoOrigen, idPuntoDestino, idVehiculo,
                        fechaPrevista, fechaReal, estado) VALUES
    (5, 2, 1, 'INTERMEDIO', 2, 5,  5, '2026-05-30 18:00:00', '2026-05-30 17:45:00', 'COMPLETADO'),
    (6, 2, 2, 'INTERMEDIO', 5, 6,  4, '2026-05-31 10:00:00', '2026-05-31 09:50:00', 'ALMACENADO');

INSERT INTO TramoRuta (id, idRuta, ordenSecuencia, tipo,
                        idPuntoOrigen, idPuntoDestino, direccionDestino,
                        idVehiculo, fechaPrevista, estado) VALUES
    (7, 2, 3, 'ENTREGA', 6, NULL,
       'Calle del Sol 10, Gijón, 33203',
       1, '2026-06-01 11:00:00', 'PLANIFICADO');


-- Envío 3: Con dos intentos de entrega fallidos previos.
--          Sirve para probar HU-06 (gestión de reintentos).
INSERT INTO Envio (id, codigo, idCliente, canalOrigen, idTipoServicio,
                   remitenteNombre, remitenteDni, remitenteTelefono,
                   idZonaOrigen,
                   destinatarioNombre, destinatarioTelefono,
                   destinatarioDireccion, destinatarioCiudad, destinatarioCodigoPostal, idZonaDestino,
                   modalidadRecogida, modalidadEntrega,
                   idPuntoOrigen, idPuntoDestino,
                   estado, costeCalculado, formaPago, pagado,
                   fechaCreacion, fechaEstimadaEntrega,
                   idUsuarioCreador) VALUES
    (3, 'ENV-20260525-0003', 5, 'WEB', 1,
     'María Pérez',        '44444444E', '600111222',
     1,
     'Andrés Castro',      '600555444',
     'Avenida Pintor Sorolla 18', 'Madrid', '28010', 2,
     'OFICINA', 'DOMICILIO',
     1, 2,
     'PENDIENTE_REENTREGA', 7.50, 'TARJETA', 1,
     '2026-05-25 09:00:00', '2026-05-30',
     NULL);

INSERT INTO Paquete (id, idEnvio, codigoBarras, descripcion,
                     pesoDeclaradoKg, pesoUltimaVerificacionKg,
                     largoCm, anchoCm, altoCm, estadoFisico) VALUES
    (3, 3, 'BC-20260525-0003-Z', 'Sobre con documentos', 0.5, 0.5, 30, 22, 3, 'CORRECTO');

INSERT INTO Ruta (id, idEnvio, fechaPlanificacion, estado) VALUES
    (3, 3, '2026-05-25 09:01:00', 'EN_CURSO');

INSERT INTO TramoRuta (id, idRuta, ordenSecuencia, tipo,
                        idPuntoOrigen, idPuntoDestino, idVehiculo,
                        fechaPrevista, fechaReal, estado) VALUES
    (8,  3, 1, 'INTERMEDIO', 1, 6, 3, '2026-05-26 08:00:00', '2026-05-26 09:30:00', 'COMPLETADO'),
    (9,  3, 2, 'INTERMEDIO', 6, 5, 4, '2026-05-27 08:00:00', '2026-05-27 14:20:00', 'COMPLETADO'),
    (10, 3, 3, 'INTERMEDIO', 5, 2, 2, '2026-05-28 08:00:00', '2026-05-28 11:00:00', 'COMPLETADO');

INSERT INTO TramoRuta (id, idRuta, ordenSecuencia, tipo,
                        idPuntoOrigen, idPuntoDestino, direccionDestino,
                        idVehiculo, fechaPrevista, estado) VALUES
    (11, 3, 4, 'ENTREGA', 2, NULL,
       'Avenida Pintor Sorolla 18, Madrid, 28010',
       5, '2026-05-29 11:00:00', 'FALLIDO');

INSERT INTO IntentoEntrega (id, idEnvio, idTramoEntrega, numeroIntento,
                            idTransportista, fechaIntento,
                            resultado, motivoFallo) VALUES
    (1, 3, 11, 1, 7, '2026-05-29 11:15:00', 'FALLIDO', 'AUSENTE'),
    (2, 3, 11, 2, 7, '2026-05-30 11:20:00', 'FALLIDO', 'AUSENTE');


-- --------------------------------------------------------------------
-- BLOQUE C - Historial de eventos de los envíos de prueba
-- --------------------------------------------------------------------

-- Historial Envío 1
INSERT INTO HistorialEvento (idEnvio, fechaEvento, accion, idUsuarioResponsable, idPuntoLogistico, estadoResultante, comentario) VALUES
    (1, '2026-06-01 10:30:00', 'REGISTRO_INICIAL', 2, 1, 'REGISTRADO',  'Envío registrado en oficina por el empleado'),
    (1, '2026-06-01 10:31:00', 'RUTA_ASIGNADA',    NULL, NULL, 'EN_RUTA', 'Ruta asignada automáticamente: 4 tramos');

-- Historial Envío 2
INSERT INTO HistorialEvento (idEnvio, fechaEvento, accion, idUsuarioResponsable, idPuntoLogistico, estadoResultante, comentario) VALUES
    (2, '2026-05-30 14:00:00', 'REGISTRO_INICIAL',     2, 2, 'REGISTRADO',  'Envío registrado en oficina de Madrid'),
    (2, '2026-05-30 14:01:00', 'RUTA_ASIGNADA',        NULL, NULL, 'EN_RUTA', 'Ruta asignada automáticamente'),
    (2, '2026-05-30 17:45:00', 'DESCARGA_EN_ALMACEN',  3, 5, 'EN_TRANSITO', 'Descargado en almacén central');

-- Historial Envío 3
INSERT INTO HistorialEvento (idEnvio, fechaEvento, accion, idUsuarioResponsable, idPuntoLogistico, estadoResultante, comentario) VALUES
    (3, '2026-05-25 09:00:00', 'REGISTRO_INICIAL',     NULL, NULL, 'REGISTRADO',          'Envío registrado por canal Web'),
    (3, '2026-05-25 09:01:00', 'RUTA_ASIGNADA',        NULL, NULL, 'EN_RUTA',             'Ruta asignada automáticamente'),
    (3, '2026-05-29 11:15:00', 'INTENTO_ENTREGA',      7, NULL, 'PENDIENTE_REENTREGA', 'Intento 1 de 4 fallido - Destinatario ausente'),
    (3, '2026-05-30 11:20:00', 'INTENTO_ENTREGA',      7, NULL, 'PENDIENTE_REENTREGA', 'Intento 2 de 4 fallido - Destinatario ausente');
