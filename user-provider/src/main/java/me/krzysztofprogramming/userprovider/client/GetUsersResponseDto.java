package me.krzysztofprogramming.userprovider.client;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class GetUsersResponseDto {
    private GetUsersResponseListDto _embedded;
    private PageResponseDto page;
}
