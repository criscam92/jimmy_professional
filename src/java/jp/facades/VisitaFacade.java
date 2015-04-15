package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Visita;

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

    /*public List<Visita> visitasByEmpleado(Emple) {
        try {
            Query q = getEntityManager().createQuery("SELECT v FROM Visita v WHERE v.empleado.id= :emp");
            q.setParameter("emp", q)
        } catch (NoResultException nre) {
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
