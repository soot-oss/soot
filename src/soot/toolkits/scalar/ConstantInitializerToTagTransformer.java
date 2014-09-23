package soot.toolkits.scalar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.G;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;
import soot.tagkit.ConstantValueTag;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;

/**
 * This is the reverse operation of the {@link ConstantValueToInitializerTransformer}.
 * We scan for <clinit> methods that initialize a final field with a constant value
 * and create a {@link ConstantValueTag} from this value. Afterwards, the assignment
 * in the <clinit> method is removed. If <clinit> runs empty, it is deleted as well.
 * 
 * @author Steven Arzt
 */
public class ConstantInitializerToTagTransformer extends SceneTransformer {
	
	public static ConstantInitializerToTagTransformer v() {
		return new ConstantInitializerToTagTransformer();
	}

	@Override
	protected void internalTransform(String phaseName,
			Map<String, String> options) {
		for (SootClass sc : Scene.v().getClasses()) {
			transformClass(sc, false);
		}
	}
	
    /**
     * Transforms the given class, i.e. scans for a <clinit> method and generates
     * new constant value tags for all constant assignments to static final fields. 
     * @param sc The class to transform
     * @param removeAssignments True if the assignments inside the <clinit> method
     * shall be removed, otherwise false
     */
	public void transformClass(SootClass sc, boolean removeAssignments) {
		// If this class has no <clinit> method, we're done
		SootMethod smInit = sc.getMethodByNameUnsafe("<clinit>");
		if (smInit == null)
			return;
		
		Set<SootField> nonConstantFields = new HashSet<SootField>();
		Map<SootField, ConstantValueTag> newTags = new HashMap<SootField, ConstantValueTag>();
		Set<SootField> removeTagList = new HashSet<SootField>(); // in case of mismatch between code/constant table values, constant tags are removed
		
		for (Iterator<Unit> itU = smInit.getActiveBody().getUnits().snapshotIterator();
				itU.hasNext(); ) {
			Unit u = itU.next();
			if (u instanceof AssignStmt) {
				AssignStmt assign = (AssignStmt) u;
				if (assign.getLeftOp() instanceof StaticFieldRef
						&& assign.getRightOp() instanceof Constant) {
					SootField field = ((StaticFieldRef) assign.getLeftOp()).getField();
					if (nonConstantFields.contains(field))
						continue;
					
					if (field.getDeclaringClass().equals(sc)
							&& field.isStatic()
							&& field.isFinal()) {
						// Do we already have a constant value for this field?
						boolean found = false;
						for (Tag t : field.getTags()) {
							if (t instanceof ConstantValueTag) {
								if (checkConstantValue((ConstantValueTag) t, (Constant) assign.getRightOp())) {
									// If we assign the same value we also have in the constant
									// table, we can get rid of the assignment.
									if (removeAssignments)
										itU.remove();
								}
								else {
									G.v().out.println("WARNING: Constant value for field '"+ field +"' mismatch between code ("+ (Constant) assign.getRightOp() +") and constant table ("+ t +")");
									removeTagList.add(field);
								}
								found = true;
								break;
							}
						}
						
						if (!found) {
							// If we already have a different tag for this field,
							// the value is not constant and we do not associate the
							// tags.
							if (!checkConstantValue(newTags.get(field), (Constant) assign.getRightOp())) {
								nonConstantFields.add(field);
								newTags.remove(field);
								removeTagList.add(field);
								continue;
							}
							
							ConstantValueTag newTag = createConstantTagFromValue((Constant) assign.getRightOp());
							if (newTag != null)
								newTags.put(field, newTag);
						}
					}
				} else if (assign.getLeftOp() instanceof StaticFieldRef){
					// a non-constant is assigned to the field
					SootField sf = ((StaticFieldRef)assign.getLeftOp()).getField();
					removeTagList.add(sf);
				}
			}
		}
		
		// Do the actual assignment
		for (Entry<SootField, ConstantValueTag> entry : newTags.entrySet()) {
			SootField field = entry.getKey();
			if (removeTagList.contains(field))
				continue;
			field.addTag(entry.getValue());
		}
		
		if (removeAssignments && !newTags.isEmpty())
			for (Iterator<Unit> itU = smInit.getActiveBody().getUnits().snapshotIterator();
					itU.hasNext(); ) {
				Unit u = itU.next();
				if (u instanceof AssignStmt) {
					AssignStmt assign = (AssignStmt) u;
					if (assign.getLeftOp() instanceof FieldRef)
						if (newTags.containsKey(((FieldRef) assign.getLeftOp()).getField()))
							itU.remove();
				}
			}
		
		// remove constant tags
		for (SootField sf: removeTagList) {
			if (removeTagList.contains(sf)) {
				List<Tag> toRemoveTagList = new ArrayList<Tag>();
				for (Tag t : sf.getTags()) {
					if (t instanceof ConstantValueTag) {
						toRemoveTagList.add(t);
					}
				}
				for (Tag t: toRemoveTagList) {
					sf.getTags().remove(t);
				}
			}
		}
	}

	private ConstantValueTag createConstantTagFromValue(Constant rightOp) {
		if (rightOp instanceof DoubleConstant)
			return new DoubleConstantValueTag(((DoubleConstant) rightOp).value);
		else if (rightOp instanceof FloatConstant)
			return new FloatConstantValueTag(((FloatConstant) rightOp).value);
		else if (rightOp instanceof IntConstant)
			return new IntegerConstantValueTag(((IntConstant) rightOp).value);
		else if (rightOp instanceof LongConstant)
			return new LongConstantValueTag(((LongConstant) rightOp).value);
		else if (rightOp instanceof StringConstant)
			return new StringConstantValueTag(((StringConstant) rightOp).value);
		else
			return null;
	}

	private boolean checkConstantValue(ConstantValueTag t, Constant rightOp) {
		if (t == null || rightOp == null)
			return true;
		
		if (t instanceof DoubleConstantValueTag) {
			if (!(rightOp instanceof DoubleConstant))
				return false;
			return ((DoubleConstantValueTag) t).getDoubleValue() == ((DoubleConstant) rightOp).value;
		}
		else if (t instanceof FloatConstantValueTag) {
			if (!(rightOp instanceof FloatConstant))
				return false;
			return ((FloatConstantValueTag) t).getFloatValue() == ((FloatConstant) rightOp).value;
		}
		else if (t instanceof IntegerConstantValueTag) {
			if (!(rightOp instanceof IntConstant))
				return false;
			return ((IntegerConstantValueTag) t).getIntValue() == ((IntConstant) rightOp).value;
		}
		else if (t instanceof LongConstantValueTag) {
			if (!(rightOp instanceof LongConstant))
				return false;
			return ((LongConstantValueTag) t).getLongValue() == ((LongConstant) rightOp).value;
		}
		else if (t instanceof StringConstantValueTag) {
			if (!(rightOp instanceof StringConstant))
				return false;
			return ((StringConstantValueTag) t).getStringValue().equals(((StringConstant) rightOp).value);
		}
		else
			// We don't know the type, so we assume it's alright
			return true;
	}
	
}
