package jp.facades;

import java.util.List;
import jp.entidades.ReciboCaja;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Concepto;
import jp.entidades.Tercero;
import jp.util.EstadoFactura;
import jp.util.TipoConcepto;

@Stateless
public class NominaFacade extends AbstractFacade<ReciboCaja> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NominaFacade() {
        super(ReciboCaja.class);
    }

    public List<ReciboCaja> getRecibosCajaCxcTercero(Tercero tercero) {
        List<ReciboCaja> recibosCaja = null;
        try {
            Query query = getEntityManager().createQuery("SELECT rc FROM ReciboCaja rc WHERE rc.tercero.id = :tercero AND rc.estado = :estado");
            query.setParameter("tercero", tercero.getId());
            query.setParameter("estado", EstadoFactura.PENDIENTE.getValor());
            recibosCaja = query.getResultList();
        } catch (Exception e) {
            recibosCaja = null;
            e.printStackTrace();
        }
        return recibosCaja;
    }

    public List<Concepto> getConceptosCXCOnlyIngresos(String query) {
        try {
            Query q = getEntityManager().createQuery("SELECT c FROM Concepto c WHERE UPPER(CONCAT(c.codigo,' - ',c.detalle)) LIKE UPPER(:param) AND c.tipo = :tipo");
            q.setParameter("param", "%" + query + "%");
            q.setParameter("tipo", TipoConcepto.INGRESO.getValor());
            q.setFirstResult(0);
            q.setMaxResults(10);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ReciboCaja> recibosCajaCxcByTransaccion(ReciboCaja reciboCajaTMP) {
        List<ReciboCaja> recibosCaja = null;
        try {
            Query query = getEntityManager().createQuery("SELECT rc FROM ReciboCaja rc WHERE rc.transaccion.id = :transaccion AND rc.estado = :estado");
            query.setParameter("transaccion", reciboCajaTMP.getId());
            query.setParameter("estado", EstadoFactura.REALIZADA.getValor());
            recibosCaja = query.getResultList();
        } catch (Exception e) {
            recibosCaja = null;
            e.printStackTrace();
        }
        return recibosCaja;
    }

}
