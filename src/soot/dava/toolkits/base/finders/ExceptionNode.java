/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
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

package soot.dava.toolkits.base.finders;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.G;
import soot.SootClass;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.asg.AugmentedStmtGraph;
import soot.util.IterableSet;

public class ExceptionNode {
	private final IterableSet<AugmentedStmt> body;
	private IterableSet<AugmentedStmt> tryBody, catchBody;
	private boolean dirty;
	private List<AugmentedStmt> exitList;
	private LinkedList<IterableSet<AugmentedStmt>> catchList;
	private SootClass exception;
	private HashMap<IterableSet<AugmentedStmt>, SootClass> catch2except;
	private AugmentedStmt handlerAugmentedStmt;

	public ExceptionNode(IterableSet<AugmentedStmt> tryBody, SootClass exception,
			AugmentedStmt handlerAugmentedStmt) {
		this.tryBody = tryBody;
		this.catchBody = null;
		this.exception = exception;
		this.handlerAugmentedStmt = handlerAugmentedStmt;

		body = new IterableSet<AugmentedStmt>(tryBody);

		dirty = true;
		exitList = null;
		catchList = null;

		catch2except = null;
	}

	public boolean add_TryStmts(Collection<AugmentedStmt> c) {
		for (AugmentedStmt as : c)
			if (add_TryStmt(as) == false)
				return false;

		return true;
	}

	public boolean add_TryStmt(AugmentedStmt as) {
		if ((body.contains(as)) || (tryBody.contains(as)))
			return false;

		body.add(as);
		tryBody.add(as);

		return true;
	}

	public void refresh_CatchBody(ExceptionFinder ef) {
		if (catchBody != null)
			body.removeAll(catchBody);

		catchBody = ef.get_CatchBody(handlerAugmentedStmt);
		body.addAll(catchBody);
	}

	public IterableSet<AugmentedStmt> get_Body() {
		return body;
	}

	public IterableSet<AugmentedStmt> get_TryBody() {
		return tryBody;
	}

	public IterableSet<AugmentedStmt> get_CatchBody() {
		return catchBody;
	}

	public boolean remove(AugmentedStmt as) {
		if (body.contains(as) == false)
			return false;

		if (tryBody.contains(as))
			tryBody.remove(as);
		else if ((catchBody != null) && (catchBody.contains(as))) {
			catchBody.remove(as);
			dirty = true;
		} else
			return false;

		body.remove(as);

		return true;
	}

	public List<AugmentedStmt> get_CatchExits() {
		if (catchBody == null)
			return null;

		if (dirty) {
			exitList = new LinkedList<AugmentedStmt>();
			dirty = false;

			for (AugmentedStmt as : catchBody) {
				for (AugmentedStmt succ : as.bsuccs)
					if (catchBody.contains(succ) == false) {
						exitList.add(as);
						break;
					}
			}
		}

		return exitList;
	}

	public void splitOff_ExceptionNode(IterableSet<AugmentedStmt> newTryBody,
			AugmentedStmtGraph asg, IterableSet<ExceptionNode> enlist) {
		IterableSet<AugmentedStmt> oldTryBody = new IterableSet<AugmentedStmt>();
		oldTryBody.addAll(tryBody);

		IterableSet<AugmentedStmt> oldBody = new IterableSet<AugmentedStmt>();
		oldBody.addAll(body);

		for (AugmentedStmt as : newTryBody) {
			if (remove(as) == false) {

				StringBuffer b = new StringBuffer();
				for (AugmentedStmt auBody : newTryBody)
					b.append("\n" + auBody.toString());
				b.append("\n-");

				for (AugmentedStmt auBody : oldTryBody)
					b.append("\n" + auBody.toString());
				b.append("\n-");

				for (AugmentedStmt auBody : oldBody)
					b.append("\n" + auBody.toString());
				b.append("\n-");

				throw new RuntimeException(
						"Tried to split off a new try body that isn't in the old one.\n"
								+ as + "\n - " + b.toString());
			}
		}

		asg.clone_Body(catchBody);

		AugmentedStmt oldCatchTarget = handlerAugmentedStmt, newCatchTarget = asg
				.get_CloneOf(handlerAugmentedStmt);

		for (AugmentedStmt as : newTryBody) {
			as.remove_CSucc(oldCatchTarget);
			oldCatchTarget.remove_CPred(as);
		}

		for (AugmentedStmt as : tryBody) {
			as.remove_CSucc(newCatchTarget);
			newCatchTarget.remove_CPred(as);
		}

		for (ExceptionNode en : enlist) {
			if (this == en)
				continue;

			if (catchBody.isSupersetOf(en.get_Body())) {

				IterableSet<AugmentedStmt> clonedTryBody = new IterableSet<AugmentedStmt>();

				for (AugmentedStmt au : en.get_TryBody())
					clonedTryBody.add(asg.get_CloneOf(au));

				enlist.addLast(new ExceptionNode(clonedTryBody, en.exception,
						asg.get_CloneOf(en.handlerAugmentedStmt)));
			}
		}

		enlist.addLast(new ExceptionNode(newTryBody, exception, asg
				.get_CloneOf(handlerAugmentedStmt)));

		for (ExceptionNode en : enlist)
			en.refresh_CatchBody(ExceptionFinder.v());

		asg.find_Dominators();
	}

	public void add_CatchBody(ExceptionNode other) {
		if (other.get_CatchList() == null) {
			add_CatchBody(other.get_CatchBody(), other.get_Exception());
			return;
		}

		for (IterableSet<AugmentedStmt> c : other.get_CatchList()) {
			add_CatchBody(c, other.get_Exception(c));
		}
	}

	public void add_CatchBody(IterableSet<AugmentedStmt> newCatchBody, SootClass except) {
		if (catchList == null) {
			catchList = new LinkedList<IterableSet<AugmentedStmt>>();
			catchList.addLast(catchBody);

			catch2except = new HashMap<IterableSet<AugmentedStmt>, SootClass>();
			catch2except.put(catchBody, exception);
		}

		body.addAll(newCatchBody);
		catchList.addLast(newCatchBody);
		catch2except.put(newCatchBody, except);
	}

	public List<IterableSet<AugmentedStmt>> get_CatchList() {
		List<IterableSet<AugmentedStmt>> l = catchList;

		if (l == null) {
			l = new LinkedList<IterableSet<AugmentedStmt>>();
			l.add(catchBody);
		}

		return l;
	}

	public Map<IterableSet<AugmentedStmt>, SootClass> get_ExceptionMap() {
		Map<IterableSet<AugmentedStmt>, SootClass> m = catch2except;

		if (m == null) {
			m = new HashMap<IterableSet<AugmentedStmt>, SootClass>();
			m.put(catchBody, exception);
		}

		return m;
	}

	public SootClass get_Exception() {
		return exception;
	}

	public SootClass get_Exception(IterableSet<AugmentedStmt> catchBody) {
		if (catch2except == null)
			return exception;

		return catch2except.get(catchBody);
	}

	public void dump() {
		G.v().out.println("try {");
		for (AugmentedStmt au : get_TryBody())
			G.v().out.println("\t" + au);
		G.v().out.println("}");

		for (IterableSet<AugmentedStmt> catchBody : get_CatchList()) {
			G.v().out.println("catch " + get_ExceptionMap().get(catchBody)
					+ " {");
			
			for (AugmentedStmt au : catchBody)
				G.v().out.println("\t" + au);
			G.v().out.println("}");

		}
	}
}
