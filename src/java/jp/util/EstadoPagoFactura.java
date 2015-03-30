package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum EstadoPagoFactura {

    REALIZADA(0, "Realizado"),
    ANULADO(1, "Anulado"),
    PENDIENTE(2, "Pendiente"),
    CANCELADO(3, "Cancelado");

    private int valor;
    private String detalle;

    private EstadoPagoFactura(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static EstadoPagoFactura getFromValue(int value) {
        for (EstadoPagoFactura tipo : EstadoPagoFactura.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static EstadoPagoFactura[] getFromValue(Integer[] arre) {
        EstadoPagoFactura[] estadoVisitasTMP = new EstadoPagoFactura[arre.length];
        int count = 0;
        for (int b : arre) {
            estadoVisitasTMP[count] = getFromValue(b);
            count++;
        }
        return estadoVisitasTMP;
    }

    public static Map<Integer, String> getMapaEstados() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (EstadoPagoFactura estadoVisita : EstadoPagoFactura.values()) {
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
