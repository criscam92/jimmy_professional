package jp.facades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.DespachoFacturaProducto;
import jp.entidades.DevolucionProducto;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.FacturaPromocion;
import jp.entidades.IngresoProducto;
import jp.entidades.Producto;
import jp.entidades.ProductoHelper2;
import jp.entidades.PromocionProducto;
import jp.entidades.SalidaProducto;
import jp.entidades.VisitaProducto;
import jp.util.EstadoFactura;
import jp.util.EstadoVisita;

@Stateless
public class ProductoFacade extends AbstractFacade<Producto> {

    public enum TIPO_PRECIO {

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

    public int getCantidadDisponibleByProducto(Producto producto) {
        int result;
        try {
            int productosIngresos = 0, productosVisitas = 0, productosDevolucion = 0, productosDespachoFactura = 0, productosSalidas = 0;
            Query query1 = getEntityManager().createQuery("SELECT ip FROM IngresoProducto ip WHERE ip.producto.id = :prod");
            query1.setParameter("prod", producto.getId());

            for (Object o : query1.getResultList()) {
                productosIngresos += ((IngresoProducto) o).getCantidad();
            }

            Query query2 = getEntityManager().createQuery("SELECT vp FROM VisitaProducto vp WHERE vp.producto.id = :prod AND vp.visita.estado <> :est1 AND vp.visita.estado <> :est2");
            query2.setParameter("prod", producto.getId());
            query2.setParameter("est1", EstadoVisita.ANULADA.getValor());
            query2.setParameter("est2", EstadoVisita.CANCELADA.getValor());

            for (Object o : query2.getResultList()) {
                productosVisitas += ((VisitaProducto) o).getCantidad();
            }

            Query query3 = getEntityManager().createQuery("SELECT dp FROM DevolucionProducto dp WHERE dp.producto.id = :prod AND dp.devolucion.realizado = :rea");
            query3.setParameter("prod", producto.getId());
            query3.setParameter("rea", true);

            for (Object o : query3.getResultList()) {
                productosDevolucion += ((DevolucionProducto) o).getCantidad();
            }

            Query query4 = getEntityManager().createQuery("SELECT dfp FROM DespachoFacturaProducto dfp WHERE dfp.producto.id = :prod AND dfp.despachoFactura.realizado = :rea");
            query4.setParameter("prod", producto.getId());
            query4.setParameter("rea", true);

            for (Object o : query4.getResultList()) {
                productosDespachoFactura += ((DespachoFacturaProducto) o).getCantidad();
            }

            Query query5 = getEntityManager().createQuery("SELECT sp FROM SalidaProducto sp WHERE sp.producto.id = :prod AND sp.salida.estado <> :est1 AND sp.salida.estado <> :est2");
            query5.setParameter("prod", producto.getId());
            query5.setParameter("est1", EstadoFactura.ANULADO.getValor());
            query5.setParameter("est2", EstadoFactura.CANCELADO.getValor());

            for (Object o : query5.getResultList()) {
                productosSalidas += ((SalidaProducto) o).getCantidad();
            }

            result = (productosIngresos - productosVisitas - productosDespachoFactura - productosSalidas) + productosDevolucion;

        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    @Override
    public List<Producto> findAll() {
        return super.findAll(true);
    }

    @Override
    public List<Producto> findAll(boolean asc) {
        return super.findAll(asc);
    }

    public Long getTotalBonificacionesByProducto(Producto producto) {
        Long cantidad = 0l;
        try {
            Query query = em.createQuery("SELECT SUM(p.unidadesBonificacion) FROM FacturaProducto p WHERE p.producto.id = :producto AND p.factura.estado = :estado");
            query.setParameter("producto", producto.getId());
            query.setParameter("estado", EstadoFactura.REALIZADA.getValor());

            cantidad = (Long) query.getSingleResult();

            cantidad = cantidad == null ? 0 : cantidad;

            query = em.createQuery("SELECT p FROM PromocionProducto p WHERE p.producto.id = :producto");
            query.setParameter("producto", producto.getId());
            List<PromocionProducto> promociones = query.getResultList();

            for (PromocionProducto promocionProducto : promociones) {
                try {
                    query = em.createQuery("SELECT SUM(p.unidadesBonificacion) FROM FacturaPromocion p WHERE p.promocion.id = :promocion AND p.factura.estado = :estado");
                    query.setParameter("promocion", promocionProducto.getPromocion().getId());
                    query.setParameter("estado", EstadoFactura.REALIZADA.getValor());
                    Long cantidadPromocion = (Long) query.getSingleResult();
                    if (cantidadPromocion == null) {
                        cantidadPromocion = 0l;
                    }
                    cantidad += (cantidadPromocion * promocionProducto.getCantidad());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return cantidad;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cantidad;
    }

    public List<ProductoHelper2> getProductosHelper2(Date fechaIni, Date fechaFin) {
        List<ProductoHelper2> list = new ArrayList<>();
        try {
            Query query1 = getEntityManager().createQuery("SELECT p FROM Producto p");
            List<Producto> productos = query1.getResultList();

            for (Producto p : productos) {
                int cantBonificaciones = 0, cantVenta = 0;
                double valBonificacion = 0, valVenta = 0;
                Query query2 = getEntityManager().createQuery("SELECT fp FROM FacturaProducto fp WHERE fp.producto.id = :producto AND fp.factura.estado = :estado AND fp.factura.fecha BETWEEN :fecInicio AND :fecFin");
                query2.setParameter("producto", p.getId());
                query2.setParameter("estado", EstadoFactura.REALIZADA.getValor());
                query2.setParameter("fecInicio", fechaIni);
                query2.setParameter("fecFin", fechaFin);
                List<FacturaProducto> facturaProductos = query2.getResultList();

                for (FacturaProducto fp : facturaProductos) {
                    cantBonificaciones += fp.getUnidadesBonificacion();
                    cantVenta += fp.getUnidadesVenta();

                    if (fp.getFactura().getDolar()) {
                        valBonificacion += (cantBonificaciones) * (p.getValorVentaUsd() * fp.getFactura().getDolarActual());
                        valVenta += (cantVenta) * (p.getValorVentaUsd() * fp.getFactura().getDolarActual());
                    } else {
                        valBonificacion += (cantBonificaciones) * (p.getValorVenta());
                        valVenta += (cantVenta) * (p.getValorVenta());
                    }
                }

                Query query3 = getEntityManager().createQuery("SELECT p FROM PromocionProducto p WHERE p.producto.id = :producto");
                query3.setParameter("producto", p.getId());
                List<PromocionProducto> promocionProductos = query3.getResultList();

                for (PromocionProducto pp : promocionProductos) {
                    Query query4 = getEntityManager().createQuery("SELECT fp FROM FacturaPromocion fp WHERE fp.promocion.id = :promocion AND fp.factura.estado = :estado AND fp.factura.fecha BETWEEN :fecInicio AND :fecFin");
                    query4.setParameter("promocion", pp.getPromocion().getId());
                    query4.setParameter("estado", EstadoFactura.REALIZADA.getValor());
                    query4.setParameter("fecInicio", fechaIni);
                    query4.setParameter("fecFin", fechaFin);
                    List<FacturaPromocion> facturaPromociones = query4.getResultList();

                    for (FacturaPromocion fp : facturaPromociones) {
                        cantBonificaciones += fp.getUnidadesBonificacion() * pp.getCantidad();
                        cantVenta += fp.getUnidadesVenta() * pp.getCantidad();

                        if (fp.getFactura().getDolar()) {
                            valBonificacion += (cantBonificaciones) * (p.getValorVentaUsd() * fp.getFactura().getDolarActual());
                            valVenta += (cantVenta) * (p.getValorVentaUsd() * fp.getFactura().getDolarActual());
                        } else {
                            valBonificacion += (cantBonificaciones) * (p.getValorVenta());
                            valVenta += (cantVenta) * (p.getValorVenta());
                        }
                    }
                }
                if (cantBonificaciones > 0 && cantVenta > 0) {
                    list.add(new ProductoHelper2(p.getId(), p, cantBonificaciones, valBonificacion, cantVenta, valVenta));
                }
            }
        } catch (Exception e) {
            list = new ArrayList<>();
            e.printStackTrace();
        }
        return list;
    }

}
