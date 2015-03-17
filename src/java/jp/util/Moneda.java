package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum Moneda {

    PESO(0, "Peso"),
    DOLAR(1, "Dolar");

    private Integer valor;
    private String detalle;

    private Moneda(Integer valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static Moneda getFromValue(int value) {
        for (Moneda tipo : Moneda.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static Moneda[] getFromValue(Integer[] arre) {
        Moneda[] monedaTMP = new Moneda[arre.length];
        int count = 0;
        for (int b : arre) {
            monedaTMP[count] = getFromValue(b);
            count++;
        }
        return monedaTMP;
    }

    public static Map<String, Integer> getMonedas() {
        Map<String, Integer> mapTMP = new HashMap<>();
        for (Moneda moneda : Moneda.values()) {
            mapTMP.put(moneda.getDetalle(), moneda.getValor());
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
