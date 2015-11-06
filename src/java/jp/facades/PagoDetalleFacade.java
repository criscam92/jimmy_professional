package jp.facades;

import java.util.ArrayList;
import java.util.List;
import jp.entidades.PagoDetalle;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.DetallePagoHelper;
import jp.entidades.Pago;
import jp.entidades.PagoPublicidad;
import jp.entidades.TipoPagoHelper;
import jp.util.TipoPagoAbono;

@Stateless
public class PagoDetalleFacade extends AbstractFacade<PagoDetalle> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagoDetalleFacade() {
        super(PagoDetalle.class);
    }

    public List<PagoDetalle> getPagoDetallesByPago(Pago pago) {
        List<PagoDetalle> pagoDetalles;
        try {
            Query query = getEntityManager().createQuery("SELECT pd FROM PagoDetalle pd WHERE pd.pago.id = :pago");
            query.setParameter("pago", pago.getId());
            pagoDetalles = query.getResultList();
        } catch (Exception e) {
            pagoDetalles = new ArrayList<>();
        }
        return pagoDetalles;
    }

    public List<DetallePagoHelper> getListPublicidad() {
        List<DetallePagoHelper> list = new ArrayList<>();
        try {
            Query query = getEntityManager().createQuery("SELECT pd FROM PagoDetalle pd WHERE pd.tipo = :tipo");
            query.setParameter("tipo", TipoPagoAbono.PUBLICIDAD.getValor());
            List<PagoDetalle> pds = query.getResultList();

            for (PagoDetalle pd : pds) {
                try {
                    Query query2 = getEntityManager().createQuery("SELECT pp FROM PagoPublicidad pp WHERE pp.pagoDetalle.id = :pagoDetalle");
                    query2.setParameter("pagoDetalle", pd.getId());
                    PagoPublicidad pp = (PagoPublicidad) query2.getSingleResult();
                    list.add(new DetallePagoHelper(pd.getId(), pd.getValor(), pd.getPago(), pp.getTipo()));
                } catch (Exception e) {
                    System.out.println("ERROR OBTENIENDO EL PAGOPUBLICIDAD DEL PAGODETALLE CON ID " + pd.getId());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR OBTENIENDO LA LISTA DE PAGODETALLE");
            e.printStackTrace();
            list = new ArrayList<>();
        }
        return list;
    }

    public List<DetallePagoHelper> getListComisiones() {
        List<DetallePagoHelper> list = new ArrayList<>();
        try {
            Query query = getEntityManager().createQuery("SELECT pd FROM PagoDetalle pd WHERE pd.tipo = :tipo");
            query.setParameter("tipo", TipoPagoAbono.COMISION.getValor());
            List<PagoDetalle> pds = query.getResultList();

            for (PagoDetalle pd : pds) {
                list.add(new DetallePagoHelper(pd.getId(), pd.getValor(), pd.getPago(), null));
            }

        } catch (Exception e) {
            System.out.println("ERROR OBTENIENDO LA LISTA DE PAGODETALLE");
            e.printStackTrace();
            list = new ArrayList<>();
        }
        return list;
    }

}
