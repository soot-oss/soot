package soot.asm;

import java.util.List;

import soot.Unit;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.tagkit.Host;
import soot.tagkit.Tag;
import soot.util.Switch;

/**
 * A psuedo unit containing different units.
 */
@SuppressWarnings("serial")
class UnitContainer implements Unit {
	
	final Unit[] units;
	
	UnitContainer(Unit... units) {
		this.units = units;
	}

	@Override
	public Object clone() {
		throw new UnsupportedOperationException();
	}
	
	public void apply(Switch sw) {
		throw new UnsupportedOperationException();
	}

	public List<Tag> getTags() {
		throw new UnsupportedOperationException();
	}

	public Tag getTag(String aName) {
		throw new UnsupportedOperationException();
	}

	public void addTag(Tag t) {
		throw new UnsupportedOperationException();
	}

	public void removeTag(String name) {
		throw new UnsupportedOperationException();
	}

	public boolean hasTag(String aName) {
		throw new UnsupportedOperationException();
	}

	public void removeAllTags() {
		throw new UnsupportedOperationException();
	}

	public void addAllTagsOf(Host h) {
		throw new UnsupportedOperationException();
	}

	public List<ValueBox> getUseBoxes() {
		throw new UnsupportedOperationException();
	}

	public List<ValueBox> getDefBoxes() {
		throw new UnsupportedOperationException();
	}

	public List<UnitBox> getUnitBoxes() {
		throw new UnsupportedOperationException();
	}

	public List<UnitBox> getBoxesPointingToThis() {
		throw new UnsupportedOperationException();
	}

	public void addBoxPointingToThis(UnitBox b) {
		throw new UnsupportedOperationException();
	}

	public void removeBoxPointingToThis(UnitBox b) {
		throw new UnsupportedOperationException();
	}

	public void clearUnitBoxes() {
		throw new UnsupportedOperationException();
	}

	public List<ValueBox> getUseAndDefBoxes() {
		throw new UnsupportedOperationException();
	}

	public boolean fallsThrough() {
		throw new UnsupportedOperationException();
	}
	public boolean branches() {
		throw new UnsupportedOperationException();
	}

	public void toString(UnitPrinter up) {
		throw new UnsupportedOperationException();
	}

	public void redirectJumpsToThisTo(Unit newLocation) {
		throw new UnsupportedOperationException();
	}
}