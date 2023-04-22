package Aspects;

public aspect Calculations {
    private long startTime;

    pointcut parseCall() : call(void parse());
    before() : parseCall() {
        startTime = System.nanoTime();
    }
    after() : parseCall() {
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println("parse() execution time: " + elapsedTime + " ns");
    }
}
