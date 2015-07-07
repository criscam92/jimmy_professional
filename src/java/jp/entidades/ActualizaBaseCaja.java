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

@Entity
@Table(name = "actualiza_base_caja_menor")
@NamedQueries({
    @NamedQuery(name = "ActualizaBaseCajaMenor.findAll", query = "SELECT a FROM ActualizaBaseCajaMenor a"),
    @NamedQuery(name = "ActualizaBaseCajaMenor.findById", query = "SELECT a FROM ActualizaBaseCajaMenor a WHERE a.id = :id"),
    @NamedQuery(name = "ActualizaBaseCajaMenor.findByFecha", query = "SELECT a FROM ActualizaBaseCajaMenor a WHERE a.fecha = :fecha"),
    @NamedQuery(name = "ActualizaBaseCajaMenor.findByValorAnterior", query = "SELECT a FROM ActualizaBaseCajaMenor a WHERE a.valorAnterior = :valorAnterior"),
    @NamedQuery(name = "ActualizaBaseCajaMenor.findByValorNuevo", query = "SELECT a FROM ActualizaBaseCajaMenor a WHERE a.valorNuevo = :valorNuevo"),
    @NamedQuery(name = "ActualizaBaseCajaMenor.findByUsuario", query = "SELECT a FROM ActualizaBaseCajaMenor a WHERE a.usuario = :usuario")})
public class ActualizaBaseCaja implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_anterior", nullable = false)
    private long valorAnterior;
    @Basic(optional = false)
    @NotNull
    @Column(name = "valor_nuevo", nullable = false)
    private long valorNuevo;
    @JoinColumn(name = "usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;
    @JoinColumn(name = "caja", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Caja caja;

    public ActualizaBaseCaja() {
    }

    public ActualizaBaseCaja(Integer id) {
        this.id = id;
    }

    public ActualizaBaseCaja(Integer id, Date fecha, long valorAnterior, long valorNuevo, Usuario usuario) {
        this.id = id;
        this.fecha = fecha;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.usuario = usuario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public long getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(long valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public long getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(long valorNuevo) {
        this.valorNuevo = valorNuevo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Caja getCaja() {
        return caja;
    }

    public void setCaja(Caja caja) {
        this.caja = caja;
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
        if (!(object instanceof ActualizaBaseCaja)) {
            return false;
        }
        ActualizaBaseCaja other = (ActualizaBaseCaja) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jp.entidades.ActualizaBaseCajaMenor[ id=" + id + " ]";
    }
    
}
