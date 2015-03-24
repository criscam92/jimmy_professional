package jp.entidades;

import javax.ejb.EJB;
import javax.persistence.Id;
import jp.facades.IngresoFacade;

public class ProductoHelper {
    
    @EJB
    private final IngresoFacade ingresoFacade = new IngresoFacade();
    
    @Id
    private int id;
    private Producto producto;
    private int cantidadFacturada;
    private Long cantidadDisponible;

    public ProductoHelper(int id, Producto producto, int cantidadFacturada) {
        this.producto = producto;
        this.cantidadFacturada = cantidadFacturada;
        this.id = id;
        this.cantidadDisponible = ingresoFacade.getCountIngresoByProducto(producto);
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

    public Long getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Long cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }
}
