package jp.entidades;

public class DetallePagoHelper {

    private long id;
    private double valor;
    private Pago pago;
    private TipoPublicidad tipoPublicidad;

    public DetallePagoHelper() {
    }
    
    public DetallePagoHelper(long id, double valor, Pago pago, TipoPublicidad tipoPublicidad) {
        this.id = id;
        this.valor = valor;
        this.pago = pago;
        this.tipoPublicidad = tipoPublicidad;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public TipoPublicidad getTipoPublicidad() {
        return tipoPublicidad;
    }

    public void setTipoPublicidad(TipoPublicidad tipoPublicidad) {
        this.tipoPublicidad = tipoPublicidad;
    }

}
