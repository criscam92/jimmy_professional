package jp.entidades.auxiliar;

import javax.ejb.EJB;
import jp.entidades.Producto;
import jp.facades.ProductoFacade;

public class ProductoAux {
    
    private int id;
    @EJB
    private ProductoFacade ejbFacade;
    private Producto producto;
    private int cantidad;
    private int cantDisponible;

    public ProductoAux(int id, Producto producto, int cantidad) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.cantDisponible = ejbFacade.getCantidadByProducto(producto);
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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantDisponible() {
        return cantDisponible;
    }

    public void setCantDisponible(int cantDisponible) {
        this.cantDisponible = cantDisponible;
    }

}
