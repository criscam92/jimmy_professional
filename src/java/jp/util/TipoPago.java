package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum TipoPago {

    CONTADO(0, "Contado"),
    CREDITO(1, "Crédito"),
    CHEQUE(2, "Cheque"),
    CONSIGNACION(3, "Consignación"),
    MANO_A_MANO(4, "Mano a mano"),
    DEVOLUCION(5, "Devolución");
    
    private Integer valor;
    private String detalle;

    private TipoPago(Integer valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static TipoPago getFromValue(int value) {
        for (TipoPago tipo : TipoPago.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static TipoPago[] getFromValue(Integer[] arre) {
        TipoPago[] tiposPagosTMP = new TipoPago[arre.length];
        int count = 0;
        for (int b : arre) {
            tiposPagosTMP[count] = getFromValue(b);
            count++;
        }
        return tiposPagosTMP;
    }

    public static Map<String, Integer> getMapaEstados() {
        Map<String, Integer> mapTMP = new HashMap<>();
        for (TipoPago tipoPago : TipoPago.values()) {
            mapTMP.put(tipoPago.getDetalle(), tipoPago.getValor());
        }
        return mapTMP;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    @Override
    public String toString() {
        return detalle;
    }

}
