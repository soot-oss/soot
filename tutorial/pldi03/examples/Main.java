class Main {
  public static void main(String[] args) {
    soot.G.v().PackManager().getPack("tag").add(new soot.Transform("tag.null", new NullTagAggregator()));
	soot.Main.main(args);
  }
}
