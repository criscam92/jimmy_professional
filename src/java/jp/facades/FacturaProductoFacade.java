package jp.facades;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.FacturaPromocion;
import jp.entidades.PromocionProducto;

@Stateless
public class FacturaProductoFacade extends AbstractFacade<FacturaProducto> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @EJB
    private FacturaPromocionFacade facturaPromocionFacade;
    @EJB
    private PromocionProductoFacade promocionProductoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FacturaProductoFacade() {
        super(FacturaProducto.class);
    }

    public PromocionProductoFacade getPromocionProductoFacade() {
        return promocionProductoFacade;
    }

    public FacturaPromocionFacade getFacturaPromocionFacade() {
        return facturaPromocionFacade;
    }

    /**
     *
     * @param factura
     * @param tipo Entero que indica el valor que quiere retornar (1 Unidades
     * vendidas o 2 Unidades bonificacion)
     * @return
     */
    public Long getCantidadVentaOrBonificacionByFactura(Factura factura, int tipo) {
        Long cantidadFProd = 0l;
        Long cantidadFProm = 0l;
        Long cantidad = 0l;
        try {
            String sql = "SELECT SUM(" + (tipo == 1 ? "fp.unidadesVenta" : "fp.unidadesBonificacion") + ") FROM FacturaProducto fp WHERE fp.factura.id = :fact";
            Query queryFP = getEntityManager().createQuery(sql);
            queryFP.setParameter("fact", factura.getId());
            cantidadFProd = (Long) queryFP.getSingleResult();
            if (cantidadFProd == null) {
                cantidadFProd = 0l;
            }

            List<FacturaPromocion> facturaPromociones = getFacturaPromocionFacade().getFacturaPromocionByFactura(factura);
            for (FacturaPromocion fp : facturaPromociones) {
                List<PromocionProducto> promocionProductos = getPromocionProductoFacade().getPromocionProductoByProducto(fp.getPromocion());
                for (PromocionProducto pp : promocionProductos) {
                    cantidadFProd += (tipo == 1 ? fp.getUnidadesVenta() : fp.getUnidadesBonificacion()) * pp.getCantidad();
                }
            }

            cantidad = cantidadFProd + cantidadFProm;
        } catch (Exception e) {
            cantidad = null;
            e.printStackTrace();
        }
        return cantidad;
    }

    public String getNumOrden() {
        try {
            Long numOrden = 0l;
            Query q = em.createNativeQuery("SELECT nextval('factura_num_orden')");
            q.setMaxResults(1);
            numOrden = (Long) q.getSingleResult();
            return "" + numOrden;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
