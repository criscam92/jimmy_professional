package jp.facades;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import jp.entidades.CambioDevolucion;
import jp.entidades.DespachoFactura;
import jp.entidades.DespachoFacturaProducto;
import jp.entidades.Devolucion;
import jp.entidades.DevolucionProducto;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.entidades.FacturaPromocion;
import jp.entidades.Ingreso;
import jp.entidades.IngresoProducto;
import jp.entidades.ListaPrecio;
import jp.entidades.Pago;
import jp.entidades.PagoDetalle;
import jp.entidades.PagoDevolucion;
import jp.entidades.PagoHelper;
import jp.entidades.PagoPublicidad;
import jp.entidades.Promocion;
import jp.entidades.PromocionProducto;
import jp.entidades.Parametros;
import jp.entidades.PrecioProducto;
import jp.entidades.Producto;
import jp.entidades.ProductoHelper;
import jp.entidades.ProductoPromocionHelper;
import jp.entidades.ReciboCaja;
import jp.entidades.RelacionFactura;
import jp.entidades.Salida;
import jp.entidades.SalidaProducto;
import jp.entidades.TipoPagoHelper;
import jp.entidades.Visita;
import jp.entidades.VisitaProducto;
import jp.util.EstadoDespachoFactura;
import jp.util.EstadoFactura;
import jp.util.EstadoPago;
import jp.util.EstadoPagoFactura;
import jp.util.EstadoVisita;

