package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Ciudad;
import jp.entidades.Cliente;

@Stateless
public class ClienteFacade extends AbstractFacade<Cliente> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClienteFacade() {
        super(Cliente.class);
    }

    public Ciudad applyRecargo(Ciudad c) {
        Ciudad ciudadTMP = null;
        try {
//            Recargo recargo;
            Query query = em.createQuery("SELECT p.ciudad FROM Parametros p WHERE p.ciudad.id= :ciu");
            query.setParameter("ciu", c.getId());

            if (query.getSingleResult() != null) {
                ciudadTMP = (Ciudad) query.getSingleResult();
            }
            return ciudadTMP;

        } catch (NoResultException nre) {
            return new Ciudad();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ciudadTMP;
    }

    public List<Cliente> getClienteByQuery(String query) {
        try {
            Query q = getEntityManager().createQuery("SELECT c FROM Cliente c WHERE UPPER(CONCAT(c.documento,' - ',c.nombre)) LIKE UPPER(:param)");
            q.setParameter("param", "%" + query + "%");
            q.setFirstResult(0);
            q.setMaxResults(10);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
