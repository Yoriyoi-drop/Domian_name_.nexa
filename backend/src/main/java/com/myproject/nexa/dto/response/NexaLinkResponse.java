package com.myproject.nexa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NexaLinkResponse {
    private String originalUrl;
    private String shortUrl;
    private Long clicks;
    private Date createdAt;
    private Date expiresAt;
}