package jp.entidades;

import javax.persistence.Id;

public class TipoPagoHelper {

    @Id
    private int id;
    private Long idObject;
    private int tipo;
    private double valor;    
    private TipoPublicidad tipoPublicidad;
    private ReciboCaja reciboCaja;

    public TipoPagoHelper() {
    }

    public TipoPagoHelper(int id, Long idObject, int tipo, double valor, TipoPublicidad tipoPublicidad, ReciboCaja reciboCaja) {
        this.id = id;
        this.idObject = idObject;
        this.tipo = tipo;
        this.valor = valor;
        this.tipoPublicidad = tipoPublicidad;
        this.reciboCaja = reciboCaja;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getIdObject() {
        return idObject;
    }

    public void setIdObject(Long idObject) {
        this.idObject = idObject;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public TipoPublicidad getTipoPublicidad() {
        return tipoPublicidad;
    }

    public void setTipoPublicidad(TipoPublicidad tipoPublicidad) {
        this.tipoPublicidad = tipoPublicidad;
    }

    public ReciboCaja getReciboCaja() {
        return reciboCaja;
    }

    public void setReciboCaja(ReciboCaja reciboCaja) {
        this.reciboCaja = reciboCaja;
    }

}
