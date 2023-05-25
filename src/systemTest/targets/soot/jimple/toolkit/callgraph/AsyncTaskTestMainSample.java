package soot.jimple.toolkit.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Qidan He
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

import android.os.AsyncTask;

public class AsyncTaskTestMainSample {

    AsyncTask task;
    public void target1() {
        task = new LongOperation().execute("stub");
    }
}

class LongOperation extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}


