package jp.facades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Empleado;
import jp.entidades.Visita;
import jp.entidades.VisitaProducto;
import jp.util.EstadoVisita;

@Stateless
public class VisitaFacade extends AbstractFacade<Visita> {
    
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public VisitaFacade() {
        super(Visita.class);
    }
    
    public Visita getVisitaById(Long idVisita) {
        try {
            return getEntityManager().find(Visita.class, idVisita);
        } catch (NoResultException e) {
        }
        return null;
    }
    
    public List<Visita> findVisitasByEmpleado(Empleado e, boolean isAdmin) {
        List<Visita> visitasTMP = new ArrayList<>();
        try {
            String query = "SELECT v FROM Visita v";
            if (!isAdmin) {
                query += " WHERE v.empleado.id= :emp";
            }
            query += " ORDER BY v.id ASC";
            Query q = getEntityManager().createQuery(query);
            if (!isAdmin) {
                q.setParameter("emp", e.getId());
            }
            visitasTMP = q.getResultList();
            return visitasTMP;
        } catch (NoResultException nre) {
            return null;
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return visitasTMP;
    }
    
    public List<VisitaProducto> getProductosByVisita(Visita visita) {
        try {
            Query query = getEntityManager().createQuery("SELECT vp FROM VisitaProducto vp WHERE vp.visita.id = :visi");
            query.setParameter("visi", visita.getId());
            return query.getResultList();
        } catch (NoResultException e) {
        }
        return null;
    }
    
    
    public List<Visita> getVisitasSiguienteSemana(Date fecha1, Date fecha2){
        List<Visita> visitas;
        try {            
            Query q = getEntityManager().createQuery("SELECT v FROM Visita v WHERE v.estado = :est AND v.fecha BETWEEN :fecha1 AND :fecha2 ORDER BY v.fecha ASC");
            q.setParameter("est", EstadoVisita.PENDIENTE.getValor());
            q.setParameter("fecha1", fecha1);
            q.setParameter("fecha2", fecha2);
            visitas = q.getResultList();
        } catch (Exception e) {
            visitas = new ArrayList<>();
        }
        return visitas;
    }
}
