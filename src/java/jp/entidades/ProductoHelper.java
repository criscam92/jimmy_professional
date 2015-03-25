package jp.entidades;

import javax.persistence.Id;

public class ProductoHelper {

    @Id
    private int id;
    private Producto producto;
    private int cantidadFacturada;
    private int cantidadDisponible;
    private int cantidadDespachada;

    public ProductoHelper(int id, Producto producto, int cantidadFacturada, Factura factura, boolean despachados) {
        this.producto = producto;
        this.cantidadFacturada = cantidadFacturada;
        this.id = id;
        this.cantidadDisponible = countIngresosByProducto(producto);
        this.cantidadDespachada = despachados ? getDespachosByProducto(producto, factura) : 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidadFacturada() {
        return cantidadFacturada;
    }

    public void setCantidadFacturada(int cantidadFacturada) {
        this.cantidadFacturada = cantidadFacturada;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public int getCantidadDespachada() {
        return cantidadDespachada;
    }

    public void setCantidadDespachada(int cantidadDespachada) {
        this.cantidadDespachada = cantidadDespachada;
    }

    private int countIngresosByProducto(Producto producto) {
        int count = 0;
        for (IngresoProducto ip : producto.getIngresoProductoList()) {
            count += ip.getCantidad();
        }
        return count;
    }

    private int getDespachosByProducto(Producto producto, Factura factura) {
        int cantDespachada = 0;
        for (DespachoFactura df : factura.getDespachoFacturaList()) {
            for (DespachoFacturaProducto dfp : df.getDespachoFacturaProductoList()) {
                if (dfp.getProducto().getId().equals(producto.getId())) {
                    cantDespachada += dfp.getCantidad();
                }
            }
        }
        return cantDespachada;
    }

}
