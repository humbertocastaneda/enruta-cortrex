package enruta.sistole_proto_tampico;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Vector;

import enruta.cortrex_mexicana.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.TextView;

public class Resumen extends Fragment {
	
	View rootView;
	
	DBHelper dbHelper;
	SQLiteDatabase db;
	TextView tv_resumen;
	Main ma_papa;
		 
	   @Override
	   public View onCreateView(LayoutInflater inflater, ViewGroup container,
	          Bundle savedInstanceState) {
	 
	       rootView = inflater.inflate(R.layout.entrada, container, false);
	       ma_papa=(Main) getActivity();
	       actualizaResumen();
	       return rootView;
	   }
	   
	   public void actualizaResumen(){
	    	long ll_total;
	    	long ll_tomadas;
	    	long ll_fotos;
	    	long ll_restantes;
	    	long ll_conAnom;
	    	long ll_noRegistrados;
	    	String ls_archivo;
	    	
	    	
	    	String ls_resumen;
	    	if (ma_papa.globales.tdlg==null)
				return;
		 
	    	final GridView gv_resumen= (GridView) rootView.findViewById(R.id.gv_resumen);
	    	tv_resumen= (TextView) rootView.findViewById(R.id.tv_resumen);
	    	Vector <EstructuraResumen>resumen= new Vector<EstructuraResumen>();
	    	
	    	Cursor c;
	    	openDatabase();
	    	c= db.rawQuery("Select count(*) canti from Ruta", null);
	    	c.moveToFirst();
	    	ll_total=c.getLong(c.getColumnIndex("canti"));
	    	if (ll_total>0){
	    		try{
	    			c=db.rawQuery("Select value from config where key='cpl'", null);
	        		c.moveToFirst();
	       		 	ls_archivo=c.getString(c.getColumnIndex("value"));
	    		}
	    		catch(Throwable e){
	    			ls_archivo="";
	    		}
	    		
	    		c=db.rawQuery("Select count(*) canti from ruta where tipoLectura='0'", null);
	    		 c.moveToFirst();
	    		 
	    		ll_tomadas=c.getLong(c.getColumnIndex("canti"));
	    		 c=db.rawQuery("Select count(*) canti from fotos", null);
	    		 c.moveToFirst();
	        	ll_fotos=c.getLong(c.getColumnIndex("canti"));
	        	c.close();
	        	
	        	 c=db.rawQuery("Select count(*) canti from ruta where tipoLectura='4'", null);
	        	 c.moveToFirst();
	        	 ll_conAnom=c.getLong(c.getColumnIndex("canti"));
	        	 c.close();
	        	 
	        	 c=db.rawQuery("Select count(*) canti from ruta where trim(tipoLectura)=''", null);
	        	 c.moveToFirst();
	        	 ll_restantes=c.getLong(c.getColumnIndex("canti"));
	        	 c.close();
	        	 
	        	 c=db.rawQuery("Select count(*) canti from ruta where numOrden=0", null);
	        	 c.moveToFirst();
	        	 ll_noRegistrados=c.getLong(c.getColumnIndex("canti"));
	        	 c.close();
	        	
	        	//ll_restantes = ll_total-ll_tomadas ;
	        	
//	        	ls_resumen="Total de Lecturas " + ll_total +"\n" +
//	        			"Medidores con Lectura " +  + ll_tomadas +"\n" +
//	        			"Medidores con Anomalias "+  ll_conAnom +"\n" +
//	        			"Lecturas Restantes "+  ll_restantes +"\n\n" +
//	        			
//	        			"Fotos Tomadas "+ ll_fotos +"\n\n" +
//	        			
//	        			"No Registrados "+ ll_noRegistrados;
//	        	
//	        	tv_resumen.setText(ls_resumen);
	        	 
	        	 float porcentaje=0;
	        	 DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
	        	 otherSymbols.setDecimalSeparator('.');
	        	 otherSymbols.setGroupingSeparator(','); 
	        	 DecimalFormat formatter = new DecimalFormat("##0.00", otherSymbols);
	        	 
	        	 for(String s:ma_papa.log){
	        		 String[]sa=s.split("\t");
	        		 resumen.add(new EstructuraResumen(sa[0],sa[1]));
	        	 }
	        	 
	        	 resumen.add(new EstructuraResumen( getString(R.string.msj_main_total_lecturas), String.valueOf(ll_total)));
	        	 resumen.add(new EstructuraResumen(getString(R.string.msj_main_fotos_tomadas), String.valueOf(ll_fotos)));
	        	 resumen.add(new EstructuraResumen("", ""));
	        	 porcentaje=  (((float)ll_restantes*100) /(float)ll_total);
	        	 resumen.add(new EstructuraResumen(getString(R.string.msj_main_lecturas_restantes),String.valueOf(ll_restantes),  formatter.format(porcentaje) +"%"));
	        	 porcentaje=  (((float)ll_tomadas*100) /(float)ll_total);
	        	 resumen.add(new EstructuraResumen(getString(R.string.msj_main_medidores_con_lectura), String.valueOf(ll_tomadas),  formatter.format(porcentaje) +"%"));
	        	 porcentaje=  (((float)ll_conAnom*100) /(float)ll_total);
	        	 resumen.add(new EstructuraResumen( getString(R.string.msj_main_medidores_con_anomalias),String.valueOf(ll_conAnom), formatter.format(porcentaje) +"%"));
	        	 
	        	 resumen.add(new EstructuraResumen("", ""));
	        	 
	        	 if (ma_papa.globales.mostrarNoRegistrados)
	        		 resumen.add(new EstructuraResumen("Nuevos Puntos", String.valueOf(ll_noRegistrados)));
	        	 
	        	 resumen.add(new EstructuraResumen("", "")); //Agregamos una linea mas
	        	 final ResumenGridAdapter adapter = new ResumenGridAdapter(getActivity(), resumen, ma_papa.infoFontSize * ma_papa.porcentaje2);
	        	 
//	        	 gv_resumen.invalidateViews();
//	        	 adapter.notifyDataSetChanged();
	        	 
	        	 gv_resumen.setAdapter(adapter);
	        	 
			    	
		        	tv_resumen.setVisibility(View.GONE);
		        	gv_resumen.setVisibility(View.VISIBLE);
		        	
//		        	gv_resumen.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//		    			@Override
//		    			public void onGlobalLayout() {
//				        	ViewGroup.LayoutParams layoutParams = gv_resumen.getLayoutParams();
//				        	layoutParams.height =(int) adapter.height; //this is in pixels
//				        	gv_resumen.setLayoutParams(layoutParams);
//		    			   
//		    			 }
//		    			});
		        	
//		        	ViewGroup.LayoutParams layoutParams = gv_resumen.getLayoutParams();
//		        	layoutParams.height =(int) adapter.height; //this is in pixels
//		        	gv_resumen.setLayoutParams(layoutParams);
		        	
		        	
	    	} else{
//	    		tv_resumen.setText("No hay itinerarios cargados" );
	    		tv_resumen.setVisibility(View.VISIBLE);
	        	gv_resumen.setVisibility(View.GONE);
	    	}
	    	
	    	
	    	closeDatabase();
	    	
	    	
	    }
	    
	    private void openDatabase(){
	    	dbHelper= new DBHelper(getActivity());
			
	        db = dbHelper.getReadableDatabase();
	    }
		
		 private void closeDatabase(){
		    	db.close();
		        dbHelper.close();
		        
		    }
	
}
