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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "promocion_producto", catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PromocionProducto.findAll", query = "SELECT p FROM PromocionProducto p"),
    @NamedQuery(name = "PromocionProducto.findById", query = "SELECT p FROM PromocionProducto p WHERE p.id = :id"),
    @NamedQuery(name = "PromocionProducto.findByCantidad", query = "SELECT p FROM PromocionProducto p WHERE p.cantidad = :cantidad")})
public class PromocionProducto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @JoinColumn(name = "producto", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto producto;
    @JoinColumn(name = "promocion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Promocion promocion;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int cantidad;

    public PromocionProducto() {        
    }

    public PromocionProducto(Long id) {
        this.id = id;
    }

    public PromocionProducto(Long id, int cantidad) {
        this.id = id;
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
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
        if (!(object instanceof PromocionProducto)) {
            return false;
        }
        PromocionProducto other = (PromocionProducto) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "jp.entidades.PromocionProducto[ id=" + id + " ]";
    }
    
}
