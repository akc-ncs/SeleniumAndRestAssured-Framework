package com.poc.framework.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Post(Integer id, Integer userId, String title, String body) {}
