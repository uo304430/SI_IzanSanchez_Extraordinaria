# Contexto del proyecto — SI Junio 2026 (Recuperación)

## Qué es este proyecto

Trabajo práctico de la asignatura **Sistemas de Información** (Ingeniería Informática, Universidad de Oviedo) para la convocatoria de **junio 2026**. Es una recuperación de la convocatoria ordinaria donde se suspendió la parte práctica.

**Importante:** este repositorio contiene actualmente el código del proyecto de la ordinaria (dominio: gestión de incidencias municipales). **Todo ese código va a ser sustituido por completo** por un nuevo dominio: **sistema de transporte de paquetes**. Lo único que se mantiene de la ordinaria es la infraestructura técnica de la plantilla `samples-test-dev` (utilidades, configuración Maven, plantilla MVC).

## Dominio nuevo: Transporte de paquetes

Sistema para una empresa de transporte con flota propia, oficinas y almacenes intermedios. Los envíos se pueden registrar desde oficina (mostrador/teléfono), por internet (cliente), por servicio web (empresa externa) o desde PDA del transportista en recogida a domicilio. Tras el registro, el sistema asigna automáticamente rutas y vehículos. Hay verificación de carga/descarga en almacenes con código de barras, inspección visual y pesaje. El cliente puede hacer seguimiento, modificar lugar de entrega y se gestionan entregas fallidas con hasta 3 reintentos. Existen tarifas inspiradas en transportistas reales.

## Stack técnico (de la ordinaria, se mantiene)

- **Java 17** + **Maven**
- **SQLite** (`sqlite-jdbc` + Apache **Commons DbUtils**) — BD local `DemoDB.db`
- **Swing** + **MigLayout** + **LGoodDatePicker** para UI
- **JUnit 5** + **JaCoCo** para tests y cobertura
- **SLF4J** + reload4j para logging (sustituir todos los `System.out.println` por logger)
- **Lombok** para getters/setters
- **Jackson** para JSON
- Patrón **MVC** (Modelo / Vista / Controlador) + DTOs

## Estructura de paquetes a seguir

Todo el código nuevo del dominio bajo: `giis.demo.tkrun.paqueteria.<modulo>`

Donde `<modulo>` es el nombre de la funcionalidad (ej.: `envios`, `seguimiento`, `almacen`, `entregas`...).

**Las utilidades de la plantilla se mantienen tal cual:**
- `giis.demo.util.Database` (lectura de `application.properties`, scripts SQL)
- `giis.demo.util.DbUtil` (acceso a BD con DbUtils)
- `giis.demo.util.SwingUtil` (utilidades Swing reutilizables)
- `giis.demo.util.SwingMain` (punto de entrada con botones de inicialización)
- `giis.demo.util.ApplicationException` / `UnexpectedException`

## Convenciones de código (a respetar estrictamente)

1. **Nada de paquetes en la raíz** ni con nombres `Izan_NNNNN`. Todo bajo `giis.demo.tkrun.paqueteria`.
2. **Sin tildes ni ñ** en nombres de paquetes, clases ni columnas de BD.
3. **SLF4J obligatorio**, prohibido `System.out.println` en el código de aplicación.
4. **Cada operación de negocio que modifique varias tablas debe ser transaccional**. Si en una historia hay un `UPDATE` + un `INSERT HistorialEvento`, deben ir en la misma transacción.
5. **Validación de entrada en el controlador** antes de invocar al modelo. El modelo asume datos válidos.
6. **Nombres de tablas en singular en MAYÚSCULA inicial** (`Envio`, `TramoRuta`, `HistorialEvento`...). Columnas en `snake_case` o `camelCase` consistentes (decidir y mantener).
7. **Todo cambio de estado de una entidad de negocio (envío, tramo, paquete) debe registrarse en `HistorialEvento`** con acción, responsable, fecha y comentario.
8. **DTO ≠ Entity**. Las clases Entity reflejan la tabla; los DTO son lo que la vista consume (pueden incluir joins, campos calculados, etc.).

## Roles del nuevo dominio

- **Cliente** — registra envíos por web, hace seguimiento, modifica entregas.
- **Empleado de oficina** — registra envíos en mostrador/teléfono, consulta envíos.
- **Operario de almacén** — gestiona carga/descarga con código de barras + pesaje.
- **Transportista** — confirma recogidas y entregas desde PDA.
- **Administrador** — gestiona tarifas, vehículos, oficinas, almacenes, usuarios.

## Las 6 historias de usuario a implementar

Estas 6 forman un flujo end-to-end coherente y son las que se demostrarán en la revisión de sprint:

1. **HU-01 — Registro de envío desde oficina**: alta de envío en mostrador con cálculo de tarifa y generación de identificador + código de barras.
2. **HU-02 — Asignación automática de ruta y vehículos**: tras crear un envío, el sistema asigna automáticamente todos los tramos (recogida → almacenes intermedios → entrega) y vehículos compatibles.
3. **HU-03 — Verificación de carga y descarga en almacén**: el operario valida código de barras, hace inspección visual y mide peso; detecta discrepancias y genera incidencias.
4. **HU-04 — Seguimiento de envío por el cliente**: vista cronológica del estado actual y todos los eventos del envío.
5. **HU-05 — Modificación del lugar de entrega**: el cliente cambia la dirección de entrega antes del último tramo, o redirige a oficina/almacén.
6. **HU-06 — Gestión de entregas fallidas y reintentos**: hasta 3 reintentos automáticos; tras 4 fallos el paquete queda depositado en oficina/almacén y se avisa al cliente.

## Buenas prácticas obligatorias

- **Un commit por historia** como mínimo (lo exige el enunciado). Mensajes de commit en formato `feat: HU-XX <titulo corto>` o `test: HU-XX ...` o `fix: HU-XX ...`.
- **Al menos un test JUnit por historia** sobre la capa de negocio (modelo).
- **Diseñar los tests por clases de equivalencia y valores límite** (mirar `TestFacturaModel.java` de la ordinaria como referencia de estilo).
- **Scripts `schema.sql` y `data.sql` siempre actualizados** y coherentes con el código.
- **Reinicialización de BD limpia entre tests** (`PRAGMA foreign_keys = OFF` → limpieza → `ON`).

## Lo que NO debes hacer

- **No conserves nada del dominio antiguo** (Incidencia, Tecnico, Zona, Tipo de incidencia, Presupuesto, Factura...). Bórralo todo del nuevo trabajo.
- **No inventes funcionalidades** que no estén en las 6 HU. El alcance está cerrado.
- **No optimices prematuramente**. Código claro y directo > código "inteligente".
- **No uses frameworks adicionales** (Spring, Hibernate, etc.). El stack es estricto: JDBC + DbUtils + Swing.

## Referencias internas

- Enunciado original: ver `docs/enunciado.pdf` (cuando se añada).
- Plantilla base: https://github.com/javiertuya/samples-test-dev
- Backlog completo y detalles: se irán añadiendo a `docs/` conforme avancemos.

## Cómo trabajamos

- Vamos historia por historia. **No empieces a programar todo de golpe.**
- Para cada historia: primero plantea modelo de datos afectado y firma de los métodos, valida conmigo, después implementas.
- Cada cambio de fase (esquema, modelo, vista, controlador, test) → commit independiente con mensaje descriptivo.
- Si tienes dudas funcionales, pregunta antes de inventar criterios.
