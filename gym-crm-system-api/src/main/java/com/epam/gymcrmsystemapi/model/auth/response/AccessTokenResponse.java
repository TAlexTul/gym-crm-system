package com.epam.gymcrmsystemapi.model.auth.response;

public record AccessTokenResponse(String accessToken,
                                  String refreshToken,
                                  long expireIn) {

}
