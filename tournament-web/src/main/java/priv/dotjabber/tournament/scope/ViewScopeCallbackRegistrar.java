package priv.dotjabber.tournament.scope;

import javax.faces.component.UIViewRoot;
import javax.faces.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public class ViewScopeCallbackRegistrar implements ViewMapListener {

    @Override
    @SuppressWarnings("unchecked")
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        if(event instanceof PostConstructViewMapEvent) {
            PostConstructViewMapEvent viewMapEvent = (PostConstructViewMapEvent)event;

            UIViewRoot viewRoot = (UIViewRoot) viewMapEvent.getComponent();
            viewRoot.getViewMap().put(ViewScope.VIEW_SCOPE_CALLBACKS, new HashMap<>());

        } else if(event instanceof PreDestroyViewMapEvent) {
            PreDestroyViewMapEvent viewMapEvent = (PreDestroyViewMapEvent)event;

            UIViewRoot viewRoot = (UIViewRoot)viewMapEvent.getComponent();
            Map<String, Runnable> callbacks = (Map<String, Runnable>) viewRoot.getViewMap().get(ViewScope.VIEW_SCOPE_CALLBACKS);

            if(callbacks != null) {
                callbacks.values().forEach(java.lang.Runnable::run);
                callbacks.clear();
            }
        }
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return source instanceof UIViewRoot;
    }
}