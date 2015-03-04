/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author arturo
 */
@Entity
@Table(catalog = "jimmy_professional", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Visita.findAll", query = "SELECT v FROM Visita v"),
    @NamedQuery(name = "Visita.findById", query = "SELECT v FROM Visita v WHERE v.id = :id"),
    @NamedQuery(name = "Visita.findByFecha", query = "SELECT v FROM Visita v WHERE v.fecha = :fecha"),
    @NamedQuery(name = "Visita.findByObservacionesCliente", query = "SELECT v FROM Visita v WHERE v.observacionesCliente = :observacionesCliente"),
    @NamedQuery(name = "Visita.findByCalificacionServicio", query = "SELECT v FROM Visita v WHERE v.calificacionServicio = :calificacionServicio"),
    @NamedQuery(name = "Visita.findByPuntualidadServicio", query = "SELECT v FROM Visita v WHERE v.puntualidadServicio = :puntualidadServicio"),
    @NamedQuery(name = "Visita.findByCumplioExpectativas", query = "SELECT v FROM Visita v WHERE v.cumplioExpectativas = :cumplioExpectativas"),
    @NamedQuery(name = "Visita.findByEstado", query = "SELECT v FROM Visita v WHERE v.estado = :estado")})
public class Visita implements Serializable {
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
    @Size(max = 200)
    @Column(name = "observaciones_cliente", length = 200)
    private String observacionesCliente;
    @Column(name = "calificacion_servicio")
    private Integer calificacionServicio;
    @Column(name = "puntualidad_servicio")
    private Integer puntualidadServicio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cumplio_expectativas", nullable = false)
    private boolean cumplioExpectativas;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int estado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "visita", fetch = FetchType.LAZY)
    private List<VisitaProducto> visitaProductoList;
    @JoinColumn(name = "cliente", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Cliente cliente;
    @JoinColumn(name = "empleado", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Empleado empleado;
    @JoinColumn(name = "procedimiento", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Procedimiento procedimiento;

    public Visita() {
    }

    public Visita(Long id) {
        this.id = id;
    }

    public Visita(Long id, Date fecha, boolean cumplioExpectativas, int estado) {
        this.id = id;
        this.fecha = fecha;
        this.cumplioExpectativas = cumplioExpectativas;
        this.estado = estado;
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

    public String getObservacionesCliente() {
        return observacionesCliente;
    }

    public void setObservacionesCliente(String observacionesCliente) {
        this.observacionesCliente = observacionesCliente;
    }

    public Integer getCalificacionServicio() {
        return calificacionServicio;
    }

    public void setCalificacionServicio(Integer calificacionServicio) {
        this.calificacionServicio = calificacionServicio;
    }

    public Integer getPuntualidadServicio() {
        return puntualidadServicio;
    }

    public void setPuntualidadServicio(Integer puntualidadServicio) {
        this.puntualidadServicio = puntualidadServicio;
    }

    public boolean getCumplioExpectativas() {
        return cumplioExpectativas;
    }

    public void setCumplioExpectativas(boolean cumplioExpectativas) {
        this.cumplioExpectativas = cumplioExpectativas;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @XmlTransient
    public List<VisitaProducto> getVisitaProductoList() {
        return visitaProductoList;
    }

    public void setVisitaProductoList(List<VisitaProducto> visitaProductoList) {
        this.visitaProductoList = visitaProductoList;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Procedimiento getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(Procedimiento procedimiento) {
        this.procedimiento = procedimiento;
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
        if (!(object instanceof Visita)) {
            return false;
        }
        Visita other = (Visita) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Visita[ id=" + id + " ]";
    }
    
}
