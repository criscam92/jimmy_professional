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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "devolucion_producto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DevolucionProducto.findAll", query = "SELECT d FROM DevolucionProducto d"),
    @NamedQuery(name = "DevolucionProducto.findById", query = "SELECT d FROM DevolucionProducto d WHERE d.id = :id"),
    @NamedQuery(name = "DevolucionProducto.findByCantidad", query = "SELECT d FROM DevolucionProducto d WHERE d.cantidad = :cantidad"),
    @NamedQuery(name = "DevolucionProducto.findByDetalle", query = "SELECT d FROM DevolucionProducto d WHERE d.detalle = :detalle")})
public class DevolucionProducto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int cantidad;
    @Size(max = 300)
    @Column(length = 300)
    private String detalle;
    @JoinColumn(name = "codigo_devolucion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CodigoDevolucion codigoDevolucion;
    @JoinColumn(name = "devolucion", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Devolucion devolucion;
    @JoinColumn(name = "producto", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Producto producto;

    public DevolucionProducto() {
    }

    public DevolucionProducto(Long id) {
        this.id = id;
    }

    public DevolucionProducto(Long id, int cantidad) {
        this.id = id;
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public CodigoDevolucion getCodigoDevolucion() {
        return codigoDevolucion;
    }

    public void setCodigoDevolucion(CodigoDevolucion codigoDevolucion) {
        this.codigoDevolucion = codigoDevolucion;
    }

    public Devolucion getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(Devolucion devolucion) {
        this.devolucion = devolucion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
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
        if (!(object instanceof DevolucionProducto)) {
            return false;
        }
        DevolucionProducto other = (DevolucionProducto) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entidades.DevolucionProducto[ id=" + id + " ]";
    }

}
