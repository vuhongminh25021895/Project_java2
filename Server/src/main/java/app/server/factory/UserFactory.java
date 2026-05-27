package app.server.factory;

import app.server.enums.UserRole;
import app.server.model.Bidder;
import app.server.model.Seller;
import app.server.model.User;

public class UserFactory {

    public static User createUser(
            UserRole role,
            String username,
            String password,
            String fullName
    ) {

        return switch (role) {

            case SELLER -> new Seller(
                    username,
                    password,
                    fullName
            );

            case BIDDER -> new Bidder(
                    username,
                    password,
                    fullName
            );

            case ADMIN -> throw new RuntimeException(
                    "Admin creation not supported yet"
            );
        };
    }
}