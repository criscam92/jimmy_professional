package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Factura;
import jp.entidades.Producto;
import jp.entidades.Promocion;

@Stateless
public class FacturaFacade extends AbstractFacade<Factura> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FacturaFacade() {
        super(Factura.class);
    }
    
    public List<Object> getProductosPromocionByQuery(String query) {
        try {
            List<Producto> productos = new ArrayList<>();
            Query q = getEntityManager().createQuery("SELECT p FROM Producto p WHERE UPPER(CONCAT(p.codigo,' - ',p.nombre)) LIKE UPPER(:param)");
            q.setParameter("param", "%" + query + "%");
            productos = q.getResultList();
            
            List<Promocion> promociones = new ArrayList<>();
            Query queryPromocion = getEntityManager().createQuery("SELECT p FROM Promocion p WHERE UPPER(CONCAT(p.codigo,' - ',p.descripcion)) LIKE UPPER(:param)");
            queryPromocion.setParameter("param", "%" + query + "%");
            promociones = queryPromocion.getResultList();
            
            List<Object> objects = new ArrayList<>();
            objects.addAll(productos);
            objects.addAll(promociones);
            
            q.setFirstResult(0);
            q.setMaxResults(10);
            
            queryPromocion.setFirstResult(0);
            queryPromocion.setMaxResults(10);
            
            return objects;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
