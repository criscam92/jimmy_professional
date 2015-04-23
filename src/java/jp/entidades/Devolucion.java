package jp.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Devolucion.findAll", query = "SELECT d FROM Devolucion d"),
    @NamedQuery(name = "Devolucion.findById", query = "SELECT d FROM Devolucion d WHERE d.id = :id"),
    @NamedQuery(name = "Devolucion.findByDolar", query = "SELECT d FROM Devolucion d WHERE d.dolar = :dolar"),
    @NamedQuery(name = "Devolucion.findByValorTotal", query = "SELECT d FROM Devolucion d WHERE d.valorTotal = :valorTotal"),
    @NamedQuery(name = "Devolucion.findByObservaciones", query = "SELECT d FROM Devolucion d WHERE d.observaciones = :observaciones")})
public class Devolucion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private Boolean dolar;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_total", nullable = false)
    private Float valorTotal;
    @Size(max = 300)
    @Column(length = 300)
    private String observaciones;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean realizado;
    @JoinColumn(name = "cliente", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cliente cliente;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = true)
    @Column(name = "dolar_actual", nullable = true)
    private float dolarActual;
    @JoinColumn(name = "usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "devolucion", fetch = FetchType.LAZY)
    private List<CambioDevolucion> cambioDevolucionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "devolucion", fetch = FetchType.LAZY)
    private List<DevolucionProducto> devolucionProductoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "devolucion", fetch = FetchType.LAZY)
    private List<PagoDevolucion> pagoDevolucionList;

    public Devolucion() {
    }

    public Devolucion(Long id) {
        this.id = id;
    }

    public Devolucion(Long id, Boolean dolar, Float valorTotal, boolean realizado) {
        this.id = id;
        this.dolar = dolar;
        this.valorTotal = valorTotal;
        this.realizado = realizado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDolar() {
        return dolar;
    }

    public void setDolar(Boolean dolar) {
        this.dolar = dolar;
    }

    public Float getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Float valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isRealizado() {
        return realizado;
    }

    public void setRealizado(boolean realizado) {
        this.realizado = realizado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public float getDolarActual() {
        return dolarActual;
    }

    public void setDolarActual(float dolarActual) {
        this.dolarActual = dolarActual;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @XmlTransient
    public List<CambioDevolucion> getCambioDevolucionList() {
        return cambioDevolucionList;
    }

    public void setCambioDevolucionList(List<CambioDevolucion> cambioDevolucionList) {
        this.cambioDevolucionList = cambioDevolucionList;
    }

    @XmlTransient
    public List<DevolucionProducto> getDevolucionProductoList() {
        return devolucionProductoList;
    }

    public void setDevolucionProductoList(List<DevolucionProducto> devolucionProductoList) {
        this.devolucionProductoList = devolucionProductoList;
    }

    @XmlTransient
    public List<PagoDevolucion> getPagoDevolucionList() {
        return pagoDevolucionList;
    }

    public void setPagoDevolucionList(List<PagoDevolucion> pagoDevolucionList) {
        this.pagoDevolucionList = pagoDevolucionList;
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
        if (!(object instanceof Devolucion)) {
            return false;
        }
        Devolucion other = (Devolucion) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entidades.Devolucion[ id=" + id + " ]";
    }

}
