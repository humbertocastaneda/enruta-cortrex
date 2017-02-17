package enruta.sistole_proto_tampico;

import enruta.cortrex_mexicana.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class CPL extends Activity {
	
	public final static int NINGUNO=0;
	public final static int ADMINISTRADOR=1;
	public final static int LECTURISTA=2;
	public final static int SUPERUSUARIO=3;
	
	public final static int ENTRADA=1;
	public final static int LOGIN=2;
	public final static int MAIN=3;
	
	public final static int CAMBIAR_USUARIO=1;
	
	public int ii_perfil=NINGUNO;
	public int ii_pantallaActual=NINGUNO;
	
	
	boolean esSuperUsuario=false;

	String is_nombre_Lect="";
	
	TextView tv_msj_login, tv_usuario, tv_contrasena;
	EditText et_usuario, et_contrasena ;
	
	DBHelper dbHelper;
	SQLiteDatabase db;
	
	String admonPass="9776";
	String superUsuarioPass="9776";
	
	String usuario="";
	
	Globales globales;
	ImageView iv_logo;
	
	Button b_admon;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	//	requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cpl);
		ii_pantallaActual=ENTRADA;
		
		 globales = ((Globales)getApplicationContext());
		 
		 esconderAdministrador();
		
		 iv_logo= (ImageView) findViewById(R.id.iv_logo);
		 TextView tv_version= (TextView) findViewById(R.id.tv_version_lbl);
			
			try {
				tv_version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode+ ", "+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
				
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			estableceVariablesDePaises();
			
	}
	
	


	
	/**
	  * Aqui se van a cargar las variables que correspondan a cada pais
	  */
	private void estableceVariablesDePaises() {
		// TODO Auto-generated method stub
		
		 switch(globales.ii_pais){
		 case Globales.ARGENTINA:
			 globales.tdlg= new TomaDeLecturasArgentina(this);
			 break;
//		 case Globales.COLOMBIA:
//			 globales.tdlg= new TomaDeLecturasColombia(this);
//			 break;
		 }
		 
		 iv_logo.setImageResource(globales.logo);
		 
	}






	public void entrarAdministrador(View v){
		ii_perfil=ADMINISTRADOR;
		setContentView(R.layout.p_login);
		ii_pantallaActual=LOGIN;
		getObjetosLogin();
		tv_msj_login.setText(R.string.str_login_msj_admon);
		tv_usuario.setVisibility(View.GONE);
		et_usuario.setVisibility(View.GONE);
		globales.secuenciaSuperUsuario="A";
		et_contrasena.requestFocus();
		mostrarTeclado();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
	}
	
	public void entrarLecturista(View v){
		ii_perfil=LECTURISTA;
		ii_pantallaActual=LOGIN;
		setContentView(R.layout.p_login);
		getObjetosLogin();
		globales.secuenciaSuperUsuario+="C";
		
		//Hay que adaptar según el tipo de validacion
		switch(globales.tipoDeValidacion){
		
		case Globales.AMBAS:
			et_usuario.requestFocus();
			break;
		
		case Globales.USUARIO:
			et_usuario.requestFocus();
			
			et_contrasena.setVisibility(View.GONE);
			tv_contrasena.setVisibility(View.GONE);
			
			tv_usuario.setVisibility(View.VISIBLE);
			et_usuario.setVisibility(View.VISIBLE);
			break;
			
		case Globales.CONTRASEÑA:
			tv_usuario.setVisibility(View.VISIBLE);
			et_usuario.setVisibility(View.GONE);
			
			et_contrasena.setVisibility(View.VISIBLE);
			tv_contrasena.setVisibility(View.GONE);
			
			et_contrasena.requestFocus();
			break;
		}
		
//		if(globales.tipoDeValidacion==Globales.CONTRASEÑA)
//			tv_msj_login.setText(R.string.str_login_msj_lecturista_contrasena);
//		else
			tv_msj_login.setText(globales.mensajeContraseñaLecturista);
		
		mostrarTeclado();
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	
	
	public void entrar(View v){
		boolean validar=false;
		switch(ii_perfil){
			case ADMINISTRADOR:
				esconderTeclado();
				validar=validarAdministrador();
				break;
			case LECTURISTA:
				esconderTeclado();
				validar=validarLecturista();
				
				break;
		}
		
		if (validar){
			//Aqui abrimos la actividad
			
			//Hay que empezar a restingir las cosas que cada uno puede hacer
			
			Intent intent =new Intent (this, Main.class);
			intent.putExtra("rol", ii_perfil);
			intent.putExtra("esSuperUsuario", esSuperUsuario);
			intent.putExtra("nombre", is_nombre_Lect);
			
		    
			startActivityForResult(intent, MAIN);
		}
		else{
			
			switch(ii_perfil){
			case ADMINISTRADOR:
				Toast.makeText(this, getString(R.string.msj_cpl_verifique_contrasena) , Toast.LENGTH_LONG).show();
				globales.secuenciaSuperUsuario+="B";
				break;
			case LECTURISTA:
				if(globales.tipoDeValidacion==Globales.CONTRASEÑA)
					Toast.makeText(this, getString(R.string.msj_cpl_verifique_contrasena) , Toast.LENGTH_LONG).show();
				else
					Toast.makeText(this,getString(R.string.msj_cpl_verifique_usuario_contrasena) , Toast.LENGTH_LONG).show();
				break;
		}
			et_usuario.setText("");
			et_contrasena.setText("");
			
		}
		
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle bu_params=null;
		if (data!=null) {
			bu_params= data.getExtras();
		}
		switch(requestCode){
		case MAIN:
			if (resultCode == Activity.RESULT_CANCELED){
				finish(); //Cancelo con el back
			}
			else if (resultCode == Activity.RESULT_OK){
				//Cuando cambia de usuario...
				if (bu_params.getInt("opcion")== CAMBIAR_USUARIO){
					cambiarUsuario();
				}
			}
			
			
		}
	}
	
	
	public boolean validarLecturista(){
		if (et_contrasena.getText().toString().trim().equals("")){
			Toast.makeText(this, "Escriba un numero de usuario", Toast.LENGTH_LONG);
			return false;
		}
		
//		if (et_contrasena.getText().toString().trim().length()!=10){
//			Toast.makeText(this, "El numero de usuario debe ser de 10 digitos", Toast.LENGTH_LONG);
//			return false;
//		}
		globales.setUsuario(  et_contrasena.getText().toString().trim());
		return true;
//		boolean esValido=false;
//		
//		esSuperUsuario=(et_contrasena.getText().toString().equals(superUsuarioPass)||et_usuario.getText().toString().equals(superUsuarioPass)) && globales.secuenciaSuperUsuario.equals(Globales.SECUENCIA_CORRECTA_SUPER);
//		
//		//Hay que buscar que la combinacion usuario y contraseña sean correctos
//		if (esSuperUsuario){
//			esValido=true;
//			is_nombre_Lect="Super Usuario";
//			globales.setUsuario("9776");
//		}
//		else{
//			openDatabase();
//			Cursor c;
//			
//			switch(globales.tipoDeValidacion){
//			case Globales.CONTRASEÑA:
//				c= db.rawQuery("Select * from usuarios where trim (contrasena)='" +et_contrasena.getText().toString().trim()+"'" , null) ;
//				break;
//			case Globales.USUARIO:
//				c= db.rawQuery("Select * from usuarios where trim(usuario)='" +et_usuario.getText().toString().trim() +"' " , null) ;
//				break;
//			default:
//				c= db.rawQuery("Select * from usuarios where trim(usuario)='" +et_usuario.getText().toString().trim() +"' "+
//						" and trim (contrasena)='" +et_contrasena.getText().toString().trim()+"'" , null) ;
//				break;
//			}
//			
//			
//			
//			if (c.getCount()>0){
//				esValido= true;
//				c.moveToFirst();
//				
//				if (globales.tipoDeValidacion==Globales.CONTRASEÑA){
//					globales.setUsuario(et_contrasena.getText().toString().trim());
//				}
//				else{
//					globales.setUsuario(et_usuario.getText().toString().trim());
//				}
//				globales.controlCalidadFotos=c.getInt(c.getColumnIndex("fotosControlCalidad"));
//				globales.baremo=Lectura.toInteger(c.getString(c.getColumnIndex("baremo")));
//				is_nombre_Lect=c.getString(c.getColumnIndex("nombre"));
//				
//			}
//				
//			
//			c.close();
////			c= db.rawQuery("Select * from usuarios ", null) ;
////			c.moveToFirst();
////			String usuario= c.getString(0);
////			String contraseña=c.getString(1);
////			
////			c.moveToNext();
////			usuario= c.getString(0);
////			contraseña=c.getString(1);
//
//			closeDatabase();
//		}
//
//		
//		return esValido;
	}
	
    private void openDatabase(){
    	dbHelper= new DBHelper(this);
		
        db = dbHelper.getReadableDatabase();
    }
	
	 private void closeDatabase(){
	    	db.close();
	        dbHelper.close();
	        
	    }
	
	public boolean validarAdministrador(){
		openDatabase();
		//Buscamos si existe la palabra administrador en los ususatios
		Cursor c;
		c= db.rawQuery("Select * from usuarios where rol in ('2', '3') " , null) ;
		
		if (c.getCount()>0){
			//Existe un administrador, usaremos su contraseña para entrar al sistema
			c.close();
			c= db.rawQuery("Select * from usuarios where rol in ('2', '3') and trim (contrasena)='" +et_contrasena.getText().toString().trim()+"'" , null) ;
			if (c.getCount()>0)
			{
				c.close();
				return true;
			}
			else{
				c.close();
				return false;
			}
		}
		c.close();
		closeDatabase();
		try{
			globales.ii_claveIngresada=Integer.parseInt(this.et_contrasena.getText().toString());
		}
		catch(Throwable e){
			
		}
		if (this.et_contrasena.getText().toString().equals(String.valueOf(globales.CLAVE_COMAPA_ZC))||this.et_contrasena.getText().toString().equals(String.valueOf(globales.CLAVE_ENRUTA))
				||this.et_contrasena.getText().toString().equals(String.valueOf(globales.CLAVE_PRUEBAS2))
				||this.et_contrasena.getText().toString().equals(String.valueOf(globales.CLAVE_PRUEBAS3))
				||this.et_contrasena.getText().toString().equals(String.valueOf(globales.CLAVE_MEXICANA))
				|| this.et_contrasena.getText().toString().equals(String.valueOf(globales.CLAVE_PREPAGO))){
			return true;
		}
		
		return this.et_contrasena.getText().toString().equals(admonPass);
	}
	
	
	public void cambiarUsuario(){
		setContentView(R.layout.cpl);
		esSuperUsuario=false;
		ii_pantallaActual=ENTRADA;
		ii_perfil=NINGUNO;
		TextView tv_version= (TextView) findViewById(R.id.tv_version_lbl);
		
		try {
			tv_version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode+ ", "+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		iv_logo= (ImageView) findViewById(R.id.iv_logo);
		iv_logo.setImageResource(globales.logo);
		globales.anomaliaARepetir="";
		globales.subAnomaliaARepetir="";
		
		
			
		 esconderAdministrador();
	
	}
	
	private void esconderAdministrador() {
		// TODO Auto-generated method stub
		openDatabase();
		 b_admon=(Button) findViewById(R.id.b_admon);
		Cursor c = db.rawQuery(
				"Select value from config where key='server_gprs'", null);
		c.moveToFirst();
		
		if (c.getCount()>0){
			if (c.getString(c.getColumnIndex("value")).length()>0){
				b_admon.setVisibility(View.GONE);
			}
			else{
				b_admon.setVisibility(View.VISIBLE);
			}
		}
		else{
			b_admon.setVisibility(View.VISIBLE);
		}
		
		closeDatabase();
	}





	public void getObjetosLogin(){
		tv_msj_login= (TextView) findViewById(R.id.tv_msj_login);
		et_usuario= (EditText) findViewById(R.id.et_usuario);
		et_contrasena= (EditText) findViewById(R.id.et_contrasena);
		tv_usuario= (TextView) findViewById(R.id.tv_usuario);
		tv_contrasena=(TextView) findViewById(R.id.tv_contrasena);
		
		OnEditorActionListener oeal=new OnEditorActionListener() {

			

			@Override
			public boolean onEditorAction(TextView arg0, int arg1,
					KeyEvent arg2) {
				// TODO Auto-generated method stub
				entrar(arg0);
				return false;
			}
	       };
	       
		if (globales.tipoDeValidacion== Globales.USUARIO){
			et_usuario.setOnEditorActionListener(oeal);
		}
		else{
			et_contrasena.setOnEditorActionListener(oeal);
		}
		
//et_contrasena.setOnEditorActionListener(new OnEditorActionListener() {
//
//			
//
//			@Override
//			public boolean onEditorAction(TextView arg0, int arg1,
//					KeyEvent arg2) {
//				// TODO Auto-generated method stub
//				entrar(arg0);
//				return false;
//			}
//	       });
	}
	
	public void salir(){
		finish();
	}
	
	public void onBackPressed() {
		switch (ii_pantallaActual){
		case ENTRADA:
			salir();
			break;
		case LOGIN:
			cambiarUsuario();
			break;
		
		}
		
	}
	
		
	
	
	public void esconderTeclado(){
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_usuario.getWindowToken(), 0);
	}
	
	public void mostrarTeclado(){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		  imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}

}
