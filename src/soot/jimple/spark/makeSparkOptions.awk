#!/usr/bin/awk -f
#/* Soot - a J*va Optimization Framework
# * Copyright (C) 2002 Ondrej Lhotak
# *
# * This library is free software; you can redistribute it and/or
# * modify it under the terms of the GNU Lesser General Public
# * License as published by the Free Software Foundation; either
# * version 2.1 of the License, or (at your option) any later version.
# *
# * This library is distributed in the hope that it will be useful,
# * but WITHOUT ANY WARRANTY; without even the implied warranty of
# * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# * Lesser General Public License for more details.
# *
# * You should have received a copy of the GNU Lesser General Public
# * License along with this library; if not, write to the
# * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
# * Boston, MA 02111-1307, USA.
# */

BEGIN {
    print "/* Soot - a J*va Optimization Framework"
    print " * Copyright (C) 2002 Ondrej Lhotak"
    print " *"
    print " * This library is free software; you can redistribute it and/or"
    print " * modify it under the terms of the GNU Lesser General Public"
    print " * License as published by the Free Software Foundation; either"
    print " * version 2.1 of the License, or (at your option) any later version."
    print " *"
    print " * This library is distributed in the hope that it will be useful,"
    print " * but WITHOUT ANY WARRANTY; without even the implied warranty of"
    print " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU"
    print " * Lesser General Public License for more details."
    print " *"
    print " * You should have received a copy of the GNU Lesser General Public"
    print " * License along with this library; if not, write to the"
    print " * Free Software Foundation, Inc., 59 Temple Place - Suite 330,"
    print " * Boston, MA 02111-1307, USA."
    print " */"
    print ""
    print "/* THIS FILE IS AUTO-GENERATED FROM options. DO NOT MODIFY */"
    print ""
    print ""
    print ""
    print "package soot.jimple.spark;"
    print "import java.util.*;"
    print "import soot.PackManager;"
    print ""
    print "/** Various options regulating the functioning of Spark."
    print " * @author Ondrej Lhotak"
    print " */"
    print "public class SparkOptions {"
    print "    public SparkOptions( Map options ) {"
    print "        this.options = options;"
    print "    }"
    print ""
}
END {
    print "    public static String getDeclaredOptions() {"
    print "        return"
    print "        \"" declaredopts "\";"
    print "    }"

    print "    public static String getDefaultOptions() {"
    print "        return"
    print "        \"" defaultopts "\";"
    print "    }"
    print ""
    print "    protected Map options;"
    print "}"
    print ""
}
/^SECTION/ {
    gsub( "^SECTION ","" );
    print "";
    print "/*********************************************************************";
    print "*** "$0;
    print "*********************************************************************/";
    print "";
}
/^BOPT/ {
    option = $2;
    getline default;
    print "    /**";
    while( 1 ) {
        getline comment;
        if( comment == "END" ) break;
        print "     * " comment;
    }
    print "     * Default value is " default;
    print "     */"
    print "    public boolean " option "() {"
    print "        return Options.getBoolean( options, \""option"\" );"
    print "    }";
    print "";
    declaredopts = declaredopts " " option;
    defaultopts = defaultopts " " option ":" default;
}

/^MOPT/ {
    option = $2;
    i = 3;
    delete value;
    while( $i != "" ) {
        value[nv++] = $i;
        i++;
    }
    getline default;
    print "    /**";
    while( 1 ) {
        getline comment;
        if( comment == "END" ) break;
        print "     * " comment;
    }
    print "     * Default value is " default;
    print "     */"
    print "    public void "option"( Switch_"option" sw ) {"
    print "        String s = Options.getString( options, \""option"\" );"
    print "        if( false );"
    for (v in value) {
        print "        else if( s.equalsIgnoreCase(\""value[v]"\") ) sw.case_"value[v]"();";
    }
    print "        else throw new RuntimeException( \"Invalid value \\\"\"+s+\"\\\" of option "option"\" );";
    print "    }";
    print "    public static abstract class Switch_"option" {";
    for (v in value) {
        print "        public abstract void case_"value[v]"();"
    }
    print "    }"
    print "";
    declaredopts = declaredopts " " option;
    defaultopts = defaultopts " " option ":" default;
}

