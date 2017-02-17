package enruta.sistole_proto_tampico;

public class EstructuraResumen {
	String descripcion="";
	String cantidad="";
	String porcentaje="";

	EstructuraResumen(String descripcion, String cantidad){
		this.descripcion=descripcion;
		this.cantidad=cantidad;
	}
	
	EstructuraResumen(String descripcion, String cantidad, String porcentaje){
		this.descripcion=descripcion;
		this.cantidad=cantidad;
		this.porcentaje=porcentaje;
	}
}
