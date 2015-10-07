package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum TipoConcepto {
    
    NEUTRAL(0, "Neutral"),
    INGRESO(1, "Ingreso"),
    EGRESO(2, "Egreso");
    
    private int valor;
    private String detalle;
    
    private TipoConcepto(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }
    
    public static TipoConcepto getFromValue(int value) {
        for (TipoConcepto tipoConcepto : TipoConcepto.values()) {
            if (tipoConcepto.getValor() == value) {
                return tipoConcepto;
            }
        }
        return null;
    }
    
    public static TipoConcepto[] getFromValue(Integer[] arre) {
        TipoConcepto[] tiposConceptos = new TipoConcepto[arre.length];
        int count = 0;
        for (int b : arre) {
            tiposConceptos[count] = getFromValue(b);
            count++;
        }
        return tiposConceptos;
    }
    
    public static Map<Integer, String> getMapaTiposConceptos() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (TipoConcepto tipoConcepto : TipoConcepto.values()) {
            mapTMP.put(tipoConcepto.getValor(), tipoConcepto.getDetalle());

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
