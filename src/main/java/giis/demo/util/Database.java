package giis.demo.util;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;

/**
 * Encapsula los datos de acceso JDBC, lectura de la configuracion
 * y scripts de base de datos para creacion y carga.
 */
public class Database extends DbUtil {
	//Localizacion de ficheros de configuracion y carga de bases de datos
	private static final String APP_PROPERTIES = "src/main/resources/application.properties";
	private static final String SQL_SCHEMA = "src/main/resources/schema.sql";
	private static final String SQL_LOAD = "src/main/resources/data.sql";
	//parametros de la base de datos leidos de application.properties (base de datos local sin usuario/password)
	private String driver;
	private String url;
	private static boolean databaseCreated=false;

	/**
	 * Crea una instancia, leyendo los parametros de driver y url de application.properties
	 */
	public Database() {
		Properties prop = new Properties();
		try (FileInputStream fs = new FileInputStream(APP_PROPERTIES)) {
			prop.load(fs);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
		driver = prop.getProperty("datasource.driver");
		url = prop.getProperty("datasource.url");
		if (driver == null || url == null)
			throw new ApplicationException("Configuracion de driver y/o url no encontrada en application.properties");
		DbUtils.loadDriver(driver);
	}

	public String getUrl() {
		return url;
	}

	/** 
	 * Creacion de una base de datos limpia a partir del script schema.sql en src/main/properties
	 * (si onlyOnce=true solo ejecutara el script la primera vez
	 */
	public void createDatabase(boolean onlyOnce) {
		// actua como singleton si onlyOnce=true: solo la primera vez que se instancia 
		// para mejorar rendimiento en pruebas
		if (!databaseCreated || !onlyOnce) {
			executeScript(SQL_SCHEMA);
			databaseCreated = true; // NOSONAR
		}
	}

	/** 
	 * Carga de datos iniciales a partir del script data.sql en src/main/properties
	 * Se evita ejecutar el script si la tabla Roles ya contiene filas para no causar
	 * conflictos de clave primaria cuando la BD ya está inicializada.
	 */
	public void loadDatabase() {
		boolean needLoad = true;
		try {
			// Comprueba si la tabla Roles existe y tiene filas
			List<Map<String, Object>> res = this.executeQueryMap("SELECT COUNT(*) as c FROM Rol");
			if (!res.isEmpty() && res.get(0).get("c") != null) {
				Object val = res.get(0).get("c");
				long cnt = 0;
				if (val instanceof Number) cnt = ((Number)val).longValue();
				else cnt = Long.parseLong(val.toString());
				if (cnt > 0) needLoad = false;
			}
		} catch (Exception e) {
			// Si hay cualquier problema (tabla no existe, errores), asumimos que hay que cargar
			needLoad = true;
		}
		if (needLoad) {
			executeScript(SQL_LOAD);
		}
	}
	
}