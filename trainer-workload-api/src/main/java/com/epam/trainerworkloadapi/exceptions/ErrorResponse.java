package com.epam.trainerworkloadapi.exceptions;

import java.util.Date;

public record ErrorResponse(Date timestamp,
                            int status,
                            String error,
                            String path) {
}
