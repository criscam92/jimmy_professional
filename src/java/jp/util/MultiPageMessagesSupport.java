package com.s4.pqrv.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
 
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class MultiPageMessagesSupport implements PhaseListener {
 
    private static final long serialVersionUID = 3328743500652081238L;

    private static final String sessionToken = "MULTI_PAGE_MESSAGES_SUPPORT";

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
            FacesContext facesContext = event.getFacesContext();
            restoreMessages(facesContext);
        }
    }
    
    @Override
    public void afterPhase(PhaseEvent event) {
        if (event.getPhaseId() == PhaseId.APPLY_REQUEST_VALUES
                || event.getPhaseId() == PhaseId.PROCESS_VALIDATIONS
                || event.getPhaseId() == PhaseId.INVOKE_APPLICATION) {
            FacesContext facesContext = event.getFacesContext();
            saveMessages(facesContext);
        }
    }
    
    @SuppressWarnings("unchecked")
    private int saveMessages(FacesContext facesContext) {
        Set<FacesMessage> messages = new HashSet<>();
        for (Iterator i = facesContext.getMessages(null); i.hasNext();) {
            FacesMessage msg = (FacesMessage) i.next();
            messages.add(msg);
            i.remove();
        }
        
        if (messages.isEmpty())
            return 0;
        Map sessionMap = facesContext.getExternalContext().getSessionMap();        
        Set<FacesMessage> existingMessages = (Set<FacesMessage>) sessionMap
                .get(sessionToken);
        if (existingMessages != null) {
            existingMessages.addAll(messages);
        } else {
            sessionMap.put(sessionToken, messages);
        }
        return messages.size();
    }

    @SuppressWarnings("unchecked")
    private int restoreMessages(FacesContext facesContext) {
        Map sessionMap = facesContext.getExternalContext().getSessionMap();
        Set<FacesMessage> messages = (Set<FacesMessage>) sessionMap
                .remove(sessionToken);
        if (messages == null)
            return 0;
        int restoredCount = messages.size();
 
        Set<FacesMessage> facesContextMessages = new HashSet<>();
        for (Iterator i = facesContext.getMessages(null); i.hasNext();) {
            FacesMessage msg = (FacesMessage) i.next();
            facesContextMessages.add(msg);
            i.remove();
        }
 
        for (FacesMessage facesMessage : messages) {
            if (!facesContextMessages.contains(facesMessage))
                facesContext.addMessage(null, facesMessage);
        }
        return restoredCount;
    }
}