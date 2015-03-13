package jp.util;

import java.util.HashMap;
import java.util.Map;

public enum TipoTalonario {

    REMISION(0, "Remisión"),
    RECIBO_CAJA(1, "Recibo de Caja"),
    RECIBO_DINERO_EFECTIVO(2, "Recibo de Dinero en Efectivo");

    private int valor;
    private String detalle;

    private TipoTalonario(int valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static TipoTalonario getFromValue(int value) {
        for (TipoTalonario tipo : TipoTalonario.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static TipoTalonario[] getFromValue(Integer[] arre) {
        TipoTalonario[] tiposTalonario = new TipoTalonario[arre.length];
        int count = 0;
        for (int b : arre) {
            tiposTalonario[count] = getFromValue(b);
            count++;
        }
        System.out.println("Size-> "+tiposTalonario.length);
        return tiposTalonario;
    }

    public static Map<Integer, String> getMapaEstados() {
        Map<Integer, String> mapTMP = new HashMap<>();
        for (TipoTalonario tipoTalonario : TipoTalonario.values()) {
            mapTMP.put(tipoTalonario.getValor(), tipoTalonario.getDetalle());

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
