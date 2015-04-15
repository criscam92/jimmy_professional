package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Empleado;
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

    public List<Visita> findVisitasByEmpleado(Empleado e, boolean isAdmin) {
        List<Visita> visitasTMP = new ArrayList<>();
        try {
            String query = "SELECT v FROM Visita v";
            if (!isAdmin) {
                query += " WHERE v.empleado.id= :emp";
            }
            System.out.println("Query--> "+query+"\nAdmin? "+isAdmin);
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

}
