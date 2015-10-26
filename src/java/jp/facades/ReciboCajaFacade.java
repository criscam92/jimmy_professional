package jp.facades;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import jp.entidades.ReciboCaja;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.util.CondicionConcepto;
import jp.util.EstadoFactura;
import jp.util.EstadoPago;

@Stateless
public class ReciboCajaFacade extends AbstractFacade<ReciboCaja> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    @EJB
    private CajaFacade cajaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReciboCajaFacade() {
        super(ReciboCaja.class);
    }

    public List<ReciboCaja> getRecibosCaja(Boolean isCxcCxp) {
        List<ReciboCaja> reciboCajas;
        String transaccion = "";
        if (isCxcCxp != null && isCxcCxp) {
            transaccion = " AND rc.transaccion = NULL";
        }
        String consulta = "SELECT rc FROM ReciboCaja rc WHERE rc.fecha >= :fec AND rc.concepto.cxccxp IN :condicion " + transaccion + " ORDER BY rc.fecha DESC";

        try {
            Query q = em.createQuery(consulta);
            q.setParameter("fec", cajaFacade.getCaja().getFechaActualizacion());
            if (isCxcCxp == null) {
                q.setParameter("condicion", CondicionConcepto.getConceptos());
            } else {
                if (isCxcCxp) {
                    q.setParameter("condicion", CondicionConcepto.getConceptosCxCCxp());
                } else {
                    List<Integer> condicionesConceptos = new ArrayList<>();
                    condicionesConceptos.add(CondicionConcepto.NINGUNO.getValor());
                    q.setParameter("condicion", condicionesConceptos);
                }
            }
            reciboCajas = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            reciboCajas = new ArrayList<>();
        }
        return reciboCajas;
    }

    public Long getValorPendienteCxcCxp(ReciboCaja reciboCaja) {
        Long pendiente = reciboCaja.getValor();
        try {
            Query query = em.createQuery("SELECT SUM(rc.valor) FROM ReciboCaja rc WHERE rc.transaccion.id = :transaccion AND rc.estado = :est");
            query.setParameter("transaccion", reciboCaja.getId());
            query.setParameter("est", EstadoFactura.REALIZADA.getValor());
            pendiente = (Long) query.getSingleResult();
            if (pendiente == null) {
                pendiente = reciboCaja.getValor();
                return pendiente;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reciboCaja.getValor() - pendiente;
    }

    public List<ReciboCaja> getPagosByCxcCxp(ReciboCaja reciboCaja, Boolean estadoAnulado) {
        List<ReciboCaja> reciboCajasTMP;
        try {
            String query = "SELECT rc FROM ReciboCaja rc WHERE rc.transaccion.id = :trans";

            if (estadoAnulado != null) {
                query += " AND rc.transaccion.estado IN :est";
            }
            Query q = em.createQuery(query);
            if (estadoAnulado != null) {
                if (estadoAnulado) {
                    q.setParameter("est", new ArrayList<Integer>().add(EstadoFactura.ANULADO.getValor()));
                } else {
                    q.setParameter("est", EstadoFactura.getEstadosFactura());
                }
            }

            
            q.setParameter("trans", reciboCaja.getId());
            reciboCajasTMP = q.getResultList();
            return reciboCajasTMP;
        } catch (Exception e) {
            reciboCajasTMP = new ArrayList<>();
        }
        return reciboCajasTMP;
    }

}
