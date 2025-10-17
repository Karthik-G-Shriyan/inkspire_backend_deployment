package com.unicksbyte.inkspire.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponse {

    private String publicId;

    private String userName;

    private String email;
}
