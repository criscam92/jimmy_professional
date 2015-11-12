package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Cliente;
import jp.entidades.Tercero;
import jp.util.TipoDocumento;

@Stateless
public class TerceroFacade extends AbstractFacade<Tercero> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TerceroFacade() {
        super(Tercero.class);
    }

    public List<Tercero> getTerceroByQuery(String query) {
        try {
            Query q = getEntityManager().createQuery("SELECT t FROM Tercero t WHERE UPPER(CONCAT(t.numdocumento,' - ',t.nombre)) LIKE UPPER(:param)");
            q.setParameter("param", "%" + query + "%");
            q.setFirstResult(0);
            q.setMaxResults(10);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existeDocumento(Tercero tercero) {
        try {
            String jpql = "SELECT COUNT(t.id) FROM Tercero t WHERE t.numdocumento = :documento";
            if (tercero.getId() != null) {
                jpql += " AND e.id != :id";
            }
            Query query = getEntityManager().createQuery(jpql);
            query.setParameter("documento", tercero.getNumdocumento());
            if (tercero.getId() != null) {
                query.setParameter("id", tercero.getId());
            }
            Long result = (Long) query.getSingleResult();
            if (result > 0l) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Tercero existeDocumento(String documento) {
        Tercero t = null;
        try {
            String jpql = "SELECT t ROM Tercero t WHERE t.numdocumento = :documento";
            Query query = getEntityManager().createQuery(jpql);
            query.setParameter("documento", documento);
            t = (Tercero) query.getSingleResult();
        } catch (Exception e) {
            t = null;
            e.printStackTrace();
        }
        return null;
    }

    public Tercero createTerceroByCliente(Cliente cliente) {
        try {
            Tercero t = new Tercero();
            t.setNombre(cliente.getNombre());
            t.setNumdocumento(cliente.getDocumento());
            t.setTipodocumento(TipoDocumento.CEDULA.getValor());
            getEntityManager().persist(t);
            
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
