package jp.util;

public enum TipoList {

    CONTADO(0, "Precio base"),
    CREDITO(1, "Lista precios");
    
    private Integer valor;
    private String detalle;

    private TipoList(Integer valor, String detalle) {
        this.valor = valor;
        this.detalle = detalle;
    }

    public static TipoList getFromValue(int value) {
        for (TipoList tipo : TipoList.values()) {
            if (tipo.getValor() == value) {
                return tipo;
            }
        }
        return null;
    }

    public static TipoList[] getFromValue(Integer[] arre) {
        TipoList[] tipoListTMP = new TipoList[arre.length];
        int count = 0;
        for (int b : arre) {
            tipoListTMP[count] = getFromValue(b);
            count++;
        }
        return tipoListTMP;
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
