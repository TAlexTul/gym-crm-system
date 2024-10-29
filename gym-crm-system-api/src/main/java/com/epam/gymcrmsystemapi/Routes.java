package com.epam.gymcrmsystemapi;

public final class Routes {

    private Routes() {
        throw new AssertionError("non-instantiable class");
    }

    public static final String API_ROOT = "/api/v1";
    public final static String TRAINEES = API_ROOT + "/trainees";
    public final static String TRAINERS = API_ROOT + "/trainers";
    public final static String TRAININGS = API_ROOT + "/trainings";
    public final static String TRAINING_TYPES = API_ROOT + "/training-types";
    public static final String TOKEN = API_ROOT + "/token";
    public static final String LOGIN = API_ROOT + "/login-data";
    public static final String HEALTH_API = API_ROOT + "/health-api";

}
