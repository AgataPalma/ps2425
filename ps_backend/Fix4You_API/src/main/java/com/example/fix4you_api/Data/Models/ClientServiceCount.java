package com.example.fix4you_api.Data.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientServiceCount {
    private String clientId;
    private String clientName;
    private Long serviceCount;
}
