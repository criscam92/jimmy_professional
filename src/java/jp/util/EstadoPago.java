package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum EstadoPago {

    REALIZADA(1, "Realizado"),
    CANCELADA(2, "Anulado");

    private int valor;
    private String detalle;

    private EstadoPago(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static EstadoPago getFromValue(int value) {
        for (EstadoPago tipo : EstadoPago.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static EstadoPago[] getFromValue(Integer[] arre) {
        EstadoPago[] estadoVisitasTMP = new EstadoPago[arre.length];
        int count = 0;
        for (int b : arre) {
            estadoVisitasTMP[count] = getFromValue(b);
            count++;
        }
        return estadoVisitasTMP;
    }

    public static Map<Integer, String> getMapaEstados() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (EstadoPago estadoVisita : EstadoPago.values()) {
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
