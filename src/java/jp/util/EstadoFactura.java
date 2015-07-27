package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum EstadoFactura {

    REALIZADA(0, "Realizado"),
    ANULADO(1, "Anulado"),
    PENDIENTE(2, "Pendiente"),
    CANCELADO(3, "Cancelado");

    private int valor;
    private String detalle;

    private EstadoFactura(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static EstadoFactura getFromValue(int value) {
        for (EstadoFactura tipo : EstadoFactura.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static EstadoFactura[] getFromValue(Integer[] arre) {
        EstadoFactura[] estadoVisitasTMP = new EstadoFactura[arre.length];
        int count = 0;
        for (int b : arre) {
            estadoVisitasTMP[count] = getFromValue(b);
            count++;
        }
        return estadoVisitasTMP;
    }

    public static Map<Integer, String> getMapaEstados() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (EstadoFactura estadoVisita : EstadoFactura.values()) {
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