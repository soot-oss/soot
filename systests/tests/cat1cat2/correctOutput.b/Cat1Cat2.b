class Cat1Cat2 extends java.lang.Object
{

    public void <init>()
    {
        word r0;

        r0 := @this;
        load.r r0;
        specialinvoke <init>;
        return;
    }

    public static java.lang.Long testCaseHart()
    {
        new java.lang.Long;
        dup1.r;
        staticinvoke f;
        specialinvoke <init>;
        return.r;
    }

    private static long f()
    {
        push 1L;
        return.l;
    }

    private static float g()
    {
        push 1.0F;
        return.f;
    }

    public static void test_dup_x2_cat1(float, float, int)
    {
        word f0, f1, i0;

        f0 := @parameter0;
        f1 := @parameter1;
        i0 := @parameter2;
        load.i i0;
        load.f f0;
        load.f f1;
        load.i i0;
        staticinvoke printStack;
        return;
    }

    public static void test_dup_x2_cat2(long, int)
    {
        dword l0;
        word i1;

        l0 := @parameter0;
        i1 := @parameter1;
        load.i i1;
        load.l l0;
        load.i i1;
        staticinvoke printStack;
        return;
    }

    public static void test_pop2_cat1(int, float, float)
    {
        word i0, f0, f1;

        i0 := @parameter0;
        f0 := @parameter1;
        f1 := @parameter2;
        load.i i0;
        staticinvoke printStack;
        staticinvoke g;
        pop;
        staticinvoke g;
        pop;
        load.i i0;
        staticinvoke printStack;
        return;
    }

    public static void test_pop2_cat2(int, long)
    {
        word i0;
        dword l1;

        i0 := @parameter0;
        l1 := @parameter1;
        load.i i0;
        staticinvoke printStack;
        staticinvoke f;
        pop;
        load.i i0;
        staticinvoke printStack;
        return;
    }

    public static void test_dup2_cat1(int, float, float)
    {
        word i0, f0, f1;

        i0 := @parameter0;
        f0 := @parameter1;
        f1 := @parameter2;
        load.i i0;
        load.f f0;
        load.f f1;
        load.f f0;
        load.f f1;
        staticinvoke printStack;
        return;
    }

    public static void test_dup2_cat2(int, double)
    {
        word i0;
        dword d0;

        i0 := @parameter0;
        d0 := @parameter1;
        load.i i0;
        load.d d0;
        load.d d0;
        staticinvoke printStack;
        return;
    }

    public static void test_dup2_x1_cat1(int, float, float)
    {
        word i0, f0, f1;

        i0 := @parameter0;
        f0 := @parameter1;
        f1 := @parameter2;
        load.f f0;
        load.f f1;
        load.i i0;
        load.f f0;
        load.f f1;
        staticinvoke printStack;
        return;
    }

    public static void test_dup2_x1_cat2(int, double)
    {
        word i0;
        dword d0;

        i0 := @parameter0;
        d0 := @parameter1;
        load.d d0;
        load.i i0;
        load.d d0;
        staticinvoke printStack;
        return;
    }

    public static void test_dup2_x2_cat1cat1(int, float, float, int, int)
    {
        word i0, f0, f1, i1, i2;

        i0 := @parameter0;
        f0 := @parameter1;
        f1 := @parameter2;
        i1 := @parameter3;
        i2 := @parameter4;
        load.i i0;
        load.i i1;
        load.i i2;
        load.f f0;
        load.f f1;
        load.i i1;
        load.i i2;
        staticinvoke printStack;
        return;
    }

    public static void test_dup2_x2_cat1cat2(int, float, float, double)
    {
        word i0, f0, f1;
        dword d0;

        i0 := @parameter0;
        f0 := @parameter1;
        f1 := @parameter2;
        d0 := @parameter3;
        load.i i0;
        load.d d0;
        load.f f0;
        load.f f1;
        load.d d0;
        staticinvoke printStack;
        return;
    }

    public static void test_dup2_x2_cat2cat1(int, long, float, float)
    {
        word i0, f0, f1;
        dword l1;

        i0 := @parameter0;
        l1 := @parameter1;
        f0 := @parameter2;
        f1 := @parameter3;
        load.i i0;
        load.f f0;
        load.f f1;
        load.l l1;
        load.f f0;
        load.f f1;
        staticinvoke printStack;
        return;
    }

    public static void test_dup2_x2_cat2cat2(int, long, double)
    {
        word i0;
        dword l1, d0;

        i0 := @parameter0;
        l1 := @parameter1;
        d0 := @parameter2;
        load.i i0;
        load.d d0;
        load.l l1;
        load.d d0;
        staticinvoke printStack;
        return;
    }

    public static void printStack(int, float, float, int)
    {
        word i0, f0, f1, i1, r0;

        i0 := @parameter0;
        f0 := @parameter1;
        f1 := @parameter2;
        i1 := @parameter3;
        staticget java.lang.System.out;
        store.r r0;
        load.r r0;
        load.i i0;
        virtualinvoke println;
        load.r r0;
        load.f f0;
        virtualinvoke println;
        load.r r0;
        load.f f1;
        virtualinvoke println;
        load.r r0;
        load.i i1;
        virtualinvoke println;
        load.r r0;
        virtualinvoke println;
        return;
    }

    public static void printStack(int, long, int)
    {
        word i0, i2, r0;
        dword l1;

        i0 := @parameter0;
        l1 := @parameter1;
        i2 := @parameter2;
        staticget java.lang.System.out;
        store.r r0;
        load.r r0;
        load.i i0;
        virtualinvoke println;
        load.r r0;
        load.l l1;
        virtualinvoke println;
        load.r r0;
        load.i i2;
        virtualinvoke println;
        load.r r0;
        virtualinvoke println;
        return;
    }

    public static void printStack(int)
    {
        word i0;

        i0 := @parameter0;
        staticget java.lang.System.out;
        dup1.r;
        load.i i0;
        virtualinvoke println;
        virtualinvoke println;
        return;
    }

    public static void printStack(int, float, float, float, float)
    {
        word i0, f0, f1, f2, f3, r0;

        i0 := @parameter0;
        f0 := @parameter1;
        f1 := @parameter2;
        f2 := @parameter3;
        f3 := @parameter4;
        staticget java.lang.System.out;
        store.r r0;
        load.r r0;
        load.i i0;
        virtualinvoke println;
        load.r r0;
        load.f f0;
        virtualinvoke println;
        load.r r0;
        load.f f1;
        virtualinvoke println;
        load.r r0;
        load.f f2;
        virtualinvoke println;
        load.r r0;
        load.f f3;
        virtualinvoke println;
        load.r r0;
        virtualinvoke println;
        return;
    }

    public static void printStack(int, double, double)
    {
        word i0, r0;
        dword d0, d1;

        i0 := @parameter0;
        d0 := @parameter1;
        d1 := @parameter2;
        staticget java.lang.System.out;
        store.r r0;
        load.r r0;
        load.i i0;
        virtualinvoke println;
        load.r r0;
        load.d d0;
        virtualinvoke println;
        load.r r0;
        load.d d1;
        virtualinvoke println;
        load.r r0;
        virtualinvoke println;
        return;
    }

    public static void printStack(float, float, int, float, float)
    {
        word f0, f1, i0, f2, f3, r0;

        f0 := @parameter0;
        f1 := @parameter1;
        i0 := @parameter2;
        f2 := @parameter3;
        f3 := @parameter4;
        staticget java.lang.System.out;
        store.r r0;
        load.r r0;
        load.f f0;
        virtualinvoke println;
        load.r r0;
        load.f f1;
        virtualinvoke println;
        load.r r0;
        load.i i0;
        virtualinvoke println;
        load.r r0;
        load.f f2;
        virtualinvoke println;
        load.r r0;
        load.f f3;
        virtualinvoke println;
        load.r r0;
        virtualinvoke println;
        return;
    }

    public static void printStack(double, int, double)
    {
        dword d0, d1;
        word i0, r0;

        d0 := @parameter0;
        i0 := @parameter1;
        d1 := @parameter2;
        staticget java.lang.System.out;
        store.r r0;
        load.r r0;
        load.d d0;
        virtualinvoke println;
        load.r r0;
        load.i i0;
        virtualinvoke println;
        load.r r0;
        load.d d1;
        virtualinvoke println;
        load.r r0;
        virtualinvoke println;
        return;
    }

    public static void printStack(int, int, int, float, float, int, int)
    {
        word i0, i1, i2, f0, f1, i3, i4, r0;

        i0 := @parameter0;
        i1 := @parameter1;
        i2 := @parameter2;
        f0 := @parameter3;
        f1 := @parameter4;
        i3 := @parameter5;
        i4 := @parameter6;
        staticget java.lang.System.out;
        store.r r0;
        load.r r0;
        load.i i0;
        virtualinvoke println;
        load.r r0;
        load.i i1;
        virtualinvoke println;
        load.r r0;
        load.i i2;
        virtualinvoke println;
        load.r r0;
        load.f f0;
        virtualinvoke println;
        load.r r0;
        load.f f1;
        virtualinvoke println;
        load.r r0;
        load.i i3;
        virtualinvoke println;
        load.r r0;
        load.i i4;
        virtualinvoke println;
        load.r r0;
        virtualinvoke println;
        return;
    }

    public static void printStack(int, double, float, float, double)
    {
        word i0, f0, f1, r0;
        dword d0, d1;

        i0 := @parameter0;
        d0 := @parameter1;
        f0 := @parameter2;
        f1 := @parameter3;
        d1 := @parameter4;
        staticget java.lang.System.out;
        store.r r0;
        load.r r0;
        load.i i0;
        virtualinvoke println;
        load.r r0;
        load.d d0;
        virtualinvoke println;
        load.r r0;
        load.f f0;
        virtualinvoke println;
        load.r r0;
        load.f f1;
        virtualinvoke println;
        load.r r0;
        load.d d1;
        virtualinvoke println;
        load.r r0;
        virtualinvoke println;
        return;
    }

    public static void printStack(int, float, float, long, float, float)
    {
        word i0, f0, f1, f2, f3, r0;
        dword l1;

        i0 := @parameter0;
        f0 := @parameter1;
        f1 := @parameter2;
        l1 := @parameter3;
        f2 := @parameter4;
        f3 := @parameter5;
        staticget java.lang.System.out;
        store.r r0;
        load.r r0;
        load.i i0;
        virtualinvoke println;
        load.r r0;
        load.f f0;
        virtualinvoke println;
        load.r r0;
        load.f f1;
        virtualinvoke println;
        load.r r0;
        load.l l1;
        virtualinvoke println;
        load.r r0;
        load.f f2;
        virtualinvoke println;
        load.r r0;
        load.f f3;
        virtualinvoke println;
        load.r r0;
        virtualinvoke println;
        return;
    }

    public static void printStack(int, double, long, double)
    {
        word i0, r0;
        dword d0, l1, d1;

        i0 := @parameter0;
        d0 := @parameter1;
        l1 := @parameter2;
        d1 := @parameter3;
        staticget java.lang.System.out;
        store.r r0;
        load.r r0;
        load.i i0;
        virtualinvoke println;
        load.r r0;
        load.d d0;
        virtualinvoke println;
        load.r r0;
        load.l l1;
        virtualinvoke println;
        load.r r0;
        load.d d1;
        virtualinvoke println;
        load.r r0;
        virtualinvoke println;
        return;
    }

    public static void main(java.lang.String[])
    {
        word r0;

        r0 := @parameter0;
        push 1.1111F;
        push 2.2222F;
        push 3;
        staticinvoke test_dup_x2_cat1;
        push 11111L;
        push 2;
        staticinvoke test_dup_x2_cat2;
        push 1;
        push 2.2222F;
        push 3.3333F;
        staticinvoke test_pop2_cat1;
        push 1;
        push 22222L;
        staticinvoke test_pop2_cat2;
        push 1;
        push 2.2222F;
        push 3.3333F;
        staticinvoke test_dup2_cat1;
        push 1;
        push 2.2222;
        staticinvoke test_dup2_cat2;
        push 1;
        push 2.2222F;
        push 3.3333F;
        staticinvoke test_dup2_x1_cat1;
        push 1;
        push 2.2222;
        staticinvoke test_dup2_x1_cat2;
        push 1;
        push 2.222F;
        push 3.333F;
        push 4;
        push 5;
        staticinvoke test_dup2_x2_cat1cat1;
        push 1;
        push 2.222F;
        push 3.333F;
        push 4444.44444;
        staticinvoke test_dup2_x2_cat1cat2;
        push 1;
        push 2222222L;
        push 3.3333F;
        push 4.4444F;
        staticinvoke test_dup2_x2_cat2cat1;
        push 1;
        push 2222222L;
        push 3333.3333;
        staticinvoke test_dup2_x2_cat2cat2;
        return;
    }
}
