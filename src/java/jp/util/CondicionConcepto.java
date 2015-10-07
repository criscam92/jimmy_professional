package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum CondicionConcepto {
    NINGUNO(0, "Ninguno"),
    CXC(1, "CXC"),
    CXP(2, "CXP");
    
    private int valor;
    private String detalle;

    private CondicionConcepto(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }
    
    public static CondicionConcepto getFromValue(int value) {
        for (CondicionConcepto condicionConcepto : CondicionConcepto.values()) {
            if (condicionConcepto.getValor() == value) {
                return condicionConcepto;
            }
        }
        return null;
    }
    
    public static CondicionConcepto[] getFromValue(Integer[] arre) {
        CondicionConcepto[] condicionConceptos = new CondicionConcepto[arre.length];
        int count = 0;
        for (int b : arre) {
            condicionConceptos[count] = getFromValue(b);
            count++;
        }
        return condicionConceptos;
    }
    
    public static Map<Integer, String> getMapaCondicionConceptos() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (CondicionConcepto condicionConcepto : CondicionConcepto.values()) {
            mapTMP.put(condicionConcepto.getValor(), condicionConcepto.getDetalle());
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
    
    
}
