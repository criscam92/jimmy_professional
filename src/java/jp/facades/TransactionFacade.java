package jp.facades;

import java.util.List;
import javax.annotation.Resource;
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
import jp.entidades.Pago;
import jp.entidades.PagoDetalle;
import jp.entidades.PagoDevolucion;
import jp.entidades.Promocion;
import jp.entidades.PromocionProducto;
import jp.entidades.Parametros;
import jp.entidades.Producto;
import jp.entidades.ProductoHelper;
import jp.entidades.ProductoPromocionHelper;
import jp.entidades.Visita;
import jp.entidades.VisitaProducto;
import jp.util.EstadoPagoFactura;
import jp.util.EstadoVisita;

@Stateful
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class TransactionFacade {

    @PersistenceContext(unitName = "jimmy_professionalPU", type = PersistenceContextType.EXTENDED)
    private EntityManager em;
    private UserTransaction userTransaction;

    @Resource
    private SessionContext sessionContext;

    protected EntityManager getEntityManager() {
        return em;
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
            parametroTMP.setPorcentajeComision(p.getPorcentajeComision());
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
            getEntityManager().merge(facturaTMP);

            for (ProductoPromocionHelper pph : objects) {
                if (pph.isProducto()) {
                    FacturaProducto fp = new FacturaProducto();
                    fp.setFactura(facturaTMP);
                    fp.setId(null);
                    fp.setPrecio(pph.getPrecio());
                    fp.setProducto((Producto) pph.getProductoPromocion());
                    fp.setUnidadesBonificacion(pph.getUnidadesBonificacion());
                    fp.setUnidadesVenta(pph.getUnidadesVenta());
                    getEntityManager().merge(fp);
                } else {
                    FacturaPromocion fp = new FacturaPromocion();
                    fp.setFactura(facturaTMP);
                    fp.setId(null);
                    fp.setPrecio(pph.getPrecio());
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
            getEntityManager().merge(despachoFacturaTMP);

            boolean facturaRealizada = true;
            for (ProductoHelper ph : productoHelpers) {
                if (facturaRealizada && (ph.getCantidadFacturada() == ph.getCantidadADespachar())) {
                    facturaRealizada = false;
                }
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
            facturaTMP.setEstado(facturaRealizada ? EstadoPagoFactura.REALIZADA.getValor() : EstadoPagoFactura.PENDIENTE.getValor());
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

}
