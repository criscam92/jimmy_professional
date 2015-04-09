package jp.entidades;

import java.io.Serializable;
import javax.persistence.Id;

public class ProductoHelper implements Serializable {

    @Id
    private int id;
    private Producto producto;
    private int cantidadFacturada;
    private int cantidadDisponible;
    private int cantidadDespachada;
    private int cantidadADespachar;

    public ProductoHelper() {
    }

    public ProductoHelper(int id, Producto producto, int cantidadFacturada) {
        this.id = id;
        this.producto = producto;
        this.cantidadFacturada = cantidadFacturada;
        obtenerCantidadADespachar();
    }

    public void obtenerCantidadADespachar() {
        int cantMax = getCantidadFacturada() - getCantidadDespachada();
        this.cantidadADespachar = cantMax > getCantidadDisponible() ? getCantidadDisponible() : cantMax;
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

    public int getCantidadADespachar() {
        return cantidadADespachar;
    }

    public void setCantidadADespachar(int cantidadADespachar) {
        this.cantidadADespachar = cantidadADespachar;
    }
}
