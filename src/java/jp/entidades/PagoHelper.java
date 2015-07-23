package jp.entidades;

import java.util.List;
import javax.persistence.Id;

public class PagoHelper {

    @Id
    private int id;
    private Pago pago;
    private List<TipoPagoHelper> tipoPagoHelpers;

    public PagoHelper() {
    }

    public PagoHelper(int id, Pago pago, List<TipoPagoHelper> tipoPagoHelpers) {
        this.id = id;
        this.pago = pago;
        this.tipoPagoHelpers = tipoPagoHelpers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public List<TipoPagoHelper> getTipoPagoHelpers() {
        return tipoPagoHelpers;
    }

    public void setTipoPagoHelpers(List<TipoPagoHelper> tipoPagoHelpers) {
        this.tipoPagoHelpers = tipoPagoHelpers;
    }
    
}
