package enruta.sistole_proto_tampico;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	// Ruta por defecto de las bases de datos en el sistema Android
	private static String DB_PATH = "/data/data/enruta.sistole_proto_tampico/databases/";

	private static String DB_NAME = "sistole_gen.db";

	private SQLiteDatabase myDataBase;

	private final Context myContext;
	
	private static DBHelper mInstance = null;
	
	private static int version=9;

	/**
	 * Constructor Toma referencia hacia el contexto de la aplicaci�n que lo
	 * invoca para poder acceder a los 'assets' y 'resources' de la aplicaci�n.
	 * Crea un objeto DBOpenHelper que nos permitir� controlar la apertura de la
	 * base de datos.
	 * 
	 * @param context
	 */
	public DBHelper(Context context) {

		super(context, DB_NAME, null, version);
		this.myContext = context;

	}
	
	/**
	 * Me permite no tener que cerrar el cursor a cada rato
	 * @param ctx
	 * @return
	 */
	public static DBHelper getInstance(Context ctx) {
	      
	    // Use the application context, which will ensure that you 
	    // don't accidentally leak an Activity's context.
	    // See this article for more information: http://bit.ly/6LRzfx
	    if (mInstance == null) {
	      mInstance = new DBHelper(ctx.getApplicationContext());
	    }
	    return mInstance;
	  }

	/**
	 * Crea una base de datos vac�a en el sistema y la reescribe con nuestro
	 * fichero de base de datos.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// la base de datos existe y no hacemos nada.
		} else {
			// Llamando a este m�todo se crea la base de datos vac�a en la ruta
			// por defecto del sistema
			// de nuestra aplicaci�n por lo que podremos sobreescribirla con
			// nuestra base de datos.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {
				throw new Error("Error copiando Base de Datos");
			}
		}

	}

	/**
	 * Comprueba si la base de datos existe para evitar copiar siempre el
	 * fichero cada vez que se abra la aplicaci�n.
	 * 
	 * @return true si existe, false si no existe
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {

			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// si llegamos aqui es porque la base de datos no existe todav�a.

		}
		if (checkDB != null) {

			checkDB.close();

		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copia nuestra base de datos desde la carpeta assets a la reci�n creada
	 * base de datos en la carpeta de sistema, desde d�nde podremos acceder a
	 * ella. Esto se hace con bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Abrimos el fichero de base de datos como entrada
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Ruta a la base de datos vac�a reci�n creada
		String outFileName = DB_PATH + DB_NAME;

		// Abrimos la base de datos vac�a como salida
		OutputStream myOutput = new FileOutputStream(outFileName);

		// Transferimos los bytes desde el fichero de entrada al de salida
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Liberamos los streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void open() throws SQLException {

		// Abre la base de datos
		try {
			createDataBase();
		} catch (IOException e) {
			throw new Error("Ha sido imposible crear la Base de Datos");	
		}

		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Vamos a crear las tablas necesarias
		//URL
		db.execSQL("CREATE TABLE URL (cpl TEXT, lote , centro TEXT, empresa , numBluetooth TEXT, macBluetooth TEXT, macImpresora TEXT, pais TEXT, factorBaremo TEXT, modo TEXT, numFotos TEXT, rutaDescarga TEXT, tamanoFotos TEXT, formatoFoto TEXT)");
		//Anomalias
		db.execSQL("CREATE TABLE Anomalia (desc , conv , capt , subanomalia , ausente , mens , lectura , foto , anomalia , activa , tipo , pais TEXT)");
		
		//Codigos Ejecucion
		
		db.execSQL("CREATE TABLE codigosEjecucion (desc , conv , capt , subanomalia , ausente , mens , lectura , foto , anomalia , activa , tipo , pais TEXT)");
		
		//Mensajes
		db.execSQL("CREATE TABLE Mensajes (mensaje TEXT)");
		
		//Lecturas (el campo descaradamente)
		db.execSQL("CREATE TABLE Lecturas (registro)");
				
		//No Registrados
		db.execSQL("CREATE TABLE NoRegistrados (poliza TEXT)");
		//Ruta
		db.execSQL("CREATE TABLE Ruta (verDatos default 0, estadoDeLaOrden default 'EO001', entrecalles default '',municipio default '',calle default '', indicador,vencido default '', diametro_toma default '', tipo_usuario, giro default '', balance default '', fecha_utlimo_pago default '', fechaEjecucion, FechaRealizacion, envio default 0, numOrden, tipoDeOrden, motivo, descOrden, supervisionLectura , reclamacionLectura , reclamacion , nisRad , poliza , sectorlargo , sectorCorto , tarifa , numEsferas , consAnoAnt , consBimAnt , ilr , marcaMedidor , tipoMedidor , serieMedidor , aviso , comoLlegar2 , comoLlegar1 , numPortal , numEdificio , secuencia TEXT, cliente TEXT, colonia TEXT, " +
				" fechadeTransmision default '', fechadeRecepcion default '',fechadeAsignacion default '', direccion TEXT, lectura, anomalia, texto, intentos, fecha, hora, sospechosa, intento1, intento2, intento3, intento4, intento5, intento6, dondeEsta, estadoDelSuministro, registro, subAnomalia, comentarios, terminacion default '-1', fotoAlFinal default 0, ordenDeLectura  default '', latitud default '0.0', longitud default '0.0', anomInst, tipoLectura default '', " +
				" sinUso1 default '', sinUso2 default '', sinUso3 default '', sinUso4 default '', sinUso5 default '', sinUso6 default '', sinUso7 default '', sinUso8 default '', consumo default '', mensaje default '', numEsferasReal default '', serieMedidorReal default '', estadoDelSuministroReal default '', "+
				" codigoLectura default '', lecturaAnterior default '', baremo default '', divisionContrato default '', lecturista default '', supervision default '', advertencias default '', ubicacion default '', situacionDelSuministro default '', fechaAviso default '', rutaReal default '', estimaciones default '', " +
				"escalera default '',piso default '', puerta default '', secuenciaReal,  ultimo_pago default 0,  diametro default '', fechaEnvio default '', habitado default '0', fechaDeInicio default '')");
		//Usuarios
		db.execSQL("CREATE TABLE usuarios (usuario , contrasena , nombre, rol default 1, fotosControlCalidad default 1, baremo default 75)");
		//fotos
		db.execSQL("CREATE TABLE fotos (secuencial, nombre , foto, envio default 1, temporal)");
		//Encabezado
		db.execSQL("CREATE TABLE encabezado (cpl , centro , lote , descargada, lecturista, registro, ultimoSeleccionado default 0)");
		//Configuraciones globales y extras
		db.execSQL("CREATE TABLE config (key, value, selected)");
		//Configuraciones globales y extras
		db.execSQL("CREATE TABLE usoAnomalias (anomalia, veces default 0, fecha )");
		
		db.execSQL("CREATE TABLE rutaGPS (id INTEGER PRIMARY KEY,latitud, longitud, PTN, fecha, tipo)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Aqui actualizamos las tablas , pero que no se nos olvide agregarlo tambien en onCreate
//		if (oldVersion<=1){
//			db.execSQL("ALTER  TABLE Ruta add column dondeEsta" );
//			db.execSQL("ALTER  TABLE Ruta add column estadoDelSuministro" );
//			db.execSQL("ALTER  TABLE Ruta add column registro" );
//		}
		
		if (oldVersion<=1){
		db.execSQL("ALTER  TABLE Ruta add column estadoDeLaOrden default 'EO001'" );
		}
		
		if (oldVersion<=2){
			db.execSQL("ALTER  TABLE Ruta add column verDatos default 0" );
			}
		
		if (oldVersion<=3){
			db.execSQL("CREATE TABLE codigosEjecucion (desc , conv , capt , subanomalia , ausente , mens , lectura , foto , anomalia , activa , tipo , pais TEXT)");
		}
		
		if (oldVersion<=4){
//			db.execSQL("ALTER  TABLE Ruta add column vencido default 0" );
//			db.execSQL("ALTER  TABLE Ruta add column balance default 0" );
			db.execSQL("ALTER  TABLE Ruta add column ultimo_pago default 0" );
//			db.execSQL("ALTER  TABLE Ruta add column fecha_ultimo_pago default ''" );
			
//			db.execSQL("ALTER  TABLE Ruta add column giro default ''" );
			db.execSQL("ALTER  TABLE Ruta add column diametro default ''" );
			
			db.execSQL("ALTER  TABLE Ruta add column fechaEnvio default ''" );
			}
		
		if (oldVersion<=5){
			db.execSQL("ALTER  TABLE Ruta add column fechaDeInicio default ''" );
//			db.execSQL("ALTER  TABLE Ruta add column habitado default '0'" );
		}
		
		if (oldVersion<=7){
			db.execSQL("CREATE TABLE rutaGPS (id default 0, latitud, longitud, PTN, fecha)");
		}
		
		if (oldVersion<=8){
			db.execSQL("drop TABLE rutaGPS");
			db.execSQL("CREATE TABLE rutaGPS (id INTEGER PRIMARY KEY, latitud, longitud, PTN, fecha, tipo)");
		}
	}
	
	
}