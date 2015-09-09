package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.ListaPrecio;

@Stateless
public class ListaPrecioFacade extends AbstractFacade<ListaPrecio> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ListaPrecioFacade() {
        super(ListaPrecio.class);
    }

    @Override
    public List<ListaPrecio> findAll() {
        return super.findAll(true);
    }

    @Override
    public List<ListaPrecio> findAll(boolean asc) {
        return super.findAll(asc);
    }

    public List<ListaPrecio> getListaPreciosByQuery(String query) {
        try {
            Query q = getEntityManager().createQuery("SELECT l FROM ListaPrecio l WHERE UPPER(CONCAT(l.codigo,' - ',l.nombre)) LIKE UPPER(:param)");
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
