package jp.util;

import java.util.List;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.primefaces.context.RequestContext;

public class JsfUtil {

    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }

    public static boolean isValidationFailed() {
        return FacesContext.getCurrentInstance().isValidationFailed();
    }

    public static void addErrorMessage(Exception ex, String defaultMsg) {
        String msg = ex.getLocalizedMessage();
        if (msg != null && msg.length() > 0) {
            addErrorMessage(msg);
        } else {
            addErrorMessage(defaultMsg);
        }
    }

    public static void addErrorMessages(List<String> messages) {
        for (String message : messages) {
            addErrorMessage(message);
        }
    }

    public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static void addSuccessMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }

    public static void addWarnMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public static String getRequestParameter(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
    }

    public static Object getObjectFromRequestParameter(String requestParameterName, Converter converter, UIComponent component) {
        String theId = JsfUtil.getRequestParameter(requestParameterName);
        return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
    }

    public static void redirect(String url) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);

        try {
            context.getExternalContext().redirect(url);
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Ocurrio un error durante la redireccion a " + url);
        }

    }

    public static enum PersistAction {

        CREATE,
        DELETE,
        UPDATE
    }

    public static String getMessageBundle(String msg) {
        return ResourceBundle.getBundle("languages/Bundle").getString(msg);
    }

    public static String getMessageBundle(String[] msgs) {
        String msgTMP = "";
        for (String msg : msgs) {
            msgTMP += " " + ResourceBundle.getBundle("languages/Bundle").getString(msg);
        }
        return msgTMP.trim();
    }

    public static void updateComponent(String component) {
        RequestContext.getCurrentInstance().update(component);
    }

    /**
     *
     * @param nombreJasper nombre del .jasper
     * @return retorna la ruta del .jasper
     */
    public static String getRutaReporte(String nombreJasper) {
        ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        return context.getRealPath("/resources/reportes/" + nombreJasper);
    }

    public static String cutText(String text) {
        if (text.length() > 38) {
            return text.substring(0, 38).concat("...");
        } else {
            return text;
        }
    }
    
    public static String rellenar(String cadenaInicial, String cadenaRelleno, int tamanoFinal, boolean rellenarAlaDerecha) {
        try {
            if (cadenaInicial.length() == tamanoFinal) {

            } else if (cadenaInicial.length() > tamanoFinal) {
                if (rellenarAlaDerecha) {
                    cadenaInicial = cadenaInicial.substring(0, tamanoFinal);
                } else {
                    int indice = cadenaInicial.length() - tamanoFinal;
                    cadenaInicial = cadenaInicial.substring(indice, cadenaInicial.length());
                }
            } else {
                while (cadenaInicial.length() < tamanoFinal) {
                    if (rellenarAlaDerecha) {
                        cadenaInicial = cadenaInicial.concat(cadenaRelleno);
                    } else {
                        cadenaInicial = cadenaRelleno.concat(cadenaInicial);
                    }
                }
                cadenaInicial = rellenar(cadenaInicial, cadenaRelleno, tamanoFinal, rellenarAlaDerecha);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cadenaInicial;
    }
}
