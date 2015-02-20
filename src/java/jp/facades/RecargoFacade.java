package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Ciudad;
import jp.entidades.Recargo;

@Stateless
public class RecargoFacade extends AbstractFacade<Recargo> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RecargoFacade() {
        super(Recargo.class);
    }

    public boolean isEntityRecargoEmpty() {
        List<Recargo> recargos = new ArrayList<>();
        boolean vacio;
        try {
            Query query = em.createQuery("SELECT r FROM Recargo r");
            recargos = query.getResultList();
            if (!recargos.isEmpty()) {
                vacio = false;
            } else {
                vacio = true;
            }
        } catch (Exception e) {
            vacio = false;
            e.printStackTrace();
        }
        return vacio;
    }

    public Recargo getFirstRecargo() {
        Recargo recargo = new Recargo();
        try {
            Query query = em.createQuery("SELECT r FROM Recargo r");
            query.setMaxResults(1);
            recargo = (Recargo) query.getSingleResult();
            if (recargo != null) {
                return recargo;
            } else {
                recargo = null;
            }
        } catch (Exception e) {
            recargo = null;
            e.printStackTrace();
        }
        return recargo;
    }

    public Float getRecargoByCiudad(Ciudad c) {
        try {
            Query query = getEntityManager().createQuery("SELECT r FROM Recargo r");
            query.setMaxResults(1);
            Recargo r = (Recargo) query.getSingleResult();

            if ((r.getCiudad().getPais().equals(c.getPais())) && (r.getCiudad().equals(c))) {
                return r.getRecargoLocal();
            } else if ((r.getCiudad().getPais().equals(c.getPais())) && !(r.getCiudad().equals(c))) {
                return r.getRecargoNacional();
            } else {
                return r.getRecargoInternacional();
            }
        } catch (NoResultException nre) {

        }
        return 0f;
    }
}
