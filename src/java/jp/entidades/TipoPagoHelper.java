package jp.entidades;

import javax.persistence.Id;

public class TipoPagoHelper {

    @Id
    private int id;
    private int tipo;
    private double valor;
    private TipoPublicidad tipoPublicidad;

    public TipoPagoHelper() {
    }

    public TipoPagoHelper(int id, int tipo, double valor, TipoPublicidad tipoPublicidad) {
        this.id = id;
        this.tipo = tipo;
        this.valor = valor;
        this.tipoPublicidad = tipoPublicidad;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

}
