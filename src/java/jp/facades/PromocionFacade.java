package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Factura;
import jp.entidades.FacturaPromocion;
import jp.entidades.Producto;
import jp.entidades.Promocion;
import jp.entidades.PromocionProducto;

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
        try {
            Query q = getEntityManager().createQuery("SELECT p FROM Producto p WHERE UPPER(CONCAT(p.codigo,' - ',p.nombre)) LIKE UPPER(:param)");
            q.setParameter("param", "%" + query + "%");
            q.setFirstResult(0);
            q.setMaxResults(10);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<PromocionProducto> getProductosByPromocion(Promocion promocion) {
        try {
            Query query = getEntityManager().createQuery("SELECT pp FROM PromocionProducto pp WHERE pp.promocion.id = :promo");
            query.setParameter("promo", promocion.getId());
            return query.getResultList();
        } catch (NoResultException e) {
        }
        return null;
    }

    public List<FacturaPromocion> getFacturaPromocionByFactura(Factura factura) {
        try {
            Query q = getEntityManager().createQuery("SELECT fp FROM FacturaPromocion fp WHERE fp.factura.id = :factura");
            q.setParameter("factura", factura.getId());
            return q.getResultList();
        } catch (NoResultException e) {
        }
        return null;
    }

}