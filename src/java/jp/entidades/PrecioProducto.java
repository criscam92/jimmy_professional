package jp.entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "precio_producto")
@NamedQueries({
    @NamedQuery(name = "PrecioProducto.findAll", query = "SELECT p FROM PrecioProducto p")})
public class PrecioProducto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = true)
    @Column(nullable = true)
    private Double precio;
    @Basic(optional = true)
    @Column(name = "precio_usd", nullable = true)
    private Double precioUSD;
    @JoinColumn(name = "lista_precio", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ListaPrecio listaPrecio;
    @JoinColumn(name = "producto", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto producto;
    @Transient
    private Boolean existe;

    public PrecioProducto() {
    }

    public PrecioProducto(Integer id) {
        this.id = id;
    }

    public PrecioProducto(Integer id, Double precio, Double precioUSD, Producto producto) {
        this.id = id;
        this.precio = precio;
        this.precioUSD = precioUSD;
        this.producto = producto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getPrecioUSD() {
        return precioUSD;
    }

    public void setPrecioUSD(Double precioUSD) {
        this.precioUSD = precioUSD;
    }

    public ListaPrecio getListaPrecio() {
        return listaPrecio;
    }

    public void setListaPrecio(ListaPrecio listaPrecio) {
        this.listaPrecio = listaPrecio;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Boolean getExiste() {
        return existe;
    }
    
    public void setExiste(Boolean existe) {
        this.existe = existe;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrecioProducto)) {
            return false;
        }
        PrecioProducto other = (PrecioProducto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getProducto().getNombre()+"("+this.getPrecio()+")";
    }
    
}
