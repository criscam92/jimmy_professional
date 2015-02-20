package jp.facades;

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

    public Recargo getRecargo() {
        try {
            Query query = getEntityManager().createQuery("SELECT r FROM Recargo r");
            query.setMaxResults(1);
            Recargo recargo = (Recargo) query.getSingleResult();
            return recargo;
        } catch (NoResultException e) {
        }
        return null;
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
