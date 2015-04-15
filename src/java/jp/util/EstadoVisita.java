package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum EstadoVisita {

    PENDIENTE(0, "Pendiente"),
    REALIZADA(1, "Realizada"),
    CANCELADA(2, "Cancelada"),
    ANULADA(3, "Anulada");//Si se hizo pero quedo mala

    private int valor;
    private String detalle;

    private EstadoVisita(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static EstadoVisita getFromValue(int value) {
        for (EstadoVisita tipo : EstadoVisita.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static EstadoVisita[] getFromValue(Integer[] arre) {
        EstadoVisita[] estadoVisitasTMP = new EstadoVisita[arre.length];
        int count = 0;
        for (int b : arre) {
            estadoVisitasTMP[count] = getFromValue(b);
            count++;
        }
        return estadoVisitasTMP;
    }

    public static Map<Integer, String> getMapaEstados() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (EstadoVisita estadoVisita : EstadoVisita.values()) {
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
