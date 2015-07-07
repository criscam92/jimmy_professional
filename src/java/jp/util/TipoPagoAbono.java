package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum TipoPagoAbono {

    ABONO(3, "Abono"),
    PUBLICIDAD(1, "Publidad"),
    COMISION(2, "Comisión");
    

    private int valor;
    private String detalle;

    private TipoPagoAbono(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static TipoPagoAbono getFromValue(int value) {
        for (TipoPagoAbono tipo : TipoPagoAbono.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static TipoPagoAbono[] getFromValue(Integer[] arre) {
        TipoPagoAbono[] estadoVisitasTMP = new TipoPagoAbono[arre.length];
        int count = 0;
        for (int b : arre) {
            estadoVisitasTMP[count] = getFromValue(b);
            count++;
        }
        System.out.println("Size-> "+estadoVisitasTMP.length);
        return estadoVisitasTMP;
    }

    public static Map<Integer, String> getMapaEstados() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (TipoPagoAbono estadoVisita : TipoPagoAbono.values()) {
            mapTMP.put(estadoVisita.getValor(), estadoVisita.getDetalle());

        }
        return mapTMP;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
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
