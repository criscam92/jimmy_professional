package jp.entidades;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "recibo_caja")
@NamedQueries({
    @NamedQuery(name = "ReciboCaja.findAll", query = "SELECT r FROM ReciboCaja r"),
    @NamedQuery(name = "ReciboCaja.findById", query = "SELECT r FROM ReciboCaja r WHERE r.id = :id"),
    @NamedQuery(name = "ReciboCaja.findByFecha", query = "SELECT r FROM ReciboCaja r WHERE r.fecha = :fecha"),
    @NamedQuery(name = "ReciboCaja.findByValor", query = "SELECT r FROM ReciboCaja r WHERE r.valor = :valor"),
    @NamedQuery(name = "ReciboCaja.findByDetalle", query = "SELECT r FROM ReciboCaja r WHERE r.detalle = :detalle"),
    @NamedQuery(name = "ReciboCaja.findByUsuario", query = "SELECT r FROM ReciboCaja r WHERE r.usuario = :usuario")})
public class ReciboCaja implements Serializable {
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
    private long valor;
    @Size(max = 100)
    @Column(length = 100)
    private String detalle;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int usuario;
    @JoinColumn(name = "caja", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CajaMenor caja;
    @JoinColumn(name = "concepto", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Concepto concepto;

    public ReciboCaja() {
    }

    public ReciboCaja(Long id) {
        this.id = id;
    }

    public ReciboCaja(Long id, Date fecha, long valor, int usuario) {
        this.id = id;
        this.fecha = fecha;
        this.valor = valor;
        this.usuario = usuario;
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

    public long getValor() {
        return valor;
    }

    public void setValor(long valor) {
        this.valor = valor;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
    }

    public CajaMenor getCaja() {
        return caja;
    }

    public void setCaja(CajaMenor caja) {
        this.caja = caja;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
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
        if (!(object instanceof ReciboCaja)) {
            return false;
        }
        ReciboCaja other = (ReciboCaja) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.entidades.ReciboCaja[ id=" + id + " ]";
    }
    
}