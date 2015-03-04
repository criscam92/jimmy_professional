package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import jp.entidades.Factura;
import jp.entidades.Factura_;
import jp.entidades.Pago;
import jp.entidades.Pago_;
import jp.entidades.Promocion;
import jp.util.EstadoPago;

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
    
    public Factura findFacturaByOrdenPedido(String ordenPedido){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Factura> fac = cq.from(Factura.class);
        cq.select(fac);
        cq.where(cb.equal(fac.get(Factura_.orden_pedido), ordenPedido));
        TypedQuery<Factura> q = getEntityManager().createQuery(cq);
        q.setFirstResult(1);
        return q.getSingleResult();
    }
    
    public double getValorPendientePagoFactura(Factura factura){
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();
            Root<Pago> fac = cq.from(Pago.class);
            cq.select(cb.sum(fac.get(Pago_.valorTotal)));
            cq.where(cb.and(cb.equal(fac.get(Pago_.factura), factura),
                    cb.equal(fac.get(Pago_.estado), EstadoPago.REALIZADA.getValor())));
            TypedQuery<Double> q = getEntityManager().createQuery(cq);
            Double valorAbono = q.getSingleResult();

            if(valorAbono==null){
                valorAbono = 0.0d;
            }
            
            System.out.println("Valor Factura: ".concat(factura.getTotalPagar()+""));
            System.out.println("Valor Abono: ".concat(valorAbono+""));
            return factura.getTotalPagar()-valorAbono;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        
    }
    
    public List<Promocion> getPromocionByQuery(String query) {
        try {
            List<Promocion> promociones;
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
