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
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Talonario.findAll", query = "SELECT t FROM Talonario t"),
    @NamedQuery(name = "Talonario.findById", query = "SELECT t FROM Talonario t WHERE t.id = :id"),
    @NamedQuery(name = "Talonario.findByTipo", query = "SELECT t FROM Talonario t WHERE t.tipo = :tipo"),
    @NamedQuery(name = "Talonario.findByInicial", query = "SELECT t FROM Talonario t WHERE t.inicial = :inicial"),
    @NamedQuery(name = "Talonario.findByFinal1", query = "SELECT t FROM Talonario t WHERE t.final1 = :final1"),
    @NamedQuery(name = "Talonario.findByActual", query = "SELECT t FROM Talonario t WHERE t.actual = :actual"),
    @NamedQuery(name = "Talonario.findByFecha", query = "SELECT t FROM Talonario t WHERE t.fecha = :fecha")})
public class Talonario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int tipo;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private long inicial;
    @Basic(optional = false)
    @NotNull
    @Column(name = "final", nullable = false)
    private long final1;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private long actual;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "empleado", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Empleado empleado;

    public Talonario() {
    }

    public Talonario(Integer id) {
        this.id = id;
    }

    public Talonario(Integer id, int tipo, long inicial, long final1, long actual, Date fecha) {
        this.id = id;
        this.tipo = tipo;
        this.inicial = inicial;
        this.final1 = final1;
        this.actual = actual;
        this.fecha = fecha;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public long getInicial() {
        return inicial;
    }

    public void setInicial(long inicial) {
        this.inicial = inicial;
    }

    public long getFinal1() {
        return final1;
    }

    public void setFinal1(long final1) {
        this.final1 = final1;
    }

    public long getActual() {
        return actual;
    }

    public void setActual(long actual) {
        this.actual = actual;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
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
        if (!(object instanceof Talonario)) {
            return false;
        }
        Talonario other = (Talonario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Talonario[ id=" + id + " ]";
    }
    
}
