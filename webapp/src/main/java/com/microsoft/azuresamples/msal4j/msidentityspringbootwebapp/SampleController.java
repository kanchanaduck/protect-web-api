// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class SampleController {
    @Autowired
    HttpServletRequest req;
    
    @Value( "${app.api.base-address}" )
    private String apiAddress;

    @Value( "${app.api.date-endpoint}" )
    private String apiDateEndpoint;

    @Value( "${app.api.roles-endpoint}" )
    private String apiRolesEndpoint;

    @Value( "${app.api.courses-endpoint}" )
    private String apiCoursesEndpoint;

    /**
     * Add HTML partial fragment from /templates/content folder to request and serve base html
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @param fragment used to determine which partial to put into UI
     */
    private String hydrateUI(Model model, String fragment) {
        model.addAttribute("bodyContent", String.format("content/%s.html", fragment));
        return "base"; //base.html in /templates folder
    }
    /**
     *  Sign in status endpoint
     *  The page demonstrates sign-in status. For full details, see the src/main/webapp/content/status.html file.
     * 
     * @param model Model used for placing bodyContent param in request before serving UI.
     * @return String the UI.
     */
    @GetMapping(value = {"/", "sign_in_status", "/index"})
    public String status(Model model, Principal principal) {
        return hydrateUI(model, "status");
    }

    /**
     *  Token details endpoint
     *  Demonstrates how to extract and make use of token details
     *  For full details, see method: Utilities.filterclaims(OidcUser principal)
     * 
     * @param model Model used for placing claims param and bodyContent param in request before serving UI.
     * @param principal OidcUser this object contains all ID token claims about the user. See utilities file.
     * @return String the UI.
     */
    @GetMapping(path = "/token_details")
    public String tokenDetails(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("claims", Utilities.filterClaims(principal));
        return hydrateUI(model, "token");
    }

    /**
     *  Call API  endpoint
     *  Demonstrates how to call a protected API
     * 
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @param apiAuthorizedClient OAuth2AuthorizedClient this object contains API Access Token.
     * @return String the UI.
     */
    @GetMapping(path = "/call_api")
    public String callAPI(Model model, @RegisteredOAuth2AuthorizedClient("web-api") OAuth2AuthorizedClient apiAuthorizedClient) {

        final WebClient apiClient = WebClient.builder()
            .baseUrl(apiAddress)
            .defaultHeader("Authorization", String.format("Bearer %s", apiAuthorizedClient.getAccessToken().getTokenValue()))
            .build();

        System.out.println(apiAuthorizedClient.getAccessToken().getTokenValue());

        Map<String,String> apiResp;
        try {
            String response = apiClient.get().uri(apiDateEndpoint).retrieve().toEntity(String.class).block().getBody();
            apiResp =  new ObjectMapper().readValue(response, HashMap.class);
            System.out.println(apiResp);
        } catch (Exception ex) {
            apiResp = new HashMap<>();
            System.out.println(ex);
            apiResp.put("Error", "Response was null or other error");
        }

        model.addAttribute("apiResp", apiResp);
        return hydrateUI(model, "api");
    }

    @GetMapping(value = {"/admin-page", "/profession-page"})
    public String callCourseAPI(Model model, @RegisteredOAuth2AuthorizedClient("web-api") OAuth2AuthorizedClient apiAuthorizedClient) {

        final WebClient apiClient = WebClient.builder()
            .baseUrl(apiAddress)
            .defaultHeader("Authorization", String.format("Bearer %s", apiAuthorizedClient.getAccessToken().getTokenValue()))
            .build();

        System.out.println(apiAuthorizedClient.getAccessToken().getTokenValue());

        ArrayList apiRoles;
        try {
            String response = apiClient.get().uri(apiRolesEndpoint).retrieve().toEntity(String.class).block().getBody();
            // apiRoles =  new ObjectMapper().readValue(response, HashMap.class);
            apiRoles =  new ObjectMapper().readValue(response,  ArrayList.class);
            // System.out.println(apiRoles);
        } catch (Exception ex) {
            System.out.println(ex);
            apiRoles = new ArrayList<>();
            // apiCourses.add({"Type": "Error","Message": ex});
        }

        ArrayList apiCourses;
        try {
            String response = apiClient.get().uri(apiCoursesEndpoint).retrieve().toEntity(String.class).block().getBody();
            // apiCourses =  new ObjectMapper().readValue(response, HashMap.class);
            apiCourses =  new ObjectMapper().readValue(response,  ArrayList.class);
            // System.out.println(apiCourses);
        } catch (Exception ex) {
            System.out.println(ex);
            apiCourses = new ArrayList<>();
            // apiCourses.add({"Type": "Error","Message": ex});
        }

        String roles = String.join(", ", apiRoles);
        Boolean readwrite = false;
        String route = req.getRequestURI();
        String fragment = "content";

        System.out.println(route);

        if(roles.contains("Developer.ReadWrite.All") || roles.contains("SRE.ReadWrite.All") || roles.contains("Profession.Admin")){
            readwrite = true;
        }

        if(route.equals("/admin-page")){
            if(!roles.contains("Profession.Admin")){
                fragment = "401";
            }
            else{
                fragment = "admin";
            }
        }
        else{
            if(roles.isEmpty()){
                fragment = "401";
            }
        }

        model.addAttribute("apiCourses", apiCourses);
        model.addAttribute("apiRoles", apiRoles);
        model.addAttribute("roles", roles);
        model.addAttribute("readwrite", readwrite);
        return hydrateUI(model, fragment);
    }

    // survey endpoint - did the sample address your needs?
    // not an integral a part of this tutorial.
    @GetMapping(path = "/survey")
    public String survey(Model model) {
        return hydrateUI(model, "survey");
    }
}
