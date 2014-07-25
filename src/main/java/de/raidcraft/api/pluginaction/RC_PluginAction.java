package de.raidcraft.api.pluginaction;

import de.raidcraft.RaidCraft;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dragonfire
 */
// TODO: clear on reload
public class RC_PluginAction {

    private static RC_PluginAction INSTANCE = null;
    private Map<Class<? extends PluginAction>, ArrayList<RcPA_Listener>> listener = new HashMap<>();

    private RC_PluginAction() {

    }

    public static RC_PluginAction getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new RC_PluginAction();
        }
        return INSTANCE;
    }

    public void fire(PluginAction action) {

        List<RcPA_Listener> actionListener = listener.get(action.getClass());
        if (actionListener == null) {
            RaidCraft.LOGGER.warning("No Plugin Action registred for: " +
                    action.getClass().getSimpleName());
            return;
        }
        for (RcPA_Listener list : actionListener) {
            list.fireEvent(action);
        }
    }

    public void registerAction(PluginActionListener listener) {
        Method[] methods = listener.getClass().getMethods();
        for(Method method : methods) {
            if(method.getAnnotation(RcPluginAction.class) == null) {
                continue;
            }
            if(method.getParameterCount() != 1) {
                RaidCraft.LOGGER.warning(listener.getClass().getName() + "@" +
                        method.getName() + " has no ore more than one params");
                continue;
            }
            Class eventClass = method.getParameterTypes()[0];
            if(!PluginAction.class.isAssignableFrom(eventClass)) {
                RaidCraft.LOGGER.warning(listener.getClass().getName() + "@" +
                        method.getName() + " param extends not PluginAction");
                continue;
            }
            addListener(eventClass, new RcPA_Listener(method, listener));
        }
    }

    private void addListener(Class<? extends PluginAction> actionClass,  RcPA_Listener listener) {
        if (!this.listener.containsKey(actionClass)) {
            this.listener.put(actionClass, new ArrayList<>());
        }
        this.listener.get(actionClass).add(listener);
    }


    public class RcPA_Listener {

        public Method method;
        private PluginActionListener listener;

        public RcPA_Listener(Method method, PluginActionListener listener) {

            this.method = method;
            this.listener = listener;
        }

        public void fireEvent(PluginAction action) {
            try {
                method.invoke(listener, action);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // skip InvocationTargetException print
                e.getCause().printStackTrace();
            }
        }
    }
}
