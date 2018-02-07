package soot.jbco.util;

import soot.ClassMember;
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

    /**
     * Returns true if the class member is visible from code in the provided class.
     * Phantom superclasses and interfaces are ignored.
     *
     * @param from   any class from which class member visibility/availability should be checked
     * @param member any class member
     * @return returns {@code true} if the {@code member } is visible from code in the provided class {@code from}.
     */
    public static boolean isVisible(SootClass from, ClassMember member) {
        from.checkLevel(SootClass.HIERARCHY);
        member.getDeclaringClass().checkLevel(SootClass.HIERARCHY);

        Hierarchy hierarchy = Scene.v().getActiveHierarchy();
        if (member.isProtected()) {
            List<SootClass> superclasses = hierarchy.getSuperclassesOfIncluding(from);
            return superclasses.contains(member.getDeclaringClass())
                    || from.getJavaPackageName().equals(member.getDeclaringClass().getJavaPackageName());
        }
        return hierarchy.isVisible(from, member);
    }

}
