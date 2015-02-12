package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Producto;
import jp.entidades.Promocion;

@Stateless
public class PromocionFacade extends AbstractFacade<Promocion> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PromocionFacade() {
        super(Promocion.class);
    }

    public List<Producto> getProductosByQuery(String query) {
        System.out.println("Hola");
        try {
            Query q = getEntityManager().createQuery("SELECT p FROM Producto p WHERE UPPER(CONCAT(p.codigo,' - ',p.nombre)) LIKE UPPER(:param)");
            q.setParameter("param", q + "%");
//            q.setFirstResult(0);
//            q.setMaxResults(10);
            for (Producto producto : (List<Producto>) q.getResultList()) {
                System.out.println("===================");
                System.out.println("Nombre:" + producto.getCodigo() + " - " + producto.getNombre() + " " + producto.getGramaje() + " ml.");
                System.out.println("===================");
            }
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
