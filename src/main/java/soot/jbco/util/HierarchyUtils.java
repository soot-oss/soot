package soot.jbco.util;

import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Utility class for convenient hierarchy checks.
 *
 * @since 07.02.18
 */
public final class HierarchyUtils {

    private HierarchyUtils() {
        throw new IllegalAccessError();
    }

    /**
     * Get whole tree of interfaces on {@code Scene} for class/interface.
     *
     * @param sc class or interface to get all its interfaces
     * @return all interfaces on {@code Scene} for class or interface
     */
    public static List<SootClass> getAllInterfacesOf(SootClass sc) {
        Hierarchy hierarchy = Scene.v().getActiveHierarchy();
        Stream<SootClass> superClassInterfaces = sc.isInterface() ? Stream.empty() : hierarchy.getSuperclassesOf(sc)
                .stream()
                .map(HierarchyUtils::getAllInterfacesOf)
                .flatMap(Collection::stream);
        Stream<SootClass> directInterfaces = Stream.concat(sc.getInterfaces().stream(), sc.getInterfaces().stream()
                .map(HierarchyUtils::getAllInterfacesOf)
                .flatMap(Collection::stream));
        return Stream.concat(superClassInterfaces, directInterfaces).collect(toList());
    }

}
