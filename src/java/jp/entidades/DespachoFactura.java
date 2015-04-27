package jp.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "despacho_factura", catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DespachoFactura.findAll", query = "SELECT d FROM DespachoFactura d"),
    @NamedQuery(name = "DespachoFactura.findById", query = "SELECT d FROM DespachoFactura d WHERE d.id = :id"),
    @NamedQuery(name = "DespachoFactura.findByFecha", query = "SELECT d FROM DespachoFactura d WHERE d.fecha = :fecha"),
    @NamedQuery(name = "DespachoFactura.findByRealizado", query = "SELECT d FROM DespachoFactura d WHERE d.realizado = :realizado")})
public class DespachoFactura implements Serializable {

    public static final boolean REALIZADA = true;
    public static final boolean CANCELADA = false;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean realizado;
    @JoinColumn(name = "despacho", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Despacho despacho;
    @JoinColumn(name = "factura", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Factura factura;
    @JoinColumn(name = "usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;
    @OneToMany(mappedBy = "despachoFactura", fetch = FetchType.LAZY)
    private List<DespachoFacturaProducto> despachoFacturaProductoList;

    public DespachoFactura() {
    }

    public DespachoFactura(Long id) {
        this.id = id;
    }

    public DespachoFactura(Long id, Date fecha, boolean realizado) {
        this.id = id;
        this.fecha = fecha;
        this.realizado = realizado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean getRealizado() {
        return realizado;
    }

    public void setRealizado(boolean realizado) {
        this.realizado = realizado;
    }

    public Despacho getDespacho() {
        return despacho;
    }

    public void setDespacho(Despacho despacho) {
        this.despacho = despacho;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @XmlTransient
    public List<DespachoFacturaProducto> getDespachoFacturaProductoList() {
        return despachoFacturaProductoList;
    }

    public void setDespachoFacturaProductoList(List<DespachoFacturaProducto> despachoFacturaProductoList) {
        this.despachoFacturaProductoList = despachoFacturaProductoList;
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
        if (!(object instanceof DespachoFactura)) {
            return false;
        }
        DespachoFactura other = (DespachoFactura) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entidades.DespachoFactura[ id=" + id + " ]";
    }

}
