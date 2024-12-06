package com.epam.trainerworkloadapi;

public final class Routes {

    private Routes() {
        throw new AssertionError("non-instantiable class");
    }

    public static final String API_ROOT = "/api/v1";
    public final static String WORKLOAD = API_ROOT + "/workload";
    public static final String TRAINING = API_ROOT + "/training";

}
