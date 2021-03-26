package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services

	// pattern singleton
	private static ServiceRegistry instance;
	static {
		instance = new ServiceRegistry();
	}
	
	public static ServiceRegistry getInstance() {
		return instance;
	}
	
	private List<Class<? extends Service>> servicesClasses;

	private ServiceRegistry() {
		servicesClasses = new ArrayList<>();

		// decorator qui va rendre la liste thread-safe
		servicesClasses = Collections.synchronizedList(servicesClasses);
	}

	
	public void addService(Class<? extends Service> service) throws Exception {

		// vérifier la conformité
		// si non conforme --> exception avec message clair
		validationService(service);
		// si conforme, ajout a la listes
		servicesClasses.add(service);
	}

	
	public Class<? extends Service> getServiceClass(int numService) {
		return servicesClasses.get(numService - 1);
	}

	
// liste les activités présentes (on a besoin d'ajouter un synchronized car on itère)
	public String toStringue() {
		String result;
		synchronized (servicesClasses) {
			result = "Activités présentes :##";
			for (Class<? extends Service> c : servicesClasses) {
				result += c.getSimpleName() + "##";
			}
		}
		return result;
	}

	
//tester la validité de la classe
	private void validationService(Class<? extends Service> classe) throws Exception {

		if (!Modifier.isPublic(classe.getModifiers()))
			throw new Exception("La classe doit etre publique");
		
		if (Modifier.isAbstract(classe.getModifiers()))
			throw new Exception("La classe ne peut pas être abstract");

		List<Class<?>> implementations = Arrays.asList(classe.getInterfaces());
		if (!implementations.contains(Service.class)) {
			throw new Exception("La classe doit implementer bri.Service");
		}

		try {
			Constructor<?> constr = classe.getDeclaredConstructor(Socket.class);
			if (!Modifier.isPublic(constr.getModifiers())) {
				throw new Exception("La classe doit contenir un constructeur public");
			}
			if (constr.getExceptionTypes().length > 0) {
				throw new Exception("Le constructeur ne doit pas renvoyer d'exceptions");
			}
		} catch (NoSuchMethodException | SecurityException e) {
			throw new Exception("La classe doit contenir un constructeur avec un parametre Socket");
		}

		try {
			Method m = classe.getMethod("toStringue");
			if (m.getExceptionTypes().length > 0) {
				throw new Exception("Cette méthode ne doit pas renvoyer d'exceptions");
			}
			if (!Modifier.isPublic(m.getModifiers()))
				throw new Exception("Cette methode doit etre publique");
			if (!Modifier.isStatic(m.getModifiers()))
				throw new Exception("Cette methode doit etre statique");

		} catch (NoSuchMethodException | SecurityException e) {
			throw new Exception("Il manque la methode toStringue");
		}

		Field[] attributs = classe.getDeclaredFields();
		boolean exists = false;
		for (Field a : attributs) {
			String nom = a.getType().getSimpleName();
			if (nom.equals("Socket")) {
				exists = true;
				if (!Modifier.isFinal(a.getModifiers()) || !Modifier.isPrivate(a.getModifiers())) {
					throw new Exception("Cet attribut doit etre private et final");
				}
				break;
			}
		}
		if (!exists)
			throw new Exception("Il manque l'attribut de type Socket");
	}

}