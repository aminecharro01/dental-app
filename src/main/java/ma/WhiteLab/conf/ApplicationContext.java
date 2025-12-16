package ma.WhiteLab.conf;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ApplicationContext {

    private final Map<String, Object> beansByName = new HashMap<>();
    private final Map<Class<?>, Object> beansByClass = new HashMap<>();
    private final Properties props = new Properties();

    public ApplicationContext(String configPath) {
        try {
            loadBeans(configPath);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement des beans : " + e.getMessage(), e);
        }
    }

    /**
     * Charge tous les beans définis dans bean.props
     */
    private void loadBeans(String configPath) throws Exception {

        InputStream file = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(configPath);

        if (file == null) {
            throw new RuntimeException("Fichier introuvable : " + configPath);
        }

        props.load(file);

        // 1. Instanciation de chaque classe dans bean.props
        for (String beanName : props.stringPropertyNames()) {

            if (!beansByName.containsKey(beanName)) {
                Object instance = createInstance(props.getProperty(beanName));
                registerBean(beanName, instance);
            }
        }

        // 2. Injection des dépendances (si tu ajoutes @Autowired plus tard)
        for (Object bean : beansByName.values()) {
            injectDependencies(bean);
        }
    }

    /**
     * Enregistre un bean dans les maps
     */
    private void registerBean(String name, Object instance) {
        beansByName.put(name, instance);
        beansByClass.put(instance.getClass(), instance);
    }

    /**
     * Crée un objet depuis son nom de classe
     */
    private Object createInstance(String className) throws Exception {

        Class<?> clazz = Class.forName(className);

        // 🔥 Correction importante : choisir le constructeur le plus "long"
        Constructor<?> bestConstructor = null;

        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            if (bestConstructor == null ||
                    c.getParameterCount() > bestConstructor.getParameterCount()) {
                bestConstructor = c;
            }
        }

        bestConstructor.setAccessible(true);

        // Aucun paramètre → simple newInstance()
        if (bestConstructor.getParameterCount() == 0) {
            return bestConstructor.newInstance();
        }

        // Paramètres → injection auto
        Object[] params = new Object[bestConstructor.getParameterCount()];
        Class<?>[] types = bestConstructor.getParameterTypes();

        for (int i = 0; i < types.length; i++) {
            params[i] = getBean(types[i]);

            if (params[i] == null) {
                // 🔥 Correction : si dépendance non trouvée → on tente de la créer
                params[i] = createMissingDependency(types[i]);
            }
        }

        return bestConstructor.newInstance(params);
    }

    /**
     * Créer une dépendance absente du contexte
     */
    private Object createMissingDependency(Class<?> type) throws Exception {

        // Trouver un bean dans props qui correspond
        for (String beanName : props.stringPropertyNames()) {
            String className = props.getProperty(beanName);
            Class<?> clazz = Class.forName(className);

            if (type.isAssignableFrom(clazz)) {
                Object instance = createInstance(className);
                registerBean(beanName, instance);
                return instance;
            }
        }

        throw new RuntimeException(
                "Impossible de résoudre la dépendance automatiquement : " + type.getName()
        );
    }

    /**
     * Inject dependencies later (setter / field if needed)
     */
    private void injectDependencies(Object bean) {
        // Tu peux rajouter @Autowired + scanning ici plus tard.
    }

    // ======================================
    //              PUBLIC API
    // ======================================

    public Object getBean(String name) {
        return beansByName.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {

        // Exact match
        if (beansByClass.containsKey(clazz)) {
            return (T) beansByClass.get(clazz);
        }

        // Interface / inheritance match
        for (Object bean : beansByName.values()) {
            if (clazz.isAssignableFrom(bean.getClass())) {
                return (T) bean;
            }
        }

        return null;
    }
}
