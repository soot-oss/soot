/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dex.instructions;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jf.dexlib.Code.Instruction;

import soot.Unit;
import soot.dex.DexBody;
import soot.dex.DexType;
import soot.tagkit.Host;
import soot.tagkit.SourceLineNumberTag;

/**
 * This class represents a wrapper around dexlib instruction.
 *
 */
public abstract class DexlibAbstractInstruction {

    protected int lineNumber = -1;

    protected Instruction instruction;
    protected int codeAddress;
    protected Unit beginUnit;
    protected Unit endUnit;

    /**
     * Jimplify this instruction.
     *
     * @param body to jimplify into.
     */
    public abstract void jimplify(DexBody body);

    /**
     * Return the target register that is a copy of the given register.
     *
     * Instructions should override this if they copy register content.
     *
     * @param register the number of the register
     * @return the new register number or -1 if it does not move.
     */
    int movesRegister(int register) {
        return -1;
    }

    /**
     * Return the source register that is moved to the given register.
     *
     * Instructions should override this if they copy register content.
     *
     * @param register the number of the register
     * @return the source register number or -1 if it does not move.
     */
    int movesToRegister(int register) {
        return -1;
    }

    /**
     * Return if the instruction overrides the value in the register.
     *
     * Instructions should override this if they modify the registers.
     *
     * @param register the number of the register
     */
    boolean overridesRegister(int register) {
        return false;
    }

    /**
     * Return if the value in the register is used as a floating point.
     *
     * Instructions that have this context information and may deal with
     * integers or floating points should override this.
     *
     * @param register the number of the register
     * @param body the body containing the instruction
     */
    boolean isUsedAsFloatingPoint(DexBody body, int register) {
        return false;
    }

    /**
     * Return the types that are be introduced by this instruction.
     *
     * Instructions that may introduce types should override this.
     */
    public Set<DexType> introducedTypes() {
        return new HashSet<DexType>();
    }

    /**
     * @param instruction the underlying dexlib instruction
     * @param codeAddress the bytecode address of this instruction
     */
    public DexlibAbstractInstruction(Instruction instruction, int codeAddress) {
        this.instruction = instruction;
        this.codeAddress = codeAddress;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Tag the passed host with this instructions linenumber (if one is set).
     *
     * @param host the host to tag
     */
    protected void tagWithLineNumber(Host host) {
        if (lineNumber != -1)
            host.addTag(new SourceLineNumberTag(lineNumber));
    }

    /**
     * Return the first of the jimple units that represent this instruction.
     *
     */
    public Unit getBeginUnit() {
        return beginUnit;
    }

    /**
     * Return the last of the jimple units that represent this instruction.
     *
     */
    public Unit getEndUnit() {
        return endUnit;
    }

    /**
     * Set the Jimple Unit, that comprises this instruction.
     *
     * Does not override already set units.
     */
    protected void defineBlock(Unit stmt) {
        defineBlock(stmt, stmt);
    }

    /**
     * Set the first and last Jimple Unit, that comprise this instruction.
     *
     * Does not override already set units.
     */
    protected void defineBlock(Unit begin, Unit end) {
        if (beginUnit == null)
            beginUnit = begin;
        if (endUnit == null)
            endUnit = end;
    }

    /**
     * Determine if the value in register is used as a floating point number in future instructions.
     *
     * @param register the register number that may contain a integer or a floating point value
     * @param body the body containing the instruction
     */
    protected boolean willFloat(int register, DexBody body) {
        List<DexlibAbstractInstruction> instructions = body.instructionsAfter(this);
        Set<Integer> usedRegisters = new HashSet<Integer>();
        usedRegisters.add(register);


        for(DexlibAbstractInstruction i : instructions) {
            if (usedRegisters.isEmpty())
                break;

            for (int reg : usedRegisters)
                if (i.isUsedAsFloatingPoint(body, reg))
                    return true;

            // look for obsolete registers
            for (int reg : usedRegisters) {
                if (i.overridesRegister(reg)) {
                    usedRegisters.remove(reg);
                    break;      // there can't be more than one obsolete
                }
            }

            // look for new registers
            for (int reg : usedRegisters) {
                int newRegister = i.movesRegister(reg);
                if (newRegister != -1) {
                    usedRegisters.add(newRegister);
                    break;      // there can't be more than one new
                }
            }
        }

        return false;
    }
}
