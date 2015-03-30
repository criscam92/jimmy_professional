package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Ciudad;
import jp.entidades.Parametros;

@Stateless
public class ParametrosFacade extends AbstractFacade<Parametros> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametrosFacade() {
        super(Parametros.class);
    }

    public Parametros getParametros() {
        try {
            Query query = getEntityManager().createQuery("SELECT p FROM Parametros p");
            query.setMaxResults(1);
            Parametros parametros = (Parametros) query.getSingleResult();
            return parametros;
        } catch (NoResultException e) {
        }
        return null;
    }

    public Float getParametrosByCiudad(Ciudad c) {
        try {
            Query query = getEntityManager().createQuery("SELECT p FROM Parametros p");
            query.setMaxResults(1);
            Parametros p = (Parametros) query.getSingleResult();

            if ((p.getCiudad().getPais().equals(c.getPais())) && (p.getCiudad().equals(c))) {
                return p.getRecargoLocal();
            } else if ((p.getCiudad().getPais().equals(c.getPais())) && !(p.getCiudad().equals(c))) {
                return p.getRecargoNacional();
            } else {
                return p.getRecargoInternacional();
            }
        } catch (NoResultException nre) {

        }
        return 0f;
    }
}
