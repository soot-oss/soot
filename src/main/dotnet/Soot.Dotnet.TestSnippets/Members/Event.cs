/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Thomas Schmeiduch
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

using System;

namespace Soot.Dotnet.TestSnippets.Members
{
    public class Event
    {
        public delegate void MyEventHandler(object sender, EventArgs e);

        // Declare the event.
        public event MyEventHandler MyEvent;

        protected virtual void OnMyEvent(EventArgs args)
        {
            MyEvent?.Invoke(this, args);
        }
    }

    internal class Test
    {
        public event EventHandler MyEvent; /*
        {
            add => Console.WriteLine ("add operation");

            remove => Console.WriteLine ("remove operation");
        }       */

        private static void Lol()
        {
            var t = new Test();

            t.MyEvent += t.DoNothing;
            t.MyEvent -= null;
        }

        private void DoNothing(object sender, EventArgs e)
        {
        }

        protected virtual void OnMyEvent()
        {
            MyEvent?.Invoke(this, EventArgs.Empty);
        }
    }
}