@Stateful
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class TransactionFacade {

    @PersistenceContext(unitName = "jimmy_professionalPU", type = PersistenceContextType.EXTENDED)
    private EntityManager em;
    private UserTransaction userTransaction;

    @EJB
    private FacturaPromocionFacade facturaPromocionFacade;
    @EJB
    private PromocionProductoFacade promocionProductoFacade;
    @Resource
    private SessionContext sessionContext;

    protected EntityManager getEntityManager() {
        return em;
    }

    public FacturaPromocionFacade getFacturaPromocionFacade() {
        return facturaPromocionFacade;
    }

    public PromocionProductoFacade getPromocionProductoFacade() {
        return promocionProductoFacade;
    }

    public boolean createVisitaProducto(List<VisitaProducto> visitaProducto, Visita v) {
        boolean complete = false;
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            Visita visitaTMP = em.find(Visita.class, v.getId());
            visitaTMP.setEstado(EstadoVisita.REALIZADA.getValor());
            visitaTMP.setCalificacionServicio(v.getCalificacionServicio());
            visitaTMP.setPuntualidadServicio(v.getPuntualidadServicio());
            visitaTMP.setCumplioExpectativas(v.getCumplioExpectativas());
            visitaTMP.setObservacionesCliente(v.getObservacionesCliente());
            em.merge(visitaTMP);
            if (!visitaProducto.isEmpty()) {
                for (VisitaProducto visitasProductoTMP : visitaProducto) {
                    visitasProductoTMP.setId(null);
                    visitasProductoTMP.setVisita(visitaTMP);
                    em.persist(visitasProductoTMP);
                }
            }

            userTransaction.commit();
            complete = true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            complete = false;
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return complete;
    }

    public boolean anularOCancelarVisitaProducto(Visita v, boolean anular) {
        boolean result;
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            Visita visitaTMP = getEntityManager().find(Visita.class, v.getId());
            visitaTMP.setEstado(anular ? EstadoVisita.ANULADA.getValor() : EstadoVisita.CANCELADA.getValor());
            getEntityManager().merge(visitaTMP);
            userTransaction.commit();
            result = true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            result = false;
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return result;
    }

    public boolean createUpdateRecargo(Parametros p) {
        boolean complete;
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            Parametros parametroTMP = new Parametros();

            parametroTMP.setId(1);
            parametroTMP.setRecargoLocal(p.getRecargoLocal());
            parametroTMP.setRecargoNacional(p.getRecargoNacional());
            parametroTMP.setRecargoInternacional(p.getRecargoInternacional());
            parametroTMP.setCiudad(p.getCiudad());
            parametroTMP.setCorreo(p.getCorreo());
            parametroTMP.setDiasDescuentoProntoPago(p.getDiasDescuentoProntoPago());
            parametroTMP.setPorcentajeProntoPago(p.getPorcentajeProntoPago());
            parametroTMP.setPorcentajePublicidad(p.getPorcentajePublicidad());
            parametroTMP.setPorcentajeVentaPublic(p.getPorcentajeVentaPublic());
            parametroTMP.setPrecioDolar(p.getPrecioDolar());
            parametroTMP.setValorProntoPago(p.getValorProntoPago());

            em.merge(parametroTMP);
            userTransaction.commit();
            complete = true;

        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            complete = false;
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return complete;
    }

    public boolean createPromocion(Promocion p, List<PromocionProducto> promocionProductos) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Promocion proTMP = new Promocion();
            proTMP.setDescripcion(p.getDescripcion());
            proTMP.setValor(p.getValor());
            proTMP.setCodigo(p.getCodigo());
            if (p.getCategoria() != null) {
                proTMP.setCategoria(p.getCategoria());
            }
            if (p.getValorVentaUsd() != null) {
                proTMP.setValorVentaUsd(p.getValorVentaUsd());
            }

            getEntityManager().merge(proTMP);

            for (PromocionProducto pp : promocionProductos) {
                pp.setId(null);
                pp.setPromocion(proTMP);
                getEntityManager().merge(pp);
            }

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }

    public boolean updatePromocion(Promocion promocion, List<PromocionProducto> promocionProductosGuardar, List<PromocionProducto> promocionProductosEliminar, List<PromocionProducto> promocionProductosEditar) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            getEntityManager().merge(promocion);

            for (PromocionProducto pp : promocionProductosEliminar) {
                PromocionProducto ppTMP = getEntityManager().find(PromocionProducto.class, pp.getId());
                getEntityManager().remove(ppTMP);
            }

            for (PromocionProducto pp : promocionProductosEditar) {
                PromocionProducto ppTMP = getEntityManager().find(PromocionProducto.class, pp.getId());
                ppTMP.setCantidad(pp.getCantidad());
                getEntityManager().merge(ppTMP);

            }

            for (PromocionProducto pp : promocionProductosGuardar) {
                pp.setId(null);
                pp.setPromocion(promocion);
                getEntityManager().merge(pp);
            }

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }

    public boolean deletePromocion(Promocion promocion) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Query query1 = getEntityManager().createQuery("DELETE FROM PromocionProducto pp WHERE pp.promocion.id = :promo");
            query1.setParameter("promo", promocion.getId());
            query1.executeUpdate();

            Query query2 = getEntityManager().createQuery("DELETE FROM Promocion p WHERE p.id = :promo");
            query2.setParameter("promo", promocion.getId());
            query2.executeUpdate();

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }

    public boolean updateFacturaProductoPromocion(Factura factura, List<ProductoPromocionHelper> objectsCrear,
            List<ProductoPromocionHelper> objectsEditar, List<ProductoPromocionHelper> objectsEliminar) {

        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Factura facturaTMP = getEntityManager().find(Factura.class, factura.getId());
            facturaTMP.setOrdenPedido(factura.getOrdenPedido());
            facturaTMP.setFecha(factura.getFecha());
            facturaTMP.setCliente(factura.getCliente());
            facturaTMP.setEmpleado(factura.getEmpleado());
            facturaTMP.setTipoPago(factura.getTipoPago());
            facturaTMP.setObservaciones(factura.getObservaciones());
            facturaTMP.setTotalBruto(factura.getTotalBruto());
            facturaTMP.setDescuento(factura.getDescuento());
            facturaTMP.setTotalPagar(factura.getTotalPagar());
            facturaTMP.setUsuario(factura.getUsuario());
            facturaTMP.setEstado(factura.getEstado());
            facturaTMP.setEstadoDespacho(factura.getEstadoDespacho());
            facturaTMP.setEstadoPago(factura.getEstadoPago());
            facturaTMP.setDolar(factura.getDolar());
            facturaTMP.setDolarActual(factura.getDolarActual());
            getEntityManager().merge(facturaTMP);

            for (ProductoPromocionHelper pph : objectsCrear) {
                if (pph.isProducto()) {
                    FacturaProducto fp = new FacturaProducto();
                    fp.setFactura(facturaTMP);
                    fp.setId(null);
                    fp.setPrecio(facturaTMP.getDolar() ? pph.getPrecioUs() : pph.getPrecio());
                    fp.setProducto((Producto) pph.getProductoPromocion());
                    fp.setUnidadesBonificacion(pph.getUnidadesBonificacion());
                    fp.setUnidadesVenta(pph.getUnidadesVenta());
                    getEntityManager().merge(fp);
                } else {
                    FacturaPromocion fp = new FacturaPromocion();
                    fp.setFactura(facturaTMP);
                    fp.setId(null);
                    fp.setPrecio(facturaTMP.getDolar() ? pph.getPrecioUs() : pph.getPrecio());
                    fp.setPromocion((Promocion) pph.getProductoPromocion());
                    fp.setUnidadesBonificacion(pph.getUnidadesBonificacion());
                    fp.setUnidadesVenta(pph.getUnidadesVenta());
                    getEntityManager().merge(fp);
                }
            }

            for (ProductoPromocionHelper pph : objectsEditar) {
                if (pph.isProducto()) {
                    FacturaProducto fp = getEntityManager().find(FacturaProducto.class, pph.getIdObject());
                    fp.setFactura(facturaTMP);
                    fp.setPrecio(facturaTMP.getDolar() ? pph.getPrecioUs() : pph.getPrecio());
                    fp.setProducto((Producto) pph.getProductoPromocion());
                    fp.setUnidadesBonificacion(pph.getUnidadesBonificacion());
                    fp.setUnidadesVenta(pph.getUnidadesVenta());
                    getEntityManager().merge(fp);
                } else {
                    FacturaPromocion fp = getEntityManager().find(FacturaPromocion.class, pph.getIdObject());
                    fp.setFactura(facturaTMP);
                    fp.setPrecio(facturaTMP.getDolar() ? pph.getPrecioUs() : pph.getPrecio());
                    fp.setPromocion((Promocion) pph.getProductoPromocion());
                    fp.setUnidadesBonificacion(pph.getUnidadesBonificacion());
                    fp.setUnidadesVenta(pph.getUnidadesVenta());
                    getEntityManager().merge(fp);
                }
            }

            for (ProductoPromocionHelper pph : objectsEliminar) {
                if (pph.isProducto()) {
                    FacturaProducto fp = getEntityManager().find(FacturaProducto.class, pph.getIdObject());
                    getEntityManager().remove(fp);
                } else {
                    FacturaPromocion fp = getEntityManager().find(FacturaPromocion.class, pph.getIdObject());
                    getEntityManager().remove(fp);
                }
            }

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }

    public void createFacturaProductoPromocion(Factura factura, List<ProductoPromocionHelper> objects) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Factura facturaTMP = new Factura();
            facturaTMP.setOrdenPedido(factura.getOrdenPedido());
            facturaTMP.setFecha(factura.getFecha());
            facturaTMP.setCliente(factura.getCliente());
            facturaTMP.setEmpleado(factura.getEmpleado());
            facturaTMP.setTipoPago(factura.getTipoPago());
            facturaTMP.setObservaciones(factura.getObservaciones());
            facturaTMP.setTotalBruto(factura.getTotalBruto());
            facturaTMP.setDescuento(factura.getDescuento());
            facturaTMP.setTotalPagar(factura.getTotalPagar());
            facturaTMP.setUsuario(factura.getUsuario());
            facturaTMP.setEstado(factura.getEstado());
            facturaTMP.setEstadoDespacho(factura.getEstadoDespacho());
            facturaTMP.setEstadoPago(factura.getEstadoPago());
            facturaTMP.setDolar(factura.getDolar());
            facturaTMP.setDolarActual(factura.getDolarActual());
            getEntityManager().merge(facturaTMP);

            for (ProductoPromocionHelper pph : objects) {
                if (pph.isProducto()) {
                    FacturaProducto fp = new FacturaProducto();
                    fp.setFactura(facturaTMP);
                    fp.setId(null);
                    fp.setPrecio(facturaTMP.getDolar() ? pph.getPrecioUs() : pph.getPrecio());
                    fp.setProducto((Producto) pph.getProductoPromocion());
                    fp.setUnidadesBonificacion(pph.getUnidadesBonificacion());
                    fp.setUnidadesVenta(pph.getUnidadesVenta());
                    getEntityManager().merge(fp);
                } else {
                    FacturaPromocion fp = new FacturaPromocion();
                    fp.setFactura(facturaTMP);
                    fp.setId(null);
                    fp.setPrecio(facturaTMP.getDolar() ? pph.getPrecioUs() : pph.getPrecio());
                    fp.setPromocion((Promocion) pph.getProductoPromocion());
                    fp.setUnidadesBonificacion(pph.getUnidadesBonificacion());
                    fp.setUnidadesVenta(pph.getUnidadesVenta());
                    getEntityManager().merge(fp);
                }
            }

            userTransaction.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
    }

    public boolean createIngreso(Ingreso i, List<IngresoProducto> ingresoProductos) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Ingreso ingreTMP = new Ingreso();
            ingreTMP.setObservaciones(i.getObservaciones());
            ingreTMP.setFechaIngreso(i.getFechaIngreso());
            ingreTMP.setUsuario(i.getUsuario());
            getEntityManager().merge(ingreTMP);

            for (IngresoProducto ip : ingresoProductos) {
                ip.setId(null);
                ip.setIngreso(ingreTMP);
                getEntityManager().merge(ip);
            }

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }

    public boolean updateIngreso(Ingreso ingreso, List<IngresoProducto> ingresoProductosGuardar, List<IngresoProducto> ingresoProductosEliminar, List<IngresoProducto> ingresoProductosEditar) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            getEntityManager().merge(ingreso);

            for (IngresoProducto ip : ingresoProductosEliminar) {
                IngresoProducto ipTMP = getEntityManager().find(IngresoProducto.class, ip.getId());
                getEntityManager().remove(ipTMP);
            }

            for (IngresoProducto ip : ingresoProductosEditar) {
                IngresoProducto ipTMP = getEntityManager().find(IngresoProducto.class, ip.getId());
                ipTMP.setCantidad(ip.getCantidad());
                getEntityManager().merge(ipTMP);
            }

            for (IngresoProducto ip : ingresoProductosGuardar) {
                ip.setId(null);
                ip.setIngreso(ingreso);
                getEntityManager().merge(ip);
            }

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }

    public boolean deleteIngreso(Ingreso ingreso) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Query query1 = getEntityManager().createQuery("DELETE FROM IngresoProducto ip WHERE ip.ingreso.id = :ingre");
            query1.setParameter("ingre", ingreso.getId());
            query1.executeUpdate();

            Query query2 = getEntityManager().createQuery("DELETE FROM Ingreso i WHERE i.id = :ingre");
            query2.setParameter("ingre", ingreso.getId());
            query2.executeUpdate();

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }

    public void createDespachoFactura(DespachoFactura despachoFactura, List<ProductoHelper> productoHelpers) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            DespachoFactura despachoFacturaTMP = new DespachoFactura();
            despachoFacturaTMP.setDespacho(despachoFactura.getDespacho());
            despachoFacturaTMP.setFactura(despachoFactura.getFactura());
            despachoFacturaTMP.setFecha(despachoFactura.getFecha());
            despachoFacturaTMP.setRealizado(despachoFactura.getRealizado());
            despachoFacturaTMP.setUsuario(despachoFactura.getUsuario());
            getEntityManager().merge(despachoFacturaTMP);

            for (ProductoHelper ph : productoHelpers) {
                if (ph.getCantidadADespachar() > 0) {
                    DespachoFacturaProducto dfp = new DespachoFacturaProducto();
                    dfp.setCantidad(ph.getCantidadADespachar());
                    dfp.setDespachoFactura(despachoFacturaTMP);
                    dfp.setId(null);
                    dfp.setProducto(ph.getProducto());
                    getEntityManager().merge(dfp);
                }
            }

            Factura facturaTMP = getEntityManager().find(Factura.class, despachoFacturaTMP.getFactura().getId());
            updateEstadosFactura(facturaTMP);
            getEntityManager().merge(facturaTMP);

            userTransaction.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
            }
        } finally {
            getEntityManager().clear();
        }
    }

    public boolean createPagoDevolucionProducto(Devolucion d, List<DevolucionProducto> dps, Factura f, List<FacturaProducto> fps) {
        boolean result;

        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            Devolucion devolucionTMP = em.merge(d);

            for (DevolucionProducto devolucionProductoTMP : dps) {
                devolucionProductoTMP.setId(null);
                devolucionProductoTMP.setDevolucion(devolucionTMP);
                em.persist(devolucionProductoTMP);
            }

            Factura facturaTMP = em.merge(f);

            for (FacturaProducto facturaProductoTMP : fps) {
                facturaProductoTMP.setId(null);
                facturaProductoTMP.setFactura(facturaTMP);
                em.persist(facturaProductoTMP);
            }

            CambioDevolucion cambioDevolucion = new CambioDevolucion();
            cambioDevolucion.setDevolucion(devolucionTMP);
            cambioDevolucion.setFactura(facturaTMP);
            cambioDevolucion.setRealizado(true);
            em.persist(cambioDevolucion);

            Pago pago = new Pago();
            pago.setFactura(facturaTMP);
            pago.setFormaPago(facturaTMP.getTipoPago());
            pago.setFecha(facturaTMP.getFecha());
            if (!facturaTMP.getObservaciones().trim().equals("")) {
                pago.setObservaciones(facturaTMP.getObservaciones());
            }
            pago.setValorTotal(facturaTMP.getTotalPagar());
            pago.setDolar(facturaTMP.getDolar());
            pago.setEstado(facturaTMP.getEstado());
            em.persist(pago);

            PagoDetalle pagoDetalle = new PagoDetalle();
            pagoDetalle.setPago(pago);
            pagoDetalle.setTipo(facturaTMP.getTipoPago());
            pagoDetalle.setValor(facturaTMP.getTotalPagar());
            em.persist(pagoDetalle);

            userTransaction.commit();
            result = true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            result = false;
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return result;
    }

    public boolean createPagoDevolucion(Pago p, Devolucion d, Factura f) {
        boolean result;
        userTransaction = sessionContext.getUserTransaction();

        try {
            userTransaction.begin();

            Factura facturaTMP = em.find(Factura.class, f.getId());
            em.merge(facturaTMP);

            Pago pagoTMP = em.merge(p);
            Devolucion devolucionTMP = em.merge(d);

            PagoDevolucion pagoDevolucion = new PagoDevolucion();
            pagoDevolucion.setDevolucion(devolucionTMP);
            pagoDevolucion.setPago(pagoTMP);
            pagoDevolucion.setRealizado(true);

            em.persist(pagoDevolucion);

            userTransaction.commit();
            result = true;

        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            result = false;
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return result;
    }

    public boolean updateVisitaProducto(Visita visita, List<VisitaProducto> visitaProductos, List<VisitaProducto> visitaProductosGuardar, List<VisitaProducto> visitaProductosEliminar, List<VisitaProducto> visitaProductosEditar) {
        System.out.println("\nVisita: " + visita + "\nVisitaProductos: " + visitaProductos.size() + "\nVisitaProductosGuardar: " + visitaProductosGuardar.size() + "\nVisitaProductosEliminar: " + visitaProductosEliminar.size() + "\nVisitaProductosEditar: " + visitaProductosEditar.size());
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Visita visitaTMP = getEntityManager().find(Visita.class, visita.getId());
            System.out.println("Visita = " + visitaTMP.getId());
            visitaTMP.setObservacionesCliente(visita.getObservacionesCliente());
            visitaTMP.setCalificacionServicio(visita.getCalificacionServicio());
            visitaTMP.setPuntualidadServicio(visita.getPuntualidadServicio());
            visitaTMP.setCumplioExpectativas(visita.getCumplioExpectativas());
            visitaTMP.setEstado(EstadoVisita.REALIZADA.getValor());
            getEntityManager().merge(visitaTMP);

            if ((visitaProductosGuardar != null && !visitaProductosGuardar.isEmpty())
                    || (visitaProductosEditar != null && !visitaProductosEditar.isEmpty())
                    || (visitaProductosEliminar != null && !visitaProductosEliminar.isEmpty())) {
                for (VisitaProducto vp : visitaProductosEliminar) {
                    VisitaProducto vpTMP = getEntityManager().find(VisitaProducto.class, vp.getId());
                    getEntityManager().remove(vpTMP);
                }

                for (VisitaProducto vp : visitaProductosEditar) {
                    VisitaProducto vpTMP = getEntityManager().find(VisitaProducto.class, vp.getId());
                    vpTMP.setCantidad(vp.getCantidad());
                    getEntityManager().merge(vpTMP);
                }

                for (VisitaProducto vp : visitaProductosGuardar) {
                    vp.setId(null);
                    vp.setVisita(visitaTMP);
                    getEntityManager().merge(vp);
                }
            } else {
                for (VisitaProducto vp : visitaProductos) {
                    VisitaProducto vpTMP = getEntityManager().find(VisitaProducto.class, vp.getId());
                    if (vpTMP == null) {
                        vp.setId(null);
                        vp.setVisita(visitaTMP);
                        getEntityManager().merge(vp);
                    }
                }
            }

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }

    public boolean anularRecibo(ReciboCaja reciboCaja) {
        boolean result;
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            ReciboCaja rc = getEntityManager().find(ReciboCaja.class, reciboCaja.getId());
            rc.setEstado(EstadoFactura.ANULADO.getValor());
            getEntityManager().merge(rc);
            userTransaction.commit();
            result = true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            result = false;
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return result;
    }

    public boolean anularDespachoFactura(DespachoFactura despachoFactura) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            DespachoFactura despachoFacturaTMP = getEntityManager().find(DespachoFactura.class, despachoFactura.getId());
            despachoFacturaTMP.setRealizado(false);
            getEntityManager().merge(despachoFacturaTMP);

            Factura facturaTMP = getEntityManager().find(Factura.class, despachoFacturaTMP.getFactura().getId());
            updateEstadosFactura(facturaTMP);
            getEntityManager().merge(facturaTMP);
            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
            }
        } finally {
            getEntityManager().clear();
        }
        return false;
    }

    public void createSalidaProducto(Salida salida, List<SalidaProducto> salidasProductos) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Salida salidaTMP = new Salida();
            salidaTMP.setDescripcion(salida.getDescripcion());
            salidaTMP.setFecha(Calendar.getInstance().getTime());
            salidaTMP.setTipo(salida.getTipo());
            salidaTMP.setUsuario(salida.getUsuario());
            getEntityManager().merge(salidaTMP);

            for (SalidaProducto sp : salidasProductos) {
                SalidaProducto salidaProducto = new SalidaProducto();
                salidaProducto.setSalida(salidaTMP);
                salidaProducto.setId(null);
                salidaProducto.setProducto(sp.getProducto());
                salidaProducto.setCantidad(sp.getCantidad());
                getEntityManager().merge(salidaProducto);
            }

            userTransaction.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
    }

    public boolean anularSalida(Salida salida) {
        boolean result;
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            Salida s = getEntityManager().find(Salida.class, salida.getId());
            s.setEstado(EstadoFactura.ANULADO.getValor());
            getEntityManager().merge(s);
            userTransaction.commit();
            result = true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            result = false;
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return result;
    }

    public void crearPago(List<PagoHelper> pagoHelpers, RelacionFactura relacionFactura) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            RelacionFactura rf = new RelacionFactura();
            rf.setFecha(relacionFactura.getFecha());
            rf.setObservaciones(relacionFactura.getObservaciones());
            rf.setVendedor(relacionFactura.getVendedor());
            getEntityManager().persist(rf);

            List<Factura> listTMP = new ArrayList<>();
            for (PagoHelper ph : pagoHelpers) {
                Factura f = getEntityManager().find(Factura.class, ph.getPago().getFactura().getId());
                if (!listTMP.contains(f)) {
                    listTMP.add(f);
                }

                Pago p = new Pago();
                p.setCuenta(ph.getPago().getCuenta());
                p.setDolar(ph.getPago().getFactura().getDolar());
                p.setEstado(ph.getPago().getEstado());
                p.setFactura(f);
                p.setFecha(ph.getPago().getFecha());
                p.setFormaPago(ph.getPago().getFormaPago());
                p.setNumeroCheque(ph.getPago().getNumeroCheque());
                p.setObservaciones(ph.getPago().getObservaciones());
                p.setOrdenPago(ph.getPago().getOrdenPago());
                p.setRelacionFactura(rf);
                p.setUsuario(ph.getPago().getUsuario());
                p.setValorTotal(ph.getPago().getValorTotal());
                getEntityManager().persist(p);

                for (TipoPagoHelper tph : ph.getTipoPagoHelpers()) {
                    PagoDetalle pd = new PagoDetalle();
                    pd.setPago(p);
                    pd.setTipo(tph.getTipo());
                    pd.setValor(tph.getValor());
                    getEntityManager().persist(pd);

                    if (tph.getTipoPublicidad() != null) {
                        PagoPublicidad pp = new PagoPublicidad();
                        pp.setPagoDetalle(pd);
                        pp.setTipo(tph.getTipoPublicidad());
                        getEntityManager().persist(pp);
                    }
                }
            }

            for (Factura f : listTMP) {
                Factura fTMP = getEntityManager().find(Factura.class, f.getId());
                updateEstadosFactura(fTMP);
                getEntityManager().merge(f);
            }
            userTransaction.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
            }
        }
    }

    private void updateEstadosFactura(Factura factura) {
        try {
            Query query1 = getEntityManager().createQuery("SELECT SUM(p.valorTotal) FROM Pago p WHERE p.factura.id = :fac AND p.estado = :est");
            query1.setParameter("fac", factura.getId());
            query1.setParameter("est", EstadoPago.REALIZADO.getValor());

            double valor;
            try {
                valor = (double) query1.getSingleResult();
            } catch (Exception e) {
                valor = 0.0;
            }

            if (factura.getTotalPagar() == valor) {
                factura.setEstadoPago(EstadoPagoFactura.PAGADA.getValor());
            } else if (valor == 0.0) {
                factura.setEstadoPago(EstadoPagoFactura.SIN_PAGO.getValor());
            } else {
                factura.setEstadoPago(EstadoPagoFactura.PAGO_PARCIAL.getValor());
            }

            Query query2 = getEntityManager().createQuery("SELECT df FROM DespachoFactura df WHERE df.factura.id = :fac AND df.realizado = :rea");
            query2.setParameter("fac", factura.getId());
            query2.setParameter("rea", true);
            List<DespachoFactura> despachoFacturas = query2.getResultList();

            long cantidadDespachos = 0;
            for (DespachoFactura df : despachoFacturas) {
                Query query3 = getEntityManager().createQuery("SELECT SUM(dfp.cantidad) FROM DespachoFacturaProducto dfp WHERE dfp.despachoFactura.id = :desF");
                query3.setParameter("desF", df.getId());
                cantidadDespachos += (long) query3.getSingleResult();
            }

            long cantidadFacturada = getCantidadProductosFacturadosByFactura(factura);

            if (cantidadFacturada == cantidadDespachos) {
                factura.setEstadoDespacho(EstadoDespachoFactura.DESPACHADO.getValor());
            } else if (cantidadDespachos == 0) {
                factura.setEstadoDespacho(EstadoDespachoFactura.SIN_DESPACHAR.getValor());
            } else {
                factura.setEstadoDespacho(EstadoDespachoFactura.DESPACHO_PARCIAL.getValor());
            }

            if ((factura.getEstadoPago() == EstadoPagoFactura.PAGADA.getValor()) && (factura.getEstadoDespacho() == EstadoDespachoFactura.DESPACHADO.getValor())) {
                factura.setEstado(EstadoFactura.REALIZADA.getValor());
            } else {
                factura.setEstado(EstadoFactura.PENDIENTE.getValor());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getCantidadProductosFacturadosByFactura(Factura factura) {
        Long cantidadFProd;
        Long cantidadFProm = 0l;
        Long cantidad;
        try {
            Query query1 = getEntityManager().createQuery("SELECT SUM(fp.unidadesVenta) + SUM(fp.unidadesBonificacion) FROM FacturaProducto fp WHERE fp.factura.id = :fact");
            query1.setParameter("fact", factura.getId());
            cantidadFProd = (Long) query1.getSingleResult();
            System.out.println("Cantidad: " + cantidadFProd);

            if (cantidadFProd == null) {
                cantidadFProd = 0l;
            }

            Query query2 = getEntityManager().createQuery("SELECT fp FROM FacturaPromocion fp WHERE fp.factura.id = :fac");
            query2.setParameter("fac", factura.getId());
            List<FacturaPromocion> facturaPromociones = query2.getResultList();

            for (FacturaPromocion fp : facturaPromociones) {

                Query query3 = getEntityManager().createQuery("SELECT pp FROM PromocionProducto pp WHERE pp.promocion.id = :prom");
                query3.setParameter("prom", fp.getPromocion().getId());
                List<PromocionProducto> promocionProductos = query3.getResultList();

                for (PromocionProducto pp : promocionProductos) {
                    cantidadFProm += (fp.getUnidadesVenta() + fp.getUnidadesBonificacion()) * pp.getCantidad();
                }
            }
            cantidad = cantidadFProd + cantidadFProm;
        } catch (Exception e) {
            cantidad = null;
            e.printStackTrace();
        }
        return cantidad;
    }

    public Long getLastCodigoByEntity(Object entidad) {
        Long lastCodigo = 0l;
        String entityName = entidad.getClass().getSimpleName();
        try {
            String query = "SELECT e.codigo FROM " + entityName + " e ORDER BY e.id DESC";
            System.out.println("QUERY-> " + query);
            Query q = getEntityManager().createQuery(query);
            q.setMaxResults(1);
            lastCodigo = Long.valueOf((String) q.getSingleResult());
            return lastCodigo;
        } catch (Exception e) {
            System.out.println("No se encontró el último codigo para " + entityName + ", se pone 001");
//        e.printStackTrace();
        }
        return lastCodigo;
    }

    public boolean anularPago(Pago pago) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Factura facturaTMP = getEntityManager().find(Factura.class, pago.getFactura().getId());

            Query query1 = getEntityManager().createQuery("SELECT pd FROM PagoDetalle pd WHERE pd.pago.id = :pago");
            query1.setParameter("pago", pago.getId());
            List<PagoDetalle> pagoDetalles = query1.getResultList();

            List<PagoPublicidad> pagosPublicidad = new ArrayList<>();
            Query query2 = getEntityManager().createQuery("SELECT pp FROM PagoPublicidad pp WHERE pp.pagoDetalle.id = :pd");
            for (PagoDetalle pd : pagoDetalles) {
                query2.setParameter("pd", pd.getId());
                pagosPublicidad.addAll(query2.getResultList());
            }

            for (PagoPublicidad pp : pagosPublicidad) {
                PagoPublicidad ppTMP = getEntityManager().find(PagoPublicidad.class, pp.getId());
                getEntityManager().remove(ppTMP);
            }

            for (PagoDetalle pd : pagoDetalles) {
                PagoDetalle pdTMP = getEntityManager().find(PagoDetalle.class, pd.getId());
                getEntityManager().remove(pdTMP);
            }

            Pago pagoTMP = getEntityManager().find(Pago.class, pago.getId());
            pagoTMP.setEstado(EstadoPago.ANULADO.getValor());
            getEntityManager().merge(pagoTMP);

            updateEstadosFactura(facturaTMP);
            getEntityManager().merge(facturaTMP);

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
            }
        }
        return false;
    }

    public boolean updatePago(Pago pago, List<TipoPagoHelper> tipoPagosCrear, List<TipoPagoHelper> tipoPagosEditar, List<TipoPagoHelper> tipoPagosEliminar) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Factura facturaTMP = getEntityManager().find(Factura.class, pago.getFactura().getId());

            Pago pagoTMP = getEntityManager().find(Pago.class, pago.getId());
            pagoTMP.setCuenta(pago.getCuenta());
            pagoTMP.setDolar(pago.getFactura().getDolar());
            pagoTMP.setEstado(pago.getEstado());
            pagoTMP.setFactura(facturaTMP);
            pagoTMP.setFecha(pago.getFecha());
            pagoTMP.setFormaPago(pago.getFormaPago());
            pagoTMP.setNumeroCheque(pago.getNumeroCheque());
            pagoTMP.setObservaciones(pago.getObservaciones());
            pagoTMP.setOrdenPago(pago.getOrdenPago());
            pagoTMP.setRelacionFactura(pago.getRelacionFactura());
            pagoTMP.setUsuario(pago.getUsuario());
            pagoTMP.setValorTotal(pago.getValorTotal());
            getEntityManager().merge(pagoTMP);

            for (TipoPagoHelper tph : tipoPagosCrear) {
                PagoDetalle pd = new PagoDetalle();
                pd.setPago(pagoTMP);
                pd.setTipo(tph.getTipo());
                pd.setValor(tph.getValor());
                getEntityManager().persist(pd);

                if (tph.getTipoPublicidad() != null) {
                    PagoPublicidad pp = new PagoPublicidad();
                    pp.setPagoDetalle(pd);
                    pp.setTipo(tph.getTipoPublicidad());
                    getEntityManager().persist(pp);
                }
            }

            Query query = getEntityManager().createQuery("SELECT pp FROM PagoPublicidad pp WHERE pp.pagoDetalle.id = :pd");

            for (TipoPagoHelper tph : tipoPagosEditar) {
                PagoDetalle pd = getEntityManager().find(PagoDetalle.class, tph.getIdObject());
                pd.setTipo(tph.getTipo());
                pd.setValor(tph.getValor());
                getEntityManager().merge(pd);

                if (tph.getTipoPublicidad() != null) {
                    query.setParameter("pd", pd.getId());
                    PagoPublicidad pp = (PagoPublicidad) query.getSingleResult();
                    pp.setTipo(tph.getTipoPublicidad());
                    getEntityManager().merge(pp);
                }
            }

            for (TipoPagoHelper tph : tipoPagosEliminar) {
                PagoDetalle pd = getEntityManager().find(PagoDetalle.class, tph.getIdObject());

                if (tph.getTipoPublicidad() != null) {
                    query.setParameter("pd", pd.getId());
                    PagoPublicidad pp = (PagoPublicidad) query.getSingleResult();
                    getEntityManager().remove(pp);
                }

                getEntityManager().remove(pd);
            }

            updateEstadosFactura(facturaTMP);
            getEntityManager().merge(facturaTMP);

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
            }
        }
        return false;
    }
    
    public boolean createPrecioProducto(ListaPrecio listaPrecio, List<PrecioProducto> precioProductos) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            ListaPrecio lp = new ListaPrecio();
            lp.setCodigo(listaPrecio.getCodigo());
            lp.setNombre(listaPrecio.getNombre());

            getEntityManager().persist(lp);

            for (PrecioProducto pp : precioProductos) {
                pp.setId(null);
                pp.setPrecio(pp.getPrecio());
                pp.setPrecioUSD(pp.getPrecioUSD());
                pp.setProducto(pp.getProducto());
                pp.setListaPrecio(lp);
                getEntityManager().merge(pp);
            }

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }
    
    public boolean deleteListaPrecio(ListaPrecio listaPrecio) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();

            Query query1 = getEntityManager().createQuery("DELETE FROM PrecioProducto pp WHERE pp.listaPrecio.id = :lp");
            query1.setParameter("lp", listaPrecio.getId());
            query1.executeUpdate();

            Query query2 = getEntityManager().createQuery("DELETE FROM ListaPrecio lp WHERE lp.id = :lp");
            query2.setParameter("lp", listaPrecio.getId());
            query2.executeUpdate();

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }
    
    public boolean updateListaPrecio(ListaPrecio listaPrecio, List<PrecioProducto> precioProductosGuardar, List<PrecioProducto> precioProductosEliminar) {
        userTransaction = sessionContext.getUserTransaction();
        try {
            userTransaction.begin();
            getEntityManager().merge(listaPrecio);

            for (PrecioProducto pp : precioProductosEliminar) {
                PrecioProducto ipTMP = getEntityManager().find(PrecioProducto.class, pp.getId());
                getEntityManager().remove(ipTMP);
            }

            for (PrecioProducto pp : precioProductosGuardar) {
                pp.setId(null);
                pp.setListaPrecio(listaPrecio);
                getEntityManager().merge(pp);
            }

            userTransaction.commit();
            return true;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            try {
                System.out.println("======>");
                e.printStackTrace();
                System.out.println("<======");
                userTransaction.rollback();
            } catch (IllegalStateException | SecurityException | SystemException es) {
                System.out.println("======>");
                es.printStackTrace();
                System.out.println("<======");
            }
        }
        return false;
    }
}
