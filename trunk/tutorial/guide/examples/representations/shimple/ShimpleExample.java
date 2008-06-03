import java.util.*;

public class ShimpleExample {

  public boolean as_long_as_it_takes = true;

  public int test() {
    int x = 100;
    while(as_long_as_it_takes) {
        if(x < 200) {
            x = 100;
	}
        else {
            x = 200;
	}
    }

    return x;
  }

}

