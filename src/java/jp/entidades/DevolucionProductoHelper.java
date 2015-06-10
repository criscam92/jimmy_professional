package jp.entidades;

import javax.persistence.Id;

public class DevolucionProductoHelper {

    @Id
    private Long id;
    private DevolucionProducto devolucionProducto;
    private boolean ingresar;

    public DevolucionProductoHelper() {
    }

    public DevolucionProductoHelper(Long id, DevolucionProducto devolucionProducto, boolean ingresar) {
        this.id = id;
        this.devolucionProducto = devolucionProducto;
        this.ingresar = ingresar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DevolucionProducto getDevolucionProducto() {
        return devolucionProducto;
    }

    public void setDevolucionProducto(DevolucionProducto devolucionProducto) {
        this.devolucionProducto = devolucionProducto;
    }

    public boolean isIngresar() {
        return ingresar;
    }

    public void setIngresar(boolean ingresar) {
        this.ingresar = ingresar;
    }
}
