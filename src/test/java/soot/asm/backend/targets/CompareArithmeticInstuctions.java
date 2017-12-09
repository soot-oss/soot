package soot.asm.backend.targets;

/**
 * @author Tobias Hamann
 */
public class CompareArithmeticInstuctions {

    int i = 2;
    float f = 221349.01213123213213213123213213124764573f;
    double d = 2123996.12312312312312312323421612334;
    long l = 2;
    byte b = 2;
    char c = 4;
    short s = 3;

    void comparei(int i1) {
        if (i <= i1) {
            i = 2;
        }
        if (i > i1) {
            i = 1;
        }
    }

    void comparef(float f1) {
        if (f <= f1) {
            f = 2.0f;
        }
        if (f > f1) {
            f = 1.0f;
        }
    }

    void compared(double d1) {
        if (d <= d1) {
            d = 2.0;
        }
        if (d > d1) {
            d = 1.0;
        }
    }

    void comparel(long l1) {
        if (l <= l1) {
            l = 2;
        }
        if (l > l1) {
            l = 1;
        }
    }

    void compareb(byte b1) {
        if (b <= b1) {
            b = 2;
        }
        if (b > b1) {
            b = 1;
        }
    }

    void comparec(char c1) {
        if (c <= c1) {
            c = 2;
        }
        if (c > c1) {
            c = 3;
        }
    }

    void compares(short s1) {
        if (s <= s1) {
            s = 1;
        }
        if (s > s1) {
            s = 3;
        }
    }

}
