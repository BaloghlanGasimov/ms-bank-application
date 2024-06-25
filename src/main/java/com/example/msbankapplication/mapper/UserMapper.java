package com.example.msbankapplication.mapper;

import com.example.msbankapplication.dao.entity.UserEntity;
import com.example.msbankapplication.model.UserRequestDto;
import com.example.msbankapplication.model.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity mapToEntity(UserResponseDto userResponseDto);
    UserEntity mapToEntity(UserRequestDto userRequestDto);
    UserRequestDto mapToReqDto(UserEntity userEntity);
    @Mapping(target = "age", source = "userEntity",qualifiedByName = "birthToAge")
    UserResponseDto mapToRespDto(UserEntity userEntity);

    @Named("birthToAge")
    default Integer birthToAge(UserEntity userEntity){
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate=userEntity.getBirthDate();
        Integer age = Period.between(birthDate,currentDate).getYears();
        return age;
    }

}
