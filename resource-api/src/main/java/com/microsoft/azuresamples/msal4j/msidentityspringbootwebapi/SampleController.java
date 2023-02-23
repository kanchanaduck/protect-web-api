// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SampleController {
    
    @GetMapping("/api/date")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_access_as_user')")
    public String date(/**BearerTokenAuthentication bearerTokenAuth*/) {
        /** 
        //uncomment the parameter in the function params above and the line below to get access to the principal.
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        // You can then access attributes of the principal, e.g., attributes (claims), the raw tokenValue, and authorities.
        // For example:
        principal.getAttribute("scp");
        */
        return new DateResponse().toString();
    }

    @GetMapping("api/roles")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_access_as_user')")
	public List<String> getRoles()   
    {  
        return Arrays.asList("Developer.Read.All","SRE.ReadWrite.All");
        // return Arrays.asList("Profession.Admin");
        // return Arrays.asList();
    }  

    @GetMapping("api/courses")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_access_as_user')")
	public List<Course> getAllDeveloperCourses()   
    {  
        List<String> roles = getRoles();
        Boolean readwrite_dev=true;
        Boolean readwrite_sre=true;


        if(roles.contains("Developer.Read.All")){
            readwrite_dev=false;
        }

        if(roles.contains("SRE.Read.All")){
            readwrite_sre=false;
        }

        List<Course> courses = new ArrayList<Course>();
        if(roles.contains("Developer.Read.All") || roles.contains("Developer.ReadWrite.All") || roles.contains("Profession.Admin") ){
            courses.add(new Course("Programming", "NET, Java, Python, Javascipt",readwrite_dev));
            courses.add(new Course("Dev-method", "Agile, Waterfall",readwrite_dev));
            courses.add(new Course("Test-tool", "JUnit5, Mocha, PyTest",readwrite_dev));
        }
        if(roles.contains("SRE.Read.All") || roles.contains("SRE.ReadWrite.All") || roles.contains("Profession.Admin") ){
            courses.add(new Course("Infrastructure-Code", "Terrafrom, Ansible, Cheft, Puppet",readwrite_sre));
            courses.add(new Course("Cloud-Compute", "AWS, Azure, GCP",readwrite_sre));
            courses.add(new Course("Unix-Admin", "Bash, Perl",readwrite_sre));
        }
        return courses;
    }  
    private class DateResponse {
        private String humanReadable;
        private String timeStamp;

        public DateResponse() {
            Date now = new Date();
            this.humanReadable = now.toString();
            this.timeStamp = Long.toString(now.getTime());
        }

        public String toString() {
            return String.format("{\"humanReadable\": \"%s\", \"timeStamp\": \"%s\"}", humanReadable, timeStamp);
        }
    }
}
