package enruta.sistole_proto_tampico;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TodosLosCampos {
	//Vector <Campo> campos = new Vector<Campo>();
	
	String is_tabla="Ruta";

// Si esta vacio, en descargar la informacion al servidor se regresa en el mismo orden
// Si no, podemos especificar los campos que queremos usar en la salida
//	String is_CamposDeSalida = "secuencia || direccion || cliente || lectura || fecha || lectura";
	String is_camposDeSalida = "";
	
	Hashtable<String, Campo> campos= new Hashtable();
	ContentValues cv_params= new ContentValues();
	
	Vector <String> camposEnOrden= new Vector<String>();
	
	
	TodosLosCampos(){
		
	}
	
	TodosLosCampos(ContentValues cv_params){
		this.cv_params= cv_params;
	}
	
	public void add(Campo campo){
		campos.put(campo.getNombre().toUpperCase(), campo);
		if (campo.esDeEntrada)
			camposEnOrden.add(campo.getNombre().toUpperCase());
	}
	
	public void byteToBD(SQLiteDatabase db, byte[] bytes, int secuenciaReal){
		
		//Vamos a verificar si el numero de orden ya esta en la bd
		String numOrden=campos.get("NUMORDEN").recortarByte(bytes);
		
		Cursor c=db.rawQuery("Select count(*) canti from ruta where numOrden='"+numOrden+"'", null) ;
		c.moveToFirst();
		
		if (c.getInt(c.getColumnIndex("canti"))>0){
			//Ya hay una poliza con ese numero de orden
			
			//Solo actualizaremos el indicador, despues sabremos que hacer con el indicador
			db.execSQL("update ruta set indicador='"+campos.get("INDICADOR").recortarByte(bytes)+"' where numOrden='"+numOrden+"' and trim(tipoLectura)=''");
			
			if (campos.get("INDICADOR").recortarByte(bytes).equals("B")){
				//Hay que cambiar Registros con indicador B a pagados y a enviar
				ContentValues cv_params = new ContentValues();
//				cv_params.put("tipolectura", "4");
				cv_params.put("estadoDeLaOrden", "EO005");
				cv_params.put("fecha", Main.obtieneFecha("ymd"));
				cv_params.put("hora", Main.obtieneFecha("his"));
				
//				cv_params.put("comentarios", "NO HABILITADO");
			
				cv_params.put("tipoLectura", "4");
				cv_params.put("envio", 1);
				


				db.update("ruta", cv_params,
						 " trim(tipoLectura)='' and numOrden='"+numOrden+"'", null);
			}
			
			c.close();
			return;
		}
		
		
		
		c.close();
		
		
		ContentValues cv_params = new ContentValues(this.cv_params);
		Enumeration <Campo> e;
		e=campos.elements();
		
		
		
		while (e.hasMoreElements())	{
		//for (int i=0; i<campos.size() ;i++){
			Campo campo=e.nextElement();
			if (campo.esDeEntrada)
				cv_params.put(campo.getNombre(), campo.recortarByte(bytes));
		}
		
		c= db.rawQuery("Select secuenciaReal from ruta order by secuenciaReal desc limit 1", null); 
		
		if (c.getCount()>0){
			c.moveToFirst();
			cv_params.put("secuenciaReal",c.getInt(c.getColumnIndex("secuenciaReal"))+1 );
		}else{
			cv_params.put("secuenciaReal", 1 );
		}
		
		try{
			db.insertOrThrow (is_tabla, null, cv_params);
		}catch(Throwable er){
			er.printStackTrace();
		}
		
	}
	
	public void byteToBD(SQLiteDatabase db, String bytes, int secuenciaReal){
		ContentValues cv_params = new ContentValues(this.cv_params);
		Enumeration <Campo> e;
		e=campos.elements();
		while (e.hasMoreElements())	{
		//for (int i=0; i<campos.size() ;i++){
			Campo campo=e.nextElement();
			if (campo.esDeEntrada)
				cv_params.put(campo.getNombre(), campo.recortarByte(bytes));
		}
		cv_params.put("secuenciaReal", secuenciaReal );
		db.insert(is_tabla, null, cv_params);
	}
	
	public String getCampo(String nombre){
		return campos.get(nombre).getNombre();
	}
	
	public int getLongCampo(String nombre){
		return campos.get(nombre.toUpperCase()).getLong();
	}
	
	public int getPosCampo(String nombre){
		return campos.get(nombre.toUpperCase()).getPos();
	}
	
	public String getRellenoCampo(String nombre){
		return campos.get(nombre.toUpperCase()).getRelleno();
	}
	
	public void getListaDeCamposFormateado(){
		getListaDeCamposFormateado(new String[0] );
	}
	
	public void getListaDeCamposFormateado(String[] lsa_campos){
		
		is_camposDeSalida="";
		String[] arreglo;
		
		
		
		if (lsa_campos.length>0){
			arreglo=lsa_campos;
		}else{
			//Debemos enviarlos en el orden
//			for (String ls_campo:camposEnOrden){
//				if (!is_camposDeSalida.equals(""))
//					is_camposDeSalida +="||";
//				
//				is_camposDeSalida=campos.get(ls_campo).campoSQLFormateado();
//			}
		
			arreglo= new String[camposEnOrden.size()];
			
			for (int i=0; i<camposEnOrden.size();i++){
				arreglo[i]= camposEnOrden.get(i);
			}
		}
		for (String ls_campo:arreglo){
			if (!is_camposDeSalida.equals(""))
				is_camposDeSalida +="||";
			
			is_camposDeSalida+=campos.get(ls_campo.toUpperCase()).campoSQLFormateado();
		}
			
		
	}
	
	

	public void byteToBD(SQLiteDatabase db, Globales globales, String poliza, String medidor){
		byte[] bytes;
		int secuenciaReal;
		String cadenaNueva="";
		
		ContentValues cv_params = new ContentValues(this.cv_params);
		Enumeration <Campo> e;
//		e=campos.elements();
//		
//		//Vamos a crear primero una cadena con todos los parametros de entrada
//		
//		while (e.hasMoreElements())	{
//			//for (int i=0; i<campos.size() ;i++){
//				Campo campo=e.nextElement();
//				boolean lugar=false;
//				if (campo.esDeEntrada){
//					
//					if (campo.getNombre().equalsIgnoreCase("poliza") ){
//						cadenaNueva+= Main.rellenaString(poliza, campo.getRelleno(), campo.getLong(), lugar);
//					}
//					else if(campo.getNombre().equalsIgnoreCase("serieMedidor")){
//						cadenaNueva+= Main.rellenaString(medidor, campo.getRelleno(), campo.getLong(), lugar);
//					}
//					else if(campo.getNombre().equalsIgnoreCase("numOrden")){
//						cadenaNueva+= Main.rellenaString("0", campo.getRelleno(), campo.getLong(), lugar);
//					}
//					else{
//						if (campo.getAlineacion()==Campo.D){
//							lugar=true;
//						}
//						cadenaNueva+= Main.rellenaString("", campo.getRelleno(), campo.getLong(), lugar);
//					}
//					
//				}
//					
//			}
		
		
		cadenaNueva+= Main.rellenaString("", " ", globales.tdlg.long_registro, false);
		bytes= cadenaNueva.getBytes();
		
		//Obtenemos el mayor secuencial real
		Cursor c=db.rawQuery("Select numOrden from ruta", null);
		
		e=campos.elements();
		
		
		while (e.hasMoreElements())	{
		//for (int i=0; i<campos.size() ;i++){
			Campo campo=e.nextElement();
			if (campo.esDeEntrada){
				
				if (campo.getNombre().equalsIgnoreCase("poliza") ){
					cv_params.put(campo.getNombre(),poliza);
				}
				else if(campo.getNombre().equalsIgnoreCase("serieMedidor")){
					cv_params.put(campo.getNombre(), medidor);
				}
				else if(campo.getNombre().equalsIgnoreCase("numOrden")){
					
					cv_params.put(campo.getNombre(), "0");
				}
				else{
					cv_params.put(campo.getNombre(), campo.recortarByte(bytes));
				}
			}
				
		}
		
		//La secuencia real ser� asignada al finalizar la entrega de ordenes
		
		c= db.rawQuery("Select secuenciaReal from ruta order by secuenciaReal desc limit 1", null); 
		
		if (c.getCount()>0){
			c.moveToFirst();
			cv_params.put("secuenciaReal",c.getInt(c.getColumnIndex("secuenciaReal"))+1 );
		}else{
			cv_params.put("secuenciaReal", 1 );
		}
		
		c= db.rawQuery("Select sinUso8 from ruta order by secuenciaReal desc limit 1", null); 
		
		if (c.getCount()>0){
			c.moveToFirst();
			cv_params.put("sinUso8",c.getInt(c.getColumnIndex("sinUso8"))+1 );
		}else{
			cv_params.put("sinUso8", 1 );
		}
		
		try{
			db.insertOrThrow (is_tabla, null, cv_params);
		}catch(Throwable er){
			er.printStackTrace();
		}
		
	}
	

}
