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
    
    public List<Promocion> getPromocionByQuery(String query) {
        try {
            List<Promocion> promociones = new ArrayList<>();
            Query queryPromocion = getEntityManager().createQuery("SELECT p FROM Promocion p WHERE UPPER(CONCAT(p.codigo,' - ',p.descripcion)) LIKE UPPER(:param)");
            queryPromocion.setParameter("param", "%" + query + "%");
            promociones = queryPromocion.getResultList();
            
            queryPromocion.setFirstResult(0);
            queryPromocion.setMaxResults(10);
            
            return promociones;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
