#include "fdd.h"
#include "myfdd.h"
#include "bdd.h"
#include "kernel.h"
#include <stdio.h>

static void print_em( int* set, int out[], int* out_index, int pos, int val, int* vars ) {
    if( pos < 0 ) {
        out[(*out_index)++] = val;
    } else {
        if( ( set[vars[pos]] & 1 ) == 0 ) 
                print_em( set, out, out_index, pos-1, val+(1<<pos), vars );
        if( ( set[vars[pos]] & 2 ) == 0 ) 
                print_em( set, out, out_index, pos-1, val, vars );
    }
}
static void fdd_allsat_rec( BDD r, int out[], int* out_index, int* set, int var ) {
   int n,m,i;
   int used = 0;
   int *vars;
   int *binval;
   int ok, first;
   
   if (r == 0)
      return;
   else
   if (r == 1) {
       print_em( set, out, out_index, domain[var].binsize-1, 0, domain[var].ivar );
   }
   else {
       set[bddlevel2var[LEVEL(r)]] = 1;
       fdd_allsat_rec(LOW(r), out, out_index, set, var);
       
       set[bddlevel2var[LEVEL(r)]] = 2;
       fdd_allsat_rec(HIGH(r), out, out_index, set, var);

       set[bddlevel2var[LEVEL(r)]] = 0;
   }
}

int fdd_allsat( BDD r, int var, int out[] ) {
    int index=0;
    int *set;
    set = (int *)malloc(sizeof(int)*bddvarnum);
    if (set == NULL) {
        bdd_error(BDD_MEMORY);
        return;
    }
    memset(set, 0, sizeof(int) * bddvarnum);
    fdd_allsat_rec(r, out, &index, set, var);
    free(set);
    return index;
}

static int isfddvar( int bdd_var, int fdd_var ) {
    int i;
    for( i = 0; i < domain[fdd_var].binsize; i++ )
        if( bdd_var == domain[fdd_var].ivar[i] ) return -1;
    return 0;
}
int fdd_satcount( BDD r, int var ) {
    int ret = fdd_satcount_guts( r, var );
    int i;
    for( i = 0; i < domain[var].binsize; i++ )
        if( bddvar2level[domain[var].ivar[i]] < bddvar2level[LEVEL(r)] ) ret *= 2;
    return ret;
}

int fdd_satcount_guts( BDD r, int var ) {
    int low;
    int high;
    int i;
    if( r == 0 ) return 0;
    if( r == 1 ) return 1;
    low = fdd_satcount_guts( LOW(r), var );
    for( i = LEVEL(r)+1; i < LEVEL(LOW(r)); i++ ) {
        if( isfddvar( bddlevel2var[i], var ) ) low *= 2;
    }
    high = fdd_satcount_guts( HIGH(r), var );
    for( i = LEVEL(r)+1; i < LEVEL(HIGH(r)); i++ ) {
        if( isfddvar( bddlevel2var[i], var ) ) high *= 2;
    }
    return low+high;
}
