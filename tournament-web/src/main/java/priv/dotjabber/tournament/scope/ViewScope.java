package priv.dotjabber.tournament.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public class ViewScope implements Scope {
    public static final String VIEW_SCOPE_CALLBACKS = "viewScope.callbacks.";

    @Override
    public synchronized Object get(String name, ObjectFactory<?> objectFactory) {
        Object instance = getViewMap().get(name);

        if (instance == null) {
            instance = objectFactory.getObject();
            getViewMap().put(name, instance);
        }

        return instance;
    }

    @Override
    public Object remove(String name) {
        Object instance = getViewMap().remove(name);

        if (instance != null && name != null) {
            getViewMap().remove(ViewScope.VIEW_SCOPE_CALLBACKS + name);
        }

        return instance;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable runnable) {
    }

    @Override
    public Object resolveContextualObject(String name) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }

    private Map<String, Object> getViewMap() {
        return FacesContext.getCurrentInstance().getViewRoot().getViewMap();
    }
}
