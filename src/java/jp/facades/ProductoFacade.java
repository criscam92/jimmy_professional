package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.Producto;

@Stateless
public class ProductoFacade extends AbstractFacade<Producto> {

    public enum TIPO_PRECIO{
        LOCALES,
        NACIONALES,
        EXTRANJEROS;
    }
    
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductoFacade() {
        super(Producto.class);
    }

    public List<FacturaProducto> getFacturaProductosByFactura(Factura factura) {
        try {
            Query q = getEntityManager().createQuery("SELECT fp FROM FacturaProducto fp WHERE fp.factura.id = :factura");
            q.setParameter("factura", factura.getId());
            return q.getResultList();
        } catch (NoResultException e) {
        }
        return null;
    }

    public int getCantidadByProducto(Producto producto) {
        try {
            Query q = getEntityManager().createQuery("SELECT COUNT(p) FROM Producto p WHERE p.id = :id");
            q.setParameter("id", producto.getId());
            return (int) q.getSingleResult();
        } catch (NoResultException e) {
        }
        return 0;
    }
    
}
