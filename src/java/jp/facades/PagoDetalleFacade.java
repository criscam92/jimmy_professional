package jp.facades;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jp.entidades.PagoDetalle;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.DetallePagoHelper;
import jp.entidades.Pago;
import jp.entidades.PagoPublicidad;
import jp.util.TipoPagoAbono;
import sun.java2d.pipe.SpanShapeRenderer;

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

    public List<DetallePagoHelper> getListPublicidad(String fechaIni, String fechaFin) {
        List<DetallePagoHelper> list = new ArrayList<>();
        try {

            String sql = "SELECT pd FROM PagoDetalle pd WHERE pd.tipo = :tipo";
            if ((fechaIni != null && !fechaIni.trim().isEmpty()) && (fechaFin != null && !fechaFin.trim().isEmpty())) {
                sql += " AND pd.pago.fecha BETWEEN :fecInicio AND :fecFin";
            }

            Query query = getEntityManager().createQuery(sql);
            query.setParameter("tipo", TipoPagoAbono.PUBLICIDAD.getValor());
            if ((fechaIni != null && !fechaIni.trim().isEmpty()) && (fechaFin != null && !fechaFin.trim().isEmpty())) {
                query.setParameter("fecInicio", new SimpleDateFormat("dd/MMM/yyyy").parse(fechaIni));
                query.setParameter("fecFin", new SimpleDateFormat("dd/MMM/yyyy").parse(fechaFin));
            }
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

    public List<DetallePagoHelper> getListComisiones(String fechaIni, String fechaFin) {
        List<DetallePagoHelper> list = new ArrayList<>();
        try {

            String sql = "SELECT pd FROM PagoDetalle pd WHERE pd.tipo = :tipo";
            if ((fechaIni != null && !fechaIni.trim().isEmpty()) && (fechaFin != null && !fechaFin.trim().isEmpty())) {
                sql += " AND pd.pago.fecha BETWEEN :fecInicio AND :fecFin";
            }

            Query query = getEntityManager().createQuery(sql);
            query.setParameter("tipo", TipoPagoAbono.COMISION.getValor());
            if ((fechaIni != null && !fechaIni.trim().isEmpty()) && (fechaFin != null && !fechaFin.trim().isEmpty())) {
                query.setParameter("fecInicio", new SimpleDateFormat("dd/MMM/yyyy").parse(fechaIni));
                query.setParameter("fecFin", new SimpleDateFormat("dd/MMM/yyyy").parse(fechaFin));
            }
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
