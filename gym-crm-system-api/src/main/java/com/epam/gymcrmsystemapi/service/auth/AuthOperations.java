package com.epam.gymcrmsystemapi.service.auth;

import com.epam.gymcrmsystemapi.exceptions.auth.InvalidRefreshTokenException;
import com.epam.gymcrmsystemapi.model.auth.CustomUserDetails;
import com.epam.gymcrmsystemapi.model.auth.response.AccessTokenResponse;

public interface AuthOperations {

    AccessTokenResponse getToken(CustomUserDetails userDetails);

    AccessTokenResponse refreshToken(String refreshToken) throws InvalidRefreshTokenException;

    void invalidateToken(String refreshToken, String ownerUsername) throws InvalidRefreshTokenException;

}
