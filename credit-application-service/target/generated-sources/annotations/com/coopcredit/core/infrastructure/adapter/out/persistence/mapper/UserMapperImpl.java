package com.coopcredit.core.infrastructure.adapter.out.persistence.mapper;

import com.coopcredit.core.domain.model.User;
import com.coopcredit.core.infrastructure.adapter.out.persistence.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T21:14:16-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toEntity(User user) {
        if ( user == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.active( user.isActive() );
        userEntity.id( user.getId() );
        userEntity.password( user.getPassword() );
        userEntity.role( user.getRole() );
        userEntity.username( user.getUsername() );

        return userEntity.build();
    }

    @Override
    public User toDomain(UserEntity entity) {
        if ( entity == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.active( entity.isActive() );
        user.id( entity.getId() );
        user.password( entity.getPassword() );
        user.role( entity.getRole() );
        user.username( entity.getUsername() );

        return user.build();
    }
}
