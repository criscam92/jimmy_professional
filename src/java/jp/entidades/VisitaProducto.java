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
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "visita_producto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VisitaProducto.findAll", query = "SELECT v FROM VisitaProducto v"),
    @NamedQuery(name = "VisitaProducto.findById", query = "SELECT v FROM VisitaProducto v WHERE v.id = :id"),
    @NamedQuery(name = "VisitaProducto.findByCantidad", query = "SELECT v FROM VisitaProducto v WHERE v.cantidad = :cantidad")})
public class VisitaProducto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    private Integer cantidad;
    @JoinColumn(name = "producto", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto producto;
    @JoinColumn(name = "visita", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Visita visita;

    public VisitaProducto() {
    }

    public VisitaProducto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Visita getVisita() {
        return visita;
    }

    public void setVisita(Visita visita) {
        this.visita = visita;
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
        if (!(object instanceof VisitaProducto)) {
            return false;
        }
        VisitaProducto other = (VisitaProducto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.VisitaProducto[ id=" + id + " ]";
    }
    
}
