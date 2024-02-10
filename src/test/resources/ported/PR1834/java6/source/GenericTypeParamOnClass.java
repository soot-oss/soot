
/** @author Hasitha Rajapakse **/


public class GenericTypeParamOnClass{

    public class A<T> {
        private T t;

        private void set(T t) {
            this.t = t;
        }

        public T get() {
            return t;
        }
    }

    public void genericTypeParamOnClass() {
        A<Integer> a = new A<Integer>();
        a.set(5);
        int x = a.get();
    }
}